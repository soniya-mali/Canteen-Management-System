package com.example.canteenmanagementapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginFormActivity extends AppCompatActivity {

    EditText email;
    EditText password;
    Button loginBtn;

    DatabaseHelper dbHelper;

    private String loginType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login_form);

        // =========================
        // Initialize Views
        // =========================

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);

        // =========================
        // Database
        // =========================

        dbHelper = new DatabaseHelper(this);

        // =========================
        // Get Login Type
        // =========================

        loginType = getIntent().getStringExtra("loginType");

        // Default = Student
        if (loginType == null || loginType.trim().isEmpty()) {
            loginType = "student";
        }

        // =========================
        // Login Button
        // =========================

        loginBtn.setOnClickListener(v -> {

            String enteredEmail =
                    email.getText().toString().trim();

            String enteredPassword =
                    password.getText().toString().trim();

            // =========================
            // Empty Validation
            // =========================

            if (enteredEmail.isEmpty()) {

                email.setError("Enter Email");
                email.requestFocus();
                return;
            }

            if (enteredPassword.isEmpty()) {

                password.setError("Enter Password");
                password.requestFocus();
                return;
            }

            // =====================================================
            // ADMIN LOGIN
            // =====================================================

            if (loginType.equalsIgnoreCase("admin")) {

                if (enteredEmail.equalsIgnoreCase("admin@gmail.com")
                        && enteredPassword.equals("1234")) {

                    Toast.makeText(
                            LoginFormActivity.this,
                            "Admin Login Successful",
                            Toast.LENGTH_SHORT
                    ).show();

                    Intent intent = new Intent(
                            LoginFormActivity.this,
                            AdminActivity.class
                    );

                    intent.setFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    );

                    startActivity(intent);

                } else {

                    Toast.makeText(
                            LoginFormActivity.this,
                            "Invalid Admin Email or Password",
                            Toast.LENGTH_SHORT
                    ).show();
                }

                // IMPORTANT:
                // Admin login झाल्यावर Student database
                // login खाली execute होणार नाही.
                return;
            }

            // =====================================================
            // STUDENT LOGIN
            // =====================================================

            SQLiteDatabase db =
                    dbHelper.getReadableDatabase();

            Cursor cursor = null;

            try {

                cursor = db.rawQuery(
                        "SELECT name, email, password " +
                                "FROM users " +
                                "WHERE email=? AND password=?",
                        new String[]{
                                enteredEmail,
                                enteredPassword
                        }
                );

                // =========================
                // Student Found
                // =========================

                if (cursor.moveToFirst()) {

                    String userName =
                            cursor.getString(
                                    cursor.getColumnIndexOrThrow("name")
                            );

                    String userEmail =
                            cursor.getString(
                                    cursor.getColumnIndexOrThrow("email")
                            );

                    // =========================
                    // Save Login Session
                    // =========================

                    SharedPreferences sp =
                            getSharedPreferences(
                                    "user",
                                    MODE_PRIVATE
                            );

                    SharedPreferences.Editor editor =
                            sp.edit();

                    editor.putString(
                            "name",
                            userName
                    );

                    editor.putString(
                            "email",
                            userEmail
                    );

                    editor.putBoolean(
                            "isLoggedIn",
                            true
                    );

                    editor.apply();

                    // =========================
                    // Login Successful
                    // =========================

                    Toast.makeText(
                            LoginFormActivity.this,
                            "Welcome " + userName + " 😊",
                            Toast.LENGTH_SHORT
                    ).show();

                    // =========================
                    // Open Student Menu
                    // =========================

                    Intent intent = new Intent(
                            LoginFormActivity.this,
                            MenuActivity.class
                    );

                    intent.setFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    );

                    startActivity(intent);

                } else {

                    Toast.makeText(
                            LoginFormActivity.this,
                            "Invalid Email or Password",
                            Toast.LENGTH_SHORT
                    ).show();
                }

            } finally {

                if (cursor != null) {
                    cursor.close();
                }
            }
        });
    }
}