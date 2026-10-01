package com.cryptotrack.backend.dto;

public class MarketCoinResponse {

    private String id;
    private String symbol;
    private String name;
    private String image;

    private Double currentPrice;
    private Double marketCap;
    private Integer marketCapRank;
    private Double totalVolume;

    private Double priceChange24h;
    private Double priceChange7d;
    private Double priceChange30d;

    public MarketCoinResponse() {
    }

    public MarketCoinResponse(
            String id,
            String symbol,
            String name,
            String image,
            Double currentPrice,
            Double marketCap,
            Integer marketCapRank,
            Double totalVolume,
            Double priceChange24h,
            Double priceChange7d,
            Double priceChange30d) {

        this.id = id;
        this.symbol = symbol;
        this.name = name;
        this.image = image;
        this.currentPrice = currentPrice;
        this.marketCap = marketCap;
        this.marketCapRank = marketCapRank;
        this.totalVolume = totalVolume;
        this.priceChange24h = priceChange24h;
        this.priceChange7d = priceChange7d;
        this.priceChange30d = priceChange30d;
    }

    public String getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public Double getCurrentPrice() {
        return currentPrice;
    }

    public Double getMarketCap() {
        return marketCap;
    }

    public Integer getMarketCapRank() {
        return marketCapRank;
    }

    public Double getTotalVolume() {
        return totalVolume;
    }

    public Double getPriceChange24h() {
        return priceChange24h;
    }

    public Double getPriceChange7d() {
        return priceChange7d;
    }

    public Double getPriceChange30d() {
        return priceChange30d;
    }
}