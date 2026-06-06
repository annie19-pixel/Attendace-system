package Model;

public class Student {

    // Private fields
    private String regNo;
    private int userId;
    private String studentName;
    private String yearOfStudy;
    private String stream;

    // Constructor
    public Student(String regNo, int userId, String studentName, String yearOfStudy, String stream) {
        this.regNo = regNo;
        this.userId = userId;
        this.studentName = studentName;
        this.yearOfStudy = yearOfStudy;
        this.stream = stream;
    }

    // Getters
    public String getRegNo() {
        return regNo;
    }

    public int getUserId() {
        return userId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getYearOfStudy() {
        return yearOfStudy;
    }

    public String getStream() {
        return stream;
    }

    // Setters
    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public void setYearOfStudy(String yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }


    @Override
    public String toString() {
        return "Student{" +
                "regNo='" + regNo + '\'' +
                ", userId=" + userId +
                ", studentName='" + studentName + '\'' +
                ", yearOfStudy='" + yearOfStudy + '\'' +
                ", stream='" + stream + '\'' +
                '}';
    }
}
