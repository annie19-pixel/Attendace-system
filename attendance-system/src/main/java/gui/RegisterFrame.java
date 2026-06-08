package gui;

import Model.Lecturer;
import Model.Student;
import Model.User;
import service.AuthService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ItemEvent;

/**
 * RegisterFrame — self-registration for all user types.
 *
 * FIX: Replaced BoxLayout + JScrollPane (which caused the blank window)
 * with a stable GridBagLayout form. GridBagLayout correctly calculates
 * preferred sizes so Swing can render the panel.
 *
 * Role dropdown (Student / Lecturer / Admin) dynamically shows/hides
 * role-specific fields. Window resizes and re-centres on role change.
 */
public class RegisterFrame extends JFrame {

    private final AuthService authService = new AuthService();

    // ── Always-visible fields ─────────────────────────────────
    private JTextField        fullNameField;
    private JTextField        emailField;
    private JPasswordField    passwordField;
    private JPasswordField    confirmField;
    private JComboBox<String> roleCombo;

    // ── Student-only fields ───────────────────────────────────
    private JTextField regNoField;
    private JTextField yearField;
    private JTextField streamField;

    // ── Lecturer-only fields ──────────────────────────────────
    private JTextField staffNoField;
    private JTextField deptField;

    // ── Feedback label ────────────────────────────────────────
    private JLabel messageLabel;

    // ── The form panel — rebuilt on every role change ─────────
    private JPanel   formPanel;
    private JPanel   mainPanel;

    public RegisterFrame() {
        buildUI();
    }

    // ─────────────────────────────────────────────────────────
    // WINDOW SHELL
    // Only the fixed pieces (title bar, bottom buttons) live here.
    // The form in the middle is rebuilt by rebuildForm().
    // ─────────────────────────────────────────────────────────
    private void buildUI() {
        setTitle("Register — Attendance System");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        mainPanel = new JPanel(new BorderLayout(10, 12));
        mainPanel.setBorder(new EmptyBorder(20, 35, 15, 35));

        // ── Title ──
        JLabel title = new JLabel("Create an Account", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 17));
        mainPanel.add(title, BorderLayout.NORTH);

        // ── Role combo lives above the dynamic form so it is always visible ──
        JPanel roleRow = new JPanel(new GridLayout(2, 1, 0, 4));
        JLabel roleLabel = new JLabel("Role");
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        roleCombo = new JComboBox<>(new String[]{"Student", "Lecturer", "Admin"});
        roleCombo.setFont(new Font("Arial", Font.PLAIN, 13));
        roleRow.add(roleLabel);
        roleRow.add(roleCombo);

        // ── Wrapper holds role row + dynamic form together ──
        JPanel centreWrapper = new JPanel(new BorderLayout(0, 8));
        centreWrapper.add(roleRow,   BorderLayout.NORTH);

        // Build the initial form for "Student"
        formPanel = buildForm("Student");
        centreWrapper.add(formPanel, BorderLayout.CENTER);

        mainPanel.add(centreWrapper, BorderLayout.CENTER);

        // ── Bottom: message + Register + Back ──
        JPanel bottom = new JPanel(new GridLayout(3, 1, 5, 5));

        messageLabel = new JLabel(" ", SwingConstants.CENTER);
        messageLabel.setForeground(Color.RED);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JButton registerBtn = new JButton("Register");
        registerBtn.setFont(new Font("Arial", Font.BOLD, 14));
        registerBtn.addActionListener(e -> handleRegister());

        JButton backBtn = new JButton("Back to Login");
        backBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        backBtn.addActionListener(e -> goToLogin());

        bottom.add(messageLabel);
        bottom.add(registerBtn);
        bottom.add(backBtn);
        mainPanel.add(bottom, BorderLayout.SOUTH);

        add(mainPanel);

        // Size the window to fit the initial (Student) form
        pack();
        setMinimumSize(new Dimension(420, getHeight()));
        setLocationRelativeTo(null);

