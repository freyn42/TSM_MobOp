package com.project.mobop.augmentedcityfinder;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import java.util.Observable;
import java.util.Observer;

public class ACFCameraViewService extends Service implements Observer {

    private final String TAG = "ACFCameraViewService";

    static final String ACTION_UPDATE_NOTIFICATION = "update-notification";
    static final String EXTRA_UPDATE_SOURCE = "update-source";
    static final String EXTRA_ORIENTATION = "orientation";
    static final String EXTRA_LOCATION = "location";

    private IBinder mIBinder = new ACFCameraViewBinder();

    private ACFOrientationController orientationController;
    private Observable orientationObservable;

    private ACFLocationController locationController;
    private Observable locationObservable;

    private Location location;
    private ACFOrientation orientation;

    private ACFCity berlin = new ACFCity("Berlin", "Germany", "Europe", 13.408333, 52.518611);
    private ACFCity stockholm = new ACFCity("Stockholm", "Sweden", "Europe", 18.05, 59.325);
    private ACFCity[] cities = {berlin, stockholm};

    private double screenWidthMillimeter, screenHeightMillimeter;
    private int screenWidthPixel, screenHeightPixel;
    private int horizontalPixelDegree, verticalPixelDegree;
    private double horizontalViewAngle, verticalViewAngle;
    private final double DISTANCE_TO_VIEWER = 300; // Millimeter

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

        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);

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

        horizontalViewAngle = 2 * Math.toDegrees(2 * Math.atan2(screenWidthMillimeter / 2, DISTANCE_TO_VIEWER));
        verticalViewAngle = 2 * Math.toDegrees(2 * Math.atan2(screenHeightMillimeter / 2, DISTANCE_TO_VIEWER));
        Log.d(TAG, "horizontalViewAngle: " + horizontalViewAngle +
                ", verticalViewAngle: " + verticalViewAngle);

        horizontalPixelDegree = (int) (screenWidthPixel / horizontalViewAngle);
        verticalPixelDegree = (int) (screenHeightPixel / verticalViewAngle);
        Log.d(TAG, "horizontalPixelDegree: " + horizontalPixelDegree +
                ", verticalPixelDegree: " + verticalPixelDegree);


        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");

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

        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");

        super.onDestroy();
    }

    private void registerOrientation(){
        orientationController = new ACFOrientationController(this);
        orientationController.registerListener();
        orientationObservable = orientationController.getOrientationListener();
        orientationObservable.addObserver(this);
    }

    private void unregisterOrientation(){
        orientationObservable.deleteObserver(this);
        orientationController.unregisterListener();
    }

    private void registerLocation(){
        locationController = new ACFLocationController(this);
        locationController.requestLocationUpdates(15000, 1);
        locationObservable = locationController.getLocationListener();
        locationObservable.addObserver(this);
    }

    private void unregisterLocation(){
        locationObservable.deleteObserver(this);
        locationController.unregisterListener();
    }

    @Override
    public void update(Observable observable, Object data) {
 //       Log.d(TAG, "Observable update - " + observable + " - " + data);

        if (observable == orientationObservable){
            orientation = (ACFOrientation) data;

            orientationCalculations();
        }else if (observable == locationObservable){
            location = (Location) data;
            locationCalculations();
        }
    }

    private void orientationCalculations(){
        for (int i=0; i<cities.length; i++){
            if (location != null){
                double bearing = location.bearingTo(cities[i].getLocation());
                if (bearing > 180){
                    bearing = bearing - 360;
                }
                double azimuth = Math.toDegrees(orientation.getAzimuth());
                double deltaAzimuth = azimuth - bearing;
                cities[i].setDeltaAzimuth(deltaAzimuth);
                cities[i].setDeltaPitch(0);

                if (Math.abs(deltaAzimuth) < (horizontalViewAngle / 2)){
                    cities[i].setInView(true);
                }else{
                    cities[i].setInView(false);
                }

                if (cities[i].isInView()){
                    int leftMargin = (int) ((screenWidthPixel / 2) +
                            (horizontalPixelDegree * ((int) -cities[i].getDeltaAzimuth())) - 25);
                    cities[i].setLeftMargin(leftMargin);

                    int topMargin = (int) ((screenHeightPixel / 2) - 25);
                    cities[i].setTopMargin(topMargin);
                }
            }
        }

        Intent intent = new Intent(ACTION_UPDATE_NOTIFICATION);
        intent.putExtra(EXTRA_UPDATE_SOURCE, EXTRA_ORIENTATION);
        sendMessage(intent);
    }

    private void locationCalculations(){
        for (int i=0; i<cities.length; i++){
            if (location != null){
                cities[i].setDistance(location.distanceTo(cities[i].getLocation()));
            }
        }

        Intent intent = new Intent(ACTION_UPDATE_NOTIFICATION);
        intent.putExtra(EXTRA_UPDATE_SOURCE, EXTRA_LOCATION);
        sendMessage(intent);
    }

    private void sendMessage(Intent intent){
//        Log.d(TAG, "sendMessage");

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public Location getLocation() {
        return location;
    }

    public ACFOrientation getOrientation() {
        return orientation;
    }

    public ACFCity[] getCities(){
        return cities;
    }
}
