package com.example.smartupi.models;

public class RegisterRequest {
    private String mobile;
    private String upi_id;
    private String password;
    private String upi_pin;
    public RegisterRequest(String number, String upiId, String password, String upiPin) {
        this.mobile = number;
        this.upi_id = upiId;
        this.password = password;
        this.upi_pin = upiPin;

    }
}