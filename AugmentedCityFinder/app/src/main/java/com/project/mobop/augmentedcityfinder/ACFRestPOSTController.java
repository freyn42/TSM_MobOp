package com.project.mobop.augmentedcityfinder;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
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
 * Created by tom on 06.04.2015.
 */
public class ACFRestPOSTController {

    private final Context context;

    public ACFRestPOSTController(Context context){
        this.context = context;
    }

    public String postDeviceToServer(){
        String result = "";
        String url = "http://acf-mobop.rhcloud.com/rest/device";
        result = POST(url,null);
        return result;
    }

    public String postGroupToServer(ACFCityGroup group) {
        String result = "";
        String url = "http://acf-mobop.rhcloud.com/rest/group";
        ACFJSONHandler jsonHandler = new ACFJSONHandler(context);
        String jsonString = jsonHandler.getJSONPostString(group);
        PostAsyncTask task = new PostAsyncTask(jsonString);
        task.execute(url);
        try {
            result = task.get(10000,TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Toast.makeText(context, "InterruptedException in POST to Server",Toast.LENGTH_SHORT).show();
        } catch (ExecutionException e) {
            Toast.makeText(context, "ExecutionException in POST to Server",Toast.LENGTH_SHORT).show();
        } catch (TimeoutException e) {
            Toast.makeText(context, "Timeout in POST to Server",Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    public String postCityToServer(ACFCity city) {
        String result = "";
        String url = "http://acf-mobop.rhcloud.com/rest/city";
        ACFJSONHandler jsonHandler = new ACFJSONHandler(context);
        String jsonString = jsonHandler.getJSONPostString(city);
        PostAsyncTask task = new PostAsyncTask(jsonString);
        task.execute(url);
        try {
            result = task.get(10000,TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Toast.makeText(context, "InterruptedException in POST to Server",Toast.LENGTH_SHORT).show();
        } catch (ExecutionException e) {
            Toast.makeText(context, "ExecutionException in POST to Server",Toast.LENGTH_SHORT).show();
        } catch (TimeoutException e) {
            Toast.makeText(context, "Timeout in POST to Server",Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    public int postUsageLocation(Location location) {
        String result = "";
        String url = "http://acf-mobop.rhcloud.com/rest/usage/userlocation";
        ACFJSONHandler jsonHandler = new ACFJSONHandler(context);
        String jsonString = jsonHandler.getJSONPostStringForUsage(location);
        PostAsyncTask task = new PostAsyncTask(jsonString);
        task.execute(url);
        try {
            result = task.get(10000,TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            Log.e("ACFRestPOSTController",e.getMessage());
        }
        int id = 0;
        try {
            id = jsonHandler.getUsageLocationId(result);
        } catch (JSONException e) {
            Log.e("ACFRestPOSTController",e.getMessage());
        }
        return id;
    }

    public boolean postUsageLocationCity(int usageLocationId, ACFCity city) {
        String result = "";
        String url = "http://acf-mobop.rhcloud.com/rest/usage/city";
        ACFJSONHandler jsonHandler = new ACFJSONHandler(context);
        String jsonString = jsonHandler.getJSONPostStringForUsage(usageLocationId,city);
        PostAsyncTask task = new PostAsyncTask(jsonString);
        task.execute(url);
        try {
            result = task.get(10000,TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            Log.e("ACFRestPOSTController",e.getMessage());
        }
        return jsonHandler.checkSuccess(result);
    }

    public String POST(String url,String jsonString){
        InputStream inputStream = null;
        String result = "";
        try {

            HttpClient httpclient = new DefaultHttpClient();

            HttpPost postRequest = new HttpPost(url);

            if(jsonString != null){
                postRequest.setEntity(new StringEntity(jsonString));
                postRequest.setHeader("Content-type", "application/json");
            }

            HttpResponse httpResponse = httpclient.execute(postRequest);

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

    private class PostAsyncTask extends AsyncTask<String, Void, String> {

        private String values;

        public PostAsyncTask(String values){
            this.values = values;
        }

        @Override
        protected String doInBackground(String... urls) {
            return POST(urls[0],values);
        }

    }

}
