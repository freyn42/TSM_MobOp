package com.project.mobop.augmentedcityfinder;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.Observable;

/**
 * Created by thomaso on 03.03.15.
 * Modified by freyn42 on 09.04.15.
 */
public class ACFOrientationListener extends Observable implements SensorEventListener{

    private Context context;
    private float[] gravity = null;
    private float[] geomagnetic = null;
    private float gO[] = new float[3];
    private float[] rotationMatrix;
    private float[] outRotationMatrix;
    private ACFOrientation orientation = new ACFOrientation();

    public ACFOrientationListener(Context appContext){
        context = appContext;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        final SensorEvent event_final = event;
        Runnable runnable = new Runnable(){
            @Override
            public void run() {
//                if (event_final.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//                    gravity = lowPass(event_final.values.clone(), gravity);
//                } else if (event_final.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
//                    geomagnetic = lowPass(event_final.values.clone(), geomagnetic);
//                }
//
//                float R_in[] = new float[9];
//                float R_out[] = new float[9];
//
//                if (geomagnetic != null && gravity != null) {
//                    if (SensorManager.getRotationMatrix(R_in, null, gravity, geomagnetic)) {
//                        SensorManager.remapCoordinateSystem(R_in, SensorManager.AXIS_X, SensorManager.AXIS_Z, R_out);
//                        SensorManager.getOrientation(R_out, gO);
//                    }
//                    orientation.setAzimuth(gO[0]);
//                    orientation.setPitch(gO[1]);
//                    orientation.setRoll(gO[2]);
//                }
                if (event_final.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR){
                    rotationMatrix = new float[16];
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, event_final.values);
                }



                outRotationMatrix = new float[16];
                SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_X,
                        SensorManager.AXIS_Z, outRotationMatrix);   // Remap coordinate System to compensate for the landscape position of device
                SensorManager.getOrientation(outRotationMatrix, gO);

                orientation.setAzimuth(gO[0]); // + this.getDeclination()); //Azimuth; (Degrees);
                orientation.setPitch(gO[1]); //Pitch; (Degrees); down is 90 , up is -90.
                orientation.setRoll(gO[2]); // Roll;

            }

            private float[] lowPass(float[] input, float[] output) {
                float ALPHA = 0.25f;
                if ( output == null ) return input;
                for ( int i=0; i<input.length; i++ ) {
                    output[i] = output[i] + ALPHA * (input[i] - output[i]);
                }
                return output;
            }
        };
        new Thread(runnable).start();
        setChanged();
        notifyObservers(orientation);
    }

    public ACFOrientation getUpdate(){
        return orientation;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
