package com.analyzer.service;

import com.analyzer.model.Student;
import java.io.*;
import java.util.*;

/**
 * VIVA EXPLANATION - OOP CONCEPT: COHESION, EXCEPTION HANDLING & FILE IO
 * 1. Single Responsibility Principle: This service handles only Student related business calculations,
 *    grade assignments, performance predictions, persistence, and reporting.
 * 2. File Handling: 
 *    - Binary I/O saves complete student structures inside 'data/students.dat' using serialization.
 *    - Plain Text I/O generates fully formatted, professional, readable reports in 'reports/' folder.
 * 3. Robust Logic: Generates multi-tiered warnings and recommendations based on attendance,
 *    assignment performance, and weak subject analysis.
 */
public class StudentService {
    private static final String DATA_DIR = "data";
    private static final String FILE_PATH = DATA_DIR + "/students.dat";
    private static final String REPORTS_DIR = "reports";

    // Core Subjects defined as static constants (Beginner-friendly & consistent)
    public static final String[] SUBJECTS = {
        "Mathematics", "Science", "English", "History", "Computer Science"
    };

    private Map<String, Student> students;

    public StudentService() {
        this.students = new HashMap<>();
        ensureDirectoriesExist();
        loadStudents();
    }

    /**
     * Helper to make sure directories for database and reports exist.
     */
    private void ensureDirectoriesExist() {
        new File(DATA_DIR).mkdirs();
        new File(REPORTS_DIR).mkdirs();
    }

    /**
     * Registers a student in the database.
     */
    public synchronized boolean registerStudent(Student s) {
        if (students.containsKey(s.getId())) {
            return false; // Student ID must be unique
        }
        students.put(s.getId(), s);
        calculateMetrics(s); // Run initial calculations
        saveStudents();
        return true;
    }

    /**
     * Updates details for an existing student.
     */
    public synchronized boolean updateStudentDetails(String id, String name, String email) {
        Student s = students.get(id);
        if (s == null) {
            return false;
        }
        s.setName(name);
        s.setEmail(email);
        saveStudents();
        return true;
    }

    /**
     * Records marks and attendance statistics, then triggers recalculations.
     */
    public synchronized boolean recordAcademicPerformance(
            String id, 
            Map<String, Double> marks, 
            double attendance, 
            double assignmentScore) {
        
        Student s = students.get(id);
        if (s == null) {
            return false;
        }

        s.setAttendance(attendance);
        s.setAssignmentScore(assignmentScore);
        for (String subject : SUBJECTS) {
            if (marks.containsKey(subject)) {
                s.setMark(subject, marks.get(subject));
            }
        }

        calculateMetrics(s);
        saveStudents();
        return true;
    }

    /**
     * Deletes a student from the database.
     */
    public synchronized boolean deleteStudent(String id) {
        if (students.remove(id) != null) {
            saveStudents();
            return true;
        }
        return false;
    }

    /**
     * Searches for a student by ID.
     */
    public Student getStudent(String id) {
        return students.get(id);
    }

    /**
     * Returns a read-only list of all registered students.
     */
    public Collection<Student> getAllStudents() {
        return Collections.unmodifiableCollection(students.values());
    }

    /**
     * Searches students dynamically matching a query in either ID or Name.
     */
    public List<Student> searchStudents(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>(students.values());
        }
        
        String lowerQuery = query.toLowerCase().trim();
        List<Student> results = new ArrayList<>();
        
