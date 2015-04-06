package com.project.mobop.augmentedcityfinder;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
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

/**
 * Created by tom on 05.04.2015.
 */
public class ACFRestGETController {

    private final Context context;
    private ACFCitiesDatabaseController dbController;

    public ACFRestGETController(Context context, ACFCitiesDatabaseController dbController){
        this.context = context;
        this.dbController = dbController;
    }

    public void getContinentsFromServer(){
        new GetContinentsAsyncTask().execute("http://acf-mobop.rhcloud.com/rest/continent");
    }

    public void getCountriesFromServer(){
        new GetCountriesAsyncTask().execute("http://acf-mobop.rhcloud.com/rest/country");
    }

    public void getCitiesFromServer(){
        new GetCitiesAsyncTask().execute("http://acf-mobop.rhcloud.com/rest/city");
    }

    public void getGroupsFromServer(){
        new GetGroupsAsyncTask().execute("http://acf-mobop.rhcloud.com/rest/group");
    }

    public String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
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

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    private class GetContinentsAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return GET(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                dbController.addContinents(result);
                Toast.makeText(context, "Kontinente empfangen", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                Toast.makeText(context, "Fehler beim Empfang der Kontinentdaten", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class GetCountriesAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                dbController.addCountries(result);
                Toast.makeText(context, "Länder empfangen", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                Toast.makeText(context, "Fehler beim Empfang der Länderdaten", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class GetCitiesAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                dbController.addCities(result);
                Toast.makeText(context, "Städte empfangen", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                Toast.makeText(context, "Fehler beim Empfang der Städtedaten", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class GetGroupsAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                dbController.addGroups(result);
                Toast.makeText(context, "Gruppen empfangen", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                Toast.makeText(context, "Fehler beim Empfang der Gruppendaten", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
