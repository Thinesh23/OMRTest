package us.to.opti_grader.optigrader.Model;

public class ExamScore {

    private String examType;
    private String totalQues;
    private String score;

    public ExamScore() {
    }

    public ExamScore(String examType, String totalQues, String score) {
        this.examType = examType;
        this.totalQues = totalQues;
        this.score = score;
    }

    public String getExamType() {
        return examType;
    }

    public void setExamType(String examType) {
        this.examType = examType;
    }

    public String getTotalQues() {
        return totalQues;
    }

    public void setTotalQues(String totalQues) {
        this.totalQues = totalQues;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}
