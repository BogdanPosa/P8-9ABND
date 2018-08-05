package com.example.android.inventoryappstage1;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.android.inventoryappstage1.data.InventoryDbHelper;
import com.example.android.inventoryappstage1.data.InventoryContract;


public class MainActivity extends AppCompatActivity {

InventoryDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDbHelper = new InventoryDbHelper(this);
        insertBook();
        readData();
    }
    @Override
    public void onStart(){
        super.onStart();
        insertBook();
    }

    /**
     * Used to insert data into the db
     */
    private void insertBook() {
    SQLiteDatabase db = mDbHelper.getWritableDatabase();


        // New entry to the inventory
    ContentValues values = new ContentValues();

    values.put(InventoryContract.InventoryEntry.PRODUCT_NAME, "Random book");
    values.put(InventoryContract.InventoryEntry.PRODUCT_PRICE, 0);
    values.put(InventoryContract.InventoryEntry.PRODUCT_QUANTITY, 9000);
    values.put(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_NAME, "supplier");
    values.put(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_PHONE, "000000000000");

    long newRowId = db.insert(InventoryContract.InventoryEntry.TABLE_NAME, null, values);

    if(newRowId == -1){
        Log.v("MainActivity","Err no new entry");
    }else{
        Log.v("MainActivity","New row "+newRowId);
    }
    }

    /**
     * Used to retrieve data from the db
     *
     * @return numbers of rows
     */
    private Cursor readData() {

        SQLiteDatabase dbase = mDbHelper.getReadableDatabase();

        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.PRODUCT_NAME,
                InventoryContract.InventoryEntry.PRODUCT_PRICE,
                InventoryContract.InventoryEntry.PRODUCT_QUANTITY,
                InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_NAME,
                InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_PHONE
        };
        Cursor cursor = dbase.query(
                InventoryContract.InventoryEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);
        try {
            Log.v("MainActivity", "If it\'s working cursor is updated:" + cursor.getCount());
        } finally {
            cursor.close();
        }
        return cursor;
    }
    }


