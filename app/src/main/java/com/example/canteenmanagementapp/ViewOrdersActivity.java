package com.example.canteenmanagementapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class ViewOrdersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_orders);

        // =========================================
        // LOAD ADMIN FRAGMENT
        // =========================================

        if (savedInstanceState == null) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.ordersFragmentContainer,
                            new AdminFragment()
                    )
                    .commit();

        }
    }
}