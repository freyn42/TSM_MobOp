package com.project.mobop.augmentedcityfinder;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class MainActivity extends ActionBarActivity {

    private ACFLocationController locationController;
    private ACFOrientationController orientationController;

    private PlaceholderFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainFragment = new PlaceholderFragment();
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mainFragment)
                    .commit();
        }

        locationController = new ACFLocationController(this);
        orientationController = new ACFOrientationController(this);

    }

    public PlaceholderFragment getMainFragment(){
        return mainFragment;
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationController.requestLocationUpdates(15000, 1);
        orientationController.registerListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationController.unregisterListener();
        orientationController.unregisterListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private TextView tv_direction;
        private TextView tv_location;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            LayoutInflater lf = getActivity().getLayoutInflater();
            View rootView = lf.inflate(R.layout.fragment_main, container, false);
            tv_direction = (TextView)rootView.findViewById(R.id.tv_direction);
            tv_location = (TextView)rootView.findViewById(R.id.tv_location);
            return rootView;
        }

        public TextView getTvDirection(){
            return tv_direction;
        }

        public TextView getTvLocation(){
            return tv_location;
        }

    }

}
