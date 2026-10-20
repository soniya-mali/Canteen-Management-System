package com.example.canteenmanagementapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WishlistAdapter
        extends RecyclerView.Adapter<WishlistAdapter.ViewHolder> {

    private Context context;

    private ArrayList<String> foodNames;
    private ArrayList<Integer> foodPrices;
    private ArrayList<Integer> foodImages;

    private Runnable onWishlistChanged;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public WishlistAdapter(
            Context context,
            ArrayList<String> foodNames,
            ArrayList<Integer> foodPrices,
            ArrayList<Integer> foodImages,
            Runnable onWishlistChanged
    ) {

        this.context = context;

        this.foodNames = foodNames;

        this.foodPrices = foodPrices;

        this.foodImages = foodImages;

        this.onWishlistChanged =
                onWishlistChanged;
    }


    // =========================================================
    // CREATE VIEW HOLDER
    // =========================================================

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater.from(context)
                        .inflate(
                                R.layout.wishlist_item,
                                parent,
                                false
                        );

        return new ViewHolder(view);
    }


    // =========================================================
    // BIND DATA
    // =========================================================

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        // ==============================
        // FOOD NAME
        // ==============================

        holder.wishlistFoodName.setText(
                foodNames.get(position)
        );


        // ==============================
        // FOOD PRICE
        // ==============================

        holder.wishlistFoodPrice.setText(
                "₹" + foodPrices.get(position)
        );


        // ==============================
        // FOOD IMAGE
        // ==============================

        holder.wishlistFoodImage.setImageResource(
                foodImages.get(position)
        );


        // ==============================
        // REMOVE FROM WISHLIST
        // ==============================

        holder.removeWishlistBtn.setOnClickListener(v -> {

            int currentPosition =
                    holder.getBindingAdapterPosition();


            if (currentPosition ==
                    RecyclerView.NO_POSITION) {

                return;
            }


            // --------------------------
            // DELETE FROM DATABASE
            // --------------------------

            deleteFromWishlist(
                    currentPosition
            );


            // --------------------------
            // REMOVE FROM LIST
            // --------------------------

            foodNames.remove(
                    currentPosition
            );


            foodPrices.remove(
                    currentPosition
            );


            foodImages.remove(
                    currentPosition
            );


            // --------------------------
            // UPDATE RECYCLER VIEW
            // --------------------------

            notifyItemRemoved(
                    currentPosition
            );


            // --------------------------
            // UPDATE WISHLIST BADGE
            // --------------------------

            if (onWishlistChanged != null) {

                onWishlistChanged.run();

            }

        });

    }


    // =========================================================
    // DELETE FROM SQLITE
    // =========================================================

    private void deleteFromWishlist(
            int position
    ) {

        // ==============================
        // GET LOGGED USER
        // ==============================

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

            return;
        }


        // ==============================
        // GET FOOD NAME
        // ==============================

        String foodName =
                foodNames.get(position);


        // ==============================
        // OPEN DATABASE
        // ==============================

        SQLiteDatabase db =
                new DatabaseHelper(context)
                        .getWritableDatabase();


        // ==============================
        // DELETE ITEM
        // ==============================

        db.delete(
                "wishlist",
                "user_email=? AND food_name=?",
                new String[]{
                        userEmail,
                        foodName
                }
        );

    }


    // =========================================================
    // ITEM COUNT
    // =========================================================

    @Override
    public int getItemCount() {

        return foodNames.size();

    }


    // =========================================================
    // VIEW HOLDER
    // =========================================================

    public static class ViewHolder
            extends RecyclerView.ViewHolder {


        ImageView wishlistFoodImage;

        TextView wishlistFoodName;

        TextView wishlistFoodPrice;

        Button removeWishlistBtn;


        public ViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);


            // ==============================
            // FOOD IMAGE
            // ==============================

            wishlistFoodImage =
                    itemView.findViewById(
                            R.id.wishlistFoodImage
                    );


            // ==============================
            // FOOD NAME
            // ==============================

            wishlistFoodName =
                    itemView.findViewById(
                            R.id.wishlistFoodName
                    );


            // ==============================
            // FOOD PRICE
            // ==============================

            wishlistFoodPrice =
                    itemView.findViewById(
                            R.id.wishlistFoodPrice
                    );


            // ==============================
            // REMOVE BUTTON
            // ==============================

            removeWishlistBtn =
                    itemView.findViewById(
                            R.id.removeWishlistBtn
                    );

        }

    }

}