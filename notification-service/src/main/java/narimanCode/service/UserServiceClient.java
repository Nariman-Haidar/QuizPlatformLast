package narimanCode.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class UserServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceClient.class);
    private final RestTemplate restTemplate;

    @Autowired
    public UserServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getUserEmail(Long userId) {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(
                    "http://user-service:8081/users/{id}", Map.class, userId);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return (String) response.getBody().get("email");
            }
            logger.warn("User not found for ID: {}", userId);
            return null;
        } catch (Exception e) {
            logger.error("Error fetching user email for ID: {}", userId, e);
            return null;
        }
    }
} 