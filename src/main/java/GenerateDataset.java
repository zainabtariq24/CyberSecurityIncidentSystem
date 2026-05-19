import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import database.MongoDBConnection;

import org.bson.Document;

import java.time.LocalDateTime;
import java.util.Random;

public class GenerateDataset {

    public static void main(String[] args) throws Exception {

        MongoDatabase db =
                MongoDBConnection.connect();

        MongoCollection<Document> collection =
                db.getCollection("incidents");

        // DELETE OLD DATASET
        collection.deleteMany(new Document());

        String[] titles = {
                "Phishing Attempt",
                "Malware Infection",
                "DDoS Attack",
                "Unauthorized Access",
                "SQL Injection",
                "Ransomware Detection",
                "Suspicious Login",
                "Data Breach",
                "Firewall Alert",
                "Password Attack"
        };

        String[] categories = {
                "Network Security",
                "Email Security",
                "Database Security",
                "Web Security",
                "Endpoint Security"
        };

        String[] severities = {
                "Low",
                "Medium",
                "High",
                "Critical"
        };

        String[] priorities = {
                "Low",
                "Medium",
                "High",
                "Urgent"
        };

        String[] statuses = {
                "Open",
                "Investigating",
                "Resolved"
        };

        String[] systems = {
                "Employee Mail",
                "Finance Server",
                "HR Database",
                "Main Website",
                "Cloud Infrastructure",
                "Authentication Server",
                "Customer Portal"
        };

        String[] descriptions = {
                "Suspicious activity detected.",
                "Potential malicious behavior identified.",
                "Security policy violation occurred.",
                "Unauthorized attempt blocked.",
                "Critical vulnerability detected."
        };

        String[] reporters = {
                "SOC Team",
                "System Administrator",
                "Security Analyst",
                "Help Desk",
                "Automated Detection System"
        };

        String[] assignees = {
                "Areesha Tahir",
                "Zainab Tariq",
                "Hadia Imran",
                "Qaiser Farooq",
                "Awais Khan",
                "Ali Raza",
                "Sara Ahmed",
                "Bilal Hussain",
                "Nadia Ali",
                "Omar Siddiqui"
        };

        Random random =
                new Random();

        for (int i = 1; i <= 1000; i++) {

            Document incident =
                    new Document();

            incident.append(
                    "title",
                    titles[random.nextInt(
                            titles.length
                    )]
            );

            incident.append(
                    "category",
                    categories[random.nextInt(
                            categories.length
                    )]
            );

            incident.append(
                    "severity",
                    severities[random.nextInt(
                            severities.length
                    )]
            );

            incident.append(
                    "priority",
                    priorities[random.nextInt(
                            priorities.length
                    )]
            );

            incident.append(
                    "status",
                    statuses[random.nextInt(
                            statuses.length
                    )]
            );

            incident.append(
                    "affectedSystem",
                    systems[random.nextInt(
                            systems.length
                    )]
            );

            incident.append(
                    "description",
                    descriptions[random.nextInt(
                            descriptions.length
                    )]
            );

            incident.append(
                    "reportedBy",
                    reporters[random.nextInt(
                            reporters.length
                    )]
            );

            incident.append(
                    "assignedTo",
                    assignees[random.nextInt(
                            assignees.length
                    )]
            );

            incident.append(
                    "createdDate",
                    LocalDateTime.now()
                            .minusDays(
                                    random.nextInt(365)
                            )
                            .toString()
            );

            incident.append(
                    "resolvedDate",
                    random.nextBoolean()
                            ? LocalDateTime.now()
                            .minusDays(
                                    random.nextInt(100)
                            )
                            .toString()
                            : null
            );

            incident.append(
                    "estimatedDamage",
                    random.nextInt(1000000)
            );

            collection.insertOne(
                    incident
            );

            System.out.println(
                    "Inserted Incident "
                            + i
            );
        }

        System.out.println(
                "\n1000 incidents inserted successfully!"
        );
    }
}