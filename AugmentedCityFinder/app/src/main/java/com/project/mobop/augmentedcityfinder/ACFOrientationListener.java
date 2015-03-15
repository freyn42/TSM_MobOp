package com.project.mobop.augmentedcityfinder;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.widget.TextView;

/**
 * Created by thomaso on 03.03.15.
 */
public class ACFOrientationListener implements SensorEventListener {

    private Activity context;
    private float[] gravity = null;
    private float[] geomagnetic = null;
    private float gO[] = new float[3];
    private float azimuth, pitch, roll;
    private String direction = "";
    private TextView tv_direction = null;

    public ACFOrientationListener(Activity appContext){
        context = appContext;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        new CalculateDirectionTask().execute(event);
    }

    private class CalculateDirectionTask extends AsyncTask<SensorEvent,Void,String>{
        @Override
        protected String doInBackground(SensorEvent... event) {
            if (event[0].sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                gravity = lowPass(event[0].values.clone(), gravity);
            }
            else if (event[0].sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                geomagnetic = lowPass(event[0].values.clone(), geomagnetic);
            }

            float R_in[] = new float[9];
            float R_out[] = new float[9];

            if (geomagnetic != null && gravity != null) {
                if (SensorManager.getRotationMatrix(R_in, null, gravity, geomagnetic)) {
                    SensorManager.remapCoordinateSystem (R_in, SensorManager.AXIS_X, SensorManager.AXIS_Z, R_out);
                    SensorManager.getOrientation (R_out, gO);
                }
                azimuth = gO[0];
                pitch = gO[1];
                roll = gO[2];
                double degrees = Math.toDegrees(gO[0]);
                degrees = degrees > 0 ? degrees : 360 + degrees;
                String directions[] = {"N", "NE", "E", "SE", "S", "SW", "W", "NW", "N"};
                String dir = directions[(int) Math.round(((degrees % 360) / 45))];
                if (!(direction.equalsIgnoreCase(dir))) {
                    direction = dir;
                    return dir;
                } else {
                    return null;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result != null){
                if(tv_direction == null) {
                    tv_direction = (TextView) ((MainActivity) context).getMainFragment().getTvDirection();
                }
                tv_direction.setText(result);
            }
        }

        private float[] lowPass(float[] input, float[] output) {
            float ALPHA = 0.25f;
            if ( output == null ) return input;
            for ( int i=0; i<input.length; i++ ) {
                output[i] = output[i] + ALPHA * (input[i] - output[i]);
            }
            return output;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public float getAzimuth() {
        return azimuth;
    }

    public float getPitch() {
        return pitch;
    }

    public float getRoll() {
        return roll;
    }
}
