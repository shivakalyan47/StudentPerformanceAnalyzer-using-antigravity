package com.analyzer.ui;

import com.analyzer.model.Student;
import com.analyzer.service.StudentService;
import com.analyzer.ui.CustomComponents.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

/**
 * VIVA EXPLANATION - OOP CONCEPT: EXCEPTION HANDLING AND FILE STREAM WRITING
 * This panel is responsible for generating academic transcripts.
 * 1. File IO Execution: On "Export", it triggers the StudentService reporting engine to write a structured
 *    plaintext document in reports/ folder.
 * 2. Streams & I/O Exception catch: If files are locked or permission is denied, it handles the standard
 *    IOException safely and alerts the user with detailed dialog feedback.
 * 3. Text Panel Viewers: Standard Monospaced fonts ensure characters align neatly (tables, borders).
 */
public class ReportPanel extends JPanel {

    private StudentService studentService;

    // Table
    private JTable tblStudents;
    private DefaultTableModel tableModel;

    // Transcript elements
    private JTextArea txtTranscript;
    private ModernButton btnExport;
    private JLabel lblHeading;
    private String selectedStudentId = null;

    public ReportPanel(StudentService studentService) {
        this.studentService = studentService;

        setLayout(new BorderLayout(20, 20));
        setOpaque(false);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Assemble Left Card: Selection roster
        JPanel leftContainer = new JPanel(new BorderLayout(15, 15));
        leftContainer.setOpaque(false);
        leftContainer.setPreferredSize(new Dimension(320, 0));

        RoundedPanel listCard = new RoundedPanel(16, CustomComponents.COLOR_CARD_BG);
        listCard.setLayout(new BorderLayout(15, 15));
        listCard.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblListTitle = new JLabel("Academic Transcripts");
        lblListTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblListTitle.setForeground(CustomComponents.COLOR_PRIMARY);

        String[] columnNames = {"ID", "Student Name", "Grade"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblStudents = new JTable(tableModel);
        tblStudents.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblStudents.setRowHeight(38);
        tblStudents.setShowGrid(false);
        tblStudents.setIntercellSpacing(new Dimension(0, 0));
        tblStudents.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblStudents.setBackground(Color.WHITE);

        JTableHeader header = tblStudents.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(CustomComponents.COLOR_BG_LIGHT);
        header.setForeground(CustomComponents.COLOR_TEXT_DARK);
        header.setPreferredSize(new Dimension(0, 40));

        tblStudents.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, 
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                if (isSelected) {
                    c.setBackground(new Color(239, 246, 255));
                    c.setForeground(CustomComponents.COLOR_PRIMARY);
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(CustomComponents.COLOR_TEXT_DARK);
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblStudents);
        scrollPane.setBorder(BorderFactory.createLineBorder(CustomComponents.COLOR_BORDER, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        listCard.add(lblListTitle, BorderLayout.NORTH);
        listCard.add(scrollPane, BorderLayout.CENTER);
        leftContainer.add(listCard, BorderLayout.CENTER);

        // Assemble Right Card: Monospaced Text Transcript Viewer
        RoundedPanel transcriptCard = new RoundedPanel(16, CustomComponents.COLOR_CARD_BG);
        transcriptCard.setLayout(new BorderLayout(15, 15));
        transcriptCard.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Upper Header controls inside Viewer Card
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        lblHeading = new JLabel("Official Report card Viewer");
        lblHeading.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeading.setForeground(CustomComponents.COLOR_TEXT_DARK);

        JLabel lblIcon = new JLabel("📄 ");
        lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 20));

        headerPanel.add(lblIcon, BorderLayout.WEST);
        headerPanel.add(lblHeading, BorderLayout.CENTER);

        // Monospaced text panel for aligning standard tabular transcripts
        txtTranscript = new JTextArea();
        txtTranscript.setEditable(false);
        txtTranscript.setFont(new Font("Consolas", Font.PLAIN, 12)); // Courier/Consolas for typewriter reports
        txtTranscript.setForeground(CustomComponents.COLOR_TEXT_DARK);
        txtTranscript.setBackground(new Color(248, 250, 252));
        txtTranscript.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CustomComponents.COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JScrollPane transcriptScroll = new JScrollPane(txtTranscript);
        transcriptScroll.setBorder(null);

        // Bottom triggers panel
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomBar.setOpaque(false);

        btnExport = new ModernButton("Export Official Report Card (.txt)");
        bottomBar.add(btnExport);

        transcriptCard.add(headerPanel, BorderLayout.NORTH);
        transcriptCard.add(transcriptScroll, BorderLayout.CENTER);
        transcriptCard.add(bottomBar, BorderLayout.SOUTH);

        add(leftContainer, BorderLayout.WEST);
        add(transcriptCard, BorderLayout.CENTER);

        // Bind events
        tblStudents.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    handleSelectionChange();
                }
            }
        });

        btnExport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleExportReport();
            }
        });

        refreshTable();
        clearTranscript();
    }

    /**
     * Refreshes the student records picker grid.
     */
    public void refreshTable() {
        tableModel.setRowCount(0);
        for (Student s : studentService.getAllStudents()) {
            tableModel.addRow(new Object[]{
                    s.getId(),
                    s.getName(),
                    s.getGrade()
            });
        }
        clearTranscript();
    }

    /**
     * Clears text content.
     */
    private void clearTranscript() {
        txtTranscript.setText("Select a student from the register roster grid on the left to compile and inspect their official academic report card transcript.");
        btnExport.setEnabled(false);
        selectedStudentId = null;
        lblHeading.setText("Official Report Card Viewer");
    }

    /**
     * Compiles textual report preview inside monospaced text panel dynamically.
     */
    private void handleSelectionChange() {
        int selectedRow = tblStudents.getSelectedRow();
        if (selectedRow == -1) {
            clearTranscript();
            return;
        }

        String id = tblStudents.getValueAt(selectedRow, 0).toString();
        Student s = studentService.getStudent(id);

        if (s == null) {
            clearTranscript();
            return;
        }

        selectedStudentId = id;
        btnExport.setEnabled(true);
        lblHeading.setText("Transcript: " + s.getName());

        // Construct standard textual layout inside text pane
        StringBuilder sb = new StringBuilder();
        sb.append("=================================================================\n");
        sb.append("                  STUDENT PERFORMANCE ANALYSIS REPORT            \n");
        sb.append("=================================================================\n");
        sb.append("  GENESIS ACADEMIC ANALYZER - OFFICIAL PREVIEW                   \n");
        sb.append("-----------------------------------------------------------------\n");
        sb.append("  STUDENT PROFILE DETAILS:                                       \n");
        sb.append("    Student Name : ").append(s.getName()).append("\n");
        sb.append("    Roll/ID No   : ").append(s.getId()).append("\n");
        sb.append("    Email Address: ").append(s.getEmail()).append("\n");
        sb.append("-----------------------------------------------------------------\n");
        sb.append("  ACADEMIC PERFORMANCE SUMMARY:                                  \n");
        sb.append("    Attendance Percentage  : ").append(String.format("%.2f", s.getAttendance())).append("%\n");
        sb.append("    Assignment Core Score  : ").append(String.format("%.2f", s.getAssignmentScore())).append("/100\n");
        sb.append("-----------------------------------------------------------------\n");
        sb.append("  SUBJECT MARKS BREAKDOWN:                                       \n");
        for (String subject : StudentService.SUBJECTS) {
            double mark = s.getMark(subject);
            String status = mark >= 50.0 ? "PASS" : "FAIL (Low)";
            sb.append(String.format("    %-22s : %6.2f / 100   [%s]\n", subject, mark, status));
        }
        sb.append("-----------------------------------------------------------------\n");
        sb.append("  ANALYTICAL METRICS:                                            \n");
        sb.append(String.format("    %-22s : %6.2f\n", "Total Cumulative Marks", s.getTotalMarks()));
        sb.append(String.format("    %-22s : %6.2f%%\n", "Average Marks Percentage", s.getAverageMarks()));
        sb.append(String.format("    %-22s : %s\n", "Assigned Academic Grade", s.getGrade()));
        sb.append(String.format("    %-22s : [ %s ]\n", "Performance Rating", s.getPerformanceCategory().toUpperCase()));
        sb.append("-----------------------------------------------------------------\n");
        sb.append("  DIAGNOSTIC ADVISORY & SUGGESTIONS:                             \n");
        
        String[] lines = s.getSuggestions().split("\n");
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                sb.append("    ").append(line).append("\n");
            }
        }
        sb.append("=================================================================\n");

        txtTranscript.setText(sb.toString());
        txtTranscript.setCaretPosition(0); // Scroll to top preview
    }

    /**
     * Generates persistent report card text file on filesystem.
     * Demonstrates I/O exception handling clean structure.
     */
    private void handleExportReport() {
        if (selectedStudentId == null) {
            return;
        }

        try {
            // Write to file inside reports/ folder
            String absolutePath = studentService.generateReportFile(selectedStudentId);

            JOptionPane.showMessageDialog(this, 
                    "📂 Report Card generated and exported successfully!\n\n" +
                    "File Location:\n" + absolutePath + "\n\n" +
                    "Open the file with Notepad to print the official transcript.", 
                    "Report Exported", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException ex) {
            // EXCEPTION HANDLED BEAUTIFULLY: File locking, permission errors caught safely.
            JOptionPane.showMessageDialog(this, 
                    "❌ Failed to generate report file.\n" +
                    "Error Details: " + ex.getMessage(), 
                    "I/O Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
