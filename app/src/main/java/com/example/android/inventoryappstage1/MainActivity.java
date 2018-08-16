package com.example.android.inventoryappstage1;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.inventoryappstage1.data.InventoryContract;


public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int BOOK_LOADER = 0;

    private BookCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.inventory);

//         Open the editorView
        FloatingActionButton btn = findViewById(R.id.fab);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });

//      Find the listView to populate
        ListView listView = findViewById(R.id.list);
//      Set the emptyView
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);
//      Setting the list item adapter for the data fetched by the cursor
        mCursorAdapter = new BookCursorAdapter(this, null);
        listView.setAdapter(mCursorAdapter);

//      List item on click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                Uri currentBookUri = ContentUris.withAppendedId(InventoryContract.InventoryEntry.CONTENT_URI, id);
                intent.setData(currentBookUri);
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(BOOK_LOADER, null, this);
    }

    /**
     * Previewed Book setup
     */
    private void insertBook() {

        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.PRODUCT_NAME, "Philosophy Book");
        values.put(InventoryContract.InventoryEntry.PRODUCT_PRICE, 47);
        values.put(InventoryContract.InventoryEntry.PRODUCT_QUANTITY, 8);
        values.put(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_NAME, "Poliform");
        values.put(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_PHONE, "0021145523");

        getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI, values);
    }

    /**
     * Menu logic
     */

    private void deleteAllBooks() {
        getContentResolver().delete(InventoryContract.InventoryEntry.CONTENT_URI, null, null);
        Toast.makeText(this, "All entries are successfully deleted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_library, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_preview_book:
                insertBook();
                return true;
            case R.id.action_delete_all:
                deleteAllBooks();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
//        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.PRODUCT_NAME,
                InventoryContract.InventoryEntry.PRODUCT_PRICE,
                InventoryContract.InventoryEntry.PRODUCT_QUANTITY
        };

        return new CursorLoader(this,
                InventoryContract.InventoryEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor dataCursor) {
        mCursorAdapter.swapCursor(dataCursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
