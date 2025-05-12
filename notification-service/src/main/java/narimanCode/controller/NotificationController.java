package narimanCode.controller;

import narimanCode.dto.QuizCompletedEvent;
import narimanCode.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/test")
    public ResponseEntity<String> testNotification(@RequestBody QuizCompletedEvent event) {
        notificationService.processQuizCompletion(event);
        return ResponseEntity.ok("Notification processed successfully");
    }
}