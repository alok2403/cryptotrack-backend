package com.cryptotrack.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "portfolio",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_crypto",
                        columnNames = {
                                "user_id",
                                "crypto_id"
                        }
                )
        }
)
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "crypto_id",
            nullable = false
    )
    private String cryptoId;

    @Column(
            nullable = false
    )
    private String cryptoName;

    private String symbol;

    @Column(
            nullable = false
    )
    private Double quantity;

    @Column(
            nullable = false
    )
    private Double buyPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;


    public Portfolio() {
    }


    public Portfolio(
            String cryptoId,
            String cryptoName,
            String symbol,
            Double quantity,
            Double buyPrice,
            User user) {

        this.cryptoId = cryptoId;
        this.cryptoName = cryptoName;
        this.symbol = symbol;
        this.quantity = quantity;
        this.buyPrice = buyPrice;
        this.user = user;
    }


    public Long getId() {
        return id;
    }


    public String getCryptoId() {
        return cryptoId;
    }

    public void setCryptoId(
            String cryptoId) {

        this.cryptoId = cryptoId;
    }


    public String getCryptoName() {
        return cryptoName;
    }

    public void setCryptoName(
            String cryptoName) {

        this.cryptoName = cryptoName;
    }


    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(
            String symbol) {

        this.symbol = symbol;
    }


    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(
            Double quantity) {

        this.quantity = quantity;
    }


    public Double getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(
            Double buyPrice) {

        this.buyPrice = buyPrice;
    }


    public User getUser() {
        return user;
    }

    public void setUser(
            User user) {

        this.user = user;
    }
}