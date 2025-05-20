package com.example.smartupi.models;

public class BalanceRequest {
    private String upiId;
    private String pin;

    public BalanceRequest(String upiId, String pin) {
        this.upiId = upiId;
        this.pin = pin;
    }
}