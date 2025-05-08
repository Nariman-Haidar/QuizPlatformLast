package narimanCode.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class AuthResponse {
    private String token;
    private String username;
    private List<String> roles;
    private Long userId;
}