package com.example.smartupi;
import android.app.Application;
import androidx.room.Room;




public class MyApp extends Application {
    private static AppDatabase db;

    @Override
    public void onCreate() {
        super.onCreate();
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "smart_upi_db")
                .fallbackToDestructiveMigration()
                .build();
    }

    public static AppDatabase getDatabase() {
        if (db == null) {
            throw new IllegalStateException("Database not initialized!");
        }
        return db;
    }
}

