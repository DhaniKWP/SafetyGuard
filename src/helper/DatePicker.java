package helper;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class DatePicker {
    int month = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH);
    int year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
    JLabel l = new JLabel("", JLabel.CENTER);
    String dayStr = "";
    JDialog d;
    JButton[] button = new JButton[49];

    public DatePicker(JFrame parent) {
        d = new JDialog(parent, "Pilih Tanggal", true);
        d.setModal(true);
        String[] header = { "Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab" };
        JPanel p1 = new JPanel(new GridLayout(7, 7));
        p1.setPreferredSize(new Dimension(430, 200));

        for (int i = 0; i < 7; i++) {
            JButton btn = new JButton(header[i]);
            btn.setForeground(Color.red);
            btn.setBackground(Color.lightGray);
            btn.setFocusPainted(false);
            p1.add(btn);
        }

        for (int i = 0; i < 42; i++) {
            final int selection = i;
            button[i] = new JButton();
            button[i].setFocusPainted(false);
            button[i].setBackground(Color.white);
            button[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    dayStr = button[selection].getActionCommand();
                    d.dispose();
                }
            });
            p1.add(button[i]);
        }
        
        JPanel p2 = new JPanel(new GridLayout(1, 3));
        JButton previous = new JButton("<< Prev");
        previous.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                month--;
                if(month < 0) { month = 11; year--; }
                displayDate();
            }
        });
        p2.add(previous);
        p2.add(l);
        JButton next = new JButton("Next >>");
        next.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                month++;
                if(month > 11) { month = 0; year++; }
                displayDate();
            }
        });
        p2.add(next);
        
        d.add(p1, BorderLayout.CENTER);
        d.add(p2, BorderLayout.SOUTH);
        d.pack();
        d.setLocationRelativeTo(parent);
        displayDate();
        d.setVisible(true);
    }

    public void displayDate() {
        for (int x = 0; x < 42; x++) {
            button[x].setText("");
            button[x].setEnabled(false);
        }
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMMM yyyy", new java.util.Locale("id", "ID"));
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(year, month, 1);
        int dayOfWeek = cal.get(java.util.Calendar.DAY_OF_WEEK);
        int daysInMonth = cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
        
        for (int i = 6 + dayOfWeek, day = 1; day <= daysInMonth; i++, day++) {
            button[i - 7].setText("" + day);
            button[i - 7].setEnabled(true);
        }
        l.setText(sdf.format(cal.getTime()));
    }

    public String setPickedDate() {
        if (dayStr.equals("")) return dayStr;
        
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(year, month, Integer.parseInt(dayStr));
        return sdf.format(cal.getTime());
    }
}
