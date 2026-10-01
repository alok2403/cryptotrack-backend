package com.cryptotrack.backend.dto;

public class AiResponse {

    private String coinId;
    private String analysis;

    public AiResponse(
            String coinId,
            String analysis) {

        this.coinId = coinId;
        this.analysis = analysis;
    }

    public String getCoinId() {
        return coinId;
    }

    public String getAnalysis() {
        return analysis;
    }
}