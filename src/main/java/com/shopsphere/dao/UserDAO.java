package com.shopsphere.dao;

import com.shopsphere.model.User;
import com.shopsphere.util.DatabaseConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class UserDAO {
    private static final String FIND_BY_EMAIL = """
            SELECT id, full_name, email, password_hash, phone, role, created_at
            FROM users
            WHERE email = ?
            """;
    private static final String CREATE_USER = """
            INSERT INTO users (full_name, email, password_hash, phone)
            VALUES (?, ?, ?, ?)
            """;

    public User findByEmail(String email) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_EMAIL)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }

                Timestamp createdAt = resultSet.getTimestamp("created_at");
                LocalDateTime createdAtValue = createdAt == null ? null : createdAt.toLocalDateTime();
                return new User(
                        resultSet.getLong("id"),
                        resultSet.getString("full_name"),
                        resultSet.getString("email"),
                        resultSet.getString("password_hash"),
                        resultSet.getString("phone"),
                        resultSet.getString("role"),
                        createdAtValue);
            }
        }
    }

    public boolean createUser(String fullName, String email, String password, String phone)
            throws SQLException {
        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(CREATE_USER, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, fullName);
            statement.setString(2, email);
            statement.setString(3, passwordHash);
            statement.setString(4, phone);
            return statement.executeUpdate() == 1;
        }
    }
}