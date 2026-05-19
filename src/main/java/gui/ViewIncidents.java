package gui;

import database.MongoDBConnection;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ViewIncidents extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;

    private JButton deleteButton;
    private JButton updateButton;
    private JButton editButton;
    private JButton searchButton;
    private JButton refreshButton;
    private JButton backButton;

    private JComboBox<String> statusBox;
    private JComboBox<String> filterBox;
    private JTextField searchField;

    // High-density, professional typography
    private final Font mainFont = new Font("Segoe UI", Font.PLAIN, 12);
    private final Font boldFont = new Font("Segoe UI", Font.BOLD, 12);

    public ViewIncidents() {
        setTitle("Manage Cybersecurity Incidents");
        setSize(1200, 650); // Clean, standard desktop frame size
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Root panel layout with structured padding
        JPanel rootPanel = new JPanel(new BorderLayout(8, 8));
        rootPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        rootPanel.setBackground(Color.WHITE);
        setContentPane(rootPanel);

        // --- TOP PANEL (Search & Filter) ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        topPanel.setBackground(Color.WHITE);
        
        TitledBorder topBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(225, 225, 225), 1), "Search & Filter Options");
        topBorder.setTitleFont(boldFont);
        topPanel.setBorder(topBorder);

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(mainFont);
        topPanel.add(searchLabel);

        searchField = new JTextField(18);
        searchField.setFont(mainFont);
        searchField.setPreferredSize(new Dimension(180, 26)); // Compact height
        topPanel.add(searchField);

        searchButton = new JButton("Search");
        searchButton.setFont(mainFont);
        searchButton.setPreferredSize(new Dimension(80, 26));
        searchButton.addActionListener(e -> searchIncidents());
        topPanel.add(searchButton);

        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
        sep.setPreferredSize(new Dimension(2, 16));
        topPanel.add(sep);

        JLabel filterLabel = new JLabel("Status:");
        filterLabel.setFont(mainFont);
        topPanel.add(filterLabel);

        filterBox = new JComboBox<>(new String[]{"All", "Open", "Investigating", "Resolved"});
        filterBox.setFont(mainFont);
        filterBox.setPreferredSize(new Dimension(110, 26));
        filterBox.addActionListener(e -> loadIncidents());
        topPanel.add(filterBox);

        refreshButton = new JButton("Clear & Refresh");
        refreshButton.setFont(mainFont);
        refreshButton.setPreferredSize(new Dimension(125, 26));
        refreshButton.addActionListener(e -> resetAndRefresh());
        topPanel.add(refreshButton);

        rootPanel.add(topPanel, BorderLayout.NORTH);

        // --- TABLE ---
        String[] columns = {
                "ObjectId", "ID", "Title", "Category", "Severity",
                "Priority", "Status", "Affected System",
                "Reported By", "Assigned To", "Created Date"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
                }
                return c;
            }
        };

        table.setFont(mainFont);
        table.setRowHeight(24); // High-density layout row constraint
        table.setSelectionBackground(new Color(45, 45, 45));
        table.setSelectionForeground(Color.WHITE);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(235, 235, 235));

        // Flat dark header styling
    JTableHeader header = table.getTableHeader();
    header.setFont(boldFont);
    header.setPreferredSize(new Dimension(header.getWidth(), 28));
    header.setDefaultRenderer(new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            // CRITICAL FIXES FOR SOLID BLACK:
            label.setOpaque(true);               // Forces Swing to paint the background color fully
            label.setBackground(Color.BLACK);    // Changes background to pure black
            label.setForeground(Color.WHITE);    // Changes text color to stark white
            
            label.setHorizontalAlignment(JLabel.CENTER);
            
            // Subtle dark gray grid separator between headers so they look organized
            label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(50, 50, 50))); 
            return label;
        }
    });

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // Hide structural identity data field
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(0);

        // Precise field spacing column matrix
        table.getColumnModel().getColumn(1).setPreferredWidth(80);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);
        table.getColumnModel().getColumn(3).setPreferredWidth(130);
        table.getColumnModel().getColumn(4).setPreferredWidth(95);
        table.getColumnModel().getColumn(5).setPreferredWidth(95);
        table.getColumnModel().getColumn(6).setPreferredWidth(110);
        table.getColumnModel().getColumn(7).setPreferredWidth(150);
        table.getColumnModel().getColumn(8).setPreferredWidth(135);
        table.getColumnModel().getColumn(9).setPreferredWidth(135);
        table.getColumnModel().getColumn(10).setPreferredWidth(160);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(225, 228, 230)));
        rootPanel.add(scrollPane, BorderLayout.CENTER);

        // --- BOTTOM PANEL (Actions Panel) ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 4));
        bottomPanel.setBackground(Color.WHITE);
        
        TitledBorder bottomBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1), "Actions");
        bottomBorder.setTitleFont(boldFont);
        bottomPanel.setBorder(bottomBorder);

        statusBox = new JComboBox<>(new String[]{"Open", "Investigating", "Resolved"});
        statusBox.setFont(mainFont);
        statusBox.setPreferredSize(new Dimension(120, 26));
        bottomPanel.add(statusBox);

        updateButton = new JButton("Update Status");
        updateButton.setFont(mainFont);
        updateButton.setPreferredSize(new Dimension(115, 26));
        updateButton.addActionListener(e -> updateIncidentStatus());
        bottomPanel.add(updateButton);

        editButton = new JButton("Edit");
        editButton.setFont(mainFont);
        editButton.setPreferredSize(new Dimension(90, 26));
        editButton.addActionListener(e -> editIncident());
        bottomPanel.add(editButton);

        deleteButton = new JButton("Delete");
        deleteButton.setFont(mainFont);
        deleteButton.setPreferredSize(new Dimension(90, 26));
        deleteButton.addActionListener(e -> deleteIncident());
        bottomPanel.add(deleteButton);

        backButton = new JButton("Back");
        backButton.setFont(mainFont);
        backButton.setPreferredSize(new Dimension(90, 26));
        backButton.addActionListener(e -> {
            dispose();
            new Dashboard();
        });
        bottomPanel.add(backButton);

        rootPanel.add(bottomPanel, BorderLayout.SOUTH);

        loadIncidents();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                new Dashboard();
            }
        });

        setVisible(true);
    }

    // LOAD DATAMODEL FROM MONGO
    private void loadIncidents() {
        model.setRowCount(0);
        try {
            MongoDatabase db = MongoDBConnection.connect();
            MongoCollection<Document> collection = db.getCollection("incidents");

            FindIterable<Document> incidents;
            String filter = (String) filterBox.getSelectedItem();

            if (filter == null || "All".equals(filter)) {
                incidents = collection.find();
            } else {
                incidents = collection.find(Filters.eq("status", filter));
            }

            for (Document doc : incidents) {
                Object rawId = doc.get("_id");
                String idStr = (rawId instanceof ObjectId) ? ((ObjectId) rawId).toString() : String.valueOf(rawId);
                String shortId = idStr.length() >= 8 ? idStr.substring(0, 8) : idStr;

                model.addRow(new Object[]{
                        idStr,
                        shortId,
                        doc.getOrDefault("title", ""),
                        doc.getOrDefault("category", ""),
                        doc.getOrDefault("severity", ""),
                        doc.getOrDefault("priority", ""),
                        doc.getOrDefault("status", ""),
                        doc.getOrDefault("affectedSystem", ""),
                        doc.getOrDefault("reportedBy", ""),
                        doc.getOrDefault("assignedTo", ""),
                        doc.getOrDefault("createdDate", "")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error fetching records: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // DYNAMIC FILTER SEARCH FUNCTIONALITY
    private void searchIncidents() {
        String key = searchField.getText().trim();
        if (key.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            // Evaluates text case-insensitively across all columns instantly
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + key));
        }
    }

    // SANITIZE FILTERS AND RELOAD
    private void resetAndRefresh() {
        searchField.setText("");
        sorter.setRowFilter(null);
        loadIncidents();
    }

    // DELETE HANDLER 
    private void deleteIncident() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        int modelRow = table.convertRowIndexToModel(row);
        String id = model.getValueAt(modelRow, 0).toString();

        try {
            MongoDatabase db = MongoDBConnection.connect();
            db.getCollection("incidents").deleteOne(Filters.eq("_id", new ObjectId(id)));
            model.removeRow(modelRow);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Execution Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // UPDATE STATUS HANDLER
    private void updateIncidentStatus() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        int modelRow = table.convertRowIndexToModel(row);
        String id = model.getValueAt(modelRow, 0).toString();
        String status = statusBox.getSelectedItem().toString();

        try {
            MongoDatabase db = MongoDBConnection.connect();
            db.getCollection("incidents").updateOne(
                    Filters.eq("_id", new ObjectId(id)),
                    new Document("$set", new Document("status", status))
            );

            model.setValueAt(status, modelRow, 6);
            
            // Clean table view state contextually if updated out of a restrictive status filter box view
            String activeFilter = (String) filterBox.getSelectedItem();
            if (activeFilter != null && !"All".equals(activeFilter) && !activeFilter.equals(status)) {
                loadIncidents(); 
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Execution Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // EDIT ROUTER
    private void editIncident() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        int modelRow = table.convertRowIndexToModel(row);
        String id = model.getValueAt(modelRow, 0).toString();

        new EditIncidentDialog(this, id);
    }

    public void refreshTable() {
        loadIncidents();
    }
}