package il.ac.huji.x_change.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

import il.ac.huji.x_change.Model.CurrencyItem;

/**
 * Created by Gil on 26/07/2015.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "currency";
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_FLAG = "flag";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "currencies.db";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_ID + " TEXT PRIMARY KEY," +
                    KEY_NAME + " TEXT NOT NULL," +
                    KEY_FLAG + " TEXT NOT NULL);";


    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
        //Populate DB from parse
        ParseQuery<ParseObject> query = ParseQuery.getQuery("currencies_new");
        query.setLimit(200);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> currencies, ParseException e) {
                if (e == null) {
                    Log.d("query", "Retrieved " + currencies.size() + " results");
                    for (int i = 0; i < currencies.size(); i++) {
                        ParseObject obj = currencies.get(i);
                        db.execSQL(insert(obj.getString("currency_code"), obj.getString("currency_name"), obj.getString("flag_code2")));
                    }
                } else {
                    Log.d("query", "Error: " + e.getMessage());
                }
            }
        });
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed and create table again
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);

    }

    private String insert(String code, String name, String flag) {
        return "INSERT INTO " + TABLE_NAME + " (" + KEY_ID + ", " + KEY_NAME + ", " +KEY_FLAG + ") VALUES (\"" + code + "\", \"" + name + "\", \"" + flag + "\");";
    }
}
