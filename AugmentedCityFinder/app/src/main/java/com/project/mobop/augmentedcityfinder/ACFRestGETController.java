package com.project.mobop.augmentedcityfinder;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by tom on 05.04.2015.
 */
public class ACFRestGETController {

    private final Context context;

    public ACFRestGETController(Context context){
        this.context = context;
    }

    public String getContinentsFromServer() throws JSONException {
        String result = GET("http://acf-mobop.rhcloud.com/rest/continent");
        return result;
    }

    public String getCountriesFromServer() throws JSONException {
        String result = GET("http://acf-mobop.rhcloud.com/rest/country");
        return result;
    }

    public String getCitiesFromServer() throws JSONException {
        String result = GET("http://acf-mobop.rhcloud.com/rest/city");
        return result;
    }

    public String getGroupsFromServer() throws JSONException {
        String result = GET("http://acf-mobop.rhcloud.com/rest/group");
        return result;
    }

    public String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {

            HttpClient httpclient = new DefaultHttpClient();

            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            inputStream = httpResponse.getEntity().getContent();

            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
        }
        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
}
