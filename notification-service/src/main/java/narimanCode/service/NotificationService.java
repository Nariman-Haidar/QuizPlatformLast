package narimanCode.service;

import narimanCode.dto.QuizCompletedEvent;
import narimanCode.model.Notification;
import narimanCode.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private final UserServiceClient userServiceClient;
    private final EmailService emailService;
    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationService(UserServiceClient userServiceClient, EmailService emailService,
                               NotificationRepository notificationRepository) {
        this.userServiceClient = userServiceClient;
        this.emailService = emailService;
        this.notificationRepository = notificationRepository;
    }

    public void processQuizCompletion(QuizCompletedEvent event) {
        logger.info("Processing QuizCompletedEvent for userId: {}", event.getUserId());
        String email = userServiceClient.getUserEmail(event.getUserId());
        if (email == null) {
            logger.warn("No email found for userId: {}", event.getUserId());
            saveNotification(event, "No email found", "FAILED");
            return;
        }

        String subject = "Quiz Completion Notification";
        String message = String.format("Congratulations! You completed the %s quiz with a score of %.2f.",
                event.getQuizTitle(), event.getScore());
        boolean emailSent = emailService.sendEmail(email, subject, message);
        String status = emailSent ? "SENT" : "FAILED";
        saveNotification(event, message, status);
    }

    private void saveNotification(QuizCompletedEvent event, String message, String status) {
        Notification notification = new Notification();
        notification.setUserId(event.getUserId());
        notification.setMessage(message);
        notification.setSentAt(LocalDateTime.now());
        notification.setStatus(status);
        notificationRepository.save(notification);
        logger.info("Notification saved for userId: {}, status: {}", event.getUserId(), status);
    }
} 