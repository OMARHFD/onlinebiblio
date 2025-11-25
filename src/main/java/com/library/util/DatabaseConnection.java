package com.library.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnection {

    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());

    // ✅ Lire l'URL depuis les variables d'environnement (Kubernetes ou Docker Compose)
    // Si pas de variable d'environnement, utiliser localhost par défaut
    private static final String URL = System.getenv("DB_URL") != null
            ? System.getenv("DB_URL")
            : "jdbc:mysql://host.docker.internal:3306/online_library?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    // ✅ Lire les credentials depuis les variables d'environnement
    // Si pas de variable d'environnement, utiliser les valeurs par défaut
    private static final String USERNAME = System.getenv("DB_USERNAME") != null
            ? System.getenv("DB_USERNAME")
            : "tarik";

    private static final String PASSWORD = System.getenv("DB_PASSWORD") != null
            ? System.getenv("DB_PASSWORD")
            : "tarik123";

    // Connection for unit tests (mock)
    private static Connection testConnection = null;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            LOGGER.info("MySQL JDBC Driver loaded successfully");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        if (testConnection != null) {
            return testConnection; // return mock connection for testing
        }

        // Log connection info (sans le mot de passe pour la sécurité)
        LOGGER.info("Connecting to database: " + URL + " with user: " + USERNAME);

        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    public static void setTestConnection(Connection conn) {
        testConnection = conn; // inject mock connection for unit tests
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                LOGGER.info("Database connection closed successfully");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Failed to close database connection", e);
            }
        }
    }
}