package com.cryptotrack.backend.dto;

public class DashboardCryptoResponse {

    private String coinId;
    private String name;
    private String symbol;
    private Double price;
    private Double marketCap;
    private Double change24h;

    public DashboardCryptoResponse() {
    }

    public DashboardCryptoResponse(
            String coinId,
            String name,
            String symbol,
            Double price,
            Double marketCap,
            Double change24h) {

        this.coinId = coinId;
        this.name = name;
        this.symbol = symbol;
        this.price = price;
        this.marketCap = marketCap;
        this.change24h = change24h;
    }

    public String getCoinId() {
        return coinId;
    }

    public void setCoinId(String coinId) {
        this.coinId = coinId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(Double marketCap) {
        this.marketCap = marketCap;
    }

    public Double getChange24h() {
        return change24h;
    }

    public void setChange24h(Double change24h) {
        this.change24h = change24h;
    }
}