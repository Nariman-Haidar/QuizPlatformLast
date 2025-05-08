package narimanCode.event;

public class QuizCompletedEvent {
    private Long userId;
    private Long quizId;
    private String quizCategory;
    private int score;

    public QuizCompletedEvent(Long userId, Long quizId, String quizCategory, int score) {
        this.userId = userId;
        this.quizId = quizId;
        this.quizCategory = quizCategory;
        this.score = score;
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getQuizId() { return quizId; }
    public void setQuizId(Long quizId) { this.quizId = quizId; }
    public String getQuizCategory() { return quizCategory; }
    public void setQuizCategory(String quizCategory) { this.quizCategory = quizCategory; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
}