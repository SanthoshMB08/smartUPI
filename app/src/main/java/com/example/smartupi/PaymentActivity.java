package com.example.smartupi;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.net.NetworkCapabilities;
import android.widget.Toast;

import androidx.room.Room;

import com.example.smartupi.api.ApiClient;
import com.example.smartupi.api.ApiService;
import com.example.smartupi.models.GenericResponse;
import com.example.smartupi.models.PaymentRequest;
import com.example.smartupi.utils.NetworkUtils;
import com.example.smartupi.utils.SessionManager;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {
    private EditText toUpiField, amountField, fromUpiField, pinField;
    private Button pay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        observeNetworkAndTrigger();
        setContentView(R.layout.activity_payment);
        SessionManager sessionManager = new SessionManager(this);
        String upiId = sessionManager.getUpiId();
NetworkUtils networkUtils=new NetworkUtils();
        toUpiField = findViewById(R.id.to_upi);
        amountField = findViewById(R.id.amount);

        pinField = findViewById(R.id.upi_pin);


        findViewById(R.id.pay_button).setOnClickListener(v -> {

            String toUpi = toUpiField.getText().toString();
            double amount = Double.parseDouble(amountField.getText().toString());
            String pin = pinField.getText().toString();
            boolean connected = networkUtils.isConnected(this);
            Log.d("NetCheck", "Connected: " + connected);
            if (networkUtils.isConnected(this))
            {
                PaymentRequest request = new PaymentRequest(upiId, toUpi, amount, pin);
                ApiService apiService = ApiClient.getApi();

                apiService.pay(request).enqueue(new Callback<GenericResponse>() {
                    public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            GenericResponse res = response.body();

                            Intent intent = new Intent(PaymentActivity.this, PaymentResultActivity.class);
                            intent.putExtra("success", res.success);
                            intent.putExtra("message", res.message);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(PaymentActivity.this, PaymentResultActivity.class);
                            intent.putExtra("success", false);
                            intent.putExtra("message", "Payment failed. Invalid response.");
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<GenericResponse> call, Throwable t) {
                        Intent intent = new Intent(PaymentActivity.this, PaymentResultActivity.class);
                        intent.putExtra("success", false);
                        intent.putExtra("message", "Payment failed: " + t.getMessage());
                        startActivity(intent);
                        finish();
                    }
                });
            }
            else {


                // Save the payment data in the database for offline processing
                OfflinePayment payment = new OfflinePayment();
                payment.toUpi = toUpi;

                payment.fromUpi = upiId;
                payment.amount = amount;
                payment.pin = pin;
                payment.timestamp = System.currentTimeMillis();
                Log.e("savedata","reached ofline");

                Executor executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    AppDatabase.getInstance(getApplicationContext()).offlinePaymentDao().insert(payment);

                    runOnUiThread(() -> {
                        Toast.makeText(PaymentActivity.this, "Saved offline", Toast.LENGTH_SHORT).show();
                    });
                });

                Log.e("savedata","saved");
                // Trigger payment logic here (either online or offline)
                Intent intent = new Intent(PaymentActivity.this, PaymentResultActivity.class);
                intent.putExtra("success",false );
                intent.putExtra("offline", true);
                intent.putExtra("message", "Payment failed: No internet connection and saved to local database.\n Will be payed ");
                startActivity(intent);
                finish();
            }});
    }
    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network network = cm.getActiveNetwork();
                if (network == null) return false;

                NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
                return capabilities != null &&
                        capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                        capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
            } else {
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                return networkInfo != null && networkInfo.isConnected();
            }
        }
        return false;
    }

    private ConnectivityManager.NetworkCallback networkCallback; // 👈 Store reference globally

    public void observeNetworkAndTrigger() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
Log.d("triggedA","reached in pay");
        if (networkCallback == null) {
            networkCallback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(Network network) {
                    super.onAvailable(network);
                    Log.d("triggedA","detected in pay");
                    Executors.newSingleThreadExecutor().execute(() -> {
                        List<OfflinePayment> payments = MyApp.getDatabase()
                                .offlinePaymentDao()
                                .getAllPayments();
                        if (payments == null || payments.isEmpty()) {
                            return; // Don't proceed if there's nothing to sync
                        }
                        ApiService apiService = ApiClient.getApi();
                        Log.d("triggedA","started request in pay");
                        for (OfflinePayment p : payments) {
                            PaymentRequest req = new PaymentRequest(
                                    p.fromUpi,
                                    p.toUpi,
                                    p.amount,
                                    p.pin
                            );

                            // Send API request
                            apiService.pay(req).enqueue(new Callback<GenericResponse>() {
                                @Override
                                public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                                    if (response.isSuccessful() && response.body() != null && response.body().success) {

                                        Intent intent = new Intent(PaymentActivity.this, PaymentResultActivity.class);
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
                                        Intent intent = new Intent(PaymentActivity.this, PaymentResultActivity.class);
                                        intent.putExtra("success", false);
                                        intent.putExtra("message", "Payment failed. Invalid response.");
                                        startActivity(intent);
                                        finish();
                                    }
                                }

                                @Override
                                public void onFailure(Call<GenericResponse> call, Throwable t) {
                                    Intent intent = new Intent(PaymentActivity.this, PaymentResultActivity.class);
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
            };

            // 🔐 Register only once
            cm.registerDefaultNetworkCallback(networkCallback);
        }
    }

}

