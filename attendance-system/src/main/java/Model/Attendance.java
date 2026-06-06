package Model;

import java.sql.Date;
import java.sql.Timestamp;

public class Attendance {

    // Attendance attributes
    private int attendanceId;
    private int enrollmentId;
    private Date attendanceDate;
    private String status;
    private Timestamp timeMarked;

    // Constructors

    // Default constructor
    public Attendance() {
    }

    // Constructor with all fields
    public Attendance(int attendanceId, int enrollmentId, Date attendanceDate, String status, Timestamp timeMarked) {
        this.attendanceId = attendanceId;
        this.enrollmentId = enrollmentId;
        this.attendanceDate = attendanceDate;
        this.status = status;
        this.timeMarked = timeMarked;
    }

    // Constructor for new attendance
    public Attendance(int enrollmentId, Date attendanceDate, String status) {
        this.enrollmentId = enrollmentId;
        this.attendanceDate = attendanceDate;
        this.status = status;
    }

    // Getters and Setters

    public int getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(int attendanceId) {
        this.attendanceId = attendanceId;
    }

    public int getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public Date getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(Date attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getTimeMarked() {
        return timeMarked;
    }

    public void setTimeMarked(Timestamp timeMarked) {
        this.timeMarked = timeMarked;
    }

    // toString method for debugging
    @Override
    public String toString() {
        return "Attendance{" +
                "attendanceId=" + attendanceId +
                ", enrollmentId=" + enrollmentId +
                ", attendanceDate=" + attendanceDate +
                ", status='" + status + '\'' +
                ", timeMarked=" + timeMarked +
                '}';
    }
}
