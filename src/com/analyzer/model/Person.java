package com.analyzer.model;

import java.io.Serializable;

/**
 * VIVA EXPLANATION - OOP CONCEPT: ABSTRACTION & INHERITANCE
 * This is an abstract class representing a generic Person. 
 * 1. Abstraction: It cannot be instantiated directly using 'new Person()'. It only serves as a blueprint.
 * 2. Inheritance: Classes like Student will extend this class to inherit its properties (ID, Name, Email).
 * 3. Serializable: Implemented to allow saving/loading person objects to/from files.
 */
public abstract class Person implements Serializable {
    private static final long serialVersionUID = 1L;

    // Encapsulated fields (Private access modifier prevents direct external modification)
    private String id;
    private String name;
    private String email;

    /**
     * Parameterized Constructor to initialize base person attributes.
     */
    public Person(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    // --- ENCAPSULATION: Getters and Setters ---
    // These methods provide controlled access to private data.

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Name: " + name + ", Email: " + email;
    }
}
