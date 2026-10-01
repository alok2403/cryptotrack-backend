package com.cryptotrack.backend.dto;

public class CryptoPriceDto {

    private Double usd;
    private Double usd_24h_change;

    public CryptoPriceDto() {
    }

    public Double getUsd() {
        return usd;
    }

    public void setUsd(Double usd) {
        this.usd = usd;
    }

    public Double getUsd_24h_change() {
        return usd_24h_change;
    }

    public void setUsd_24h_change(Double usd_24h_change) {
        this.usd_24h_change = usd_24h_change;
    }
}