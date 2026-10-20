package com.example.canteenmanagementapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    RecyclerView cartListView;
    TextView totalBill;

    Button continueBtn;
    Button placeOrderBtn;

    LinearLayout homeNav;
    LinearLayout wishlistNav;
    LinearLayout cartNav;

    DatabaseHelper dbHelper;

    ArrayList<String> foodNames = new ArrayList<>();
    ArrayList<String> foodSizes = new ArrayList<>();
    ArrayList<Integer> foodPrices = new ArrayList<>();
    ArrayList<Integer> quantities = new ArrayList<>();
    ArrayList<Integer> images = new ArrayList<>();

    // Selected items
    ArrayList<Boolean> selectedItems = new ArrayList<>();

    CartAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cart);


        // ==============================
        // DATABASE
        // ==============================

        dbHelper = new DatabaseHelper(this);


        // ==============================
        // FIND VIEWS
        // ==============================

        cartListView = findViewById(R.id.cartRecycler);

        totalBill = findViewById(R.id.totalBill);

        continueBtn = findViewById(R.id.continueBtn);

        placeOrderBtn = findViewById(R.id.placeOrderBtn);

        homeNav = findViewById(R.id.homeNav);

        wishlistNav = findViewById(R.id.wishlistNav);

        cartNav = findViewById(R.id.cartNav);


        // ==============================
        // RECYCLER VIEW
        // ==============================

        cartListView.setLayoutManager(
                new LinearLayoutManager(this)
        );


        // ==============================
        // LOAD CART
        // ==============================

        loadCartItems();


        // ==============================
        // CONTINUE SHOPPING
        // ==============================

        continueBtn.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            CartActivity.this,
                            MenuActivity.class
                    );

            startActivity(intent);

        });


        // ==============================
        // PLACE ORDER - SELECTED ITEMS ONLY
        // ==============================

        placeOrderBtn.setOnClickListener(v -> {

            ArrayList<String> selectedNames =
                    new ArrayList<>();

            ArrayList<String> selectedSizes =
                    new ArrayList<>();

            ArrayList<Integer> selectedPrices =
                    new ArrayList<>();

            ArrayList<Integer> selectedQuantities =
                    new ArrayList<>();


            // =========================================
            // GET ONLY SELECTED ITEMS
            // =========================================

            for (int i = 0; i < foodNames.size(); i++) {

                if (selectedItems.get(i)) {

                    selectedNames.add(
                            foodNames.get(i)
                    );

                    selectedSizes.add(
                            foodSizes.get(i)
                    );

                    selectedPrices.add(
                            foodPrices.get(i)
                    );

                    selectedQuantities.add(
                            quantities.get(i)
                    );
                }
            }


            // =========================================
            // NO ITEM SELECTED
            // =========================================

            if (selectedNames.isEmpty()) {

                Toast.makeText(
                        CartActivity.this,
                        "Please select an item to order 🛒",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }


            // =========================================
            // OPEN PAYMENT ACTIVITY
            // =========================================

            Intent intent =
                    new Intent(
                            CartActivity.this,
                            PaymentActivity.class
                    );


            intent.putStringArrayListExtra(
                    "foodNames",
                    selectedNames
            );


            intent.putStringArrayListExtra(
                    "foodSizes",
                    selectedSizes
            );


            intent.putIntegerArrayListExtra(
                    "foodPrices",
                    selectedPrices
            );


            intent.putIntegerArrayListExtra(
                    "quantities",
                    selectedQuantities
            );


            startActivity(intent);

        });


        // ==============================
        // HOME
        // ==============================

        homeNav.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            CartActivity.this,
                            MenuActivity.class
                    );

            startActivity(intent);

            finish();

        });


        // ==============================
        // WISHLIST
        // ==============================

        wishlistNav.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            CartActivity.this,
                            WishlistActivity.class
                    );

            startActivity(intent);

        });


        // ==============================
        // CART
        // ==============================

        cartNav.setOnClickListener(v -> {

            Toast.makeText(
                    CartActivity.this,
                    "You are already in Cart 🛒",
                    Toast.LENGTH_SHORT
            ).show();

        });

    }


    // =========================================================
    // LOAD CART ITEMS
    // =========================================================

    private void loadCartItems() {

        foodNames.clear();

        foodSizes.clear();

        foodPrices.clear();

        quantities.clear();

        images.clear();

        selectedItems.clear();


        // ==============================
        // USER
        // ==============================

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


        if (userEmail.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // ==============================
        // DATABASE
        // ==============================

        SQLiteDatabase db =
                dbHelper.getReadableDatabase();


        Cursor cursor =
                db.rawQuery(
                        "SELECT food_name, food_size, quantity, price " +
                                "FROM cart WHERE user_email=?",
                        new String[]{
                                userEmail
                        }
                );


        while (cursor.moveToNext()) {

            String name =
                    cursor.getString(0);

            String size =
                    cursor.getString(1);

            int quantity =
                    cursor.getInt(2);

            int price =
                    cursor.getInt(3);


            foodNames.add(name);

            foodSizes.add(size);

            foodPrices.add(price);

            quantities.add(quantity);


            // Initially not selected

            selectedItems.add(false);


            // ==========================
            // IMAGE
            // ==========================

            images.add(
                    getFoodImage(name)
            );

        }


        cursor.close();


        // ==============================
        // ADAPTER
        // ==============================

        adapter =
                new CartAdapter(
                        this,
                        foodNames,
                        foodSizes,
                        foodPrices,
                        quantities,
                        images,
                        selectedItems,
                        this::calculateTotal
                );


        cartListView.setAdapter(adapter);


        calculateTotal();

    }


    // =========================================================
    // FOOD IMAGE
    // =========================================================

    private int getFoodImage(String foodName) {

        String imageName =
                foodName
                        .toLowerCase()
                        .replace(" ", "")
                        .replace("-", "");


        int resourceId =
                getResources().getIdentifier(
                        imageName,
                        "drawable",
                        getPackageName()
                );


        if (resourceId == 0) {

            resourceId =
                    R.drawable.burger;
        }


        return resourceId;

    }


    // =========================================================
    // SELECTED TOTAL
    // =========================================================

    private void calculateTotal() {

        int total = 0;


        for (int i = 0; i < foodPrices.size(); i++) {

            if (selectedItems.get(i)) {

                total +=
                        foodPrices.get(i)
                                * quantities.get(i);
            }

        }


        totalBill.setText(
                "₹" + total
        );

    }


    // =========================================================
    // REFRESH
    // =========================================================

    @Override
    protected void onResume() {

        super.onResume();


        if (dbHelper != null) {

            loadCartItems();

        }

    }

}