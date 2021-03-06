package com.project.mobop.augmentedcityfinder;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class ACFCameraViewService extends Service implements Observer {
    private final String TAG = "ACFCameraViewService";

    static final String ACTION_UPDATE_NOTIFICATION = "update-notification";

    private IBinder mIBinder = new ACFCameraViewBinder();

    private ACFOrientationController orientationController;
    private Observable orientationObservable;

    private ACFLocationController locationController;
    private Observable locationObservable;

    private Location location;
    private ACFOrientation orientation;

    private List<ACFCity> citiesList = new ArrayList<>();

    private double screenWidthMillimeter, screenHeightMillimeter;
    private int screenWidthPixel, screenHeightPixel;
    private int horizontalPixelDegree, verticalPixelDegree;
    private double horizontalViewAngle, verticalViewAngle;
    private final double DISTANCE_TO_VIEWER = 300; // Millimeter

    private final String PREFS_LAST_KNOWN_LOCATION = "LastKnownLocation";
    private final String PREFS_LONGITUDE = "Longitude";
    private final String PREFS_LATITUDE = "Latitude";

    private final double ANGLE_PRECISION = 0.022;

    private double oldAzimuth = 0;
    private boolean locationFixed = false;

    public ACFCameraViewService() {
    }

    /**
     * Class for Activity to get access to service
     */
    public class ACFCameraViewBinder extends Binder{
        ACFCameraViewService getService(){
            return ACFCameraViewService.this;
        }
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");

        super.onCreate();

        if (!locationFixed){
            Toast.makeText(getApplicationContext(), "Location not yet fixed! \nUsing last known location.", Toast.LENGTH_LONG).show();
        }

        SharedPreferences prefs = getSharedPreferences(PREFS_LAST_KNOWN_LOCATION, 0);
        if (prefs.contains(PREFS_LATITUDE)){
            location = new Location("MyLocation");
            location.setLatitude(prefs.getFloat(PREFS_LATITUDE, 0));
            location.setLongitude(prefs.getFloat(PREFS_LONGITUDE, 0));
        }

        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);

        // Calculating the size of the display
        screenWidthPixel = dm.widthPixels;
        screenWidthMillimeter = screenWidthPixel/dm.xdpi * 25.4;
        screenHeightPixel = dm.heightPixels;
        screenHeightMillimeter = screenHeightPixel/dm.ydpi * 25.4;

        double x = Math.pow(dm.widthPixels/dm.xdpi,2);
        double y = Math.pow(dm.heightPixels/dm.ydpi,2);
        double screenInches = Math.sqrt(x+y);
        Log.d(TAG,"Screen inches: " + screenInches +
                ", screen width: " + screenWidthMillimeter +
                ", screen height: " + screenHeightMillimeter);

        // Calculating viewing angle of the camera from size of the screen, distance to user and density of the display.
        float density = getApplicationContext().getResources().getDisplayMetrics().density;
        horizontalViewAngle = density * Math.toDegrees(2 * Math.atan2(screenWidthMillimeter / 2, DISTANCE_TO_VIEWER));
        verticalViewAngle = density * Math.toDegrees(2 * Math.atan2(screenHeightMillimeter / 2, DISTANCE_TO_VIEWER));
        Log.d(TAG, "horizontalViewAngle: " + horizontalViewAngle +
                ", verticalViewAngle: " + verticalViewAngle);

        horizontalPixelDegree = (int) (screenWidthPixel / horizontalViewAngle);
        verticalPixelDegree = (int) (screenHeightPixel / verticalViewAngle);
        Log.d(TAG, "horizontalPixelDegree: " + horizontalPixelDegree +
                ", verticalPixelDegree: " + verticalPixelDegree);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");

        loadCities();
        registerOrientation();
        registerLocation();

        // Returns the object for interaction from the activity.
        return mIBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");

        unregisterOrientation();
        unregisterLocation();

        if (location != null){
            SharedPreferences prefs = getSharedPreferences(PREFS_LAST_KNOWN_LOCATION, 0);
            SharedPreferences.Editor prefsEdit = prefs.edit();
            prefsEdit.putFloat(PREFS_LONGITUDE, (float) location.getLongitude());
            prefsEdit.putFloat(PREFS_LATITUDE, (float) location.getLatitude());
            prefsEdit.apply();
        }

        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");

        super.onDestroy();
    }

    private void registerOrientation(){
        Log.d(TAG, "registerOrientation");

        orientationController = new ACFOrientationController(this);
        orientationController.registerListener();
        orientationObservable = orientationController.getOrientationListener();
        orientationObservable.addObserver(this);
    }

    private void unregisterOrientation(){
        Log.d(TAG, "unregisterOrientation");

        orientationObservable.deleteObserver(this);
        orientationController.unregisterListener();
    }

    private void registerLocation(){
        Log.d(TAG, "registerLocation");

        locationController = new ACFLocationController(this);
        locationController.requestLocationUpdates(1000, 1);
        locationObservable = locationController.getLocationListener();
        locationObservable.addObserver(this);
    }

    private void unregisterLocation(){
        Log.d(TAG, "unregisterLocation");

        locationObservable.deleteObserver(this);
        locationController.unregisterListener();
    }

    private void loadCities() {
        Log.d(TAG, "loadCities");

        ACFCitiesDatabaseController citiesDBCtrl = new ACFCitiesDatabaseController(this);
        citiesList = citiesDBCtrl.getAllVisibleCities();
    }

    @Override
    public void update(Observable observable, Object data) {
        if (observable == orientationObservable){
            orientation = (ACFOrientation) data;
            if (Math.abs(oldAzimuth - orientation.getAzimuth()) < ANGLE_PRECISION){
                return;
            }
            if (orientation != null){
                oldAzimuth = orientation.getAzimuth();
            }
        }else if (observable == locationObservable){
            location = (Location) data;
            if ((!locationFixed) && (location != null)){
//                //Post UsageLocation
//                ACFRestPOSTController postController = new ACFRestPOSTController(this);
//                int usageId = postController.postUsageLocation(location);
//                ACFCityPointer.setUsageId(usageId);
                Toast.makeText(getApplicationContext(), "Location fixed!", Toast.LENGTH_LONG).show();
                locationFixed = true;
            }
        }
        positionCalculations();
    }

    private void positionCalculations(){
        if (location != null){
            for (ACFCity city : citiesList){

                // Update distance to city
                city.setDistance(location.distanceTo(city.getLocation()));

                // Calculate orientation to city
                double bearing = location.bearingTo(city.getLocation());
                if (bearing > 180){
                    bearing = bearing - 360;
                }
                double azimuth = Math.toDegrees(orientation.getAzimuth());
                double deltaAzimuth = azimuth - bearing;
                city.setDeltaAzimuth(deltaAzimuth);
                city.setDeltaPitch(0);

                if (Math.abs(deltaAzimuth) < ((horizontalViewAngle / 2) * 1.5)){
                    city.setInView(true);
                }else{
                    city.setInView(false);
                }

                // Update city pointer position
                if (city.isInView()){
                    int leftMargin = (int) ((screenWidthPixel / 2) +
                            (horizontalPixelDegree * ((int) -city.getDeltaAzimuth())) - 25);
                    city.setLeftMargin(leftMargin);


                    int topMargin = (int) ((screenHeightPixel / 2) - 25);
                    city.setTopMargin(topMargin);
                }
            }
        }

        Intent intent = new Intent(ACTION_UPDATE_NOTIFICATION);
        sendMessage(intent);
    }

    private void sendMessage(Intent intent){
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public Location getLocation() {
        return location;
    }

    public ACFOrientation getOrientation(){
        return orientation;
    }

    public List<ACFCity> getCitiesList() {
        return citiesList;
    }
}