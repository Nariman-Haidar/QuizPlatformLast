package narimanCode.service;

import narimanCode.dto.QuestionDTO;
import narimanCode.dto.QuizDTO;
import narimanCode.dto.QuizSubmissionDTO;
import narimanCode.entity.Question;
import narimanCode.entity.Quiz;
import narimanCode.entity.QuizAttempt;
import narimanCode.event.QuizCompletedEvent;
import narimanCode.repository.QuestionRepository;
import narimanCode.repository.QuizAttemptRepository;
import narimanCode.repository.QuizRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuizServiceTest {

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private QuizAttemptRepository quizAttemptRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private QuizService quizService;

    private Quiz quiz;
    private Question question1, question2;
    private QuizDTO quizDTO;
    private QuestionDTO questionDTO;

    @BeforeEach
    void setUp() {
        // Setup basic test data
        quiz = new Quiz();
        quiz.setId(1L);
        quiz.setTitle("Java Test Quiz");
        quiz.setCategory("Programming");
        quiz.setTimeLimit(30);

        question1 = new Question();
        question1.setId(1L);
        question1.setText("What is Java?");
        question1.setOptions(List.of("A coffee", "A programming language", "An island", "A dance"));
        question1.setCorrectAnswer("A programming language");
        question1.setQuiz(quiz);

        question2 = new Question();
        question2.setId(2L);
        question2.setText("What is a JVM?");
        question2.setOptions(List.of("Java Virtual Machine", "Java Variable Method", "Just Valuable Method", "Java Visual Mode"));
        question2.setCorrectAnswer("Java Virtual Machine");
        question2.setQuiz(quiz);

        List<Question> questions = new ArrayList<>();
        questions.add(question1);
        questions.add(question2);
        quiz.setQuestions(questions);

        // Setup DTOs
        quizDTO = new QuizDTO();
        quizDTO.setId(1L);
        quizDTO.setTitle("Java Test Quiz");
        quizDTO.setCategory("Programming");
        quizDTO.setTimeLimit(30);

        questionDTO = new QuestionDTO();
        questionDTO.setText("What is Java?");
        questionDTO.setOptions(List.of("A coffee", "A programming language", "An island", "A dance"));
        questionDTO.setCorrectAnswer("A programming language");
    }

    @Test
    void testCreateQuiz() {
        // Given
        when(quizRepository.save(any(Quiz.class))).thenReturn(quiz);

        // When
        QuizDTO result = quizService.createQuiz(quizDTO);

        // Then
        assertNotNull(result);
        assertEquals(quizDTO.getTitle(), result.getTitle());
        assertEquals(quizDTO.getCategory(), result.getCategory());
        assertEquals(quizDTO.getTimeLimit(), result.getTimeLimit());
        verify(quizRepository, times(1)).save(any(Quiz.class));
    }

    @Test
    void testAddQuestion() {
        // Given
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));
        when(questionRepository.save(any(Question.class))).thenReturn(question1);

        // When
        QuestionDTO result = quizService.addQuestion(1L, questionDTO);

        // Then
        assertNotNull(result);
        assertEquals(questionDTO.getText(), result.getText());
        assertEquals(questionDTO.getOptions(), result.getOptions());
        // Note: correctAnswer is intentionally not returned in the DTO for security
        verify(quizRepository, times(1)).findById(1L);
        verify(questionRepository, times(1)).save(any(Question.class));
    }

    @Test
    void testGetAllQuizzes() {
        // Given
        when(quizRepository.findAll()).thenReturn(List.of(quiz));

        // When
        List<QuizDTO> result = quizService.getAllQuizzes();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(quiz.getTitle(), result.get(0).getTitle());
        verify(quizRepository, times(1)).findAll();
    }

    @Test
    void testGetQuiz() {
        // Given
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));

        // When
        QuizDTO result = quizService.getQuiz(1L);

        // Then
        assertNotNull(result);
        assertEquals(quiz.getTitle(), result.getTitle());
        assertEquals(quiz.getCategory(), result.getCategory());
        assertEquals(2, result.getQuestions().size());
        verify(quizRepository, times(1)).findById(1L);
    }

    @Test
    void testSubmitQuiz_FullScore() {
        // Given
        Long userId = 1L;
        Long quizId = 1L;
        
        // Create answers with all correct
        Map<Long, String> answers = new HashMap<>();
        answers.put(1L, "A programming language");
        answers.put(2L, "Java Virtual Machine");
        
        QuizSubmissionDTO submission = new QuizSubmissionDTO();
        submission.setAnswers(answers);
        
        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));
        when(quizAttemptRepository.save(any(QuizAttempt.class))).thenAnswer(i -> i.getArgument(0));
        doNothing().when(kafkaProducerService).sendQuizCompletedEvent(any(QuizCompletedEvent.class));

        // When
        int score = quizService.submitQuiz(quizId, userId, submission);

        // Then
        assertEquals(2, score); // 2 correct answers
        verify(quizRepository, times(1)).findById(quizId);
        verify(quizAttemptRepository, times(1)).save(any(QuizAttempt.class));
        verify(kafkaProducerService, times(1)).sendQuizCompletedEvent(any(QuizCompletedEvent.class));
    }

    @Test
    void testSubmitQuiz_PartialScore() {
        // Given
        Long userId = 1L;
        Long quizId = 1L;
        
        // Create answers with 1 correct, 1 wrong
        Map<Long, String> answers = new HashMap<>();
        answers.put(1L, "A programming language"); // Correct
        answers.put(2L, "Java Variable Method");  // Wrong
        
        QuizSubmissionDTO submission = new QuizSubmissionDTO();
        submission.setAnswers(answers);
        
        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));
        when(quizAttemptRepository.save(any(QuizAttempt.class))).thenAnswer(i -> i.getArgument(0));
        doNothing().when(kafkaProducerService).sendQuizCompletedEvent(any(QuizCompletedEvent.class));

        // When
        int score = quizService.submitQuiz(quizId, userId, submission);

        // Then
        assertEquals(1, score); // 1 correct answer
        verify(quizRepository, times(1)).findById(quizId);
        verify(quizAttemptRepository, times(1)).save(any(QuizAttempt.class));
        verify(kafkaProducerService, times(1)).sendQuizCompletedEvent(any(QuizCompletedEvent.class));
    }

    @Test
    void testGetUserQuizHistory() {
        // Given
        Long userId = 1L;
        List<QuizAttempt> attempts = new ArrayList<>();
        
        QuizAttempt attempt = new QuizAttempt();
        attempt.setId(1L);
        attempt.setUserId(userId);
        attempt.setQuiz(quiz);
        attempt.setScore(2);
        
        attempts.add(attempt);
        
        when(quizAttemptRepository.findByUserId(userId)).thenReturn(attempts);

        // When
        List<QuizAttempt> result = quizService.getUserQuizHistory(userId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getScore());
        verify(quizAttemptRepository, times(1)).findByUserId(userId);
    }
} 