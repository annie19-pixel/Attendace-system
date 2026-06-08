package gui;

import Model.Attendance;
import Model.Enrollment;
import Model.Lecturer;
import Model.Student;
import Model.Unit;
import Model.User;
import dao.AttendanceDAO;
import dao.EnrollmentDAO;
import dao.StudentDAO;
import dao.UnitDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * LecturerDashboard — one class, all lecturer panels inside via CardLayout.
 *
 * Constructor: LecturerDashboard(User user, Lecturer lecturer)
 * LoginFrame calls:
 *   Lecturer profile = authService.getLecturerProfile(user);
 *   new LecturerDashboard(user, profile).setVisible(true);
 *
 * Panels:
 *  OVERVIEW      — name, staff no, department, number of assigned units
 *  MY_UNITS      — list of units this lecturer teaches
 *  MARK_ATTEND   — select a unit + date, then mark Present/Absent/Late per student
 *  VIEW_ATTEND   — view attendance records for a chosen unit on a chosen date
 */
public class LecturerDashboard extends JFrame {

    // ── Palette ──────────────────────────────────────────────
    private static final Color BG         = new Color(0x10141F);
    private static final Color SIDEBAR_BG = new Color(0x171E2E);
    private static final Color CARD_BG    = new Color(0x1D2640);
    private static final Color ACCENT     = new Color(0x7C6AF7);   // purple for lecturer
    private static final Color ACCENT2    = new Color(0x38D9A9);
    private static final Color TEXT       = new Color(0xE8EAF0);
    private static final Color MUTED      = new Color(0x6B7595);
    private static final Color WARN       = new Color(0xF7A94F);
    private static final Color DANGER     = new Color(0xFF5C6A);
    private static final Color BORDER     = new Color(0x252E48);
    private static final Color PRESENT_C  = new Color(0x38D9A9);
    private static final Color ABSENT_C   = new Color(0xFF5C6A);
    private static final Color LATE_C     = new Color(0xF7A94F);

