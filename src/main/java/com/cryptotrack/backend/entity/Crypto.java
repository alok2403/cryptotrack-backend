package com.cryptotrack.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "cryptos")
public class Crypto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol;

    private String name;

    private Double price;

    private Double change24h;

    public Crypto() {
    }

    public Crypto(String symbol, String name, Double price, Double change24h) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
        this.change24h = change24h;
    }

    public Long getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getChange24h() {
        return change24h;
    }

    public void setChange24h(Double change24h) {
        this.change24h = change24h;
    }
}