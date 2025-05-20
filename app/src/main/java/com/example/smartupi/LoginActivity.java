package com.example.smartupi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartupi.api.ApiClient;
import com.example.smartupi.api.ApiService;
import com.example.smartupi.models.LoginRequest;
import com.example.smartupi.models.LoginResponse;
import com.example.smartupi.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText numberField, passwordField;
    Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        numberField = findViewById(R.id.username);
        passwordField = findViewById(R.id.password);
        loginBtn = findViewById(R.id.login_button);

        loginBtn.setOnClickListener(v -> {
            String number = numberField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            if (number.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            ApiService apiService = ApiClient.getApi();
            LoginRequest request = new LoginRequest(number, password);

            apiService.login(request).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        LoginResponse res = response.body();
                        LoginResponse.User user = res.getUser();
                        Log.e("Login", "Login successful");
                        if (user. getMobile().equals(number)) {
                            Log.e("Login", "Login successful");
                            new SessionManager(LoginActivity.this).saveLogin(
                                    number, user.getUpiId(), user.getBalance()
                            );
                            // ✅ Go to MainActivity
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {

                            Toast.makeText(LoginActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
