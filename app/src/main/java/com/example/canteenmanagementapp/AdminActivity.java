package com.example.canteenmanagementapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class AdminActivity extends AppCompatActivity {

    Button addFoodBtn;
    Button updatePriceBtn;
    Button viewOrdersBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // =====================================================
        // ADMIN PANEL LAYOUT
        // =====================================================

        setContentView(R.layout.activity_admin);

        // =====================================================
        // INITIALIZE BUTTONS
        // =====================================================

        addFoodBtn = findViewById(R.id.addFoodBtn);
        updatePriceBtn = findViewById(R.id.updatePriceBtn);
        viewOrdersBtn = findViewById(R.id.viewOrdersBtn);

        // =====================================================
        // ADD FOOD ITEM
        // =====================================================

        addFoodBtn.setOnClickListener(v -> {

            Toast.makeText(
                    AdminActivity.this,
                    "Add Food Item selected",
                    Toast.LENGTH_SHORT
            ).show();

            // Actual Add Food functionality
            // will be added in the next step.
        });

        // =====================================================
        // UPDATE PRICE
        // =====================================================

        updatePriceBtn.setOnClickListener(v -> {

            Toast.makeText(
                    AdminActivity.this,
                    "Update Price selected",
                    Toast.LENGTH_SHORT
            ).show();

            // Actual Update Price functionality
            // will be added in the next step.
        });

        // =====================================================
        // VIEW ORDERS
        // =====================================================

        viewOrdersBtn.setOnClickListener(v -> {

            loadFragment(new AdminFragment());

        });
    }

    // =====================================================
    // LOAD FRAGMENT
    // =====================================================

    private void loadFragment(Fragment fragment) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.fragmentContainer,
                        fragment
                )
                .addToBackStack(null)
                .commit();
    }
}