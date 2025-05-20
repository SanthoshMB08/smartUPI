package com.example.smartupi;
import static com.example.smartupi.utils.NetworkUtils.isConnected;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartupi.api.ApiClient;
import com.example.smartupi.api.ApiService;
import com.example.smartupi.models.GenericResponse;
import com.example.smartupi.models.PaymentRequest;
import com.example.smartupi.utils.NetworkUtils;
import com.example.smartupi.utils.SessionManager;

import java.util.List;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private ConnectivityManager.NetworkCallback networkCallback;
    public NetworkUtils networkUtils=new NetworkUtils();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SessionManager sessionManager = new SessionManager(this);
        String upiId = sessionManager.getUpiId();


        super.onCreate(savedInstanceState);
        boolean connected = isConnected(this);
        Log.d("NetCheck", "Connected: " + connected);
        observeNetworkAndTrigger();
        if (upiId == null || upiId.isEmpty()) {
            // 🔁 Redirect to LoginRegisterActivity if not logged in
            Intent intent = new Intent(MainActivity.this, LoginRegisterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
         PaymentActivity paymentActivity=new PaymentActivity();


        TextView upiText = findViewById(R.id.upi_text);
        upiText.setText("Logged in as: " + upiId);


        findViewById(R.id.logout_button).setOnClickListener(v -> {
            sessionManager.logout();
            Intent i = new Intent(MainActivity.this, LoginRegisterActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        });
        findViewById(R.id.transaction_history_button).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, TransactionHistoryActivity.class));
        });

        findViewById(R.id.pay_now_button).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, PaymentActivity.class));
        });

        findViewById(R.id.check_balance_button).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, UpiPinActivity.class));
        });
    }


    public void observeNetworkAndTrigger() {

        Handler handler = new Handler(Looper.getMainLooper());
Log.d("triggerA","reached");
        Runnable networkCheckRunnable = new Runnable() {
            @Override
            public void run() {
                if (networkUtils.isConnected(MainActivity.this)) {
                    Log.d("triggerA","detected");
                    Executors.newSingleThreadExecutor().execute(() -> {
                        List<OfflinePayment> payments = MyApp.getDatabase()
                                .offlinePaymentDao()
                                .getAllPayments();
                        Log.d("triggerA","extracted"+payments);
                        ApiService apiService = ApiClient.getApi();

                        for (OfflinePayment p : payments) {
                            PaymentRequest req = new PaymentRequest(
                                    p.fromUpi,
                                    p.toUpi,
                                    p.amount,
                                    p.pin
                            );
Log.d("triggerA","starting request");
                            // Send API request
                            apiService.pay(req).enqueue(new Callback<GenericResponse>() {
                                @Override
                                public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                                    if (response.isSuccessful() && response.body() != null && response.body().success) {

                                        Intent intent = new Intent(MainActivity.this, PaymentResultActivity.class);
                                        intent.putExtra("success", true);
                                        intent.putExtra("message", "payment success");
                                        startActivity(intent);
                                        finish();

                                        // ✅ Delete the synced payment from local DB
                                        Executors.newSingleThreadExecutor().execute(() -> {
                                            MyApp.getDatabase().offlinePaymentDao().delete(p);
                                        });

                                        // Optional: Notify user
                                        runOnUiThread(() -> {
                                            Toast.makeText(getApplicationContext(),
                                                    "Offline payment to " + p.toUpi + " synced successfully",
                                                    Toast.LENGTH_SHORT).show();
                                        });
                                    } else {
                                        Intent intent = new Intent(MainActivity.this, PaymentResultActivity.class);
                                        intent.putExtra("success", false);
                                        intent.putExtra("message", "Payment failed. Invalid response.");
                                        startActivity(intent);
                                        finish();
                                    }
                                }

                                @Override
                                public void onFailure(Call<GenericResponse> call, Throwable t) {
                                    Intent intent = new Intent(MainActivity.this, PaymentResultActivity.class);
                                    intent.putExtra("success", false);
                                    intent.putExtra("message", "Payment failed: " + t.getMessage());
                                    startActivity(intent);
                                    finish();

                                    runOnUiThread(() ->
                                            Toast.makeText(getApplicationContext(), "Sync failed: " + t.getMessage(), Toast.LENGTH_SHORT).show());
                                }
                            });
                        }
                    });
                }

                // Schedule next check in 60 seconds
                handler.postDelayed(this, 10000);
            }
        };

        // Start the first check immediately
        handler.post(networkCheckRunnable);
    }}
