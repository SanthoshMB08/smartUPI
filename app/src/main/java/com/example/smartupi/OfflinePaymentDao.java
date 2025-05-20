package com.example.smartupi;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface OfflinePaymentDao {
    @Insert
    void insert(OfflinePayment payment);

    @Query("SELECT * FROM offline_payments")
    List<OfflinePayment> getAllPayments();

    @Delete
    void delete(OfflinePayment payment);
}

