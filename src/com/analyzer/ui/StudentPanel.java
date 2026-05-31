package com.analyzer.ui;

import com.analyzer.model.Student;
import com.analyzer.service.StudentService;
import com.analyzer.ui.CustomComponents.*;
import com.analyzer.util.ValidationUtil;
import com.analyzer.util.ValidationUtil.ValidationException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * VIVA EXPLANATION - OOP CONCEPT: EXCEPTION HANDLING & EVENT LISTENING
 * 1. UI Event Binding: Attaches ActionListeners to trigger additions, clears, and removals.
 * 2. GUI Form Validation: Harnesses ValidationUtil inside try-catch blocks. If a user leaves a field
 *    blank or inputs invalid data, it stops immediately, alerts the user, and keeps the program safe.
 * 3. Swing JTable Control: Maps local Student array models into standard DefaultTableModel structures.
 */
public class StudentPanel extends JPanel {

    private StudentService studentService;
    private DashboardFrame parentFrame;

    // Form inputs
    private ModernTextField txtId;
    private ModernTextField txtName;
    private ModernTextField txtEmail;
    private JLabel lblFormTitle;

    // Table elements
    private JTable tblStudents;
    private DefaultTableModel tableModel;
    private ModernTextField txtSearch;

    public StudentPanel(StudentService studentService, DashboardFrame parentFrame) {
        this.studentService = studentService;
        this.parentFrame = parentFrame;

        setLayout(new BorderLayout(20, 20));
        setOpaque(false);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Assemble Left Card: Registration Form
        JPanel leftContainer = new JPanel(new BorderLayout());
        leftContainer.setOpaque(false);
        leftContainer.setPreferredSize(new Dimension(320, 0));

        RoundedPanel formCard = new RoundedPanel(16, CustomComponents.COLOR_CARD_BG);
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBorder(new EmptyBorder(20, 20, 20, 20));

        lblFormTitle = new JLabel("Register Student");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblFormTitle.setForeground(CustomComponents.COLOR_PRIMARY);
        lblFormTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblFormTitle.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel lblId = new JLabel("Student ID / Roll No");
        lblId.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblId.setForeground(CustomComponents.COLOR_TEXT_DARK);
        lblId.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblId.setBorder(new EmptyBorder(5, 0, 4, 0));

        txtId = new ModernTextField("e.g. STU101", 15);
        txtId.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtId.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblName = new JLabel("Full Name");
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblName.setForeground(CustomComponents.COLOR_TEXT_DARK);
        lblName.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblName.setBorder(new EmptyBorder(10, 0, 4, 0));

        txtName = new ModernTextField("e.g. Alice Smith", 15);
        txtName.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblEmail = new JLabel("Email Address");
        lblEmail.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblEmail.setForeground(CustomComponents.COLOR_TEXT_DARK);
        lblEmail.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblEmail.setBorder(new EmptyBorder(10, 0, 4, 0));

        txtEmail = new ModernTextField("e.g. alice@school.com", 15);
        txtEmail.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtEmail.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Buttons for Form
        JPanel buttonRow = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonRow.setOpaque(false);
        buttonRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        buttonRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonRow.setBorder(new EmptyBorder(25, 0, 0, 0));

        ModernButton btnRegister = new ModernButton("Save");
        ModernButton btnClear = new ModernButton("Clear", 
                CustomComponents.COLOR_TEXT_MUTED, 
                CustomComponents.COLOR_TEXT_MUTED.brighter());

        buttonRow.add(btnRegister);
        buttonRow.add(btnClear);

        formCard.add(lblFormTitle);
        formCard.add(lblId);
        formCard.add(txtId);
        formCard.add(lblName);
        formCard.add(txtName);
        formCard.add(lblEmail);
        formCard.add(txtEmail);
        formCard.add(buttonRow);
        
        leftContainer.add(formCard, BorderLayout.NORTH);

        // Assemble Right Card: Student Records Grid Table
        RoundedPanel tableCard = new RoundedPanel(16, CustomComponents.COLOR_CARD_BG);
        tableCard.setLayout(new BorderLayout(15, 15));
        tableCard.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Upper Header controls inside Table Card
        JPanel searchBar = new JPanel(new BorderLayout(10, 0));
        searchBar.setOpaque(false);
        
        JLabel lblSearch = new JLabel("🔍");
        lblSearch.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        txtSearch = new ModernTextField("Search by ID or Name...", 20);
        
        // Dynamic search event
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                refreshTable(txtSearch.getText().trim());
            }
        });

        searchBar.add(lblSearch, BorderLayout.WEST);
        searchBar.add(txtSearch, BorderLayout.CENTER);

        // Modern JTable initialization
        String[] columnNames = {"Student ID", "Full Name", "Email Address", "Grade", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Immutable cells for double-click safety
            }
        };
        
        tblStudents = new JTable(tableModel);
        tblStudents.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblStudents.setRowHeight(38);
        tblStudents.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblStudents.setShowGrid(false);
        tblStudents.setIntercellSpacing(new Dimension(0, 0));
        tblStudents.setBackground(Color.WHITE);

        // Style the Table Header
        JTableHeader header = tblStudents.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(CustomComponents.COLOR_BG_LIGHT);
        header.setForeground(CustomComponents.COLOR_TEXT_DARK);
        header.setPreferredSize(new Dimension(0, 40));
        header.setReorderingAllowed(false);

        // Custom Cell Renderer to add spacing and custom visual tag chips
        tblStudents.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, 
                    boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                
                if (isSelected) {
                    c.setBackground(new Color(239, 246, 255)); // Soft primary highlight
                    c.setForeground(CustomComponents.COLOR_PRIMARY);
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(CustomComponents.COLOR_TEXT_DARK);
                }

                // Add nice bold color rendering to the "Status" column
                if (column == 4 && value != null) {
                    setFont(getFont().deriveFont(Font.BOLD));
                    String val = value.toString();
                    if (val.equalsIgnoreCase("Excellent")) {
                        c.setForeground(CustomComponents.COLOR_SUCCESS);
                    } else if (val.equalsIgnoreCase("Good")) {
                        c.setForeground(CustomComponents.COLOR_PRIMARY);
                    } else if (val.equalsIgnoreCase("Average")) {
                        c.setForeground(CustomComponents.COLOR_WARNING);
                    } else {
                        c.setForeground(CustomComponents.COLOR_DANGER);
                    }
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblStudents);
        scrollPane.setBorder(BorderFactory.createLineBorder(CustomComponents.COLOR_BORDER, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Table Bottom Control Panel (Delete Student button)
        JPanel bottomControl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomControl.setOpaque(false);

        ModernButton btnDelete = new ModernButton("Delete Selected Student", 
                CustomComponents.COLOR_DANGER, 
                CustomComponents.COLOR_DANGER.brighter());
        
        bottomControl.add(btnDelete);

        tableCard.add(searchBar, BorderLayout.NORTH);
        tableCard.add(scrollPane, BorderLayout.CENTER);
        tableCard.add(bottomControl, BorderLayout.SOUTH);

        // Add both containers
        add(leftContainer, BorderLayout.WEST);
        add(tableCard, BorderLayout.CENTER);

        // --- Controller Event Triggers ---

        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegistration();
            }
        });

        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleDeletion();
            }
        });

        // Load all students on creation
        refreshTable("");
    }

    /**
     * Updates the Table rows with current values matching the filter query.
     */
    public void refreshTable(String query) {
        tableModel.setRowCount(0);
        List<Student> list = studentService.searchStudents(query);
        for (Student s : list) {
            tableModel.addRow(new Object[]{
                    s.getId(),
                    s.getName(),
                    s.getEmail(),
                    s.getGrade(),
                    s.getPerformanceCategory()
            });
        }
    }

    /**
     * Empties the form fields safely.
     */
    private void clearForm() {
        txtId.setText("");
        txtId.setEnabled(true);
        txtName.setText("");
        txtEmail.setText("");
        lblFormTitle.setText("Register Student");
    }

    /**
     * Captures, validates, and registers a student, handling custom exceptions elegantly.
     */
    private void handleRegistration() {
        try {
            String id = txtId.getText().trim();
            String name = txtName.getText().trim();
            String email = txtEmail.getText().trim();

            // Run validations through Exception-Throwing Utility
            ValidationUtil.validateId(id);
            ValidationUtil.validateNotEmpty("Full Name", name);
            ValidationUtil.validateEmail(email);

            // Construct entity
            Student newStudent = new Student(id, name, email);

            // Attempt save via StudentService
            if (studentService.registerStudent(newStudent)) {
                JOptionPane.showMessageDialog(this, 
                        "🎉 Student '" + name + "' successfully registered!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                refreshTable("");
                parentFrame.refreshDashboardMetrics(); // Instantly update Overview panels!
            } else {
                JOptionPane.showMessageDialog(this, 
                        "❌ Registration Failed. Student ID '" + id + "' is already enrolled.", 
                        "Conflict Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (ValidationException ex) {
            // EXCEPTION HANDLED BEAUTIFULLY: Informs user directly with the customized exception string
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "⚠️ An unexpected error occurred: " + ex.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Removes selected Student.
     */
    private void handleDeletion() {
        int selectedRow = tblStudents.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "⚠️ Please select a student from the grid to delete.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = tblStudents.getValueAt(selectedRow, 0).toString();
        String name = tblStudents.getValueAt(selectedRow, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(this, 
                "⚠️ Are you sure you want to completely delete student '" + name + "' (" + id + ")?\nThis action cannot be undone.", 
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (studentService.deleteStudent(id)) {
                JOptionPane.showMessageDialog(this, "✅ Student record deleted successfully.", "Removed", JOptionPane.INFORMATION_MESSAGE);
                refreshTable("");
                parentFrame.refreshDashboardMetrics(); // Synchronize overview
            } else {
                JOptionPane.showMessageDialog(this, "❌ Could not delete record.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