    private static final Font TITLE_FONT  = new Font("SansSerif", Font.BOLD, 22);
    private static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 14);
    private static final Font BODY_FONT   = new Font("SansSerif", Font.PLAIN, 15);
    private static final Font SMALL_FONT  = new Font("SansSerif", Font.PLAIN, 13);

    // ── State ────────────────────────────────────────────────
    private final User           loggedInUser;
    private final Lecturer       lecturerProfile;
    private final AttendanceDAO  attendanceDAO = new AttendanceDAO();
    private final EnrollmentDAO  enrollmentDAO = new EnrollmentDAO();
    private final StudentDAO     studentDAO    = new StudentDAO();
    private final UnitDAO        unitDAO       = new UnitDAO();

    // ── Layout ───────────────────────────────────────────────
    private CardLayout cardLayout;
    private JPanel     contentPanel;

    private static final String PANEL_OVERVIEW    = "OVERVIEW";
    private static final String PANEL_MY_UNITS    = "MY_UNITS";
    private static final String PANEL_MARK_ATTEND = "MARK_ATTEND";
    private static final String PANEL_VIEW_ATTEND = "VIEW_ATTEND";

    // ── Table models ─────────────────────────────────────────
    private DefaultTableModel myUnitsModel;
    private DefaultTableModel markModel;
    private DefaultTableModel viewModel;

    // ── Mark attendance panel controls ───────────────────────
    private JComboBox<String> markUnitCombo;
    private JTextField        markDateField;
    private List<Enrollment>  currentEnrollments; // enrollments for selected unit

    // ── View attendance panel controls ───────────────────────
    private JComboBox<String> viewUnitCombo;
    private JTextField        viewDateField;

    public LecturerDashboard(User user, Lecturer lecturer) {
        this.loggedInUser    = user;
        this.lecturerProfile = lecturer;
        buildWindow();
    }

    private void buildWindow() {
        setTitle("Lecturer Dashboard — " + loggedInUser.getFullName());
        setSize(1100, 680);
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
        sb.add(sideLabel("  LECTURER", SMALL_FONT, ACCENT2,
                new EmptyBorder(0, 20, 24, 0)));
        sb.add(divider());

        sb.add(navBtn("Overview",          PANEL_OVERVIEW));
        sb.add(navBtn("My Units",          PANEL_MY_UNITS));
        sb.add(navBtn("Mark Attendance",   PANEL_MARK_ATTEND));
        sb.add(navBtn("View Attendance",   PANEL_VIEW_ATTEND));

        sb.add(Box.createVerticalGlue());
        sb.add(divider());

        String staffNo = lecturerProfile != null ? lecturerProfile.getStaffNo() : "-";
        sb.add(sideLabel("  " + staffNo, SMALL_FONT, MUTED,
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
    // CONTENT
    // ─────────────────────────────────────────────────────────
    private JPanel buildContent() {
        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG);

        contentPanel.add(buildOverviewPanel(),    PANEL_OVERVIEW);
        contentPanel.add(buildMyUnitsPanel(),     PANEL_MY_UNITS);
        contentPanel.add(buildMarkAttendPanel(),  PANEL_MARK_ATTEND);
        contentPanel.add(buildViewAttendPanel(),  PANEL_VIEW_ATTEND);

        cardLayout.show(contentPanel, PANEL_OVERVIEW);
        return contentPanel;
    }

    // ── Overview ──────────────────────────────────────────────
    private JPanel buildOverviewPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 24));
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(30, 30, 30, 30));

        String name = lecturerProfile != null
                ? lecturerProfile.getLecturerName()
                : loggedInUser.getFullName();
        JLabel title = new JLabel("Welcome, " + name);
        title.setFont(TITLE_FONT);
        title.setForeground(TEXT);
        p.add(title, BorderLayout.NORTH);

        JPanel row = new JPanel(new GridLayout(1, 3, 16, 0));
        row.setBackground(BG);

        String staffNo = lecturerProfile != null ? lecturerProfile.getStaffNo()   : "-";
        String dept    = lecturerProfile != null ? lecturerProfile.getDepartment() : "-";
        int units = 0;
        if (lecturerProfile != null)
            units = enrollmentDAO.getEnrollmentsByLecturer(lecturerProfile.getStaffNo()).size();

        row.add(statCard("Staff No",     staffNo,            ACCENT));
        row.add(statCard("Department",   dept,               ACCENT2));
        row.add(statCard("Active Units", String.valueOf(units), WARN));

        p.add(row, BorderLayout.CENTER);

        JLabel hint = new JLabel("  Go to 'Mark Attendance' to record today's session.");
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
        val.setFont(new Font("SansSerif", Font.BOLD, 26));
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
        String[] cols = {"Unit ID", "Unit Code", "Unit Name", "Semester", "Academic Year"};
        myUnitsModel = blankModel(cols);
        p.add(scrolledTable(myUnitsModel), BorderLayout.CENTER);
        return p;
    }

    private void refreshMyUnitsPanel() {
        myUnitsModel.setRowCount(0);
        if (lecturerProfile == null) return;

        List<Unit> allUnits = unitDAO.getAllUnits();
        List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsByLecturer(lecturerProfile.getStaffNo());

        // Use a set to avoid printing the same unit twice (multiple students, same unit)
        java.util.Set<Integer> seen = new java.util.HashSet<>();
        for (Enrollment en : enrollments) {
            if (seen.contains(en.getUnitId())) continue;
            seen.add(en.getUnitId());

            String unitCode = String.valueOf(en.getUnitId());
            String unitName = "-";
            for (Unit u : allUnits) {
                if (u.getUnitId() == en.getUnitId()) {
                    unitCode = u.getUnitCode();
                    unitName = u.getUnitName();
                    break;
                }
            }
            myUnitsModel.addRow(new Object[]{
                    en.getUnitId(), unitCode, unitName, en.getSemester(), en.getAcademicYear()
            });
        }
    }

    // ── Mark Attendance ───────────────────────────────────────
    private JPanel buildMarkAttendPanel() {
        JPanel outer = new JPanel(new BorderLayout(0, 16));
        outer.setBackground(BG);
        outer.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("Mark Attendance");
        title.setFont(TITLE_FONT);
        title.setForeground(TEXT);
        outer.add(title, BorderLayout.NORTH);

        // ── Controls row ──
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        controls.setBackground(CARD_BG);
        controls.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(10, 10, 10, 10)));

        controls.add(muted("Unit:"));
        markUnitCombo = new JComboBox<>();
        styleCombo(markUnitCombo);
        controls.add(markUnitCombo);

        controls.add(muted("Date (YYYY-MM-DD):"));
        markDateField = field();
        markDateField.setText(LocalDate.now().toString());
        markDateField.setPreferredSize(new Dimension(130, 32));
        controls.add(markDateField);

        JButton loadBtn = new JButton("Load Students");
        styleBtn(loadBtn, ACCENT, Color.WHITE);
        controls.add(loadBtn);

        outer.add(controls, BorderLayout.NORTH);

        // ── Mark table: Reg No | Name | Status (combo) ──
        String[] cols = {"Reg No", "Student Name", "Status"};
        markModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return c == 2; } // only Status is editable
        };
        JTable markTable = new JTable(markModel);
        styleTable(markTable);

        // Put a JComboBox in the Status column
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Present", "Absent", "Late"});
        markTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(statusCombo));

        JScrollPane sp = new JScrollPane(markTable);
        sp.getViewport().setBackground(CARD_BG);
        sp.setBorder(BorderFactory.createLineBorder(BORDER));
        outer.add(sp, BorderLayout.CENTER);

        // ── Submit button ──
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(BG);
        JLabel msgLabel = new JLabel(" ");
        msgLabel.setForeground(DANGER);
        msgLabel.setFont(SMALL_FONT);
        JButton submitBtn = new JButton("Save Attendance");
        styleBtn(submitBtn, ACCENT2, Color.WHITE);
        bottom.add(msgLabel);
        bottom.add(submitBtn);
        outer.add(bottom, BorderLayout.SOUTH);

        // ── Wire up load button ──
        loadBtn.addActionListener(e -> {
            markModel.setRowCount(0);
            String selectedUnit = (String) markUnitCombo.getSelectedItem();
            if (selectedUnit == null || selectedUnit.isEmpty()) {
                msgLabel.setText("Please select a unit."); return;
            }
            int unitId = Integer.parseInt(selectedUnit.split(" ")[0]);
            currentEnrollments = enrollmentDAO.getEnrollmentsByUnit(unitId);

            for (Enrollment en : currentEnrollments) {
                Student s = studentDAO.getStudentById(en.getRegNo());
                String sName = s != null ? s.getStudentName() : en.getRegNo();
                markModel.addRow(new Object[]{en.getRegNo(), sName, "Present"});
            }
            msgLabel.setText(" ");
        });

        // ── Wire up submit button ──
        submitBtn.addActionListener(e -> {
            if (markModel.getRowCount() == 0) { msgLabel.setText("No students loaded."); return; }

            String dateStr = markDateField.getText().trim();
            Date sqlDate;
            try {
                sqlDate = Date.valueOf(dateStr);
            } catch (IllegalArgumentException ex) {
                msgLabel.setText("Date must be YYYY-MM-DD format."); return;
            }

            int saved = 0, failed = 0;
            for (int i = 0; i < markModel.getRowCount(); i++) {
                String regNo  = (String) markModel.getValueAt(i, 0);
                String status = (String) markModel.getValueAt(i, 2);

                // Find the enrollment_id for this student in this unit
                if (currentEnrollments == null) continue;
                for (Enrollment en : currentEnrollments) {
                    if (en.getRegNo().equals(regNo)) {
                        Attendance att = new Attendance(en.getEnrollmentId(), sqlDate, status);
                        if (attendanceDAO.markAttendance(att)) saved++;
                        else failed++;
                        break;
                    }
                }
            }
            msgLabel.setForeground(failed == 0 ? ACCENT2 : DANGER);
            msgLabel.setText("Saved: " + saved + (failed > 0 ? "  |  Failed: " + failed : ""));
        });

        return outer;
    }

    private void refreshMarkAttendPanel() {
        markUnitCombo.removeAllItems();
        if (lecturerProfile == null) return;

        List<Unit>       allUnits    = unitDAO.getAllUnits();
        List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsByLecturer(lecturerProfile.getStaffNo());

        java.util.Set<Integer> seen = new java.util.HashSet<>();
        for (Enrollment en : enrollments) {
            if (seen.contains(en.getUnitId())) continue;
            seen.add(en.getUnitId());

            String label = en.getUnitId() + " — " + unitNameFor(allUnits, en.getUnitId());
            markUnitCombo.addItem(label);
        }
    }

    // ── View Attendance ───────────────────────────────────────
    private JPanel buildViewAttendPanel() {
        JPanel outer = new JPanel(new BorderLayout(0, 16));
        outer.setBackground(BG);
        outer.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("View Attendance");
        title.setFont(TITLE_FONT);
        title.setForeground(TEXT);
        outer.add(title, BorderLayout.NORTH);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        controls.setBackground(CARD_BG);
        controls.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(10, 10, 10, 10)));

        controls.add(muted("Unit:"));
        viewUnitCombo = new JComboBox<>();
        styleCombo(viewUnitCombo);
        controls.add(viewUnitCombo);

        controls.add(muted("Date (YYYY-MM-DD):"));
        viewDateField = field();
        viewDateField.setText(LocalDate.now().toString());
        viewDateField.setPreferredSize(new Dimension(130, 32));
        controls.add(viewDateField);

        JButton searchBtn = new JButton("Search");
        styleBtn(searchBtn, ACCENT, Color.WHITE);
        controls.add(searchBtn);

        outer.add(controls, BorderLayout.NORTH);

        String[] cols = {"Attendance ID", "Reg No", "Date", "Status", "Time Marked"};
        viewModel = blankModel(cols);
        JTable viewTable = new JTable(viewModel);
        styleTable(viewTable);

        JScrollPane sp = new JScrollPane(viewTable);
        sp.getViewport().setBackground(CARD_BG);
        sp.setBorder(BorderFactory.createLineBorder(BORDER));
        outer.add(sp, BorderLayout.CENTER);

        JLabel msgLabel = new JLabel(" ");
        msgLabel.setForeground(MUTED);
        msgLabel.setFont(SMALL_FONT);
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.setBackground(BG);
        bottom.add(msgLabel);
        outer.add(bottom, BorderLayout.SOUTH);

        searchBtn.addActionListener(e -> {
            viewModel.setRowCount(0);
            String selectedUnit = (String) viewUnitCombo.getSelectedItem();
            if (selectedUnit == null) { msgLabel.setText("Select a unit."); return; }

            String dateStr = viewDateField.getText().trim();
            Date sqlDate;
            try { sqlDate = Date.valueOf(dateStr); }
            catch (IllegalArgumentException ex) { msgLabel.setText("Date must be YYYY-MM-DD."); return; }

            int unitId = Integer.parseInt(selectedUnit.split(" ")[0]);
            List<Attendance> records = attendanceDAO.getAttendanceByUnitAndDate(unitId, sqlDate);

            // Pair enrollment → reg_no for display
            List<Enrollment> unitEnrollments = enrollmentDAO.getEnrollmentsByUnit(unitId);

            for (Attendance a : records) {
                String regNo = "-";
                for (Enrollment en : unitEnrollments) {
                    if (en.getEnrollmentId() == a.getEnrollmentId()) {
                        regNo = en.getRegNo(); break;
                    }
                }
                viewModel.addRow(new Object[]{
                        a.getAttendanceId(), regNo,
                        a.getAttendanceDate(), a.getStatus(), a.getTimeMarked()
                });
            }

            msgLabel.setText(records.isEmpty()
                    ? "No records found for that unit/date."
                    : records.size() + " record(s) found.");
        });

        return outer;
    }

    private void refreshViewAttendPanel() {
        viewUnitCombo.removeAllItems();
        if (lecturerProfile == null) return;

        List<Unit>       allUnits    = unitDAO.getAllUnits();
        List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsByLecturer(lecturerProfile.getStaffNo());

        java.util.Set<Integer> seen = new java.util.HashSet<>();
        for (Enrollment en : enrollments) {
            if (seen.contains(en.getUnitId())) continue;
            seen.add(en.getUnitId());
            viewUnitCombo.addItem(en.getUnitId() + " — " + unitNameFor(allUnits, en.getUnitId()));
        }
    }

    // ─────────────────────────────────────────────────────────
    // REFRESH DISPATCHER
    // ─────────────────────────────────────────────────────────
    private void refreshPanel(String name) {
        switch (name) {
            case PANEL_MY_UNITS:    refreshMyUnitsPanel();    break;
            case PANEL_MARK_ATTEND: refreshMarkAttendPanel(); break;
            case PANEL_VIEW_ATTEND: refreshViewAttendPanel(); break;
        }
    }

    // ─────────────────────────────────────────────────────────
    // REUSABLE UI HELPERS
    // ─────────────────────────────────────────────────────────
    private String unitNameFor(List<Unit> units, int id) {
        for (Unit u : units) if (u.getUnitId() == id) return u.getUnitName();
        return String.valueOf(id);
    }

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
        JTable t = new JTable(model);
        styleTable(t);
        JScrollPane sp = new JScrollPane(t);
        sp.getViewport().setBackground(CARD_BG);
        sp.setBorder(BorderFactory.createLineBorder(BORDER));
        return sp;
    }

    private void styleTable(JTable table) {
        table.setBackground(CARD_BG);
        table.setForeground(TEXT);
        table.setFont(BODY_FONT);
        table.setRowHeight(42);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(0x2A3260));
        table.setSelectionForeground(TEXT);
        table.setFillsViewportHeight(true);

        JTableHeader h = table.getTableHeader();
        h.setBackground(SIDEBAR_BG);
        h.setForeground(MUTED);
        h.setFont(SMALL_FONT);
        h.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
    }

    private void styleBtn(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(HEADER_FONT);
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleCombo(JComboBox<String> combo) {
        combo.setBackground(BG);
        combo.setForeground(TEXT);
        combo.setFont(BODY_FONT);
        combo.setPreferredSize(new Dimension(220, 32));
    }

    private JTextField field() {
        JTextField f = new JTextField();
        f.setBackground(BG); f.setForeground(TEXT); f.setCaretColor(TEXT);
        f.setFont(BODY_FONT);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(6, 10, 6, 10)));
        return f;
    }

    private JLabel muted(String text) {
        JLabel l = new JLabel(text);
        l.setFont(SMALL_FONT);
        l.setForeground(MUTED);
        return l;
    }

    // ─────────────────────────────────────────────────────────
    // MAIN — standalone testing only
    // ─────────────────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            User     u = new User(2001, "Dr. Smith", "smith@test.com", "", "lecturer");
            Lecturer l = new Lecturer("LCT/24/001", 2001, "Dr. Smith", "Computer Science");
            new LecturerDashboard(u, l).setVisible(true);
        });
    }
}