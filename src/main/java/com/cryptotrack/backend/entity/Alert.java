package com.cryptotrack.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "alerts")
public class Alert {

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

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;


    public Alert() {
    }


    public Alert(
            String cryptoId,
            String cryptoName,
            String symbol,
            Double targetPrice,
            String condition,
            Boolean active,
            User user) {

        this.cryptoId = cryptoId;
        this.cryptoName = cryptoName;
        this.symbol = symbol;
        this.targetPrice = targetPrice;
        this.condition = condition;
        this.active = active;
        this.user = user;
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

    public Boolean getActive() {
        return active;
    }

    public User getUser() {
        return user;
    }


    public void setId(Long id) {
        this.id = id;
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

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setUser(User user) {
        this.user = user;
    }
}