package com.cryptotrack.backend.service;

import com.cryptotrack.backend.entity.Notification;
import com.cryptotrack.backend.entity.User;
import com.cryptotrack.backend.repository.NotificationRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;


    public NotificationService(
            NotificationRepository notificationRepository) {

        this.notificationRepository =
                notificationRepository;
    }


    // =====================================================
    // CREATE NOTIFICATION
    // =====================================================

    @Transactional
    public Notification createNotification(
            User user,
            String message) {

        if (user == null) {
            throw new IllegalArgumentException(
                    "User cannot be null"
            );
        }

        if (message == null ||
                message.isBlank()) {

            throw new IllegalArgumentException(
                    "Notification message cannot be empty"
            );
        }

        Notification notification =
                new Notification(
                        message,
                        user
                );

        return notificationRepository.save(
                notification
        );
    }


    // =====================================================
    // GET ALL NOTIFICATIONS
    // =====================================================

    @Transactional(readOnly = true)
    public List<Notification> getUserNotifications(
            User user) {

        return notificationRepository
                .findByUserOrderByCreatedAtDesc(user);
    }


    // =====================================================
    // GET UNREAD NOTIFICATIONS
    // =====================================================

    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotifications(
            User user) {

        return notificationRepository
                .findByUserAndReadFalseOrderByCreatedAtDesc(
                        user
                );
    }


    // =====================================================
    // GET UNREAD COUNT
    // =====================================================

    @Transactional(readOnly = true)
    public long getUnreadNotificationCount(
            User user) {

        return notificationRepository
                .countByUserAndReadFalse(user);
    }


    // =====================================================
    // MARK ONE AS READ
    // =====================================================

    @Transactional
    public void markAsRead(
            Long id,
            User user) {

        Notification notification =
                notificationRepository
                        .findByIdAndUser(id, user)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Notification not found"
                                )
                        );

        notification.setRead(true);

        notificationRepository.save(
                notification
        );
    }


    // =====================================================
    // MARK ALL AS READ
    // =====================================================

    @Transactional
    public void markAllAsRead(
            User user) {

        List<Notification> notifications =
                notificationRepository
                        .findByUserAndReadFalseOrderByCreatedAtDesc(
                                user
                        );

        if (notifications.isEmpty()) {
            return;
        }

        for (Notification notification :
                notifications) {

            notification.setRead(true);
        }

        notificationRepository.saveAll(
                notifications
        );
    }


    // =====================================================
    // DELETE
    // =====================================================

    @Transactional
    public void deleteNotification(
            Long id,
            User user) {

        Notification notification =
                notificationRepository
                        .findByIdAndUser(id, user)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Notification not found"
                                )
                        );

        notificationRepository.delete(
                notification
        );
    }
}