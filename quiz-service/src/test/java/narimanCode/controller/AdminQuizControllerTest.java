package narimanCode.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import narimanCode.dto.QuestionDTO;
import narimanCode.dto.QuizDTO;
import narimanCode.service.QuizService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminQuizController.class)
public class AdminQuizControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private QuizService quizService;

    private QuizDTO quizDTO;
    private QuestionDTO questionDTO;

    @BeforeEach
    void setUp() {
        // Setup quiz DTO
        quizDTO = new QuizDTO();
        quizDTO.setId(1L);
        quizDTO.setTitle("Java Programming Quiz");
        quizDTO.setCategory("Programming");
        quizDTO.setTimeLimit(30);
        quizDTO.setQuestions(new ArrayList<>());

        // Setup question DTO
        questionDTO = new QuestionDTO();
        questionDTO.setId(1L);
        questionDTO.setText("What is JVM?");
        questionDTO.setOptions(Arrays.asList(
                "Java Virtual Machine",
                "Java Visual Machine",
                "Java Verified Module",
                "Just Valid Machine"
        ));
        questionDTO.setCorrectAnswer("Java Virtual Machine");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateQuiz() throws Exception {
        // Given
        when(quizService.createQuiz(any(QuizDTO.class))).thenReturn(quizDTO);

        // When & Then
        mockMvc.perform(post("/admin/quizzes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(quizDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Java Programming Quiz"))
                .andExpect(jsonPath("$.category").value("Programming"))
                .andExpect(jsonPath("$.timeLimit").value(30));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAddQuestion() throws Exception {
        // Given
        Long quizId = 1L;
        when(quizService.addQuestion(eq(quizId), any(QuestionDTO.class))).thenReturn(questionDTO);

        // When & Then
        mockMvc.perform(post("/admin/quizzes/{quizId}/questions", quizId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(questionDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("What is JVM?"))
                .andExpect(jsonPath("$.options[0]").value("Java Virtual Machine"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetUserQuizSummaries() throws Exception {
        // Given
        // Create a return value for the getUserQuizSummaries method
        List<Object> summaries = new ArrayList<>();
        
        // When & Then
        mockMvc.perform(get("/admin/quizzes/users")
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateQuiz_Unauthorized() throws Exception {
        // No authentication provided
        mockMvc.perform(post("/admin/quizzes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(quizDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER") // Regular user, not ADMIN
    void testCreateQuiz_Forbidden() throws Exception {
        mockMvc.perform(post("/admin/quizzes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(quizDTO)))
                .andExpect(status().isForbidden());
    }
} 