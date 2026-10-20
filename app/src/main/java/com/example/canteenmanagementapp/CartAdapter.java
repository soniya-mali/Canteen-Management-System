package com.example.canteenmanagementapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CartAdapter
        extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    Context context;

    ArrayList<String> foodNames;
    ArrayList<String> foodSizes;
    ArrayList<Integer> foodPrices;
    ArrayList<Integer> quantities;
    ArrayList<Integer> images;

    ArrayList<Boolean> selectedItems;

    Runnable onCartChanged;


    public CartAdapter(
            Context context,
            ArrayList<String> foodNames,
            ArrayList<String> foodSizes,
            ArrayList<Integer> foodPrices,
            ArrayList<Integer> quantities,
            ArrayList<Integer> images,
            ArrayList<Boolean> selectedItems,
            Runnable onCartChanged) {

        this.context = context;

        this.foodNames = foodNames;

        this.foodSizes = foodSizes;

        this.foodPrices = foodPrices;

        this.quantities = quantities;

        this.images = images;

        this.selectedItems = selectedItems;

        this.onCartChanged = onCartChanged;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater.from(context)
                        .inflate(
                                R.layout.cart_item,
                                parent,
                                false
                        );

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {


        // ==============================
        // CHECKBOX
        // ==============================

        holder.selectItem.setOnCheckedChangeListener(null);


        holder.selectItem.setChecked(
                selectedItems.get(position)
        );


        holder.selectItem.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    int currentPosition =
                            holder.getBindingAdapterPosition();

                    if (currentPosition ==
                            RecyclerView.NO_POSITION) {

                        return;
                    }


                    selectedItems.set(
                            currentPosition,
                            isChecked
                    );


                    if (onCartChanged != null) {

                        onCartChanged.run();

                    }

                }
        );


        // ==============================
        // IMAGE
        // ==============================

        holder.cartImage.setImageResource(
                images.get(position)
        );


        // ==============================
        // FOOD NAME
        // ==============================

        holder.cartFoodName.setText(
                foodNames.get(position)
        );


        // ==============================
        // FOOD SIZE
        // ==============================

        String size =
                foodSizes.get(position);


        if (size == null ||
                size.trim().isEmpty()) {

            size = "Small";

        }


        if (size.toLowerCase()
                .startsWith("size:")) {

            size =
                    size.substring(5)
                            .trim();

        }


        if (size.toLowerCase()
                .startsWith("size ")) {

            size =
                    size.substring(5)
                            .trim();

        }


        holder.cartFoodSize.setText(
                "Size: " + size
        );


        // ==============================
        // PRICE
        // ==============================

        int price =
                foodPrices.get(position);


        holder.cartFoodPrice.setText(
                "Price: ₹" + price
        );


        // ==============================
        // QUANTITY
        // ==============================

        int quantity =
                quantities.get(position);


        holder.cartQuantity.setText(
                String.valueOf(quantity)
        );


        // ==============================
        // TOTAL
        // ==============================

        int total =
                price * quantity;


        holder.cartTotal.setText(
                "Total: ₹" + total
        );


        // ==============================
        // PLUS BUTTON
        // ==============================

        holder.btnPlus.setOnClickListener(v -> {

            int currentPosition =
                    holder.getBindingAdapterPosition();


            if (currentPosition ==
                    RecyclerView.NO_POSITION) {

                return;
            }


            int newQuantity =
                    quantities.get(
                            currentPosition
                    ) + 1;


            quantities.set(
                    currentPosition,
                    newQuantity
            );


            updateQuantityInDatabase(
                    currentPosition,
                    newQuantity
            );


            notifyItemChanged(
                    currentPosition
            );


            if (onCartChanged != null) {

                onCartChanged.run();

            }

        });


        // ==============================
        // MINUS BUTTON
        // ==============================

        holder.btnMinus.setOnClickListener(v -> {

            int currentPosition =
                    holder.getBindingAdapterPosition();


            if (currentPosition ==
                    RecyclerView.NO_POSITION) {

                return;
            }


            int currentQuantity =
                    quantities.get(
                            currentPosition
                    );


            if (currentQuantity > 1) {

                int newQuantity =
                        currentQuantity - 1;


                quantities.set(
                        currentPosition,
                        newQuantity
                );


                updateQuantityInDatabase(
                        currentPosition,
                        newQuantity
                );


                notifyItemChanged(
                        currentPosition
                );


                if (onCartChanged != null) {

                    onCartChanged.run();

                }

            }

        });


        // ==============================
        // REMOVE BUTTON
        // ==============================

        holder.btnRemove.setOnClickListener(v -> {

            int currentPosition =
                    holder.getBindingAdapterPosition();


            if (currentPosition ==
                    RecyclerView.NO_POSITION) {

                return;
            }


            deleteFromDatabase(
                    currentPosition
            );


            foodNames.remove(
                    currentPosition
            );


            foodSizes.remove(
                    currentPosition
            );


            foodPrices.remove(
                    currentPosition
            );


            quantities.remove(
                    currentPosition
            );


            images.remove(
                    currentPosition
            );


            selectedItems.remove(
                    currentPosition
            );


            notifyItemRemoved(
                    currentPosition
            );


            notifyItemRangeChanged(
                    currentPosition,
                    getItemCount() - currentPosition
            );


            if (onCartChanged != null) {

                onCartChanged.run();

            }

        });

    }


    // =========================================================
    // UPDATE QUANTITY IN DATABASE
    // =========================================================

    private void updateQuantityInDatabase(
            int position,
            int newQuantity) {

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


        String foodName =
                foodNames.get(position);


        String foodSize =
                foodSizes.get(position);


        SQLiteDatabase db =
                new DatabaseHelper(context)
                        .getWritableDatabase();


        db.execSQL(
                "UPDATE cart SET quantity=?, total=? " +
                        "WHERE user_email=? " +
                        "AND food_name=? " +
                        "AND food_size=?",

                new Object[]{
                        newQuantity,
                        newQuantity * foodPrices.get(position),
                        userEmail,
                        foodName,
                        foodSize
                }
        );

    }


    // =========================================================
    // DELETE ITEM FROM DATABASE
    // =========================================================

    private void deleteFromDatabase(
            int position) {

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


        String foodName =
                foodNames.get(position);


        String foodSize =
                foodSizes.get(position);


        SQLiteDatabase db =
                new DatabaseHelper(context)
                        .getWritableDatabase();


        db.execSQL(
                "DELETE FROM cart " +
                        "WHERE user_email=? " +
                        "AND food_name=? " +
                        "AND food_size=?",

                new Object[]{
                        userEmail,
                        foodName,
                        foodSize
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

        CheckBox selectItem;

        ImageView cartImage;

        TextView cartFoodName;

        TextView cartFoodSize;

        TextView cartFoodPrice;

        TextView cartQuantity;

        TextView cartTotal;

        Button btnPlus;

        Button btnMinus;

        Button btnRemove;


        public ViewHolder(
                @NonNull View itemView) {

            super(itemView);


            // ==============================
            // CHECKBOX
            // ==============================

            selectItem =
                    itemView.findViewById(
                            R.id.selectItem
                    );


            // ==============================
            // IMAGE
            // ==============================

            cartImage =
                    itemView.findViewById(
                            R.id.cartImage
                    );


            // ==============================
            // FOOD NAME
            // ==============================

            cartFoodName =
                    itemView.findViewById(
                            R.id.cartFoodName
                    );


            // ==============================
            // FOOD SIZE
            // ==============================

            cartFoodSize =
                    itemView.findViewById(
                            R.id.cartFoodSize
                    );


            // ==============================
            // FOOD PRICE
            // ==============================

            cartFoodPrice =
                    itemView.findViewById(
                            R.id.cartFoodPrice
                    );


            // ==============================
            // QUANTITY
            // ==============================

            cartQuantity =
                    itemView.findViewById(
                            R.id.cartQuantity
                    );


            // ==============================
            // TOTAL
            // ==============================

            cartTotal =
                    itemView.findViewById(
                            R.id.cartTotal
                    );


            // ==============================
            // PLUS
            // ==============================

            btnPlus =
                    itemView.findViewById(
                            R.id.btnPlus
                    );


            // ==============================
            // MINUS
            // ==============================

            btnMinus =
                    itemView.findViewById(
                            R.id.btnMinus
                    );


            // ==============================
            // REMOVE
            // ==============================

            btnRemove =
                    itemView.findViewById(
                            R.id.btnRemove
                    );

        }

    }

}