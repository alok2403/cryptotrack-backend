package com.cryptotrack.backend.dto;

public class PortfolioAiRequest {

    private String question;

    public PortfolioAiRequest() {
    }

    public PortfolioAiRequest(String question) {
        this.question = question;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}