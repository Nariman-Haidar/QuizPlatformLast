package narimanCode.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import narimanCode.config.TestSecurityConfig;
import narimanCode.config.TestExceptionHandler;
import narimanCode.dto.PasswordResetRequest;
import narimanCode.exception.BadRequestException;
import narimanCode.security.JwtAuthenticationFilter;
import narimanCode.service.AuthService;
import narimanCode.service.PasswordResetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({TestSecurityConfig.class, TestExceptionHandler.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private PasswordResetService passwordResetService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void requestPasswordReset_ShouldReturnToken() throws Exception {
        // Given
        String email = "test@example.com";
        String expectedToken = "test-reset-token";
        when(passwordResetService.initiatePasswordReset(email)).thenReturn(expectedToken);

        // When & Then
        mockMvc.perform(post("/auth/password-reset/request")
                        .param("email", email)
                        .accept(MediaType.TEXT_PLAIN))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string("Reset token: " + expectedToken));
    }

    @Test
    void resetPassword_WithValidToken_ShouldSucceed() throws Exception {
        // Given
        PasswordResetRequest request = new PasswordResetRequest();
        request.setToken("valid-token");
        request.setNewPassword("newPassword123!");
        
        doNothing().when(authService).resetPassword(any(PasswordResetRequest.class));

        // When & Then
        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.TEXT_PLAIN)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string("Password reset successful."));
    }

    @Test
    void resetPassword_WithInvalidToken_ShouldReturnBadRequest() throws Exception {
        // Given
        PasswordResetRequest request = new PasswordResetRequest();
        request.setToken("invalid-token");
        request.setNewPassword("newPassword123!");
        
        doThrow(new BadRequestException("Invalid or expired password reset token"))
            .when(authService).resetPassword(any(PasswordResetRequest.class));

        // When & Then
        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid or expired password reset token"))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.path").value("/auth/reset-password"));
    }
} 