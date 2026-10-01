package com.cryptotrack.backend.dto;

public class CryptoResponse {

    private String id;
    private String name;
    private String symbol;
    private Double price;
    private Double marketCap;
    private Double change24h;

    public CryptoResponse(
            String id,
            String name,
            String symbol,
            Double price,
            Double marketCap,
            Double change24h) {

        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.price = price;
        this.marketCap = marketCap;
        this.change24h = change24h;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public Double getPrice() {
        return price;
    }

    public Double getMarketCap() {
        return marketCap;
    }

    public Double getChange24h() {
        return change24h;
    }
}