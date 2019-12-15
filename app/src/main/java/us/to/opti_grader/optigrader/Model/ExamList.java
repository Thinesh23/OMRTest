package us.to.opti_grader.optigrader.Model;

public class ExamList {
    private String examType;
    private String studentNo;
    private String subjectName;
    private String subjectId;
    private String totalQues;

    public ExamList() {
    }

    public ExamList(String examType, String studentNo, String subjectName, String subjectId, String totalQues) {
        this.examType = examType;
        this.studentNo = studentNo;
        this.subjectName = subjectName;
        this.subjectId = subjectId;

        this.totalQues = totalQues;

    }

    public String getTotalQues() {
        return totalQues;
    }

    public void setTotalQues(String totalQues) {
        this.totalQues = totalQues;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getExamType() {
        return examType;
    }

    public void setExamType(String examType) {
        this.examType = examType;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }
}
