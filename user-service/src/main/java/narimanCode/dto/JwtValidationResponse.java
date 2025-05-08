package narimanCode.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtValidationResponse {
    private boolean valid;
    private String username;
    private Long userId;
    private List<String> roles;
    private String error;
} 