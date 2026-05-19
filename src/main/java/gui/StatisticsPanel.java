package gui;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import database.MongoDBConnection;
import org.bson.Document;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class StatisticsPanel extends JFrame {

    private MongoCollection<Document> collection;

    // Matched UI Theme Palette
    private final Color COLOR_BG = new Color(243, 244, 246);       // Light Gray background
    private final Color COLOR_CARD = Color.WHITE;                  // Crisp white cards
    private final Color COLOR_PRIMARY = new Color(17, 24, 39);     // Modern Near-Black for headers
    private final Color COLOR_BUTTON_BG = new Color(55, 65, 81);   // Dark Gray for header button
    private final Color COLOR_SECONDARY = new Color(209, 213, 219); // Clean gray borders
    private final Color COLOR_TEXT_MAIN = new Color(31, 41, 55);   // High readability text
    private final Color COLOR_TEXT_MUTED = new Color(156, 163, 175);
    private final Color COLOR_ACCENT_RED = new Color(239, 68, 68);

    // UI elements that need background data updates
    private JLabel totalLabel, criticalLabel, resolvedLabel, openLabel;
    private JPanel severityPanel, categoryPanel;
    private JLabel damageLabel;

    public StatisticsPanel() throws Exception {

        setTitle("Incident Statistics Dashboard");
        setSize(1050, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(COLOR_BG);

        MongoDatabase db = MongoDBConnection.connect();
        collection = db.getCollection("incidents");

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(COLOR_BG);

        // HEADER PANEL
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_PRIMARY);
        headerPanel.setBorder(new EmptyBorder(15, 30, 15, 30));

        JLabel title = new JLabel("Cybersecurity Incident Analytics");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        headerPanel.add(title, BorderLayout.WEST);

        // Back button with White text color
        JButton backButton = createStyledHeaderButton("Back");
        backButton.addActionListener(e -> {
            dispose();
            new Dashboard();
        });
        headerPanel.add(backButton, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // CONTENT CONTAINER
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(COLOR_BG);
        contentPanel.setBorder(new EmptyBorder(25, 30, 25, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        // ROW 1: TOP STAT CARDS
        JPanel cardsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        cardsPanel.setBackground(COLOR_BG);
        
        cardsPanel.add(createStatCard("Total Incidents", totalLabel = new JLabel("..."), COLOR_PRIMARY));
        cardsPanel.add(createStatCard("Critical Status", criticalLabel = new JLabel("..."), COLOR_ACCENT_RED));
        cardsPanel.add(createStatCard("Resolved Cases", resolvedLabel = new JLabel("..."), new Color(16, 185, 129)));
        cardsPanel.add(createStatCard("Open Tasks", openLabel = new JLabel("..."), new Color(245, 158, 11)));
        
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 20, 0);
        contentPanel.add(cardsPanel, gbc);

        // ROW 2: ANALYTICS SPLIT PANEL
        JPanel analyticsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        analyticsPanel.setBackground(COLOR_BG);

        severityPanel = createAnalyticsPanel("Incidents by Severity");
        analyticsPanel.add(severityPanel);

        categoryPanel = createAnalyticsPanel("Incidents by Category");
        analyticsPanel.add(categoryPanel);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 20, 0);
        contentPanel.add(analyticsPanel, gbc);

        // ROW 3: FINANCIAL IMPACT PANEL
        JPanel damagePanel = createAnalyticsPanel("Total Estimated Financial Impact");
        damagePanel.setLayout(new BorderLayout());

        damageLabel = new JLabel("Calculating...");
        damageLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        damageLabel.setForeground(COLOR_ACCENT_RED);
        damagePanel.add(damageLabel, BorderLayout.WEST);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        contentPanel.add(damagePanel, gbc);

        // SCROLLPANE WRAPPER
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(COLOR_BG);
        scrollPane.getViewport().setBackground(COLOR_BG); // Force light gray scroll window background
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel);
        setVisible(true);

        loadStatisticsAsync();
    }

    private void loadStatisticsAsync() {
        SwingWorker<Document, Void> worker = new SwingWorker<>() {
            @Override
            protected Document doInBackground() {
                Document stats = new Document();

                long total = collection.countDocuments();
                stats.append("total", total);
                stats.append("critical", collection.countDocuments(new Document("severity", "Critical")));
                stats.append("resolved", collection.countDocuments(new Document("status", "Resolved")));
                stats.append("open", collection.countDocuments(new Document("status", "Open")));

                stats.append("sev_low", collection.countDocuments(new Document("severity", "Low")));
                stats.append("sev_med", collection.countDocuments(new Document("severity", "Medium")));
                stats.append("sev_high", collection.countDocuments(new Document("severity", "High")));
                stats.append("sev_crit", collection.countDocuments(new Document("severity", "Critical")));

                String[] categories = {"Malware", "Phishing", "DDoS Attack", "Unauthorized Access", "Data Breach", "Ransomware", "Social Engineering", "Other"};
                Document catsDoc = new Document();
                for (String cat : categories) {
                    catsDoc.append(cat, collection.countDocuments(new Document("category", cat)));
                }
                stats.append("categories", catsDoc);

                long totalDamage = 0;
                for (Document doc : collection.find()) {
                    Object value = doc.get("estimatedDamage");
                    if (value instanceof Number) {
                        totalDamage += ((Number) value).longValue();
                    }
                }
                stats.append("damage", totalDamage);

                return stats;
            }

            @Override
            protected void done() {
                try {
                    Document result = get();

                    totalLabel.setText(String.valueOf(result.getLong("total")));
                    criticalLabel.setText(String.valueOf(result.getLong("critical")));
                    resolvedLabel.setText(String.valueOf(result.getLong("resolved")));
                    openLabel.setText(String.valueOf(result.getLong("open")));

                    severityPanel.add(createStatRow("Low Level", result.getLong("sev_low")));
                    severityPanel.add(createStatRow("Medium Level", result.getLong("sev_med")));
                    severityPanel.add(createStatRow("High Level", result.getLong("sev_high")));
                    severityPanel.add(createStatRow("Critical Level", result.getLong("sev_crit")));
                    severityPanel.revalidate();
                    severityPanel.repaint();

                    Document cats = (Document) result.get("categories");
                    for (String cat : cats.keySet()) {
                        categoryPanel.add(createStatRow(cat, cats.getLong(cat)));
                    }
                    categoryPanel.revalidate();
                    categoryPanel.repaint();

                    damageLabel.setText("Rs. " + String.format("%,d", result.getLong("damage")));

                } catch (Exception e) {
                    damageLabel.setText("Error pulling metrics");
                    JOptionPane.showMessageDialog(StatisticsPanel.this, "Error fetching background analytics: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color indicatorColor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_SECONDARY, 1, true),
                new EmptyBorder(12, 16, 12, 16)
        ));

        JLabel titleLabel = new JLabel(title.toUpperCase());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        titleLabel.setForeground(COLOR_TEXT_MUTED);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valueLabel.setForeground(indicatorColor);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createAnalyticsPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_SECONDARY, 1, true),
                new EmptyBorder(16, 20, 16, 20)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLabel.setForeground(COLOR_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(10));

        return panel;
    }

    private JPanel createStatRow(String labelText, long count) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(COLOR_CARD);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setBorder(new EmptyBorder(2, 0, 2, 0));

        JLabel nameLabel = new JLabel(labelText);
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        nameLabel.setForeground(COLOR_TEXT_MAIN);

        JLabel countLabel = new JLabel(String.valueOf(count));
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        countLabel.setForeground(count > 0 ? COLOR_PRIMARY : COLOR_TEXT_MUTED);

        row.add(nameLabel, BorderLayout.WEST);
        row.add(countLabel, BorderLayout.EAST);

        return row;
    }

    private JButton createStyledHeaderButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setPreferredSize(new Dimension(90, 32));
        
        // Changed to match the settings panel button background and color
        button.setBackground(COLOR_BUTTON_BG);
        button.setForeground(Color.WHITE); // Turned foreground to White
        
        button.setBorder(BorderFactory.createLineBorder(COLOR_SECONDARY, 1));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover animation logic for Dark Gray aesthetics
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(31, 41, 55)); // Slightly darker on hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(COLOR_BUTTON_BG);
            }
        });

        return button;
    }
}