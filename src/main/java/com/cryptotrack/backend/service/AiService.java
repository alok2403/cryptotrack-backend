
        package com.cryptotrack.backend.service;

import com.google.genai.Client;
import com.google.genai.errors.ServerException;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AiService {

    private static final String MODEL =
            "gemini-3.5-flash-lite";

    private final Client client;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public AiService(
            @Value("${gemini.api.key}") String apiKey) {

        if (apiKey == null ||
                apiKey.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Gemini API key is missing."
            );
        }

        this.client = Client.builder()
                .apiKey(apiKey)
                .build();

        System.out.println(
                "========================================"
        );

        System.out.println(
                "Gemini AI Service initialized"
        );

        System.out.println(
                "Model: " + MODEL
        );

        System.out.println(
                "========================================"
        );
    }


    // =========================================================
    // ANALYZE COIN
    // =========================================================

    public String analyzeCoin(String coinId) {

        if (coinId == null ||
                coinId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Coin name cannot be empty."
            );
        }

        String coin = coinId.trim();

        System.out.println(
                "========================================"
        );

        System.out.println(
                "AI ANALYSIS"
        );

        System.out.println(
                "Coin: " + coin
        );

        System.out.println(
                "Model: " + MODEL
        );

        System.out.println(
                "========================================"
        );


        /*
         * IMPORTANT:
         *
         * No CryptoService
         * No CoinGecko
         * No price history
         * No portfolio
         * No database
         *
         * Only coin name is sent to Gemini.
         */

        String prompt = buildPrompt(coin);

        return generateAnalysis(prompt);
    }


    // =========================================================
    // BUILD PROMPT
    // =========================================================

    private String buildPrompt(String coin) {

        return """
                You are the AI analysis assistant inside
                a cryptocurrency application called CryptoTrack.

                Analyze the cryptocurrency named: %s

                This is a GENERAL educational analysis.

                IMPORTANT:

                You do NOT have live market data.

                Therefore:

                - Do not claim a current price.
                - Do not invent market data.
                - Do not invent today's news.
                - Do not invent statistics.
                - Do not predict an exact future price.
                - Do not tell the user to buy.
                - Do not tell the user to sell.
                - Do not provide personalized financial advice.

                Explain the cryptocurrency using these sections:

                1. Market Overview

                Explain what the cryptocurrency is,
                what problem it tries to solve,
                and its general purpose.

                2. Technology

                Explain the technology and blockchain
                architecture behind it.

                3. Ecosystem

                Explain its major use cases,
                applications and ecosystem.

                4. Strengths

                Explain important potential strengths.

                5. Risks

                Explain technical, security, competition,
                adoption and market risks.

                6. What To Watch

                Give important factors that users should
                monitor when evaluating this cryptocurrency.

                7. Long-Term Considerations

                Explain factors that could influence its
                long-term development.

                8. AI Summary

                Give a short balanced summary.

                Keep the explanation simple and useful.

                Clearly distinguish known information
                from uncertainty.

                Do not use live market data.

                Cryptocurrency:

                %s
                """.formatted(
                coin,
                coin
        );
    }


    // =========================================================
    // GEMINI REQUEST
    // =========================================================

    private String generateAnalysis(String prompt) {

        try {

            System.out.println(
                    "Sending request to Gemini..."
            );

            long start =
                    System.currentTimeMillis();


            GenerateContentResponse response =
                    client.models.generateContent(
                            MODEL,
                            prompt,
                            null
                    );


            long end =
                    System.currentTimeMillis();


            System.out.println(
                    "Gemini response received."
            );

            System.out.println(
                    "Response time: "
                            + (end - start)
                            + " ms"
            );


            String result =
                    response.text();


            if (result == null ||
                    result.isBlank()) {

                throw new RuntimeException(
                        "Gemini returned an empty response."
                );
            }


            return result;


        } catch (ServerException e) {

            System.out.println(
                    "========================================"
            );

            System.out.println(
                    "GEMINI SERVER ERROR"
            );

            System.out.println(
                    "Message: "
                            + e.getMessage()
            );

            System.out.println(
                    "========================================"
            );


            throw new RuntimeException(
                    "Gemini is temporarily unavailable. "
                            + "Please try again."
            );


        } catch (Exception e) {

            System.out.println(
                    "========================================"
            );

            System.out.println(
                    "GEMINI ERROR"
            );

            System.out.println(
                    "Exception: "
                            + e.getClass().getName()
            );

            System.out.println(
                    "Message: "
                            + e.getMessage()
            );

            System.out.println(
                    "========================================"
            );


            throw new RuntimeException(
                    "Unable to generate AI analysis."
            );
        }
    }
}

