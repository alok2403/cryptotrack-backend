package com.cryptotrack.backend.service;

import com.cryptotrack.backend.entity.User;
import com.cryptotrack.backend.entity.Watchlist;
import com.cryptotrack.backend.repository.WatchlistRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WatchlistService {

    private final WatchlistRepository watchlistRepository;

    public WatchlistService(
            WatchlistRepository watchlistRepository) {

        this.watchlistRepository = watchlistRepository;
    }

    @Transactional
    public Watchlist addToWatchlist(
            User user,
            String cryptoId,
            String cryptoName,
            String symbol) {

        cryptoId = cryptoId.trim().toLowerCase();

        if (watchlistRepository
                .findByUserAndCryptoId(user, cryptoId)
                .isPresent()) {

            throw new IllegalStateException(
                    "Cryptocurrency already in watchlist"
            );
        }

        Watchlist watchlist = new Watchlist();

        watchlist.setCryptoId(cryptoId);
        watchlist.setCryptoName(cryptoName);
        watchlist.setSymbol(symbol);
        watchlist.setUser(user);

        return watchlistRepository.save(watchlist);
    }

    @Transactional(readOnly = true)
    public List<Watchlist> getUserWatchlist(User user) {

        return watchlistRepository.findByUser(user);
    }

    @Transactional
    public boolean removeFromWatchlist(
            User user,
            String cryptoId) {

        cryptoId = cryptoId.trim().toLowerCase();

        Watchlist watchlist =
                watchlistRepository
                        .findByUserAndCryptoId(
                                user,
                                cryptoId
                        )
                        .orElse(null);

        if (watchlist == null) {
            return false;
        }

        watchlistRepository.delete(watchlist);

        return true;
    }
}