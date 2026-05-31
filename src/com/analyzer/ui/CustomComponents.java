package com.analyzer.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * VIVA EXPLANATION - OOP CONCEPT: GRAPHICS PROGRAMMING & CUSTOM CONTROLS
 * To create a "wow" UI without external JAR libraries (important for easy compiling in Viva exams),
 * we use AWT and Swing Graphics2D painting.
 * 1. Rendering Hints: We activate ANTIALIASING to render super-smooth curves and vector shapes.
 * 2. Event Listeners: Mouse and focus listeners are attached to repaint hover/active states dynamically.
 * 3. Inheritance: Custom classes extend standard Swing objects (JPanel, JButton, JTextField)
 *    and override paintComponent() to draw customized, modern shapes.
 */
public class CustomComponents {

    // --- Modern Color Palette Tokens ---
    public static final Color COLOR_PRIMARY = new Color(59, 130, 246);      // Indigo Blue
    public static final Color COLOR_SECONDARY = new Color(139, 92, 246);    // Accent Purple
    public static final Color COLOR_SLATE_DARK = new Color(15, 23, 42);     // Deep Slate Dark
    public static final Color COLOR_SLATE_LIGHT = new Color(30, 41, 59);    // Sidebar Active Slate
    public static final Color COLOR_BG_LIGHT = new Color(248, 250, 252);    // Dashboard Background Light Gray
    public static final Color COLOR_CARD_BG = Color.WHITE;                  // Card Background White
    public static final Color COLOR_BORDER = new Color(226, 232, 240);      // Soft Slate border
    public static final Color COLOR_TEXT_DARK = new Color(15, 23, 42);     // Bold text dark gray
    public static final Color COLOR_TEXT_MUTED = new Color(100, 116, 139);  // Muted gray subtext
    
    // Status colors
    public static final Color COLOR_SUCCESS = new Color(34, 197, 94);       // Green
    public static final Color COLOR_WARNING = new Color(234, 179, 8);       // Yellow
    public static final Color COLOR_DANGER = new Color(239, 68, 68);        // Red

    /**
     * A beautiful custom JPanel that paints a linear gradient background.
     */
    public static class GradientPanel extends JPanel {
        private Color startColor;
        private Color endColor;
        private boolean horizontal;

        public GradientPanel(Color startColor, Color endColor, boolean horizontal) {
            this.startColor = startColor;
            this.endColor = endColor;
            this.horizontal = horizontal;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int w = getWidth();
            int h = getHeight();
            
            GradientPaint gp;
            if (horizontal) {
                gp = new GradientPaint(0, 0, startColor, w, 0, endColor);
            } else {
                gp = new GradientPaint(0, 0, startColor, 0, h, endColor);
            }
            
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, h);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * A clean white card component with beautifully rounded corners and elegant thin borders.
     */
    public static class RoundedPanel extends JPanel {
        private int cornerRadius = 16;
        private Color backgroundColor = COLOR_CARD_BG;
        private Color borderColor = COLOR_BORDER;
        private int borderThickness = 1;

        public RoundedPanel(int radius) {
            this.cornerRadius = radius;
            setOpaque(false);
            setBorder(new EmptyBorder(16, 16, 16, 16));
        }

        public RoundedPanel(int radius, Color bg) {
            this.cornerRadius = radius;
            this.backgroundColor = bg;
            setOpaque(false);
            setBorder(new EmptyBorder(16, 16, 16, 16));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int w = getWidth();
            int h = getHeight();
            
            // Draw background card shape
            g2.setColor(backgroundColor);
            g2.fill(new RoundRectangle2D.Double(0, 0, w - 1, h - 1, cornerRadius, cornerRadius));
            
            // Draw crisp modern boundary border
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(borderThickness));
            g2.draw(new RoundRectangle2D.Double(0, 0, w - 1, h - 1, cornerRadius, cornerRadius));
            
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Modern vector rounded button with active mouse-hover background transitions and pointer triggers.
     */
    public static class ModernButton extends JButton {
        private Color baseColor = COLOR_PRIMARY;
        private Color hoverColor = COLOR_PRIMARY.brighter();
        private Color pressedColor = COLOR_PRIMARY.darker();
        private int cornerRadius = 12;
        private boolean isHovered = false;

        public ModernButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
            });
        }

