package com.cryptotrack.backend.controller;

import com.cryptotrack.backend.dto.SupportRequestDto;
import com.cryptotrack.backend.entity.SupportRequest;
import com.cryptotrack.backend.service.SupportRequestService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/support")
@CrossOrigin(origins = "http://localhost:5173")
public class SupportRequestController {

    private final SupportRequestService service;

    public SupportRequestController(
            SupportRequestService service) {

        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> createRequest(
            @RequestBody SupportRequestDto request) {

        try {

            SupportRequest saved =
                    service.createRequest(request);

            return ResponseEntity.ok(saved);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());

        } catch (Exception e) {

            return ResponseEntity
                    .internalServerError()
                    .body(
                            "Failed to create support request: "
                                    + e.getMessage()
                    );
        }
    }

    @GetMapping
    public ResponseEntity<List<SupportRequest>>
    getAllRequests() {

        return ResponseEntity.ok(
                service.getAllRequests()
        );
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        try {

            return ResponseEntity.ok(
                    service.updateStatus(
                            id,
                            status
                    )
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());

        } catch (Exception e) {

            return ResponseEntity
                    .internalServerError()
                    .body(e.getMessage());
        }
    }
}