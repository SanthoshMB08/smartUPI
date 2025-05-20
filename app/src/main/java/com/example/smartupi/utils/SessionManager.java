package com.example.smartupi.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "SmartUPISession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_UPI_ID = "upiId";
    private static final String KEY_BALANCE = "balance";
    private static final String KEY_NUMBER = "number";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveLogin(String number, String upiId, double balance) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_NUMBER, number);
        editor.putString(KEY_UPI_ID, upiId);
        editor.putFloat(KEY_BALANCE, (float) balance); // Store double as float
        editor.apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUpiId() {
        return prefs.getString(KEY_UPI_ID, null);
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}