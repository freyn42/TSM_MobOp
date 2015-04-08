package com.project.mobop.augmentedcityfinder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

import android.database.SQLException;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tom on 22.03.2015.
 */
public class ACFCitiesDatabaseController extends SQLiteOpenHelper {

    private Context context;
    private SQLiteDatabase db;

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 3;

    // Database Name
    private static final String DATABASE_NAME = "acf";

    // ACF table names
    private static final String TABLE_CITY = "city";
    private static final String TABLE_COUNTRY = "country";
    private static final String TABLE_CONTINENT = "continent";
    private static final String TABLE_DEVICE = "device";
    private static final String TABLE_GROUP = "group_list";
    private static final String TABLE_CITY_GROUP = "city_group";
    private static final String TABLE_USER_LOCATION = "user_location";
    private static final String TABLE_USER_LOCATION_CITY = "user_location_city";

    // City Table Columns names
    private static final String KEY_CITY_ID = "city_id";
    private static final String KEY_CITY_NAME = "city_name";
    private static final String KEY_CITY_COUNTRY = "city_country";
    private static final String KEY_CITY_LATITUDE = "city_latitude";
    private static final String KEY_CITY_LONGITUDE = "city_longitude";
    private static final String KEY_CITY_DEVICE = "city_device";

    // Country Table Columns names
    private static final String KEY_COUNTRY_ID = "country_id";
    private static final String KEY_COUNTRY_CONTINENT = "country_continent";
    private static final String KEY_COUNTRY_NAME = "country_name";
    private static final String KEY_COUNTRY_CODE = "country_code";

    // Continent Table Columns names
    private static final String KEY_CONTINENT_ID = "continent_id";
    private static final String KEY_CONTINENT_NAME = "continent_name";

    // Device Table Columns names
    private static final String KEY_DEVICE_ID = "device_id";
    private static final String KEY_DEVICE_NUMBER = "device_number";
    private static final String KEY_DEVICE_REGISTRATION_DATE = "device_registration_date";

    // Group Table Columns names
    private static final String KEY_GROUP_ID = "group_id";
    private static final String KEY_GROUP_DEVICE = "group_device";
    private static final String KEY_GROUP_NAME = "group_name";
    private static final String KEY_GROUP_CREATION_DATE = "group_creation_date";
    private static final String KEY_GROUP_MODIFICATION_DATE = "group_modification_date";
    private static final String KEY_GROUP_VISIBLE = "group_show";

    //City Group Join Table Columns Names
    private static final String KEY_CITY_GROUP_ID = "city_group_id";
    private static final String KEY_CITY_GROUP_CITY = "city_group_city";
    private static final String KEY_CITY_GROUP_GROUP = "city_group_group";

    //User Location Table Columns Names
    private static final String KEY_USER_LOCATION_ID = "user_location_id";
    private static final String KEY_USER_LOCATION_DATE = "user_location_date";
    private static final String KEY_USER_LOCATION_DEVICE = "user_location_device";
    private static final String KEY_USER_LOCATION_LATITUDE = "user_location_latitude";
    private static final String KEY_USER_LOCATION_LONGITUDE = "user_location_longitude";

    //User Location City Join Table Columns Names
    private static final String KEY_USER_LOCATION_CITY_ID = "user_location_city_id";
    private static final String KEY_USER_LOCATION_CITY_CITY = "user_location_city_city";
    private static final String KEY_USER_LOCATION_CITY_USER_LOCATION = "user_location_city_user_location";

