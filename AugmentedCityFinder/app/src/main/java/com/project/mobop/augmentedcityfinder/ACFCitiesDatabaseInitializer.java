package com.project.mobop.augmentedcityfinder;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;

import java.util.Date;

/**
 * Created by tom on 09.04.2015.
 */
public class ACFCitiesDatabaseInitializer {

    private Context context;
    private ACFCitiesDatabaseController dbController;

    public ACFCitiesDatabaseInitializer(Context context, ACFCitiesDatabaseController dbController){
        this.context = context;
        this.dbController = dbController;
    }

    public void initializeDB() throws JSONException, ACFDatabaseException {
        String response;
        ACFRestPOSTController postController = new ACFRestPOSTController(context);

        response = postController.postDeviceToServer();
        addDevice(response);

        addSystemDevice();

        ACFRestGETController getController = new ACFRestGETController(context);
        response = getController.getContinentsFromServer();
        try {
            addContinents(response);
        } catch (JSONException e) {
            throw new JSONException("Fehler beim Empfang der Kontinentdaten");
        }

        response = getController.getCountriesFromServer();
        try {
            addCountries(response);
        } catch (JSONException e) {
            throw new JSONException("Fehler beim Empfang der Länderdaten");
        }

        response = getController.getCitiesFromServer();
        try {
            addCities(response);
        } catch (JSONException e) {
            throw new JSONException("Fehler beim Empfang der Städtedaten");
        }

        response = getController.getGroupsFromServer();
        try {
            addGroups(response);
        } catch (JSONException e) {
            throw new JSONException("Fehler beim Empfang der Gruppendaten");
        }
    }

    public void addDevice(String result) throws JSONException, ACFDatabaseException {
        try{
            SQLiteDatabase db = dbController.getWritableDatabase();
            JsonElement jelement = new JsonParser().parse(result);
            JsonObject jobject = jelement.getAsJsonObject();
            JsonElement success = jobject.get("success");
            if(!success.getAsBoolean()){
                throw new JSONException("success = false");
            }
            jobject = jobject.getAsJsonObject("data");

            try{
                db.beginTransaction();
                ContentValues values = new ContentValues();
                values.put(ACFCitiesDatabase.KEY_DEVICE_ID, jobject.get("deviceId").getAsInt());
                values.put(ACFCitiesDatabase.KEY_DEVICE_REGISTRATION_DATE, jobject.get("creationDate").getAsString());
                db.insert(ACFCitiesDatabase.TABLE_DEVICE, null, values);
                db.setTransactionSuccessful();
            } catch(Exception any){
                throw new ACFDatabaseException("Fehler beim Speichern des Systemgeräts");
            } finally {
                db.endTransaction();
            }
        } catch(JsonSyntaxException e){
            throw new JSONException("");
        } catch(Exception any){
            throw new ACFDatabaseException("Fehler beim Speichern des Systemgeräts");
        }
    }

    public void addContinents(String result) throws JSONException, ACFDatabaseException {
        SQLiteDatabase db = dbController.getWritableDatabase();
        ACFJSONHandler jsonHandler = new ACFJSONHandler(context);
        JsonArray continentArray = jsonHandler.getJSONArray(result, "continents");
        try{
            db.beginTransaction();
            for(int i = 0; i < continentArray.size(); i++){
                JsonObject jobject = continentArray.get(i).getAsJsonObject();
                ContentValues values = new ContentValues();
                values.put(ACFCitiesDatabase.KEY_CONTINENT_ID, jobject.get("continentId").getAsInt());
                values.put(ACFCitiesDatabase.KEY_CONTINENT_NAME, jobject.get("name").getAsString());
                db.insert(ACFCitiesDatabase.TABLE_CONTINENT, null, values);
            }
            db.setTransactionSuccessful();
        } catch(Exception any){
            throw new ACFDatabaseException("Fehler beim Speichern der Kontinente");
        } finally {
            db.endTransaction();
        }
    }

    public void addCountries(String result) throws JSONException, ACFDatabaseException {
        SQLiteDatabase db = dbController.getWritableDatabase();
        ACFJSONHandler jsonHandler = new ACFJSONHandler(context);
        JsonArray countryArray = jsonHandler.getJSONArray(result, "countries");
        try{
            db.beginTransaction();
            for(int i = 0; i < countryArray.size(); i++){
                JsonObject jobject = countryArray.get(i).getAsJsonObject();
                ContentValues values = new ContentValues();
                values.put(ACFCitiesDatabase.KEY_COUNTRY_ID, jobject.get("countryId").getAsInt());
                values.put(ACFCitiesDatabase.KEY_COUNTRY_CONTINENT, jobject.get("continentId").getAsInt());
                values.put(ACFCitiesDatabase.KEY_COUNTRY_CODE, jobject.get("code").getAsString());
                values.put(ACFCitiesDatabase.KEY_COUNTRY_NAME, jobject.get("name").getAsString());
                db.insert(ACFCitiesDatabase.TABLE_COUNTRY, null, values);
            }
            db.setTransactionSuccessful();
        } catch(Exception any){
            throw new ACFDatabaseException("Fehler beim Speichern der Länder");
        } finally {
            db.endTransaction();
        }
    }

    public void addSystemDevice() throws ACFDatabaseException {
        SQLiteDatabase db = dbController.getWritableDatabase();
        try{
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(ACFCitiesDatabase.KEY_DEVICE_ID, 1);
            values.put(ACFCitiesDatabase.KEY_DEVICE_REGISTRATION_DATE, String.valueOf(new Date()));
            db.insert(ACFCitiesDatabase.TABLE_DEVICE, null, values);
            db.setTransactionSuccessful();
        } catch(Exception any){
            throw new ACFDatabaseException("Fehler beim Speichern des Systemgeräts");
        } finally {
            db.endTransaction();
        }
    }

    public void addCities(String result) throws JSONException, ACFDatabaseException {
        SQLiteDatabase db = dbController.getWritableDatabase();
        ACFJSONHandler jsonHandler = new ACFJSONHandler(context);
        JsonArray cityArray = jsonHandler.getJSONArray(result, "cities");
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
                db.insert(ACFCitiesDatabase.TABLE_CITY, null, values);
            }
            db.setTransactionSuccessful();
        } catch(Exception any){
            throw new ACFDatabaseException("Fehler beim Speichern der Städte");
        } finally {
            db.endTransaction();
        }
    }

    public void addGroups(String result) throws JSONException, ACFDatabaseException {
        SQLiteDatabase db = dbController.getWritableDatabase();
        ACFJSONHandler jsonHandler = new ACFJSONHandler(context);
        JsonArray groupArray = jsonHandler.getJSONArray(result, "groupList");
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
                if(db.insert(ACFCitiesDatabase.TABLE_GROUP, null, values) == -1){
                    throw new JSONException("");
                }
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

}
