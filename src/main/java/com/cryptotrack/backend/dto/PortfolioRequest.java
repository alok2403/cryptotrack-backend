package com.cryptotrack.backend.dto;

public class PortfolioRequest {

    private String cryptoId;
    private Double quantity;

    public PortfolioRequest() {
    }

    public String getCryptoId() {
        return cryptoId;
    }

    public void setCryptoId(String cryptoId) {
        this.cryptoId = cryptoId;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }
}