package view;

import com.formdev.flatlaf.FlatLightLaf;
import model.Pengguna;

import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {
    private Pengguna loggedInUser;
    private JTabbedPane tabbedPane;

    public DashboardFrame(Pengguna user) {
        this.loggedInUser = user;
        setTitle("SafetyGuard HSE Dashboard - Login sebagai: " + user.getUsername() + " (" + user.getPeran() + ")");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        getContentPane().setBackground(new Color(255, 255, 255));
        
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(new Color(241, 245, 249));
        tabbedPane.setOpaque(true);

        tabbedPane.addTab("Laporan Insiden", new PanelInsiden(loggedInUser));
        tabbedPane.addTab("Tindakan CAPA", new PanelCapa(loggedInUser));
        
        if (!loggedInUser.getPeran().equalsIgnoreCase("Karyawan")) {
            tabbedPane.addTab("Inspeksi Keselamatan", new PanelInspeksi(loggedInUser));
        }
        
        tabbedPane.addTab("Distribusi APD", new PanelApd(loggedInUser));
        tabbedPane.addTab("Laporan & Rekap", new PanelLaporan(loggedInUser));

        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogout.setForeground(new Color(239, 68, 68));
        btnLogout.setContentAreaFilled(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin keluar?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose();
                new LoginForm().setVisible(true);
            }
        });
        
        tabbedPane.putClientProperty("JTabbedPane.trailingComponent", btnLogout);

        add(tabbedPane, BorderLayout.CENTER);

        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        statusPanel.setBackground(new Color(241, 245, 249));
        
        JLabel lblStatus = new JLabel("Pengguna Aktif: " + loggedInUser.getUsername());
        lblStatus.setForeground(new Color(100, 116, 139));
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusPanel.add(lblStatus, BorderLayout.WEST);

        add(statusPanel, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        try {
            FlatLightLaf.setup();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        Pengguna mockUser = new Pengguna("hse_test", "password", "mock-uuid", "Staff HSE");
        SwingUtilities.invokeLater(() -> new DashboardFrame(mockUser).setVisible(true));
    }
}
