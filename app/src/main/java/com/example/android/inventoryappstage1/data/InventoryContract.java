package com.example.android.inventoryappstage1.data;


import android.provider.BaseColumns;

public class InventoryContract {

    public InventoryContract() {
    }

    public static final class InventoryEntry implements BaseColumns {

        public static final String TABLE_NAME = "Inventory";

        public final static String _ID = BaseColumns._ID;
        public final static String PRODUCT_NAME = "product";
        public final static String PRODUCT_PRICE = "price";
        public final static String PRODUCT_QUANTITY = "quantity";
        public final static String PRODUCT_SUPPLIER_NAME = "supplier";
        public final static String PRODUCT_SUPPLIER_PHONE = "phone";

    }
}