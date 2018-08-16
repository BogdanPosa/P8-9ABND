package com.example.android.inventoryappstage1.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class InventoryProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = InventoryDbHelper.class.getSimpleName();
    private static final int BOOKS = 500;
    private static final int BOOKS_ID = 501;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_BOOKS, BOOKS);

        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_BOOKS + "/#", BOOKS_ID);
    }

    /**
     * Db helper object
     */
    private InventoryDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        // Get readable db
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // Cursor that holds the result of the query
        Cursor cursor;

        //Figure out if the Uri matcher can match the Uri
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                //Query the shelf table with the given args
                cursor = database.query(InventoryContract.InventoryEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;
            case BOOKS_ID:
                //Extracts the id from the URI
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                //Performs the query for the extracted id
                cursor = database.query(InventoryContract.InventoryEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query, unknown URI " + uri);

        }
        // Set notification to the Cursor
        //If data changes the Cursor needs update
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        //Returns the cursor
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for this URI" + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateBook(uri, contentValues, selection, selectionArgs);
            case BOOKS_ID:
                //For the selected BOOK extract the ID and uses it for the selectionArgs
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for this URI" + uri);
        }
    }

    /**
     * Insert a Book into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertBook(Uri uri, ContentValues values) {
        //Check that the name !=null
        String name = values.getAsString(InventoryContract.InventoryEntry.PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("The book needs a name");
        }

        //Check that the book has a valid price
        Integer price = values.getAsInteger(InventoryContract.InventoryEntry.PRODUCT_PRICE);
        if (price == null || price < 0) {
            throw new IllegalArgumentException("The book needs a price");
        }


        //Check that the book has a valid quantity
        Integer quantity = values.getAsInteger(InventoryContract.InventoryEntry.PRODUCT_QUANTITY);
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("The book needs a quantity");
        }

        //Check that the name !=null
        String supName = values.getAsString(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_NAME);
        if (supName == null) {
            throw new IllegalArgumentException("The book needs a supplier ");
        }

        //Check that the supplier has a valid phone
        Integer supContact = values.getAsInteger(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_PHONE);
        if (supContact == null || supContact < 0) {
            throw new IllegalArgumentException("The supplier needs a valid contact");
        }

        //Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //Insert the book with the provided values
        long id = database.insert(InventoryContract.InventoryEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row " + uri);
            return null;
        }

        //Notify all the listeners that the data has changed
        getContext().getContentResolver().notifyChange(uri, null);

        //Return the new URI with the ID of the inserted row
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Update a Book into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        //If name is present Check that the name !=null
        if (values.containsKey(InventoryContract.InventoryEntry.PRODUCT_NAME)) {
            String name = values.getAsString(InventoryContract.InventoryEntry.PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("The book needs a name");
            }
        }
        //If present Check that the book has a valid price
        if (values.containsKey(InventoryContract.InventoryEntry.PRODUCT_PRICE)) {
            Integer price = values.getAsInteger(InventoryContract.InventoryEntry.PRODUCT_PRICE);
            if (price == null || price < 0) {
                throw new IllegalArgumentException("The book needs a price");
            }
        }

        //If present Check that the book has a valid quantity
        if (values.containsKey(InventoryContract.InventoryEntry.PRODUCT_QUANTITY)) {
            Integer quantity = values.getAsInteger(InventoryContract.InventoryEntry.PRODUCT_QUANTITY);
            if (quantity == null || quantity < 0) {
                throw new IllegalArgumentException("The book needs a quantity");
            }
        }

        //If present Check that the supplier name !=null
        if (values.containsKey(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_NAME)) {
            String supName = values.getAsString(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_NAME);
            if (supName == null) {
                throw new IllegalArgumentException("The book needs a supplier ");
            }
        }

        //If present Check that the supplier has a valid phone
        if (values.containsKey(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_PHONE)) {
            Integer supContact = values.getAsInteger(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_PHONE);
            if (supContact == null || supContact < 0) {
                throw new IllegalArgumentException("The supplier needs a valid contact");
            }
        }

        //Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //Update the db and get the number of rows affected
        int rowsUpdated = database.update(InventoryContract.InventoryEntry.TABLE_NAME, values, selection, selectionArgs);

        //If db has been updated notify all the listeners that the data has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        //Return the number of rows updated
        return rowsUpdated;
    }
}
