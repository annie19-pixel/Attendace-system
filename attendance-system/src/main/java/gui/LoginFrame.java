package gui;

import Model.Lecturer;
import Model.Student;
import Model.User;
import service.AuthService;

import javax.swing.*;
import java.awt.*;

/**
 * LoginFrame — entry point for the whole application.
 *
 * Changes from previous version:
 *  - "admin"    case now opens AdminDashboard(user)
 *  - "student"  case fetches Student profile then opens StudentDashboard(user, student)
 *  - "lecturer" case fetches Lecturer profile then opens LecturerDashboard(user, lecturer)
 *  - Removed all JOptionPane placeholders
 */
public class LoginFrame extends JFrame {

    private final AuthService authService;
    private JTextField    emailField;
    private JPasswordField passwordField;
    private JLabel        messageLabel;

    public LoginFrame() {
        this.authService = new AuthService();
        buildUI();
    }

    private void buildUI() {
        setTitle("Attendance Management System");
        setSize(420, 280);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel titleLabel = new JLabel("Attendance Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        formPanel.add(emailField);
        formPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setForeground(Color.RED);
        bottomPanel.add(loginButton);
        bottomPanel.add(messageLabel);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        loginButton.addActionListener(e -> handleLogin());
        passwordField.addActionListener(e -> handleLogin());
    }

    private void handleLogin() {
        String email    = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter both email and password");
            return;
        }

        User user = authService.login(email, password);

        if (user == null) {
            messageLabel.setText("Invalid email or password");
            passwordField.setText("");
            return;
        }

        // Login OK — close login window, open the right dashboard
        dispose();

        switch (user.getRole()) {

            case "admin":
                SwingUtilities.invokeLater(() ->
                        new AdminDashboard(user).setVisible(true)
                );
                break;

            case "student":
                // Fetch the extended student profile (reg_no, year, stream)
                Student studentProfile = authService.getStudentProfile(user);
                SwingUtilities.invokeLater(() ->
                        new StudentDashboard(user, studentProfile).setVisible(true)
                );
                break;

            case "lecturer":
                // Fetch the extended lecturer profile (staff_no, department)
                Lecturer lecturerProfile = authService.getLecturerProfile(user);
                SwingUtilities.invokeLater(() ->
                        new LecturerDashboard(user, lecturerProfile).setVisible(true)
                );
                break;

            default:
                // Shouldn't happen if database roles are correct
                new LoginFrame().setVisible(true);
                JOptionPane.showMessageDialog(null,
                        "Unknown role '" + user.getRole() + "'. Contact admin.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}