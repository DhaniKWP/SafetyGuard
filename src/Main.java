import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.UIManager;
import javax.swing.SwingUtilities;
import view.LoginForm;

/**
 * Entry point aplikasi SafetyGuard HSE Management System.
 * @author macbook
 */
public class Main {
    public static void main(String[] args) {
        try {
            FlatLightLaf.setup();
            UIManager.put("TabbedPane.selectedBackground", new java.awt.Color(37, 99, 235));
            UIManager.put("TabbedPane.background",         new java.awt.Color(248, 250, 252));
            UIManager.put("TabbedPane.contentAreaColor",   new java.awt.Color(241, 245, 249));
            UIManager.put("TabbedPane.unselectedBackground", new java.awt.Color(241, 245, 249));
            UIManager.put("TabbedPane.hoverColor",         new java.awt.Color(226, 232, 240));
            UIManager.put("TabbedPane.font",               new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 15));
            UIManager.put("TabbedPane.foreground",         new java.awt.Color(15, 23, 42));
            UIManager.put("TabbedPane.selectedForeground", new java.awt.Color(255, 255, 255));
            UIManager.put("TabbedPane.tabHeight",          45);
            UIManager.put("TabbedPane.underlineColor",     new java.awt.Color(96, 165, 250));

            UIManager.put("Table.selectionBackground",     new java.awt.Color(37, 99, 235, 180));
            UIManager.put("Table.rowHeight", 32);
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

        SwingUtilities.invokeLater(() -> {
            LoginForm loginForm = new LoginForm();
            loginForm.setLocationRelativeTo(null);
            loginForm.setVisible(true);
        });
    }
}
