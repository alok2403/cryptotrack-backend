package com.cryptotrack.backend.controller;

import com.cryptotrack.backend.dto.PortfolioAnalyticsResponse;
import com.cryptotrack.backend.dto.PortfolioRequest;
import com.cryptotrack.backend.entity.Portfolio;
import com.cryptotrack.backend.entity.User;
import com.cryptotrack.backend.repository.UserRepository;
import com.cryptotrack.backend.service.PortfolioService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final UserRepository userRepository;


    public PortfolioController(
            PortfolioService portfolioService,
            UserRepository userRepository) {

        this.portfolioService =
                portfolioService;

        this.userRepository =
                userRepository;
    }


    // =====================================================
    // ADD TO PORTFOLIO
    // =====================================================

    @PostMapping
    public ResponseEntity<?> addToPortfolio(
            @RequestBody PortfolioRequest request,
            Authentication authentication) {

        try {

            if (request == null) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                "Request body is required."
                        );
            }


            User user =
                    getUser(authentication);


            Portfolio portfolio =
                    portfolioService.addToPortfolio(

                            user,

                            request.getCryptoId(),

                            request.getQuantity()
                    );


            return ResponseEntity.ok(
                    portfolio
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            e.getMessage()
                    );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .internalServerError()
                    .body(
                            e.getMessage()
                    );
        }
    }


    // =====================================================
    // GET PORTFOLIO
    // =====================================================

    @GetMapping
    public ResponseEntity<?> getPortfolio(
            Authentication authentication) {

        try {

            User user =
                    getUser(authentication);


            List<Portfolio> portfolio =
                    portfolioService.getPortfolio(
                            user
                    );


            return ResponseEntity.ok(
                    portfolio
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .internalServerError()
                    .body(
                            e.getMessage()
                    );
        }
    }


    // =====================================================
    // ANALYTICS
    // =====================================================

    @GetMapping("/analytics")
    public ResponseEntity<?> getAnalytics(
            Authentication authentication) {

        try {

            User user =
                    getUser(authentication);


            PortfolioAnalyticsResponse response =
                    portfolioService
                            .getPortfolioAnalytics(
                                    user
                            );


            return ResponseEntity.ok(
                    response
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .internalServerError()
                    .body(
                            e.getMessage()
                    );
        }
    }


    // =====================================================
    // GET SINGLE ITEM
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<?> getPortfolioItem(
            @PathVariable Long id,
            Authentication authentication) {

        try {

            User user =
                    getUser(authentication);


            Portfolio portfolio =
                    portfolioService
                            .getPortfolioItem(
                                    user,
                                    id
                            );


            return ResponseEntity.ok(
                    portfolio
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(404)
                    .body(
                            e.getMessage()
                    );
        }
    }


    // =====================================================
    // DELETE
    // =====================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeFromPortfolio(
            @PathVariable Long id,
            Authentication authentication) {

        try {

            User user =
                    getUser(authentication);


            portfolioService
                    .removeFromPortfolio(
                            user,
                            id
                    );


            return ResponseEntity.ok(
                    "Removed from portfolio."
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(404)
                    .body(
                            e.getMessage()
                    );
        }
    }


    // =====================================================
    // GET USER
    // =====================================================

    private User getUser(
            Authentication authentication) {

        if (authentication == null) {

            throw new RuntimeException(
                    "Authentication required."
            );
        }


        String email =
                authentication.getName();


        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found."
                        )
                );
    }
}