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

import java.util.Observable;
import java.util.Random;

/**
 * Created by Nils on 08.03.2015.
 */
public class ACFCameraViewActivity extends Activity implements View.OnClickListener {
    final private String TAG = "ACFCameraViewActivity";
    private FrameLayout background, foreground;
    private CameraView cameraView;
    private ImageView iv_city_pointer;
    private TextView tv_orientation,tv_location;
    private int view_width, view_height, city_pointer_width, city_pointer_height;
    private int touch_x, touch_y, city_pointer_margin_left, city_pointer_margin_top;

    private ACFOrientationController orientationController;
    private Observable orientationObservable;

    private ACFLocationController locationController;
    private Observable locationObservable;
    private boolean mIsBound = false;
    private ACFCameraViewService mBoundService;

    private City[] cities;
    private TextView[] tv_city_pointer;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service. Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mBoundService = ((ACFCameraViewService.ACFCameraViewBinder) service).getService();
            cities = mBoundService.getCities();
            tv_city_pointer = new TextView[cities.length];

            Random rnd = new Random();
            for (int i=0; i<tv_city_pointer.length; i++){
                tv_city_pointer[i] = new TextView(getApplicationContext());
                tv_city_pointer[i].setText("X\r\n" + cities[i].getCityName());
                tv_city_pointer[i].setTextColor(Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));
                tv_city_pointer[i].setTextSize(20);
                tv_city_pointer[i].setVisibility(View.GONE);
                foreground.addView(tv_city_pointer[i]);
            }

            Toast.makeText(getApplicationContext(), "ACFCameraViewActivity.onServiceConnected",
                    Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            mBoundService = null;
            Toast.makeText(getApplicationContext(), "ACFCameraViewActivity.onServiceConnected",
                    Toast.LENGTH_SHORT).show();
        }
    };

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Log.d(TAG, "onReceive with intent: " + intent.getAction());

            if (intent.getAction() == ACFCameraViewService.ACTION_UPDATE_NOTIFICATION){
                if (intent.getStringExtra(ACFCameraViewService.EXTRA_UPDATE_SOURCE) ==
                        ACFCameraViewService.EXTRA_LOCATION){
                    Location location = mBoundService.getLocation();
//                    Log.d(TAG, "new location: long=" + location.getLongitude() + ", lat=" +
//                            location.getLatitude());
                    City[] cities = mBoundService.getCities();
                    tv_location.setText("\r\nLatitude:" + (int) location.getLatitude() +
                            ", Longitude: " + (int) location.getLongitude() +
                            ", Distance: " + cities[0].getDistance());

                    int childCount = foreground.getChildCount();
                    for (int i=0; i<childCount; i++){
                        Log.d(TAG, "ChildCount: " + childCount + ", Child [" + i + "]: " +
                                foreground.getChildAt(i));
                    }
                }else if (intent.getStringExtra(ACFCameraViewService.EXTRA_UPDATE_SOURCE) ==
                        ACFCameraViewService.EXTRA_ORIENTATION){
                    ACFOrientation orientation = mBoundService.getOrientation();
//                    Log.d(TAG, "new orientation: azimuth=" + Math.toDegrees(orientation.getAzimuth()) +
//                            ", pitch=" + Math.toDegrees(orientation.getPitch()) +
//                            ", roll=" + Math.toDegrees(orientation.getRoll()));
                    City[] cities = mBoundService.getCities();
                    tv_orientation.setText("Azimuth: " + (int) Math.toDegrees(orientation.getAzimuth()) +
                            ", Pitch: " + (int) Math.toDegrees(orientation.getPitch()) +
                            ", Roll: " + (int) Math.toDegrees(orientation.getRoll()) +
                            "\r\ndeltaAzimuth: " + (int) cities[0].getDeltaAzimuth());

                    for (int i=0; i<tv_city_pointer.length; i++){
                        if (cities[i].isInView()){
                            tv_city_pointer[i].setVisibility(View.VISIBLE);
                            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) tv_city_pointer[i].getLayoutParams();
                            params.leftMargin = cities[i].getLeftMargin();
                            params.topMargin = cities[i].getTopMargin();
                            params.gravity = Gravity.LEFT + Gravity.TOP;
                            tv_city_pointer[i].setLayoutParams(params);
                        }else{
                            tv_city_pointer[i].setVisibility(View.GONE);
                        }
                    }
//                    foreground.removeView(iv_city_pointer);
//                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(50,
//                            50);
//                    params.leftMargin = cities[0].getLeftMargin();
//                    params.topMargin = cities[0].getTopMargin();
//                    params.gravity = Gravity.LEFT + Gravity.TOP;
//                    iv_city_pointer.setTag(R.string.city_pointer, getString(R.string.city_pointer));
//                    foreground.addView(iv_city_pointer, params);
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_view);

        background = (FrameLayout) findViewById(R.id.background);
        view_width = background.getWidth();
        view_height = background.getHeight();

        cameraView = (CameraView) findViewById(R.id.camera_preview);

        foreground = (FrameLayout) findViewById(R.id.foreground);

        iv_city_pointer = new ImageView(this);
        iv_city_pointer.setImageResource(R.drawable.locator_symbol);
        iv_city_pointer.setOnClickListener(this);
        //city_pointer_height = iv_city_pointer.getHeight();
        //city_pointer_width = iv_city_pointer.getWidth();
        city_pointer_height = 50;
        city_pointer_width = 50;
        Log.d(TAG, "City pointer width: " + String.valueOf(city_pointer_width) +
                ", City pointer height: " + String.valueOf(city_pointer_height));

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
