package rodriguez.miguel.ordinarioo_miguel_rc;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
public class DatabaseHelper  extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_PRODUCTS = "products";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_IMAGE = "image";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_PRODUCTS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_PRICE + " REAL, " +
                COLUMN_CATEGORY + " TEXT, " +
                COLUMN_IMAGE + " BLOB)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    public long insertProduct(String name, double price, String category, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_CATEGORY, category);
        values.put(COLUMN_IMAGE, image);

        return db.insert(TABLE_PRODUCTS, null, values);
    }

    public boolean deleteProduct(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_PRODUCTS, COLUMN_ID + "=?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean updateProductPrice(int id, double newPrice) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRICE, newPrice);

        return db.update(TABLE_PRODUCTS, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)}) > 0;
    }

    public List<Product> getProductsByCategory(String category) {
        List<Product> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCTS, null, COLUMN_CATEGORY + "=?", new String[]{category}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                double price = cursor.getDouble(cursor.getColumnIndex(COLUMN_PRICE));
                byte[] image = cursor.getBlob(cursor.getColumnIndex(COLUMN_IMAGE));
                productList.add(new Product(id, name, price, category, image));
            } while (cursor.moveToNext());
            cursor.close();
        }

        return productList;
    }

    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                double price = cursor.getDouble(cursor.getColumnIndex(COLUMN_PRICE));
                String category = cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY));
                byte[] image = cursor.getBlob(cursor.getColumnIndex(COLUMN_IMAGE));
                productList.add(new Product(id, name, price, category, image));
            } while (cursor.moveToNext());
            cursor.close();
        }

        return productList;
    }
}