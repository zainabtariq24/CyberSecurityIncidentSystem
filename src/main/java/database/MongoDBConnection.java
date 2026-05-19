package database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.MongoClientSettings;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * MongoDB Connection Manager
 * Handles database connectivity with connection pooling and error handling.
 * Reads credentials from config.properties at the project root.
 */
public class MongoDBConnection {

    private static final String CONFIG_FILE = "config.properties";
    private static final Properties CONFIG = loadConfig();

    private static final String URI = requireProp("mongodb.uri");
    private static final String DATABASE_NAME =
            CONFIG.getProperty("mongodb.database", "cyber_incident_db");

    private static Properties loadConfig() {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            props.load(fis);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Could not load " + CONFIG_FILE +
                    ". Copy config.properties.example to config.properties and fill in your credentials.",
                    e);
        }
        return props;
    }

    private static String requireProp(String key) {
        String value = CONFIG.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Missing required property '" + key + "' in " + CONFIG_FILE);
        }
        return value;
    }
    
    private static MongoClient mongoClient = null;
    
    /**
     * Establishes and returns a MongoDB database connection
     * Uses connection pooling to optimize resource usage
     * @return MongoDatabase instance
     * @throws Exception if connection fails
     */
    public static synchronized MongoDatabase connect() throws Exception {
        try {
            if (mongoClient == null) {
                MongoClientSettings settings = MongoClientSettings.builder()
                        .applyConnectionString(new com.mongodb.ConnectionString(URI))
                        .applyToConnectionPoolSettings(builder ->
                                builder.maxConnectionIdleTime(30, TimeUnit.SECONDS)
                                        .maxSize(10)
                                        .minSize(2))
                        .build();
                
                mongoClient = MongoClients.create(settings);
            }
            
            return mongoClient.getDatabase(DATABASE_NAME);
            
        } catch (Exception e) {
            throw new Exception("Failed to connect to MongoDB: " + e.getMessage(), e);
        }
    }
    
    /**
     * Closes the MongoDB connection
     */
    public static synchronized void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
        }
    }
    
    /**
     * Tests the database connection
     * @return true if connection is successful
     */
    public static boolean testConnection() {
        try {
            MongoDatabase db = connect();
            db.listCollectionNames().first();
            return true;
        } catch (Exception e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }
}