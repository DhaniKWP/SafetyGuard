/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import com.formdev.flatlaf.FlatDarkLaf;
import dao.PenggunaDAO;
import model.Pengguna;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
/**
 *
 * @author macbook
 */
public class LoginForm extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    public LoginForm() {
        // Inisialisasi Frame Utama
        setTitle("SafetyGuard HSE - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Memosisikan form di tengah layar
        setLayout(new BorderLayout());
        // Membuat Panel Form Tengah dengan padding
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8); // Jarak antar komponen
        // Judul Aplikasi
        JLabel lblTitle = new JLabel("SafetyGuard Login", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        gbc.gridx = 0;  
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(lblTitle, gbc);
        // Input Username
        JLabel lblUsername = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(lblUsername, gbc);
        txtUsername = new JTextField(15);
        // Custom FlatLaf placeholder text (opsional tapi keren)
        txtUsername.putClientProperty("JTextField.placeholderText", "Masukkan username...");
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(txtUsername, gbc);
        // Input Password
        JLabel lblPassword = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(lblPassword, gbc);
        txtPassword = new JPasswordField(15);
        txtPassword.putClientProperty("JTextField.placeholderText", "Masukkan password...");
        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(txtPassword, gbc);
        // Tombol Login
        btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formPanel.add(btnLogin, gbc);
        add(formPanel, BorderLayout.CENTER);
        // Menghubungkan Aksi Klik Tombol ke Method Login
        btnLogin.addActionListener(e -> processLogin());
    }
    private void processLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        // Validasi input kosong
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                    "Username dan password tidak boleh kosong!", 
                    "Peringatan", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Tampilkan loading cursor sementara
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            // Validasi ke Database Supabase
            PenggunaDAO penggunaDAO = new PenggunaDAO();
            Pengguna pengguna = penggunaDAO.login(username, password);
            if (pengguna != null) {
                JOptionPane.showMessageDialog(this, 
                        "Login sukses! Masuk sebagai: " + pengguna.getPeran(), 
                        "Sukses", 
                        JOptionPane.INFORMATION_MESSAGE);
                // Buka Dashboard Utama (DashboardFrame akan diimplementasikan setelah ini)
                new DashboardFrame(pengguna).setVisible(true);
                this.dispose(); // Tutup window login
            } else {
                JOptionPane.showMessageDialog(this, 
                        "Username atau Password salah!", 
                        "Gagal", 
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan sistem: " + e.getMessage());
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }
    public static void main(String[] args) {
        // Aktifkan tema modern FlatLaf
        try {
            FlatDarkLaf.setup();
        } catch (Exception ex) {
            System.err.println("Gagal mengaktifkan FlatLaf");
        }
        // Jalankan UI
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}
