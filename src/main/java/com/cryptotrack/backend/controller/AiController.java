
        package com.cryptotrack.backend.controller;

import com.cryptotrack.backend.dto.AiRequest;
import com.cryptotrack.backend.dto.AiResponse;
import com.cryptotrack.backend.service.AiService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "http://localhost:3000")
public class AiController {

    private final AiService aiService;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }


    // =====================================================
    // AI COIN ANALYSIS
    // =====================================================

    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeCoin(
            @RequestBody AiRequest request) {

        try {

            // =================================================
            // VALIDATE REQUEST
            // =================================================

            if (request == null ||
                    request.getCoinId() == null ||
                    request.getCoinId().trim().isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                new AiResponse(
                                        null,
                                        "Coin ID is required."
                                )
                        );
            }


            String coinId =
                    request.getCoinId()
                            .trim()
                            .toLowerCase();


            // =================================================
            // CALL AI SERVICE
            // =================================================

            System.out.println();
            System.out.println(
                    "========================================"
            );

            System.out.println(
                    "AI ANALYSIS REQUEST"
            );

            System.out.println(
                    "Coin: " + coinId
            );

            System.out.println(
                    "========================================"
            );


            String analysis =
                    aiService.analyzeCoin(
                            coinId
                    );


            // =================================================
            // RESPONSE
            // =================================================

            AiResponse response =
                    new AiResponse(
                            coinId,
                            analysis
                    );


            return ResponseEntity.ok(
                    response
            );


        } catch (IllegalArgumentException e) {

            // =================================================
            // BAD REQUEST
            // =================================================

            return ResponseEntity
                    .badRequest()
                    .body(
                            new AiResponse(
                                    request != null
                                            ? request.getCoinId()
                                            : null,

                                    e.getMessage()
                            )
                    );


        } catch (RuntimeException e) {

            String message =
                    e.getMessage() == null
                            ? "AI analysis failed."
                            : e.getMessage();


            System.out.println();
            System.out.println(
                    "========================================"
            );

            System.out.println(
                    "AI CONTROLLER ERROR"
            );

            System.out.println(
                    "Message: " + message
            );

            System.out.println(
                    "========================================"
            );


            // =================================================
            // GEMINI 429 / QUOTA
            // =================================================

            if (message.contains("quota") ||
                    message.contains("429") ||
                    message.contains("rate limit")) {

                return ResponseEntity
                        .status(
                                HttpStatus.TOO_MANY_REQUESTS
                        )
                        .body(
                                new AiResponse(
                                        request != null
                                                ? request.getCoinId()
                                                : null,

                                        "Gemini API quota or rate limit "
                                                + "has been reached. "
                                                + "Please try again later."
                                )
                        );
            }


            // =================================================
            // GEMINI 503
            // =================================================

            if (message.contains("503") ||
                    message.contains("temporarily unavailable") ||
                    message.contains("high demand") ||
                    message.contains("server")) {

                return ResponseEntity
                        .status(
                                HttpStatus.SERVICE_UNAVAILABLE
                        )
                        .body(
                                new AiResponse(
                                        request != null
                                                ? request.getCoinId()
                                                : null,

                                        "Gemini AI is temporarily "
                                                + "unavailable. "
                                                + "Please try again shortly."
                                )
                        );
            }


            // =================================================
            // OTHER ERROR
            // =================================================

            return ResponseEntity
                    .status(
                            HttpStatus.INTERNAL_SERVER_ERROR
                    )
                    .body(
                            new AiResponse(
                                    request != null
                                            ? request.getCoinId()
                                            : null,

                                    "AI analysis failed: "
                                            + message
                            )
                    );
        }
    }
}

