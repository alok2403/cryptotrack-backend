package com.cryptotrack.backend.repository;

import com.cryptotrack.backend.entity.SupportRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupportRequestRepository
        extends JpaRepository<SupportRequest, Long> {

    List<SupportRequest> findAllByOrderByCreatedAtDesc();
}