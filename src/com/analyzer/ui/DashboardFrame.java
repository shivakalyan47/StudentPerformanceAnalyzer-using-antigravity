package com.analyzer.ui;

import com.analyzer.model.Student;
import com.analyzer.model.User;
import com.analyzer.service.AuthService;
import com.analyzer.service.StudentService;
import com.analyzer.ui.CustomComponents.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

/**
 * VIVA EXPLANATION - OOP CONCEPT: COHESION AND VIEW-CONTROLLER COORDINATION
 * This is the primary window frame coordinating all system activities.
 * 1. CardLayout Controller: Operates as a central router to easily swap panels (Overview, Students, Marks, Analysis, Reports).
 * 2. Real-time synchronization: When changes are made in the Marks or Student registry, they immediately
 *    bubble up to this DashboardFrame which triggers refreshes across all other panels.
 * 3. High-Cohesion: Maintains simple subpanels inside, delegating granular controls cleanly.
 */
public class DashboardFrame extends JFrame {

    private AuthService authService;
    private StudentService studentService;

    // Layout Panels
    private JPanel rightViewport;
    private CardLayout cardLayout;

    // View Panels
    private StudentPanel panelStudents;
    private MarksPanel panelMarks;
    private AnalysisPanel panelAnalysis;
    private ReportPanel panelReports;

    // Overview Stats Labels (Dynamic updates)
    private JLabel lblStatTotalStudents;
    private JLabel lblStatAverageMarks;
    private JLabel lblStatAverageAttendance;
    private JLabel lblStatAtRisk;

    // Navigation buttons (for active selection highlights)
    private JButton[] navButtons;
    private static final String[] PANEL_NAMES = {"Overview", "Students", "Marks", "Analysis", "Reports"};

    public DashboardFrame(AuthService authService, StudentService studentService) {
        this.authService = authService;
        this.studentService = studentService;

        setTitle("Genesis Student Performance Analyzer - Workspace Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setMinimumSize(new Dimension(1100, 680));
        setLocationRelativeTo(null);

        // Main window splitter container
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(CustomComponents.COLOR_BG_LIGHT);
        setContentPane(mainContainer);

        // ----------------- SIDEBAR PANEL (LEFT) -----------------
        GradientPanel sidebar = new GradientPanel(
                CustomComponents.COLOR_SLATE_DARK, 
                CustomComponents.COLOR_SLATE_LIGHT, 
                false
        );
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setLayout(new BorderLayout());
        sidebar.setBorder(new EmptyBorder(25, 15, 25, 15));

        // Sidebar Top: Branding & Title
        JPanel brandPanel = new JPanel();
        brandPanel.setOpaque(false);
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblBrandIcon = new JLabel("🎓 GENESIS");
        lblBrandIcon.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblBrandIcon.setForeground(Color.WHITE);
        
        JLabel lblBrandTag = new JLabel("ACADEMIC ANALYZER");
        lblBrandTag.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblBrandTag.setForeground(CustomComponents.COLOR_PRIMARY);
        lblBrandTag.setBorder(new EmptyBorder(2, 4, 0, 0));

        brandPanel.add(lblBrandIcon);
        brandPanel.add(lblBrandTag);

        // Sidebar Middle: User Account Badge
        RoundedPanel userBadge = new RoundedPanel(12, CustomComponents.COLOR_SLATE_LIGHT);
        userBadge.setBorder(new EmptyBorder(12, 12, 12, 12));
        userBadge.setLayout(new GridLayout(2, 1, 0, 4));
        userBadge.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));
        userBadge.setBorder(BorderFactory.createLineBorder(new Color(51, 65, 85), 1)); // Thin border

        User u = authService.getCurrentUser();
        String name = (u != null) ? u.getFullName() : "Faculty Member";
        String role = (u != null) ? u.getRole() : "Advisor";

        JLabel lblUserName = new JLabel("👤 " + name);
        lblUserName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUserName.setForeground(Color.WHITE);
        
