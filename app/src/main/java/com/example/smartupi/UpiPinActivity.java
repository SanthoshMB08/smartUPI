package com.example.smartupi;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartupi.api.ApiClient;
import com.example.smartupi.api.ApiService;
import com.example.smartupi.models.BalanceRequest;
import com.example.smartupi.models.BalanceResponse;
import com.example.smartupi.models.LoginRequest;
import com.example.smartupi.models.TransactionItem;
import com.example.smartupi.utils.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpiPinActivity extends AppCompatActivity {
    private EditText pinField;
    private SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(this);
        String upiId = sessionManager.getUpiId();

        setContentView(R.layout.activity_upi_pin);

        pinField = findViewById(R.id.upi_pin);

        findViewById(R.id.save_pin_button).setOnClickListener(v -> {
            String pin = pinField.getText().toString();
 checkBalance(upiId,pin);
            // Save the UPI PIN securely (consider encryption for real apps)
            // PinManager.savePin(pin);
        });
    }
    private void checkBalance(String upiId,String pin) {
        ApiService apiService = ApiClient.getApi();
        BalanceRequest balanceRequest=new BalanceRequest(upiId,pin);
        apiService.checkBalance(balanceRequest).enqueue(new Callback<BalanceResponse>(){
            @Override
            public void onResponse(Call<BalanceResponse> call, Response<BalanceResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    setContentView(R.layout.activity_upi_pin);
                    TextView balanceTextView = findViewById(R.id.balance);
                    balanceTextView.setText("Balance: " + response.body().balance);
                    balanceTextView.setVisibility(View.VISIBLE);
                    Button button=findViewById(R.id.save_pin_button);
                    button.setVisibility(View.GONE);
                    pinField.setVisibility(View.GONE);


                }


            }
            @Override
            public void onFailure(Call<BalanceResponse> call, Throwable t) {
                setContentView(R.layout.activity_upi_pin);
                TextView balanceTextView = findViewById(R.id.balance);
                balanceTextView.setText("Balance: " + "unable to fetch");

            }
        });

    }
}

