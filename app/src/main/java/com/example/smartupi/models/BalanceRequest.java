package com.example.smartupi.models;

public class BalanceRequest {
    private String upi_id;
    private String pin;

    public BalanceRequest(String upi_id, String pin) {
        this.upi_id = upi_id;
        this.pin = pin;
    }
}