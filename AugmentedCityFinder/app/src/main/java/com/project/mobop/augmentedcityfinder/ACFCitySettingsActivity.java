package com.project.mobop.augmentedcityfinder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by tom on 05.04.2015.
 */
public class ACFCitySettingsActivity extends Activity {

    ACFExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<ACFCityGroup> listDataHeader;
    HashMap<Integer, List<ACFCity>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_settings);

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lv_groups);

        // preparing list data
        prepareListData();

        listAdapter = new ACFExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);
    }

    private void prepareListData() {
        ACFCitiesDatabaseController dbController = new ACFCitiesDatabaseController(this);
        listDataHeader = dbController.getGroups();
        listDataChild = new HashMap<Integer, List<ACFCity>>();

        for(ACFCityGroup group : listDataHeader){
            List<ACFCity> members = new ArrayList<ACFCity>();
            for(ACFCity city : group.getCityList()){
                members.add(city);
            }
            listDataChild.put(group.getId(), members);
        }

    }

    public void onButtonExitClicked(View v){
        finish();
    }

    public void onButtonAddGroupClicked(View v){
        Intent intent = new Intent(this, ACFEditGroupActivity.class);
        startActivity(intent);
    }

    public void onButtonAddCityClicked(View v){

    }

}
