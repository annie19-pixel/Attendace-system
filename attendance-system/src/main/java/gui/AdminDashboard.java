package gui;

import Model.Enrollment;
import Model.Lecturer;
import Model.Student;
import Model.Unit;
import Model.User;
import dao.EnrollmentDAO;
import dao.LecturerDAO;
import dao.StudentDAO;
import dao.UnitDAO;
import dao.UserDAO;
import service.AuthService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;


/**
 * AdminDashboard — one class, all admin panels inside via CardLayout.
 *
 * KEY FIX: Constructor now accepts a User parameter so LoginFrame can
 * call  new AdminDashboard(user)  after a successful admin login.
 *
 * Panels:
 *  OVERVIEW  — live stat cards (students / lecturers / units / users)
 *  STUDENTS  — table + register new student dialog
 *  LECTURERS — table + register new lecturer dialog
 *  UNITS     — table + add new unit dialog
 *  USERS     — read-only table of all accounts
 */
public class AdminDashboard extends JFrame {

    // ── Colour palette ───────────────────────────────────────
    private static final Color BG         = new Color(0x0F1117);
    private static final Color SIDEBAR_BG = new Color(0x161B27);
    private static final Color CARD_BG    = new Color(0x1E2435);
    private static final Color ACCENT     = new Color(0x4F8EF7);
    private static final Color ACCENT2    = new Color(0x38D9A9);
    private static final Color TEXT       = new Color(0xE8EAF0);
    private static final Color MUTED      = new Color(0x7B82A0);
    private static final Color DANGER     = new Color(0xFF5C6A);
    private static final Color BORDER     = new Color(0x2A3050);
    private static final Color WARN       = new Color(0xF7A94F);

