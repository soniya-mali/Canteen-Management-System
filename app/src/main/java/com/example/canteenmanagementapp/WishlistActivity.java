package com.example.canteenmanagementapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WishlistActivity extends AppCompatActivity {

    RecyclerView wishlistRecycler;

    TextView emptyWishlistText;
    TextView wishlistBadgeCount;
    TextView cartBadgeCount;

    LinearLayout homeNav;
    LinearLayout wishlistNav;
    LinearLayout cartNav;

    DatabaseHelper dbHelper;

    ArrayList<String> foodNames =
            new ArrayList<>();

    ArrayList<Integer> foodPrices =
            new ArrayList<>();

    ArrayList<Integer> foodImages =
            new ArrayList<>();

    WishlistAdapter adapter;


    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(
                savedInstanceState
        );

        setContentView(
                R.layout.activity_wishlist
        );


        // ==============================
        // DATABASE
        // ==============================

        dbHelper =
                new DatabaseHelper(this);


        // ==============================
        // FIND VIEWS
        // ==============================

        wishlistRecycler =
                findViewById(
                        R.id.wishlistRecycler
                );


        emptyWishlistText =
                findViewById(
                        R.id.emptyWishlistText
                );


        wishlistBadgeCount =
                findViewById(
                        R.id.wishlistBadgeCount
                );


        cartBadgeCount =
                findViewById(
                        R.id.cartBadgeCount
                );


        homeNav =
                findViewById(
                        R.id.homeNav
                );


        wishlistNav =
                findViewById(
                        R.id.wishlistNav
                );


        cartNav =
                findViewById(
                        R.id.cartNav
                );


        // ==============================
        // RECYCLER VIEW
        // ==============================

        wishlistRecycler.setLayoutManager(
                new LinearLayoutManager(this)
        );


        // ==============================
        // LOAD WISHLIST
        // ==============================

        loadWishlistItems();


        // ==============================
        // HOME
        // ==============================

        homeNav.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            WishlistActivity.this,
                            MenuActivity.class
                    );

            startActivity(intent);

            finish();

        });


        // ==============================
        // WISHLIST
        // ==============================

        wishlistNav.setOnClickListener(v -> {

            Toast.makeText(
                    WishlistActivity.this,
                    "You are already in Wishlist ❤️",
                    Toast.LENGTH_SHORT
            ).show();

        });


        // ==============================
        // CART
        // ==============================

        cartNav.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            WishlistActivity.this,
                            CartActivity.class
                    );

            startActivity(intent);

        });

    }


    // =========================================================
    // LOAD WISHLIST
    // =========================================================

    private void loadWishlistItems() {

        foodNames.clear();

        foodPrices.clear();

        foodImages.clear();


        // ==============================
        // GET LOGGED USER
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

            showEmptyWishlist();

            updateBadges();

            return;
        }


        // ==============================
        // DATABASE
        // ==============================

        SQLiteDatabase db =
                dbHelper.getReadableDatabase();


        Cursor cursor = null;


        try {

            cursor =
                    db.rawQuery(
                            "SELECT food_name, food_price " +
                                    "FROM wishlist " +
                                    "WHERE user_email=?",
                            new String[]{
                                    userEmail
                            }
                    );


            while (
                    cursor.moveToNext()
            ) {

                String foodName =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "food_name"
                                )
                        );

                int foodPrice =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "food_price"
                                )
                        );


                foodNames.add(
                        foodName
                );


                foodPrices.add(
                        foodPrice
                );


                foodImages.add(
                        getFoodImage(
                                foodName
                        )
                );

            }

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Wishlist database error: " +
                            e.getMessage(),
                    Toast.LENGTH_SHORT
            ).show();

        } finally {

            if (cursor != null) {

                cursor.close();

            }

        }


        // ==============================
        // SHOW EMPTY OR LIST
        // ==============================

        if (foodNames.isEmpty()) {

            showEmptyWishlist();

        } else {

            wishlistRecycler.setVisibility(
                    View.VISIBLE
            );

            emptyWishlistText.setVisibility(
                    View.GONE
            );

        }


        // ==============================
        // ADAPTER
        // ==============================

        adapter =
                new WishlistAdapter(
                        this,
                        foodNames,
                        foodPrices,
                        foodImages,
                        () -> {

                            if (
                                    foodNames.isEmpty()
                            ) {

                                showEmptyWishlist();

                            }

                            updateBadges();

                        }
                );


        wishlistRecycler.setAdapter(
                adapter
        );


        // ==============================
        // UPDATE BADGES
        // ==============================

        updateBadges();

    }


    // =========================================================
    // SHOW EMPTY WISHLIST
    // =========================================================

    private void showEmptyWishlist() {

        wishlistRecycler.setVisibility(
                View.GONE
        );


        emptyWishlistText.setVisibility(
                View.VISIBLE
        );

    }


    // =========================================================
    // FOOD IMAGE
    // =========================================================

    private int getFoodImage(
            String foodName
    ) {

        if (foodName == null ||
                foodName.trim().isEmpty()) {

            return R.drawable.burger;
        }


        String imageName =
                foodName
                        .toLowerCase()
                        .replace(
                                " ",
                                ""
                        )
                        .replace(
                                "-",
                                ""
                        );


        int resourceId =
                getResources()
                        .getIdentifier(
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
    // UPDATE BADGES
    // =========================================================

    private void updateBadges() {

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

            wishlistBadgeCount.setVisibility(
                    View.GONE
            );

            cartBadgeCount.setVisibility(
                    View.GONE
            );

            return;
        }


        SQLiteDatabase db =
                dbHelper.getReadableDatabase();


        // ==============================
        // WISHLIST COUNT
        // ==============================

        int wishlistCount = 0;


        Cursor wishlistCursor = null;


        try {

            wishlistCursor =
                    db.rawQuery(
                            "SELECT COUNT(*) " +
                                    "FROM wishlist " +
                                    "WHERE user_email=?",
                            new String[]{
                                    userEmail
                            }
                    );


            if (
                    wishlistCursor.moveToFirst()
            ) {

                wishlistCount =
                        wishlistCursor.getInt(0);
            }

        } catch (Exception e) {

            wishlistCount = 0;

        } finally {

            if (
                    wishlistCursor != null
            ) {

                wishlistCursor.close();

            }

        }


        if (wishlistCount > 0) {

            wishlistBadgeCount.setText(
                    String.valueOf(
                            wishlistCount
                    )
            );


            wishlistBadgeCount.setVisibility(
                    View.VISIBLE
            );

        } else {

            wishlistBadgeCount.setVisibility(
                    View.GONE
            );

        }


        // ==============================
        // CART COUNT
        // ==============================

        int cartCount = 0;


        Cursor cartCursor = null;


        try {

            cartCursor =
                    db.rawQuery(
                            "SELECT COUNT(*) " +
                                    "FROM cart " +
                                    "WHERE user_email=?",
                            new String[]{
                                    userEmail
                            }
                    );


            if (
                    cartCursor.moveToFirst()
            ) {

                cartCount =
                        cartCursor.getInt(0);
            }

        } catch (Exception e) {

            cartCount = 0;

        } finally {

            if (
                    cartCursor != null
            ) {

                cartCursor.close();

            }

        }


        if (cartCount > 0) {

            cartBadgeCount.setText(
                    String.valueOf(
                            cartCount
                    )
            );


            cartBadgeCount.setVisibility(
                    View.VISIBLE
            );

        } else {

            cartBadgeCount.setVisibility(
                    View.GONE
            );

        }

    }


    // =========================================================
    // REFRESH
    // =========================================================

    @Override
    protected void onResume() {

        super.onResume();

        loadWishlistItems();

    }

}