package narimanCode.dto;

public class UserQuizSummaryDTO {
    private Long userId;
    private String username;
    private String quizCategory;

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getQuizCategory() { return quizCategory; }
    public void setQuizCategory(String quizCategory) { this.quizCategory = quizCategory; }
}