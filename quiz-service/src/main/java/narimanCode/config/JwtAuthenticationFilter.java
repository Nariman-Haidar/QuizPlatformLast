package narimanCode.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final RestTemplate restTemplate;

    public JwtAuthenticationFilter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + token);
                HttpEntity<String> entity = new HttpEntity<>(headers);
                logger.debug("Validating JWT token with user-service");
                ResponseEntity<Map> result = restTemplate.exchange(
                        "http://user-service:8081/auth/validate",
                        HttpMethod.GET,
                        entity,
                        Map.class
                );
                Map<String, Object> validationResponse = result.getBody();
                
                if (validationResponse != null && result.getStatusCode().is2xxSuccessful()) {
                    boolean isValid = (boolean) validationResponse.get("valid");
                    
                    if (isValid) {
                        Long userId = Long.valueOf(validationResponse.get("userId").toString());
                        String username = (String) validationResponse.get("username");
                        List<String> roles = (List<String>) validationResponse.get("roles");
                        
                        List<SimpleGrantedAuthority> authorities = roles.stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList());
                        
                        logger.info("Authenticated user: {} with userId: {} and roles: {}", username, userId, roles);
                        
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                userId, null, authorities
                        );
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    } else {
                        String error = (String) validationResponse.get("error");
                        logger.warn("Invalid JWT token: {}", error);
                        SecurityContextHolder.clearContext();
                    }
                } else {
                    logger.warn("Invalid JWT token validation response");
                    SecurityContextHolder.clearContext();
                }
            } catch (Exception e) {
                logger.error("JWT validation failed: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            }
        } else {
            logger.debug("No Bearer token found in request");
        }
        filterChain.doFilter(request, response);
    }
}