package com.analyzer.service;

import com.analyzer.model.User;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * VIVA EXPLANATION - OOP CONCEPT: FILE HANDLING & ENCAPSULATION
 * This service handles security and authentication.
 * 1. Persistent Storage: It saves all credentials in a binary file 'data/users.dat' using ObjectOutputStream.
 * 2. Self-Initialization: If the database doesn't exist, it automatically creates default accounts (e.g. 'admin' and 'advisor')
 *    so the application is runnable immediately with no setup.
 * 3. Encapsulation: Provides a controlled login verification routine.
 */
public class AuthService {
    private static final String DATA_DIR = "data";
    private static final String FILE_PATH = DATA_DIR + "/users.dat";
    
    private Map<String, User> users;
    private User currentUser;

    public AuthService() {
        this.users = new HashMap<>();
        this.currentUser = null;
        ensureDataDirectoryExists();
        loadUsers();
        if (users.isEmpty()) {
            createDefaultUsers();
        }
    }

    /**
     * Ensures that the 'data' directory exists in the working directory.
     */
    private void ensureDataDirectoryExists() {
        File directory = new File(DATA_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    /**
     * Creates default users if the binary storage is empty or does not exist.
     */
    private void createDefaultUsers() {
        // Pre-populating default credentials for examiner convenience
        registerUser(new User("admin", "admin123", "System Administrator", "Administrator"));
        registerUser(new User("advisor", "advisor123", "Dr. Jane Smith", "Academic Advisor"));
        registerUser(new User("registrar", "reg123", "Prof. John Doe", "Registrar"));
    }

    /**
     * Registers a new user and saves to file.
     */
    public synchronized boolean registerUser(User user) {
        if (users.containsKey(user.getUsername())) {
            return false; // User already exists
        }
        users.put(user.getUsername(), user);
        saveUsers();
        return true;
    }

    /**
     * Verifies the login credentials.
     */
    public synchronized boolean authenticate(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            return true;
        }
        return false;
    }

    /**
     * Logs out the current user.
     */
    public void logout() {
        currentUser = null;
    }

    /**
     * Gets the currently logged-in user.
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Binary File Handling: Saves the map of users to users.dat.
     */
    private synchronized void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(users);
        } catch (IOException e) {
            System.err.println("⚠️ Warning: Could not save user database: " + e.getMessage());
        }
    }

    /**
     * Binary File Handling: Loads the map of users from users.dat.
     */
    @SuppressWarnings("unchecked")
    private synchronized void loadUsers() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            users = (Map<String, User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("⚠️ Warning: Could not load user database. Reinitializing new file. " + e.getMessage());
            users = new HashMap<>();
        }
    }
}
