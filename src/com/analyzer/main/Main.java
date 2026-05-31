package com.analyzer.main;

import com.analyzer.model.Student;
import com.analyzer.service.AuthService;
import com.analyzer.service.StudentService;
import com.analyzer.ui.LoginFrame;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

/**
 * VIVA EXPLANATION - SYSTEM ENTRY POINT
 * This is the Main executable class of the Student Performance Analyzer.
 * 1. Look-and-Feel Setup: Sets up System-native styles so window frames blend perfectly with the OS.
 * 2. Service Orchestration: Instantiates the singletons 'AuthService' and 'StudentService' and injects
 *    them into the GUI frames (Dependency Injection concept).
 * 3. Seed Mock Data: Automatically injects 3 distinct student test profiles (Excellent, Average, Poor)
 *    so the system has rich, immediate analytical charts during viva evaluations without manual entry.
 * 4. Thread Safety: Uses SwingUtilities.invokeLater() to ensure the GUI launches safely on the
 *    Swing Event Dispatch Thread (EDT).
 */
public class Main {
    
    public static void main(String[] args) {
        // Step 1: Set Native System Look & Feel for crisp OS window controls
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("⚠️ Could not load native system theme. Using fallback default.");
        }

        // Step 2: Initialize Core Service Singletons
        AuthService authService = new AuthService();
        StudentService studentService = new StudentService();

        // Step 3: Seed realistic student data if the database is currently empty (viva convenience)
        if (studentService.getAllStudents().isEmpty()) {
            seedSampleStudents(studentService);
        }

        // Step 4: Boot up the beautiful Login UI on the Swing Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginFrame login = new LoginFrame(authService, studentService);
                login.setVisible(true);
            }
        });
    }

    /**
     * Seeds the local flat-file database with 3 distinct student performance archetypes.
     * This provides a complete ready-made simulation for testing analysis and reports!
     */
    private static void seedSampleStudents(StudentService service) {
        // Archetype 1: Excellent Student (Straight A Performer)
        Student s1 = new Student("STU201", "Emma Watson", "emma.watson@academia.edu");
        Map<String, Double> marks1 = new HashMap<>();
        marks1.put("Mathematics", 94.0);
        marks1.put("Science", 96.0);
        marks1.put("English", 90.0);
        marks1.put("History", 88.0);
        marks1.put("Computer Science", 98.0);
        service.registerStudent(s1);
        service.recordAcademicPerformance("STU201", marks1, 95.5, 92.0);

        // Archetype 2: Average Student (Passing but low assignment and attendance warnings)
        Student s2 = new Student("STU202", "Ronald Weasley", "ronald.w@academia.edu");
        Map<String, Double> marks2 = new HashMap<>();
        marks2.put("Mathematics", 58.0);
        marks2.put("Science", 62.0);
        marks2.put("English", 55.0);
        marks2.put("History", 68.0);
        marks2.put("Computer Science", 72.0);
        service.registerStudent(s2);
        // Ron has 72% attendance (<75% threshold) and 55.0 assignment score (<60% threshold)
        service.recordAcademicPerformance("STU202", marks2, 72.0, 55.0);

        // Archetype 3: Struggling Student (Poor standing, failing subject warning)
        Student s3 = new Student("STU203", "Neville Longbottom", "neville.l@academia.edu");
        Map<String, Double> marks3 = new HashMap<>();
        marks3.put("Mathematics", 42.0); // Fail subject mark (<50)
        marks3.put("Science", 70.0);
        marks3.put("English", 48.0); // Fail subject mark (<50)
        marks3.put("History", 50.0);
        marks3.put("Computer Science", 45.0); // Fail subject mark (<50)
        service.registerStudent(s3);
        // Neville has excellent attendance, but low assignment score and three weak subjects
        service.recordAcademicPerformance("STU203", marks3, 88.0, 58.0);
        
        System.out.println("🌱 Successfully seeded database with 3 sample profiles for viva examinations.");
    }
}
