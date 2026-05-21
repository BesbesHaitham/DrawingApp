package com.drawingapp.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DrawingDAO implements ActionLogRepository {
    private static final String DEFAULT_DB_URL =
            "jdbc:mysql://localhost:3306/drawing_app?createDatabaseIfNotExist=true&serverTimezone=UTC";
    private static final String DEFAULT_DB_USER = "root";
    private static final String DEFAULT_DB_PASSWORD = "";

    private final String dbUrl;
    private final String dbUser;
    private final String dbPassword;

    public DrawingDAO() {
        this.dbUrl = getConfig("DRAWINGAPP_DB_URL", DEFAULT_DB_URL);
        this.dbUser = getConfig("DRAWINGAPP_DB_USER", DEFAULT_DB_USER);
        this.dbPassword = getConfig("DRAWINGAPP_DB_PASSWORD", DEFAULT_DB_PASSWORD);
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection conn = openConnection();
             Statement stmt = conn.createStatement()) {

            String createDrawingsTable = "CREATE TABLE IF NOT EXISTS drawings (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "name VARCHAR(255) NOT NULL," +
                    "data TEXT NOT NULL," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            stmt.execute(createDrawingsTable);

            String createLogsTable = "CREATE TABLE IF NOT EXISTS logs (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "action TEXT NOT NULL," +
                    "timestamp VARCHAR(32)," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            stmt.execute(createLogsTable);
        } catch (SQLException e) {
            System.err.println("Erreur initialisation MySQL: " + e.getMessage());
        }
    }

    public int saveDrawing(String name, String data) {
        try (Connection conn = openConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO drawings (name, data) VALUES (?, ?)")) {

            pstmt.setString(1, name);
            pstmt.setString(2, data);
            pstmt.executeUpdate();

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT LAST_INSERT_ID()")) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

            return -1;
        } catch (SQLException e) {
            System.err.println("Erreur sauvegarde dessin MySQL: " + e.getMessage());
            return -1;
        }
    }

    public String getDrawing(int id) {
        try (Connection conn = openConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT data FROM drawings WHERE id = ?")) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("data");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lecture dessin MySQL: " + e.getMessage());
        }
        return null;
    }

    public boolean saveLog(String action, String timestamp) {
        try (Connection conn = openConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO logs (action, timestamp) VALUES (?, ?)")) {

            pstmt.setString(1, action);
            pstmt.setString(2, timestamp);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Erreur enregistrement log MySQL: " + e.getMessage());
            return false;
        }
    }

    public void getAllLogs() {
        try (Connection conn = openConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT * FROM logs ORDER BY created_at DESC, id DESC")) {

            while (rs.next()) {
                System.out.println("[" + rs.getString("timestamp") + "] " + rs.getString("action"));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lecture logs MySQL: " + e.getMessage());
        }
    }

    private Connection openConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL introuvable.", e);
        }
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    private String getConfig(String key, String defaultValue) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return value;
    }
}
