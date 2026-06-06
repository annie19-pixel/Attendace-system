package Model;

public class Lecturer {

    // Private fields
    private String staffNo;
    private int userId;
    private String lecturerName;
    private String department;

    // Constructor
    public Lecturer(String staffNo, int userId, String lecturerName, String department) {
        this.staffNo = staffNo;
        this.userId = userId;
        this.lecturerName = lecturerName;
        this.department = department;
    }

    // Getters
    public String getStaffNo() {
        return staffNo;
    }

    public int getUserId() {
        return userId;
    }

    public String getLecturerName() {
        return lecturerName;
    }

    public String getDepartment() {
        return department;
    }

    // Setters
    public void setStaffNo(String staffNo) {
        this.staffNo = staffNo;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setLecturerName(String lecturerName) {
        this.lecturerName = lecturerName;
    }

    public void setDepartment(String department) {
        this.department = department;
    }


    @Override
    public String toString() {
        return "Lecturer{" +
                "staffNo='" + staffNo + '\'' +
                ", userId=" + userId +
                ", lecturerName='" + lecturerName + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}
