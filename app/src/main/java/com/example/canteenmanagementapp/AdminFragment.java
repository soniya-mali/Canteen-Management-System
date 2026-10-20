package com.example.canteenmanagementapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class AdminFragment extends Fragment {

    // =====================================================
    // DECLARATION
    // =====================================================

    EditText foodName;
    EditText foodPrice;

    Button placeOrderBtn;

    TextView orderHistory;

    DatabaseHelper dbHelper;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public AdminFragment() {
        // Empty constructor required
    }

    // =====================================================
    // ON CREATE VIEW
    // =====================================================

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_admin,
                container,
                false
        );

        // =================================================
        // DATABASE
        // =================================================

        dbHelper = new DatabaseHelper(
                requireContext()
        );

        // =================================================
        // INITIALIZE VIEWS
        // =================================================

        foodName = view.findViewById(
                R.id.foodName
        );

        foodPrice = view.findViewById(
                R.id.foodPrice
        );

        placeOrderBtn = view.findViewById(
                R.id.placeOrderBtn
        );

        orderHistory = view.findViewById(
                R.id.orderHistory
        );

        // =================================================
        // LOAD ALL ORDERS
        // =================================================

        loadAllOrders();

        // =================================================
        // EXISTING PLACE ORDER BUTTON
        // =================================================

        if (placeOrderBtn != null) {

            placeOrderBtn.setOnClickListener(v -> {

                String item =
                        foodName.getText()
                                .toString()
                                .trim();

                String price =
                        foodPrice.getText()
                                .toString()
                                .trim();

                // =========================================
                // VALIDATION
                // =========================================

                if (TextUtils.isEmpty(item)
                        || TextUtils.isEmpty(price)) {

                    Toast.makeText(
                            requireContext(),
                            "Fill all fields",
                            Toast.LENGTH_SHORT
                    ).show();

                    return;
                }

                // =========================================
                // EXISTING FUNCTIONALITY
                // =========================================

                Toast.makeText(
                        requireContext(),
                        "Use Student Cart to place orders",
                        Toast.LENGTH_SHORT
                ).show();
            });
        }

        return view;
    }

    // =====================================================
    // LOAD ALL ORDERS FOR ADMIN
    // =====================================================

    private void loadAllOrders() {

        if (dbHelper == null
                || orderHistory == null) {

            return;
        }

        SQLiteDatabase db =
                dbHelper.getReadableDatabase();

        Cursor cursor = null;

        try {

            // =================================================
            // IMPORTANT
            // =================================================
            // NO USER EMAIL FILTER HERE.
            //
            // Therefore Admin will get:
            //
            // Old orders
            // New orders
            // Orders from Student 1
            // Orders from Student 2
            // Orders from Student 3
            //
            // Everything from orders table.
            // =================================================

            cursor = db.rawQuery(

                    "SELECT " +
                            "order_id, " +
                            "user_name, " +
                            "user_email, " +
                            "food_name, " +
                            "food_size, " +
                            "quantity, " +
                            "price, " +
                            "total, " +
                            "order_date " +
                            "FROM orders " +
                            "ORDER BY order_id DESC",

                    null
            );

            // =================================================
            // CHECK WHETHER ORDERS EXIST
            // =================================================

            if (cursor.getCount() == 0) {

                orderHistory.setText(
                        "📋 ALL STUDENT ORDERS\n\n" +
                                "No Orders Yet 😔"
                );

                return;
            }

            // =================================================
            // STRING BUILDER
            // =================================================

            StringBuilder data =
                    new StringBuilder();

            data.append(
                    "📋 ALL STUDENT ORDERS\n\n"
            );

            // =================================================
            // ORDER COUNT
            // =================================================

            int orderNumber = 1;

            // =================================================
            // LOOP THROUGH EVERY ORDER
            // =================================================

            while (cursor.moveToNext()) {

                // =============================================
                // ORDER ID
                // =============================================

                int orderId =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "order_id"
                                )
                        );

                // =============================================
                // CUSTOMER NAME
                // =============================================

                String userName =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "user_name"
                                )
                        );

                // =============================================
                // CUSTOMER EMAIL
                // =============================================

                String userEmail =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "user_email"
                                )
                        );

                // =============================================
                // FOOD NAME
                // =============================================

                String foodNameValue =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "food_name"
                                )
                        );

                // =============================================
                // FOOD SIZE
                // =============================================

                String foodSize =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "food_size"
                                )
                        );

                // =============================================
                // QUANTITY
                // =============================================

                int quantity =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "quantity"
                                )
                        );

                // =============================================
                // PRICE
                // =============================================

                int price =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "price"
                                )
                        );

                // =============================================
                // TOTAL
                // =============================================

                int total =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "total"
                                )
                        );

                // =============================================
                // ORDER DATE
                // =============================================

                String orderDate =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "order_date"
                                )
                        );

                // =================================================
                // ORDER SEPARATOR
                // =================================================

                data.append(
                        "━━━━━━━━━━━━━━━━━━━━\n"
                );

                // =================================================
                // ORDER NUMBER
                // =================================================

                data.append(
                                "🧾 Order #"
                        )
                        .append(orderId)
                        .append("\n\n");

                // =================================================
                // CUSTOMER DETAILS
                // =================================================

                data.append(
                                "👤 Name: "
                        )
                        .append(userName)
                        .append("\n");

                data.append(
                                "📧 Email: "
                        )
                        .append(userEmail)
                        .append("\n\n");

                // =================================================
                // FOOD DETAILS
                // =================================================

                data.append(
                                "🍔 Food: "
                        )
                        .append(foodNameValue)
                        .append("\n");

                data.append(
                                "📏 Size: "
                        )
                        .append(foodSize)
                        .append("\n");

                data.append(
                                "🔢 Quantity: "
                        )
                        .append(quantity)
                        .append("\n\n");

                // =================================================
                // PRICE
                // =================================================

                data.append(
                                "💰 Price: ₹"
                        )
                        .append(price)
                        .append("\n");

                // =================================================
                // TOTAL
                // =================================================

                data.append(
                                "💵 Total: ₹"
                        )
                        .append(total)
                        .append("\n");

                // =================================================
                // ORDER DATE
                // =================================================

                data.append(
                                "📅 Date: "
                        )
                        .append(orderDate)
                        .append("\n");

                // =================================================
                // END OF ORDER
                // =================================================

                data.append(
                        "━━━━━━━━━━━━━━━━━━━━\n\n"
                );

                orderNumber++;
            }

            // =================================================
            // SET ALL ORDERS TO TEXTVIEW
            // =================================================

            orderHistory.setText(
                    data.toString()
            );

        } catch (Exception e) {

            // =================================================
            // ERROR MESSAGE
            // =================================================

            Toast.makeText(
                    requireContext(),
                    "Unable to load orders: "
                            + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();

            orderHistory.setText(
                    "Unable to load orders."
            );

        } finally {

            // =================================================
            // CLOSE CURSOR
            // =================================================

            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // =====================================================
    // REFRESH ORDERS WHEN FRAGMENT RESUMES
    // =====================================================

    @Override
    public void onResume() {

        super.onResume();

        if (dbHelper != null
                && orderHistory != null) {

            loadAllOrders();
        }
    }
}