        public ModernButton(String text, Color baseColor, Color hoverColor) {
            this(text);
            this.baseColor = baseColor;
            this.hoverColor = hoverColor;
            this.pressedColor = baseColor.darker();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int w = getWidth();
            int h = getHeight();
            
            // Choose background color based on mouse interaction state (Micro-animations trigger)
            if (getModel().isPressed()) {
                g2.setColor(pressedColor);
            } else if (isHovered) {
                g2.setColor(hoverColor);
            } else {
                g2.setColor(baseColor);
            }
            
            g2.fill(new RoundRectangle2D.Double(0, 0, w, h, cornerRadius, cornerRadius));
            g2.dispose();
            
            super.paintComponent(g);
        }
    }

    /**
     * Vector rounded text input field that highlights with a glowing blue border when focused.
     */
    public static class ModernTextField extends JTextField implements FocusListener {
        private int cornerRadius = 10;
        private Color activeBorderColor = COLOR_PRIMARY;
        private Color inactiveBorderColor = COLOR_BORDER;
        private boolean hasFocus = false;
        private String placeholder = "";

        public ModernTextField(int columns) {
            super(columns);
            initField();
        }

        public ModernTextField(String placeholder, int columns) {
            super(columns);
            this.placeholder = placeholder;
            initField();
        }

        private void initField() {
            setOpaque(false);
            setBorder(new EmptyBorder(10, 14, 10, 14));
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setForeground(COLOR_TEXT_DARK);
            setCaretColor(COLOR_PRIMARY);
            addFocusListener(this);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int w = getWidth();
            int h = getHeight();
            
            // Draw clean background
            g2.setColor(Color.WHITE);
            g2.fill(new RoundRectangle2D.Double(1, 1, w - 2, h - 2, cornerRadius, cornerRadius));
            
            // Draw dynamic border depending on focus state
            if (hasFocus) {
                g2.setColor(activeBorderColor);
                g2.setStroke(new BasicStroke(2.0f));
            } else {
                g2.setColor(inactiveBorderColor);
                g2.setStroke(new BasicStroke(1.0f));
            }
            g2.draw(new RoundRectangle2D.Double(1, 1, w - 2, h - 2, cornerRadius, cornerRadius));
            g2.dispose();
            
            super.paintComponent(g);

            // Draw placeholder if text is empty and not focused
            if (getText().isEmpty() && !hasFocus && !placeholder.isEmpty()) {
                Graphics2D gPlaceholder = (Graphics2D) g.create();
                gPlaceholder.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                gPlaceholder.setColor(COLOR_TEXT_MUTED);
                gPlaceholder.setFont(getFont().deriveFont(Font.ITALIC));
                FontMetrics fm = gPlaceholder.getFontMetrics();
                int x = getInsets().left;
                int y = (h - fm.getHeight()) / 2 + fm.getAscent();
                gPlaceholder.drawString(placeholder, x, y);
                gPlaceholder.dispose();
            }
        }

        @Override
        public void focusGained(FocusEvent e) {
            hasFocus = true;
            repaint();
        }

        @Override
        public void focusLost(FocusEvent e) {
            hasFocus = false;
            repaint();
        }
    }

    /**
     * Modern vector rounded password field matching ModernTextField styling.
     */
    public static class ModernPasswordField extends JPasswordField implements FocusListener {
        private int cornerRadius = 10;
        private Color activeBorderColor = COLOR_PRIMARY;
        private Color inactiveBorderColor = COLOR_BORDER;
        private boolean hasFocus = false;
        private String placeholder = "";

        public ModernPasswordField(int columns) {
            super(columns);
            initField();
        }

        public ModernPasswordField(String placeholder, int columns) {
            super(columns);
            this.placeholder = placeholder;
            initField();
        }

        private void initField() {
            setOpaque(false);
            setBorder(new EmptyBorder(10, 14, 10, 14));
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setForeground(COLOR_TEXT_DARK);
            setCaretColor(COLOR_PRIMARY);
            addFocusListener(this);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int w = getWidth();
            int h = getHeight();
            
            // Draw clean background
            g2.setColor(Color.WHITE);
            g2.fill(new RoundRectangle2D.Double(1, 1, w - 2, h - 2, cornerRadius, cornerRadius));
            
            // Draw active/inactive border
            if (hasFocus) {
                g2.setColor(activeBorderColor);
                g2.setStroke(new BasicStroke(2.0f));
            } else {
                g2.setColor(inactiveBorderColor);
                g2.setStroke(new BasicStroke(1.0f));
            }
            g2.draw(new RoundRectangle2D.Double(1, 1, w - 2, h - 2, cornerRadius, cornerRadius));
            g2.dispose();
            
            super.paintComponent(g);

            // Draw placeholder if empty
            if (getPassword().length == 0 && !hasFocus && !placeholder.isEmpty()) {
                Graphics2D gPlaceholder = (Graphics2D) g.create();
                gPlaceholder.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                gPlaceholder.setColor(COLOR_TEXT_MUTED);
                gPlaceholder.setFont(getFont().deriveFont(Font.ITALIC));
                FontMetrics fm = gPlaceholder.getFontMetrics();
                int x = getInsets().left;
                int y = (h - fm.getHeight()) / 2 + fm.getAscent();
                gPlaceholder.drawString(placeholder, x, y);
                gPlaceholder.dispose();
            }
        }

        @Override
        public void focusGained(FocusEvent e) {
            hasFocus = true;
            repaint();
        }

        @Override
        public void focusLost(FocusEvent e) {
            hasFocus = false;
            repaint();
        }
    }
}
