package gui;

import database.MongoDBConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;

public class IncidentForm extends JFrame {

    private JTextField titleField;
    private JComboBox<String> categoryField;
    private JComboBox<String> severityBox;
    private JComboBox<String> priorityBox;
    private JTextField systemField;
    private JTextArea descriptionArea;
    private JTextField reportedByField;
    private JTextField assignedToField;
    private JSpinner damageSpinner;
    private JButton submitButton;
    private JButton clearButton;
    private JButton backButton;

    // Premium Neutral Color Palette (Tailwind UI Inspired)
    private final Color COLOR_BG = new Color(243, 244, 246);       // Soft background gray
    private final Color COLOR_CARD = Color.WHITE;                  // Crisp card face
    private final Color COLOR_PRIMARY = new Color(17, 24, 39);     // Slate Primary
    private final Color COLOR_PRIMARY_HOVER = new Color(37, 47, 63);
    private final Color COLOR_SECONDARY = new Color(229, 231, 235); // Subtle Borders
    private final Color COLOR_TEXT_MAIN = new Color(31, 41, 55);   // High-contrast text
    private final Color COLOR_HOVER_LIGHT = new Color(249, 250, 251);

    public IncidentForm() {
        setTitle("Report New Cybersecurity Incident");
        setSize(800, 900); // Expanded slightly for optimal component scaling
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(COLOR_BG);

        setLayout(new BorderLayout());

        // ==========================================
        // HEADER PANEL
        // ==========================================
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerPanel.setBackground(COLOR_BG);
        headerPanel.setBorder(new EmptyBorder(30, 45, 15, 45));
        
        JLabel heading = new JLabel("New Incident Report");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 28));
        heading.setForeground(COLOR_PRIMARY);
        headerPanel.add(heading);
        add(headerPanel, BorderLayout.NORTH);

        // ==========================================
        // CENTRAL FORM CARD (GridBagLayout Polish)
        // ==========================================
        JPanel cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setBackground(COLOR_CARD);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_SECONDARY, 1, true),
                new EmptyBorder(35, 40, 35, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12); // Breathing room between rows
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Segoe UI", Font.BOLD, 13);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);

        // Row 0: Title (Required)
        addFormLabel(cardPanel, "<html>Incident Title <font color='red'>*</font></html>", labelFont, gbc, 0, 0);
        titleField = createStyledTextField(fieldFont);
        gbc.gridx = 1; gbc.weightx = 1.0;
        cardPanel.add(titleField, gbc);

        // Row 1: Category
        addFormLabel(cardPanel, "Category", labelFont, gbc, 0, 1);
        String[] categories = {"Malware", "Phishing", "DDoS Attack", "Unauthorized Access", "Data Breach", "Ransomware", "Social Engineering", "Other"};
        categoryField = createStyledComboBox(categories, fieldFont);
        gbc.gridx = 1;
        cardPanel.add(categoryField, gbc);

        // Row 2: Severity
        addFormLabel(cardPanel, "Severity", labelFont, gbc, 0, 2);
        String[] severities = {"Low", "Medium", "High", "Critical"};
        severityBox = createStyledComboBox(severities, fieldFont);
        gbc.gridx = 1;
        cardPanel.add(severityBox, gbc);

        // Row 3: Priority
        addFormLabel(cardPanel, "Priority", labelFont, gbc, 0, 3);
        String[] priorities = {"Low", "Medium", "High", "Urgent"};
        priorityBox = createStyledComboBox(priorities, fieldFont);
        gbc.gridx = 1;
        cardPanel.add(priorityBox, gbc);

        // Row 4: Affected System (Required)
        addFormLabel(cardPanel, "<html>Affected System <font color='red'>*</font></html>", labelFont, gbc, 0, 4);
        systemField = createStyledTextField(fieldFont);
        gbc.gridx = 1;
        cardPanel.add(systemField, gbc);

        // Row 5: Reported By
        addFormLabel(cardPanel, "Reported By", labelFont, gbc, 0, 5);
        reportedByField = createStyledTextField(fieldFont);
        gbc.gridx = 1;
        cardPanel.add(reportedByField, gbc);

        // Row 6: Assigned To
        addFormLabel(cardPanel, "Assigned To", labelFont, gbc, 0, 6);
        assignedToField = createStyledTextField(fieldFont);
        gbc.gridx = 1;
        cardPanel.add(assignedToField, gbc);

        // Row 7: Estimated Damage (PKR Custom Formatting)
        addFormLabel(cardPanel, "Estimated Damage (Rs.)", labelFont, gbc, 0, 7);
        damageSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 50000000, 5000));
        damageSpinner.setFont(fieldFont);
        damageSpinner.setPreferredSize(new Dimension(100, 38)); 
        damageSpinner.setBorder(new LineBorder(COLOR_SECONDARY, 1, true));
        
        JComponent editor = damageSpinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField spinnerField = ((JSpinner.DefaultEditor) editor).getTextField();
            spinnerField.setBackground(Color.WHITE);
            spinnerField.setForeground(COLOR_TEXT_MAIN);
            spinnerField.setBorder(new EmptyBorder(0, 10, 0, 10));
        }
        gbc.gridx = 1; gbc.weightx = 1.0;
        cardPanel.add(damageSpinner, gbc);

        // Row 8: Description (Required & Top-aligned label anchor fix)
        addFormLabel(cardPanel, "<html>Description <font color='red'>*</font></html>", labelFont, gbc, 0, 8);
        descriptionArea = new JTextArea(5, 25);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(fieldFont);
        descriptionArea.setForeground(COLOR_TEXT_MAIN);
        descriptionArea.setBorder(new EmptyBorder(10, 12, 10, 12));
        
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        descriptionScroll.setBorder(new LineBorder(COLOR_SECONDARY, 1, true));
        gbc.gridx = 1; gbc.weightx = 1.0;
        cardPanel.add(descriptionScroll, gbc);

        // Wrapper keeping the card crisp and centered via a Scroll Pane viewport guard
        JPanel marginWrapper = new JPanel(new BorderLayout());
        marginWrapper.setBackground(COLOR_BG);
        marginWrapper.setBorder(new EmptyBorder(0, 45, 10, 45));
        marginWrapper.add(cardPanel, BorderLayout.CENTER);

        JScrollPane formScroll = new JScrollPane(marginWrapper);
        formScroll.setBorder(null);
        formScroll.setBackground(COLOR_BG);
        formScroll.getViewport().setBackground(COLOR_BG);
        formScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(formScroll, BorderLayout.CENTER);

        // ==========================================
        // ACTION BUTTONS FOOTER
        // ==========================================
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        footerPanel.setBackground(COLOR_BG);
        footerPanel.setBorder(new EmptyBorder(15, 45, 30, 45));

        backButton = createStyledButton("Back", false);
        clearButton = createStyledButton("Clear Form", false);
        submitButton = createStyledButton("Submit Incident", true);

        submitButton.addActionListener(e -> saveIncident());
        clearButton.addActionListener(e -> clearForm());
        backButton.addActionListener(e -> {
            dispose();
            new Dashboard();
        });

        footerPanel.add(backButton);
        footerPanel.add(clearButton);
        footerPanel.add(submitButton);
        add(footerPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void addFormLabel(JPanel panel, String text, Font font, GridBagConstraints gbc, int x, int y) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(COLOR_TEXT_MAIN); 
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.weightx = 0.0;
        
        if (text.contains("Description")) {
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.insets = new Insets(20, 12, 12, 12); 
        } else {
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(12, 12, 12, 12);
        }
        panel.add(label, gbc);
    }

    private JTextField createStyledTextField(Font font) {
        JTextField tf = new JTextField();
        tf.setFont(font);
        tf.setPreferredSize(new Dimension(100, 38)); 
        tf.setForeground(COLOR_TEXT_MAIN);
        tf.setBackground(Color.WHITE);
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_SECONDARY, 1, true),
                new EmptyBorder(0, 12, 0, 12) 
        ));
        return tf;
    }

    private JComboBox<String> createStyledComboBox(String[] items, Font font) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(font);
        cb.setPreferredSize(new Dimension(100, 38));
        cb.setBackground(Color.WHITE);
        cb.setForeground(COLOR_TEXT_MAIN);
        cb.setBorder(new LineBorder(COLOR_SECONDARY, 1, true));
        cb.setFocusable(false);
        return cb;
    }

    private JButton createStyledButton(String text, boolean isPrimary) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(150, 42));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);

        if (isPrimary) {
            button.setBackground(COLOR_PRIMARY);
            button.setForeground(Color.WHITE);
            
            button.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { button.setBackground(COLOR_PRIMARY_HOVER); }
                @Override public void mouseExited(MouseEvent e) { button.setBackground(COLOR_PRIMARY); }
            });
        } else {
            button.setBackground(Color.WHITE);
            button.setForeground(COLOR_PRIMARY);
            button.setBorder(new LineBorder(COLOR_SECONDARY, 1, true));
            button.setBorderPainted(true);
            
            button.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { button.setBackground(COLOR_HOVER_LIGHT); }
                @Override public void mouseExited(MouseEvent e) { button.setBackground(Color.WHITE); }
            });
        }
        return button;
    }

    private boolean validateForm() {
        if (titleField.getText().trim().isEmpty()) {
            showValidationError("Enter an incident title.");
            return false;
        }
        if (systemField.getText().trim().isEmpty()) {
            showValidationError("Specify the affected system.");
            return false;
        }
        if (descriptionArea.getText().trim().isEmpty()) {
            showValidationError("Please supply an incident description.");
            return false;
        }
        return true;
    }

    private void showValidationError(String message) {
        UIManager.put("OptionPane.messageFont", new Font("Segoe UI", Font.PLAIN, 14));
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.WARNING_MESSAGE);
    }

    private void saveIncident() {
        if (!validateForm()) return;

        try {
            MongoDatabase db = MongoDBConnection.connect();
            MongoCollection<Document> collection = db.getCollection("incidents");

            Document incident = new Document()
                    .append("title", titleField.getText().trim())
                    .append("category", categoryField.getSelectedItem())
                    .append("severity", severityBox.getSelectedItem())
                    .append("priority", priorityBox.getSelectedItem())
                    .append("status", "Open")
                    .append("affectedSystem", systemField.getText().trim())
                    .append("description", descriptionArea.getText().trim())
                    .append("reportedBy", reportedByField.getText().trim())
                    .append("assignedTo", assignedToField.getText().trim())
                    .append("createdDate", LocalDateTime.now().toString())
                    .append("resolvedDate", null)
                    .append("estimatedDamage", damageSpinner.getValue());

            collection.insertOne(incident);
            
            UIManager.put("OptionPane.messageFont", new Font("Segoe UI", Font.PLAIN, 14));
            JOptionPane.showMessageDialog(this, "Incident submitted successfully to system registry.", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving incident:\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        titleField.setText("");
        categoryField.setSelectedIndex(0);
        severityBox.setSelectedIndex(0);
        priorityBox.setSelectedIndex(0);
        systemField.setText("");
        descriptionArea.setText("");
        reportedByField.setText("");
        assignedToField.setText("");
        damageSpinner.setValue(0);
    }
}