package com.example.canteenmanagementapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class OrderFragment extends Fragment {

    TextView orderText;
    DatabaseHelper dbHelper;

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_order_fragment,
                container,
                false);

        orderText = view.findViewById(R.id.orderText);

        dbHelper = new DatabaseHelper(getContext());

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM orders", null);

        StringBuilder data = new StringBuilder();

        while(cursor.moveToNext()){

            data.append("👤 ")
                    .append(cursor.getString(1))
                    .append("\n");

            data.append("🍔 ")
                    .append(cursor.getString(2))
                    .append("\n");

            data.append("₹")
                    .append(cursor.getInt(3))
                    .append("\n\n");
        }

        orderText.setText(data.toString());

        cursor.close();

        return view;
    }
}