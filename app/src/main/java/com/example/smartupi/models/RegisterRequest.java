package com.example.smartupi.models;

public class RegisterRequest {
    private String number;
    private String upiId;
    private String password;
    private String upiPin;
    public RegisterRequest(String number, String upiId, String password, String upiPin) {
        this.number = number;
        this.upiId = upiId;
        this.password = password;
        this.upiPin = upiPin;

    }
}