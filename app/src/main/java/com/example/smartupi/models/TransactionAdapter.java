package com.example.smartupi.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.smartupi.R;

import java.util.List;

public class TransactionAdapter extends ArrayAdapter<TransactionItem> {
    public TransactionAdapter(Context context, List<TransactionItem> transactions) {
        super(context, 0, transactions);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TransactionItem txn = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.transaction_item, parent, false);
        }

        TextView fromToView = convertView.findViewById(R.id.from_to);
        TextView amountView = convertView.findViewById(R.id.amount);
        TextView timeView = convertView.findViewById(R.id.time);

        fromToView.setText("From: " + txn.from_upi + " → To: " + txn.to_upi);
        amountView.setText("₹" + txn.amount);
        timeView.setText("At: " + txn.timestamp);

        return convertView;
    }
}