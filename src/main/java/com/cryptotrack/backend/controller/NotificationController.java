package com.cryptotrack.backend.controller;

import com.cryptotrack.backend.entity.Notification;
import com.cryptotrack.backend.entity.User;
import com.cryptotrack.backend.service.NotificationService;
import com.cryptotrack.backend.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:3000")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    public NotificationController(
            NotificationService notificationService,
            UserService userService) {

        this.notificationService = notificationService;
        this.userService = userService;
    }

    private User getCurrentUser(Authentication authentication) {

        String email = authentication.getName();

        User user = userService.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found: " + email);
        }

        return user;
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications(
            Authentication authentication) {

        User user = getCurrentUser(authentication);

        return ResponseEntity.ok(
                notificationService.getUserNotifications(user)
        );
    }

    @GetMapping("/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(
            Authentication authentication) {

        User user = getCurrentUser(authentication);

        return ResponseEntity.ok(
                notificationService.getUnreadNotifications(user)
        );
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long id,
            Authentication authentication) {

        User user = getCurrentUser(authentication);

        notificationService.markAsRead(id, user);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(
            Authentication authentication) {

        User user = getCurrentUser(authentication);

        notificationService.markAllAsRead(user);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable Long id,
            Authentication authentication) {

        User user = getCurrentUser(authentication);

        notificationService.deleteNotification(id, user);

        return ResponseEntity.ok().build();
    }
}