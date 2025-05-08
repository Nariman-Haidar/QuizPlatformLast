package narimanCode.service;

import narimanCode.event.QuizCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);
    private final KafkaTemplate<String, QuizCompletedEvent> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, QuizCompletedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendQuizCompletedEvent(QuizCompletedEvent event) {
        logger.info("Sending QuizCompletedEvent for userId: {}, quizId: {}", event.getUserId(), event.getQuizId());
        kafkaTemplate.send("quiz-completed", String.valueOf(event.getUserId()), event);
        logger.debug("QuizCompletedEvent sent successfully");
    }
}