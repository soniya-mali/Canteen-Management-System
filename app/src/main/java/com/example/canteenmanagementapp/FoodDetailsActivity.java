package com.example.canteenmanagementapp;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FoodDetailsActivity extends AppCompatActivity {

    // =====================================================
    // VIEWS
    // =====================================================

    ImageView foodImage;
    ImageButton wishlistBtn;

    TextView foodName;
    TextView foodPrice;
    TextView quantityText;
    TextView totalPrice;

    RadioGroup sizeGroup;
    RadioButton smallSize;
    RadioButton mediumSize;
    RadioButton largeSize;

    Button minusBtn;
    Button plusBtn;
    Button addCartBtn;
    Button orderBtn;


    // =====================================================
    // DATABASE
    // =====================================================

    DatabaseHelper dbHelper;


    // =====================================================
    // FOOD DATA
    // =====================================================

    String selectedFoodName = "";
    int selectedFoodPrice = 0;
    int selectedFoodImage = 0;

    int quantity = 1;

    String selectedSize = "Medium";


    // =====================================================
    // WISHLIST
    // =====================================================

    SharedPreferences wishlistPreferences;


    // =====================================================
    // ON CREATE
    // =====================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_food_details);


        // =================================================
        // DATABASE
        // =================================================

        dbHelper = new DatabaseHelper(this);


        // =================================================
        // WISHLIST STORAGE
        // =================================================

        wishlistPreferences =
                getSharedPreferences(
                        "wishlist",
                        MODE_PRIVATE
                );


        // =================================================
        // FIND VIEWS
        // =================================================

        foodImage =
                findViewById(R.id.foodImage);

        wishlistBtn =
                findViewById(R.id.wishlistBtn);

        foodName =
                findViewById(R.id.foodName);

        foodPrice =
                findViewById(R.id.foodPrice);

        sizeGroup =
                findViewById(R.id.sizeGroup);

        smallSize =
                findViewById(R.id.smallSize);

        mediumSize =
                findViewById(R.id.mediumSize);

        largeSize =
                findViewById(R.id.largeSize);

        minusBtn =
                findViewById(R.id.minusBtn);

        plusBtn =
                findViewById(R.id.plusBtn);

        quantityText =
                findViewById(R.id.quantityText);

        totalPrice =
                findViewById(R.id.totalPrice);

        addCartBtn =
                findViewById(R.id.addCartBtn);

        orderBtn =
                findViewById(R.id.orderBtn);


        // =================================================
        // GET DATA FROM MENU ACTIVITY
        // =================================================

        Intent intent = getIntent();

        if (intent != null) {

            selectedFoodName =
                    intent.getStringExtra("foodName");

            selectedFoodPrice =
                    intent.getIntExtra(
                            "foodPrice",
                            0
                    );

            selectedFoodImage =
                    intent.getIntExtra(
                            "foodImage",
                            0
                    );

        }


        // =================================================
        // SAFETY
        // =================================================

        if (selectedFoodName == null ||
                selectedFoodName.trim().isEmpty()) {

            selectedFoodName = "Food Item";

        }


        // =================================================
        // SET FOOD NAME
        // =================================================

        foodName.setText(
                selectedFoodName
        );


        // =================================================
        // SET FOOD PRICE
        // =================================================

        foodPrice.setText(
                "₹" + selectedFoodPrice
        );


        // =================================================
        // SET FOOD IMAGE
        // =================================================

        if (selectedFoodImage != 0) {

            foodImage.setImageResource(
                    selectedFoodImage
            );

        }


        // =================================================
        // INITIAL QUANTITY
        // =================================================

        quantity = 1;

        quantityText.setText(
                String.valueOf(quantity)
        );


        // =================================================
        // INITIAL SIZE
        // =================================================

        mediumSize.setChecked(true);

        selectedSize = "Medium";


        // =================================================
        // UPDATE TOTAL
        // =================================================

        updateTotalPrice();


        // =================================================
        // INITIAL WISHLIST HEART
        // =================================================

        updateWishlistIcon();


        // =================================================
        // WISHLIST BUTTON
        // =================================================

        wishlistBtn.setOnClickListener(v -> {

            toggleWishlist();

        });


        // =================================================
        // SMALL SIZE
        // =================================================

        smallSize.setOnClickListener(v -> {

            selectedSize = "Small";

        });


        // =================================================
        // MEDIUM SIZE
        // =================================================

        mediumSize.setOnClickListener(v -> {

            selectedSize = "Medium";

        });


        // =================================================
        // LARGE SIZE
        // =================================================

        largeSize.setOnClickListener(v -> {

            selectedSize = "Large";

        });


        // =================================================
        // SIZE GROUP
        // =================================================

        sizeGroup.setOnCheckedChangeListener(
                (group, checkedId) -> {

                    if (checkedId == R.id.smallSize) {

                        selectedSize = "Small";

                    } else if (checkedId == R.id.mediumSize) {

                        selectedSize = "Medium";

                    } else if (checkedId == R.id.largeSize) {

                        selectedSize = "Large";

                    }

                }
        );


        // =================================================
        // MINUS BUTTON
        // =================================================

        minusBtn.setOnClickListener(v -> {

            if (quantity > 1) {

                quantity--;

                quantityText.setText(
                        String.valueOf(quantity)
                );

                updateTotalPrice();

            } else {

                Toast.makeText(
                        FoodDetailsActivity.this,
                        "Minimum quantity is 1",
                        Toast.LENGTH_SHORT
                ).show();

            }

        });


        // =================================================
        // PLUS BUTTON
        // =================================================

        plusBtn.setOnClickListener(v -> {

            quantity++;

            quantityText.setText(
                    String.valueOf(quantity)
            );

            updateTotalPrice();

        });


        // =================================================
        // ADD TO CART
        // =================================================

        addCartBtn.setOnClickListener(v -> {

            addToCart();

        });


        // =================================================
        // PLACE ORDER
        // =================================================

        orderBtn.setOnClickListener(v -> {

            placeOrder();

        });

    }


    // =====================================================
    // UPDATE TOTAL PRICE
    // =====================================================

    private void updateTotalPrice() {

        int total =
                selectedFoodPrice * quantity;

        totalPrice.setText(
                "₹" + total
        );

    }


    // =====================================================
    // CHECK WISHLIST
    // =====================================================

    private boolean isInWishlist() {

        return wishlistPreferences.getBoolean(
                selectedFoodName,
                false
        );

    }


    // =====================================================
    // UPDATE WISHLIST ICON
    // =====================================================

    private void updateWishlistIcon() {

        if (isInWishlist()) {

            // Filled red heart

            wishlistBtn.setImageResource(
                    android.R.drawable.btn_star_big_on
            );

            wishlistBtn.setColorFilter(
                    0xFFE53935
            );

        } else {

            // Empty heart-like icon

            wishlistBtn.setImageResource(
                    android.R.drawable.btn_star_big_off
            );

            wishlistBtn.setColorFilter(
                    0xFF000000
            );

        }

    }


    // =====================================================
    // TOGGLE WISHLIST
    // =====================================================

    private void toggleWishlist() {

        boolean currentlyInWishlist =
                isInWishlist();


        if (currentlyInWishlist) {

            // =============================================
            // REMOVE
            // =============================================

            wishlistPreferences
                    .edit()
                    .remove(selectedFoodName)
                    .apply();


            Toast.makeText(
                    FoodDetailsActivity.this,
                    "Removed from Wishlist",
                    Toast.LENGTH_SHORT
            ).show();

        } else {

            // =============================================
            // ADD
            // =============================================

            wishlistPreferences
                    .edit()
                    .putBoolean(
                            selectedFoodName,
                            true
                    )
                    .apply();


            Toast.makeText(
                    FoodDetailsActivity.this,
                    "Added to Wishlist",
                    Toast.LENGTH_SHORT
            ).show();

        }


        // Update icon

        updateWishlistIcon();

    }


    // =====================================================
    // ADD TO CART
    // =====================================================

    private void addToCart() {

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


        if (userEmail == null ||
                userEmail.trim().isEmpty()) {

            Toast.makeText(
                    FoodDetailsActivity.this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            return;

        }


        int total =
                selectedFoodPrice * quantity;


        SQLiteDatabase db =
                dbHelper.getWritableDatabase();


        ContentValues values =
                new ContentValues();


        values.put(
                "user_email",
                userEmail
        );

        values.put(
                "food_name",
                selectedFoodName
        );

        values.put(
                "food_size",
                selectedSize
        );

        values.put(
                "quantity",
                quantity
        );

        values.put(
                "price",
                selectedFoodPrice
        );

        values.put(
                "total",
                total
        );


        long result =
                db.insert(
                        "cart",
                        null,
                        values
                );


        if (result != -1) {

            Toast.makeText(
                    FoodDetailsActivity.this,
                    "Added to Cart",
                    Toast.LENGTH_SHORT
            ).show();

        } else {

            Toast.makeText(
                    FoodDetailsActivity.this,
                    "Failed to Add to Cart",
                    Toast.LENGTH_SHORT
            ).show();

        }

    }


    // =====================================================
    // PLACE ORDER
    // =====================================================

    private void placeOrder() {

        SharedPreferences sp =
                getSharedPreferences(
                        "user",
                        MODE_PRIVATE
                );


        String userName =
                sp.getString(
                        "name",
                        "Guest"
                );


        String userEmail =
                sp.getString(
                        "email",
                        ""
                );


        if (userEmail == null ||
                userEmail.trim().isEmpty()) {

            Toast.makeText(
                    FoodDetailsActivity.this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            return;

        }


        int total =
                selectedFoodPrice * quantity;


        SQLiteDatabase db =
                dbHelper.getWritableDatabase();


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
                selectedFoodName
        );

        values.put(
                "food_size",
                selectedSize
        );

        values.put(
                "quantity",
                quantity
        );

        values.put(
                "price",
                selectedFoodPrice
        );

        values.put(
                "total",
                total
        );

        values.put(
                "order_date",
                String.valueOf(
                        System.currentTimeMillis()
                )
        );


        long result =
                db.insert(
                        "orders",
                        null,
                        values
                );


        if (result != -1) {

            Toast.makeText(
                    FoodDetailsActivity.this,
                    "Order Placed Successfully",
                    Toast.LENGTH_SHORT
            ).show();


            // =============================================
            // OPEN ORDER HISTORY
            // =============================================

            Intent intent =
                    new Intent(
                            FoodDetailsActivity.this,
                            OrderHistoryActivity.class
                    );


            startActivity(intent);


            finish();

        } else {

            Toast.makeText(
                    FoodDetailsActivity.this,
                    "Order Failed",
                    Toast.LENGTH_SHORT
            ).show();

        }

    }

}