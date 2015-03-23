package com.project.mobop.augmentedcityfinder;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by thomaso on 03.03.15.
 */
public class ACFLocationController {
    private Context context;
    private LocationManager locationMgr;
    private String bestLocationProvider;
    private ACFLocationListener locationListener;

    public ACFLocationController(Context appContext){
        context = appContext;
        locationMgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        bestLocationProvider = locationMgr.getBestProvider(criteria, true);

        if (bestLocationProvider != null) {
            Location location = locationMgr.getLastKnownLocation(bestLocationProvider);
            Log.d("ACFLocationController", "\nLocation Provider: " + bestLocationProvider);
        } else {
            Log.w("ACFLocationController","\nno location provider found");
            Toast.makeText(context, "no location provider found", Toast.LENGTH_SHORT).show();
        }
        locationListener = new ACFLocationListener(context);

    }

    public void requestLocationUpdates(int minDelay, int minDistance){
        if (bestLocationProvider != null) {
            locationMgr.requestLocationUpdates(bestLocationProvider, minDelay, minDistance, locationListener);
        }
    }

    public void unregisterListener(){
        locationMgr.removeUpdates(locationListener);
    }

    public ACFLocationListener getLocationListener() {
        return locationListener;
    }
}
