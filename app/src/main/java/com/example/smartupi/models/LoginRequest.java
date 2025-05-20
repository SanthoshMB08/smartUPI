package com.example.smartupi.models;

public class LoginRequest {
    private String mobile;
    private String password;

    public LoginRequest(String number, String password) {
        this.mobile = number;
        this.password = password;
    }
}

