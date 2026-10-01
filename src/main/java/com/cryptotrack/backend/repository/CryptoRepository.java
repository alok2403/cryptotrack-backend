package com.cryptotrack.backend.repository;

import com.cryptotrack.backend.entity.Crypto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CryptoRepository extends JpaRepository<Crypto, Long> {

    Optional<Crypto> findBySymbolIgnoreCase(String symbol);
}