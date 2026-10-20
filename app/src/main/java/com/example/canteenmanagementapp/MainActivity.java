package com.example.canteenmanagementapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnStudent;
    Button btnAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // =========================
        // Initialize Buttons
        // =========================

        btnStudent = findViewById(R.id.btnStudent);
        btnAdmin = findViewById(R.id.btnAdmin);

        // =========================
        // STUDENT LOGIN
        // =========================

        btnStudent.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    LoginActivity.class
            );

            // Send login type as Student
            intent.putExtra("loginType", "student");

            startActivity(intent);
        });

        // =========================
        // ADMIN LOGIN
        // =========================

        btnAdmin.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    LoginActivity.class
            );

            // Send login type as Admin
            intent.putExtra("loginType", "admin");

            startActivity(intent);
        });
    }
}