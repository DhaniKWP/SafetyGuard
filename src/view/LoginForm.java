package view;

import com.formdev.flatlaf.FlatLightLaf;
import dao.PenggunaDAO;
import model.Pengguna;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginForm extends JFrame {

    private static final Color BG_DARK      = new Color(241, 245, 249);
    private static final Color BG_CARD      = new Color(255, 255, 255);
    private static final Color ACCENT_BLUE  = new Color(37, 99, 235);
    private static final Color ACCENT_CYAN  = new Color(59, 130, 246);
    private static final Color BORDER_COLOR = new Color(203, 213, 225);
    private static final Color TEXT_WHITE   = new Color(15, 23, 42);
    private static final Color TEXT_MUTED   = new Color(100, 116, 139);
    private static final Color INPUT_BG     = new Color(248, 250, 252);
    private static final Color PROGRESS_BG  = new Color(226, 232, 240);

    private JTextField     txtUsername;
    private JPasswordField txtPassword;
    private JButton        btnLogin;
    private JLabel         lblError;
    private JLabel         lblStatus;
    private JProgressBar   progressBar;
    private JPanel         progressSection;

    public LoginForm() {
        setTitle("SafetyGuard HSE - Login");
        setSize(900, 580);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridLayout(1, 2)) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0, BG_DARK, getWidth(), getHeight(), new Color(226, 232, 240));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.add(createBrandingPanel());
        mainPanel.add(createFormPanel());
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createBrandingPanel() {
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(30, 58, 138), getWidth(), getHeight(), new Color(37, 99, 235));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(new Color(255, 255, 255, 12));
                g2d.fillOval(-60, -60, 280, 280);
                g2d.setColor(new Color(255, 255, 255, 8));
                g2d.fillOval(120, 280, 320, 320);
                g2d.setColor(new Color(34, 211, 238, 15));
                g2d.fillOval(160, 60, 200, 200);
                g2d.dispose();
            }
        };
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(8, 30, 8, 30);
        gbc.anchor = GridBagConstraints.CENTER;



        JLabel lblAppName = new JLabel("SafetyGuard");
        lblAppName.setFont(new Font("Segoe UI", Font.BOLD, 38));
        lblAppName.setForeground(Color.WHITE);
        lblAppName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTagline = new JLabel("<html><center>Health, Safety &amp; Environment<br>Management System</center></html>");
        lblTagline.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblTagline.setForeground(new Color(147, 197, 253));
        lblTagline.setHorizontalAlignment(SwingConstants.CENTER);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 40));
        sep.setPreferredSize(new Dimension(220, 1));

        JPanel featuresPanel = new JPanel();
        featuresPanel.setOpaque(false);
        featuresPanel.setLayout(new BoxLayout(featuresPanel, BoxLayout.Y_AXIS));

        String[] features = {
            "Manajemen Insiden K3",
            "Tindakan CAPA Terintegrasi",
            "Inspeksi Keselamatan Area",
            "Distribusi APD Digital"
        };
        for (String f : features) {
            JLabel lbl = new JLabel("  - " + f);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            lbl.setForeground(new Color(224, 242, 254));
            lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            featuresPanel.add(lbl);
            featuresPanel.add(Box.createVerticalStrut(6));
        }

        gbc.gridy = 1; gbc.insets = new Insets(12, 30, 4, 30); panel.add(lblAppName, gbc);
        gbc.gridy = 2; gbc.insets = new Insets(0, 30, 8, 30); panel.add(lblTagline, gbc);
        gbc.gridy = 3; gbc.insets = new Insets(15, 30, 15, 30); panel.add(sep, gbc);
        gbc.gridy = 4; gbc.insets = new Insets(0, 30, 8, 30); panel.add(featuresPanel, gbc);

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        JPanel card = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(BG_CARD);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2d.setColor(BORDER_COLOR);
                g2d.setStroke(new BasicStroke(1f));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2d.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(380, 460));
        card.setBorder(new EmptyBorder(32, 32, 28, 32));

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

        JLabel lblWelcome = new JLabel("Selamat Datang", SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblWelcome.setForeground(TEXT_WHITE);
        lblWelcome.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Masuk ke akun SafetyGuard Anda", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(TEXT_MUTED);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        inner.add(lblWelcome);
        inner.add(Box.createVerticalStrut(4));
        inner.add(lblSub);
        inner.add(Box.createVerticalStrut(24));

        inner.add(fieldLabel("USERNAME"));
        inner.add(Box.createVerticalStrut(6));
        txtUsername = styledField("Masukkan username Anda...");
        inner.add(txtUsername);
        inner.add(Box.createVerticalStrut(14));

        inner.add(fieldLabel("PASSWORD"));
        inner.add(Box.createVerticalStrut(6));
        txtPassword = new JPasswordField();
        styleField(txtPassword, "Masukkan password Anda...");
        inner.add(txtPassword);
        inner.add(Box.createVerticalStrut(6));

        lblError = new JLabel(" ", SwingConstants.CENTER);
        lblError.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblError.setForeground(new Color(248, 113, 113));
        lblError.setAlignmentX(Component.CENTER_ALIGNMENT);
        inner.add(lblError);
        inner.add(Box.createVerticalStrut(12));

        progressSection = new JPanel(new BorderLayout(0, 4));
        progressSection.setOpaque(false);
        progressSection.setAlignmentX(Component.CENTER_ALIGNMENT);
        progressSection.setPreferredSize(new Dimension(300, 30));
        progressSection.setMaximumSize(new Dimension(300, 30));

        lblStatus = new JLabel("Menghubungkan ke server...");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(TEXT_MUTED);

        progressBar = new JProgressBar(0, 100) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(PROGRESS_BG);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                int fillW = (int)((getWidth() * getValue()) / (double)getMaximum());
                if (fillW > 0) {
                    GradientPaint gp = new GradientPaint(0, 0, ACCENT_BLUE, fillW, 0, ACCENT_CYAN);
                    g2d.setPaint(gp);
                    g2d.fillRoundRect(0, 0, fillW, getHeight(), getHeight(), getHeight());
                }
                g2d.dispose();
            }
        };
        progressBar.setStringPainted(false);
        progressBar.setBorderPainted(false);
        progressBar.setOpaque(false);
        progressBar.setPreferredSize(new Dimension(0, 6));
        progressBar.setValue(0);

        progressSection.add(lblStatus, BorderLayout.NORTH);
        progressSection.add(progressBar, BorderLayout.CENTER);
        progressSection.setVisible(false);

        inner.add(progressSection);
        inner.add(Box.createVerticalStrut(14));

        btnLogin = new JButton("MASUK") {
            private boolean hovered = false;
            {
                addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent e) { hovered = true; repaint(); }
                    public void mouseExited(java.awt.event.MouseEvent e)  { hovered = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c1 = hovered ? ACCENT_CYAN  : ACCENT_BLUE;
                Color c2 = hovered ? ACCENT_BLUE : ACCENT_CYAN;
                g2.setPaint(new GradientPaint(0, 0, c1, getWidth(), 0, c2));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBorderPainted(false);
        btnLogin.setContentAreaFilled(false);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setPreferredSize(new Dimension(300, 50));
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.addActionListener(e -> processLogin());
        txtPassword.addActionListener(e -> processLogin());

        inner.add(btnLogin);
        inner.add(Box.createVerticalStrut(16));

        JLabel lblFooter = new JLabel("PT. Pabrik Manufaktur Sejahtera - Sistem HSE Internal");
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        lblFooter.setForeground(new Color(71, 85, 105));
        lblFooter.setAlignmentX(Component.CENTER_ALIGNMENT);
        inner.add(lblFooter);

        card.add(inner, BorderLayout.CENTER);
        wrapper.add(card, new GridBagConstraints());
        return wrapper;
    }

    private JLabel fieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(TEXT_MUTED);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField styledField(String placeholder) {
        JTextField f = new JTextField();
        styleField(f, placeholder);
        return f;
    }

    private void styleField(JTextField f, String placeholder) {
        f.putClientProperty("JTextField.placeholderText", placeholder);
        f.setBackground(INPUT_BG);
        f.setForeground(TEXT_WHITE);
        f.setCaretColor(ACCENT_BLUE);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(12, 14, 12, 14)
        ));
        f.setAlignmentX(Component.CENTER_ALIGNMENT);
        f.setPreferredSize(new Dimension(300, 48));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
    }

    private void processLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Username dan password tidak boleh kosong!");
            return;
        }
        lblError.setText(" ");

        btnLogin.setEnabled(false);
        btnLogin.setText("  MEMVERIFIKASI...");
        progressSection.setVisible(true);
        progressBar.setValue(0);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        String[] steps = {"Menghubungkan ke server...", "Memvalidasi kredensial...", "Memuat data pengguna..."};
        int[] targets = {30, 65, 85};
        final int[] stepIdx = {0};

        Timer progressTimer = new Timer(500, null);
        progressTimer.addActionListener(ev -> {
            if (stepIdx[0] < steps.length) {
                lblStatus.setText(steps[stepIdx[0]]);
                animateProgress(progressBar, targets[stepIdx[0]]);
                stepIdx[0]++;
            } else {
                progressTimer.stop();
            }
        });
        progressTimer.start();

        SwingWorker<Pengguna, Void> worker = new SwingWorker<>() {
            @Override protected Pengguna doInBackground() throws Exception {
                PenggunaDAO dao = new PenggunaDAO();
                return dao.login(username, password);
            }
            @Override protected void done() {
                progressTimer.stop();
                setCursor(Cursor.getDefaultCursor());
                try {
                    Pengguna pengguna = get();
                    if (pengguna != null) {
                        lblStatus.setText("Login berhasil! Membuka dashboard...");
                        animateProgress(progressBar, 100);
                        Timer closeTimer = new Timer(600, ev2 -> {
                            dispose();
                            new DashboardFrame(pengguna).setVisible(true);
                        });
                        closeTimer.setRepeats(false);
                        closeTimer.start();
                    } else {
                        progressSection.setVisible(false);
                        progressBar.setValue(0);
                        btnLogin.setEnabled(true);
                        btnLogin.setText("  MASUK");
                        lblError.setText("Username atau password salah. Coba lagi.");
                        txtPassword.setText("");
                        txtPassword.requestFocusInWindow();
                    }
                } catch (Exception ex) {
                    progressSection.setVisible(false);
                    btnLogin.setEnabled(true);
                    btnLogin.setText("  MASUK");
                    lblError.setText("Gagal terhubung ke server. Periksa koneksi.");
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    /** Animasi smooth progress bar ke nilai target */
    private void animateProgress(JProgressBar bar, int target) {
        Timer t = new Timer(20, null);
        t.addActionListener(e -> {
            int cur = bar.getValue();
            if (cur < target) {
                bar.setValue(Math.min(cur + 2, target));
            } else {
                ((Timer) e.getSource()).stop();
            }
        });
        t.start();
    }

    public static void main(String[] args) {
        try { FlatLightLaf.setup(); } catch (Exception ex) { }
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}
