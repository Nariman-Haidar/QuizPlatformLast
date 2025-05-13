package narimanCode.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import narimanCode.dto.QuizCompletedEvent;
import narimanCode.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @Autowired
    public KafkaConsumer(NotificationService notificationService, ObjectMapper objectMapper) {
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "quiz-completed", groupId = "notification-service-group")
    public void consumeQuizCompletedEvent(String message) {
        try {
            logger.info("Received QuizCompletedEvent: {}", message);
            QuizCompletedEvent event = objectMapper.readValue(message, QuizCompletedEvent.class);
            notificationService.processQuizCompletion(event);
        } catch (Exception e) {
            logger.error("Error processing QuizCompletedEvent: {}", message, e);
        }
    }
} 