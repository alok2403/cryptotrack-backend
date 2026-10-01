package com.cryptotrack.backend.service;

import com.cryptotrack.backend.entity.Alert;
import com.cryptotrack.backend.entity.User;
import com.cryptotrack.backend.repository.AlertRepository;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class AlertService {

    private final AlertRepository alertRepository;
    private final CryptoService cryptoService;
    private final NotificationService notificationService;


    public AlertService(
            AlertRepository alertRepository,
            CryptoService cryptoService,
            NotificationService notificationService) {

        this.alertRepository = alertRepository;
        this.cryptoService = cryptoService;
        this.notificationService = notificationService;
    }


    // =====================================================
    // CREATE ALERT
    // =====================================================

    @Transactional
    public Alert createAlert(
            User user,
            String cryptoId,
            String cryptoName,
            String symbol,
            Double targetPrice,
            String condition) {

        if (cryptoId == null ||
                cryptoId.isBlank()) {

            throw new RuntimeException(
                    "Crypto ID is required"
            );
        }

        if (targetPrice == null ||
                targetPrice <= 0) {

            throw new RuntimeException(
                    "Target price must be greater than zero"
            );
        }

        if (condition == null ||
                (!condition.equalsIgnoreCase("ABOVE")
                        &&
                        !condition.equalsIgnoreCase("BELOW"))) {

            throw new RuntimeException(
                    "Condition must be ABOVE or BELOW"
            );
        }


        Alert alert = new Alert();

        alert.setCryptoId(
                cryptoId.trim().toLowerCase()
        );

        alert.setCryptoName(
                cryptoName
        );

        alert.setSymbol(
                symbol
        );

        alert.setTargetPrice(
                targetPrice
        );

        alert.setCondition(
                condition.trim().toUpperCase()
        );

        alert.setActive(true);

        alert.setUser(user);


        return alertRepository.save(alert);
    }


    // =====================================================
    // GET USER ALERTS
    // =====================================================

    public List<Alert> getUserAlerts(
            User user) {

        return alertRepository.findByUser(user);
    }


    // =====================================================
    // DELETE ALERT
    // =====================================================

    @Transactional
    public void deleteAlert(
            Long id,
            User user) {

        Alert alert =
                alertRepository
                        .findByIdAndUser(id, user)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Alert not found"
                                )
                        );

        alertRepository.delete(alert);
    }


    // =====================================================
    // ALERT SCHEDULER
    // =====================================================

    @Scheduled(
            fixedRate = 60000,
            initialDelay = 10000
    )
    @Transactional
    public void checkPriceAlerts() {

        System.out.println(
                "======================================"
        );

        System.out.println(
                "Checking crypto price alerts..."
        );


        List<Alert> alerts =
                alertRepository.findByActiveTrue();


        if (alerts.isEmpty()) {

            System.out.println(
                    "No active alerts."
            );

            System.out.println(
                    "======================================"
            );

            return;
        }


        System.out.println(
                "Active alerts found: "
                        + alerts.size()
        );


        // =================================================
        // COLLECT UNIQUE CRYPTO IDS
        // =================================================

        Set<String> cryptoIds =
                new HashSet<>();


        for (Alert alert : alerts) {

            if (alert == null ||
                    alert.getCryptoId() == null ||
                    alert.getCryptoId().isBlank()) {

                continue;
            }

            cryptoIds.add(
                    alert.getCryptoId()
                            .trim()
                            .toLowerCase()
            );
        }


        if (cryptoIds.isEmpty()) {

            System.out.println(
                    "No valid crypto IDs."
            );

            return;
        }


        // =================================================
        // GET CURRENT PRICES
        // =================================================

        Map<String, Double> prices;

        try {

            prices =
                    cryptoService.getCurrentPrices(
                            cryptoIds
                    );

        } catch (Exception e) {

            System.out.println(
                    "Failed to fetch prices: "
                            + e.getMessage()
            );

            return;
        }


        if (prices == null ||
                prices.isEmpty()) {

            System.out.println(
                    "No prices received."
            );

            return;
        }


        // =================================================
        // CHECK ALERTS
        // =================================================

        for (Alert alert : alerts) {

            try {

                if (alert == null ||
                        !Boolean.TRUE.equals(
                                alert.getActive()
                        )) {

                    continue;
                }


                String cryptoId =
                        alert.getCryptoId()
                                .trim()
                                .toLowerCase();


                Double currentPrice =
                        prices.get(cryptoId);


                if (currentPrice == null) {

                    System.out.println(
                            "Price unavailable for: "
                                    + cryptoId
                    );

                    continue;
                }


                System.out.println(
                        "Alert #"
                                + alert.getId()
                                + " | "
                                + cryptoId
                                + " | Current: "
                                + currentPrice
                                + " | Target: "
                                + alert.getTargetPrice()
                                + " | Condition: "
                                + alert.getCondition()
                );


                boolean triggered =
                        isTriggered(
                                alert,
                                currentPrice
                        );


                if (!triggered) {

                    continue;
                }


                // =========================================
                // ALERT TRIGGERED
                // =========================================

                String cryptoName =
                        alert.getCryptoName();


                if (cryptoName == null ||
                        cryptoName.isBlank()) {

                    cryptoName = cryptoId;
                }


                String message =
                        cryptoName
                                + " has reached your target price of $"
                                + alert.getTargetPrice()
                                + ". Current price: $"
                                + currentPrice;


                System.out.println(
                        "**************************************"
                );

                System.out.println(
                        "PRICE ALERT TRIGGERED!"
                );

                System.out.println(
                        message
                );

                System.out.println(
                        "**************************************"
                );


                // =========================================
                // CREATE NOTIFICATION
                // =========================================

                notificationService.createNotification(
                        alert.getUser(),
                        message
                );


                // =========================================
                // DISABLE ALERT
                // =========================================

                alert.setActive(false);

                alertRepository.save(alert);


            } catch (Exception e) {

                System.out.println(
                        "Error processing alert #"
                                + alert.getId()
                                + ": "
                                + e.getMessage()
                );
            }
        }


        System.out.println(
                "Alert checking completed."
        );

        System.out.println(
                "======================================"
        );
    }


    // =====================================================
    // CONDITION
    // =====================================================

    private boolean isTriggered(
            Alert alert,
            Double currentPrice) {

        if (alert == null ||
                currentPrice == null ||
                alert.getTargetPrice() == null ||
                alert.getCondition() == null) {

            return false;
        }


        if (alert.getCondition()
                .equalsIgnoreCase("ABOVE")) {

            return currentPrice >=
                    alert.getTargetPrice();
        }


        if (alert.getCondition()
                .equalsIgnoreCase("BELOW")) {

            return currentPrice <=
                    alert.getTargetPrice();
        }


        return false;
    }
}