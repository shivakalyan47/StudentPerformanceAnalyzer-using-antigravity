package com.analyzer.model;

import java.util.HashMap;
import java.util.Map;

/**
 * VIVA EXPLANATION - OOP CONCEPT: INHERITANCE, ENCAPSULATION & COLLECTIONS
 * 1. Inheritance: 'Student extends Person' inherits the id, name, and email fields and constructor logic.
 * 2. Encapsulation: All fields are private, accessible only via standard getter and setter methods.
 * 3. Collections Framework: Uses java.util.Map (HashMap implementation) to store dynamic subject-marks pairs.
 */
public class Student extends Person {
    private static final long serialVersionUID = 1L;

    // Academic scores & records
    private double attendance;      // Percentage (0 - 100)
    private double assignmentScore; // Score (0 - 100)
    private Map<String, Double> subjectMarks; // Subject Name -> Marks

    // Calculated Analytical attributes
    private double totalMarks;
    private double averageMarks;
    private String grade;
    private String performanceCategory;
    private String suggestions;

    /**
     * Parameterized Constructor. Passes basic student details to the super (Person) class.
     */
    public Student(String id, String name, String email) {
        super(id, name, email); // Invokes the constructor of the parent class 'Person'
        this.subjectMarks = new HashMap<>();
        this.attendance = 0.0;
        this.assignmentScore = 0.0;
        this.totalMarks = 0.0;
        this.averageMarks = 0.0;
        this.grade = "F";
        this.performanceCategory = "Poor";
        this.suggestions = "No data analyzed yet.";
    }

    // --- Core Operations & Calculations ---

    /**
     * Adds or updates a mark for a specific subject.
     */
    public void setMark(String subject, double mark) {
        subjectMarks.put(subject, mark);
    }

    /**
     * Gets a mark for a specific subject, returns 0.0 if not found.
     */
    public double getMark(String subject) {
        return subjectMarks.getOrDefault(subject, 0.0);
    }

    // --- ENCAPSULATION: Getters and Setters ---

    public double getAttendance() {
        return attendance;
    }

    public void setAttendance(double attendance) {
        this.attendance = attendance;
    }

    public double getAssignmentScore() {
        return assignmentScore;
    }

    public void setAssignmentScore(double assignmentScore) {
        this.assignmentScore = assignmentScore;
    }

    public Map<String, Double> getSubjectMarks() {
        return subjectMarks;
    }

    public void setSubjectMarks(Map<String, Double> subjectMarks) {
        this.subjectMarks = subjectMarks;
    }

    public double getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(double totalMarks) {
        this.totalMarks = totalMarks;
    }

    public double getAverageMarks() {
        return averageMarks;
    }

    public void setAverageMarks(double averageMarks) {
        this.averageMarks = averageMarks;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getPerformanceCategory() {
        return performanceCategory;
    }

    public void setPerformanceCategory(String performanceCategory) {
        this.performanceCategory = performanceCategory;
    }

    public String getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(String suggestions) {
        this.suggestions = suggestions;
    }

    /**
     * Overriding toString() to display Student Details cleanly (Polymorphism).
     */
    @Override
    public String toString() {
        return super.toString() + " | Avg: " + String.format("%.2f", averageMarks) + 
               " | Grade: " + grade + " | Category: " + performanceCategory;
    }
}
