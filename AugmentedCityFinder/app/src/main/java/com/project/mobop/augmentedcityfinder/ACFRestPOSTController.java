package com.project.mobop.augmentedcityfinder;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by tom on 06.04.2015.
 */
public class ACFRestPOSTController {

    private final Context context;
    private ACFCitiesDatabaseController dbController;

    public ACFRestPOSTController(Context context, ACFCitiesDatabaseController dbController){
        this.context = context;
        this.dbController = dbController;
    }

    public String postDeviceToServer() {
        String result = "";
        String url = "http://acf-mobop.rhcloud.com/rest/device";
        PostAsyncTask task = new PostAsyncTask(null);
        task.execute(url);
        try {
            result = task.get(5000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            ((Activity)context).finish();
        } catch (ExecutionException e) {
            ((Activity)context).finish();
        } catch (TimeoutException e) {
            ((Activity)context).finish();
        }
        return result;
    }

    public String postGroupToServer(ACFCityGroup group) {
        String result = "";
        String url = "http://acf-mobop.rhcloud.com/rest/group";
        ACFJSONHandler jsonHandler = new ACFJSONHandler(context);
        String jsonString = jsonHandler.getJSONString(group);
        PostAsyncTask task = new PostAsyncTask(jsonString);
        task.execute(url);
        try {
            result = task.get();
        } catch (InterruptedException e) {
            ((Activity)context).finish();
        } catch (ExecutionException e) {
            ((Activity)context).finish();
        } /*catch (TimeoutException e) {
            ((Activity)context).finish();
        }*/
        return result;
    }

    public String POST(String url,String jsonString){
        InputStream inputStream = null;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make POST request to the given URL
            HttpPost postRequest = new HttpPost(url);

            if(jsonString != null){
                postRequest.setEntity(new StringEntity(jsonString));
                postRequest.setHeader("Content-type", "application/json");
            }

            HttpResponse httpResponse = httpclient.execute(postRequest);

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getMessage());
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

    private class PostAsyncTask extends AsyncTask<String, Void, String> {

        private String values;

        public PostAsyncTask(String values){
            this.values = values;
        }

        @Override
        protected String doInBackground(String... urls) {
            return POST(urls[0],values);
        }

        public String getValues() {
            return values;
        }

        public void setValues(String values) {
            this.values = values;
        }
    }

}
