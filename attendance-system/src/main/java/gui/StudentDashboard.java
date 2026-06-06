package gui;

import Model.Attendance;
import Model.Enrollment;
import Model.Student;
import Model.Unit;
import Model.User;
import dao.AttendanceDAO;
import dao.EnrollmentDAO;
import dao.UnitDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

/**
 * StudentDashboard — one class, all student panels inside via CardLayout.
 *
 * Constructor: StudentDashboard(User user, Student student)
 * LoginFrame calls:
 *   Student profile = authService.getStudentProfile(user);
 *   new StudentDashboard(user, profile).setVisible(true);
 *
 * Panels:
 *  OVERVIEW    — name, reg no, year, enrolled unit count
 *  MY_UNITS    — enrolled units with attendance % per unit
 *  ATTENDANCE  — full attendance history across all units
 */
public class StudentDashboard extends JFrame {

    // ── Palette ──────────────────────────────────────────────
    private static final Color BG         = new Color(0x0D1321);
    private static final Color SIDEBAR_BG = new Color(0x141D30);
    private static final Color CARD_BG    = new Color(0x1A2540);
    private static final Color ACCENT     = new Color(0x38D9A9);
    private static final Color ACCENT2    = new Color(0x4F8EF7);
    private static final Color TEXT       = new Color(0xE8EAF0);
    private static final Color MUTED      = new Color(0x6B7595);
    private static final Color WARN       = new Color(0xF7A94F);
    private static final Color DANGER     = new Color(0xFF5C6A);
    private static final Color BORDER     = new Color(0x253050);

