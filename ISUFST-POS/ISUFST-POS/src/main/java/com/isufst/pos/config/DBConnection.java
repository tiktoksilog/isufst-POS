package com.isufst.pos.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Provides JDBC connections to the MySQL database.
 *
 * Change USER and PASSWORD below to match your MySQL Workbench / MySQL Server login.
 *
 */
public final class DBConnection {

    private static final String HOST = "localhost";
    private static final int PORT = 3306;
    private static final String DATABASE = "isufst_pos_db";
    private static final String USER = "root";
    private static final String PASSWORD = "12345678";   // <-- put your MySQL root password here

    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Manila";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(
                    "MySQL JDBC driver not found. Add mysql-connector-j to the project dependencies.");
        }
    }

    private DBConnection() {
    }

    /**
     * Opens a new connection. Always close it (use try-with-resources).
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Quick check used at startup so the user gets a clear message if MySQL is not running.
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn.isValid(3);
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            return false;
        }
    }
}
