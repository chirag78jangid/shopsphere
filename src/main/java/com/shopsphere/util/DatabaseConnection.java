package com.shopsphere.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {

    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/shopsphere?serverTimezone=UTC";

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        String url = environmentValue("SHOPSPHERE_DB_URL", DEFAULT_URL);
        String username = environmentValue("SHOPSPHERE_DB_USER", null);
        String password = environmentValue("SHOPSPHERE_DB_PASSWORD", null);

        if (username == null || username.isBlank()) {
            throw new SQLException("SHOPSPHERE_DB_USER must be configured.");
        }

        if (password == null) {
            throw new SQLException("SHOPSPHERE_DB_PASSWORD must be configured.");
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC driver not found.", e);
        }

        return DriverManager.getConnection(url, username, password);
    }

    private static String environmentValue(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null ? defaultValue : value;
    }
}