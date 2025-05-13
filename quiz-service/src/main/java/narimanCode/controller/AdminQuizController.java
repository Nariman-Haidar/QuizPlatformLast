package narimanCode.controller;

import narimanCode.dto.*;
import narimanCode.service.QuizService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/admin/quizzes")
@Validated
public class AdminQuizController {
    private static final Logger logger = LoggerFactory.getLogger(AdminQuizController.class);
    private final QuizService quizService;

    public AdminQuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping
    public ResponseEntity<?> createQuiz(@Valid @RequestBody QuizDTO quizDTO) {
        try {
            logger.info("Creating quiz with title: {}", quizDTO.getTitle());
            QuizDTO createdQuiz = quizService.createQuiz(quizDTO);
            return ResponseEntity.ok(createdQuiz);
        } catch (Exception e) {
            logger.error("Error creating quiz: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create quiz: " + e.getMessage());
        }
    }

    @PostMapping("/{quizId}/questions")
    public ResponseEntity<?> addQuestion(@PathVariable Long quizId, @Valid @RequestBody QuestionDTO questionDTO) {
        try {
            logger.info("Adding question to quizId: {}", quizId);
            QuestionDTO addedQuestion = quizService.addQuestion(quizId, questionDTO);
            return ResponseEntity.ok(addedQuestion);
        } catch (Exception e) {
            logger.error("Error adding question to quiz {}: {}", quizId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add question: " + e.getMessage());
        }
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUserQuizSummaries() {
        try {
            logger.info("Fetching user quiz summaries");
            List<UserQuizSummaryDTO> summaries = quizService.getUserQuizSummaries();
            return ResponseEntity.ok(summaries);
        } catch (Exception e) {
            logger.error("Error fetching user quiz summaries: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch summaries: " + e.getMessage());
        }
    }
}