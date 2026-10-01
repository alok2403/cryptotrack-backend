package com.cryptotrack.backend.repository;

import com.cryptotrack.backend.entity.Portfolio;
import com.cryptotrack.backend.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PortfolioRepository
        extends JpaRepository<Portfolio, Long> {

    List<Portfolio> findByUser(
            User user
    );

    Optional<Portfolio> findByUserAndCryptoId(
            User user,
            String cryptoId
    );

    Optional<Portfolio> findByUserAndId(
            User user,
            Long id
    );

    void deleteByUserAndId(
            User user,
            Long id
    );
}