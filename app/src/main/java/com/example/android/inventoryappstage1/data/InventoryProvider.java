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
    public static final String LOG_TAG = InventoryDbHelper.class.getSimpleName();
    private InventoryDbHelper mDbHelper;
    private static final int BOOKS = 500;
    private static final int BOOKS_ID = 501;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_BOOKS, BOOKS);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_BOOKS + "/#", BOOKS_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);

        switch (match) {
            case BOOKS:
                cursor = database.query(InventoryContract.InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case BOOKS_ID:
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(InventoryContract.InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI" + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                if (contentValues != null) {
                    return insertBook(uri, contentValues);
                }
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    public Uri insertBook(@NonNull Uri uri, @Nullable ContentValues values) {

        String name = values.getAsString(InventoryContract.InventoryEntry.PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("We need a name");
        }

        Integer price = values.getAsInteger(InventoryContract.InventoryEntry.PRODUCT_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("We need a price");
        }

        Integer quantity = values.getAsInteger(InventoryContract.InventoryEntry.PRODUCT_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("We need a quantity");
        }
        String supName = values.getAsString(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_NAME);
        if (supName == null) {
            throw new IllegalArgumentException("We need a name");
        }

        Long supPhone = values.getAsLong(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_PHONE);
        if (supPhone != null && supPhone < 0) {
            throw new IllegalArgumentException("We need a phone number");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(InventoryContract.InventoryEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                rowsDeleted = db.delete(InventoryContract.InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOKS_ID:
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(InventoryContract.InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }


    /**
     * Update books in the database with the given content values. Apply the changes to the rows
     * Return the number of rows that were successfully updated.
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateBook(uri, contentValues, selection, selectionArgs);
            case BOOKS_ID:
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                if (contentValues != null) {
                    return updateBook(uri, contentValues, selection, selectionArgs);
                }
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(InventoryContract.InventoryEntry.PRODUCT_NAME)) {
            String name = values.getAsString(InventoryContract.InventoryEntry.PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Book requires a name");
            }
        }
        if (values.containsKey(InventoryContract.InventoryEntry.PRODUCT_PRICE)) {
            Integer price = values.getAsInteger(InventoryContract.InventoryEntry.PRODUCT_PRICE);
            if (price != null && price < 0) {
                throw new IllegalArgumentException("Book requires a price");
            }
        }

        if (values.containsKey(InventoryContract.InventoryEntry.PRODUCT_QUANTITY)) {
            Integer quantity = values.getAsInteger(InventoryContract.InventoryEntry.PRODUCT_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Book requires a quantity");
            }
        }

        if (values.containsKey(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_NAME)) {
            String supName = values.getAsString(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_NAME);
            if (supName == null) {
                throw new IllegalArgumentException("Book requires a supName");
            }
        }
        if (values.containsKey(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_PHONE)) {
            String supPhone = values.getAsString(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_PHONE);
            if (supPhone == null) {
                throw new IllegalArgumentException("Book requires a supPhone");
            }
        }

        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(InventoryContract.InventoryEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return InventoryContract.InventoryEntry.CONTENT_LIST_TYPE;
            case BOOKS_ID:
                return InventoryContract.InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
