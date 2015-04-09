package com.project.mobop.augmentedcityfinder;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by tom on 07.04.2015.
 */
public class ACFRestPUTController {

    private final Context context;

    public ACFRestPUTController(Context context){
        this.context = context;
    }

    public String putGroupToServer(ACFCityGroup group) {
        String result = "";
        String url = "http://acf-mobop.rhcloud.com/rest/group/"+group.getDeviceId()+"/"+group.getId();
        ACFJSONHandler jsonHandler = new ACFJSONHandler(context);
        String jsonString = jsonHandler.getJSONPutString(group);
        PutAsyncTask task = new PutAsyncTask(jsonString);
        task.execute(url);
        try {
            result = task.get(5000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Toast.makeText(context, "InterruptedException in PUT to Server", Toast.LENGTH_SHORT).show();
        } catch (ExecutionException e) {
            Toast.makeText(context, "ExecutionException in PUT to Server",Toast.LENGTH_SHORT).show();
        } catch (TimeoutException e) {
            Toast.makeText(context, "Timeout in PUT to Server",Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    public String putCityToServer(ACFCity city) {
        String result = "";
        String url = "http://acf-mobop.rhcloud.com/rest/city/"+city.getDeviceId()+"/"+city.getId();
        ACFJSONHandler jsonHandler = new ACFJSONHandler(context);
        String jsonString = jsonHandler.getJSONPutString(city);
        PutAsyncTask task = new PutAsyncTask(jsonString);
        task.execute(url);
        try {
            result = task.get(5000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Toast.makeText(context, "InterruptedException in PUT to Server", Toast.LENGTH_SHORT).show();
        } catch (ExecutionException e) {
            Toast.makeText(context, "ExecutionException in PUT to Server",Toast.LENGTH_SHORT).show();
        } catch (TimeoutException e) {
            Toast.makeText(context, "Timeout in PUT to Server",Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    public String PUT(String url,String jsonString){
        InputStream inputStream = null;
        String result = "";
        try {

            HttpClient httpclient = new DefaultHttpClient();

            HttpPut putRequest = new HttpPut(url);

            if(jsonString != null){
                putRequest.setEntity(new StringEntity(jsonString));
                putRequest.setHeader("Content-type", "application/json");
            }

            HttpResponse httpResponse = httpclient.execute(putRequest);

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

    private class PutAsyncTask extends AsyncTask<String, Void, String> {

        private String values;

        public PutAsyncTask(String values){
            this.values = values;
        }

        @Override
        protected String doInBackground(String... urls) {
            return PUT(urls[0], values);
        }

    }
}
