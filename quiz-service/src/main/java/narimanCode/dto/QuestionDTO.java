package narimanCode.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

@Data
public class QuestionDTO {
    private Long id;

    @NotBlank(message = "Question text cannot be blank")
    @Size(max = 500, message = "Question text must be less than 500 characters")
    private String text;

    @NotEmpty(message = "Options cannot be empty")
    @Size(min = 2, max = 6, message = "Options must be between 2 and 6")
    private List<@NotBlank String> options;

    private String correctAnswer; // Only returned to admins

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }
    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
}