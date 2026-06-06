package Model;

public class Enrollment {

    // Private fields
    private int enrollmentId;
    private String regNo;
    private int unitId;
    private String staffNo;
    private String semester;
    private String academicYear;

    // Constructor
    public Enrollment(int enrollmentId, String regNo, int unitId,
                      String staffNo, String semester, String academicYear) {
        this.enrollmentId = enrollmentId;
        this.regNo = regNo;
        this.unitId = unitId;
        this.staffNo = staffNo;
        this.semester = semester;
        this.academicYear = academicYear;
    }

    // Getters
    public int getEnrollmentId() {
        return enrollmentId;
    }

    public String getRegNo() {
        return regNo;
    }

    public int getUnitId() {
        return unitId;
    }

    public String getStaffNo() {
        return staffNo;
    }

    public String getSemester() {
        return semester;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    // Setters
    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public void setUnitId(int unitId) {
        this.unitId = unitId;
    }

    public void setStaffNo(String staffNo) {
        this.staffNo = staffNo;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    @Override
    public String toString() {
        return "Enrollment{" +
                "enrollmentId=" + enrollmentId +
                ", regNo='" + regNo + '\'' +
                ", unitId=" + unitId +
                ", staffNo='" + staffNo + '\'' +
                ", semester='" + semester + '\'' +
                ", academicYear='" + academicYear + '\'' +
                '}';
    }
}