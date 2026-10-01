package com.cryptotrack.backend.controller;

import com.cryptotrack.backend.entity.PriceAlert;
import com.cryptotrack.backend.entity.User;
import com.cryptotrack.backend.repository.UserRepository;
import com.cryptotrack.backend.service.PriceAlertService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "http://localhost:5173")
public class PriceAlertController {

    private final PriceAlertService priceAlertService;
    private final UserRepository userRepository;


    public PriceAlertController(
            PriceAlertService priceAlertService,
            UserRepository userRepository) {

        this.priceAlertService =
                priceAlertService;

        this.userRepository =
                userRepository;
    }


    // =====================================================
    // GET ALERTS
    // =====================================================

    @GetMapping
    public ResponseEntity<?> getAlerts(
            Authentication authentication) {

        try {

            User user =
                    getAuthenticatedUser(
                            authentication
                    );

            List<PriceAlert> alerts =
                    priceAlertService
                            .getUserAlerts(user);

            return ResponseEntity.ok(
                    alerts
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(
                            HttpStatus.INTERNAL_SERVER_ERROR
                    )
                    .body(
                            "Could not load price alerts."
                    );
        }
    }


    // =====================================================
    // CREATE ALERT
    // =====================================================

    @PostMapping
    public ResponseEntity<?> createAlert(
            @RequestBody PriceAlert alert,
            Authentication authentication) {

        try {

            User user =
                    getAuthenticatedUser(
                            authentication
                    );


            if (alert == null) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                "Alert data is required."
                        );
            }


            if (alert.getCryptoId() == null ||
                    alert.getCryptoId().isBlank()) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                "Crypto ID is required."
                        );
            }


            if (alert.getTargetPrice() == null ||
                    alert.getTargetPrice() <= 0) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                "Target price must be greater than zero."
                        );
            }


            if (alert.getCondition() == null ||
                    (
                            !alert.getCondition()
                                    .equalsIgnoreCase("ABOVE")
                                    &&
                                    !alert.getCondition()
                                            .equalsIgnoreCase("BELOW")
                    )
            ) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                "Condition must be ABOVE or BELOW."
                        );
            }


            /*
             * NEVER trust user information sent
             * from frontend.
             */

            alert.setUser(user);


            PriceAlert savedAlert =
                    priceAlertService
                            .createAlert(alert);


            return ResponseEntity.ok(
                    savedAlert
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            e.getMessage()
                    );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(
                            HttpStatus.INTERNAL_SERVER_ERROR
                    )
                    .body(
                            "Could not create price alert."
                    );
        }
    }


    // =====================================================
    // TOGGLE ALERT
    // =====================================================

    @PutMapping("/{id}/toggle")
    public ResponseEntity<?> toggleAlert(
            @PathVariable Long id,
            Authentication authentication) {

        try {

            User user =
                    getAuthenticatedUser(
                            authentication
                    );


            PriceAlert updatedAlert =
                    priceAlertService
                            .toggleAlert(
                                    id,
                                    user
                            );


            return ResponseEntity.ok(
                    updatedAlert
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            e.getMessage()
                    );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(
                            HttpStatus.INTERNAL_SERVER_ERROR
                    )
                    .body(
                            "Could not update alert."
                    );
        }
    }


    // =====================================================
    // DELETE
    // =====================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAlert(
            @PathVariable Long id,
            Authentication authentication) {

        try {

            User user =
                    getAuthenticatedUser(
                            authentication
                    );


            priceAlertService.deleteAlert(
                    id,
                    user
            );


            return ResponseEntity.ok(
                    "Alert deleted successfully"
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            e.getMessage()
                    );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(
                            HttpStatus.INTERNAL_SERVER_ERROR
                    )
                    .body(
                            "Could not delete alert."
                    );
        }
    }


    // =====================================================
    // AUTHENTICATED USER
    // =====================================================

    private User getAuthenticatedUser(
            Authentication authentication) {

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new RuntimeException(
                    "Session expired. Please login again."
            );
        }


        String email =
                authentication.getName();


        if (email == null ||
                email.isBlank()) {

            throw new RuntimeException(
                    "Invalid authentication."
            );
        }


        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found for email: "
                                        + email
                        )
                );
    }
}