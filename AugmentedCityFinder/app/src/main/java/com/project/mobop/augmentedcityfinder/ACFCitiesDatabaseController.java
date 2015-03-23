package com.project.mobop.augmentedcityfinder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tom on 22.03.2015.
 */
public class ACFCitiesDatabaseController extends SQLiteOpenHelper {

    private Context context;

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 3;

    // Database Name
    private static final String DATABASE_NAME = "cities";

    // Cities table name
    private static final String TABLE_CITIES = "cities";

    // Cities Table Columns names
    private static final String KEY_ID = "city_id";
    private static final String KEY_NAME = "city_name";
    private static final String KEY_COUNTRY = "city_country";
    private static final String KEY_LATITUDE = "city_latitude";
    private static final String KEY_LONGITUDE = "city_longitude";
    private static final String KEY_CONTINENT = "city_continent";

    public ACFCitiesDatabaseController(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CITIES + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME + " TEXT,"
                + KEY_COUNTRY + " TEXT,"
                + KEY_LATITUDE + " DECIMAL,"
                + KEY_LONGITUDE + " DECIMAL,"
                + KEY_CONTINENT + " TEXT" + ");";

        try{
            db.beginTransaction();
            db.execSQL(CREATE_CONTACTS_TABLE);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        ACFJSONHandler jsonHandler = new ACFJSONHandler(context);
        try {
            jsonHandler.importCitiesFromFile(db);
        } catch (IOException e) {
            Log.e("MainActivity", "Error reading in JSON File");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CITIES + ";");
        onCreate(db);
    }

    public void addCity(ACFCity city){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, city.getName());
        values.put(KEY_COUNTRY, city.getCountry());
        values.put(KEY_LATITUDE, city.getLocation().getLatitude());
        values.put(KEY_LONGITUDE, city.getLocation().getLongitude());
        values.put(KEY_CONTINENT, city.getContinent());

        try{
            db.beginTransaction();
            db.insert(TABLE_CITIES, null, values);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        db.close();
    }

    public void addCity(ACFCity city, SQLiteDatabase db){
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, city.getName());
        values.put(KEY_COUNTRY, city.getCountry());
        values.put(KEY_LATITUDE, city.getLocation().getLatitude());
        values.put(KEY_LONGITUDE, city.getLocation().getLongitude());
        values.put(KEY_CONTINENT, city.getContinent());

        try{
            db.beginTransaction();
            db.insert(TABLE_CITIES, null, values);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public List<ACFCity> getAllCities(){
        List<ACFCity> citiesList = new ArrayList<ACFCity>();
        String selectQuery = "SELECT  * FROM " + TABLE_CITIES;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ACFCity city = new ACFCity();
                city.setId(Integer.parseInt(cursor.getString(0)));
                city.setName(cursor.getString(1));
                city.setCountry(cursor.getString(2));

                Location location = new Location("City");
                location.setLatitude(cursor.getDouble(3));
                location.setLongitude(cursor.getDouble(4));
                city.setLocation(location);
                city.setContinent(cursor.getString(5));

                // Adding city to list
                citiesList.add(city);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return citiesList;
    }

    public int updateCity(ACFCity city) {
        int result;
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, city.getName());
        values.put(KEY_COUNTRY, city.getCountry());
        values.put(KEY_LATITUDE, city.getLocation().getLatitude());
        values.put(KEY_LONGITUDE, city.getLocation().getLongitude());
        values.put(KEY_CONTINENT, city.getContinent());

        // updating row
        try{
            db.beginTransaction();
            result = db.update(TABLE_CITIES, values, KEY_ID + " = ?",
                    new String[] { String.valueOf(city.getId()) });
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
        return result;
    }

    public void deleteCity(ACFCity city) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            db.delete(TABLE_CITIES, KEY_ID + " = ?",
                    new String[]{String.valueOf(city.getId())});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
    }


}
