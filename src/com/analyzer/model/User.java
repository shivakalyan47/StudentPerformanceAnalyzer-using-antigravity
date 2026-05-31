package com.analyzer.model;

import java.io.Serializable;

/**
 * VIVA EXPLANATION - OOP CONCEPT: ENCAPSULATION
 * This class represents a System User (e.g., Advisor, Registrar, Admin) who can log in.
 * It encapsulates the username, password, and role, ensuring that passwords are treated securely
 * and roles are managed strictly through standard object controls.
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private String fullName;
    private String role; // e.g., "Academic Advisor", "Registrar", "Administrator"

    /**
     * Parameterized Constructor.
     */
    public User(String username, String password, String fullName, String role) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
    }

    // --- ENCAPSULATION: Getters and Setters ---

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
