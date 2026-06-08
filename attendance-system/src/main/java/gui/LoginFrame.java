package gui;

import Model.Lecturer;
import Model.Student;
import Model.User;
import service.AuthService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * LoginFrame — application entry point.
 *
 * "Don't have an account? Register here" link opens RegisterFrame
 * for all user types (Student, Lecturer, Admin).
 */
public class LoginFrame extends JFrame {

    private final AuthService  authService;
    private JTextField         emailField;
    private JPasswordField     passwordField;
    private JLabel             messageLabel;

    public LoginFrame() {
        this.authService = new AuthService();
        buildUI();
    }

    private void buildUI() {
        setTitle("Attendance Management System");
        setSize(420, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // ── Title ──
        JLabel titleLabel = new JLabel("Attendance Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        main.add(titleLabel, BorderLayout.NORTH);

        // ── Form ──
        JPanel form = new JPanel(new GridLayout(4, 1, 5, 5));
        form.add(new JLabel("Email:"));
        emailField = new JTextField();
        form.add(emailField);
        form.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        form.add(passwordField);
        main.add(form, BorderLayout.CENTER);

        // ── Bottom: login button + error + register link ──
        JPanel bottom = new JPanel(new GridLayout(3, 1, 5, 5));

        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));

        messageLabel = new JLabel(" ", SwingConstants.CENTER);
        messageLabel.setForeground(Color.RED);

        // Register link — works for all roles
        JLabel registerLink = new JLabel("Don't have an account? Register here", SwingConstants.CENTER);
        registerLink.setFont(new Font("Arial", Font.PLAIN, 12));
        registerLink.setForeground(new Color(0x1A6FD4));
        registerLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                SwingUtilities.invokeLater(() -> new RegisterFrame().setVisible(true));
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                registerLink.setText("<html><u>Don't have an account? Register here</u></html>");
            }
            @Override
            public void mouseExited(MouseEvent e) {
                registerLink.setText("Don't have an account? Register here");
            }
        });

        bottom.add(loginButton);
        bottom.add(messageLabel);
        bottom.add(registerLink);
        main.add(bottom, BorderLayout.SOUTH);

        add(main);

        loginButton.addActionListener(e -> handleLogin());
        passwordField.addActionListener(e -> handleLogin());
    }

    // ─────────────────────────────────────────────────────────
    // HANDLE LOGIN
    // ─────────────────────────────────────────────────────────
    private void handleLogin() {
        String email    = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter both email and password.");
            return;
        }

        User user = authService.login(email, password);

        if (user == null) {
            messageLabel.setText("Invalid email or password.");
            passwordField.setText("");
            return;
        }

        dispose();

        switch (user.getRole()) {
            case "admin":
                SwingUtilities.invokeLater(() ->
                        new AdminDashboard(user).setVisible(true));
                break;

            case "student":
                Student studentProfile = authService.getStudentProfile(user);
                SwingUtilities.invokeLater(() ->
                        new StudentDashboard(user, studentProfile).setVisible(true));
                break;

            case "lecturer":
                Lecturer lecturerProfile = authService.getLecturerProfile(user);
                SwingUtilities.invokeLater(() ->
                        new LecturerDashboard(user, lecturerProfile).setVisible(true));
                break;

            default:
                new LoginFrame().setVisible(true);
                JOptionPane.showMessageDialog(null,
                        "Unknown role '" + user.getRole() + "'. Contact admin.");
        }
    }

    // ─────────────────────────────────────────────────────────
    // MAIN — application entry point
    // ─────────────────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}