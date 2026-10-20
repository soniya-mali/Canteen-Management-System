package com.example.canteenmanagementapp;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PaymentActivity
        extends AppCompatActivity
        implements PaymentResultListener {

    TextView paymentTotal;
    TextView orderSummary;

    RadioGroup paymentGroup;

    RadioButton onlinePayment;
    RadioButton cashOnDelivery;

    Button backBtn;
    Button placeOrderBtn;

    DatabaseHelper dbHelper;

    int grandTotal = 0;

    ArrayList<String> selectedFoodNames;
    ArrayList<String> selectedFoodSizes;
    ArrayList<Integer> selectedFoodPrices;
    ArrayList<Integer> selectedQuantities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_payment
        );

        dbHelper =
                new DatabaseHelper(this);

        Checkout.preload(
                getApplicationContext()
        );

        paymentTotal =
                findViewById(
                        R.id.paymentTotal
                );

        orderSummary =
                findViewById(
                        R.id.orderSummary
                );

        paymentGroup =
                findViewById(
                        R.id.paymentGroup
                );

        onlinePayment =
                findViewById(
                        R.id.onlinePayment
                );

        cashOnDelivery =
                findViewById(
                        R.id.cashOnDelivery
                );

        backBtn =
                findViewById(
                        R.id.backBtn
                );

        placeOrderBtn =
                findViewById(
                        R.id.placeOrderBtn
                );

        onlinePayment.setChecked(true);

        getSelectedItems();

        loadOrderSummary();

        backBtn.setOnClickListener(v -> {
            finish();
        });

        placeOrderBtn.setOnClickListener(v -> {
            startPayment();
        });
    }

    // =====================================================
    // GET SELECTED ITEMS FROM CART ACTIVITY
    // =====================================================

    private void getSelectedItems() {

        Intent intent =
                getIntent();

        selectedFoodNames =
                intent.getStringArrayListExtra(
                        "foodNames"
                );

        selectedFoodSizes =
                intent.getStringArrayListExtra(
                        "foodSizes"
                );

        selectedFoodPrices =
                intent.getIntegerArrayListExtra(
                        "foodPrices"
                );

        selectedQuantities =
                intent.getIntegerArrayListExtra(
                        "quantities"
                );

        if (selectedFoodNames == null) {
            selectedFoodNames =
                    new ArrayList<>();
        }

        if (selectedFoodSizes == null) {
            selectedFoodSizes =
                    new ArrayList<>();
        }

        if (selectedFoodPrices == null) {
            selectedFoodPrices =
                    new ArrayList<>();
        }

        if (selectedQuantities == null) {
            selectedQuantities =
                    new ArrayList<>();
        }
    }

    // =====================================================
    // LOAD ONLY SELECTED ORDER SUMMARY
    // =====================================================

    private void loadOrderSummary() {

        if (selectedFoodNames.isEmpty()) {

            Toast.makeText(
                    this,
                    "No item selected",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }

        StringBuilder summary =
                new StringBuilder();

        grandTotal = 0;

        for (int i = 0;
             i < selectedFoodNames.size();
             i++) {

            String foodName =
                    selectedFoodNames.get(i);

            String foodSize =
                    selectedFoodSizes.get(i);

            int price =
                    selectedFoodPrices.get(i);

            int quantity =
                    selectedQuantities.get(i);

            int itemTotal =
                    price * quantity;

            grandTotal += itemTotal;

            summary.append(foodName)
                    .append("\n");

            summary.append("Size: ")
                    .append(foodSize)
                    .append("   × ")
                    .append(quantity)
                    .append("\n");

            summary.append("₹")
                    .append(itemTotal)
                    .append("\n\n");
        }

        orderSummary.setText(
                summary.toString()
        );

        paymentTotal.setText(
                "₹" + grandTotal
        );
    }

    // =====================================================
    // START PAYMENT
    // =====================================================

    private void startPayment() {

        if (grandTotal <= 0) {

            Toast.makeText(
                    this,
                    "Invalid order amount",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        int selectedId =
                paymentGroup.getCheckedRadioButtonId();

        if (selectedId == -1) {

            Toast.makeText(
                    this,
                    "Please select payment method",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (selectedId ==
                R.id.cashOnDelivery) {

            placeOrder(
                    "Cash on Delivery"
            );

            return;
        }

        if (selectedId ==
                R.id.onlinePayment) {

            openRazorpay();
        }
    }

    // =====================================================
    // OPEN RAZORPAY
    // =====================================================

    private void openRazorpay() {

        try {

            Checkout checkout =
                    new Checkout();

            checkout.setKeyID(
                    "rzp_test_TSlj7PRsejsDlT"
            );

            JSONObject options =
                    new JSONObject();

            options.put(
                    "amount",
                    grandTotal * 100
            );

            options.put(
                    "currency",
                    "INR"
            );

            options.put(
                    "name",
                    "Canteen Management App"
            );

            options.put(
                    "description",
                    "Canteen Food Order"
            );

            SharedPreferences sp =
                    getSharedPreferences(
                            "user",
                            MODE_PRIVATE
                    );

            String userEmail =
                    sp.getString(
                            "email",
                            ""
                    );

            String userName =
                    sp.getString(
                            "name",
                            "Guest"
                    );

            options.put(
                    "prefill.email",
                    userEmail
            );

            options.put(
                    "prefill.name",
                    userName
            );

            checkout.open(
                    PaymentActivity.this,
                    options
            );

        } catch (Exception e) {

            Toast.makeText(
                    PaymentActivity.this,
                    "Unable to start payment: "
                            + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    // =====================================================
    // PAYMENT SUCCESS
    // =====================================================

    @Override
    public void onPaymentSuccess(
            String razorpayPaymentID) {

        Toast.makeText(
                PaymentActivity.this,
                "Payment Successful 🎉",
                Toast.LENGTH_SHORT
        ).show();

        placeOrder(
                "Online Payment"
        );
    }

    // =====================================================
    // PAYMENT FAILED
    // =====================================================

    @Override
    public void onPaymentError(
            int code,
            String response) {

        Toast.makeText(
                PaymentActivity.this,
                "Payment Failed ❌",
                Toast.LENGTH_LONG
        ).show();
    }

    // =====================================================
    // SAVE ONLY SELECTED ITEMS
    // =====================================================

    private void placeOrder(
            String paymentMethod) {

        SharedPreferences sp =
                getSharedPreferences(
                        "user",
                        MODE_PRIVATE
                );

        String userEmail =
                sp.getString(
                        "email",
                        ""
                );

        String userName =
                sp.getString(
                        "name",
                        "Guest"
                );

        if (userEmail.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (selectedFoodNames.isEmpty()) {

            Toast.makeText(
                    this,
                    "No selected items",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        SQLiteDatabase db =
                dbHelper.getWritableDatabase();

        String orderDate =
                new SimpleDateFormat(
                        "dd/MM/yyyy HH:mm",
                        Locale.getDefault()
                ).format(
                        new Date()
                );

        db.beginTransaction();

        try {

            // ==========================================
            // INSERT ONLY SELECTED ITEMS INTO ORDERS
            // ==========================================

            for (int i = 0;
                 i < selectedFoodNames.size();
                 i++) {

                String foodName =
                        selectedFoodNames.get(i);

                String foodSize =
                        selectedFoodSizes.get(i);

                int quantity =
                        selectedQuantities.get(i);

                int price =
                        selectedFoodPrices.get(i);

                int total =
                        quantity * price;

                ContentValues values =
                        new ContentValues();

                values.put(
                        "user_name",
                        userName
                );

                values.put(
                        "user_email",
                        userEmail
                );

                values.put(
                        "food_name",
                        foodName
                );

                values.put(
                        "food_size",
                        foodSize
                );

                values.put(
                        "quantity",
                        quantity
                );

                values.put(
                        "price",
                        price
                );

                values.put(
                        "total",
                        total
                );

                values.put(
                        "order_date",
                        orderDate
                );

                db.insert(
                        "orders",
                        null,
                        values
                );
            }

            // ==========================================
            // DELETE ONLY SELECTED ITEMS FROM CART
            // ==========================================

            for (int i = 0;
                 i < selectedFoodNames.size();
                 i++) {

                String foodName =
                        selectedFoodNames.get(i);

                String foodSize =
                        selectedFoodSizes.get(i);

                db.delete(
                        "cart",
                        "user_email=? AND food_name=? AND food_size=?",
                        new String[]{
                                userEmail,
                                foodName,
                                foodSize
                        }
                );
            }

            db.setTransactionSuccessful();

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Order failed: "
                            + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();

            return;

        } finally {

            db.endTransaction();
        }

        Toast.makeText(
                this,
                "Order Placed Successfully 🎉",
                Toast.LENGTH_SHORT
        ).show();

        Intent intent =
                new Intent(
                        PaymentActivity.this,
                        OrderHistoryActivity.class
                );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
        );

        startActivity(intent);

        finish();
    }
}