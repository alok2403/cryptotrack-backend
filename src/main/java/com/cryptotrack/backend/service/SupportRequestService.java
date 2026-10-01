package com.cryptotrack.backend.service;

import com.cryptotrack.backend.dto.SupportRequestDto;
import com.cryptotrack.backend.entity.SupportRequest;
import com.cryptotrack.backend.repository.SupportRequestRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SupportRequestService {

    private final SupportRequestRepository repository;

    public SupportRequestService(
            SupportRequestRepository repository) {

        this.repository = repository;
    }

    @Transactional
    public SupportRequest createRequest(
            SupportRequestDto request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Support request is required"
            );
        }

        if (request.getEmail() == null ||
                request.getEmail().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Email is required"
            );
        }

        if (request.getIssue() == null ||
                request.getIssue().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Issue is required"
            );
        }

        if (request.getDescription() == null ||
                request.getDescription().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Description is required"
            );
        }

        SupportRequest supportRequest =
                new SupportRequest(
                        request.getEmail().trim(),
                        request.getIssue().trim(),
                        request.getDescription().trim()
                );

        return repository.save(supportRequest);
    }

    public List<SupportRequest> getAllRequests() {

        return repository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public SupportRequest updateStatus(
            Long id,
            String status) {

        SupportRequest request =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Support request not found"
                                )
                        );

        String normalizedStatus =
                status.trim().toUpperCase();

        if (!normalizedStatus.equals("OPEN") &&
                !normalizedStatus.equals("IN_PROGRESS") &&
                !normalizedStatus.equals("RESOLVED")) {

            throw new IllegalArgumentException(
                    "Invalid support request status"
            );
        }

        request.setStatus(normalizedStatus);

        return repository.save(request);
    }
}