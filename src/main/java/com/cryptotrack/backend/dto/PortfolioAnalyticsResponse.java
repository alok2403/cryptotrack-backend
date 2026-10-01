package com.cryptotrack.backend.dto;

import java.util.List;

public class PortfolioAnalyticsResponse {

    private Double totalInvested;
    private Double currentValue;
    private Double totalProfitLoss;
    private Double totalProfitLossPercent;

    private Integer totalHoldings;

    private List<PortfolioHoldingAnalytics> holdings;

    public PortfolioAnalyticsResponse() {
    }

    public PortfolioAnalyticsResponse(
            Double totalInvested,
            Double currentValue,
            Double totalProfitLoss,
            Double totalProfitLossPercent,
            Integer totalHoldings,
            List<PortfolioHoldingAnalytics> holdings) {

        this.totalInvested = totalInvested;
        this.currentValue = currentValue;
        this.totalProfitLoss = totalProfitLoss;
        this.totalProfitLossPercent = totalProfitLossPercent;
        this.totalHoldings = totalHoldings;
        this.holdings = holdings;
    }

    public Double getTotalInvested() {
        return totalInvested;
    }

    public Double getCurrentValue() {
        return currentValue;
    }

    public Double getTotalProfitLoss() {
        return totalProfitLoss;
    }

    public Double getTotalProfitLossPercent() {
        return totalProfitLossPercent;
    }

    public Integer getTotalHoldings() {
        return totalHoldings;
    }

    public List<PortfolioHoldingAnalytics> getHoldings() {
        return holdings;
    }
}