import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.UIManager;
import javax.swing.SwingUtilities;
import view.LoginForm;

/**
 * Entry point aplikasi SafetyGuard HSE Management System.
 * @author macbook
 */
public class Main {
    public static void main(String[] args) {
        // 1. Inisialisasi tema modern FlatLaf Dark
        try {
            FlatDarkLaf.setup();
            UIManager.put("TabbedPane.selectedBackground", new java.awt.Color(37, 99, 235)); // Biru jelas saat dipilih
            UIManager.put("TabbedPane.background",         new java.awt.Color(22, 27, 45));
            UIManager.put("TabbedPane.contentAreaColor",   new java.awt.Color(15, 23, 42));
            UIManager.put("TabbedPane.tabAreaBackground",  new java.awt.Color(13, 17, 28)); // Mengganti background abu-abu
            UIManager.put("TabbedPane.hoverColor",         new java.awt.Color(51, 65, 85));
            UIManager.put("TabbedPane.font",               new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 15));
            UIManager.put("TabbedPane.foreground",         new java.awt.Color(203, 213, 225));
            UIManager.put("TabbedPane.selectedForeground", java.awt.Color.WHITE);
            UIManager.put("TabbedPane.tabHeight",          45); // Tab lebih besar agar mudah diklik
            UIManager.put("TabbedPane.underlineColor",     new java.awt.Color(96, 165, 250));

            UIManager.put("Table.selectionBackground",     new java.awt.Color(37, 99, 235, 180));
            UIManager.put("Table.rowHeight", 32); // Baris tabel lebih lega
            UIManager.put("Button.arc", 12);
            UIManager.put("Component.arc", 12);
            UIManager.put("TextComponent.arc", 12);
        } catch (Throwable ex) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 2. Langsung tampilkan Login Form
        SwingUtilities.invokeLater(() -> {
            LoginForm loginForm = new LoginForm();
            loginForm.setLocationRelativeTo(null);
            loginForm.setVisible(true);
        });
    }
}
