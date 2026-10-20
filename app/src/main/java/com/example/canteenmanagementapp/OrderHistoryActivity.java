package com.example.canteenmanagementapp;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class OrderHistoryActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;

    LinearLayout ordersContainer;
    TextView emptyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        // =========================================
        // DATABASE
        // =========================================

        dbHelper = new DatabaseHelper(this);

        // =========================================
        // FIND VIEWS
        // =========================================

        ordersContainer = findViewById(R.id.ordersContainer);
        emptyText = findViewById(R.id.emptyText);

        // =========================================
        // LOAD ORDERS
        // =========================================

        loadOrders();
    }

    // ============================================================
    // LOAD ORDERS
    // ============================================================

    private void loadOrders() {

        ordersContainer.removeAllViews();

        // =========================================
        // GET LOGGED-IN USER
        // =========================================

        SharedPreferences sp =
                getSharedPreferences("user", MODE_PRIVATE);

        String userEmail =
                sp.getString("email", "");

        // =========================================
        // CHECK LOGIN
        // =========================================

        if (userEmail.isEmpty()) {

            showEmptyMessage("Please login to view your orders");

            return;
        }

        // =========================================
        // DATABASE
        // =========================================

        SQLiteDatabase db =
                dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT user_name, user_email, food_name, " +
                        "food_size, quantity, price, total, order_date " +
                        "FROM orders " +
                        "WHERE user_email=? " +
                        "ORDER BY rowid DESC",
                new String[]{userEmail}
        );

        // =========================================
        // NO ORDERS
        // =========================================

        if (!cursor.moveToFirst()) {

            cursor.close();

            showEmptyMessage("No Orders Yet 🍽️");

            return;
        }

        emptyText.setVisibility(TextView.GONE);

        // =========================================
        // LOAD EVERY ORDER
        // =========================================

        do {

            String foodName =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("food_name")
                    );

            String foodSize =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("food_size")
                    );

            int quantity =
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow("quantity")
                    );

            int price =
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow("price")
                    );

            int total =
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow("total")
                    );

            String orderDate =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("order_date")
                    );

            // =========================================
            // CREATE ORDER CARD
            // =========================================

            addOrderCard(
                    foodName,
                    foodSize,
                    quantity,
                    price,
                    total,
                    orderDate
            );

        } while (cursor.moveToNext());

        cursor.close();
    }

    // ============================================================
    // CREATE ORDER CARD
    // ============================================================

    private void addOrderCard(
            String foodName,
            String foodSize,
            int quantity,
            int price,
            int total,
            String orderDate) {

        // =========================================
        // MAIN CARD
        // =========================================

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(
                20,
                18,
                20,
                18
        );

        card.setBackgroundColor(
                Color.parseColor("#FFF8EF")
        );

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(
                0,
                0,
                0,
                18
        );

        card.setLayoutParams(cardParams);

        // =========================================
        // FOOD NAME
        // =========================================

        TextView foodNameText =
                new TextView(this);

        foodNameText.setText(
                "🍽️  " + foodName
        );

        foodNameText.setTextSize(19);

        foodNameText.setTypeface(
                null,
                Typeface.BOLD
        );

        foodNameText.setTextColor(
                Color.parseColor("#3B1F0F")
        );

        // =========================================
        // SIZE + QUANTITY
        // =========================================

        TextView sizeText =
                new TextView(this);

        sizeText.setText(
                "Size: " + foodSize +
                        "   × " + quantity
        );

        sizeText.setTextSize(15);

        sizeText.setTextColor(
                Color.parseColor("#6B5142")
        );

        sizeText.setPadding(
                0,
                8,
                0,
                0
        );

        // =========================================
        // PRICE
        // =========================================

        TextView priceText =
                new TextView(this);

        priceText.setText(
                "₹" + price + " × " + quantity +
                        "  =  ₹" + total
        );

        priceText.setTextSize(16);

        priceText.setTypeface(
                null,
                Typeface.BOLD
        );

        priceText.setTextColor(
                Color.parseColor("#8B5E34")
        );

        priceText.setPadding(
                0,
                10,
                0,
                0
        );

        // =========================================
        // STATUS
        // =========================================

        TextView statusText =
                new TextView(this);

        statusText.setText(
                "●  Placed"
        );

        statusText.setTextSize(14);

        statusText.setTypeface(
                null,
                Typeface.BOLD
        );

        statusText.setTextColor(
                Color.parseColor("#2E7D32")
        );

        statusText.setPadding(
                0,
                10,
                0,
                0
        );

        // =========================================
        // DATE
        // =========================================

        TextView dateText =
                new TextView(this);

        dateText.setText(
                orderDate
        );

        dateText.setTextSize(13);

        dateText.setTextColor(
                Color.parseColor("#8A7770")
        );

        dateText.setPadding(
                0,
                6,
                0,
                0
        );

        // =========================================
        // ADD VIEWS TO CARD
        // =========================================

        card.addView(foodNameText);

        card.addView(sizeText);

        card.addView(priceText);

        card.addView(statusText);

        card.addView(dateText);

        // =========================================
        // ADD CARD TO SCREEN
        // =========================================

        ordersContainer.addView(card);
    }

    // ============================================================
    // EMPTY MESSAGE
    // ============================================================

    private void showEmptyMessage(String message) {

        emptyText.setVisibility(
                TextView.VISIBLE
        );

        emptyText.setText(message);

        emptyText.setGravity(
                Gravity.CENTER
        );
    }
}