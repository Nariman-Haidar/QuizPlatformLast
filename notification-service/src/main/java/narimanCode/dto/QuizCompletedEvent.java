package narimanCode.dto;

import java.time.LocalDateTime;

public class QuizCompletedEvent {
    private Long userId;
    private Long quizId;
    private String quizTitle;
    private Double score;
    private LocalDateTime completedAt;

    public QuizCompletedEvent() {
    }

    public QuizCompletedEvent(Long userId, Long quizId, String quizTitle, Double score, LocalDateTime completedAt) {
        this.userId = userId;
        this.quizId = quizId;
        this.quizTitle = quizTitle;
        this.score = score;
        this.completedAt = completedAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public String getQuizTitle() {
        return quizTitle;
    }

    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
} 