        for (Student s : students.values()) {
            if (s.getId().toLowerCase().contains(lowerQuery) || 
                s.getName().toLowerCase().contains(lowerQuery)) {
                results.add(s);
            }
        }
        return results;
    }

    // --- Core Grading and Suggestion Engine ---

    /**
     * Calculates totals, averages, assigns grade & performance, and generates diagnostic suggestions.
     */
    public void calculateMetrics(Student s) {
        double sum = 0.0;
        int count = SUBJECTS.length;
        
        for (String subject : SUBJECTS) {
            sum += s.getMark(subject);
        }

        s.setTotalMarks(sum);
        double avg = sum / count;
        s.setAverageMarks(avg);

        // Grade calculation based on academic guidelines
        String grade;
        if (avg >= 85.0) grade = "A";
        else if (avg >= 70.0) grade = "B";
        else if (avg >= 60.0) grade = "C";
        else if (avg >= 50.0) grade = "D";
        else grade = "F";
        s.setGrade(grade);

        // Performance category prediction based on requirements
        String category;
        if (avg >= 85.0) category = "Excellent";
        else if (avg >= 70.0) category = "Good";
        else if (avg >= 50.0) category = "Average";
        else category = "Poor";
        s.setPerformanceCategory(category);

        // Generate tailored suggestions
        StringBuilder suggestions = new StringBuilder();
        boolean hasIssue = false;

        // 1. Attendance warning
        if (s.getAttendance() < 75.0) {
            suggestions.append("⚠️ Attendance Warning: Current attendance is ")
                       .append(String.format("%.1f", s.getAttendance()))
                       .append("%. Attend regular lectures to cross the mandatory 75% threshold.\n\n");
            hasIssue = true;
        }

        // 2. Assignment score warning
        if (s.getAssignmentScore() < 60.0) {
            suggestions.append("📝 Assignment Gap: Assignment score (")
                       .append(String.format("%.1f", s.getAssignmentScore()))
                       .append("/100) is below benchmark. Focus on timely submissions and quality check.\n\n");
            hasIssue = true;
        }

        // 3. Subject-wise marks warning
        List<String> weakSubjects = new ArrayList<>();
        for (String subject : SUBJECTS) {
            if (s.getMark(subject) < 50.0) {
                weakSubjects.add(subject);
            }
        }

        if (!weakSubjects.isEmpty()) {
            suggestions.append("📚 Academic Focus: Low marks detected in ")
                       .append(String.join(", ", weakSubjects))
                       .append(" (below 50 marks). Revise basic concepts, resolve doubts, and request extra tutoring.\n\n");
            hasIssue = true;
        }

        // 4. Default positive report if all standards met
        if (!hasIssue) {
            suggestions.append("✨ Outstanding Performance: Academic records are in stellar standing. Maintain your current study habits and continue striving for excellence!");
        } else {
            // Trim any excess trailing newlines
            String rawSug = suggestions.toString().trim();
            s.setSuggestions(rawSug);
        }
    }

    // --- File Operations ---

    /**
     * Serialization: Saves the active student records to 'data/students.dat'.
     */
    public synchronized void saveStudents() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(students);
        } catch (IOException e) {
            System.err.println("⚠️ Warning: Could not save student records: " + e.getMessage());
        }
    }

    /**
     * Serialization: Loads the active student records from 'data/students.dat'.
     */
    @SuppressWarnings("unchecked")
    private synchronized void loadStudents() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            students = (Map<String, Student>) ois.readObject();
            // Re-run calculations to keep state intact
            for (Student s : students.values()) {
                calculateMetrics(s);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("⚠️ Warning: Could not load student data. Reinitializing database: " + e.getMessage());
            students = new HashMap<>();
        }
    }

    /**
     * Report Generator: Exports a beautiful, professional, human-readable student performance report.
     * Generates a plain-text file inside reports/ folder.
     */
    public String generateReportFile(String id) throws IOException {
        Student s = students.get(id);
        if (s == null) {
            throw new IllegalArgumentException("❌ Student ID " + id + " not found.");
        }

        String fileName = REPORTS_DIR + "/report_" + s.getId() + ".txt";
        File reportFile = new File(fileName);
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(reportFile))) {
            writer.println("=================================================================");
            writer.println("                  STUDENT PERFORMANCE ANALYSIS REPORT            ");
            writer.println("=================================================================");
            writer.println("  GENESIS ACADEMIC ANALYZER - OFFICIAL SYSTEM REPORT             ");
            writer.println("  Date Generated: " + new Date().toString());
            writer.println("-----------------------------------------------------------------");
            writer.println("  STUDENT PROFILE DETAILS:                                       ");
            writer.println("    Student Name : " + s.getName());
            writer.println("    Roll/ID No   : " + s.getId());
            writer.println("    Email Address: " + s.getEmail());
            writer.println("-----------------------------------------------------------------");
            writer.println("  ACADEMIC PERFORMANCE SUMMARY:                                  ");
            writer.println("    Attendance Percentage  : " + String.format("%.2f", s.getAttendance()) + "%");
            writer.println("    Assignment Core Score  : " + String.format("%.2f", s.getAssignmentScore()) + "/100");
            writer.println("-----------------------------------------------------------------");
            writer.println("  SUBJECT MARKS BREAKDOWN:                                       ");
            for (String subject : SUBJECTS) {
                double mark = s.getMark(subject);
                String status = mark >= 50.0 ? "PASS" : "FAIL (Low)";
                writer.printf("    %-22s : %6.2f / 100   [%s]\n", subject, mark, status);
            }
            writer.println("-----------------------------------------------------------------");
            writer.println("  ANALYTICAL METRICS:                                            ");
            writer.printf("    %-22s : %6.2f\n", "Total Cumulative Marks", s.getTotalMarks());
            writer.printf("    %-22s : %6.2f%%\n", "Average Marks Percentage", s.getAverageMarks());
            writer.printf("    %-22s : %s\n", "Assigned Academic Grade", s.getGrade());
            writer.printf("    %-22s : [ %s ]\n", "Performance Rating", s.getPerformanceCategory().toUpperCase());
            writer.println("-----------------------------------------------------------------");
            writer.println("  DIAGNOSTIC ADVISORY & SUGGESTIONS:                             ");
            
            String[] lines = s.getSuggestions().split("\n");
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    writer.println("    " + line);
                }
            }
            writer.println("=================================================================");
            writer.println("         Developed by Antigravity AI - Premium Viva Edition      ");
            writer.println("=================================================================");
        }
        
        return reportFile.getAbsolutePath();
    }
}