    private static final Font TITLE_FONT  = new Font("SansSerif", Font.BOLD, 22);
    private static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 14);
    private static final Font BODY_FONT   = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font SMALL_FONT  = new Font("SansSerif", Font.PLAIN, 11);

    // ── Services / DAOs ──────────────────────────────────────
    private final User        adminUser;
    private final AuthService authService = new AuthService();
    private final UserDAO     userDAO     = new UserDAO();
    private final StudentDAO  studentDAO  = new StudentDAO();
    private final LecturerDAO lecturerDAO = new LecturerDAO();
    private final UnitDAO     unitDAO     = new UnitDAO();
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();

    // ── CardLayout panels ────────────────────────────────────
    private CardLayout cardLayout;
    private JPanel     contentPanel;

    private static final String PANEL_OVERVIEW    = "OVERVIEW";
    private static final String PANEL_STUDENTS    = "STUDENTS";
    private static final String PANEL_LECTURERS   = "LECTURERS";
    private static final String PANEL_UNITS       = "UNITS";
    private static final String PANEL_USERS       = "USERS";
    private static final String PANEL_ENROLLMENTS = "ENROLLMENTS";

    // ── Table models ─────────────────────────────────────────
    private DefaultTableModel studentsModel;
    private DefaultTableModel lecturersModel;
    private DefaultTableModel unitsModel;
    private DefaultTableModel usersModel;
    private DefaultTableModel enrollmentsModel;

    // ── Overview stat labels ─────────────────────────────────
    private JLabel statStudents;
    private JLabel statLecturers;
    private JLabel statUnits;
    private JLabel statUsers;

    // ────────────────────────────────────────────────────────
    // CONSTRUCTOR  — fixed: was AdminDashboard() with no param
    // ────────────────────────────────────────────────────────
    public AdminDashboard(User user) {
        this.adminUser = user;
        buildWindow();
    }

    private void buildWindow() {
        setTitle("Admin Dashboard — Attendance System");
        setSize(1150, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildContent(), BorderLayout.CENTER);
        add(root);
    }

    // ─────────────────────────────────────────────────────────
    // SIDEBAR
    // ─────────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sb = new JPanel();
        sb.setBackground(SIDEBAR_BG);
        sb.setPreferredSize(new Dimension(220, 0));
        sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));
        sb.setBorder(new EmptyBorder(30, 0, 20, 0));

        sb.add(sideLabel("  AttendMS", new Font("SansSerif", Font.BOLD, 18), ACCENT,
                new EmptyBorder(0, 20, 8, 0)));
        sb.add(sideLabel("  ADMIN", SMALL_FONT, ACCENT2,
                new EmptyBorder(0, 20, 24, 0)));
        sb.add(divider());

        String[][] nav = {
                {"Overview",     PANEL_OVERVIEW},
                {"Students",     PANEL_STUDENTS},
                {"Lecturers",    PANEL_LECTURERS},
                {"Units",        PANEL_UNITS},
                {"Enrollments",  PANEL_ENROLLMENTS},
                {"Users",        PANEL_USERS}
        };
        for (String[] item : nav) sb.add(navBtn(item[0], item[1]));

        sb.add(Box.createVerticalGlue());
        sb.add(divider());
        sb.add(sideLabel("  " + adminUser.getFullName(), BODY_FONT, TEXT,
                new EmptyBorder(12, 20, 4, 0)));
        sb.add(sideLabel("  " + adminUser.getEmail(), SMALL_FONT, MUTED,
                new EmptyBorder(0, 20, 10, 0)));

        JButton logoutBtn = new JButton("Logout");
        styleBtn(logoutBtn, DANGER, Color.WHITE);
        logoutBtn.addActionListener(e -> logout());
        JPanel lw = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lw.setBackground(SIDEBAR_BG);
        lw.add(logoutBtn);
        sb.add(lw);

        return sb;
    }

    private JLabel sideLabel(String text, Font font, Color fg, EmptyBorder border) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(fg);
        l.setAlignmentX(LEFT_ALIGNMENT);
        l.setBorder(border);
        return l;
    }

    private JButton navBtn(String label, String panel) {
        JButton btn = new JButton("  " + label);
        btn.setFont(BODY_FONT);
        btn.setForeground(MUTED);
        btn.setBackground(SIDEBAR_BG);
        btn.setBorder(new EmptyBorder(12, 20, 12, 0));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(220, 45));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setForeground(TEXT); }
            public void mouseExited(java.awt.event.MouseEvent e)  { btn.setForeground(MUTED); }
        });
        btn.addActionListener(e -> {
            cardLayout.show(contentPanel, panel);
            refreshPanel(panel);
        });
        return btn;
    }

    private JSeparator divider() {
        JSeparator s = new JSeparator();
        s.setForeground(BORDER);
        s.setMaximumSize(new Dimension(220, 1));
        return s;
    }

    // ─────────────────────────────────────────────────────────
    // CONTENT AREA
    // ─────────────────────────────────────────────────────────
    private JPanel buildContent() {
        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG);

        contentPanel.add(buildOverviewPanel(),     PANEL_OVERVIEW);
        contentPanel.add(buildStudentsPanel(),     PANEL_STUDENTS);
        contentPanel.add(buildLecturersPanel(),    PANEL_LECTURERS);
        contentPanel.add(buildUnitsPanel(),        PANEL_UNITS);
        contentPanel.add(buildEnrollmentsPanel(),  PANEL_ENROLLMENTS);
        contentPanel.add(buildUsersPanel(),        PANEL_USERS);

        cardLayout.show(contentPanel, PANEL_OVERVIEW);
        return contentPanel;
    }

    // ── Overview ──────────────────────────────────────────────
    private JPanel buildOverviewPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 24));
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("Welcome, " + adminUser.getFullName());
        title.setFont(TITLE_FONT);
        title.setForeground(TEXT);
        p.add(title, BorderLayout.NORTH);

        JPanel row = new JPanel(new GridLayout(1, 4, 16, 0));
        row.setBackground(BG);

        statStudents  = bigLabel("0", ACCENT);
        statLecturers = bigLabel("0", ACCENT2);
        statUnits     = bigLabel("0", WARN);
        statUsers     = bigLabel("0", new Color(0xB57BF7));

        row.add(statCard("Students",  statStudents));
        row.add(statCard("Lecturers", statLecturers));
        row.add(statCard("Units",     statUnits));
        row.add(statCard("Users",     statUsers));

        p.add(row, BorderLayout.CENTER);
        refreshOverview(); // populate counts immediately
        return p;
    }

    private JLabel bigLabel(String text, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 36));
        l.setForeground(color);
        return l;
    }

    private JPanel statCard(String label, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(20, 20, 20, 20)));
        JLabel lbl = new JLabel(label.toUpperCase());
        lbl.setFont(SMALL_FONT);
        lbl.setForeground(MUTED);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(lbl, BorderLayout.SOUTH);
        return card;
    }

    private void refreshOverview() {
        statStudents.setText(String.valueOf(studentDAO.getAllStudents().size()));
        statLecturers.setText(String.valueOf(lecturerDAO.getAllLecturers().size()));
        statUnits.setText(String.valueOf(unitDAO.getAllUnits().size()));
        statUsers.setText(String.valueOf(userDAO.getAllUsers().size()));
    }

    // ── Students ──────────────────────────────────────────────
    private JPanel buildStudentsPanel() {
        JPanel p = contentPanel("Students");
        JButton addBtn = new JButton("+ Register Student");
        styleBtn(addBtn, ACCENT, Color.WHITE);
        addBtn.addActionListener(e -> showRegisterStudentDialog());
        headerOf(p).add(addBtn, BorderLayout.EAST);

        String[] cols = {"Reg No", "Name", "Year", "Stream", "User ID"};
        studentsModel = blankModel(cols);
        p.add(scrolledTable(studentsModel), BorderLayout.CENTER);
        return p;
    }

    private void refreshStudentsPanel() {
        studentsModel.setRowCount(0);
        for (Student s : studentDAO.getAllStudents())
            studentsModel.addRow(new Object[]{
                    s.getRegNo(), s.getStudentName(), s.getYearOfStudy(), s.getStream(), s.getUserId()
            });
    }

    private void showRegisterStudentDialog() {
        JDialog dlg  = dialog("Register New Student", 420, 490);
        JPanel  form = dialogForm();

        JTextField     nameF   = field();
        JTextField     emailF  = field();
        JPasswordField passF   = passField();
        JTextField     regNoF  = field();
        JTextField     yearF   = field();
        JTextField     streamF = field();

        addRow(form, "Full Name",     nameF);
        addRow(form, "Email",         emailF);
        addRow(form, "Password",      passF);
        addRow(form, "Reg No",        regNoF);
        addRow(form, "Year of Study", yearF);
        addRow(form, "Stream",        streamF);

        JLabel msg    = msgLabel();
        JButton submit = new JButton("Register");
        styleBtn(submit, ACCENT, Color.WHITE);
        form.add(msg);
        form.add(submit);

        submit.addActionListener(e -> {
            String name   = nameF.getText().trim(),  email  = emailF.getText().trim();
            String pass   = new String(passF.getPassword()).trim();
            String regNo  = regNoF.getText().trim(), year   = yearF.getText().trim();
            String stream = streamF.getText().trim();

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || regNo.isEmpty()) {
                msg.setText("Name, email, password and reg no are required."); return;
            }
            User    u = new User(name, email, pass, "student");
            Student s = new Student(regNo, 0, name, year, stream);
            if (authService.registerStudent(u, s)) {
                dlg.dispose();
                refreshStudentsPanel();
                refreshOverview();
                JOptionPane.showMessageDialog(this, "Student registered successfully.");
            } else {
                msg.setText("Failed — email or reg no may already exist.");
            }
        });

        dlg.add(form);
        dlg.setVisible(true);
    }

    // ── Lecturers ─────────────────────────────────────────────
    private JPanel buildLecturersPanel() {
        JPanel p = contentPanel("Lecturers");
        JButton addBtn = new JButton("+ Register Lecturer");
        styleBtn(addBtn, ACCENT2, Color.WHITE);
        addBtn.addActionListener(e -> showRegisterLecturerDialog());
        headerOf(p).add(addBtn, BorderLayout.EAST);

        String[] cols = {"Staff No", "Name", "Department", "User ID"};
        lecturersModel = blankModel(cols);
        p.add(scrolledTable(lecturersModel), BorderLayout.CENTER);
        return p;
    }

    private void refreshLecturersPanel() {
        lecturersModel.setRowCount(0);
        for (Lecturer l : lecturerDAO.getAllLecturers())
            lecturersModel.addRow(new Object[]{
                    l.getStaffNo(), l.getLecturerName(), l.getDepartment(), l.getUserId()
            });
    }

    private void showRegisterLecturerDialog() {
        JDialog dlg  = dialog("Register New Lecturer", 420, 400);
        JPanel  form = dialogForm();

        JTextField     nameF    = field();
        JTextField     emailF   = field();
        JPasswordField passF    = passField();
        JTextField     staffNoF = field();
        JTextField     deptF    = field();

        addRow(form, "Full Name",  nameF);
        addRow(form, "Email",      emailF);
        addRow(form, "Password",   passF);
        addRow(form, "Staff No",   staffNoF);
        addRow(form, "Department", deptF);

        JLabel  msg    = msgLabel();
        JButton submit = new JButton("Register");
        styleBtn(submit, ACCENT2, Color.WHITE);
        form.add(msg);
        form.add(submit);

        submit.addActionListener(e -> {
            String name    = nameF.getText().trim(),    email   = emailF.getText().trim();
            String pass    = new String(passF.getPassword()).trim();
            String staffNo = staffNoF.getText().trim(), dept    = deptF.getText().trim();

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || staffNo.isEmpty()) {
                msg.setText("Name, email, password and staff no are required."); return;
            }
            User     u = new User(name, email, pass, "lecturer");
            Lecturer l = new Lecturer(staffNo, 0, name, dept);
            if (authService.registerLecturer(u, l)) {
                dlg.dispose();
                refreshLecturersPanel();
                refreshOverview();
                JOptionPane.showMessageDialog(this, "Lecturer registered successfully.");
            } else {
                msg.setText("Failed — email or staff no may already exist.");
            }
        });

        dlg.add(form);
        dlg.setVisible(true);
    }

    // ── Units ─────────────────────────────────────────────────
    private JPanel buildUnitsPanel() {
        JPanel p = contentPanel("Units");
        JButton addBtn = new JButton("+ Add Unit");
        styleBtn(addBtn, WARN, Color.WHITE);
        addBtn.addActionListener(e -> showAddUnitDialog());
        headerOf(p).add(addBtn, BorderLayout.EAST);

        String[] cols = {"Unit ID", "Unit Code", "Unit Name"};
        unitsModel = blankModel(cols);
        p.add(scrolledTable(unitsModel), BorderLayout.CENTER);
        return p;
    }

    private void refreshUnitsPanel() {
        unitsModel.setRowCount(0);
        for (Unit u : unitDAO.getAllUnits())
            unitsModel.addRow(new Object[]{u.getUnitId(), u.getUnitCode(), u.getUnitName()});
    }

    private void showAddUnitDialog() {
        JDialog dlg  = dialog("Add New Unit", 360, 220);
        JPanel  form = dialogForm();

        JTextField codeF = field();
        JTextField nameF = field();
        addRow(form, "Unit Code", codeF);
        addRow(form, "Unit Name", nameF);

        JLabel  msg    = msgLabel();
        JButton submit = new JButton("Add Unit");
        styleBtn(submit, WARN, Color.WHITE);
        form.add(msg);
        form.add(submit);

        submit.addActionListener(e -> {
            String code = codeF.getText().trim(), name = nameF.getText().trim();
            if (code.isEmpty() || name.isEmpty()) { msg.setText("Both fields are required."); return; }
            if (unitDAO.addUnit(new Unit(0, code, name))) {
                dlg.dispose();
                refreshUnitsPanel();
                refreshOverview();
            } else {
                msg.setText("Failed — unit code may already exist.");
            }
        });

        dlg.add(form);
        dlg.setVisible(true);
    }

    // ── Users ─────────────────────────────────────────────────
    private JPanel buildUsersPanel() {
        JPanel p = contentPanel("All Users");
        String[] cols = {"User ID", "Full Name", "Email", "Role"};
        usersModel = blankModel(cols);
        p.add(scrolledTable(usersModel), BorderLayout.CENTER);
        return p;
    }

    private void refreshUsersPanel() {
        usersModel.setRowCount(0);
        for (User u : userDAO.getAllUsers())
            usersModel.addRow(new Object[]{u.getUserId(), u.getFullName(), u.getEmail(), u.getRole()});
    }

    // ── Enrollments ───────────────────────────────────────────
    private JPanel buildEnrollmentsPanel() {
        JPanel p = contentPanel("Enrollments");

        JButton addBtn = new JButton("+ Add Enrollment");
        styleBtn(addBtn, new Color(0xB57BF7), Color.WHITE);
        addBtn.addActionListener(e -> showAddEnrollmentDialog());
        headerOf(p).add(addBtn, BorderLayout.EAST);

        // Columns: enrollment_id | reg_no | unit code | unit name | staff_no | semester | year
        String[] cols = {"ID", "Reg No", "Unit Code", "Unit Name", "Staff No", "Semester", "Academic Year"};
        enrollmentsModel = blankModel(cols);
        p.add(scrolledTable(enrollmentsModel), BorderLayout.CENTER);
        return p;
    }

    private void refreshEnrollmentsPanel() {
        enrollmentsModel.setRowCount(0);

        List<Unit> allUnits = unitDAO.getAllUnits();

        for (Student s : studentDAO.getAllStudents()) {
            for (Enrollment en : enrollmentDAO.getEnrollmentsByStudent(s.getRegNo())) {
                String unitCode = String.valueOf(en.getUnitId());
                String unitName = "-";
                for (Unit u : allUnits) {
                    if (u.getUnitId() == en.getUnitId()) {
                        unitCode = u.getUnitCode();
                        unitName = u.getUnitName();
                        break;
                    }
                }
                enrollmentsModel.addRow(new Object[]{
                        en.getEnrollmentId(), en.getRegNo(),
                        unitCode, unitName,
                        en.getStaffNo(), en.getSemester(), en.getAcademicYear()
                });
            }
        }
    }

    private void showAddEnrollmentDialog() {
        JDialog dlg  = dialog("Add Enrollment", 420, 380);
        JPanel  form = dialogForm();

        // Dropdowns populated from the database so admin picks from real data
        // ── Student dropdown ──
        List<Student> students = studentDAO.getAllStudents();
        String[] studentItems = students.stream()
                .map(s -> s.getRegNo() + " — " + s.getStudentName())
                .toArray(String[]::new);
        JComboBox<String> studentCombo = new JComboBox<>(studentItems);
        styleCombo(studentCombo);

        // ── Unit dropdown ──
        List<Unit> units = unitDAO.getAllUnits();
        String[] unitItems = units.stream()
                .map(u -> u.getUnitId() + " — " + u.getUnitCode() + " " + u.getUnitName())
                .toArray(String[]::new);
        JComboBox<String> unitCombo = new JComboBox<>(unitItems);
        styleCombo(unitCombo);

        // ── Lecturer dropdown ──
        List<Lecturer> lecturers = lecturerDAO.getAllLecturers();
        String[] lecturerItems = lecturers.stream()
                .map(l -> l.getStaffNo() + " — " + l.getLecturerName())
                .toArray(String[]::new);
        JComboBox<String> lecturerCombo = new JComboBox<>(lecturerItems);
        styleCombo(lecturerCombo);

        JTextField semesterField     = field();
        JTextField academicYearField = field();

        addRow(form, "Student",       studentCombo);
        addRow(form, "Unit",          unitCombo);
        addRow(form, "Lecturer",      lecturerCombo);
        addRow(form, "Semester  (e.g. Sem 1)", semesterField);
        addRow(form, "Academic Year  (e.g. 2024/2025)", academicYearField);

        JLabel  msg    = msgLabel();
        JButton submit = new JButton("Enroll");
        styleBtn(submit, new Color(0xB57BF7), Color.WHITE);
        form.add(msg);
        form.add(submit);

        submit.addActionListener(e -> {
            // Guard: check lists are not empty before trying to read selection
            if (students.isEmpty() || units.isEmpty() || lecturers.isEmpty()) {
                msg.setText("Make sure students, units and lecturers exist first."); return;
            }

            String semester     = semesterField.getText().trim();
            String academicYear = academicYearField.getText().trim();
            if (semester.isEmpty() || academicYear.isEmpty()) {
                msg.setText("Semester and academic year are required."); return;
            }

            // Parse the selected reg_no, unit_id, staff_no from the combo labels
            String regNo   = ((String) studentCombo.getSelectedItem()).split(" — ")[0].trim();
            int    unitId  = Integer.parseInt(((String) unitCombo.getSelectedItem()).split(" — ")[0].trim());
            String staffNo = ((String) lecturerCombo.getSelectedItem()).split(" — ")[0].trim();

            Enrollment en = new Enrollment(0, regNo, unitId, staffNo, semester, academicYear);
            boolean ok = enrollmentDAO.addEnrollment(en);

            if (ok) {
                dlg.dispose();
                refreshEnrollmentsPanel();
                JOptionPane.showMessageDialog(this, "Student enrolled successfully.");
            } else {
                msg.setText("Enrollment failed. It may already exist.");
            }
        });

        dlg.add(form);
        dlg.setVisible(true);
    }

    // ─────────────────────────────────────────────────────────
    // REFRESH DISPATCHER
    // ─────────────────────────────────────────────────────────
    private void refreshPanel(String name) {
        switch (name) {
            case PANEL_OVERVIEW:     refreshOverview();          break;
            case PANEL_STUDENTS:     refreshStudentsPanel();     break;
            case PANEL_LECTURERS:    refreshLecturersPanel();    break;
            case PANEL_UNITS:        refreshUnitsPanel();        break;
            case PANEL_ENROLLMENTS:  refreshEnrollmentsPanel();  break;
            case PANEL_USERS:        refreshUsersPanel();        break;
        }
    }

    private void logout() {
        dispose();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    // ─────────────────────────────────────────────────────────
    // REUSABLE UI HELPERS
    // ─────────────────────────────────────────────────────────

    /** Creates a dark panel with a BorderLayout and a header JPanel already in NORTH. */
    private JPanel contentPanel(String title) {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG);
        JLabel lbl = new JLabel(title);
        lbl.setFont(TITLE_FONT);
        lbl.setForeground(TEXT);
        header.add(lbl, BorderLayout.WEST);
        p.add(header, BorderLayout.NORTH);
        return p;
    }

    /** Returns the header panel stored in NORTH of a contentPanel(). */
    private JPanel headerOf(JPanel contentPanel) {
        return (JPanel) ((BorderLayout) contentPanel.getLayout())
                .getLayoutComponent(BorderLayout.NORTH);
    }

    private DefaultTableModel blankModel(String[] cols) {
        return new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
    }

    private JScrollPane scrolledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setBackground(CARD_BG);
        table.setForeground(TEXT);
        table.setFont(BODY_FONT);
        table.setRowHeight(36);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(0x2A3F6F));
        table.setSelectionForeground(TEXT);
        table.setFillsViewportHeight(true);

        JTableHeader h = table.getTableHeader();
        h.setBackground(SIDEBAR_BG);
        h.setForeground(MUTED);
        h.setFont(SMALL_FONT);
        h.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));

        JScrollPane sp = new JScrollPane(table);
        sp.getViewport().setBackground(CARD_BG);
        sp.setBorder(BorderFactory.createLineBorder(BORDER));
        return sp;
    }

    private void styleBtn(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(HEADER_FONT);
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private JDialog dialog(String title, int w, int h) {
        JDialog dlg = new JDialog(this, title, true);
        dlg.setSize(w, h);
        dlg.setLocationRelativeTo(this);
        dlg.getContentPane().setBackground(CARD_BG);
        return dlg;
    }

    private JPanel dialogForm() {
        JPanel form = new JPanel(new GridLayout(0, 1, 5, 5));
        form.setBackground(CARD_BG);
        form.setBorder(new EmptyBorder(20, 20, 20, 20));
        return form;
    }

    private void addRow(JPanel form, String labelText, JComponent field) {
        JLabel l = new JLabel(labelText);
        l.setFont(SMALL_FONT);
        l.setForeground(MUTED);
        form.add(l);
        form.add(field);
    }

    private JTextField field() {
        JTextField f = new JTextField();
        f.setBackground(BG); f.setForeground(TEXT); f.setCaretColor(TEXT);
        f.setFont(BODY_FONT);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(6, 10, 6, 10)));
        return f;
    }

    private JPasswordField passField() {
        JPasswordField f = new JPasswordField();
        f.setBackground(BG); f.setForeground(TEXT); f.setCaretColor(TEXT);
        f.setFont(BODY_FONT);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(6, 10, 6, 10)));
        return f;
    }

    private void styleCombo(JComboBox<String> combo) {
        combo.setBackground(BG);
        combo.setForeground(TEXT);
        combo.setFont(BODY_FONT);
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
    }

    private JLabel msgLabel() {
        JLabel l = new JLabel(" ");
        l.setForeground(DANGER);
        l.setFont(SMALL_FONT);
        return l;
    }

    // ─────────────────────────────────────────────────────────
    // MAIN — for standalone testing only
    // Normal entry point is LoginFrame
    // ─────────────────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            User dummy = new User(1, "Test Admin", "admin@test.com", "", "admin");
            new AdminDashboard(dummy).setVisible(true);
        });
    }
}