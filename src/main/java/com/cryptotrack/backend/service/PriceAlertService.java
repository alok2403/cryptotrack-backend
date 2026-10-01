package com.cryptotrack.backend.service;

import com.cryptotrack.backend.entity.PriceAlert;
import com.cryptotrack.backend.entity.User;
import com.cryptotrack.backend.repository.PriceAlertRepository;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PriceAlertService {

    private final PriceAlertRepository priceAlertRepository;
    private final CryptoService cryptoService;
    private final NotificationService notificationService;

    public PriceAlertService(
            PriceAlertRepository priceAlertRepository,
            CryptoService cryptoService,
            NotificationService notificationService
    ) {

        this.priceAlertRepository =
                priceAlertRepository;

        this.cryptoService =
                cryptoService;

        this.notificationService =
                notificationService;
    }


    // =====================================================
    // CREATE ALERT
    // =====================================================

    @Transactional
    public PriceAlert createAlert(
            PriceAlert alert) {

        if (alert == null) {
            throw new RuntimeException(
                    "Alert is required"
            );
        }

        User user =
                alert.getUser();

        if (user == null) {
            throw new RuntimeException(
                    "User is required"
            );
        }

        if (alert.getCryptoId() == null ||
                alert.getCryptoId().isBlank()) {

            throw new RuntimeException(
                    "Crypto ID is required"
            );
        }

        if (alert.getTargetPrice() == null ||
                alert.getTargetPrice() <= 0) {

            throw new RuntimeException(
                    "Target price must be greater than zero"
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

            throw new RuntimeException(
                    "Condition must be ABOVE or BELOW"
            );
        }

        alert.setCryptoId(
                alert.getCryptoId()
                        .trim()
                        .toLowerCase()
        );

        alert.setCondition(
                alert.getCondition()
                        .trim()
                        .toUpperCase()
        );

        alert.setActive(true);

        return priceAlertRepository.save(
                alert
        );
    }


    // =====================================================
    // GET USER ALERTS
    // =====================================================

    @Transactional(readOnly = true)
    public List<PriceAlert> getUserAlerts(
            User user) {

        if (user == null) {
            throw new RuntimeException(
                    "User is required"
            );
        }

        return priceAlertRepository
                .findByUser(user);
    }


    // =====================================================
    // GET ACTIVE USER ALERTS
    // =====================================================

    @Transactional(readOnly = true)
    public List<PriceAlert> getActiveUserAlerts(
            User user) {

        if (user == null) {
            throw new RuntimeException(
                    "User is required"
            );
        }

        return priceAlertRepository
                .findByUserAndActive(
                        user,
                        true
                );
    }


    // =====================================================
    // DELETE ALERT
    // =====================================================

    @Transactional
    public void deleteAlert(
            Long alertId,
            User user) {

        if (alertId == null) {
            throw new RuntimeException(
                    "Alert ID is required"
            );
        }

        if (user == null) {
            throw new RuntimeException(
                    "User is required"
            );
        }

        PriceAlert alert =
                priceAlertRepository
                        .findByIdAndUser(
                                alertId,
                                user
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Alert not found"
                                )
                        );

        priceAlertRepository.delete(
                alert
        );
    }


    // =====================================================
    // TOGGLE ALERT
    // =====================================================

    @Transactional
    public PriceAlert toggleAlert(
            Long alertId,
            User user) {

        if (alertId == null) {

            throw new RuntimeException(
                    "Alert ID is required"
            );
        }

        if (user == null) {

            throw new RuntimeException(
                    "User is required"
            );
        }

        PriceAlert alert =
                priceAlertRepository
                        .findByIdAndUser(
                                alertId,
                                user
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Alert not found"
                                )
                        );

        alert.setActive(
                !alert.isActive()
        );

        return priceAlertRepository.save(
                alert
        );
    }


    // =====================================================
    // PRICE ALERT SCHEDULER
    // =====================================================

    /*
     * Runs every 60 seconds.
     *
     * IMPORTANT:
     * This method uses LIVE CoinGecko prices.
     * It does NOT use the normal application cache.
     */

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void checkPriceAlerts() {

        System.out.println();
        System.out.println(
                "=============================================="
        );

        System.out.println(
                "PRICE ALERT CHECK STARTED"
        );

        System.out.println(
                "=============================================="
        );


        List<PriceAlert> alerts =
                priceAlertRepository
                        .findByActiveTrue();


        if (alerts == null ||
                alerts.isEmpty()) {

            System.out.println(
                    "No active price alerts."
            );

            System.out.println(
                    "=============================================="
            );

            return;
        }


        System.out.println(
                "Active alerts: "
                        + alerts.size()
        );


        // =================================================
        // UNIQUE COINS
        // =================================================

        Map<String, String> normalizedIds =
                new HashMap<>();

        for (PriceAlert alert : alerts) {

            if (alert == null) {
                continue;
            }

            if (alert.getCryptoId() == null ||
                    alert.getCryptoId().isBlank()) {

                continue;
            }

            String cryptoId =
                    alert.getCryptoId()
                            .trim()
                            .toLowerCase();

            normalizedIds.put(
                    cryptoId,
                    cryptoId
            );
        }


        if (normalizedIds.isEmpty()) {

            System.out.println(
                    "No valid crypto IDs."
            );

            return;
        }


        // =================================================
        // FETCH LIVE PRICES
        // =================================================

        Map<String, Double> prices;

        try {

            prices =
                    cryptoService
                            .getCurrentPricesForAlerts(
                                    normalizedIds.keySet()
                            );

        } catch (Exception e) {

            System.err.println(
                    "Unable to fetch live prices: "
                            + e.getMessage()
            );

            return;
        }


        if (prices == null ||
                prices.isEmpty()) {

            System.out.println(
                    "No live prices received."
            );

            return;
        }


        // =================================================
        // CHECK ALERTS
        // =================================================

        for (PriceAlert alert : alerts) {

            try {

                if (alert == null ||
                        !alert.isActive()) {

                    continue;
                }

                String cryptoId =
                        alert.getCryptoId()
                                .trim()
                                .toLowerCase();


                Double currentPrice =
                        prices.get(
                                cryptoId
                        );


                if (currentPrice == null) {

                    System.out.println(
                            "No price available for: "
                                    + cryptoId
                    );

                    continue;
                }


                Double targetPrice =
                        alert.getTargetPrice();


                String condition =
                        alert.getCondition();


                System.out.println(
                        "Checking alert: "
                                + cryptoId
                                + " | Current: $"
                                + currentPrice
                                + " | Target: $"
                                + targetPrice
                                + " | Condition: "
                                + condition
                );


                boolean triggered =
                        isAlertTriggered(
                                alert,
                                currentPrice
                        );


                if (!triggered) {

                    continue;
                }


                // =========================================
                // TRIGGERED
                // =========================================

                String cryptoName =
                        alert.getCryptoName();


                if (cryptoName == null ||
                        cryptoName.isBlank()) {

                    cryptoName =
                            cryptoId;
                }


                String message =
                        cryptoName
                                + " has reached your target price of $"
                                + targetPrice
                                + ". Current price: $"
                                + currentPrice;


                System.out.println(
                        "****************************************"
                );

                System.out.println(
                        "PRICE ALERT TRIGGERED!"
                );

                System.out.println(
                        message
                );

                System.out.println(
                        "****************************************"
                );


                // =========================================
                // CREATE NOTIFICATION
                // =========================================

                notificationService
                        .createNotification(
                                alert.getUser(),
                                message
                        );


                // =========================================
                // DISABLE ALERT
                // =========================================

                alert.setActive(false);

                priceAlertRepository.save(
                        alert
                );


                System.out.println(
                        "Alert disabled after trigger."
                );

            } catch (Exception e) {

                System.err.println(
                        "Could not process alert: "
                                + e.getMessage()
                );

                e.printStackTrace();
            }
        }


        System.out.println(
                "PRICE ALERT CHECK FINISHED"
        );

        System.out.println(
                "=============================================="
        );
    }


    // =====================================================
    // CHECK CONDITION
    // =====================================================

    private boolean isAlertTriggered(
            PriceAlert alert,
            Double currentPrice) {

        if (alert == null ||
                currentPrice == null ||
                alert.getTargetPrice() == null ||
                alert.getCondition() == null) {

            return false;
        }


        double target =
                alert.getTargetPrice();


        if (alert.getCondition()
                .equalsIgnoreCase("ABOVE")) {

            return currentPrice >= target;
        }


        if (alert.getCondition()
                .equalsIgnoreCase("BELOW")) {

            return currentPrice <= target;
        }


        return false;
    }
}