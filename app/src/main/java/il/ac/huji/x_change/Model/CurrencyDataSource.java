package il.ac.huji.x_change.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import il.ac.huji.x_change.DB.MySQLiteHelper;

/**
 * Created by Gil on 26/07/2015.
 */
public class CurrencyDataSource {

    // Database fields
    private SQLiteDatabase db;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.KEY_ID, MySQLiteHelper.KEY_NAME,
            MySQLiteHelper.KEY_FLAG };

    public CurrencyDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // Adding new currency
    public void insertItem(CurrencyItem currency) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.KEY_ID, currency.getCode());
        values.put(MySQLiteHelper.KEY_NAME, currency.getName());
        values.put(MySQLiteHelper.KEY_FLAG, currency.getFlagCode());
        db.insert(MySQLiteHelper.TABLE_NAME, null, values);
    }

    // Deleting currency
    public void deleteItem(CurrencyItem currency) {
        db.delete(MySQLiteHelper.TABLE_NAME, MySQLiteHelper.KEY_ID + " = ?",
                new String[]{currency.getCode()});
    }

    // Getting all currencies
    public ArrayList<CurrencyItem> getAllCurrencies() {
        ArrayList<CurrencyItem> currencies = new ArrayList<CurrencyItem>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + MySQLiteHelper.TABLE_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                CurrencyItem currency = new CurrencyItem(cursor.getString(0), cursor.getString(1), cursor.getString(2));
                // Adding currency to list
                currencies.add(currency);
            } while (cursor.moveToNext());
        }
        cursor.close();
        sortList(currencies);
        // return items list
        if (currencies.isEmpty()) {
            return new ArrayList<CurrencyItem>();
        }
        else {
            return currencies;
        }

    }

    private void sortList(ArrayList<CurrencyItem> currencyList) {
        for (int i = 0; i < currencyList.size(); i++) {
            CurrencyItem curr = currencyList.get(i);
            replace("USD", i, 0, curr, currencyList);
            replace("EUR", i, 0, curr, currencyList);
            replace("GBP", i, 1, curr, currencyList);
            replace("ILS", i, 2, curr, currencyList);
        }
    }

    private void replace(String code, int iBefore, int iAfter, CurrencyItem curr, ArrayList<CurrencyItem> currencyList) {
        if (curr.getCode().equals(code)) {
            currencyList.remove(iBefore);
            currencyList.add(iAfter, curr);
        }
    }

    public CurrencyItem getCurrencyByCode(String code) {
        CurrencyItem currency = null;
        String selectQuery = "SELECT * FROM " + MySQLiteHelper.TABLE_NAME + " WHERE " + MySQLiteHelper.KEY_ID + " = \"" + code + "\";";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            currency = new CurrencyItem(cursor.getString(0), cursor.getString(1), cursor.getString(2));
        }
        return  currency;
    }

}
