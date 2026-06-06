package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.DatabaseConnection;
import Model.Enrollment;

public class EnrollmentDAO {

    // Add enrollment
    public boolean addEnrollment(Enrollment enrollment) {

        // table is 'enrollment' not 'enrollments'
        // columns are reg_no, unit_id, staff_no, semester, academic_year
        String sql = "INSERT INTO enrollment (reg_no, unit_id, staff_no, semester, academic_year) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, enrollment.getRegNo());
            stmt.setInt(2, enrollment.getUnitId());
            stmt.setString(3, enrollment.getStaffNo());
            stmt.setString(4, enrollment.getSemester());
            stmt.setString(5, enrollment.getAcademicYear());

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Remove enrollment
    public boolean removeEnrollment(int enrollmentId) {

        String sql = "DELETE FROM enrollment WHERE enrollment_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, enrollmentId);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get all units a student is enrolled in
    public List<Enrollment> getEnrollmentsByStudent(String regNo) {

        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT * FROM enrollment WHERE reg_no = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, regNo);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                enrollments.add(new Enrollment(
                        rs.getInt("enrollment_id"),
                        rs.getString("reg_no"),
                        rs.getInt("unit_id"),
                        rs.getString("staff_no"),
                        rs.getString("semester"),
                        rs.getString("academic_year")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return enrollments;
    }

    // Get all students enrolled in a unit
    public List<Enrollment> getEnrollmentsByUnit(int unitId) {

        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT * FROM enrollment WHERE unit_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, unitId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                enrollments.add(new Enrollment(
                        rs.getInt("enrollment_id"),
                        rs.getString("reg_no"),
                        rs.getInt("unit_id"),
                        rs.getString("staff_no"),
                        rs.getString("semester"),
                        rs.getString("academic_year")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return enrollments;
    }

    // Get enrollments by lecturer — so lecturer sees only their units
    public List<Enrollment> getEnrollmentsByLecturer(String staffNo) {

        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT * FROM enrollment WHERE staff_no = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, staffNo);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                enrollments.add(new Enrollment(
                        rs.getInt("enrollment_id"),
                        rs.getString("reg_no"),
                        rs.getInt("unit_id"),
                        rs.getString("staff_no"),
                        rs.getString("semester"),
                        rs.getString("academic_year")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return enrollments;
    }
}