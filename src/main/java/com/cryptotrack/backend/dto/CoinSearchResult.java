package com.cryptotrack.backend.dto;

public class CoinSearchResult {

    private String id;
    private String name;
    private String symbol;
    private String thumb;

    public CoinSearchResult() {
    }

    public CoinSearchResult(
            String id,
            String name,
            String symbol,
            String thumb) {

        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.thumb = thumb;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }
}