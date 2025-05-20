package com.example.smartupi.models;
public class PaymentRequest {
    private String sender_upi;
    private String receiver_upi;
    private double amount;
    private String pin;

    public PaymentRequest(String fromUpi, String toUpi, double amount, String pin) {
        this.sender_upi = fromUpi;
        this.receiver_upi= toUpi;
        this.amount = amount;
        this.pin = pin;
    }
}