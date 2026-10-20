package com.example.canteenmanagementapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // =====================================================
    // DATABASE INFORMATION
    // =====================================================

    public static final String DB_NAME = "CanteenDB";

    // Version 7 = Added order status
    public static final int DB_VERSION = 7;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public DatabaseHelper(Context context) {

        super(
                context,
                DB_NAME,
                null,
                DB_VERSION
        );

    }


    // =====================================================
    // ON CREATE
    // =====================================================

    @Override
    public void onCreate(SQLiteDatabase db) {

        // =================================================
        // USERS TABLE
        // =================================================

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS users (" +
                        "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT, " +
                        "email TEXT UNIQUE, " +
                        "password TEXT" +
                        ")"
        );


        // =================================================
        // MENU TABLE
        // =================================================

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS menu (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT, " +
                        "price INTEGER, " +
                        "image TEXT, " +
                        "category TEXT" +
                        ")"
        );


        // =================================================
        // ORDERS TABLE
        // =================================================

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS orders (" +
                        "order_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "user_name TEXT, " +
                        "user_email TEXT, " +
                        "food_name TEXT, " +
                        "food_size TEXT, " +
                        "quantity INTEGER, " +
                        "price INTEGER, " +
                        "total INTEGER, " +
                        "order_date TEXT, " +
                        "status TEXT DEFAULT 'Pending'" +
                        ")"
        );


        // =================================================
        // CART TABLE
        // =================================================

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS cart (" +
                        "cart_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "user_email TEXT, " +
                        "food_name TEXT, " +
                        "food_size TEXT, " +
                        "quantity INTEGER, " +
                        "price INTEGER, " +
                        "total INTEGER" +
                        ")"
        );


        // =================================================
        // WISHLIST TABLE
        // =================================================

        createWishlistTable(db);

    }


    // =====================================================
    // CREATE WISHLIST TABLE
    // =====================================================

    private void createWishlistTable(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS wishlist (" +
                        "wishlist_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "user_email TEXT NOT NULL, " +
                        "food_name TEXT NOT NULL, " +
                        "food_price INTEGER NOT NULL, " +
                        "UNIQUE(user_email, food_name)" +
                        ")"
        );

    }


    // =====================================================
    // ON UPGRADE
    // =====================================================

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion
    ) {

        // =================================================
        // VERSION 4
        // CART TABLE
        // =================================================

        if (oldVersion < 4) {

            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS cart (" +
                            "cart_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "user_email TEXT, " +
                            "food_name TEXT, " +
                            "food_size TEXT, " +
                            "quantity INTEGER, " +
                            "price INTEGER, " +
                            "total INTEGER" +
                            ")"
            );

        }


        // =================================================
        // VERSION 5
        // WISHLIST TABLE
        // =================================================

        if (oldVersion < 5) {

            createWishlistTable(db);

        }


        // =================================================
        // VERSION 6
        // FIX / RECREATE WISHLIST TABLE
        // =================================================

        if (oldVersion < 6) {

            db.execSQL(
                    "DROP TABLE IF EXISTS wishlist"
            );

            createWishlistTable(db);

        }


        // =================================================
        // VERSION 7
        // ADD ORDER STATUS
        // =================================================

        if (oldVersion < 7) {

            db.execSQL(
                    "ALTER TABLE orders " +
                            "ADD COLUMN status TEXT DEFAULT 'Pending'"
            );

        }

    }

}