package com.project.mobop.augmentedcityfinder;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
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
public class ACFRestDELETEController {

    private final Context context;

    public ACFRestDELETEController(Context context){
        this.context = context;
    }

    public String deleteGroupFromServer(ACFCityGroup group) {
        String result = "";
        String url = "http://acf-mobop.rhcloud.com/rest/group/"+group.getDeviceId()+"/"+group.getId();
        result = DELETE(url);
        return result;
    }

    public String deleteCityFromServer(ACFCity city) {
        String result = "";
        String url = "http://acf-mobop.rhcloud.com/rest/city/"+city.getDeviceId()+"/"+city.getId();
        result = DELETE(url);
        return result;
    }

    public String DELETE(String url){
        InputStream inputStream = null;
        String result = "";
        try {

            HttpClient httpclient = new DefaultHttpClient();

            HttpDelete deleteRequest = new HttpDelete(url);

            HttpResponse httpResponse = httpclient.execute(deleteRequest);

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
