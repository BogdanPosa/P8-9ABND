package com.example.android.inventoryappstage1.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.inventoryappstage1.data.InventoryContract.InventoryEntry;

public class InventoryDbHelper extends SQLiteOpenHelper {

    //Name of the database
    private static final String DATABASE_NAME = "Inventory.db";

    //Version of the database
    private static final int DATABASE_VERSION = 1;

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        //Creates a new table into the db
        String SQL_BOOK_TABLE = "CREATE TABLE "
                + InventoryEntry.TABLE_NAME + " ("
                + InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryEntry.PRODUCT_NAME + " TEXT NOT NULL, "
                + InventoryEntry.PRODUCT_PRICE + " INTEGER NOT NULL, "
                + InventoryEntry.PRODUCT_QUANTITY + " INTEGER NOT NULL, "
                + InventoryEntry.PRODUCT_SUPPLIER_NAME + " TEXT,"
                + InventoryEntry.PRODUCT_SUPPLIER_PHONE + " LONG NOT NULL );";

        // Execute the SQL statement
        sqLiteDatabase.execSQL(SQL_BOOK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
