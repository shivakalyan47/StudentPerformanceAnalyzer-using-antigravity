package com.analyzer.ui;

import com.analyzer.model.Student;
import com.analyzer.service.StudentService;
import com.analyzer.ui.CustomComponents.*;
import com.analyzer.util.ValidationUtil;
import com.analyzer.util.ValidationUtil.ValidationException;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * VIVA EXPLANATION - OOP CONCEPT: VALIDATION & FORM SYNCHRONIZATION
 * This panel bridges the gap between student identity and academic records.
 * 1. Synchronized selection: Listening to table row selection allows us to dynamically retrieve
 *    the Student object and populate the input fields with existing scores.
 * 2. Range validation: Ensures all numeric entries are strict doubles in the 0.0 - 100.0 range.
 * 3. Automatic recalculation: Saving instantly recalculates metrics, updates grades, and persists data.
 */
public class MarksPanel extends JPanel {

    private StudentService studentService;
    private DashboardFrame parentFrame;

    // Student selection elements
    private JTable tblStudents;
    private DefaultTableModel tableModel;
    private ModernTextField txtSearch;

    // Academic inputs (Math, Science, English, History, CompSci)
    private Map<String, ModernTextField> subjectFields;
    private ModernTextField txtAttendance;
    private ModernTextField txtAssignment;

    private JLabel lblSelectedStudentName;
    private String selectedStudentId = null;

