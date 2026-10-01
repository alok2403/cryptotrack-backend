package com.cryptotrack.backend.repository;

import com.cryptotrack.backend.entity.PriceAlert;
import com.cryptotrack.backend.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PriceAlertRepository
        extends JpaRepository<PriceAlert, Long> {

    List<PriceAlert> findByUser(
            User user
    );

    List<PriceAlert> findByUserAndActive(
            User user,
            boolean active
    );

    List<PriceAlert> findByActiveTrue();

    Optional<PriceAlert> findByIdAndUser(
            Long id,
            User user
    );
}