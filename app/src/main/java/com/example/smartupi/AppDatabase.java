package com.example.smartupi;
//import static java.time.chrono.ThaiBuddhistChronology.INSTANCE;



import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {OfflinePayment.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;
    public abstract OfflinePaymentDao offlinePaymentDao();
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "smart_upi_db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
