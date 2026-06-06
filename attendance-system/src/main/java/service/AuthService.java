package service;

import dao.UserDAO;
import dao.StudentDAO;
import dao.LecturerDAO;
import Model.User;
import Model.Student;
import Model.Lecturer;

public class AuthService {

    // These DAOs are the only way AuthService talks to the database
    // It never writes SQL itself — it delegates to the DAOs
    private final UserDAO userDAO;
    private final StudentDAO studentDAO;
    private final LecturerDAO lecturerDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
        this.studentDAO = new StudentDAO();
        this.lecturerDAO = new LecturerDAO();
    }

    // ─────────────────────────────────────────────
    // LOGIN
    // Called by LoginFrame when the user clicks login
    // Returns the User object if credentials are correct
    // Returns null if email not found OR password is wrong
    // The GUI uses the returned User's role to decide
    // which dashboard to open next
    // ─────────────────────────────────────────────
    public User login(String email, String password) {

        // Don't touch the database if fields are empty
        if (email == null || email.trim().isEmpty()) return null;
        if (password == null || password.trim().isEmpty()) return null;

        // Ask UserDAO to find a user with this email
        User user = userDAO.getUserByEmail(email.trim());

        // No account with that email exists
        if (user == null) return null;

        // Account found — check if the password matches
        if (!user.getPassword().equals(password)) return null;

        // Both checks passed — login successful
        return user;
    }

    // ─────────────────────────────────────────────
    // ROLE CHECKS
    // The GUI calls these before showing any screen
    // to make sure the logged-in user is allowed there
    // ─────────────────────────────────────────────
    public boolean isAdmin(User user) {
        return user != null && "admin".equals(user.getRole());
    }

    public boolean isStudent(User user) {
        return user != null && "student".equals(user.getRole());
    }

    public boolean isLecturer(User user) {
        return user != null && "lecturer".equals(user.getRole());
    }

    // ─────────────────────────────────────────────
    // PROFILE FETCHING
    // After login we have a User but it only has data
    // from the users table. These methods fetch the
    // extra profile data from students/lecturers tables
    // using the user_id that came back from login
    // ─────────────────────────────────────────────
    public Student getStudentProfile(User user) {
        if (!isStudent(user)) return null;
        return studentDAO.getStudentByUserId(user.getUserId());
    }

    public Lecturer getLecturerProfile(User user) {
        if (!isLecturer(user)) return null;
        return lecturerDAO.getLecturerByUserId(user.getUserId());
    }

    // ─────────────────────────────────────────────
    // REGISTRATION — called by admin only
    // Creating a student is two steps:
    // Step 1 — insert into users table
    // Step 2 — insert into students table
    // If step 2 fails we delete the user created in
    // step 1 so we don't leave orphan data behind
    // ─────────────────────────────────────────────
    public boolean registerStudent(User user, Student student) {

        // Step 1 — create the login account
        boolean userCreated = userDAO.addUser(user);
        if (!userCreated) return false;

        // Step 2 — get the auto-generated user_id back from DB
        User createdUser = userDAO.getUserByEmail(user.getEmail());
        if (createdUser == null) return false;

        // Step 3 — link the student row to the user row
        student.setUserId(createdUser.getUserId());
        boolean studentCreated = studentDAO.addStudent(student);

        if (!studentCreated) {
            // Clean up the orphan user row
            userDAO.removeUser(createdUser.getUserId());
            return false;
        }

        return true;
    }

    // Same two-step pattern for lecturers
    public boolean registerLecturer(User user, Lecturer lecturer) {

        boolean userCreated = userDAO.addUser(user);
        if (!userCreated) return false;

        User createdUser = userDAO.getUserByEmail(user.getEmail());
        if (createdUser == null) return false;

        lecturer.setUserId(createdUser.getUserId());
        boolean lecturerCreated = lecturerDAO.addLecturer(lecturer);

        if (!lecturerCreated) {
            userDAO.removeUser(createdUser.getUserId());
            return false;
        }

        return true;
    }

    // Admins only need a users table row — no separate profile table
    public boolean registerAdmin(User user) {
        user.setRole("admin");
        return userDAO.addUser(user);
    }
}