
        package com.cryptotrack.backend.controller;

import com.cryptotrack.backend.service.CryptoService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/crypto")
public class CryptoController {

    private static final Logger log =
            LoggerFactory.getLogger(CryptoController.class);

    private final CryptoService cryptoService;

    public CryptoController(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    // =====================================================
    // GET CURRENT CRYPTO DATA
    // =====================================================

    @GetMapping("/{coinId}")
    public ResponseEntity<?> getCrypto(
            @PathVariable String coinId) {

        if (coinId == null || coinId.isBlank()) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "message",
                            "Cryptocurrency ID cannot be empty"
                    ));
        }

        try {

            return ResponseEntity.ok(
                    cryptoService.getCrypto(coinId)
            );

        } catch (Exception e) {

            log.error(
                    "Failed to fetch crypto data for {}: {}",
                    coinId,
                    e.getMessage()
            );

            /*
             * CoinGecko may be rate-limited or temporarily
             * unavailable. This is not a bad client request.
             */
            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of(
                            "message",
                            "Cryptocurrency data is temporarily unavailable. Please try again shortly.",
                            "coinId",
                            coinId
                    ));
        }
    }


    // =====================================================
    // PRICE HISTORY
    // =====================================================

    /*
     * days:
     *
     * 1   = 24 hours
     * 7   = 7 days
     * 30  = 30 days
     * 365 = 1 year
     *
     * Invalid values are normalized to 7 days
     * inside CryptoService.
     */
    @GetMapping("/{coinId}/history")
    public ResponseEntity<?> getPriceHistory(
            @PathVariable String coinId,
            @RequestParam(defaultValue = "7") int days) {

        if (coinId == null || coinId.isBlank()) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "message",
                            "Cryptocurrency ID cannot be empty"
                    ));
        }

        try {

            return ResponseEntity.ok(
                    cryptoService.getPriceHistory(
                            coinId,
                            days
                    )
            );

        } catch (Exception e) {

            log.error(
                    "Failed to fetch {}-day price history for {}: {}",
                    days,
                    coinId,
                    e.getMessage()
            );

            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of(
                            "message",
                            "Price history is temporarily unavailable. Please try again shortly.",
                            "coinId",
                            coinId,
                            "days",
                            days
                    ));
        }
    }


    // =====================================================
    // SEARCH CRYPTOCURRENCIES
    // =====================================================

    @GetMapping("/search")
    public ResponseEntity<?> searchCoins(
            @RequestParam String query) {

        if (query == null || query.isBlank()) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "message",
                            "Search query cannot be empty"
                    ));
        }

        try {

            return ResponseEntity.ok(
                    cryptoService.searchCoins(query)
            );

        } catch (Exception e) {

            /*
             * Search is non-critical.
             * Returning an empty list keeps the frontend
             * from breaking when CoinGecko is unavailable.
             */
            log.error(
                    "Failed to search coins for query '{}': {}",
                    query,
                    e.getMessage()
            );

            return ResponseEntity.ok(
                    List.of()
            );
        }
    }
}

