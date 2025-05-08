package narimanCode.service;

import lombok.RequiredArgsConstructor;
import narimanCode.dto.PasswordResetEvent;
import narimanCode.entity.PasswordResetToken;
import narimanCode.entity.Person;
import narimanCode.exception.BadRequestException;
import narimanCode.exception.ResourceNotFoundException;
import narimanCode.repository.PasswordResetTokenRepository;
import narimanCode.repository.PersonRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final RedisTemplate<String, String> redisTemplate;
    private final KafkaTemplate<String, PasswordResetEvent> kafkaTemplate;
    private final PersonRepository personRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private static final String RESET_TOKEN_PREFIX = "password:reset:";
    private static final long TOKEN_VALIDITY = 3600; // 1 hour in seconds

    public String initiatePasswordReset(String email) {
        Person person = personRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Person", "email", email));

        String token = generateResetToken();
        LocalDateTime expiryDate = LocalDateTime.now().plusSeconds(TOKEN_VALIDITY);

        PasswordResetToken resetToken = new PasswordResetToken(token, person, expiryDate);
        tokenRepository.save(resetToken);

        String key = RESET_TOKEN_PREFIX + token;
        redisTemplate.opsForValue().set(key, email, TOKEN_VALIDITY, TimeUnit.SECONDS);

        // Temporarily disable Kafka for testing
        // PasswordResetEvent event = new PasswordResetEvent(email, token);
        // kafkaTemplate.send("password-reset-events", event);
        
        return token;
    }

    public boolean validateResetToken(String token) {
        String key = RESET_TOKEN_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public String getEmailFromToken(String token) {
        String key = RESET_TOKEN_PREFIX + token;
        String email = redisTemplate.opsForValue().get(key);
        if (email == null) {
            throw new BadRequestException("Invalid or expired token");
        }
        return email;
    }

    public void invalidateToken(String token) {
        String key = RESET_TOKEN_PREFIX + token;
        redisTemplate.delete(key);
        tokenRepository.findByToken(token)
                .ifPresent(tokenRepository::delete);
    }

    private String generateResetToken() {
        return UUID.randomUUID().toString();
    }
}