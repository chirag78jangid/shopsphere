package com.shopsphere.model;

import java.time.LocalDateTime;

public class User {
    private final long id;
    private final String fullName;
    private final String email;
    private final String passwordHash;
    private final String phone;
    private final String role;
    private final LocalDateTime createdAt;

    public User(long id, String fullName, String email, String passwordHash,
                String phone, String role, LocalDateTime createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.phone = phone;
        this.role = role;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getPhone() {
        return phone;
    }

    public String getRole() {
        return role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}