package com.cryptotrack.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "price_alerts")
public class PriceAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "crypto_id", nullable = false)
    private String cryptoId;

    @Column(name = "crypto_name")
    private String cryptoName;

    @Column(name = "symbol")
    private String symbol;

    @Column(name = "target_price", nullable = false)
    private Double targetPrice;

    @Column(name = "alert_condition", nullable = false)
    private String condition;

    @Column(nullable = false)
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    public PriceAlert() {
    }


    public PriceAlert(
            String cryptoId,
            String cryptoName,
            String symbol,
            Double targetPrice,
            String condition,
            User user
    ) {

        this.cryptoId = cryptoId;
        this.cryptoName = cryptoName;
        this.symbol = symbol;
        this.targetPrice = targetPrice;
        this.condition = condition;
        this.user = user;
        this.active = true;
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

    public Double getTargetPrice() {
        return targetPrice;
    }

    public String getCondition() {
        return condition;
    }

    public boolean isActive() {
        return active;
    }

    public User getUser() {
        return user;
    }


    public void setCryptoId(String cryptoId) {
        this.cryptoId = cryptoId;
    }

    public void setCryptoName(String cryptoName) {
        this.cryptoName = cryptoName;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setTargetPrice(Double targetPrice) {
        this.targetPrice = targetPrice;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setUser(User user) {
        this.user = user;
    }
}