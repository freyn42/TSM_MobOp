package com.project.mobop.augmentedcityfinder;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

/**
 * Created by thomaso on 03.03.15.
 * Modified by freyn42 on 09.04.15.
 */
public class ACFOrientationController {
    private Context context;
    private SensorManager sensorMgr;
    private Sensor rotationSensor;
    private ACFOrientationListener orientationListener;

    public ACFOrientationController(Context appContext){
        context = appContext;
        sensorMgr = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        rotationSensor = sensorMgr.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        orientationListener = new ACFOrientationListener(context);
    }

    public void registerListener(){
        sensorMgr.registerListener(orientationListener, rotationSensor, SensorManager.SENSOR_DELAY_UI);
    }

    public void unregisterListener(){
        sensorMgr.unregisterListener(orientationListener, rotationSensor);
    }

    public ACFOrientationListener getOrientationListener() {
        return orientationListener;
    }
}
