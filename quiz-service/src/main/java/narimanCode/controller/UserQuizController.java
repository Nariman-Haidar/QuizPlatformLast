package narimanCode.controller;

import narimanCode.dto.*;
import narimanCode.entity.QuizAttempt;
import narimanCode.service.QuizService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/quizzes")
@Validated
public class UserQuizController {
    private static final Logger logger = LoggerFactory.getLogger(UserQuizController.class);
    private final QuizService quizService;

    public UserQuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping
    public ResponseEntity<?> getAllQuizzes() {
        try {
            logger.info("Fetching all quizzes");
            List<QuizDTO> quizzes = quizService.getAllQuizzes();
            return ResponseEntity.ok(quizzes);
        } catch (Exception e) {
            logger.error("Error fetching quizzes: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch quizzes: " + e.getMessage());
        }
    }

    @GetMapping("/{quizId}")
    public ResponseEntity<?> getQuiz(@PathVariable Long quizId) {
        try {
            logger.info("Fetching quiz with id: {}", quizId);
            QuizDTO quiz = quizService.getQuiz(quizId);
            return ResponseEntity.ok(quiz);
        } catch (Exception e) {
            logger.error("Error fetching quiz {}: {}", quizId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch quiz: " + e.getMessage());
        }
    }

    @PostMapping("/{quizId}/submit")
    public ResponseEntity<?> submitQuiz(@PathVariable Long quizId,
                                        @Valid @RequestBody QuizSubmissionDTO submission,
                                        Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            logger.info("User {} submitting quiz {}", userId, quizId);
            int score = quizService.submitQuiz(quizId, userId, submission);
            return ResponseEntity.ok(score);
        } catch (Exception e) {
            logger.error("Error submitting quiz {}: {}", quizId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to submit quiz: " + e.getMessage());
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> getQuizHistory(Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            logger.info("Fetching quiz history for user {}", userId);
            List<QuizAttempt> history = quizService.getUserQuizHistory(userId);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            logger.error("Error fetching quiz history: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch history: " + e.getMessage());
        }
    }
}