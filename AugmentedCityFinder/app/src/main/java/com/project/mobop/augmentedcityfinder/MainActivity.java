package com.project.mobop.augmentedcityfinder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;

public class MainActivity extends ActionBarActivity {

    private boolean connected;
    private boolean initialized = true;
    ACFCitiesDatabaseController dbController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbController = new ACFCitiesDatabaseController(this);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);
        Location location = new Location(provider);
        location.setLongitude(4);
        location.setLatitude(4);



    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isConnected()){
            connected = false;
            Toast.makeText(this,"Keine Verbindung zum Internet",Toast.LENGTH_SHORT).show();
        } else {
            connected = true;
            if(dbController.checkDataBase()) {
                initialized = true;
            } else {
                ProgressDialog progress = new ProgressDialog(MainActivity.this);
                progress.setIndeterminate(true);
                progress.setTitle("DB Initialisierung");
                progress.setMessage("Die Datenbank wird initialisiert vom Server");
                new InitializeDatabaseTask(progress).execute();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    public void startCameraViewActivity(View v){
        if(initialized && connected) {
            Log.d("MainActivity", "Start Camera Button clicked!");
            Intent intent = new Intent(this, ACFCameraViewActivity.class);
            startActivity(intent);
        } else if(!connected){
            Toast.makeText(this,"Keine Verbindung zum Internet",Toast.LENGTH_SHORT).show();
        } else if(!initialized){
            Toast.makeText(this,"Datenbank nicht initialisiert",Toast.LENGTH_SHORT).show();
        }
    }

    public void startCitySettingsActivity(View v){
        if(initialized && connected) {
            Intent intent = new Intent(this, ACFCitySettingsActivity.class);
            startActivity(intent);
        } else if(!connected){
            Toast.makeText(this,"Keine Verbindung zum Internet",Toast.LENGTH_SHORT).show();
        } else if(!initialized){
            Toast.makeText(this,"Datenbank nicht initialisiert",Toast.LENGTH_SHORT).show();
        }
    }

    private class InitializeDatabaseTask extends AsyncTask<Void,Void,Void> {

        private ProgressDialog progress;

        public InitializeDatabaseTask(ProgressDialog progress) {
            this.progress = progress;
        }

        public void onPreExecute() {
            progress.show();
        }

        public Void doInBackground(Void... unused) {
            initialized = false;
            try {
                dbController.initializeLocalDB();
                initialized = true;
            } catch (JSONException e) {
                final String msg = e.getMessage();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (ACFDatabaseException e){
                final String msg = e.getMessage();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return null;
        }

        public void onPostExecute(Void unused) {
            if(!initialized){
                dbController.deleteDatabase();
                Toast.makeText(MainActivity.this,"Initialisierung der DB fehlgeschlagen",Toast.LENGTH_SHORT).show();
            }
            progress.dismiss();
        }
    }
}
