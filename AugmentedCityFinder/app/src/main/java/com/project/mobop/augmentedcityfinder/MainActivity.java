package com.project.mobop.augmentedcityfinder;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class MainActivity extends ActionBarActivity implements  LocationListener, SensorEventListener {

    private static final String[] ACCURACY = { "invalid", "n/a", "fine",
            "coarse" };
    private static final String[] POWERREQ = { "invalid", "n/a", "low",
            "medium", "high" };
    private static final String[] STATUS = { "out of service",
            "temporarily unavailable", "available" };
    private Context context;
    private LocationManager locationMgr;
    private SensorManager sensorMgr;
    private Sensor accelerometerSensor;
    private Sensor magneticFieldSensor;

    String direction = "";

    private TextView tv;
    private String best; // name of the best suited location provider

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getApplicationContext();
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        locationMgr = (LocationManager) getSystemService(LOCATION_SERVICE);
        sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometerSensor = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magneticFieldSensor = sensorMgr.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        //tv = (TextView) findViewById(R.id.tv);

        sensorMgr.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);
        sensorMgr.registerListener(this, magneticFieldSensor, SensorManager.SENSOR_DELAY_UI);

        List<String> providers = locationMgr.getAllProviders();
        for (String provider : providers) {
            LocationProvider info = locationMgr.getProvider(provider);
            StringBuilder builder = new StringBuilder();
            builder.append("\nLocationProvider[").append("name=")
                    .append(info.getName()).append(",enabled=")
                    .append(locationMgr.isProviderEnabled(provider))
                    .append(",getAccuracy=")
                    .append(ACCURACY[info.getAccuracy() + 1])
                    .append(",getPowerRequirement=")
                    .append(POWERREQ[info.getPowerRequirement() + 1])
                    .append(",hasMonetaryCost=").append(info.hasMonetaryCost())
                    .append(",requiresCell=").append(info.requiresCell())
                    .append(",requiresNetwork=").append(info.requiresNetwork())
                    .append(",requiresSatellite=").append(info.requiresSatellite())
                    .append(",supportsAltitude=").append(info.supportsAltitude())
                    .append(",supportsBearing=").append(info.supportsBearing())
                    .append(",supportsSpeed=").append(info.supportsSpeed())
                    .append("]");
            Log.d("MainActivity",builder.toString());
        }

        Criteria criteria = new Criteria();
        //criteria.setSpeedRequired(true);
        best = locationMgr.getBestProvider(criteria, true);
        Log.d("MainActivity","\nLocation Provider: "+best);
        if (best != null) {
            Location location = locationMgr.getLastKnownLocation(best);
            Log.d("MainActivity","\nLocation: "+ (location==null?"[unknown]":location.toString()));
        } else {
            Log.w("MainActivity","\nLocation: [unknown]");
            Toast.makeText(this.getApplicationContext(), "Location not found", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        // To save battery only request updates only when activity
        // displaying results runs in foreground
        if (best != null) {
            locationMgr.requestLocationUpdates(best, 15000, 1, this);
            // parameter: provider name,
            // time delay for updates in ms,
            // spatial distance for updates in m,
            // reference to location listener
        }



    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorMgr.unregisterListener(this,accelerometerSensor);
        sensorMgr.unregisterListener(this,magneticFieldSensor);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

    public void onLocationChanged(Location location) {
        Log.d("MainActivity", "\nLocation: " + (location == null ? "[unknown]" : location.toString()));
    }

    public void onProviderDisabled(String provider) {
        Log.d("MainActivity","\nProvider disabled: " + provider);
    }

    public void onProviderEnabled(String provider) {
        Log.d("MainActivity", "\nProvider enabled: " + provider);
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("MainActivity", "\nProvider status changed: " + provider + ", status="
                + STATUS[status] + ", extras=" + extras);
    }

    float[] gravity = null;
    float[] geomagnetic = null;
    float gO[] = new float[3];

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravity = lowPass(event.values.clone(), gravity);
        }
        else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagnetic = lowPass(event.values.clone(), geomagnetic);
        }

        float R_in[] = new float[9];
        float R_out[] = new float[9];


        if (geomagnetic != null && gravity != null) {
            if (SensorManager.getRotationMatrix(R_in, null, gravity, geomagnetic)) {
                SensorManager.remapCoordinateSystem (R_in, SensorManager.AXIS_X, SensorManager.AXIS_Z, R_out);
                SensorManager.getOrientation (R_out, gO);
            }
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run () {
                double degrees = Math.toDegrees(gO[0]);
                degrees = degrees > 0 ? degrees : 360 + degrees;
                String directions[] = {"N", "NE", "E", "SE", "S", "SW", "W", "NW", "N"};
                String dir = directions[(int) Math.round(((degrees % 360) / 45))];
                if (!(direction.equalsIgnoreCase(dir))) {
                    direction = dir;
                    Log.d("MainActivity","Direction:"+dir);
                }
            }
        });



    }

    private float[] lowPass(float[] input, float[] output) {
        float ALPHA = 0.25f;
        if ( output == null ) return input;
        for ( int i=0; i<input.length; i++ ) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
