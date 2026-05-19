import gui.Dashboard;
import database.MongoDBConnection;
import javax.swing.*;

/**
 * Main Entry Point for Cybersecurity Incident Management System
 * Initializes the application and ensures database connectivity
 */
public class Main {

    public static void main(String[] args) {
        try {
            // Test database connection before starting the application
            if (!MongoDBConnection.testConnection()) {
                System.err.println("Warning: Could not establish database connection.");
                int option = JOptionPane.showConfirmDialog(null,
                        "Database connection failed. Continue anyway?",
                        "Connection Error",
                        JOptionPane.YES_NO_OPTION);
                if (option != JOptionPane.YES_OPTION) {
                    System.exit(1);
                }
            }

            // Launch the GUI on the Event Dispatch Thread
            SwingUtilities.invokeLater(() -> {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new Dashboard();
            });

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Fatal Error: " + e.getMessage(),
                    "Application Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            System.exit(1);
        }
    }
}