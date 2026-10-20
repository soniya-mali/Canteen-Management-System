package com.example.canteenmanagementapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private Button registerBtn;
    private TextView signInText;

    private String loginType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Views
        registerBtn = findViewById(R.id.loginBtn);
        signInText = findViewById(R.id.signInText);

        // Get Login Type from MainActivity
        loginType = getIntent().getStringExtra("loginType");

        // Default Student if no type is received
        if (loginType == null || loginType.isEmpty()) {
            loginType = "student";
        }

        // =========================
        // Register Button
        // =========================

        registerBtn.setOnClickListener(v -> {

            Intent intent = new Intent(
                    LoginActivity.this,
                    RegisterActivity.class
            );

            // Pass Student/Admin type
            intent.putExtra("loginType", loginType);

            startActivity(intent);
        });

        // =========================
        // Already Have Account
        // =========================

        signInText.setOnClickListener(v -> {

            Intent intent = new Intent(
                    LoginActivity.this,
                    LoginFormActivity.class
            );

            // Pass Student/Admin type
            intent.putExtra("loginType", loginType);

            startActivity(intent);
        });
    }
}