package narimanCode.dto;

import lombok.Data;

@Data
public class PasswordResetEvent {
    private String email;
    private String token;

    public PasswordResetEvent() {}

    public PasswordResetEvent(String email, String token) {
        this.email = email;
        this.token = token;
    }
}
