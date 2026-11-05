import gui.LoginFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Entry point. Run this class. */
public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // keep the default look if the system one is not available
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginFrame().setVisible(true);
            }
        });
    }
}
