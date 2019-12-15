package us.to.opti_grader.optigrader.Model;

public class SubjectScore {

    private String subjectName;
    private String averageScore;
    private String studentId;

    public SubjectScore() {
    }

    public SubjectScore(String subjectName, String averageScore, String studentId) {
        this.subjectName = subjectName;
        this.averageScore = averageScore;
        this.studentId = studentId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(String averageScore) {
        this.averageScore = averageScore;
    }
}
