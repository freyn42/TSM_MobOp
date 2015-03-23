package com.project.mobop.augmentedcityfinder;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Random;

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
    private List<TextView> tvCityPointerList = new ArrayList<>();

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service. Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mBoundService = ((ACFCameraViewService.ACFCameraViewBinder) service).getService();
            citiesList = mBoundService.getCitiesList();

            Random rnd = new Random();
            int i = 0;
            for (ACFCity city : citiesList){
                tvCityPointerList.add(new TextView(getApplicationContext()));
                tvCityPointerList.get(i).setText("X\r\n" + city.getCityName());
                tvCityPointerList.get(i).setTextColor(Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));
                tvCityPointerList.get(i).setTextSize(20);
                tvCityPointerList.get(i).setVisibility(View.GONE);
                foreground.addView(tvCityPointerList.get(i));
                i++;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");

            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            mBoundService = null;
        }
    };

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Log.d(TAG, "onReceive with action: " + intent.getAction());

            if (intent.getAction() == ACFCameraViewService.ACTION_UPDATE_NOTIFICATION){
                citiesList = mBoundService.getCitiesList();
                if (intent.getStringExtra(ACFCameraViewService.EXTRA_UPDATE_SOURCE) ==
                        ACFCameraViewService.EXTRA_LOCATION){
                    Location location = mBoundService.getLocation();
//                    Log.d(TAG, "new location: long=" + location.getLongitude() + ", lat=" +
//                            location.getLatitude());
                    tv_location.setText("\r\nLatitude:" + (int) location.getLatitude() +
                            ", Longitude: " + (int) location.getLongitude() +
                            ", Distance: " + citiesList.get(0).getDistance());
                }else if (intent.getStringExtra(ACFCameraViewService.EXTRA_UPDATE_SOURCE) ==
                        ACFCameraViewService.EXTRA_ORIENTATION){
                    ACFOrientation orientation = mBoundService.getOrientation();
//                    Log.d(TAG, "new orientation: azimuth=" + Math.toDegrees(orientation.getAzimuth()) +
//                            ", pitch=" + Math.toDegrees(orientation.getPitch()) +
//                            ", roll=" + Math.toDegrees(orientation.getRoll()));

                    tv_orientation.setText("Azimuth: " + (int) Math.toDegrees(orientation.getAzimuth()) +
                            ", Pitch: " + (int) Math.toDegrees(orientation.getPitch()) +
                            ", Roll: " + (int) Math.toDegrees(orientation.getRoll()) +
                            "\r\ndeltaAzimuth to " + citiesList.get(0).getCityName() + ": " +
                            (int) citiesList.get(0).getDeltaAzimuth());

                    int i = 0;
                    for (TextView tvCityPointer : tvCityPointerList){
                        if (citiesList.get(i).isInView()){
                            tvCityPointer.setVisibility(View.VISIBLE);
                            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) tvCityPointer.getLayoutParams();
                            params.leftMargin = citiesList.get(i).getLeftMargin();
                            params.topMargin = citiesList.get(i).getTopMargin();
                            params.gravity = Gravity.LEFT + Gravity.TOP;
                            tvCityPointer.setLayoutParams(params);
                        }else{
                            tvCityPointer.setVisibility(View.GONE);
                        }
                        i++;
                    }
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
        // Establish a connection with the service.
        bindService(new Intent(this,
                ACFCameraViewService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void unbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }
}