        // ── Role change listener — swaps the form panel ──
        roleCombo.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String selected = (String) roleCombo.getSelectedItem();
                centreWrapper.remove(formPanel);
                formPanel = buildForm(selected);
                centreWrapper.add(formPanel, BorderLayout.CENTER);
                messageLabel.setText(" ");
                centreWrapper.revalidate();
                centreWrapper.repaint();
                pack();
                setLocationRelativeTo(null);
            }
        });
    }

    // ─────────────────────────────────────────────────────────
    // FORM BUILDER
    // Returns a freshly built GridBagLayout panel for the given role.
    // GridBagLayout correctly reports preferred size — no scroll pane needed.
    // ─────────────────────────────────────────────────────────
    private JPanel buildForm(String role) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets    = new Insets(3, 0, 3, 0);
        gbc.fill      = GridBagConstraints.HORIZONTAL;
        gbc.weightx   = 1.0;
        gbc.gridx     = 0;
        int row = 0;

        // Full Name
        gbc.gridy = row++;
        panel.add(lbl("Full Name"), gbc);
        gbc.gridy = row++;
        fullNameField = fullNameField != null ? fullNameField : tf();
        panel.add(fullNameField, gbc);

        // Email
        gbc.gridy = row++;
        panel.add(lbl("Email"), gbc);
        gbc.gridy = row++;
        emailField = emailField != null ? emailField : tf();
        panel.add(emailField, gbc);

        // Password
        gbc.gridy = row++;
        panel.add(lbl("Password  (min 6 characters)"), gbc);
        gbc.gridy = row++;
        passwordField = passwordField != null ? passwordField : pf();
        panel.add(passwordField, gbc);

        // Confirm Password
        gbc.gridy = row++;
        panel.add(lbl("Confirm Password"), gbc);
        gbc.gridy = row++;
        confirmField = confirmField != null ? confirmField : pf();
        panel.add(confirmField, gbc);

        // ── Role-specific extra fields ──
        switch (role) {

            case "Student":
                gbc.gridy = row++; panel.add(lbl("Registration Number  (e.g. 24/06623)"), gbc);
                gbc.gridy = row++; regNoField = tf(); panel.add(regNoField, gbc);

                gbc.gridy = row++; panel.add(lbl("Year of Study  (e.g. Year 2)"), gbc);
                gbc.gridy = row++; yearField = tf(); panel.add(yearField, gbc);

                gbc.gridy = row++; panel.add(lbl("Stream  (e.g. Morning / Evening)"), gbc);
                gbc.gridy = row++; streamField = tf(); panel.add(streamField, gbc);
                break;

            case "Lecturer":
                gbc.gridy = row++; panel.add(lbl("Staff Number  (e.g. LCT/24/001)"), gbc);
                gbc.gridy = row++; staffNoField = tf(); panel.add(staffNoField, gbc);

                gbc.gridy = row++; panel.add(lbl("Department"), gbc);
                gbc.gridy = row++; deptField = tf(); panel.add(deptField, gbc);
                break;

            case "Admin":
                gbc.gridy = row++;
                JLabel note = new JLabel("No additional details required for Admin.");
                note.setFont(new Font("Arial", Font.ITALIC, 11));
                note.setForeground(Color.GRAY);
                panel.add(note, gbc);
                break;
        }

        return panel;
    }

    // ─────────────────────────────────────────────────────────
    // HANDLE REGISTRATION
    // ─────────────────────────────────────────────────────────
    private void handleRegister() {
        String fullName = fullNameField.getText().trim();
        String email    = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String confirm  = new String(confirmField.getPassword()).trim();
        String role     = ((String) roleCombo.getSelectedItem()).toLowerCase();

        // ── Common validation ──
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError("Full name, email and password are required."); return;
        }
        if (!email.contains("@") || !email.contains(".")) {
            showError("Please enter a valid email address."); return;
        }
        if (password.length() < 6) {
            showError("Password must be at least 6 characters."); return;
        }
        if (!password.equals(confirm)) {
            showError("Passwords do not match.");
            confirmField.setText(""); return;
        }

        // ── Role-specific registration ──
        User    user    = new User(fullName, email, password, role);
        boolean success;

        switch (role) {
            case "student": {
                String regNo  = regNoField  != null ? regNoField.getText().trim()  : "";
                String year   = yearField   != null ? yearField.getText().trim()   : "";
                String stream = streamField != null ? streamField.getText().trim() : "";
                if (regNo.isEmpty() || year.isEmpty() || stream.isEmpty()) {
                    showError("Reg no, year of study and stream are required."); return;
                }
                success = authService.registerStudent(user, new Student(regNo, 0, fullName, year, stream));
                break;
            }
            case "lecturer": {
                String staffNo = staffNoField != null ? staffNoField.getText().trim() : "";
                String dept    = deptField    != null ? deptField.getText().trim()    : "";
                if (staffNo.isEmpty() || dept.isEmpty()) {
                    showError("Staff number and department are required."); return;
                }
                success = authService.registerLecturer(user, new Lecturer(staffNo, 0, fullName, dept));
                break;
            }
            case "admin":
                success = authService.registerAdmin(user);
                break;
            default:
                showError("Unknown role selected."); return;
        }

        if (success) {
            JOptionPane.showMessageDialog(
                    this,
                    "Registration successful!\nYou can now log in with your email and password.",
                    "Account Created",
                    JOptionPane.INFORMATION_MESSAGE
            );
            goToLogin();
        } else {
            showError("Registration failed. Email or ID may already be taken.");
        }
    }

    // ─────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────
    private void goToLogin() {
        dispose();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    private void showError(String msg) {
        messageLabel.setForeground(Color.RED);
        messageLabel.setText(msg);
    }

    /** Label styled for form field headings */
    private JLabel lbl(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.PLAIN, 12));
        return l;
    }

    /** Standard text field */
    private JTextField tf() {
        JTextField f = new JTextField();
        f.setFont(new Font("Arial", Font.PLAIN, 13));
        f.setPreferredSize(new Dimension(340, 28));
        return f;
    }

    /** Password field */
    private JPasswordField pf() {
        JPasswordField f = new JPasswordField();
        f.setFont(new Font("Arial", Font.PLAIN, 13));
        f.setPreferredSize(new Dimension(340, 28));
        return f;
    }
}