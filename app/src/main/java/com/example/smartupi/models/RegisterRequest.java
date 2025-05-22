package com.example.smartupi.models;

public class RegisterRequest {
    private String mobile;
    private String upi_id;
    private String password;
    private String upi_pin;
    public RegisterRequest(String mobile, String upi_id, String password, String upi_pin) {
        this.mobile = mobile;
        this.upi_id = upi_id;
        this.password = password;
        this.upi_pin = upi_pin;

    }
}