package com.project.mobop.augmentedcityfinder;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nils on 08.03.2015.
 */
public class ACFCameraViewActivity extends Activity implements View.OnClickListener {
    final private String TAG = "ACFCameraViewActivity";
    private FrameLayout foreground;
    private TextView tv_orientation, tv_location;

    private boolean mIsBound = false;
    private ACFCameraViewService mBoundService;

    private List<ACFCity> citiesList;
    private List<ACFCityPointer> cityPointerList = new ArrayList<>();

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            // This is called when the connection with the service has been
            // established, giving us the service object.
            mBoundService = ((ACFCameraViewService.ACFCameraViewBinder) service).getService();
            citiesList = mBoundService.getCitiesList();

            ACFCityPointer cpTmp;
            for (final ACFCity city : citiesList){
//                cpTmp = new ACFCityPointer(getApplicationContext(), city.getCityName(),
//                        city.getCountryName(), city.getContinentName());
                cpTmp = new ACFCityPointer(getApplicationContext(), city);
                cpTmp.setDistance(city.getDistance());
                cpTmp.setVisibility(View.INVISIBLE);

                cityPointerList.add(cpTmp);
                foreground.addView(cpTmp);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");

            // This is called when the connection with the service has been
            // unexpectedly disconnected (i.e. its process crashed).
            mBoundService = null;
        }
    };

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Log.d(TAG, "onReceive with action: " + intent.getAction());

            if ((intent.getAction() == ACFCameraViewService.ACTION_UPDATE_NOTIFICATION) && (mBoundService != null)){
                citiesList = mBoundService.getCitiesList();
                Location location = mBoundService.getLocation();
                tv_location.setText("\nLatitude:" + (int) location.getLatitude() +
                        ", Longitude: " + (int) location.getLongitude() +
                        ", Distance: " + citiesList.get(0).getDistance());
                ACFOrientation orientation = mBoundService.getOrientation();
                tv_orientation.setText("Azimuth: " + (int) Math.toDegrees(orientation.getAzimuth()) +
                        ", Pitch: " + (int) Math.toDegrees(orientation.getPitch()) +
                        ", Roll: " + (int) Math.toDegrees(orientation.getRoll()) +
                        "\ndeltaAzimuth to " + citiesList.get(0).getCityName() + ": " +
                        (int) citiesList.get(0).getDeltaAzimuth());

                ACFCityPointer cpTmp;
                int cityEnum = 0;
                int lastTopMargin = foreground.getHeight();
                for (ACFCity city : citiesList){
                    cpTmp = cityPointerList.get(cityEnum);
                    if (city.isInView()){
                        cpTmp.setDistance(city.getDistance());

                        cpTmp.setLeftMargin(city.getLeftMargin());

                        int height = cpTmp.getCityPointerHeight();
                        if ((lastTopMargin - (height)) < 0){
                            lastTopMargin = foreground.getHeight();
                        }
                        int currentTopMargin = lastTopMargin - (height);
                        cpTmp.setTopMargin(currentTopMargin);
                        //cpTmp.setTopMargin(300);
                        lastTopMargin = currentTopMargin;

                        cpTmp.setVisibility(View.VISIBLE);
                    }else{
                        cpTmp.setVisibility(View.INVISIBLE);
                    }
                    cityEnum++;
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_view);

        foreground = (FrameLayout) findViewById(R.id.foreground);

        tv_orientation = (TextView) findViewById(R.id.tv_orientation);
        tv_location = (TextView) findViewById(R.id.tv_location);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");

        super.onResume();
        bindService();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(ACFCameraViewService.ACTION_UPDATE_NOTIFICATION));
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");

        unbindService();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick");

        foreground.removeView(v);
        tv_orientation.setText("X: Gone, Y: Gone");
    }

    void bindService() {
        Log.d(TAG, "bindService");

        // Establish a connection with the service.
        bindService(new Intent(this,
                ACFCameraViewService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void unbindService() {
        Log.d(TAG, "unbindService");

        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }
}
