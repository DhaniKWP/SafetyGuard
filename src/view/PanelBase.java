package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public abstract class PanelBase extends JPanel {

    protected static final Color BG_MAIN      = new Color(15, 23, 42);
    protected static final Color BG_CARD      = new Color(22, 27, 45);
    protected static final Color BG_FILTER    = new Color(17, 24, 39);
    protected static final Color BG_HEADER    = new Color(13, 18, 30);
    protected static final Color BORDER_COLOR = new Color(51, 65, 85);
    protected static final Color TEXT_WHITE   = new Color(248, 250, 252);
    protected static final Color TEXT_MUTED   = new Color(148, 163, 184);
    protected static final Color TBL_EVEN     = new Color(22, 27, 45);
    protected static final Color TBL_ODD      = new Color(17, 24, 39);
    protected static final Color TBL_HDR      = new Color(30, 41, 59);
    protected static final Color ACCENT_BLUE  = new Color(59, 130, 246);
    protected static final Color ACCENT_CYAN  = new Color(34, 211, 238);
    protected static final Color ACCENT_GREEN = new Color(16, 185, 129);
    protected static final Color ACCENT_RED   = new Color(239, 68, 68);

    protected JTable  mainTable;
    protected JPanel  filterBar;
    protected JPanel  actionBar;

    public PanelBase(Color accentColor, String title, String subtitle) {
        setLayout(new BorderLayout());
        setBackground(BG_MAIN);
        add(buildHeader(accentColor, title, subtitle), BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        JPanel topCenter = new JPanel();
        topCenter.setLayout(new BoxLayout(topCenter, BoxLayout.X_AXIS));
        topCenter.setOpaque(false);
        filterBar = buildFilterBar();
        actionBar = buildActionBar();
        topCenter.add(filterBar);
        topCenter.add(Box.createHorizontalGlue()); // Ini fitur auto-layout untuk menahan tombol di kanan
        topCenter.add(actionBar);

        // Background kustom untuk bar filter & aksi gabungan
        JPanel topContainer = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(BG_FILTER);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(BORDER_COLOR);
                g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
                g2.dispose();
            }
        };
        topContainer.setOpaque(false);
        topContainer.setBorder(new EmptyBorder(4, 8, 4, 8));
        topContainer.add(topCenter, BorderLayout.CENTER);

        centerPanel.add(topContainer, BorderLayout.NORTH);
        
        mainTable = buildTable();
        JScrollPane scroll = new JScrollPane(mainTable);
        scroll.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        scroll.getViewport().setBackground(BG_MAIN);
        centerPanel.add(scroll, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
    }

    // ── Header ──────────────────────────────────────────────────────────────
    private JPanel buildHeader(Color accent, String title, String subtitle) {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, BG_HEADER, getWidth(), 0, BG_CARD);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Garis accent di sisi kiri
                g2.setColor(accent);
                g2.fillRect(0, 0, 4, getHeight());
                // Border bawah
                g2.setColor(BORDER_COLOR);
                g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(14, 20, 14, 20));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(TEXT_WHITE);

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(TEXT_MUTED);

        JPanel textStack = new JPanel();
        textStack.setOpaque(false);
        textStack.setLayout(new BoxLayout(textStack, BoxLayout.Y_AXIS));
        textStack.add(lblTitle);
        textStack.add(Box.createVerticalStrut(2));
        textStack.add(lblSub);
        header.add(textStack, BorderLayout.CENTER);
        return header;
    }

    // ── Filter Bar ──────────────────────────────────────────────────────────
    protected JPanel buildFilterBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        bar.setOpaque(false);
        return bar;
    }

    // ── Tabel ────────────────────────────────────────────────────────────────
    private JTable buildTable() {
        JTable table = new JTable() {
            @Override public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? TBL_EVEN : TBL_ODD);
                } else {
                    c.setBackground(new Color(37, 99, 235, 160));
                }
                c.setForeground(TEXT_WHITE);
                return c;
            }
        };
        table.setBackground(BG_MAIN);
        table.setForeground(TEXT_WHITE);
        table.setGridColor(BORDER_COLOR);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(34);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionBackground(new Color(37, 99, 235, 160));
        table.setSelectionForeground(Color.WHITE);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setBackground(TBL_HDR);
        header.setForeground(TEXT_MUTED);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT_BLUE));
        header.setPreferredSize(new Dimension(0, 40));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);
        return table;
    }

    // ── Action Bar ───────────────────────────────────────────────────────────
    protected JPanel buildActionBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        bar.setOpaque(false);
        return bar;
    }

    // ── Helper untuk subkelas ─────────────────────────────────────────────────

    protected JTextField styledSearch(String placeholder) {
        JTextField f = new JTextField(18);
        f.putClientProperty("JTextField.placeholderText", placeholder);
        f.setBackground(new Color(30, 41, 59));
        f.setForeground(TEXT_WHITE);
        f.setCaretColor(ACCENT_BLUE);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setPreferredSize(new Dimension(f.getPreferredSize().width, 36));
        return f;
    }

    protected JComboBox<String> styledCombo(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setBackground(new Color(30, 41, 59));
        cb.setForeground(TEXT_WHITE);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cb.setPreferredSize(new Dimension(cb.getPreferredSize().width, 36));
        return cb;
    }

    protected JLabel filterLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l.setForeground(TEXT_MUTED);
        return l;
    }

    /** Tombol aksi bergradien tanpa ikon */
    protected JButton actionButton(String text, Color from, Color to) {
        JButton btn = new JButton(text) {
            private boolean hovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
                    public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (!isEnabled()) {
                    g2.setColor(new Color(51, 65, 85)); // Warna abu-abu untuk tombol non-aktif
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                } else {
                    Color c1 = hovered ? to   : from;
                    Color c2 = hovered ? from : to;
                    g2.setPaint(new GradientPaint(0, 0, c1, getWidth(), 0, c2));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 20, 36));
        return btn;
    }

    protected JPanel createGridForm(String[] labels, JComponent[] inputs) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 6, 8, 12); // Padding antar elemen
        gbc.anchor = GridBagConstraints.WEST;
        
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 0;
            gbc.fill = GridBagConstraints.NONE;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lbl.setForeground(TEXT_MUTED);
            panel.add(lbl, gbc);

            gbc.gridx = 1;
            gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            if (inputs[i] instanceof JScrollPane) {
                // Biar text area bisa meregang tingginya
                gbc.weighty = 1.0;
                gbc.fill = GridBagConstraints.BOTH;
            } else {
                gbc.weighty = 0;
            }
            // Ubah font standar inputan
            inputs[i].setFont(new Font("Segoe UI", Font.PLAIN, 14));
            panel.add(inputs[i], gbc);
        }
        return panel;
    }
}
