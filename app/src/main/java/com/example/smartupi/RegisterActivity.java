package com.example.smartupi;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartupi.api.ApiClient;
import com.example.smartupi.api.ApiService;
import com.example.smartupi.models.GenericResponse;
import com.example.smartupi.models.RegisterRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private EditText numberField, upiIdField, passwordField, upiPinField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        numberField = findViewById(R.id.number);
        upiIdField = findViewById(R.id.upi_id);
        passwordField = findViewById(R.id.password);
        upiPinField = findViewById(R.id.upi_pin);

        findViewById(R.id.register_button).setOnClickListener(v -> {
            String number = numberField.getText().toString();
            String upiId = upiIdField.getText().toString();
            String password = passwordField.getText().toString();
            String upiPin = upiPinField.getText().toString();

            RegisterRequest request = new RegisterRequest(number, upiId, password, upiPin);

            ApiService apiService = ApiClient.getApi();
            apiService.register(request).enqueue(new Callback<GenericResponse>() {
                @Override
                public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<GenericResponse> call, Throwable t) {
                    Toast.makeText(RegisterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