    public ACFCitiesDatabaseController(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public void createContinentTable(SQLiteDatabase db){
        String CREATE_CONTINENT_TABLE = "CREATE TABLE " + TABLE_CONTINENT + "("
                + KEY_CONTINENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_CONTINENT_NAME + " TEXT"
                + ");";
        try{
            db.beginTransaction();
            db.execSQL(CREATE_CONTINENT_TABLE);
            db.setTransactionSuccessful();
        } catch(SQLException sqlE){
            Log.e("ACFCitiesDatabase",sqlE.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    public void createCountryTable(SQLiteDatabase db){
        String CREATE_COUNTRY_TABLE = "CREATE TABLE " + TABLE_COUNTRY + "("
                + KEY_COUNTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_COUNTRY_NAME + " TEXT,"
                + KEY_COUNTRY_CONTINENT + " INTEGER ,"
                + KEY_COUNTRY_CODE + " TEXT,"
                + "FOREIGN KEY (" + KEY_COUNTRY_CONTINENT + ") REFERENCES " + TABLE_CONTINENT + "(" + KEY_CONTINENT_ID + ")"
                + ");";
        try {
            db.beginTransaction();
            db.execSQL(CREATE_COUNTRY_TABLE);
            db.setTransactionSuccessful();
        } catch(SQLException sqlE){
            Log.e("ACFCitiesDatabase",sqlE.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    public void createCityTable(SQLiteDatabase db){
        String CREATE_CITY_TABLE = "CREATE TABLE " + TABLE_CITY + "("
                + KEY_CITY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_CITY_NAME + " TEXT,"
                + KEY_CITY_COUNTRY + " INTEGER,"
                + KEY_CITY_LATITUDE + " DECIMAL,"
                + KEY_CITY_LONGITUDE + " DECIMAL,"
                + KEY_CITY_DEVICE + " INTEGER, "
                + "FOREIGN KEY (" + KEY_CITY_COUNTRY + ") REFERENCES " + TABLE_COUNTRY + "(" + KEY_COUNTRY_ID + "),"
                + "FOREIGN KEY (" + KEY_CITY_DEVICE + ") REFERENCES " + TABLE_DEVICE + "(" + KEY_DEVICE_ID + ")"
                + ");";
        try{
            db.beginTransaction();
            db.execSQL(CREATE_CITY_TABLE);
            db.setTransactionSuccessful();
        } catch(SQLException sqlE){
            Log.e("ACFCitiesDatabase",sqlE.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    public void createDeviceTable(SQLiteDatabase db){
        String CREATE_DEVICE_TABLE = "CREATE TABLE " + TABLE_DEVICE + "("
                + KEY_DEVICE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_DEVICE_REGISTRATION_DATE + " DATETIME"
                + ");";
        try{
            db.beginTransaction();
            db.execSQL(CREATE_DEVICE_TABLE);
            db.setTransactionSuccessful();
        } catch(SQLException sqlE){
            Log.e("ACFCitiesDatabase",sqlE.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    public void createGroupTable(SQLiteDatabase db){
        String CREATE_GROUP_TABLE = "CREATE TABLE " + TABLE_GROUP + "("
                + KEY_GROUP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_GROUP_NAME + " TEXT,"
                + KEY_GROUP_DEVICE + " INTEGER,"
                + KEY_GROUP_CREATION_DATE + " DATETIME,"
                + KEY_GROUP_MODIFICATION_DATE + " DATETIME,"
                + KEY_GROUP_VISIBLE + " BOOLEAN DEFAULT 1,"
                + "FOREIGN KEY (" + KEY_GROUP_DEVICE + ") REFERENCES " + TABLE_DEVICE + "(" + KEY_DEVICE_ID + ")"
                + ");";
        try{
            db.beginTransaction();
            db.execSQL(CREATE_GROUP_TABLE);
            db.setTransactionSuccessful();
        } catch(SQLException sqlE){
            Log.e("ACFCitiesDatabase",sqlE.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    public void createCityGroupTable(SQLiteDatabase db){
        String CREATE_CITY_GROUP_TABLE = "CREATE TABLE " + TABLE_CITY_GROUP + "("
                + KEY_CITY_GROUP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_CITY_GROUP_CITY + " INTEGER,"
                + KEY_CITY_GROUP_GROUP + " INTEGER,"
                + "FOREIGN KEY (" + KEY_CITY_GROUP_CITY + ") REFERENCES " + TABLE_CITY + "(" + KEY_CITY_ID + "),"
                + "FOREIGN KEY (" + KEY_CITY_GROUP_GROUP + ") REFERENCES " + TABLE_GROUP + "(" + KEY_GROUP_ID + ")"
                + ");";
        try{
            db.beginTransaction();
            db.execSQL(CREATE_CITY_GROUP_TABLE);
            db.setTransactionSuccessful();
        } catch(SQLException sqlE){
            Log.e("ACFCitiesDatabase",sqlE.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    public void createUserLocationTable(SQLiteDatabase db){
        String CREATE_USER_LOCATION_TABLE = "CREATE TABLE " + TABLE_USER_LOCATION + "("
                + KEY_USER_LOCATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_USER_LOCATION_DATE + " DATETIME,"
                + KEY_USER_LOCATION_DEVICE + " INTEGER,"
                + KEY_USER_LOCATION_LONGITUDE + " DECIMAL,"
                + KEY_USER_LOCATION_LATITUDE + " DECIMAL,"
                + "FOREIGN KEY (" + KEY_USER_LOCATION_DEVICE + ") REFERENCES " + TABLE_DEVICE + "(" + KEY_DEVICE_ID + ")"
                + ");";
        try{
            db.beginTransaction();
            db.execSQL(CREATE_USER_LOCATION_TABLE);
            db.setTransactionSuccessful();
        } catch(SQLException sqlE){
            Log.e("ACFCitiesDatabase",sqlE.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    public void createUserLocationCityTable(SQLiteDatabase db){
        String CREATE_USER_LOCATION_CITY_TABLE = "CREATE TABLE " + TABLE_USER_LOCATION_CITY + "("
                + KEY_USER_LOCATION_CITY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_USER_LOCATION_CITY_CITY + " INTEGER,"
                + KEY_USER_LOCATION_CITY_USER_LOCATION + " INTEGER,"
                + "FOREIGN KEY (" + KEY_USER_LOCATION_CITY_CITY + ") REFERENCES " + TABLE_CITY + "(" + KEY_CITY_ID + "),"
                + "FOREIGN KEY (" + KEY_USER_LOCATION_CITY_USER_LOCATION + ") REFERENCES " + TABLE_USER_LOCATION + "(" + KEY_USER_LOCATION_ID + ")"
                + ");";
        try{
            db.beginTransaction();
            db.execSQL(CREATE_USER_LOCATION_CITY_TABLE);
            db.setTransactionSuccessful();
        } catch(SQLException sqlE){
            Log.e("ACFCitiesDatabase",sqlE.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        createContinentTable(db);
        createCountryTable(db);
        createDeviceTable(db);
        createCityTable(db);
        createGroupTable(db);
        createCityGroupTable(db);
        createUserLocationTable(db);
        createUserLocationCityTable(db);

        initializeLocalDB();
    }

    public void initializeLocalDB(){
        ACFRestPOSTController postController = new ACFRestPOSTController(context,this);
        try {
            addDevice(postController.postDeviceToServer());
            Toast.makeText(context, "Device erstellt", Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            Toast.makeText(context, "Fehler beim Erstellen des Device", Toast.LENGTH_SHORT).show();
        }
        addSystemDevice();

        ACFRestGETController restController = new ACFRestGETController(context,this);
        restController.getContinentsFromServer();
        restController.getCountriesFromServer();
        restController.getCitiesFromServer();
        restController.getGroupsFromServer();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CITY + ";");
        onCreate(db);
    }

    public void addCity(ACFCity city){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CITY_NAME, city.getCityName());
        values.put(KEY_CITY_COUNTRY, city.getCountryName());
        values.put(KEY_CITY_LATITUDE, city.getLatitude());
        values.put(KEY_CITY_LONGITUDE, city.getLongitude());

        try{
            db.beginTransaction();
            db.insert(TABLE_CITY, null, values);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        db.close();
    }

    public void addCity(ACFCity city, SQLiteDatabase db){
        ContentValues values = new ContentValues();
        values.put(KEY_CITY_NAME, city.getCityName());
        values.put(KEY_CITY_COUNTRY, city.getCountryName());
        values.put(KEY_CITY_LATITUDE, city.getLatitude());
        values.put(KEY_CITY_LONGITUDE, city.getLongitude());

        try{
            db.beginTransaction();
            db.insert(TABLE_CITY, null, values);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public List<ACFCity> getAllCities(){
        List<ACFCity> citiesList = new ArrayList<ACFCity>();
        String selectQuery = "SELECT " + KEY_CITY_ID + "," + KEY_CITY_DEVICE + "," + KEY_CITY_NAME + "," + KEY_CITY_LONGITUDE + "," + KEY_CITY_LATITUDE + "," + KEY_COUNTRY_NAME + "," + KEY_CONTINENT_NAME
            + " FROM " + TABLE_CITY  + " JOIN " + TABLE_COUNTRY + " ON " + KEY_CITY_COUNTRY + " =" + KEY_COUNTRY_ID
                + " JOIN " + TABLE_CONTINENT + " ON " + KEY_COUNTRY_CONTINENT + " =" + KEY_CONTINENT_ID + ";";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ACFCity city = new ACFCity();
                city.setId(cursor.getInt(0));
                city.setDeviceId(cursor.getInt(1));
                city.setCityName(cursor.getString(2));
                Location location = new Location("City");
                location.setLatitude(cursor.getDouble(3));
                location.setLongitude(cursor.getDouble(4));
                city.setLocation(location);
                city.setCountryName(cursor.getString(5));
                city.setContinentName(cursor.getString(6));

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
        values.put(KEY_CITY_NAME, city.getCityName());
        values.put(KEY_CITY_COUNTRY, city.getCountryName());
        values.put(KEY_CITY_LATITUDE, city.getLocation().getLatitude());
        values.put(KEY_CITY_LONGITUDE, city.getLocation().getLongitude());

        // updating row
        try{
            db.beginTransaction();
            result = db.update(TABLE_CITY, values, KEY_CITY_ID + " = ?",
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
            db.delete(TABLE_CITY, KEY_CITY_ID + " = ?",
                    new String[]{String.valueOf(city.getId())});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    public void addContinents(String result) throws JSONException {
        ACFJSONHandler jsonHandler = new ACFJSONHandler(context);
        JsonArray continentArray = jsonHandler.getJSONArray(result, "continents");
        if(db == null || !db.isOpen()){
            db = getWritableDatabase();
        }
        try{
            db.beginTransaction();
            for(int i = 0; i < continentArray.size(); i++){
                JsonObject jobject = continentArray.get(i).getAsJsonObject();
                ContentValues values = new ContentValues();
                values.put(KEY_CONTINENT_ID, jobject.get("continentId").getAsInt());
                values.put(KEY_CONTINENT_NAME, jobject.get("name").getAsString());
                db.insert(TABLE_CONTINENT, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void addCountries(String result) throws JSONException {
        ACFJSONHandler jsonHandler = new ACFJSONHandler(context);
        JsonArray countryArray = jsonHandler.getJSONArray(result, "countries");
        if(db == null || !db.isOpen()){
            db = getWritableDatabase();
        }
        try{
            db.beginTransaction();
            for(int i = 0; i < countryArray.size(); i++){
                JsonObject jobject = countryArray.get(i).getAsJsonObject();
                ContentValues values = new ContentValues();
                values.put(KEY_COUNTRY_ID, jobject.get("countryId").getAsInt());
                values.put(KEY_COUNTRY_CONTINENT, jobject.get("continentId").getAsInt());
                values.put(KEY_COUNTRY_CODE, jobject.get("code").getAsString());
                values.put(KEY_COUNTRY_NAME, jobject.get("name").getAsString());
                db.insert(TABLE_COUNTRY, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void addSystemDevice(){
        if(db == null || !db.isOpen()){
            db = getWritableDatabase();
        }
        try{
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(KEY_DEVICE_ID, 1);
            values.put(KEY_DEVICE_REGISTRATION_DATE, String.valueOf(new Date()));
            db.insert(TABLE_DEVICE, null, values);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void addCities(String result) throws JSONException {
        ACFJSONHandler jsonHandler = new ACFJSONHandler(context);
        JsonArray cityArray = jsonHandler.getJSONArray(result, "cities");
        if(db == null || !db.isOpen()){
            db = getWritableDatabase();
        }
        try{
            db.beginTransaction();
            for(int i = 0; i < cityArray.size(); i++){
                JsonObject jobject = cityArray.get(i).getAsJsonObject();
                ContentValues values = new ContentValues();
                values.put(KEY_CITY_ID, jobject.get("id").getAsInt());
                values.put(KEY_CITY_DEVICE, jobject.get("deviceId").getAsInt());
                values.put(KEY_CITY_LONGITUDE, jobject.get("longitude").getAsDouble());
                values.put(KEY_CITY_LATITUDE, jobject.get("latitude").getAsDouble());
                values.put(KEY_CITY_COUNTRY, jobject.get("countryId").getAsInt());
                values.put(KEY_CITY_NAME, jobject.get("name").getAsString());
                db.insert(TABLE_CITY, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void updateCities(String result) throws JSONException {
        ACFJSONHandler jsonHandler = new ACFJSONHandler(context);
        JsonArray cityArray = jsonHandler.getJSONArray(result, "cities");
        if(db == null || !db.isOpen()){
            db = getWritableDatabase();
        }
        try{
            db.beginTransaction();
            for(int i = 0; i < cityArray.size(); i++){
                JsonObject jobject = cityArray.get(i).getAsJsonObject();
                ContentValues values = new ContentValues();
                values.put(KEY_CITY_ID, jobject.get("id").getAsInt());
                values.put(KEY_CITY_DEVICE, jobject.get("deviceId").getAsInt());
                values.put(KEY_CITY_LONGITUDE, jobject.get("longitude").getAsDouble());
                values.put(KEY_CITY_LATITUDE, jobject.get("latitude").getAsDouble());
                values.put(KEY_CITY_COUNTRY, jobject.get("countryId").getAsInt());
                values.put(KEY_CITY_NAME, jobject.get("name").getAsString());
                db.update(TABLE_CITY, values, KEY_CITY_ID + " = ?",
                        new String[]{String.valueOf(jobject.get("id").getAsInt())});
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void addGroups(String result) throws JSONException {
        ACFJSONHandler jsonHandler = new ACFJSONHandler(context);
        JsonArray groupArray = jsonHandler.getJSONArray(result, "groupList");
        if(db == null || !db.isOpen()){
            db = getWritableDatabase();
        }
        try{
            db.beginTransaction();
            for(int i = 0; i < groupArray.size(); i++){
                JsonObject jobject = groupArray.get(i).getAsJsonObject();
                ContentValues values = new ContentValues();
                values.put(KEY_GROUP_ID, jobject.get("id").getAsInt());
                values.put(KEY_GROUP_CREATION_DATE, jobject.get("creationDate").getAsString());
                values.put(KEY_GROUP_MODIFICATION_DATE, jobject.get("modificationDate").getAsString());
                values.put(KEY_GROUP_DEVICE, jobject.get("deviceId").getAsInt());
                values.put(KEY_GROUP_NAME, jobject.get("name").getAsString());
                if(db.insert(TABLE_GROUP, null, values) == -1){
                    throw new JSONException("");
                }
                JsonArray memberArray = jobject.getAsJsonArray("members");
                for(int j = 0; j < memberArray.size(); j++){
                    ContentValues memberValues = new ContentValues();
                    memberValues.put(KEY_CITY_GROUP_CITY,memberArray.get(j).getAsInt());
                    memberValues.put(KEY_CITY_GROUP_GROUP,jobject.get("id").getAsInt());
                    db.insert(TABLE_CITY_GROUP, null, memberValues);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void updateGroups(String result) throws JSONException {
        ACFJSONHandler jsonHandler = new ACFJSONHandler(context);
        JsonArray groupArray = jsonHandler.getJSONArray(result, "groupList");
        if(db == null || !db.isOpen()){
            db = getWritableDatabase();
        }
        try{
            db.beginTransaction();
            for(int i = 0; i < groupArray.size(); i++){
                JsonObject jobject = groupArray.get(i).getAsJsonObject();
                ContentValues values = new ContentValues();
                values.put(KEY_GROUP_ID, jobject.get("id").getAsInt());
                values.put(KEY_GROUP_CREATION_DATE, jobject.get("creationDate").getAsString());
                values.put(KEY_GROUP_MODIFICATION_DATE, jobject.get("modificationDate").getAsString());
                values.put(KEY_GROUP_DEVICE, jobject.get("deviceId").getAsInt());
                values.put(KEY_GROUP_NAME, jobject.get("name").getAsString());
                db.update(TABLE_GROUP, values, KEY_GROUP_ID + " = ?", new String[] { String.valueOf(jobject.get("id").getAsInt()) });

                //delete all entries with this group_id in table city_group
                db.delete(TABLE_CITY_GROUP,KEY_CITY_GROUP_GROUP + " = ?",new String[] { String.valueOf(jobject.get("id").getAsInt()) });

                JsonArray memberArray = jobject.getAsJsonArray("members");
                for(int j = 0; j < memberArray.size(); j++){
                    ContentValues memberValues = new ContentValues();
                    memberValues.put(KEY_CITY_GROUP_CITY,memberArray.get(j).getAsInt());
                    memberValues.put(KEY_CITY_GROUP_GROUP,jobject.get("id").getAsInt());
                    db.insert(TABLE_CITY_GROUP, null, memberValues);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public List<ACFCityGroup> getGroups() {
        List<ACFCityGroup> groups = new ArrayList<ACFCityGroup>();
        SQLiteDatabase db = getReadableDatabase();

        String selectGroupQuery = "SELECT " + KEY_GROUP_ID + "," + KEY_GROUP_NAME + "," + KEY_GROUP_VISIBLE + "," + KEY_GROUP_DEVICE
                + " FROM " + TABLE_GROUP + ";";

        Cursor groupCursor = db.rawQuery(selectGroupQuery, null);
        if (groupCursor.moveToFirst()) {
            do {
                ACFCityGroup group = new ACFCityGroup();
                group.setId(groupCursor.getInt(0));
                group.setName(groupCursor.getString(1));
                group.setVisible(groupCursor.getInt(2) == 1);
                group.setDeviceId(groupCursor.getInt(3));
                groups.add(group);
            } while (groupCursor.moveToNext());
        }
        groupCursor.close();

        for(ACFCityGroup group : groups) {
            String selectMembersQuery = "SELECT " + KEY_CITY_ID + "," + KEY_CITY_DEVICE + "," + KEY_CITY_NAME + "," + KEY_CITY_LONGITUDE + "," + KEY_CITY_LATITUDE + "," + KEY_COUNTRY_NAME + "," + KEY_CONTINENT_NAME + "," + KEY_COUNTRY_ID
                    + " FROM " + TABLE_GROUP + " JOIN " + TABLE_CITY_GROUP + " ON " + KEY_GROUP_ID + " =" + KEY_CITY_GROUP_GROUP
                    + " JOIN " + TABLE_CITY + " ON " + KEY_CITY_GROUP_CITY + " =" + KEY_CITY_ID
                    + " JOIN " + TABLE_COUNTRY + " ON " + KEY_CITY_COUNTRY + " =" + KEY_COUNTRY_ID
                    + " JOIN " + TABLE_CONTINENT + " ON " + KEY_COUNTRY_CONTINENT + " =" + KEY_CONTINENT_ID
                    + " WHERE " + KEY_GROUP_ID + " = " + group.getId() + ";";
            Cursor cityCursor = db.rawQuery(selectMembersQuery, null);
            if (cityCursor.moveToFirst()) {
                do {
                    ACFCity city = new ACFCity();
                    city.setId(cityCursor.getInt(0));
                    city.setDeviceId(cityCursor.getInt(1));
                    city.setCityName(cityCursor.getString(2));
                    Location location = new Location("City");
                    location.setLongitude(cityCursor.getDouble(3));
                    location.setLatitude(cityCursor.getDouble(4));
                    city.setLocation(location);
                    city.setCountryName(cityCursor.getString(5));
                    city.setContinentName(cityCursor.getString(6));
                    city.setCountryId(cityCursor.getInt(7));
                    group.getCityList().add(city);
                } while (cityCursor.moveToNext());
            }
            cityCursor.close();
        }
        db.close();
        return groups;
    }

    public List<ACFCity> getAllVisibleCities() {
        SQLiteDatabase db = getReadableDatabase();
        List<ACFCity> cities = new ArrayList<ACFCity>();
        String selectVisibleCitiesQuery = "SELECT " + KEY_CITY_ID + "," + KEY_CITY_DEVICE + "," + KEY_CITY_NAME + "," + KEY_CITY_LONGITUDE + "," + KEY_CITY_LATITUDE + "," + KEY_COUNTRY_NAME + "," + KEY_CONTINENT_NAME + "," + KEY_COUNTRY_ID
                + " FROM " + TABLE_GROUP + " JOIN " + TABLE_CITY_GROUP + " ON " + KEY_GROUP_ID + " =" + KEY_CITY_GROUP_GROUP
                + " JOIN " + TABLE_CITY + " ON " + KEY_CITY_GROUP_CITY + " =" + KEY_CITY_ID
                + " JOIN " + TABLE_COUNTRY + " ON " + KEY_CITY_COUNTRY + " =" + KEY_COUNTRY_ID
                + " JOIN " + TABLE_CONTINENT + " ON " + KEY_COUNTRY_CONTINENT + " =" + KEY_CONTINENT_ID
                + " WHERE " + KEY_GROUP_VISIBLE + " = 1;";
        Cursor cityCursor = db.rawQuery(selectVisibleCitiesQuery, null);
        if (cityCursor.moveToFirst()) {
            do {
                ACFCity city = new ACFCity();
                city.setId(cityCursor.getInt(0));
                city.setDeviceId(cityCursor.getInt(1));
                city.setCityName(cityCursor.getString(2));
                Location location = new Location("City");
                location.setLongitude(cityCursor.getDouble(3));
                location.setLatitude(cityCursor.getDouble(4));
                city.setLocation(location);
                city.setCountryName(cityCursor.getString(5));
                city.setContinentName(cityCursor.getString(6));
                city.setCountryId(cityCursor.getInt(7));
                cities.add(city);
            } while (cityCursor.moveToNext());
        }
        cityCursor.close();
        db.close();
        return cities;
    }

    public void updateGroupVisibility(ACFCityGroup group){
        SQLiteDatabase db = getWritableDatabase();
        try{
            db.beginTransaction();
                ContentValues values = new ContentValues();
                values.put(KEY_GROUP_VISIBLE, group.isVisible());
                db.update(TABLE_GROUP, values, KEY_GROUP_ID + " = ?",
                        new String[] { String.valueOf(group.getId()) });
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    public int getDeviceId() {
        int deviceId = 0;
        SQLiteDatabase db = getReadableDatabase();

        String selectQuery = "SELECT " + KEY_DEVICE_ID
                + " FROM " + TABLE_DEVICE
                + " WHERE " + KEY_DEVICE_ID + " <> " + 1
                + ";";

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            deviceId = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return deviceId;
    }

    public ACFCityGroup getGroup(int group_id) {
        ACFCityGroup group = null;
        SQLiteDatabase db = getReadableDatabase();

        String selectGroupQuery = "SELECT " + KEY_GROUP_ID + "," + KEY_GROUP_NAME + "," + KEY_GROUP_VISIBLE + "," + KEY_GROUP_DEVICE
                + " FROM " + TABLE_GROUP
                + " WHERE " + KEY_GROUP_ID + " = " + group_id
                + ";";

        Cursor groupCursor = db.rawQuery(selectGroupQuery, null);
        if (groupCursor.moveToFirst()) {
            do {
                group = new ACFCityGroup();
                group.setId(groupCursor.getInt(0));
                group.setName(groupCursor.getString(1));
                group.setVisible(groupCursor.getInt(2) == 1);
                group.setDeviceId(groupCursor.getInt(3));
            } while (groupCursor.moveToNext());
        }
        groupCursor.close();

        String selectMembersQuery = "SELECT " + KEY_CITY_ID + "," + KEY_CITY_DEVICE + "," + KEY_CITY_NAME + "," + KEY_CITY_LONGITUDE + "," + KEY_CITY_LATITUDE + "," + KEY_COUNTRY_NAME + "," + KEY_CONTINENT_NAME + "," + KEY_COUNTRY_ID
                + " FROM " + TABLE_GROUP + " JOIN " + TABLE_CITY_GROUP + " ON " + KEY_GROUP_ID + " =" + KEY_CITY_GROUP_GROUP
                + " JOIN " + TABLE_CITY + " ON " + KEY_CITY_GROUP_CITY + " =" + KEY_CITY_ID
                + " JOIN " + TABLE_COUNTRY + " ON " + KEY_CITY_COUNTRY + " =" + KEY_COUNTRY_ID
                + " JOIN " + TABLE_CONTINENT + " ON " + KEY_COUNTRY_CONTINENT + " =" + KEY_CONTINENT_ID
                + " WHERE " + KEY_GROUP_ID + " = " + group_id + ";";
        Cursor cityCursor = db.rawQuery(selectMembersQuery, null);
        if (cityCursor.moveToFirst()) {
            do {
                ACFCity city = new ACFCity();
                city.setId(cityCursor.getInt(0));
                city.setDeviceId(cityCursor.getInt(1));
                city.setCityName(cityCursor.getString(2));
                Location location = new Location("City");
                location.setLongitude(cityCursor.getDouble(3));
                location.setLatitude(cityCursor.getDouble(4));
                city.setLocation(location);
                city.setCountryName(cityCursor.getString(5));
                city.setContinentName(cityCursor.getString(6));
                city.setCountryId(cityCursor.getInt(7));
                group.getCityList().add(city);
            } while (cityCursor.moveToNext());
        }
        cityCursor.close();
        db.close();
        return group;
    }

    public void addDevice(String result) throws JSONException {
        ACFJSONHandler jsonHandler = new ACFJSONHandler(context);
        JsonElement jelement = new JsonParser().parse(result);
        JsonObject jobject = jelement.getAsJsonObject();
        JsonElement success = jobject.get("success");
        if(!success.getAsBoolean()){
            throw new JSONException("success = false");
        }
        jobject = jobject.getAsJsonObject("data");
        if(!db.isOpen()){
            db = getWritableDatabase();
        }
        try{
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(KEY_DEVICE_ID, jobject.get("deviceId").getAsInt());
            values.put(KEY_DEVICE_REGISTRATION_DATE, jobject.get("creationDate").getAsString());
            db.insert(TABLE_DEVICE, null, values);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public List<ACFCountry> getAllCountries(){
        List<ACFCountry> countryList = new ArrayList<ACFCountry>();
        String selectQuery = "SELECT " + KEY_COUNTRY_ID + "," + KEY_COUNTRY_NAME + "," + KEY_COUNTRY_CODE
                + " FROM " + TABLE_COUNTRY + ";";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ACFCountry country = new ACFCountry();
                country.setId(cursor.getInt(0));
                country.setName(cursor.getString(1));
                country.setCode(cursor.getString(2));
                countryList.add(country);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return countryList;
    }

    public ACFCity getCity(int id) {
        SQLiteDatabase db = getReadableDatabase();
        ACFCity city = null;
        String selectVisibleCitiesQuery = "SELECT " + KEY_CITY_ID + "," + KEY_CITY_DEVICE + "," + KEY_CITY_NAME + "," + KEY_CITY_LONGITUDE + "," + KEY_CITY_LATITUDE + "," + KEY_COUNTRY_NAME + "," + KEY_CONTINENT_NAME + "," + KEY_COUNTRY_ID
                + " FROM " + TABLE_CITY
                + " JOIN " + TABLE_COUNTRY + " ON " + KEY_CITY_COUNTRY + " =" + KEY_COUNTRY_ID
                + " JOIN " + TABLE_CONTINENT + " ON " + KEY_COUNTRY_CONTINENT + " =" + KEY_CONTINENT_ID
                + " WHERE " + KEY_CITY_ID + " = "+ id + ";";
        Cursor cityCursor = db.rawQuery(selectVisibleCitiesQuery, null);
        if (cityCursor.moveToFirst()) {
            do {
                city = new ACFCity();
                city.setId(cityCursor.getInt(0));
                city.setDeviceId(cityCursor.getInt(1));
                city.setCityName(cityCursor.getString(2));
                Location location = new Location("City");
                location.setLongitude(cityCursor.getDouble(3));
                location.setLatitude(cityCursor.getDouble(4));
                city.setLocation(location);
                city.setCountryName(cityCursor.getString(5));
                city.setContinentName(cityCursor.getString(6));
                city.setCountryId(cityCursor.getInt(7));
            } while (cityCursor.moveToNext());
        }
        cityCursor.close();
        db.close();
        return city;
    }

    public ACFCountry getCountry(int id) {
        SQLiteDatabase db = getReadableDatabase();
        ACFCountry country = null;
        String selectVisibleCitiesQuery = "SELECT " + KEY_COUNTRY_ID + "," + KEY_COUNTRY_CODE + "," + KEY_COUNTRY_NAME
                + " FROM " + TABLE_COUNTRY
                + " WHERE " + KEY_COUNTRY_ID + " = "+ id + ";";
        Cursor cityCursor = db.rawQuery(selectVisibleCitiesQuery, null);
        if (cityCursor.moveToFirst()) {
                country = new ACFCountry();
                country.setId(cityCursor.getInt(0));
                country.setCode(cityCursor.getString(1));
                country.setName(cityCursor.getString(2));
        }
        cityCursor.close();
        db.close();
        return country;
    }
}
