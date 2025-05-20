package com.example.smartupi.api;

import com.example.smartupi.models.BalanceRequest;
import com.example.smartupi.models.BalanceResponse;
import com.example.smartupi.models.GenericResponse;
import com.example.smartupi.models.LoginRequest;
import com.example.smartupi.models.LoginResponse;
import com.example.smartupi.models.PaymentRequest;
import com.example.smartupi.models.RegisterRequest;
import com.example.smartupi.models.TransactionItem;
import com.example.smartupi.models.TransactionListResponse;
import com.example.smartupi.models.TransactionRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface ApiService {

        @POST("/login")
        Call<LoginResponse> login(@Body LoginRequest request);

        @POST("/register")
        Call<GenericResponse> register(@Body RegisterRequest request);

        @POST("/balance")
        Call<BalanceResponse> checkBalance(@Body BalanceRequest request);

        @GET("/transactions")
        Call<List<TransactionItem>> getTransactions(@Query("upi_id") String upiId);

        @POST("/pay")
        Call<GenericResponse> pay(@Body PaymentRequest request);
    }
