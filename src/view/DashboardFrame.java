package view;

import com.formdev.flatlaf.FlatDarkLaf;
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

        // Inisialisasi Tabbed Pane & Background
        getContentPane().setBackground(new Color(13, 18, 30)); // Warna BG_HEADER
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(new Color(22, 27, 45)); // Warna BG_CARD untuk tab tidak aktif
        tabbedPane.setOpaque(true);

        // Menyambungkan Panel-Panel Modular
        tabbedPane.addTab("Laporan Insiden", new PanelInsiden(loggedInUser));
        tabbedPane.addTab("Tindakan CAPA", new PanelCapa(loggedInUser));
        
        // Tab Inspeksi Keselamatan (Hanya untuk Admin/Staff HSE)
        if (!loggedInUser.getPeran().equalsIgnoreCase("Karyawan")) {
            tabbedPane.addTab("Inspeksi Keselamatan", new PanelInspeksi(loggedInUser));
        }
        
        tabbedPane.addTab("Distribusi APD", new PanelApd(loggedInUser));
        tabbedPane.addTab("Laporan & Rekap", new PanelLaporan(loggedInUser));

        // Tombol Logout di Tab Kanan Atas
        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogout.setForeground(new Color(239, 68, 68)); // Warna merah peringatan
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
        
        // Memasukkan tombol Logout ke ruang kosong tabbed pane sebelah kanan
        tabbedPane.putClientProperty("JTabbedPane.trailingComponent", btnLogout);

        add(tabbedPane, BorderLayout.CENTER);

        // Footer Status Bar
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        statusPanel.setBackground(new Color(15, 23, 42)); // Sama dengan BG_MAIN

        JLabel lblStatus = new JLabel("Status: Terhubung ke database Supabase | Pengguna Aktif: " + user.getUsername());
        lblStatus.setForeground(new Color(148, 163, 184)); // Sama dengan TEXT_MUTED
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusPanel.add(lblStatus, BorderLayout.WEST);

        add(statusPanel, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        // Kelas main cadangan jika ingin langsung mengetes DashboardFrame tanpa login
        try {
            FlatDarkLaf.setup();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        // Buat mock user untuk testing
        Pengguna mockUser = new Pengguna("hse_test", "password", "mock-uuid", "Staff HSE");
        SwingUtilities.invokeLater(() -> new DashboardFrame(mockUser).setVisible(true));
    }
}
