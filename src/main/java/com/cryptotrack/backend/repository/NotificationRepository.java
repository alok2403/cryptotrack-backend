package com.cryptotrack.backend.repository;

import com.cryptotrack.backend.entity.Notification;
import com.cryptotrack.backend.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {


    // =====================================================
    // ALL NOTIFICATIONS
    // =====================================================

    List<Notification>
    findByUserOrderByCreatedAtDesc(
            User user
    );


    // =====================================================
    // UNREAD NOTIFICATIONS
    // =====================================================

    List<Notification>
    findByUserAndReadFalseOrderByCreatedAtDesc(
            User user
    );


    // =====================================================
    // UNREAD COUNT
    // =====================================================

    long countByUserAndReadFalse(
            User user
    );


    // =====================================================
    // FIND BY ID + USER
    // =====================================================

    Optional<Notification>
    findByIdAndUser(
            Long id,
            User user
    );
}