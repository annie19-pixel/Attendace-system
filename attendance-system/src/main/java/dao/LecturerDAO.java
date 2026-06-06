package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

import database.DatabaseConnection;
import Model.Lecturer;

public class LecturerDAO {

    // Add lecturer
    public boolean addLecturer(Lecturer lecturer) {

        String sql = "INSERT INTO lecturers " +
                "(staff_no, user_id, lecturer_name, department) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, lecturer.getStaffNo());
            stmt.setInt(2, lecturer.getUserId());
            stmt.setString(3, lecturer.getLecturerName());
            stmt.setString(4, lecturer.getDepartment());

            int rows = stmt.executeUpdate();

            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete lecturer
    public boolean deleteLecturer(String staffNo) {

        String sql = "DELETE FROM lecturers WHERE staff_no = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, staffNo);

            int rows = stmt.executeUpdate();

            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get lecturer by staff number
    public Lecturer getLecturerByStaffNo(String staffNo) {

        String sql = "SELECT * FROM lecturers WHERE staff_no = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, staffNo);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                return new Lecturer(
                        rs.getString("staff_no"),
                        rs.getInt("user_id"),
                        rs.getString("lecturer_name"),
                        rs.getString("department")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Get lecturer by user_id — useful after login
    public Lecturer getLecturerByUserId(int userId) {

        String sql = "SELECT * FROM lecturers WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Lecturer(
                        rs.getString("staff_no"),
                        rs.getInt("user_id"),
                        rs.getString("lecturer_name"),
                        rs.getString("department")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    // Get all lecturers
    public List<Lecturer> getAllLecturers() {

        List<Lecturer> lecturers = new ArrayList<>();

        String sql = "SELECT * FROM lecturers";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                lecturers.add(new Lecturer(
                        rs.getString("staff_no"),
                        rs.getInt("user_id"),
                        rs.getString("lecturer_name"),
                        rs.getString("department")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lecturers;
    }
}