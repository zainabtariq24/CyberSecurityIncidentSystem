package gui;

import database.MongoDBConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class EditIncidentDialog extends JDialog {

    private JTextField titleField;
    private JComboBox<String> categoryBox;
    private JComboBox<String> severityBox;
    private JComboBox<String> priorityBox;
    private JComboBox<String> statusBox;
    private JTextField systemField;
    private JTextField reportedByField;
    private JTextField assignedToField;

    private JButton updateButton;
    private JButton cancelButton;

    private String incidentId;
    private Font labelFont;
    private Font inputFont;

    public EditIncidentDialog(JFrame parent, String id) {
        super(parent, "Edit Incident Details", true);
        this.incidentId = id;

        // Configuration
        setSize(520, 500);
        setLocationRelativeTo(parent);
        setResizable(false); // Prevents layout distortion

        // Modern flat colors and fonts
        Color bgColor = new Color(248, 249, 250); // Soft off-white
        Color primaryColor = new Color(33, 37, 41); // Dark charcoal
        Color secondaryColor = new Color(108, 117, 125); // Sleek gray
        
        labelFont = new Font("Segoe UI", Font.BOLD, 12);
        inputFont = new Font("Segoe UI", Font.PLAIN, 13);

        // Main content pane setup
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(bgColor);
        contentPanel.setBorder(new EmptyBorder(25, 25, 15, 25)); // Even margins
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); // Perfect spacing between elements
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Initialize UI Elements
        titleField = createTextField();
        categoryBox = createComboBox(new String[]{
                "Malware", "Phishing", "DDoS Attack", "Unauthorized Access",
                "SQL Injection", "Ransomware", "Suspicious Login", "Data Breach",
                "Firewall Alert", "Password Attack"
        });
        severityBox = createComboBox(new String[]{"Low", "Medium", "High", "Critical"});
        priorityBox = createComboBox(new String[]{"Low", "Medium", "High", "Urgent"});
        statusBox = createComboBox(new String[]{"Open", "Investigating", "Resolved"});
        systemField = createTextField();
        reportedByField = createTextField();
        assignedToField = createTextField();

        // Add fields to grid layout
        int row = 0;
        // Change this line in your constructor:
        addFieldRow(contentPanel, gbc, "<html>Incident Title <font color='red'>*</font></html>", titleField, row++);
        addFieldRow(contentPanel, gbc, "Category", categoryBox, row++);
        addFieldRow(contentPanel, gbc, "Severity", severityBox, row++);
        addFieldRow(contentPanel, gbc, "Priority", priorityBox, row++);
        addFieldRow(contentPanel, gbc, "Status", statusBox, row++);
        addFieldRow(contentPanel, gbc, "Affected System", systemField, row++);
        addFieldRow(contentPanel, gbc, "Reported By", reportedByField, row++);
        addFieldRow(contentPanel, gbc, "Assigned To", assignedToField, row++);

        // Separate Button Panel for cleaner alignment
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttonPanel.setBackground(bgColor);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        updateButton = createButton("Save Changes", primaryColor, Color.WHITE);
        cancelButton = createButton("Cancel", secondaryColor, Color.WHITE);

        buttonPanel.add(cancelButton);
        buttonPanel.add(updateButton);

        // Add panels to dialog
        setLayout(new BorderLayout());
        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load data from DB
        loadIncident();

        // Actions
        updateButton.addActionListener(e -> updateIncident());
        cancelButton.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void addFieldRow(JPanel panel, GridBagConstraints gbc, String labelText, Component component, int row) {
        JLabel label = new JLabel(labelText);
        label.setFont(labelFont);
        label.setForeground(new Color(73, 80, 87)); // Modern slate text color

        // Label constraints
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        panel.add(label, gbc);

        // Input field constraints
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(component, gbc);
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(inputFont);
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 30)); // Taller fields
        return field;
    }

    private JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> box = new JComboBox<>(items);
        box.setFont(inputFont);
        box.setBackground(Color.WHITE);
        box.setPreferredSize(new Dimension(box.getPreferredSize().width, 30));
        return box;
    }

    private JButton createButton(String text, Color bg, Color fg) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 35)); // Consistent button sizing
        return button;
    }

    private void loadIncident() {
        try {
            MongoDatabase db = MongoDBConnection.connect();
            MongoCollection<Document> col = db.getCollection("incidents");

            Document doc = col.find(Filters.eq("_id", new ObjectId(incidentId))).first();

            if (doc == null) {
                JOptionPane.showMessageDialog(this, "Incident not found!", "Error", JOptionPane.ERROR_MESSAGE);
                dispose();
                return;
            }

            titleField.setText(safe(doc, "title"));
            categoryBox.setSelectedItem(safe(doc, "category"));
            severityBox.setSelectedItem(safe(doc, "severity"));
            priorityBox.setSelectedItem(safe(doc, "priority"));
            statusBox.setSelectedItem(safe(doc, "status"));
            systemField.setText(safe(doc, "affectedSystem"));
            reportedByField.setText(safe(doc, "reportedBy"));
            assignedToField.setText(safe(doc, "assignedTo"));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Load error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateIncident() {
        if (titleField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Incident Title is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            MongoDatabase db = MongoDBConnection.connect();
            MongoCollection<Document> col = db.getCollection("incidents");

            col.updateOne(
                    Filters.eq("_id", new ObjectId(incidentId)),
                    new Document("$set",
                            new Document("title", titleField.getText().trim())
                                    .append("category", categoryBox.getSelectedItem())
                                    .append("severity", severityBox.getSelectedItem())
                                    .append("priority", priorityBox.getSelectedItem())
                                    .append("status", statusBox.getSelectedItem())
                                    .append("affectedSystem", systemField.getText().trim())
                                    .append("reportedBy", reportedByField.getText().trim())
                                    .append("assignedTo", assignedToField.getText().trim())
                    )
            );

            JOptionPane.showMessageDialog(this, "Incident updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Update error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String safe(Document doc, String key) {
        Object val = doc.get(key);
        return val == null ? "" : val.toString();
    }
}