    public MarksPanel(StudentService studentService, DashboardFrame parentFrame) {
        this.studentService = studentService;
        this.parentFrame = parentFrame;
        this.subjectFields = new HashMap<>();

        setLayout(new BorderLayout(20, 20));
        setOpaque(false);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Assemble Left Container: Student Picker List Table
        JPanel leftContainer = new JPanel(new BorderLayout(15, 15));
        leftContainer.setOpaque(false);
        leftContainer.setPreferredSize(new Dimension(380, 0));

        RoundedPanel listCard = new RoundedPanel(16, CustomComponents.COLOR_CARD_BG);
        listCard.setLayout(new BorderLayout(15, 15));
        listCard.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblListTitle = new JLabel("Select Student to Enter Marks");
        lblListTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblListTitle.setForeground(CustomComponents.COLOR_PRIMARY);

        JPanel searchBar = new JPanel(new BorderLayout(10, 0));
        searchBar.setOpaque(false);
        JLabel lblSearch = new JLabel("🔍");
        lblSearch.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        txtSearch = new ModernTextField("Search...", 15);
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                refreshTable(txtSearch.getText().trim());
            }
        });
        searchBar.add(lblSearch, BorderLayout.WEST);
        searchBar.add(txtSearch, BorderLayout.CENTER);

        // Standard student list columns
        String[] columnNames = {"ID/Roll No", "Full Name", "Average %"};
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
        listCard.add(searchBar, BorderLayout.CENTER); // Will layout cleanly
        
        // Put scroll inside listCard center
        JPanel scrollWrapper = new JPanel(new BorderLayout(0, 10));
        scrollWrapper.setOpaque(false);
        scrollWrapper.add(searchBar, BorderLayout.NORTH);
        scrollWrapper.add(scrollPane, BorderLayout.CENTER);
        listCard.add(scrollWrapper, BorderLayout.CENTER);

        leftContainer.add(listCard, BorderLayout.CENTER);

        // Assemble Right Container: Marks Editor Panel
        RoundedPanel editorCard = new RoundedPanel(16, CustomComponents.COLOR_CARD_BG);
        editorCard.setLayout(new BorderLayout(15, 15));
        editorCard.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header showing selection status
        JPanel editorHeader = new JPanel(new BorderLayout());
        editorHeader.setOpaque(false);
        editorHeader.setBorder(new EmptyBorder(0, 0, 10, 0));

        lblSelectedStudentName = new JLabel("Select a student to edit records");
        lblSelectedStudentName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblSelectedStudentName.setForeground(CustomComponents.COLOR_TEXT_DARK);

        JLabel lblModuleIcon = new JLabel("📝 ");
        lblModuleIcon.setFont(new Font("Segoe UI", Font.PLAIN, 20));

        editorHeader.add(lblModuleIcon, BorderLayout.WEST);
        editorHeader.add(lblSelectedStudentName, BorderLayout.CENTER);

        // Main input fields grid layout
        JPanel gridInputs = new JPanel(new GridLayout(7, 2, 15, 12));
        gridInputs.setOpaque(false);

        // 1. Add fields for Core Subjects
        for (String subject : StudentService.SUBJECTS) {
            JLabel lblSub = new JLabel(subject + " Marks (0-100)");
            lblSub.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblSub.setForeground(CustomComponents.COLOR_TEXT_DARK);

            ModernTextField txtField = new ModernTextField("0.0", 10);
            subjectFields.put(subject, txtField);

            gridInputs.add(lblSub);
            gridInputs.add(txtField);
        }

        // 2. Add fields for Attendance
        JLabel lblAttendance = new JLabel("Attendance Percentage (0-100%)");
        lblAttendance.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblAttendance.setForeground(CustomComponents.COLOR_TEXT_DARK);
        txtAttendance = new ModernTextField("0.0", 10);
        gridInputs.add(lblAttendance);
        gridInputs.add(txtAttendance);

        // 3. Add fields for Assignments
        JLabel lblAssignment = new JLabel("Assignment Score (0-100)");
        lblAssignment.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblAssignment.setForeground(CustomComponents.COLOR_TEXT_DARK);
        txtAssignment = new ModernTextField("0.0", 10);
        gridInputs.add(lblAssignment);
        gridInputs.add(txtAssignment);

        // Editor Form Action buttons
        JPanel bottomButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottomButtons.setOpaque(false);
        bottomButtons.setBorder(new EmptyBorder(15, 0, 0, 0));

        ModernButton btnSave = new ModernButton("Save Academic Performance");
        ModernButton btnClear = new ModernButton("Reset Fields", 
                CustomComponents.COLOR_TEXT_MUTED, 
                CustomComponents.COLOR_TEXT_MUTED.brighter());

        bottomButtons.add(btnClear);
        bottomButtons.add(btnSave);

        editorCard.add(editorHeader, BorderLayout.NORTH);
        editorCard.add(gridInputs, BorderLayout.CENTER);
        editorCard.add(bottomButtons, BorderLayout.SOUTH);

        // Add to main panel layout
        add(leftContainer, BorderLayout.WEST);
        add(editorCard, BorderLayout.CENTER);

        // Set inputs disabled initially until a student selection is locked
        enableInputs(false);

        // --- Controller Event Triggers ---

        // Listen for table selection row changes
        tblStudents.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    handleStudentSelection();
                }
            }
        });

        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSavePerformance();
            }
        });

        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetInputFields();
            }
        });

        // Initialize table
        refreshTable("");
    }

    /**
     * Refreshes the student selection grid panel.
     */
    public void refreshTable(String query) {
        tableModel.setRowCount(0);
        List<Student> list = studentService.searchStudents(query);
        for (Student s : list) {
            tableModel.addRow(new Object[]{
                    s.getId(),
                    s.getName(),
                    String.format("%.2f%%", s.getAverageMarks())
            });
        }
        selectedStudentId = null;
        lblSelectedStudentName.setText("Select a student to edit records");
        enableInputs(false);
        resetInputFields();
    }

    /**
     * Toggles whether the text fields are editable.
     */
    private void enableInputs(boolean enable) {
        for (ModernTextField tf : subjectFields.values()) {
            tf.setEnabled(enable);
        }
        txtAttendance.setEnabled(enable);
        txtAssignment.setEnabled(enable);
    }

    /**
     * Empties out values in text boxes.
     */
    private void resetInputFields() {
        for (ModernTextField tf : subjectFields.values()) {
            tf.setText("0.0");
        }
        txtAttendance.setText("0.0");
        txtAssignment.setText("0.0");
    }

    /**
     * Synchronizes row selection in table, loading records into fields.
     */
    private void handleStudentSelection() {
        int selectedRow = tblStudents.getSelectedRow();
        if (selectedRow == -1) {
            enableInputs(false);
            selectedStudentId = null;
            return;
        }

        String id = tblStudents.getValueAt(selectedRow, 0).toString();
        Student s = studentService.getStudent(id);
        if (s != null) {
            selectedStudentId = id;
            lblSelectedStudentName.setText("Academic Card: " + s.getName());
            enableInputs(true);

            // Populate current values into the editor text fields
            for (String subject : StudentService.SUBJECTS) {
                subjectFields.get(subject).setText(String.valueOf(s.getMark(subject)));
            }
            txtAttendance.setText(String.valueOf(s.getAttendance()));
            txtAssignment.setText(String.valueOf(s.getAssignmentScore()));
        }
    }

    /**
     * Validates inputs, saves performance metrics, recalculates scores, and syncs changes.
     */
    private void handleSavePerformance() {
        if (selectedStudentId == null) {
            JOptionPane.showMessageDialog(this, "⚠️ No student selected.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Map<String, Double> marksMap = new HashMap<>();

            // 1. Validate subject scores
            for (String subject : StudentService.SUBJECTS) {
                String valStr = subjectFields.get(subject).getText().trim();
                double mark = ValidationUtil.validatePercentage(subject + " Marks", valStr);
                marksMap.put(subject, mark);
            }

            // 2. Validate attendance
            String attendanceStr = txtAttendance.getText().trim();
            double attendance = ValidationUtil.validatePercentage("Attendance Percentage", attendanceStr);

            // 3. Validate assignments
            String assignmentStr = txtAssignment.getText().trim();
            double assignment = ValidationUtil.validatePercentage("Assignment Score", assignmentStr);

            // 4. Save and calculate
            if (studentService.recordAcademicPerformance(selectedStudentId, marksMap, attendance, assignment)) {
                JOptionPane.showMessageDialog(this, 
                        "📊 Academic Performance records successfully updated!", 
                        "Updated", JOptionPane.INFORMATION_MESSAGE);
                
                // Refresh views dynamically
                refreshTable(txtSearch.getText().trim());
                parentFrame.refreshDashboardMetrics(); // Instantly update dashboard numbers!
            } else {
                JOptionPane.showMessageDialog(this, "❌ Could not save metrics.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (ValidationException ex) {
            // Exception catches gracefully and tells advisor where the input is out-of-bounds or formatting is broken
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Format Alert", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "⚠️ System Failure: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