    private static final Font TITLE_FONT  = new Font("SansSerif", Font.BOLD, 22);
    private static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 14);
    private static final Font BODY_FONT   = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font SMALL_FONT  = new Font("SansSerif", Font.PLAIN, 11);

    // ── State ────────────────────────────────────────────────
    private final User           loggedInUser;
    private final Student        studentProfile;
    private final AttendanceDAO  attendanceDAO = new AttendanceDAO();
    private final EnrollmentDAO  enrollmentDAO = new EnrollmentDAO();
    private final UnitDAO        unitDAO       = new UnitDAO();

    // ── Layout ───────────────────────────────────────────────
    private CardLayout cardLayout;
    private JPanel     contentPanel;

    private static final String PANEL_OVERVIEW   = "OVERVIEW";
    private static final String PANEL_MY_UNITS   = "MY_UNITS";
    private static final String PANEL_ATTENDANCE = "ATTENDANCE";

    // ── Table models ─────────────────────────────────────────
    private DefaultTableModel unitsModel;
    private DefaultTableModel attendanceModel;

    public StudentDashboard(User user, Student student) {
        this.loggedInUser   = user;
        this.studentProfile = student;
        buildWindow();
    }

    private void buildWindow() {
        setTitle("Student Dashboard — " + loggedInUser.getFullName());
        setSize(1000, 650);
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
        sb.setPreferredSize(new Dimension(215, 0));
        sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));
        sb.setBorder(new EmptyBorder(30, 0, 20, 0));

        sb.add(sideLabel("  AttendMS", new Font("SansSerif", Font.BOLD, 18), ACCENT,
                new EmptyBorder(0, 20, 8, 0)));
        sb.add(sideLabel("  STUDENT", SMALL_FONT, ACCENT2,
                new EmptyBorder(0, 20, 24, 0)));
        sb.add(divider());

        sb.add(navBtn("Overview",           PANEL_OVERVIEW));
        sb.add(navBtn("My Units",           PANEL_MY_UNITS));
        sb.add(navBtn("Attendance History", PANEL_ATTENDANCE));

        sb.add(Box.createVerticalGlue());
        sb.add(divider());

        String regNo = studentProfile != null ? studentProfile.getRegNo() : "-";
        sb.add(sideLabel("  " + regNo, SMALL_FONT, MUTED,
                new EmptyBorder(10, 20, 3, 0)));
        sb.add(sideLabel("  " + loggedInUser.getFullName(), BODY_FONT, TEXT,
                new EmptyBorder(3, 20, 10, 0)));

        JButton logoutBtn = new JButton("Logout");
        styleBtn(logoutBtn, DANGER, Color.WHITE);
        logoutBtn.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        });
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
        btn.setMaximumSize(new Dimension(215, 45));
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
        s.setMaximumSize(new Dimension(215, 1));
        return s;
    }

    // ─────────────────────────────────────────────────────────
    // CONTENT
    // ─────────────────────────────────────────────────────────
    private JPanel buildContent() {
        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG);

        contentPanel.add(buildOverviewPanel(),   PANEL_OVERVIEW);
        contentPanel.add(buildMyUnitsPanel(),    PANEL_MY_UNITS);
        contentPanel.add(buildAttendancePanel(), PANEL_ATTENDANCE);

        cardLayout.show(contentPanel, PANEL_OVERVIEW);
        return contentPanel;
    }

    // ── Overview ──────────────────────────────────────────────
    private JPanel buildOverviewPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 24));
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(30, 30, 30, 30));

        String name = studentProfile != null
                ? studentProfile.getStudentName()
                : loggedInUser.getFullName();
        JLabel title = new JLabel("Welcome, " + name);
        title.setFont(TITLE_FONT);
        title.setForeground(TEXT);
        p.add(title, BorderLayout.NORTH);

        JPanel row = new JPanel(new GridLayout(1, 3, 16, 0));
        row.setBackground(BG);

        String regNo = studentProfile != null ? studentProfile.getRegNo()       : "-";
        String year  = studentProfile != null ? studentProfile.getYearOfStudy() : "-";
        int enrolled = 0;
        if (studentProfile != null)
            enrolled = enrollmentDAO.getEnrollmentsByStudent(studentProfile.getRegNo()).size();

        row.add(statCard("Reg No",         regNo,                    ACCENT2));
        row.add(statCard("Year of Study",  year,                     WARN));
        row.add(statCard("Enrolled Units", String.valueOf(enrolled),  ACCENT));

        p.add(row, BorderLayout.CENTER);

        JLabel hint = new JLabel("  Go to 'My Units' to see attendance percentages per unit.");
        hint.setFont(SMALL_FONT);
        hint.setForeground(MUTED);
        p.add(hint, BorderLayout.SOUTH);

        return p;
    }

    private JPanel statCard(String label, String value, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(20, 20, 20, 20)));
        JLabel val = new JLabel(value);
        val.setFont(new Font("SansSerif", Font.BOLD, 28));
        val.setForeground(accent);
        JLabel lbl = new JLabel(label.toUpperCase());
        lbl.setFont(SMALL_FONT);
        lbl.setForeground(MUTED);
        card.add(val, BorderLayout.CENTER);
        card.add(lbl, BorderLayout.SOUTH);
        return card;
    }

    // ── My Units ──────────────────────────────────────────────
    private JPanel buildMyUnitsPanel() {
        JPanel p = contentPanel("My Units");

        // Columns: unit code | unit name | semester | academic year | attendance %
        String[] cols = {"Unit Code", "Unit Name", "Semester", "Academic Year", "Attendance %"};
        unitsModel = blankModel(cols);
        p.add(scrolledTable(unitsModel), BorderLayout.CENTER);

        JLabel note = new JLabel("  Attendance % = (sessions present ÷ total sessions) × 100");
        note.setFont(SMALL_FONT);
        note.setForeground(MUTED);
        p.add(note, BorderLayout.SOUTH);
        return p;
    }

    private void refreshMyUnitsPanel() {
        unitsModel.setRowCount(0);
        if (studentProfile == null) return;

        List<Unit>       allUnits    = unitDAO.getAllUnits();
        List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsByStudent(studentProfile.getRegNo());

        for (Enrollment en : enrollments) {
            // Look up unit name from the full unit list
            String unitCode = String.valueOf(en.getUnitId());
            String unitName = "-";
            for (Unit u : allUnits) {
                if (u.getUnitId() == en.getUnitId()) {
                    unitCode = u.getUnitCode();
                    unitName = u.getUnitName();
                    break;
                }
            }

            double pct    = attendanceDAO.getAttendancePercentage(studentProfile.getRegNo(), en.getUnitId());
            String pctStr = String.format("%.1f%%", pct);

            unitsModel.addRow(new Object[]{
                    unitCode, unitName, en.getSemester(), en.getAcademicYear(), pctStr
            });
        }
    }

    // ── Attendance History ────────────────────────────────────
    private JPanel buildAttendancePanel() {
        JPanel p = contentPanel("Attendance History");

        String[] cols = {"Attendance ID", "Enrollment ID", "Date", "Status", "Time Marked"};
        attendanceModel = blankModel(cols);
        p.add(scrolledTable(attendanceModel), BorderLayout.CENTER);
        return p;
    }

    private void refreshAttendancePanel() {
        attendanceModel.setRowCount(0);
        if (studentProfile == null) return;

        for (Attendance a : attendanceDAO.getAttendanceByStudent(studentProfile.getRegNo()))
            attendanceModel.addRow(new Object[]{
                    a.getAttendanceId(), a.getEnrollmentId(),
                    a.getAttendanceDate(), a.getStatus(), a.getTimeMarked()
            });
    }

    // ─────────────────────────────────────────────────────────
    // REFRESH DISPATCHER
    // ─────────────────────────────────────────────────────────
    private void refreshPanel(String name) {
        switch (name) {
            case PANEL_MY_UNITS:   refreshMyUnitsPanel();   break;
            case PANEL_ATTENDANCE: refreshAttendancePanel(); break;
        }
    }

    // ─────────────────────────────────────────────────────────
    // REUSABLE UI HELPERS
    // ─────────────────────────────────────────────────────────
    private JPanel contentPanel(String title) {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(30, 30, 30, 30));
        JLabel lbl = new JLabel(title);
        lbl.setFont(TITLE_FONT);
        lbl.setForeground(TEXT);
        p.add(lbl, BorderLayout.NORTH);
        return p;
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
        table.setSelectionBackground(new Color(0x1F3A60));
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

    // ─────────────────────────────────────────────────────────
    // MAIN — standalone testing only
    // ─────────────────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            User    u = new User(1001, "Jane Doe", "jane@test.com", "", "student");
            Student s = new Student("24/00001", 1001, "Jane Doe", "Year 2", "Morning");
            new StudentDashboard(u, s).setVisible(true);
        });
    }
}