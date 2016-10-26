package com.jeansandtshirt.wifive;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by samir on 2016-09-02.
 */
public class SQLiteDatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 2;

    public static final String DATABASE_NAME = "WifiveDB";

    public static final String TABLE_NAME = "wifi_table";

    public static final String COL_1 = "FIREBASEKEY";
    public static final String COL_2 = "SSID";
    public static final String COL_3 = "PASSWORD";
    public static final String COL_4 = "MAC";
    public static final String COL_5 = "DEVICE_ID";
    public static final String COL_6 = "LATITUDE";
    public static final String COL_7 = "LONGITUDE";
    public static final String COL_8 = "CITY";
    public static final String COL_9 = "USER_RATINGS";

    public static final String[] ALL_KEYS = new String[] {COL_1, COL_2, COL_3, COL_4, COL_5, COL_6, COL_7, COL_8, COL_9};

    private static final String TEXT_TYPE = " TEXT";
    private static final String DOUBLE_TYPE = " DOUBLE";
    private static final String COMMA_SEP = ", ";
    private static final String PRIMARY_KEY = " TEXT PRIMARY KEY";

    private static final String SQL_CREATE_ENTRIES =
                    "CREATE TABLE " + TABLE_NAME + " (" +
                    COL_1 + TEXT_TYPE + COMMA_SEP +
                    COL_2 + TEXT_TYPE + COMMA_SEP +
                    COL_3 + TEXT_TYPE + COMMA_SEP +
                    COL_4 + TEXT_TYPE + COMMA_SEP +
                    COL_5 + TEXT_TYPE + COMMA_SEP +
                    COL_6 + DOUBLE_TYPE + COMMA_SEP +
                    COL_7 + DOUBLE_TYPE + COMMA_SEP +
                    COL_8 + TEXT_TYPE + COMMA_SEP +
                    COL_9 + TEXT_TYPE +");";
    //TODO: CLOSE DATABASE WHEN DONE
    public SQLiteDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean insertData(String firebaseKey, String SSID, String password, String MAC, String deviceID,
                              double latitude, double longitude, String city, String userRatings){

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, firebaseKey);
        contentValues.put(COL_2, SSID);
        contentValues.put(COL_3, password);
        contentValues.put(COL_4, MAC);
        contentValues.put(COL_5, deviceID);
        contentValues.put(COL_6, latitude);
        contentValues.put(COL_7, longitude);
        contentValues.put(COL_8, city);
        contentValues.put(COL_9, userRatings);
        long result = sqLiteDatabase.insert(TABLE_NAME, null, contentValues);

        return result != -1;
    }

    public void getData(){
        String selectQuery = "SELECT * FROM wifi_table";
        SQLiteDatabase db  = this.getReadableDatabase();

        android.database.Cursor cursor = db.rawQuery("SELECT  * FROM wifi_table", null);
        String[] data = new String[100];
        int i = 0;
        if (cursor.moveToFirst()) {
            do {
                data[i] = cursor.getString(cursor.getColumnIndex(COL_1));
                i++;
                // get the data into array, or class variable
            } while (cursor.moveToNext());
        }
        cursor.close();

        /*String where = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = 	db.query(true, TABLE_NAME, ALL_KEYS,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }*/
    }

}
