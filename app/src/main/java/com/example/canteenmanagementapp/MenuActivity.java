package com.example.canteenmanagementapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity {

    // =========================
    // Views
    // =========================

    GridView gridView;
    Button historyBtn;

    Button btnAll;
    Button btnBreakfast;
    Button btnLunch;
    Button btnDinner;
    Button btnSnacks;
    Button btnDrinks;

    EditText searchBar;

    ImageView profileImage;
    ImageView notification;

    TextView userName;

    // =========================
    // Bottom Navigation
    // =========================

    LinearLayout bottomNav;
    LinearLayout homeNav;
    LinearLayout wishlistNav;
    LinearLayout cartNav;

    // =========================
    // Database
    // =========================

    DatabaseHelper dbHelper;

    // =========================
    // Food Data
    // =========================

    ArrayList<String> names = new ArrayList<>();
    ArrayList<Integer> prices = new ArrayList<>();
    ArrayList<Integer> images = new ArrayList<>();

    MenuAdapter adapter;


    // =====================================================
    // ON CREATE
    // =====================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu);


        // =================================================
        // DATABASE
        // =================================================

        dbHelper = new DatabaseHelper(this);


        // =================================================
        // FIND VIEWS
        // =================================================

        gridView = findViewById(R.id.gridView);

        historyBtn = findViewById(R.id.historyBtn);

        btnAll = findViewById(R.id.btnAll);
        btnBreakfast = findViewById(R.id.btnBreakfast);
        btnLunch = findViewById(R.id.btnLunch);
        btnDinner = findViewById(R.id.btnDinner);
        btnSnacks = findViewById(R.id.btnSnacks);
        btnDrinks = findViewById(R.id.btnDrinks);

        searchBar = findViewById(R.id.searchBar);

        profileImage = findViewById(R.id.profileImage);

        notification = findViewById(R.id.notification);

        userName = findViewById(R.id.userName);


        // =================================================
        // BOTTOM NAVIGATION
        // =================================================

        bottomNav = findViewById(R.id.bottomNav);

        homeNav = findViewById(R.id.homeNav);

        wishlistNav = findViewById(R.id.wishlistNav);

        cartNav = findViewById(R.id.cartNav);


        // =================================================
        // USER NAME
        // =================================================

        SharedPreferences sp =
                getSharedPreferences(
                        "user",
                        MODE_PRIVATE
                );

        String username =
                sp.getString(
                        "name",
                        "Guest"
                );

        userName.setText(username);


        // =================================================
        // INSERT MENU ITEMS
        // =================================================

        insertMenuItems();


        // =================================================
        // ADAPTER
        // =================================================

        adapter =
                new MenuAdapter(
                        this,
                        names,
                        prices,
                        images
                );

        gridView.setAdapter(adapter);


        // =================================================
        // LOAD ALL FOOD
        // =================================================

        filterItems("All");


        // =================================================
        // HOME NAVIGATION
        // =================================================

        homeNav.setOnClickListener(v -> {

            Toast.makeText(
                    MenuActivity.this,
                    "Home",
                    Toast.LENGTH_SHORT
            ).show();

        });


        // =================================================
        // WISHLIST NAVIGATION
        // =================================================

        wishlistNav.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            MenuActivity.this,
                            WishlistActivity.class
                    );

            startActivity(intent);

        });


        // =================================================
        // CART NAVIGATION
        // =================================================

        cartNav.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            MenuActivity.this,
                            CartActivity.class
                    );

            startActivity(intent);

        });


        // =================================================
        // PROFILE
        // =================================================

        profileImage.setOnClickListener(v -> {

            Toast.makeText(
                    MenuActivity.this,
                    "Profile",
                    Toast.LENGTH_SHORT
            ).show();

        });


        // =================================================
        // NOTIFICATION
        // =================================================

        notification.setOnClickListener(v -> {

            Toast.makeText(
                    MenuActivity.this,
                    "No New Notifications",
                    Toast.LENGTH_SHORT
            ).show();

        });


        // =================================================
        // ALL CATEGORY
        // =================================================

        btnAll.setOnClickListener(v -> {

            searchBar.setText("");

            filterItems("All");

        });


        // =================================================
        // BREAKFAST
        // =================================================

        btnBreakfast.setOnClickListener(v -> {

            searchBar.setText("");

            filterItems("Breakfast");

        });


        // =================================================
        // LUNCH
        // =================================================

        btnLunch.setOnClickListener(v -> {

            searchBar.setText("");

            filterItems("Lunch");

        });


        // =================================================
        // DINNER
        // =================================================

        btnDinner.setOnClickListener(v -> {

            searchBar.setText("");

            filterItems("Dinner");

        });


        // =================================================
        // SNACKS
        // =================================================

        btnSnacks.setOnClickListener(v -> {

            searchBar.setText("");

            filterItems("Snacks");

        });


        // =================================================
        // DRINKS
        // =================================================

        btnDrinks.setOnClickListener(v -> {

            searchBar.setText("");

            filterItems("Drinks");

        });


        // =================================================
        // FOOD ITEM CLICK
        // =================================================

        gridView.setOnItemClickListener(
                (parent, view, position, id) -> {

                    // -----------------------------------------
                    // GET CURRENT FOOD FROM ADAPTER
                    // This works after category/search filtering
                    // -----------------------------------------

                    String selectedFoodName =
                            adapter.getFoodName(position);

                    int selectedFoodPrice =
                            adapter.getFoodPrice(position);

                    int selectedFoodImage =
                            adapter.getFoodImage(position);


                    // -----------------------------------------
                    // OPEN FOOD DETAILS
                    // -----------------------------------------

                    Intent intent =
                            new Intent(
                                    MenuActivity.this,
                                    FoodDetailsActivity.class
                            );


                    // -----------------------------------------
                    // SEND FOOD DATA
                    // -----------------------------------------

                    intent.putExtra(
                            "foodName",
                            selectedFoodName
                    );

                    intent.putExtra(
                            "foodPrice",
                            selectedFoodPrice
                    );

                    intent.putExtra(
                            "foodImage",
                            selectedFoodImage
                    );


                    // -----------------------------------------
                    // START DETAILS PAGE
                    // -----------------------------------------

                    startActivity(intent);

                }
        );


        // =================================================
        // ORDER HISTORY
        // =================================================

        historyBtn.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            MenuActivity.this,
                            OrderHistoryActivity.class
                    );

            startActivity(intent);

        });


        // =================================================
        // LIVE SEARCH
        // =================================================

        searchBar.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after) {
                    }


                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count) {

                        if (adapter != null) {

                            adapter
                                    .getFilter()
                                    .filter(
                                            s.toString()
                                    );

                        }

                    }


                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }

                }
        );

    }


    // =====================================================
    // INSERT MENU ITEMS
    // =====================================================

    private void insertMenuItems() {

        SQLiteDatabase db =
                dbHelper.getWritableDatabase();


        // जुने menu items delete करा
        db.execSQL(
                "DELETE FROM menu"
        );


        // 1. Burger
        db.execSQL(
                "INSERT INTO menu(name,price,image,category) " +
                        "VALUES('Burger',50,'burger','Lunch')"
        );


        // 2. Pizza
        db.execSQL(
                "INSERT INTO menu(name,price,image,category) " +
                        "VALUES('Pizza',120,'pizza','Dinner')"
        );


        // 3. Sandwich
        db.execSQL(
                "INSERT INTO menu(name,price,image,category) " +
                        "VALUES('Sandwich',40,'sandwich','Breakfast')"
        );


        // 4. Tea
        db.execSQL(
                "INSERT INTO menu(name,price,image,category) " +
                        "VALUES('Tea',10,'tea','Breakfast')"
        );


        // 5. Coffee
        db.execSQL(
                "INSERT INTO menu(name,price,image,category) " +
                        "VALUES('Coffee',20,'coffee','Breakfast')"
        );


        // 6. Momos
        db.execSQL(
                "INSERT INTO menu(name,price,image,category) " +
                        "VALUES('Momos',60,'momos','Snacks')"
        );


        // 7. Paneer Tikka
        db.execSQL(
                "INSERT INTO menu(name,price,image,category) " +
                        "VALUES('Paneer Tikka',90,'paneertikka','Dinner')"
        );


        // 8. Veg Biryani
        db.execSQL(
                "INSERT INTO menu(name,price,image,category) " +
                        "VALUES('Veg Biryani',100,'vegbiryani','Lunch')"
        );


        // 9. Chicken Biryani
        db.execSQL(
                "INSERT INTO menu(name,price,image,category) " +
                        "VALUES('Chicken Biryani',150,'chickenbiryani','Lunch')"
        );


        // 10. Spring Roll
        db.execSQL(
                "INSERT INTO menu(name,price,image,category) " +
                        "VALUES('Spring Roll',40,'springroll','Snacks')"
        );


        // 11. Doughnut
        db.execSQL(
                "INSERT INTO menu(name,price,image,category) " +
                        "VALUES('Doughnut',20,'doughnut','Snacks')"
        );


        // 12. Cupcake
        db.execSQL(
                "INSERT INTO menu(name,price,image,category) " +
                        "VALUES('Cupcake',25,'cupcake','Snacks')"
        );


        // 13. Milkshake
        db.execSQL(
                "INSERT INTO menu(name,price,image,category) " +
                        "VALUES('Milkshake',50,'milkshake','Drinks')"
        );


        // 14. Cold Coffee
        db.execSQL(
                "INSERT INTO menu(name,price,image,category) " +
                        "VALUES('Cold Coffee',40,'coldcoffee','Drinks')"
        );


        // 15. French Toast
        db.execSQL(
                "INSERT INTO menu(name,price,image,category) " +
                        "VALUES('French Toast',35,'frenchtoast','Breakfast')"
        );

    }


    // =====================================================
    // FILTER ITEMS
    // =====================================================

    private void filterItems(String category) {

        // -----------------------------------------------
        // CLEAR OLD DATA
        // -----------------------------------------------

        names.clear();

        prices.clear();

        images.clear();


        // -----------------------------------------------
        // DATABASE
        // -----------------------------------------------

        SQLiteDatabase db =
                dbHelper.getReadableDatabase();


        Cursor cursor;


        // -----------------------------------------------
        // ALL ITEMS
        // -----------------------------------------------

        if (category.equals("All")) {

            cursor =
                    db.rawQuery(
                            "SELECT name, price, image, category " +
                                    "FROM menu",
                            null
                    );

        }


        // -----------------------------------------------
        // CATEGORY ITEMS
        // -----------------------------------------------

        else {

            cursor =
                    db.rawQuery(
                            "SELECT name, price, image, category " +
                                    "FROM menu WHERE category=?",
                            new String[]{
                                    category
                            }
                    );

        }


        // -----------------------------------------------
        // READ DATABASE
        // -----------------------------------------------

        while (cursor.moveToNext()) {

            // -------------------------------------------
            // FOOD NAME
            // -------------------------------------------

            String foodName =
                    cursor.getString(0);

            names.add(foodName);


            // -------------------------------------------
            // FOOD PRICE
            // -------------------------------------------

            int foodPrice =
                    cursor.getInt(1);

            prices.add(foodPrice);


            // -------------------------------------------
            // IMAGE NAME
            // -------------------------------------------

            String imgName =
                    cursor.getString(2);


            // -------------------------------------------
            // FIND DRAWABLE
            // -------------------------------------------

            int imageId =
                    getResources().getIdentifier(
                            imgName.toLowerCase(),
                            "drawable",
                            getPackageName()
                    );


            // -------------------------------------------
            // FALLBACK IMAGE
            // -------------------------------------------

            if (imageId == 0) {

                imageId =
                        R.drawable.burger;

            }


            images.add(imageId);

        }


        cursor.close();


        // -----------------------------------------------
        // UPDATE ADAPTER
        // -----------------------------------------------

        if (adapter != null) {

            adapter.updateData(
                    new ArrayList<>(names),
                    new ArrayList<>(prices),
                    new ArrayList<>(images)
            );

        }

    }

}