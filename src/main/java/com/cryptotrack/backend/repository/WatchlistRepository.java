package com.cryptotrack.backend.repository;

import com.cryptotrack.backend.entity.User;
import com.cryptotrack.backend.entity.Watchlist;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WatchlistRepository
        extends JpaRepository<Watchlist, Long> {

    List<Watchlist> findByUser(User user);

    Optional<Watchlist> findByUserAndCryptoId(
            User user,
            String cryptoId
    );
}