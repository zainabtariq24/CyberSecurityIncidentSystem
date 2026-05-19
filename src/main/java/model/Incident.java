package model;

public class Incident {

    private String title;
    private String category;
    private String severity;
    private String status;
    private String affectedSystem;
    private String description;

    public Incident(String title,
                    String category,
                    String severity,
                    String status,
                    String affectedSystem,
                    String description) {

        this.title = title;
        this.category = category;
        this.severity = severity;
        this.status = status;
        this.affectedSystem = affectedSystem;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getSeverity() {
        return severity;
    }

    public String getStatus() {
        return status;
    }

    public String getAffectedSystem() {
        return affectedSystem;
    }

    public String getDescription() {
        return description;
    }
}