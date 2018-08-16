package com.example.android.inventoryappstage1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.example.android.inventoryappstage1.data.InventoryContract;

public class BookCursorAdapter extends CursorAdapter {
    public BookCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
//        find the views to populate
        TextView nameTV = view.findViewById(R.id.name);
        TextView priceTV = view.findViewById(R.id.price_int);
        final TextView quantityTV = view.findViewById(R.id.quantity_int);
        final Button buyButton = view.findViewById(R.id.btn_buy);

//        find the column we're looking for
//        final int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.PRODUCT_QUANTITY);

//      find the item attributes we want to bind
//        final int currentBookId = cursor.getInt(idColumnIndex);
        String bookName = cursor.getString(nameColumnIndex);
        int bookPrice = cursor.getInt(priceColumnIndex);
        final int bookQuantity = cursor.getInt(quantityColumnIndex);

//      Updated attributes in the views
        nameTV.setText(bookName);
        priceTV.setText(Integer.toString(bookPrice));
        quantityTV.setText(Integer.toString(bookQuantity));

//       Order logic
        buyButton.setText(R.string.buy_btn);

/**
 * Reduces the number of books in the stock when clicked
 * Shows toast when value is 0
 * */
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int updatedQuantity = Integer.parseInt(quantityTV.getText().toString().trim());
                if (updatedQuantity > 0) {
                    quantityTV.setText(String.valueOf(updatedQuantity - 1));
                }
                if (updatedQuantity == 0) {
                    buyButton.setEnabled(false);
                    Toast.makeText(context, "Stock exhausted", Toast.LENGTH_SHORT).show();
                }
                ContentValues values = new ContentValues();
                values.put(InventoryContract.InventoryEntry.PRODUCT_QUANTITY, updatedQuantity);
            }
        });
    }
}
