package gui;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import database.MongoDBConnection;
import org.bson.Document;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class SettingsPanel extends JFrame {

    // Modern Minimalist Light Gray & White Theme Palette
    private final Color BG = new Color(249, 250, 251);       // Ultra-clean page background
    private final Color CARD = Color.WHITE;                  // Card backgrounds
    private final Color TEXT_DARK = new Color(17, 24, 39);   // Primary Text
    private final Color TEXT_MUTED = new Color(107, 114, 128); // Secondary/Label Text
    private final Color BORDER = new Color(229, 231, 235);   // Crisp, thin dividers

    // Status Accent Colors
    private final Color SUCCESS_GREEN = new Color(34, 197, 94); // Vibrant Emerald Green
    private final Color ERROR_RED = new Color(239, 68, 68);     // Modern Red

    public SettingsPanel() {
        setTitle("System Settings and Status");
        setSize(800, 520); // Slightly wider for structural breathing room
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(BG);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG);

        // ==========================================
        // HEADER (Clean & Minimal)
        // ==========================================
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CARD); 
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, BORDER),
                new EmptyBorder(16, 30, 16, 30)
        ));

        JLabel title = new JLabel("System Information");
        title.setForeground(TEXT_DARK);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        headerPanel.add(title, BorderLayout.WEST);

        JButton backButton = createButton("Back");
        backButton.addActionListener(e -> {
            dispose();
            new Dashboard();
        });
        headerPanel.add(backButton, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // ==========================================
        // CONTENT SCROLL CONTAINER
        // ==========================================
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG);
        contentPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // ==========================================
        // CARD 1: DATABASE STATUS
        // ==========================================
        JPanel dbPanel = createCardPanel("Database Infrastructure");
        
        try {
            MongoDatabase db = MongoDBConnection.connect();
            MongoCollection<Document> collection = db.getCollection("incidents");
            long totalRecords = collection.countDocuments();

            // Row 1: Status with a live rendering UI dot
            dbPanel.add(createStatusRow("Connection Status", "Connected", SUCCESS_GREEN));
            dbPanel.add(createRowDivider());
            // Row 2: DB Name
            dbPanel.add(createDataRow("Database Name", "incidentDB"));
            dbPanel.add(createRowDivider());
            // Row 3: Collection
            dbPanel.add(createDataRow("Target Collection", "incidents"));
            dbPanel.add(createRowDivider());
            // Row 4: Records Count
            dbPanel.add(createDataRow("Total Document Records", String.valueOf(totalRecords)));

        } catch (Exception ex) {
            dbPanel.add(createStatusRow("Connection Status", "Offline / Disconnected", ERROR_RED));
            dbPanel.add(createRowDivider());
            dbPanel.add(createDataRow("Error Log", ex.getMessage()));
        }

        contentPanel.add(dbPanel);
        contentPanel.add(Box.createVerticalStrut(25));

        // ==========================================
        // CARD 2: ENVIRONMENT INFO
        // ==========================================
        JPanel systemPanel = createCardPanel("Environment & Runtime");
        
        systemPanel.add(createDataRow("Java Runtime Version", System.getProperty("java.version")));
        systemPanel.add(createRowDivider());
        systemPanel.add(createDataRow("Host Operating System", System.getProperty("os.name")));
        systemPanel.add(createRowDivider());
        systemPanel.add(createDataRow("OS Architecture", System.getProperty("os.arch")));
        systemPanel.add(createRowDivider());
        systemPanel.add(createDataRow("Application Version", "1.0.0"));

        contentPanel.add(systemPanel);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(BG);
        scrollPane.getViewport().setBackground(BG); 
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    // ==========================================
    // UI COMPONENT & LAYOUT GENERATORS
    // ==========================================
    
    private JPanel createCardPanel(String titleText) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true), // Sharp, slightly rounded clean card border
                new EmptyBorder(20, 24, 20, 24)
        ));

        JLabel titleLabel = new JLabel(titleText);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_DARK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(15));

        return panel;
    }

    // Generates a clean key-value grid split row
    private JPanel createDataRow(String labelText, String valueText) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(CARD);
        row.setMaximumSize(new Dimension(Short.MAX_VALUE, 35));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lbl.setForeground(TEXT_MUTED);

        JLabel val = new JLabel(valueText);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        val.setForeground(TEXT_DARK);

        row.add(lbl, BorderLayout.WEST);
        row.add(val, BorderLayout.EAST);
        return row;
    }

    // Generates a dashboard row featuring an automated status indicator dot
    private JPanel createStatusRow(String labelText, String statusText, Color statusColor) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(CARD);
        row.setMaximumSize(new Dimension(Short.MAX_VALUE, 35));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lbl.setForeground(TEXT_MUTED);
        row.add(lbl, BorderLayout.WEST);

        // Side-by-side panel containing the glowing indicator dot + status text
        JPanel statusContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        statusContainer.setBackground(CARD);

        StatusDot dot = new StatusDot(statusColor);
        JLabel val = new JLabel(statusText);
        val.setFont(new Font("Segoe UI", Font.BOLD, 13));
        val.setForeground(statusColor);

        statusContainer.add(dot);
        statusContainer.add(val);

        row.add(statusContainer, BorderLayout.EAST);
        return row;
    }

    // Creates an ultra-fine divider between properties inside cards
    private JComponent createRowDivider() {
        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setMaximumSize(new Dimension(Short.MAX_VALUE, 1));
        sep.setForeground(BORDER);
        sep.setBackground(CARD);
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        return sep;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(CARD);
        button.setForeground(TEXT_MUTED);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(90, 32));
        button.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(BG);
                button.setForeground(TEXT_DARK);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(CARD);
                button.setForeground(TEXT_MUTED);
            }
        });
        return button;
    }

    // ==========================================
    // INNER CUSTOM GRAPHICS CLASS: STATUS DOT
    // ==========================================
    private static class StatusDot extends JComponent {
        private final Color dotColor;

        public StatusDot(Color color) {
            this.dotColor = color;
            setPreferredSize(new Dimension(10, 10));
            setMinimumSize(new Dimension(10, 10));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(dotColor);
            // Draws a perfectly centered clean circle dot
            g2d.fillOval(0, 1, 8, 8);
        }
    }
}