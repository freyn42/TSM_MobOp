package com.project.mobop.augmentedcityfinder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tom on 22.03.2015.
 */
public class ACFCitiesDatabaseController extends SQLiteOpenHelper {

    private Context context;
    private ACFCitiesDatabaseCreator dbCreator;
    ACFCitiesDatabaseInitializer dbInitializer;
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 3;

    // Database Name
    private static final String DATABASE_NAME = "acf";

    public ACFCitiesDatabaseController(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        dbInitializer = new ACFCitiesDatabaseInitializer(context, this);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        dbCreator = new ACFCitiesDatabaseCreator();
        dbCreator.createContinentTable(db);
        dbCreator.createCountryTable(db);
        dbCreator.createDeviceTable(db);
        dbCreator.createCityTable(db);
        dbCreator.createGroupTable(db);
        dbCreator.createCityGroupTable(db);
        dbCreator.createUserLocationTable(db);
        dbCreator.createUserLocationCityTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ACFCitiesDatabase.TABLE_CONTINENT + ";");
        db.execSQL("DROP TABLE IF EXISTS " + ACFCitiesDatabase.TABLE_COUNTRY + ";");
        db.execSQL("DROP TABLE IF EXISTS " + ACFCitiesDatabase.TABLE_CITY + ";");
        db.execSQL("DROP TABLE IF EXISTS " + ACFCitiesDatabase.TABLE_GROUP + ";");
        db.execSQL("DROP TABLE IF EXISTS " + ACFCitiesDatabase.TABLE_CITY_GROUP + ";");
        db.execSQL("DROP TABLE IF EXISTS " + ACFCitiesDatabase.TABLE_DEVICE + ";");
        db.execSQL("DROP TABLE IF EXISTS " + ACFCitiesDatabase.TABLE_USER_LOCATION + ";");
        db.execSQL("DROP TABLE IF EXISTS " + ACFCitiesDatabase.TABLE_USER_LOCATION_CITY + ";");
        onCreate(db);
    }

    public boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            String path = context.getDatabasePath(DATABASE_NAME).getAbsolutePath();
            checkDB = SQLiteDatabase.openDatabase(path, null,
                    SQLiteDatabase.OPEN_READONLY);
            checkDB.close();
        } catch (SQLiteException e) {
        }
        return checkDB != null ? true : false;
    }

    public void initializeLocalDB() throws JSONException, ACFDatabaseException {
        dbInitializer.initializeDB();
    }

    public void addCities(String jsonString) throws JSONException, ACFDatabaseException {
        dbInitializer.addCities(jsonString);
    }

    public void addGroups(String jsonString) throws JSONException, ACFDatabaseException {
        dbInitializer.addGroups(jsonString);
    }

    public ACFCity getCity(int id) {
        SQLiteDatabase db = getReadableDatabase();
        ACFCity city = null;
        String selectCityQuery = ACFCitiesDatabase.SELECT_CITY + id;
        Cursor cityCursor = db.rawQuery(selectCityQuery, null);
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
    public int getDeviceId() {
        int deviceId = 0;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(ACFCitiesDatabase.SELECT_DEVICE_ID, null);
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
        String selectGroupQuery = ACFCitiesDatabase.SELECT_GROUP + group_id;
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
        Cursor cityCursor = db.rawQuery(ACFCitiesDatabase.SELECT_CITIES_FROM_GROUP + group_id, null);
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


    public ACFCountry getCountry(int id) {
        SQLiteDatabase db = getReadableDatabase();
        ACFCountry country = null;
        String selectCountryQuery = ACFCitiesDatabase.SELECT_COUNTRY + id;
        Cursor cityCursor = db.rawQuery(selectCountryQuery, null);
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

    public List<ACFCity> getAllCities(){
        List<ACFCity> citiesList = new ArrayList<ACFCity>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(ACFCitiesDatabase.SELECT_ALL_CITIES, null);
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

    public List<ACFCity> getAllVisibleCities() {
        SQLiteDatabase db = getReadableDatabase();
        List<ACFCity> cities = new ArrayList<ACFCity>();
        Cursor cityCursor = db.rawQuery(ACFCitiesDatabase.SELECT_VISIBLE_CITIES, null);
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

    public List<ACFCityGroup> getGroups() {
        List<ACFCityGroup> groups = new ArrayList<ACFCityGroup>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor groupCursor = db.rawQuery(ACFCitiesDatabase.SELECT_ALL_GROUPS, null);
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
            String selectMembersQuery = ACFCitiesDatabase.SELECT_CITIES_FROM_GROUP + group.getId();
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

    public List<ACFCountry> getAllCountries(){
        List<ACFCountry> countryList = new ArrayList<ACFCountry>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(ACFCitiesDatabase.SELECT_ALL_COUNTRIES, null);

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

    public void updateCities(String result) throws JSONException, ACFDatabaseException {
        ACFJSONHandler jsonHandler = new ACFJSONHandler(context);
        JsonArray cityArray = jsonHandler.getJSONArray(result, "cities");
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.beginTransaction();
            for (int i = 0; i < cityArray.size(); i++) {
                JsonObject jobject = cityArray.get(i).getAsJsonObject();
                ContentValues values = new ContentValues();
                values.put(ACFCitiesDatabase.KEY_CITY_ID, jobject.get("id").getAsInt());
                values.put(ACFCitiesDatabase.KEY_CITY_DEVICE, jobject.get("deviceId").getAsInt());
                values.put(ACFCitiesDatabase.KEY_CITY_LONGITUDE, jobject.get("longitude").getAsDouble());
                values.put(ACFCitiesDatabase.KEY_CITY_LATITUDE, jobject.get("latitude").getAsDouble());
                values.put(ACFCitiesDatabase.KEY_CITY_COUNTRY, jobject.get("countryId").getAsInt());
                values.put(ACFCitiesDatabase.KEY_CITY_NAME, jobject.get("name").getAsString());
                db.update(ACFCitiesDatabase.TABLE_CITY, values, ACFCitiesDatabase.KEY_CITY_ID + " = ?",
                        new String[]{String.valueOf(jobject.get("id").getAsInt())});
            }
            db.setTransactionSuccessful();
        } catch(Exception any){
                throw new ACFDatabaseException("Fehler beim Speichern der Städte");
        } finally {
            db.endTransaction();
        }
    }

    public void updateGroups(String result) throws JSONException, ACFDatabaseException {
        ACFJSONHandler jsonHandler = new ACFJSONHandler(context);
        JsonArray groupArray = jsonHandler.getJSONArray(result, "groupList");
        SQLiteDatabase db = getWritableDatabase();
        try{
            db.beginTransaction();
            for(int i = 0; i < groupArray.size(); i++){
                JsonObject jobject = groupArray.get(i).getAsJsonObject();
                ContentValues values = new ContentValues();
                values.put(ACFCitiesDatabase.KEY_GROUP_ID, jobject.get("id").getAsInt());
                values.put(ACFCitiesDatabase.KEY_GROUP_CREATION_DATE, jobject.get("creationDate").getAsString());
                values.put(ACFCitiesDatabase.KEY_GROUP_MODIFICATION_DATE, jobject.get("modificationDate").getAsString());
                values.put(ACFCitiesDatabase.KEY_GROUP_DEVICE, jobject.get("deviceId").getAsInt());
                values.put(ACFCitiesDatabase.KEY_GROUP_NAME, jobject.get("name").getAsString());
                db.update(ACFCitiesDatabase.TABLE_GROUP, values, ACFCitiesDatabase.KEY_GROUP_ID + " = ?", new String[] { String.valueOf(jobject.get("id").getAsInt()) });

                db.delete(ACFCitiesDatabase.TABLE_CITY_GROUP,ACFCitiesDatabase.KEY_CITY_GROUP_GROUP + " = ?",new String[] { String.valueOf(jobject.get("id").getAsInt()) });

                JsonArray memberArray = jobject.getAsJsonArray("members");
                for(int j = 0; j < memberArray.size(); j++){
                    ContentValues memberValues = new ContentValues();
                    memberValues.put(ACFCitiesDatabase.KEY_CITY_GROUP_CITY,memberArray.get(j).getAsInt());
                    memberValues.put(ACFCitiesDatabase.KEY_CITY_GROUP_GROUP,jobject.get("id").getAsInt());
                    db.insert(ACFCitiesDatabase.TABLE_CITY_GROUP, null, memberValues);
                }
            }
            db.setTransactionSuccessful();
        } catch(Exception any){
            throw new ACFDatabaseException("Fehler beim Speichern der Gruppen");
        } finally {
            db.endTransaction();
        }
    }

    public void updateGroupVisibility(ACFCityGroup group) throws ACFDatabaseException {
        SQLiteDatabase db = getWritableDatabase();
        try{
            db.beginTransaction();
                ContentValues values = new ContentValues();
                values.put(ACFCitiesDatabase.KEY_GROUP_VISIBLE, group.isVisible());
                db.update(ACFCitiesDatabase.TABLE_GROUP, values, ACFCitiesDatabase.KEY_GROUP_ID + " = ?",
                        new String[] { String.valueOf(group.getId()) });
            db.setTransactionSuccessful();
        } catch(Exception any){
            throw new ACFDatabaseException("Fehler beim Speichern der Gruppen");
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    public void deleteCity(ACFCity city) throws ACFDatabaseException {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(ACFCitiesDatabase.KEY_CITY_DELETED, 1);
            db.update(ACFCitiesDatabase.TABLE_CITY, values, ACFCitiesDatabase.KEY_CITY_ID + " = ?",
                    new String[]{String.valueOf(city.getId())});
            db.setTransactionSuccessful();
        } catch(Exception any){
            throw new ACFDatabaseException("Fehler beim Löschen der Stadt");
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    public void deleteGroup(ACFCityGroup group) throws ACFDatabaseException {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(ACFCitiesDatabase.KEY_GROUP_DELETED, 1);
            db.update(ACFCitiesDatabase.TABLE_GROUP, values, ACFCitiesDatabase.KEY_GROUP_ID + " = ?",
                    new String[]{String.valueOf(group.getId())});
            db.setTransactionSuccessful();
        } catch(Exception any){
            throw new ACFDatabaseException("Fehler beim Löschen der Gruppe");
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    public boolean deleteDatabase() {
        boolean deleted = false;
        try {
            File path = context.getDatabasePath(DATABASE_NAME);
            deleted = SQLiteDatabase.deleteDatabase(path);
        } catch (SQLiteException e) {
            // database doesn't exist yet.
        }
        return deleted;
    }
}
