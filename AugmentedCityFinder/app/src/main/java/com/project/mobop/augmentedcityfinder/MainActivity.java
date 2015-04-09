package com.project.mobop.augmentedcityfinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class MainActivity extends ActionBarActivity {

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
    }

    public PlaceholderFragment getMainFragment(){
        return mainFragment;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            LayoutInflater lf = getActivity().getLayoutInflater();
            View rootView = lf.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

    }

    public void startCameraViewActivity(View v){
        Log.d("MainActivity", "Start Camera Button clicked!");
        Intent intent = new Intent(this, ACFCameraViewActivity.class);
        startActivity(intent);
    }

    public void startCitySettingsActivity(View v){
        Intent intent = new Intent(this, ACFCitySettingsActivity.class);
        startActivity(intent);
    }
}
