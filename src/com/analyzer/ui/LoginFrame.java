package com.analyzer.ui;

import com.analyzer.service.AuthService;
import com.analyzer.service.StudentService;
import com.analyzer.ui.CustomComponents.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * VIVA EXPLANATION - OOP CONCEPT: GUI EVENT HANDLING
 * This class builds the security gateway GUI.
 * 1. Layout Managers: Uses BorderLayout and GridBagLayout to position the login card precisely in the center.
 * 2. Event Listeners: Implements ActionListener to capture click events on the "Login" button and trigger authentication.
 * 3. Separation of Concerns: Delegates user validation to the AuthService instead of checking hardcoded values.
 */
public class LoginFrame extends JFrame {
    
    private AuthService authService;
    private StudentService studentService;

    // GUI Components
    private ModernTextField txtUsername;
    private ModernPasswordField txtPassword;
    private JLabel lblError;
    private ModernButton btnLogin;

    public LoginFrame(AuthService authService, StudentService studentService) {
        this.authService = authService;
        this.studentService = studentService;

        setTitle("Genesis Academic Analyzer - Security Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 550);
        setMinimumSize(new Dimension(800, 500));
        setLocationRelativeTo(null); // Center window on screen

        // Set up the beautiful gradient background
        GradientPanel mainPanel = new GradientPanel(
                CustomComponents.COLOR_SLATE_DARK, 
                CustomComponents.COLOR_PRIMARY, 
                false
        );
        mainPanel.setLayout(new GridBagLayout());
        setContentPane(mainPanel);

        // Build the Login Card Panel
        RoundedPanel loginCard = new RoundedPanel(24, CustomComponents.COLOR_CARD_BG);
        loginCard.setPreferredSize(new Dimension(380, 440));
        loginCard.setLayout(new BoxLayout(loginCard, BoxLayout.Y_AXIS));
        loginCard.setBorder(new EmptyBorder(30, 24, 30, 24));

        // Add System Logo Header
        JLabel lblLogo = new JLabel("🎓");
        lblLogo.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel("GENESIS");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(CustomComponents.COLOR_PRIMARY);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Student Performance Analyzer");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(CustomComponents.COLOR_TEXT_MUTED);
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSubtitle.setBorder(new EmptyBorder(2, 0, 20, 0));

        // Username Field Layout
        JLabel lblUser = new JLabel("Username / ID");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUser.setForeground(CustomComponents.COLOR_TEXT_DARK);
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblUser.setBorder(new EmptyBorder(0, 0, 4, 0));

        txtUsername = new ModernTextField("Enter your username", 20);
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtUsername.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtUsername.setText("admin"); // Preload default for quick viva evaluation!

        // Password Field Layout
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPass.setForeground(CustomComponents.COLOR_TEXT_DARK);
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblPass.setBorder(new EmptyBorder(12, 0, 4, 0));

        txtPassword = new ModernPasswordField("Enter password", 20);
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtPassword.setText("admin123"); // Preload default for quick viva evaluation!

        // Error message label
        lblError = new JLabel(" ");
        lblError.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblError.setForeground(CustomComponents.COLOR_DANGER);
        lblError.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblError.setBorder(new EmptyBorder(8, 0, 8, 0));

        // Login Button
        btnLogin = new ModernButton("Sign In Secured");
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Setup Login Event Trigger
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        // Add helper credentials text at the bottom
        JLabel lblHelp = new JLabel("🔑 Default: admin / admin123");
        lblHelp.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblHelp.setForeground(CustomComponents.COLOR_TEXT_MUTED);
        lblHelp.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblHelp.setBorder(new EmptyBorder(15, 0, 0, 0));

        // Assemble Card Components
        loginCard.add(lblLogo);
        loginCard.add(lblTitle);
        loginCard.add(lblSubtitle);
        loginCard.add(lblUser);
        loginCard.add(txtUsername);
        loginCard.add(lblPass);
        loginCard.add(txtPassword);
        loginCard.add(lblError);
        loginCard.add(btnLogin);
        loginCard.add(lblHelp);

        // Add Login Card to Center Grid
        mainPanel.add(loginCard);
    }

    /**
     * Logic to handle credentials verification.
     */
    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("⚠️ Please fill in all fields.");
            return;
        }

        // Authenticate via AuthService
        if (authService.authenticate(username, password)) {
            lblError.setText(" ");
            // Close login window and launch dashboard!
            this.dispose();
            
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    DashboardFrame dashboard = new DashboardFrame(authService, studentService);
                    dashboard.setVisible(true);
                }
            });
        } else {
            lblError.setText("❌ Invalid username or password.");
            txtPassword.setText("");
        }
    }
}
