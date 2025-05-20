package com.example.smartupi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentResultActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_result);

        boolean success = getIntent().getBooleanExtra("success", false);
        boolean offline = getIntent().getBooleanExtra("offline", false);
        String message = getIntent().getStringExtra("message");

        ImageView statusIcon = findViewById(R.id.statusIcon);
        TextView statusText = findViewById(R.id.statusText);
        LinearLayout rootLayout = findViewById(R.id.rootLayout);

        if (success) {
            rootLayout.setBackgroundColor(Color.parseColor("#C8E6C9")); // light green
            statusIcon.setImageResource(R.drawable.ic_success); // green tick icon
            statusText.setText("Payment Successful\n" + message);
            statusText.setTextColor(Color.WHITE);
        } else if (offline) { rootLayout.setBackgroundColor(Color.parseColor("#fdfd64")); // light green
            statusIcon.setImageResource(R.drawable.ic_success); // green tick icon
            statusText.setText("Payment pending\n" + message);
            statusText.setTextColor(Color.WHITE);

        } else  {
            rootLayout.setBackgroundColor(Color.parseColor("#FFCDD2")); // light red
            statusIcon.setImageResource(R.drawable.ic_failure); // red cross icon
            statusText.setText("Payment Failed\n" + message);
            statusText.setTextColor(Color.WHITE);
        }

    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PaymentResultActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();  // Finish current activity
    }
}
