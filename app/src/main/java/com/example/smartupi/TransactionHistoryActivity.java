package com.example.smartupi;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartupi.api.ApiClient;
import com.example.smartupi.api.ApiService;
import com.example.smartupi.models.TransactionAdapter;
import com.example.smartupi.models.TransactionItem;
import com.example.smartupi.utils.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionHistoryActivity extends AppCompatActivity {
    private ListView transactionListView;
     // Replace with actual user UPI
     private SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        transactionListView = findViewById(R.id.transaction_list);
       sessionManager = new SessionManager(this);
        String upiId = sessionManager.getUpiId();
        // Fetch transactions from backend
        fetchTransactionHistory(upiId);
    }

    private void fetchTransactionHistory(String upiId) {
        ApiService apiService = ApiClient.getApi();
        Call<List<TransactionItem>> call = apiService.getTransactions(upiId);

        call.enqueue(new Callback<List<TransactionItem>>() {
            @Override
            public void onResponse(Call<List<TransactionItem>> call, Response<List<TransactionItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TransactionItem> transactions = response.body();
                    runOnUiThread(() -> {
                        TransactionAdapter adapter = new TransactionAdapter(TransactionHistoryActivity.this, transactions);
                        transactionListView.setAdapter(adapter);
                    });
                } else {
                    Toast.makeText(TransactionHistoryActivity.this, "Failed to fetch transactions", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<TransactionItem>> call, Throwable t) {
                Toast.makeText(TransactionHistoryActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

