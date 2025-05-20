package com.example.smartupi;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "offline_payments")
public class OfflinePayment {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String fromUpi;
    public String toUpi;
    public double amount;
    public String pin;
    public long timestamp;  // for sorting by timestamp
}

