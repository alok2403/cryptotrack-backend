package com.cryptotrack.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "watchlist",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "user_id",
                                "crypto_id"
                        }
                )
        }
)
public class Watchlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "crypto_id", nullable = false)
    private String cryptoId;

    @Column(name = "crypto_name")
    private String cryptoName;

    @Column(name = "symbol")
    private String symbol;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    public Watchlist() {
    }

    public Watchlist(
            String cryptoId,
            String cryptoName,
            String symbol,
            User user) {

        this.cryptoId = cryptoId;
        this.cryptoName = cryptoName;
        this.symbol = symbol;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public String getCryptoId() {
        return cryptoId;
    }

    public void setCryptoId(String cryptoId) {
        this.cryptoId = cryptoId;
    }

    public String getCryptoName() {
        return cryptoName;
    }

    public void setCryptoName(String cryptoName) {
        this.cryptoName = cryptoName;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}