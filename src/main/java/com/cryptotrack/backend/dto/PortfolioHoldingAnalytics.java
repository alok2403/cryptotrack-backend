package com.cryptotrack.backend.dto;

public class PortfolioHoldingAnalytics {

    private Long id;
    private String cryptoId;
    private String cryptoName;
    private String symbol;

    private Double quantity;
    private Double averageBuyPrice;

    private Double investedValue;
    private Double currentPrice;
    private Double currentValue;

    private Double profitLoss;
    private Double profitLossPercent;
    private Double allocationPercent;

    public PortfolioHoldingAnalytics() {
    }

    public PortfolioHoldingAnalytics(
            Long id,
            String cryptoId,
            String cryptoName,
            String symbol,
            Double quantity,
            Double averageBuyPrice,
            Double investedValue,
            Double currentPrice,
            Double currentValue,
            Double profitLoss,
            Double profitLossPercent,
            Double allocationPercent) {

        this.id = id;
        this.cryptoId = cryptoId;
        this.cryptoName = cryptoName;
        this.symbol = symbol;
        this.quantity = quantity;
        this.averageBuyPrice = averageBuyPrice;
        this.investedValue = investedValue;
        this.currentPrice = currentPrice;
        this.currentValue = currentValue;
        this.profitLoss = profitLoss;
        this.profitLossPercent = profitLossPercent;
        this.allocationPercent = allocationPercent;
    }

    public Long getId() {
        return id;
    }

    public String getCryptoId() {
        return cryptoId;
    }

    public String getCryptoName() {
        return cryptoName;
    }

    public String getSymbol() {
        return symbol;
    }

    public Double getQuantity() {
        return quantity;
    }

    public Double getAverageBuyPrice() {
        return averageBuyPrice;
    }

    public Double getInvestedValue() {
        return investedValue;
    }

    public Double getCurrentPrice() {
        return currentPrice;
    }

    public Double getCurrentValue() {
        return currentValue;
    }

    public Double getProfitLoss() {
        return profitLoss;
    }

    public Double getProfitLossPercent() {
        return profitLossPercent;
    }

    public Double getAllocationPercent() {
        return allocationPercent;
    }
}