package com.cryptotrack.backend.dto;

public class PriceHistoryDto {

    private String date;
    private Double price;

    public PriceHistoryDto() {
    }

    public PriceHistoryDto(String date, Double price) {
        this.date = date;
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}