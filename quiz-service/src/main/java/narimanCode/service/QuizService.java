package narimanCode.service;

import narimanCode.dto.*;
import narimanCode.entity.*;
import narimanCode.event.QuizCompletedEvent;
import narimanCode.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QuizService {
    private static final Logger logger = LoggerFactory.getLogger(QuizService.class);
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final KafkaProducerService kafkaProducerService;

    public QuizService(QuizRepository quizRepository, QuestionRepository questionRepository,
                       QuizAttemptRepository quizAttemptRepository, KafkaProducerService kafkaProducerService) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    @Transactional
    public QuizDTO createQuiz(QuizDTO quizDTO) {
        try {
            logger.info("Creating quiz: {}", quizDTO.getTitle());
            Quiz quiz = new Quiz();
            quiz.setCategory(quizDTO.getCategory());
            quiz.setTitle(quizDTO.getTitle());
            quiz.setTimeLimit(quizDTO.getTimeLimit());
            quiz.setQuestions(List.of());
            quizRepository.save(quiz);
            return mapToQuizDTO(quiz);
        } catch (Exception e) {
            logger.error("Failed to create quiz: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create quiz", e);
        }
    }

    @Transactional
    public QuestionDTO addQuestion(Long quizId, QuestionDTO questionDTO) {
        try {
            logger.info("Adding question to quizId: {}", quizId);
            Quiz quiz = quizRepository.findById(quizId)
                    .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + quizId));
            Question question = new Question();
            question.setText(questionDTO.getText());
            question.setOptions(questionDTO.getOptions());
            question.setCorrectAnswer(questionDTO.getCorrectAnswer());
            question.setQuiz(quiz);
            questionRepository.save(question);
            return mapToQuestionDTO(question);
        } catch (Exception e) {
            logger.error("Failed to add question to quiz {}: {}", quizId, e.getMessage(), e);
            throw new RuntimeException("Failed to add question", e);
        }
    }

    public List<QuizDTO> getAllQuizzes() {
        try {
            logger.info("Fetching all quizzes");
            return quizRepository.findAll().stream()
                    .map(this::mapToQuizDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Failed to fetch quizzes: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch quizzes", e);
        }
    }

    @Transactional(readOnly = true)
    public QuizDTO getQuiz(Long quizId) {
        try {
            logger.info("Fetching quiz with id: {}", quizId);
            Quiz quiz = quizRepository.findById(quizId)
                    .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + quizId));
            return mapToQuizDTO(quiz);
        } catch (Exception e) {
            logger.error("Failed to fetch quiz {}: {}", quizId, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch quiz", e);
        }
    }

    @Transactional
    public int submitQuiz(Long quizId, Long userId, QuizSubmissionDTO submission) {
        try {
            logger.info("User {} submitting quiz {}", userId, quizId);
            Quiz quiz = quizRepository.findById(quizId)
                    .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + quizId));
            int score = calculateScore(quiz, submission.getAnswers());
            QuizAttempt attempt = new QuizAttempt();
            attempt.setUserId(userId);
            attempt.setQuiz(quiz);
            attempt.setAnswers(submission.getAnswers());
            attempt.setScore(score);
            attempt.setSubmittedAt(LocalDateTime.now());
            quizAttemptRepository.save(attempt);
            kafkaProducerService.sendQuizCompletedEvent(new QuizCompletedEvent(userId, quizId, quiz.getCategory(), score));
            return score;
        } catch (Exception e) {
            logger.error("Failed to submit quiz {}: {}", quizId, e.getMessage(), e);
            throw new RuntimeException("Failed to submit quiz", e);
        }
    }

    public List<QuizAttempt> getUserQuizHistory(Long userId) {
        try {
            logger.info("Fetching quiz history for user {}", userId);
            return quizAttemptRepository.findByUserId(userId);
        } catch (Exception e) {
            logger.error("Failed to fetch quiz history for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch quiz history", e);
        }
    }

    public List<UserQuizSummaryDTO> getUserQuizSummaries() {
        try {
            logger.info("Fetching user quiz summaries");
            return quizAttemptRepository.findUserQuizCategories().stream()
                    .map(obj -> {
                        UserQuizSummaryDTO dto = new UserQuizSummaryDTO();
                        dto.setUserId((Long) obj[0]);
                        dto.setUsername("Unknown"); // Fetch from user-service later
                        dto.setQuizCategory((String) obj[1]);
                        return dto;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Failed to fetch user quiz summaries: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch user quiz summaries", e);
        }
    }

    private int calculateScore(Quiz quiz, Map<Long, String> answers) {
        int score = 0;
        for (Question question : quiz.getQuestions()) {
            if (answers.containsKey(question.getId()) &&
                    answers.get(question.getId()).equals(question.getCorrectAnswer())) {
                score++;
            }
        }
        return score;
    }

    private QuizDTO mapToQuizDTO(Quiz quiz) {
        QuizDTO dto = new QuizDTO();
        dto.setId(quiz.getId());
        dto.setCategory(quiz.getCategory());
        dto.setTitle(quiz.getTitle());
        dto.setTimeLimit(quiz.getTimeLimit());
        dto.setQuestions(quiz.getQuestions().stream()
                .map(this::mapToQuestionDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    private QuestionDTO mapToQuestionDTO(Question question) {
        QuestionDTO dto = new QuestionDTO();
        dto.setId(question.getId());
        dto.setText(question.getText());
        dto.setOptions(question.getOptions());
        // Hide correctAnswer for users
        return dto;
    }
}