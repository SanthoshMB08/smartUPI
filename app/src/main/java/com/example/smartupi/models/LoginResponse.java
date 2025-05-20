package com.example.smartupi.models;
public class LoginResponse {
    public String message;
    public User user;

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }

    public static class User {

        public String mobile;
        public String upi_id;
        public double balance;
        public boolean success;

        public boolean isSuccess() {
            return success;
        }

        public String getMobile() {
            return mobile;
        }

        public String getUpiId() {
            return upi_id;
        }

        public double getBalance() {
            return balance;
        }
    }
}
