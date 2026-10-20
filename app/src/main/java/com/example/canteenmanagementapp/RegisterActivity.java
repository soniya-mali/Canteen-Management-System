package com.example.canteenmanagementapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    EditText name, email, password;
    Button registerBtn;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        registerBtn = findViewById(R.id.registerBtn);

        dbHelper = new DatabaseHelper(this);

        registerBtn.setOnClickListener(v -> {

            String userName = name.getText().toString().trim();
            String userEmail = email.getText().toString().trim();
            String userPassword = password.getText().toString().trim();

            if(userName.isEmpty() || userEmail.isEmpty() || userPassword.isEmpty()){
                Toast.makeText(this,"Please fill all fields",Toast.LENGTH_SHORT).show();
                return;
            }

            SQLiteDatabase db = dbHelper.getWritableDatabase();

            Cursor cursor = db.rawQuery(
                    "SELECT * FROM users WHERE email=?",
                    new String[]{userEmail});

            if(cursor.moveToFirst()){
                Toast.makeText(this,
                        "Email already registered.\nPlease Login.",
                        Toast.LENGTH_SHORT).show();
                cursor.close();
                return;
            }

            cursor.close();

            db.execSQL(
                    "INSERT INTO users(name,email,password) VALUES(?,?,?)",
                    new Object[]{userName,userEmail,userPassword});

            Toast.makeText(this,
                    "Registration Successful",
                    Toast.LENGTH_SHORT).show();

            startActivity(new Intent(RegisterActivity.this,
                    LoginFormActivity.class));

            finish();

        });
    }
}