package com.example.canteenmanagementapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MenuAdapter extends BaseAdapter implements Filterable {

    // =====================================================
    // CONTEXT
    // =====================================================

    Context context;


    // =====================================================
    // CURRENT DISPLAY DATA
    // =====================================================

    ArrayList<String> names;
    ArrayList<Integer> prices;
    ArrayList<Integer> images;


    // =====================================================
    // ORIGINAL DATA FOR SEARCH
    // =====================================================

    ArrayList<String> namesFull;
    ArrayList<Integer> pricesFull;
    ArrayList<Integer> imagesFull;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public MenuAdapter(
            Context context,
            ArrayList<String> names,
            ArrayList<Integer> prices,
            ArrayList<Integer> images
    ) {

        this.context = context;

        // Current data
        this.names = new ArrayList<>(names);
        this.prices = new ArrayList<>(prices);
        this.images = new ArrayList<>(images);

        // Full data for search
        this.namesFull = new ArrayList<>(names);
        this.pricesFull = new ArrayList<>(prices);
        this.imagesFull = new ArrayList<>(images);

    }


    // =====================================================
    // GET COUNT
    // =====================================================

    @Override
    public int getCount() {

        return names.size();

    }


    // =====================================================
    // GET ITEM
    // =====================================================

    @Override
    public Object getItem(int position) {

        return names.get(position);

    }


    // =====================================================
    // GET ITEM ID
    // =====================================================

    @Override
    public long getItemId(int position) {

        return position;

    }


    // =====================================================
    // GET VIEW
    // =====================================================

    @Override
    public View getView(
            int position,
            View convertView,
            ViewGroup parent
    ) {

        // =================================================
        // CREATE ITEM VIEW
        // =================================================

        if (convertView == null) {

            convertView =
                    LayoutInflater
                            .from(context)
                            .inflate(
                                    R.layout.menu_item,
                                    parent,
                                    false
                            );
        }


        // =================================================
        // FIND VIEWS
        // =================================================

        ImageView image =
                convertView.findViewById(
                        R.id.itemImage
                );

        TextView name =
                convertView.findViewById(
                        R.id.itemName
                );

        TextView price =
                convertView.findViewById(
                        R.id.itemPrice
                );

        TextView wishlist =
                convertView.findViewById(
                        R.id.heartIcon
                );


        // =================================================
        // CURRENT FOOD DATA
        // =================================================

        String foodName =
                names.get(position);

        int foodPrice =
                prices.get(position);

        int foodImage =
                images.get(position);


        // =================================================
        // SET FOOD DATA
        // =================================================

        image.setImageResource(
                foodImage
        );

        name.setText(
                foodName
        );

        price.setText(
                "₹" + foodPrice
        );


        // =================================================
        // CHECK WISHLIST STATUS
        // =================================================

        boolean isInWishlist =
                isItemInWishlist(
                        foodName
                );


        updateHeart(
                wishlist,
                isInWishlist
        );


        // =================================================
        // HEART CLICK
        // =================================================

        wishlist.setOnClickListener(v -> {

            // ---------------------------------------------
            // Get latest wishlist status from database
            // ---------------------------------------------

            boolean currentlyInWishlist =
                    isItemInWishlist(
                            foodName
                    );


            // =================================================
            // REMOVE FROM WISHLIST
            // =================================================

            if (currentlyInWishlist) {

                boolean removed =
                        removeFromWishlist(
                                foodName
                        );


                if (removed) {

                    updateHeart(
                            wishlist,
                            false
                    );

                    wishlist.invalidate();

                }

            }


            // =================================================
            // ADD TO WISHLIST
            // =================================================

            else {

                boolean added =
                        addToWishlist(
                                foodName,
                                foodPrice
                        );


                if (added) {

                    updateHeart(
                            wishlist,
                            true
                    );

                    wishlist.invalidate();

                }

            }

        });


        // =================================================
        // FOOD CARD CLICK
        // =================================================

        convertView.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            context,
                            FoodDetailsActivity.class
                    );


            // ---------------------------------------------
            // SEND FOOD NAME
            // ---------------------------------------------

            intent.putExtra(
                    "foodName",
                    foodName
            );


            // ---------------------------------------------
            // SEND FOOD PRICE
            // ---------------------------------------------

            intent.putExtra(
                    "foodPrice",
                    foodPrice
            );


            // ---------------------------------------------
            // SEND FOOD IMAGE
            // ---------------------------------------------

            intent.putExtra(
                    "foodImage",
                    foodImage
            );


            // ---------------------------------------------
            // OPEN FOOD DETAILS
            // ---------------------------------------------

            context.startActivity(
                    intent
            );

        });


        // =================================================
        // RETURN ITEM VIEW
        // =================================================

        return convertView;

    }


    // =====================================================
    // ENSURE WISHLIST TABLE
    // =====================================================

    private void ensureWishlistTable(
            SQLiteDatabase db
    ) {

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS wishlist (" +
                        "wishlist_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "user_email TEXT, " +
                        "food_name TEXT, " +
                        "food_price INTEGER" +
                        ")"
        );

    }


    // =====================================================
    // CHECK ITEM IN WISHLIST
    // =====================================================

    private boolean isItemInWishlist(
            String foodName
    ) {

        // =================================================
        // GET LOGGED USER
        // =================================================

        SharedPreferences sp =
                context.getSharedPreferences(
                        "user",
                        Context.MODE_PRIVATE
                );


        String userEmail =
                sp.getString(
                        "email",
                        ""
                );


        if (userEmail.isEmpty()) {

            return false;

        }


        // =================================================
        // OPEN DATABASE
        // =================================================

        SQLiteDatabase db =
                new DatabaseHelper(context)
                        .getReadableDatabase();


        Cursor cursor = null;


        try {

            // Make sure table exists
            ensureWishlistTable(db);


            cursor =
                    db.rawQuery(
                            "SELECT COUNT(*) " +
                                    "FROM wishlist " +
                                    "WHERE user_email=? " +
                                    "AND food_name=?",
                            new String[]{
                                    userEmail,
                                    foodName
                            }
                    );


            if (cursor.moveToFirst()) {

                return cursor.getInt(0) > 0;

            }

        } catch (Exception e) {

            Toast.makeText(
                    context,
                    "Wishlist check error: " +
                            e.getMessage(),
                    Toast.LENGTH_SHORT
            ).show();

        } finally {

            if (cursor != null) {

                cursor.close();

            }

        }


        return false;

    }


    // =====================================================
    // ADD TO WISHLIST
    // =====================================================

    private boolean addToWishlist(
            String foodName,
            int foodPrice
    ) {

        // =================================================
        // GET LOGGED USER
        // =================================================

        SharedPreferences sp =
                context.getSharedPreferences(
                        "user",
                        Context.MODE_PRIVATE
                );


        String userEmail =
                sp.getString(
                        "email",
                        ""
                );


        // =================================================
        // CHECK LOGIN
        // =================================================

        if (userEmail.isEmpty()) {

            Toast.makeText(
                    context,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            return false;

        }


        // =================================================
        // OPEN DATABASE
        // =================================================

        SQLiteDatabase db =
                new DatabaseHelper(context)
                        .getWritableDatabase();


        try {

            // =================================================
            // MAKE SURE TABLE EXISTS
            // =================================================

            ensureWishlistTable(db);


            // =================================================
            // CHECK DUPLICATE
            // =================================================

            Cursor cursor = null;

            try {

                cursor =
                        db.rawQuery(
                                "SELECT COUNT(*) " +
                                        "FROM wishlist " +
                                        "WHERE user_email=? " +
                                        "AND food_name=?",
                                new String[]{
                                        userEmail,
                                        foodName
                                }
                        );


                if (
                        cursor.moveToFirst() &&
                                cursor.getInt(0) > 0
                ) {

                    return true;

                }

            } finally {

                if (cursor != null) {

                    cursor.close();

                }

            }


            // =================================================
            // CREATE CONTENT VALUES
            // =================================================

            ContentValues values =
                    new ContentValues();


            values.put(
                    "user_email",
                    userEmail
            );


            values.put(
                    "food_name",
                    foodName
            );


            values.put(
                    "food_price",
                    foodPrice
            );


            // =================================================
            // INSERT
            // =================================================

            long result =
                    db.insert(
                            "wishlist",
                            null,
                            values
                    );


            // =================================================
            // SUCCESS
            // =================================================

            if (result != -1) {

                Toast.makeText(
                        context,
                        foodName +
                                " added to Wishlist ❤️",
                        Toast.LENGTH_SHORT
                ).show();

                return true;

            }


            // =================================================
            // INSERT FAILED
            // =================================================

            Toast.makeText(
                    context,
                    "Unable to add Wishlist item",
                    Toast.LENGTH_SHORT
            ).show();

            return false;

        } catch (Exception e) {

            // =================================================
            // SHOW ACTUAL DATABASE ERROR
            // =================================================

            Toast.makeText(
                    context,
                    "Wishlist add error: " +
                            e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();

            return false;

        }

    }


    // =====================================================
    // REMOVE FROM WISHLIST
    // =====================================================

    private boolean removeFromWishlist(
            String foodName
    ) {

        // =================================================
        // GET LOGGED USER
        // =================================================

        SharedPreferences sp =
                context.getSharedPreferences(
                        "user",
                        Context.MODE_PRIVATE
                );


        String userEmail =
                sp.getString(
                        "email",
                        ""
                );


        if (userEmail.isEmpty()) {

            Toast.makeText(
                    context,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            return false;

        }


        // =================================================
        // OPEN DATABASE
        // =================================================

        SQLiteDatabase db =
                new DatabaseHelper(context)
                        .getWritableDatabase();


        try {

            // Make sure table exists
            ensureWishlistTable(db);


            // =================================================
            // DELETE ITEM
            // =================================================

            int deletedRows =
                    db.delete(
                            "wishlist",
                            "user_email=? AND food_name=?",
                            new String[]{
                                    userEmail,
                                    foodName
                            }
                    );


            // =================================================
            // DELETE SUCCESS
            // =================================================

            if (deletedRows > 0) {

                Toast.makeText(
                        context,
                        foodName +
                                " removed from Wishlist",
                        Toast.LENGTH_SHORT
                ).show();

                return true;

            }


            // =================================================
            // NOTHING DELETED
            // =================================================

            Toast.makeText(
                    context,
                    "Item was not found in Wishlist",
                    Toast.LENGTH_SHORT
            ).show();

            return false;

        } catch (Exception e) {

            Toast.makeText(
                    context,
                    "Wishlist remove error: " +
                            e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();

            return false;

        }

    }


    // =====================================================
    // UPDATE HEART
    // =====================================================

    private void updateHeart(
            TextView wishlist,
            boolean isInWishlist
    ) {

        if (wishlist == null) {

            return;

        }


        if (isInWishlist) {

            // =================================================
            // FAVORITE
            // RED FILLED HEART
            // =================================================

            wishlist.setText(
                    "♥"
            );

            wishlist.setTextColor(
                    Color.RED
            );

        } else {

            // =================================================
            // NOT FAVORITE
            // BLACK EMPTY HEART
            // =================================================

            wishlist.setText(
                    "♡"
            );

            wishlist.setTextColor(
                    Color.BLACK
            );

        }


        wishlist.invalidate();

    }


    // =====================================================
    // GET FILTER
    // =====================================================

    @Override
    public Filter getFilter() {

        return menuFilter;

    }


    // =====================================================
    // SEARCH FILTER
    // =====================================================

    private final Filter menuFilter =
            new Filter() {

                @Override
                protected FilterResults performFiltering(
                        CharSequence constraint
                ) {

                    ArrayList<String> filteredNames =
                            new ArrayList<>();

                    ArrayList<Integer> filteredPrices =
                            new ArrayList<>();

                    ArrayList<Integer> filteredImages =
                            new ArrayList<>();


                    // =====================================
                    // EMPTY SEARCH
                    // =====================================

                    if (
                            constraint == null ||
                                    constraint.length() == 0
                    ) {

                        filteredNames.addAll(
                                namesFull
                        );

                        filteredPrices.addAll(
                                pricesFull
                        );

                        filteredImages.addAll(
                                imagesFull
                        );

                    }


                    // =====================================
                    // SEARCH
                    // =====================================

                    else {

                        String pattern =
                                constraint
                                        .toString()
                                        .toLowerCase(
                                                Locale.getDefault()
                                        )
                                        .trim();


                        for (
                                int i = 0;
                                i < namesFull.size();
                                i++
                        ) {

                            String currentName =
                                    namesFull
                                            .get(i)
                                            .toLowerCase(
                                                    Locale.getDefault()
                                            );


                            if (
                                    currentName.contains(
                                            pattern
                                    )
                            ) {

                                filteredNames.add(
                                        namesFull.get(i)
                                );

                                filteredPrices.add(
                                        pricesFull.get(i)
                                );

                                filteredImages.add(
                                        imagesFull.get(i)
                                );

                            }

                        }

                    }


                    // =====================================
                    // CREATE RESULTS
                    // =====================================

                    FilterResults results =
                            new FilterResults();


                    ArrayList<Object> data =
                            new ArrayList<>();


                    data.add(
                            filteredNames
                    );

                    data.add(
                            filteredPrices
                    );

                    data.add(
                            filteredImages
                    );


                    results.values =
                            data;

                    results.count =
                            filteredNames.size();


                    return results;

                }


                // =================================================
                // PUBLISH RESULTS
                // =================================================

                @Override
                @SuppressWarnings("unchecked")
                protected void publishResults(
                        CharSequence constraint,
                        FilterResults results
                ) {

                    if (
                            results == null ||
                                    results.values == null
                    ) {

                        return;

                    }


                    ArrayList<Object> data =
                            (ArrayList<Object>)
                                    results.values;


                    ArrayList<String> filteredNames =
                            (ArrayList<String>)
                                    data.get(0);


                    ArrayList<Integer> filteredPrices =
                            (ArrayList<Integer>)
                                    data.get(1);


                    ArrayList<Integer> filteredImages =
                            (ArrayList<Integer>)
                                    data.get(2);


                    // =====================================
                    // CLEAR CURRENT DATA
                    // =====================================

                    names.clear();

                    prices.clear();

                    images.clear();


                    // =====================================
                    // ADD FILTERED DATA
                    // =====================================

                    names.addAll(
                            filteredNames
                    );

                    prices.addAll(
                            filteredPrices
                    );

                    images.addAll(
                            filteredImages
                    );


                    // =====================================
                    // REFRESH
                    // =====================================

                    notifyDataSetChanged();

                }

            };


    // =====================================================
    // UPDATE DATA
    // CATEGORY CHANGE
    // =====================================================

    public void updateData(
            ArrayList<String> newNames,
            ArrayList<Integer> newPrices,
            ArrayList<Integer> newImages
    ) {

        // =================================================
        // SAFE COPIES
        // =================================================

        ArrayList<String> tempNames =
                new ArrayList<>(
                        newNames
                );

        ArrayList<Integer> tempPrices =
                new ArrayList<>(
                        newPrices
                );

        ArrayList<Integer> tempImages =
                new ArrayList<>(
                        newImages
                );


        // =================================================
        // UPDATE CURRENT DATA
        // =================================================

        names.clear();

        prices.clear();

        images.clear();


        names.addAll(
                tempNames
        );

        prices.addAll(
                tempPrices
        );

        images.addAll(
                tempImages
        );


        // =================================================
        // UPDATE FULL SEARCH DATA
        // =================================================

        namesFull.clear();

        pricesFull.clear();

        imagesFull.clear();


        namesFull.addAll(
                tempNames
        );

        pricesFull.addAll(
                tempPrices
        );

        imagesFull.addAll(
                tempImages
        );


        // =================================================
        // REFRESH
        // =================================================

        notifyDataSetChanged();

    }


    // =====================================================
    // GET FOOD NAME
    // =====================================================

    public String getFoodName(
            int position
    ) {

        return names.get(
                position
        );

    }


    // =====================================================
    // GET FOOD PRICE
    // =====================================================

    public int getFoodPrice(
            int position
    ) {

        return prices.get(
                position
        );

    }


    // =====================================================
    // GET FOOD IMAGE
    // =====================================================

    public int getFoodImage(
            int position
    ) {

        return images.get(
                position
        );

    }

}