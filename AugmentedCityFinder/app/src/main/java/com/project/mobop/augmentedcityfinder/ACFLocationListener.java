package com.project.mobop.augmentedcityfinder;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by thomaso on 03.03.15.
 */
public class ACFLocationListener implements LocationListener {

    private static final String[] ACCURACY = { "invalid", "n/a", "fine",
            "coarse" };
    private static final String[] POWERREQ = { "invalid", "n/a", "low",
            "medium", "high" };
    private static final String[] STATUS = { "out of service",
            "temporarily unavailable", "available" };
    private Activity context;
    private TextView tv_location;

    public ACFLocationListener(Activity appContext){
        context = appContext;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("MainActivity", "\nLocation: " + (location == null ? "[unknown]" : location.toString()));
        if(tv_location == null) {
            tv_location = (TextView) ((MainActivity) context).getMainFragment().getTvLocation();
        }
        tv_location.setText(location.toString());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("MainActivity", "\nProvider status changed: " + provider + ", status="
                + STATUS[status] + ", extras=" + extras);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("MainActivity", "\nProvider enabled: " + provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("MainActivity","\nProvider disabled: " + provider);
    }
}
