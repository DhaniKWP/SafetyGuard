/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.UIManager;
import javax.swing.SwingUtilities;
import view.LoginForm;
/**
 *
 * @author macbook
 */
public class Main {
    public static void main(String[] args) {
        // 1. Inisialisasi tema modern
        try {
            FlatDarkLaf.setup();
        } catch (Throwable ex) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // DIAGNOSTIC - Cetak respon mentah dari Supabase untuk memeriksa kolom dan koneksi
        try {
            String res = helper.SupabaseClient.get("/pengguna?username=eq.dhani");
            System.out.println("DIAGNOSTIC - Respon Supabase: " + res);
        } catch (Exception e) {
            System.out.println("DIAGNOSTIC - Gagal koneksi API: " + e.getMessage());
            e.printStackTrace();
        }

        // 2. Jalankan Tampilan Login (LoginForm)
        SwingUtilities.invokeLater(() -> {
            LoginForm loginForm = new LoginForm();
            loginForm.setLocationRelativeTo(null);
            loginForm.setVisible(true);
        });
    }
}
