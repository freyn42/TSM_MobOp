package com.project.mobop.augmentedcityfinder;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

/**
 * Created by thomaso on 03.03.15.
 */
public class ACFOrientationController_old {
    private Context context;
    private SensorManager sensorMgr;
    private Sensor accelerometerSensor;
    private Sensor magneticFieldSensor;
    private ACFOrientationListener orientationListener;

    public ACFOrientationController_old(Context appContext){
        context = appContext;
        sensorMgr = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magneticFieldSensor = sensorMgr.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        orientationListener = new ACFOrientationListener(context);
    }

    public void registerListener(){
        sensorMgr.registerListener(orientationListener, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);
        sensorMgr.registerListener(orientationListener, magneticFieldSensor, SensorManager.SENSOR_DELAY_UI);
    }

    public void unregisterListener(){
        sensorMgr.unregisterListener(orientationListener,accelerometerSensor);
        sensorMgr.unregisterListener(orientationListener,magneticFieldSensor);
    }

    public ACFOrientationListener getOrientationListener() {
        return orientationListener;
    }
}
