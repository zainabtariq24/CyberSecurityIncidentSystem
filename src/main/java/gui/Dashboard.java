package gui;

import database.MongoDBConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Dashboard extends JFrame {

    private JButton reportButton;
    private JButton viewButton;
    private JButton statisticsButton;
    private JButton settingsButton;
    private JButton exitButton;

    // Modern Enterprise Light Palette
    private final Color appBg = new Color(249, 250, 251);       // Ultra-light clean slate background (#F9FAFB)
    private final Color headerBg = Color.WHITE;                  // Crisp white header
    private final Color cardBg = Color.WHITE;                    // White cards
    private final Color borderLight = new Color(229, 231, 235);  // Delicate separator line color (#E5E7EB)
    
    // Typography Colors
    private final Color textPrimary = new Color(17, 24, 39);     // Deep charcoal/black for readability (#111827)
    private final Color textSecondary = new Color(75, 85, 99);   // Professional slate gray (#4B5563)
    
    // Cyber Accents
    private final Color hoverTint = new Color(243, 244, 246);    // Subtle gray for mouse hover (#F3F4F6)

    public Dashboard() {
        setTitle("Cybersecurity Incident Management System");
        setSize(950, 650); 
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // =========================
        // MAIN PANEL
        // =========================
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(appBg);

        // =======================================================
        // HEADER (Title Left, Uniform KPI Cards Right)
        // =======================================================
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(950, 110)); 
        headerPanel.setBackground(headerBg);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, borderLight));

        // LEFT: Header Title Typography Block
        JPanel textWrapper = new JPanel();
        textWrapper.setLayout(new BoxLayout(textWrapper, BoxLayout.Y_AXIS));
        textWrapper.setBackground(headerBg);
        textWrapper.setBorder(new EmptyBorder(25, 45, 20, 15)); 
        
        JLabel titleLabel = new JLabel("Cybersecurity Incident System"); 
        titleLabel.setForeground(textPrimary);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22)); 
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Incident Reporting & Management Console");
        subtitle.setForeground(textSecondary);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        textWrapper.add(titleLabel);
        textWrapper.add(Box.createVerticalStrut(4));
        textWrapper.add(subtitle);
        headerPanel.add(textWrapper, BorderLayout.WEST);

        // RIGHT: Integrated Perfectly Sized KPI Metrics Row
        JPanel metricsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        metricsPanel.setBackground(headerBg);
        metricsPanel.setBorder(new EmptyBorder(15, 10, 15, 45)); // Adjusted vertically to center the uniform blocks
        
        // Custom color codes applied to the boundaries of uniform square modules
        metricsPanel.add(createUniformKPICard("ACTIVE", "14", new Color(220, 38, 38)));    // Red Focus
        metricsPanel.add(createUniformKPICard("CRITICAL", "3", new Color(234, 179, 8)));   // Yellow Focus
        metricsPanel.add(createUniformKPICard("RESOLVED", "28", new Color(22, 163, 74)));  // Green Focus

        headerPanel.add(metricsPanel, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // =========================
        // CENTER PANEL (GRID CARDS)
        // =========================
        JPanel centerPanel = new JPanel(new GridLayout(2, 2, 25, 25)); 
        centerPanel.setBorder(BorderFactory.createEmptyBorder(40, 45, 40, 45)); 
        centerPanel.setBackground(appBg);

        reportButton = createCardButton("Report Incident", "Log new security anomalies, system breaches, or threats.");
        reportButton.addActionListener(e -> {
            new IncidentForm();
            dispose();
        });

        viewButton = createCardButton("Manage Incidents", "Review active logs, update severity statuses, and assign handlers.");
        viewButton.addActionListener(e -> {
            new ViewIncidents();
            dispose();
        });

        statisticsButton = createCardButton("Statistics & Trends", "Examine metrics, threat frequency, and diagnostic charts.");
        statisticsButton.addActionListener(e -> {
            try { new StatisticsPanel(); } catch (Exception ex) { ex.printStackTrace(); }
            dispose();
        });

        settingsButton = createCardButton("System Settings", "Configure application preferences and database parameters.");
        settingsButton.addActionListener(e -> {
            new SettingsPanel();
            this.dispose();
        });

        centerPanel.add(reportButton);
        centerPanel.add(viewButton);
        centerPanel.add(statisticsButton);
        centerPanel.add(settingsButton);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // =========================
        // FOOTER
        // =========================
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(headerBg); 
        footerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, borderLight),
                BorderFactory.createEmptyBorder(12, 45, 12, 45) 
        ));

        JLabel statusLabel = new JLabel("<html><span style='color:#16a34a; font-size:11px;'>●</span> <span style='font-weight:600;'>System Status:</span> Connected to MongoDB Atlas</html>");
        statusLabel.setForeground(textSecondary);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        footerPanel.add(statusLabel, BorderLayout.WEST);

        exitButton = new JButton("Exit Console");
        exitButton.setFocusPainted(false);
        exitButton.setBackground(Color.WHITE);
        exitButton.setForeground(new Color(220, 38, 38)); 
        exitButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        exitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exitButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(252, 165, 165), 1), 
                BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));

        exitButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { 
                exitButton.setBackground(new Color(254, 242, 242)); 
            }
            public void mouseExited(MouseEvent e) { 
                exitButton.setBackground(Color.WHITE); 
            }
        });

        exitButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this, "Are you sure you want to exit the management console?", 
                    "Confirm Exit", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION) {
                MongoDBConnection.closeConnection();
                System.exit(0);
            }
        });

        footerPanel.add(exitButton, BorderLayout.EAST);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    // =========================================================================
    // THE UNIFORM KPI CARD MODULE (Strict Dimensions & Color Coded Outlines)
    // =========================================================================
    private JPanel createUniformKPICard(String labelText, String countValue, Color statusColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        
        // FIXED: Explicit structural layout constraint ensuring absolute uniformity among variables
        card.setPreferredSize(new Dimension(90, 76)); 
        card.setMinimumSize(new Dimension(90, 76));
        card.setMaximumSize(new Dimension(90, 76));

        // FIXED: Handled with a clean double-pixel matte border frame of their respective status color
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(statusColor, 2), 
                BorderFactory.createEmptyBorder(10, 5, 8, 5) 
        ));

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(textSecondary);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel val = new JLabel(countValue);
        val.setFont(new Font("Segoe UI", Font.BOLD, 22)); 
        val.setForeground(textPrimary); // Swapped color focus to textPrimary for high-end web readability
        val.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(lbl);
        card.add(Box.createVerticalStrut(3));
        card.add(val);
        return card;
    }

    // ===================================
    // REFINED WEB-STYLE CARD GENERATION
    // ===================================
    private JButton createCardButton(String title, String description) {
        String htmlContent = "<html>"
                + "<body style='font-family: \"Segoe UI\", sans-serif; padding: 2px; text-align: left;'>"
                + "<div style='color: #111827; font-size: 14px; font-weight: bold; margin-bottom: 5px;'>" + title + "</div>"
                + "<div style='color: #4B5563; font-size: 11px; font-weight: normal; line-height: 1.4;'>" + description + "</div>"
                + "</body></html>";

        JButton button = new JButton(htmlContent);
        button.setBackground(cardBg);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT); 
        
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderLight, 1),
                BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverTint);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(textPrimary, 1), 
                        BorderFactory.createEmptyBorder(24, 24, 24, 24)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(cardBg);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(borderLight, 1),
                        BorderFactory.createEmptyBorder(24, 24, 24, 24)
                ));
            }
        });

        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Dashboard::new);
    }
}