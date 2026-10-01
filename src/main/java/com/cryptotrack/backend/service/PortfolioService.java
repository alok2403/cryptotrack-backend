package com.cryptotrack.backend.service;

import com.cryptotrack.backend.dto.CryptoResponse;
import com.cryptotrack.backend.dto.PortfolioAnalyticsResponse;
import com.cryptotrack.backend.dto.PortfolioHoldingAnalytics;
import com.cryptotrack.backend.entity.Portfolio;
import com.cryptotrack.backend.entity.User;
import com.cryptotrack.backend.repository.PortfolioRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final CryptoService cryptoService;

    public PortfolioService(
            PortfolioRepository portfolioRepository,
            CryptoService cryptoService) {

        this.portfolioRepository = portfolioRepository;
        this.cryptoService = cryptoService;
    }

    // =========================================================
    // ADD CRYPTO
    // =========================================================

    @Transactional
    public Portfolio addToPortfolio(
            User user,
            String cryptoId,
            Double quantity) {

        // -----------------------------------------------------
        // VALIDATION
        // -----------------------------------------------------

        if (user == null) {
            throw new IllegalArgumentException(
                    "User is required."
            );
        }

        if (cryptoId == null ||
                cryptoId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Crypto ID is required."
            );
        }

        if (quantity == null ||
                quantity <= 0) {

            throw new IllegalArgumentException(
                    "Quantity must be greater than zero."
            );
        }

        String normalizedCryptoId =
                cryptoId.trim().toLowerCase();


        // -----------------------------------------------------
        // GET LIVE CRYPTO DATA
        // -----------------------------------------------------

        CryptoResponse crypto;

        try {

            crypto =
                    cryptoService.getCrypto(
                            normalizedCryptoId
                    );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to fetch cryptocurrency data."
            );
        }


        if (crypto == null) {

            throw new RuntimeException(
                    "Cryptocurrency not found."
            );
        }


        // -----------------------------------------------------
        // GET CURRENT MARKET PRICE
        // -----------------------------------------------------

        Double currentPrice =
                crypto.getPrice();


        if (currentPrice == null ||
                currentPrice <= 0) {

            throw new RuntimeException(
                    "Current cryptocurrency price is unavailable."
            );
        }


        // -----------------------------------------------------
        // GET NAME AND SYMBOL FROM API
        // -----------------------------------------------------

        String cryptoName =
                crypto.getName();

        String symbol =
                crypto.getSymbol();


        // -----------------------------------------------------
        // CHECK EXISTING HOLDING
        // -----------------------------------------------------

        Portfolio existing =
                portfolioRepository
                        .findByUserAndCryptoId(
                                user,
                                normalizedCryptoId
                        )
                        .orElse(null);


        // =====================================================
        // UPDATE EXISTING HOLDING
        // =====================================================

        if (existing != null) {

            double oldQuantity =
                    existing.getQuantity() == null
                            ? 0.0
                            : existing.getQuantity();


            double oldAveragePrice =
                    existing.getBuyPrice() == null
                            ? 0.0
                            : existing.getBuyPrice();


            // -------------------------------------------------
            // OLD INVESTMENT
            // -------------------------------------------------

            double oldInvestment =
                    oldQuantity *
                            oldAveragePrice;


            // -------------------------------------------------
            // NEW INVESTMENT
            // -------------------------------------------------

            double newInvestment =
                    quantity *
                            currentPrice;


            // -------------------------------------------------
            // TOTAL QUANTITY
            // -------------------------------------------------

            double totalQuantity =
                    oldQuantity +
                            quantity;


            // -------------------------------------------------
            // WEIGHTED AVERAGE BUY PRICE
            // -------------------------------------------------

            double averageBuyPrice =
                    (oldInvestment +
                            newInvestment)
                            / totalQuantity;


            existing.setQuantity(
                    totalQuantity
            );

            existing.setBuyPrice(
                    averageBuyPrice
            );


            // Update latest API information

            if (cryptoName != null &&
                    !cryptoName.isBlank()) {

                existing.setCryptoName(
                        cryptoName
                );
            }


            if (symbol != null &&
                    !symbol.isBlank()) {

                existing.setSymbol(
                        symbol.toUpperCase()
                );
            }


            return portfolioRepository.save(
                    existing
            );
        }


        // =====================================================
        // CREATE NEW HOLDING
        // =====================================================

        Portfolio portfolio =
                new Portfolio();

        portfolio.setCryptoId(
                normalizedCryptoId
        );

        portfolio.setCryptoName(
                cryptoName
        );

        portfolio.setSymbol(
                symbol == null
                        ? null
                        : symbol.toUpperCase()
        );

        portfolio.setQuantity(
                quantity
        );

        /*
         * IMPORTANT:
         *
         * The buy price is automatically taken
         * from the current market price.
         */
        portfolio.setBuyPrice(
                currentPrice
        );

        portfolio.setUser(
                user
        );


        return portfolioRepository.save(
                portfolio
        );
    }


    // =========================================================
    // GET PORTFOLIO
    // =========================================================

    public List<Portfolio> getPortfolio(
            User user) {

        if (user == null) {

            throw new IllegalArgumentException(
                    "User is required."
            );
        }

        return portfolioRepository.findByUser(
                user
        );
    }


    // =========================================================
    // GET SINGLE ITEM
    // =========================================================

    public Portfolio getPortfolioItem(
            User user,
            Long id) {

        if (user == null) {

            throw new IllegalArgumentException(
                    "User is required."
            );
        }

        if (id == null) {

            throw new IllegalArgumentException(
                    "Portfolio ID is required."
            );
        }

        return portfolioRepository
                .findByUserAndId(
                        user,
                        id
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Portfolio item not found."
                        )
                );
    }


    // =========================================================
    // REMOVE PORTFOLIO ITEM
    // =========================================================

    @Transactional
    public void removeFromPortfolio(
            User user,
            Long id) {

        Portfolio portfolio =
                getPortfolioItem(
                        user,
                        id
                );

        portfolioRepository.delete(
                portfolio
        );
    }


    // =========================================================
    // CHECK COIN
    // =========================================================

    public boolean hasCoin(
            User user,
            String cryptoId) {

        if (user == null ||
                cryptoId == null ||
                cryptoId.trim().isEmpty()) {

            return false;
        }

        return portfolioRepository
                .findByUserAndCryptoId(
                        user,
                        cryptoId.trim().toLowerCase()
                )
                .isPresent();
    }


    // =========================================================
    // GET COIN
    // =========================================================

    public Portfolio getCoin(
            User user,
            String cryptoId) {

        if (user == null ||
                cryptoId == null ||
                cryptoId.trim().isEmpty()) {

            return null;
        }

        return portfolioRepository
                .findByUserAndCryptoId(
                        user,
                        cryptoId.trim().toLowerCase()
                )
                .orElse(null);
    }


    // =========================================================
    // PORTFOLIO ANALYTICS
    // =========================================================

    public PortfolioAnalyticsResponse getPortfolioAnalytics(
            User user) {

        if (user == null) {

            throw new IllegalArgumentException(
                    "User is required."
            );
        }


        List<Portfolio> portfolio =
                portfolioRepository.findByUser(
                        user
                );


        List<PortfolioHoldingAnalytics> holdings =
                new ArrayList<>();


        double totalInvested = 0.0;

        double currentValue = 0.0;


        // -----------------------------------------------------
        // FIRST PASS
        // -----------------------------------------------------

        for (Portfolio holding : portfolio) {

            double quantity =
                    holding.getQuantity() == null
                            ? 0.0
                            : holding.getQuantity();


            double buyPrice =
                    holding.getBuyPrice() == null
                            ? 0.0
                            : holding.getBuyPrice();


            totalInvested +=
                    quantity *
                            buyPrice;


            try {

                Double currentPrice =
                        cryptoService.getCurrentPrice(
                                holding.getCryptoId()
                        );


                if (currentPrice != null &&
                        currentPrice > 0) {

                    currentValue +=
                            quantity *
                                    currentPrice;
                }

            } catch (Exception ignored) {

                // Continue with remaining coins
            }
        }


        // -----------------------------------------------------
        // SECOND PASS
        // -----------------------------------------------------

        for (Portfolio holding : portfolio) {

            double quantity =
                    holding.getQuantity() == null
                            ? 0.0
                            : holding.getQuantity();


            double averageBuyPrice =
                    holding.getBuyPrice() == null
                            ? 0.0
                            : holding.getBuyPrice();


            double investedValue =
                    quantity *
                            averageBuyPrice;


            Double currentPrice = null;

            double coinCurrentValue = 0.0;

            double profitLoss = 0.0;

            double profitLossPercent = 0.0;

            double allocationPercent = 0.0;


            try {

                currentPrice =
                        cryptoService.getCurrentPrice(
                                holding.getCryptoId()
                        );


                if (currentPrice != null &&
                        currentPrice > 0) {

                    coinCurrentValue =
                            quantity *
                                    currentPrice;


                    profitLoss =
                            coinCurrentValue -
                                    investedValue;


                    if (investedValue > 0) {

                        profitLossPercent =
                                (profitLoss /
                                        investedValue)
                                        * 100.0;
                    }


                    if (currentValue > 0) {

                        allocationPercent =
                                (coinCurrentValue /
                                        currentValue)
                                        * 100.0;
                    }
                }

            } catch (Exception ignored) {

                // Keep holding even if API fails
            }


            PortfolioHoldingAnalytics analytics =
                    new PortfolioHoldingAnalytics(

                            holding.getId(),

                            holding.getCryptoId(),

                            holding.getCryptoName(),

                            holding.getSymbol(),

                            quantity,

                            averageBuyPrice,

                            investedValue,

                            currentPrice,

                            coinCurrentValue,

                            profitLoss,

                            profitLossPercent,

                            allocationPercent
                    );


            holdings.add(
                    analytics
            );
        }


        // -----------------------------------------------------
        // TOTAL P/L
        // -----------------------------------------------------

        double totalProfitLoss =
                currentValue -
                        totalInvested;


        double totalProfitLossPercent =
                0.0;


        if (totalInvested > 0) {

            totalProfitLossPercent =
                    (totalProfitLoss /
                            totalInvested)
                            * 100.0;
        }


        // -----------------------------------------------------
        // RESPONSE
        // -----------------------------------------------------

        return new PortfolioAnalyticsResponse(

                totalInvested,

                currentValue,

                totalProfitLoss,

                totalProfitLossPercent,

                holdings.size(),

                holdings
        );
    }
}