package com.cryptotrack.backend.controller;

import com.cryptotrack.backend.entity.User;
import com.cryptotrack.backend.entity.Watchlist;
import com.cryptotrack.backend.repository.UserRepository;
import com.cryptotrack.backend.service.WatchlistService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController {

    private final WatchlistService watchlistService;
    private final UserRepository userRepository;

    public WatchlistController(
            WatchlistService watchlistService,
            UserRepository userRepository) {

        this.watchlistService = watchlistService;
        this.userRepository = userRepository;
    }


    // =========================
    // ADD TO WATCHLIST
    // =========================

    @PostMapping
    public ResponseEntity<?> addToWatchlist(
            @RequestBody WatchlistRequest request,
            Authentication authentication) {

        try {

            String email = authentication.getName();

            User user = userRepository
                    .findByEmail(email)
                    .orElseThrow(() ->
                            new RuntimeException("User not found")
                    );

            Watchlist watchlist = watchlistService.addToWatchlist(
                    user,
                    request.getCryptoId(),
                    request.getCryptoName(),
                    request.getSymbol()
            );

            return ResponseEntity.ok(watchlist);

        } catch (IllegalStateException e) {

            // Already in watchlist - not a server error, just a conflict
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("message", e.getMessage()));

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Failed to add to watchlist"));
        }
    }


    // =========================
    // GET USER WATCHLIST
    // =========================

    @GetMapping
    public ResponseEntity<?> getWatchlist(
            Authentication authentication) {

        try {

            String email = authentication.getName();

            User user = userRepository
                    .findByEmail(email)
                    .orElseThrow(() ->
                            new RuntimeException("User not found")
                    );

            List<Watchlist> watchlist =
                    watchlistService.getUserWatchlist(user);

            return ResponseEntity.ok(watchlist);

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Failed to load watchlist"));
        }
    }


    // =========================
    // REMOVE FROM WATCHLIST
    // =========================

    @DeleteMapping("/{cryptoId}")
    public ResponseEntity<?> removeFromWatchlist(
            @PathVariable String cryptoId,
            Authentication authentication) {

        try {

            String email = authentication.getName();

            User user = userRepository
                    .findByEmail(email)
                    .orElseThrow(() ->
                            new RuntimeException("User not found")
                    );

            watchlistService.removeFromWatchlist(
                    user,
                    cryptoId
            );

            return ResponseEntity.ok(
                    Map.of("message", "Removed from watchlist")
            );

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Failed to remove from watchlist"));
        }
    }


    // =========================
    // REQUEST DTO
    // =========================

    public static class WatchlistRequest {

        private String cryptoId;
        private String cryptoName;
        private String symbol;


        public String getCryptoId() {
            return cryptoId;
        }

        public void setCryptoId(String cryptoId) {
            this.cryptoId = cryptoId;
        }


        public String getCryptoName() {
            return cryptoName;
        }

        public void setCryptoName(String cryptoName) {
            this.cryptoName = cryptoName;
        }


        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }
    }
}