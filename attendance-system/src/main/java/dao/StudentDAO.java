package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.DatabaseConnection;
import Model.Student;

public class StudentDAO {

    // Insert a new student
    public boolean addStudent(Student student) {

        String sql = "INSERT INTO students (reg_no, user_id, student_name, " +
                "year_of_study, stream) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, student.getRegNo());
            stmt.setInt(2, student.getUserId());
            stmt.setString(3, student.getStudentName());
            stmt.setString(4, student.getYearOfStudy());
            stmt.setString(5, student.getStream());

            int rows = stmt.executeUpdate();

            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete student by reg_no -not int
    public boolean removeStudent(String regNo) {

        String sql = "DELETE FROM students WHERE reg_no = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, regNo);

            int rows = stmt.executeUpdate();

            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get student by reg_no
    public Student getStudentById(String regNo) {

        String sql = "SELECT * FROM students WHERE reg_no = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, regNo);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                return new Student(
                        rs.getString("reg_no"),
                        rs.getInt("user_id"),
                        rs.getString("student_name"),
                        rs.getString("year_of_study"),
                        rs.getString("stream")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    // Get student by user_id — useful after login
    public Student getStudentByUserId(int userId) {

        String sql = "SELECT * FROM students WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Student(
                        rs.getString("reg_no"),
                        rs.getInt("user_id"),
                        rs.getString("student_name"),
                        rs.getString("year_of_study"),
                        rs.getString("stream")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Get all students
    public List<Student> getAllStudents() {

        List<Student> students = new ArrayList<>();

        String sql = "SELECT * FROM students";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                students.add(new Student(
                        rs.getString("reg_no"),
                        rs.getInt("user_id"),
                        rs.getString("student_name"),
                        rs.getString("year_of_study"),
                        rs.getString("stream")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }
}