        JLabel lblUserRole = new JLabel("   " + role);
        lblUserRole.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblUserRole.setForeground(CustomComponents.COLOR_TEXT_MUTED);

        userBadge.add(lblUserName);
        userBadge.add(lblUserRole);

        // Top Column containing brand and user details
        JPanel topSidebar = new JPanel();
        topSidebar.setOpaque(false);
        topSidebar.setLayout(new BoxLayout(topSidebar, BoxLayout.Y_AXIS));
        topSidebar.add(brandPanel);
        topSidebar.add(Box.createRigidArea(new Dimension(0, 25)));
        topSidebar.add(userBadge);
        topSidebar.add(Box.createRigidArea(new Dimension(0, 30)));

        // Navigation button links panel
        JPanel navPanel = new JPanel();
        navPanel.setOpaque(false);
        navPanel.setLayout(new GridLayout(5, 1, 0, 10)); // Grid for navigation buttons

        navButtons = new JButton[5];
        navButtons[0] = createNavButton("📊   Overview", "Overview");
        navButtons[1] = createNavButton("👥   Register Student", "Students");
        navButtons[2] = createNavButton("✍️   Enter Marks", "Marks");
        navButtons[3] = createNavButton("🔍   Performance Stats", "Analysis");
        navButtons[4] = createNavButton("📄   Export Reports", "Reports");

        for (JButton btn : navButtons) {
            navPanel.add(btn);
        }

        topSidebar.add(navPanel);
        sidebar.add(topSidebar, BorderLayout.NORTH);

