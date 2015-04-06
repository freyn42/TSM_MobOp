package com.project.mobop.augmentedcityfinder;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created by tom on 22.03.2015.
 */
public class ACFJSONHandler {

    private Context context;

    public ACFJSONHandler(Context context){
        this.context = context;
    }

    public void importCity(JSONObject city){

    }

    public void importCitiesFromFile(SQLiteDatabase db) throws IOException{
        InputStream is = context.getResources().openRawResource(R.raw.cities_europe);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } finally {
            is.close();
        }

        ACFCitiesDatabaseController dbController = new ACFCitiesDatabaseController(context);

        String jsonString = writer.toString();

        JsonElement jelement = new JsonParser().parse(jsonString);
        JsonObject jobject = jelement.getAsJsonObject();
        //jobject = jobject.getAsJsonObject("cities");
        JsonArray jarray = jobject.getAsJsonArray("cities");
        for(int i = 0; i < jarray.size(); i++){
            jobject = jarray.get(i).getAsJsonObject();
            ACFCity city = new ACFCity();
            city.setCityName(jobject.get("city_name").getAsString());
            city.setCountryName(jobject.get("city_country").getAsString());
            Location location = new Location("city");
            location.setLatitude(jobject.get("city_latitude").getAsDouble());
            location.setLongitude(jobject.get("city_longitude").getAsDouble());
            city.setLocation(location);
            city.setContinentName(jobject.get("city_continent").getAsString());
            dbController.addCity(city,db);
        }
    }

    public JsonArray getJSONArray(String jsonString, String arrayName) throws JSONException{
        JsonElement jelement = new JsonParser().parse(jsonString);
        JsonObject jobject = jelement.getAsJsonObject();
        JsonElement success = jobject.get("success");
        if(!success.getAsBoolean()){
            throw new JSONException("success = false");
        }

        jobject = jobject.getAsJsonObject("data");
        JsonArray jarray = jobject.getAsJsonArray(arrayName);
        return jarray;
    }

    public String getJSONString(ACFCityGroup group){
        JsonObject jsonobj = new JsonObject();
        jsonobj.addProperty("deviceId", group.getDeviceId());
        jsonobj.addProperty("name", group.getName());
        JsonArray array = new JsonArray();
        for (ACFCity city : group.getCityList()) {
            array.add(new JsonPrimitive(city.getId()));
        }
        jsonobj.add("members",array);
        return jsonobj.toString();
    }
}
