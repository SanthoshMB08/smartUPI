package com.example.smartupi;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smartupi.utils.SessionManager;
public class LoginRegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            // User is already logged in
            startActivity(new Intent(this, MainActivity.class));
        } else {
            // Show login/register options
            setContentView(R.layout.activity_login_register);
            findViewById(R.id.login).setOnClickListener(v ->
                    startActivity(new Intent(this, LoginActivity.class)));
            findViewById(R.id.register).setOnClickListener(v ->
                    startActivity(new Intent(this, RegisterActivity.class)));
        }
    }
}
