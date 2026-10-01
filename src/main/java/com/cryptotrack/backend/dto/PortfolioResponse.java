package com.cryptotrack.backend.dto;

public class PortfolioResponse {

    private Long id;
    private String cryptoId;
    private String cryptoName;
    private String symbol;

    private Double quantity;
    private Double buyPrice;
    private Double currentPrice;

    private Double investedAmount;
    private Double currentValue;
    private Double profitLoss;
    private Double profitLossPercentage;

    public PortfolioResponse() {
    }

    public PortfolioResponse(
            Long id,
            String cryptoId,
            String cryptoName,
            String symbol,
            Double quantity,
            Double buyPrice,
            Double currentPrice,
            Double investedAmount,
            Double currentValue,
            Double profitLoss,
            Double profitLossPercentage) {

        this.id = id;
        this.cryptoId = cryptoId;
        this.cryptoName = cryptoName;
        this.symbol = symbol;
        this.quantity = quantity;
        this.buyPrice = buyPrice;
        this.currentPrice = currentPrice;
        this.investedAmount = investedAmount;
        this.currentValue = currentValue;
        this.profitLoss = profitLoss;
        this.profitLossPercentage = profitLossPercentage;
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

    public Double getBuyPrice() {
        return buyPrice;
    }

    public Double getCurrentPrice() {
        return currentPrice;
    }

    public Double getInvestedAmount() {
        return investedAmount;
    }

    public Double getCurrentValue() {
        return currentValue;
    }

    public Double getProfitLoss() {
        return profitLoss;
    }

    public Double getProfitLossPercentage() {
        return profitLossPercentage;
    }
}