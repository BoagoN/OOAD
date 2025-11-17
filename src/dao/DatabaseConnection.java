package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Updated for MySQL Connector/J 9.5.0
    private static final String URL = "jdbc:mysql://localhost:3306/banking system?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    private static Connection connection;

    static {
        try {
            // For MySQL Connector/J 9.x, the driver class is different
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ MySQL JDBC Driver loaded successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL JDBC Driver not found: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                System.out.println("🔗 Attempting to connect to MySQL...");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Successfully connected to MySQL database");
            } catch (SQLException e) {
                System.err.println("❌ Database connection failed: " + e.getMessage());
                System.err.println("URL: " + URL);
                System.err.println("User: " + USER);

                // More detailed troubleshooting
                if (e.getMessage().contains("Unknown database")) {
                    System.err.println("💡 The database 'banking_system' doesn't exist or has a different name");
                }
                throw e;
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed");
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    // Test connection
    public static void testConnection() {
        try {
            Connection conn = getConnection();
            System.out.println("✅ MySQL connection test: SUCCESS");
            conn.close();
        } catch (SQLException e) {
            System.out.println("❌ MySQL connection test: FAILED - " + e.getMessage());
        }
    }

    // Method to test basic connection without database
    public static void testServerConnection() {
        try {
            String testURL = "jdbc:mysql://localhost:3306/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
            Connection conn = DriverManager.getConnection(testURL, USER, PASSWORD);
            System.out.println("✅ MySQL SERVER connection test: SUCCESS");
            conn.close();
        } catch (SQLException e) {
            System.out.println("❌ MySQL SERVER connection test: FAILED - " + e.getMessage());
        }
    }
}