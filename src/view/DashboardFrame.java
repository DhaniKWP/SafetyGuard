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

        // Inisialisasi Tabbed Pane
        tabbedPane = new JTabbedPane();

        // Menyambungkan Panel-Panel Modular
        tabbedPane.addTab("Laporan Insiden", new PanelInsiden(loggedInUser));
        tabbedPane.addTab("Tindakan CAPA", new PanelCapa(loggedInUser));
        
        // Tab Inspeksi Keselamatan (Hanya untuk Admin/Staff HSE)
        if (!loggedInUser.getPeran().equalsIgnoreCase("Karyawan")) {
            tabbedPane.addTab("Inspeksi Keselamatan", new PanelInspeksi(loggedInUser));
        }
        
        tabbedPane.addTab("Distribusi APD", new PanelApd(loggedInUser));
        tabbedPane.addTab("Laporan & Rekap", new PanelLaporan(loggedInUser));

        add(tabbedPane, BorderLayout.CENTER);

        // Footer Status Bar
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBorder(BorderFactory.createEtchedBorder());
        JLabel lblStatus = new JLabel("Status: Terhubung ke database Supabase | Pengguna Aktif: " + user.getUsername());
        statusPanel.add(lblStatus);
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
