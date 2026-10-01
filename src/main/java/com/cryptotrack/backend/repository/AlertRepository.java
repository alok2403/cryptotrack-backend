package com.cryptotrack.backend.repository;

import com.cryptotrack.backend.entity.Alert;
import com.cryptotrack.backend.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlertRepository
        extends JpaRepository<Alert, Long> {

    List<Alert> findByUser(User user);

    List<Alert> findByUserAndActive(
            User user,
            Boolean active
    );

    Optional<Alert> findByIdAndUser(
            Long id,
            User user
    );

    List<Alert> findByActiveTrue();
}