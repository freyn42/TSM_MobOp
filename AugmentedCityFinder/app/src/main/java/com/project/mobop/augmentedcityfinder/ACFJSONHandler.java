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
import com.google.gson.JsonSyntaxException;

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

    public JsonArray getJSONArray(String jsonString, String arrayName) throws JSONException{
        try{
            JsonElement jelement = new JsonParser().parse(jsonString);
            JsonObject jobject = jelement.getAsJsonObject();
            JsonElement success = jobject.get("success");
            if(!success.getAsBoolean()){
                throw new JSONException("success = false");
            }

            jobject = jobject.getAsJsonObject("data");
            JsonArray jarray = jobject.getAsJsonArray(arrayName);
            return jarray;
        } catch(JsonSyntaxException e){
            throw new JSONException("");
        } catch(Exception any){
            throw new JSONException("");
        }

    }

    public String getJSONPostString(ACFCityGroup group){
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

    public String getJSONPutString(ACFCityGroup group){
        JsonObject jsonobj = new JsonObject();
        jsonobj.addProperty("id", group.getId());
        jsonobj.addProperty("name", group.getName());
        JsonArray array = new JsonArray();
        for (ACFCity city : group.getCityList()) {
            array.add(new JsonPrimitive(city.getId()));
        }
        jsonobj.add("members",array);
        return jsonobj.toString();
    }

    public String getJSONPostString(ACFCity city){
        JsonObject jsonobj = new JsonObject();
        jsonobj.addProperty("deviceId", city.getDeviceId());
        jsonobj.addProperty("name", city.getCityName());
        jsonobj.addProperty("longitude", city.getLongitude());
        jsonobj.addProperty("latitude", city.getLatitude());
        jsonobj.addProperty("countryId", city.getCountryId());
        return jsonobj.toString();
    }

    public String getJSONPutString(ACFCity city){
        JsonObject jsonobj = new JsonObject();
        jsonobj.addProperty("name", city.getCityName());
        jsonobj.addProperty("longitude", city.getLongitude());
        jsonobj.addProperty("latitude", city.getLatitude());
        jsonobj.addProperty("countryId", city.getCountryId());
        return jsonobj.toString();
    }

    public boolean checkSuccess(String jsonString){
        JsonElement jelement = new JsonParser().parse(jsonString);
        JsonObject jobject = jelement.getAsJsonObject();
        JsonElement success = jobject.get("success");
        if(success.getAsBoolean()){
            return true;
        } else {
            return false;
        }
    }

    public String getJSONPostStringForUsage(Location location) {
        ACFCitiesDatabaseController dbController = new ACFCitiesDatabaseController(context);
        JsonObject jsonobj = new JsonObject();
        jsonobj.addProperty("deviceId",dbController.getDeviceId());
        jsonobj.addProperty("latitude",location.getLatitude());
        jsonobj.addProperty("longitude",location.getLongitude());
        return jsonobj.toString();
    }

    public int getUsageLocationId(String jsonString) throws JSONException{
        JsonElement jelement = new JsonParser().parse(jsonString);
        JsonObject jobject = jelement.getAsJsonObject();
        JsonElement success = jobject.get("success");
        if(!success.getAsBoolean()){
            throw new JSONException("success = false");
        }
        jobject = jobject.getAsJsonObject("data");
        return jobject.get("userLocationId").getAsInt();
    }

    public String getJSONPostStringForUsage(int usageLocationId, ACFCity city) {
        ACFCitiesDatabaseController dbController = new ACFCitiesDatabaseController(context);
        JsonObject jsonobj = new JsonObject();
        jsonobj.addProperty("userLocationId",usageLocationId);
        jsonobj.addProperty("cityId",city.getId());
        return jsonobj.toString();
    }
}
