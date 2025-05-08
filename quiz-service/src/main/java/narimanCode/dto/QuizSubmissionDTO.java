package narimanCode.dto;

import java.util.Map;

public class QuizSubmissionDTO {
    private Map<Long, String> answers; // questionId -> userAnswer

    // Getters and Setters
    public Map<Long, String> getAnswers() { return answers; }
    public void setAnswers(Map<Long, String> answers) { this.answers = answers; }
}