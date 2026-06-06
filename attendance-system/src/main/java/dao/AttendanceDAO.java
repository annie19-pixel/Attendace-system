package dao;

import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import database.DatabaseConnection;
import Model.Attendance;

public class AttendanceDAO {
    //Mark attendance for one student on one date
    public boolean markAttendance(Attendance attendance){
    String sql = "INSERT INTO attendance (enrollment_id, attendance_date,status)" +
            "VALUES (?,?,?)";

    try(
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, attendance.getEnrollmentId());
        stmt.setDate(2, attendance.getAttendanceDate());
        stmt.setString(3, attendance.getStatus());

        int rows = stmt.executeUpdate();
        return rows > 0;

    }catch(
    SQLException e) {
        e.printStackTrace();
        return false;
    }
}

//Update attendance if marked wrongly
public boolean updateAttendance(int attendanceId, String newStatus) {

    String sql = "UPDATE attendance SET status = ? WHERE attendance_id = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, newStatus);
        stmt.setInt(2, attendanceId);

        int rows = stmt.executeUpdate();
        return rows > 0;

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

// Delete an attendance record
public boolean deleteAttendance(int attendanceId) {

    String sql = "DELETE FROM attendance WHERE attendance_id = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, attendanceId);

        int rows = stmt.executeUpdate();
        return rows > 0;

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

// Student views their full attendance history across all units
// JOIN needed because attendance only stores enrollment_id
// we trace through enrollment to get reg_no
public List<Attendance> getAttendanceByStudent(String regNo) {

    List<Attendance> records = new ArrayList<>();

    String sql = "SELECT a.* FROM attendance a " +
            "JOIN enrollment e ON a.enrollment_id = e.enrollment_id " +
            "WHERE e.reg_no = ? " +
            "ORDER BY a.attendance_date DESC";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, regNo);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Attendance att = new Attendance();
            att.setAttendanceId(rs.getInt("attendance_id"));
            att.setEnrollmentId(rs.getInt("enrollment_id"));
            att.setAttendanceDate(rs.getDate("attendance_date"));
            att.setStatus(rs.getString("status"));
            att.setTimeMarked(rs.getTimestamp("time_marked"));
            records.add(att);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return records;
}

// Student views attendance for one specific unit only
public List<Attendance> getAttendanceByStudentAndUnit(String regNo, int unitId) {

    List<Attendance> records = new ArrayList<>();

    String sql = "SELECT a.* FROM attendance a " +
            "JOIN enrollment e ON a.enrollment_id = e.enrollment_id " +
            "WHERE e.reg_no = ? AND e.unit_id = ? " +
            "ORDER BY a.attendance_date DESC";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, regNo);
        stmt.setInt(2, unitId);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Attendance att = new Attendance();
            att.setAttendanceId(rs.getInt("attendance_id"));
            att.setEnrollmentId(rs.getInt("enrollment_id"));
            att.setAttendanceDate(rs.getDate("attendance_date"));
            att.setStatus(rs.getString("status"));
            att.setTimeMarked(rs.getTimestamp("time_marked"));
            records.add(att);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return records;
}

// Lecturer views all attendance for a unit on a specific date
public List<Attendance> getAttendanceByUnitAndDate(int unitId, Date date) {

    List<Attendance> records = new ArrayList<>();

    String sql = "SELECT a.* FROM attendance a " +
            "JOIN enrollment e ON a.enrollment_id = e.enrollment_id " +
            "WHERE e.unit_id = ? AND a.attendance_date = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, unitId);
        stmt.setDate(2, date);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Attendance att = new Attendance();
            att.setAttendanceId(rs.getInt("attendance_id"));
            att.setEnrollmentId(rs.getInt("enrollment_id"));
            att.setAttendanceDate(rs.getDate("attendance_date"));
            att.setStatus(rs.getString("status"));
            att.setTimeMarked(rs.getTimestamp("time_marked"));
            records.add(att);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return records;
}

// Calculate attendance percentage — used by student dashboard and admin reports
// formula: (present sessions / total sessions) × 100
public double getAttendancePercentage(String regNo, int unitId) {

    String sql = "SELECT " +
            "COUNT(*) AS total, " +
            "SUM(CASE WHEN a.status = 'Present' THEN 1 ELSE 0 END) AS present " +
            "FROM attendance a " +
            "JOIN enrollment e ON a.enrollment_id = e.enrollment_id " +
            "WHERE e.reg_no = ? AND e.unit_id = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, regNo);
        stmt.setInt(2, unitId);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            int total = rs.getInt("total");
            int present = rs.getInt("present");
            if (total == 0) return 0.0;
            return (present * 100.0) / total;
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return 0.0;
}
}
