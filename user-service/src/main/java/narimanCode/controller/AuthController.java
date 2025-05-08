package narimanCode.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import narimanCode.dto.*;
import narimanCode.service.AuthService;
import narimanCode.service.PasswordResetService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegistrationDTO request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
    /*
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
        return ResponseEntity.ok().build();
    }

    */
    @PostMapping("/password-reset/request")
    public ResponseEntity<String> requestPasswordReset(@RequestParam String email) {
        String token = passwordResetService.initiatePasswordReset(email);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body("Reset token: " + token);
    }

    /* 
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok("Password reset successful.");
    }
    */

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body("Password reset successful.");
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = authHeader != null && authHeader.startsWith("Bearer ") ? authHeader.substring(7) : null;
        authService.logout(token);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body("Successfully logged out.");
    }
    
    @GetMapping("/validate")
    public ResponseEntity<JwtValidationResponse> validateToken(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = authHeader != null && authHeader.startsWith("Bearer ") ? authHeader.substring(7) : null;
        
        JwtValidationResponse response = authService.validateToken(token);
        return ResponseEntity.ok(response);
    }
}