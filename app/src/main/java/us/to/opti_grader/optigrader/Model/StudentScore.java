package us.to.opti_grader.optigrader.Model;

public class StudentScore {
    private String score;
    private String id;

    public StudentScore() {
    }

    public StudentScore(String score, String id) {
        this.score = score;
        this.id = id;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