        // Logout Action Link (Bottom of Sidebar)
        ModernButton btnLogout = new ModernButton("🚪  Log Out System", 
                CustomComponents.COLOR_DANGER.darker(), 
                CustomComponents.COLOR_DANGER);
        btnLogout.setPreferredSize(new Dimension(0, 42));
        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogout();
            }
        });
        sidebar.add(btnLogout, BorderLayout.SOUTH);

        mainContainer.add(sidebar, BorderLayout.WEST);

        // ----------------- CONTENT VIEWPORT PANEL (RIGHT) -----------------
        cardLayout = new CardLayout();
        rightViewport = new JPanel(cardLayout);
        rightViewport.setOpaque(false);
        rightViewport.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Create Panel Instances
        panelStudents = new StudentPanel(studentService, this);
        panelMarks = new MarksPanel(studentService, this);
        panelAnalysis = new AnalysisPanel(studentService);
        panelReports = new ReportPanel(studentService);

        // Add to Card Viewport
        rightViewport.add(createOverviewPanel(), "Overview");
        rightViewport.add(panelStudents, "Students");
        rightViewport.add(panelMarks, "Marks");
        rightViewport.add(panelAnalysis, "Analysis");
        rightViewport.add(panelReports, "Reports");

        mainContainer.add(rightViewport, BorderLayout.CENTER);

        // Highlight initial tab
        setActiveNavHighlight("Overview");
        refreshDashboardMetrics();
    }

    /**
     * Helper to create beautiful navigation sidebar buttons.
     */
    private JButton createNavButton(String label, final String targetCard) {
        final JButton button = new JButton(label);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(new Color(148, 163, 184)); // Muted light slate
        button.setBackground(new Color(0, 0, 0, 0));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMargin(new Insets(10, 15, 10, 15));

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(rightViewport, targetCard);
                setActiveNavHighlight(targetCard);
                
                // Dynamically reload underlying panels on focus!
                if (targetCard.equalsIgnoreCase("Overview")) {
                    refreshDashboardMetrics();
                } else if (targetCard.equalsIgnoreCase("Students")) {
                    panelStudents.refreshTable("");
                } else if (targetCard.equalsIgnoreCase("Marks")) {
                    panelMarks.refreshTable("");
                } else if (targetCard.equalsIgnoreCase("Analysis")) {
                    panelAnalysis.refreshTable();
                } else if (targetCard.equalsIgnoreCase("Reports")) {
                    panelReports.refreshTable();
                }
            }
        });

        return button;
    }

    /**
     * Styles the active sidebar menu link dynamically.
     */
    private void setActiveNavHighlight(String activeCard) {
        for (int i = 0; i < PANEL_NAMES.length; i++) {
            JButton btn = navButtons[i];
            if (PANEL_NAMES[i].equalsIgnoreCase(activeCard)) {
                btn.setForeground(Color.WHITE);
                btn.setOpaque(true);
                btn.setBackground(CustomComponents.COLOR_SLATE_LIGHT);
            } else {
                btn.setForeground(new Color(148, 163, 184));
                btn.setOpaque(false);
                btn.setBackground(new Color(0, 0, 0, 0));
            }
        }
    }

    /**
     * Overview Dashboard Tab Panel: Displays live global enrollment data and metrics.
     */
    private JPanel createOverviewPanel() {
        JPanel overview = new JPanel(new BorderLayout(20, 20));
        overview.setOpaque(false);

        // Header Title Block
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel lblTitle = new JLabel("Academic Cockpit Overview");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(CustomComponents.COLOR_TEXT_DARK);

        JLabel lblWelcome = new JLabel("Welcome back to your workspace. Here is your class summary.");
        lblWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblWelcome.setForeground(CustomComponents.COLOR_TEXT_MUTED);
        lblWelcome.setBorder(new EmptyBorder(4, 0, 0, 0));

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblWelcome, BorderLayout.CENTER);

        // Analytics Row Cards (Grid of 4 Metric blocks)
        JPanel metricsGrid = new JPanel(new GridLayout(1, 4, 20, 0));
        metricsGrid.setOpaque(false);
        metricsGrid.setPreferredSize(new Dimension(0, 130));

        // Stat Card 1: Total Enrolled
        RoundedPanel card1 = new RoundedPanel(16);
        card1.setLayout(new BorderLayout());
        JLabel lblC1Title = new JLabel("ENROLLED STUDENTS");
        lblC1Title.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblC1Title.setForeground(CustomComponents.COLOR_TEXT_MUTED);
        lblStatTotalStudents = new JLabel("0");
        lblStatTotalStudents.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblStatTotalStudents.setForeground(CustomComponents.COLOR_PRIMARY);
        card1.add(lblC1Title, BorderLayout.NORTH);
        card1.add(lblStatTotalStudents, BorderLayout.CENTER);

        // Stat Card 2: Cumulative Average
        RoundedPanel card2 = new RoundedPanel(16);
        card2.setLayout(new BorderLayout());
        JLabel lblC2Title = new JLabel("CLASS SCORE AVG");
        lblC2Title.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblC2Title.setForeground(CustomComponents.COLOR_TEXT_MUTED);
        lblStatAverageMarks = new JLabel("0.00%");
        lblStatAverageMarks.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblStatAverageMarks.setForeground(CustomComponents.COLOR_SECONDARY);
        card2.add(lblC2Title, BorderLayout.NORTH);
        card2.add(lblStatAverageMarks, BorderLayout.CENTER);

        // Stat Card 3: Class Attendance Average
        RoundedPanel card3 = new RoundedPanel(16);
        card3.setLayout(new BorderLayout());
        JLabel lblC3Title = new JLabel("CLASS ATTENDANCE AVG");
        lblC3Title.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblC3Title.setForeground(CustomComponents.COLOR_TEXT_MUTED);
        lblStatAverageAttendance = new JLabel("0.00%");
        lblStatAverageAttendance.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblStatAverageAttendance.setForeground(CustomComponents.COLOR_SUCCESS);
        card3.add(lblC3Title, BorderLayout.NORTH);
        card3.add(lblStatAverageAttendance, BorderLayout.CENTER);

        // Stat Card 4: Critical Warnings
        RoundedPanel card4 = new RoundedPanel(16);
        card4.setLayout(new BorderLayout());
        JLabel lblC4Title = new JLabel("AT RISK STUDENTS (AVG < 50)");
        lblC4Title.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblC4Title.setForeground(CustomComponents.COLOR_TEXT_MUTED);
        lblStatAtRisk = new JLabel("0");
        lblStatAtRisk.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblStatAtRisk.setForeground(CustomComponents.COLOR_DANGER);
        card4.add(lblC4Title, BorderLayout.NORTH);
        card4.add(lblStatAtRisk, BorderLayout.CENTER);

        metricsGrid.add(card1);
        metricsGrid.add(card2);
        metricsGrid.add(card3);
        metricsGrid.add(card4);

        // Bottom Dashboard Graphic card showing quick visual usage instructions
        RoundedPanel graphicCard = new RoundedPanel(16, CustomComponents.COLOR_CARD_BG);
        graphicCard.setLayout(new BorderLayout(15, 15));
        graphicCard.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel lblHelpHeader = new JLabel("🎓 Viva Examination Quick Run Guide");
        lblHelpHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHelpHeader.setForeground(CustomComponents.COLOR_TEXT_DARK);
        lblHelpHeader.setBorder(new EmptyBorder(0, 0, 10, 0));

        // Quick Instructions Text
        JTextPane txtGuide = new JTextPane();
        txtGuide.setEditable(false);
        txtGuide.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtGuide.setForeground(CustomComponents.COLOR_TEXT_MUTED);
        txtGuide.setText(
                "Welcome to the Genesis Student Performance Analyzer! Use the tabs on the left to present the features:\n\n" +
                "1. 👥 Register Student: Add student profiles with unique IDs. Text inputs are validated in real-time.\n" +
                "2. ✍️ Enter Marks: Populate marks (0-100) across 5 subjects, register attendance % and assignment scores.\n" +
                "3. 🔍 Performance Stats: Inspect individual student averages, grading blocks, progress bars, and diagnostics.\n" +
                "4. 📄 Export Reports: Load official plain text transcripts and compile formatted text file cards directly to disk.\n\n" +
                "🔑 VIVA TIP: The system operates a robust flat-file binary serialization system. All changes saved inside the application " +
                "are persisted inside the 'data/' folder relative to the project directory, so closing and reopening will reload the entire roster automatically!"
        );
        txtGuide.setOpaque(false);

        graphicCard.add(lblHelpHeader, BorderLayout.NORTH);
        graphicCard.add(txtGuide, BorderLayout.CENTER);

        overview.add(headerPanel, BorderLayout.NORTH);
        overview.add(metricsGrid, BorderLayout.CENTER);
        overview.add(graphicCard, BorderLayout.SOUTH);

        return overview;
    }

    /**
     * Recalculates and updates the statistical dashboard meters on demand.
     */
    public void refreshDashboardMetrics() {
        Collection<Student> allStudents = studentService.getAllStudents();
        int count = allStudents.size();
        
        lblStatTotalStudents.setText(String.valueOf(count));

        if (count == 0) {
            lblStatAverageMarks.setText("0.00%");
            lblStatAverageAttendance.setText("0.00%");
            lblStatAtRisk.setText("0");
            return;
        }

        double sumAvg = 0.0;
        double sumAttendance = 0.0;
        int atRiskCount = 0;

        for (Student s : allStudents) {
            sumAvg += s.getAverageMarks();
            sumAttendance += s.getAttendance();
            if (s.getAverageMarks() < 50.0) {
                atRiskCount++;
            }
        }

        lblStatAverageMarks.setText(String.format("%.2f%%", sumAvg / count));
        lblStatAverageAttendance.setText(String.format("%.2f%%", sumAttendance / count));
        lblStatAtRisk.setText(String.valueOf(atRiskCount));
    }

    /**
     * Safe application logout procedure.
     */
    private void handleLogout() {
        authService.logout();
        this.dispose();
        
        // Re-launch login panel
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginFrame(authService, studentService).setVisible(true);
            }
        });
    }
}
