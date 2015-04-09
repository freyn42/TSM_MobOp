package com.project.mobop.augmentedcityfinder;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by tom on 05.04.2015.
 */
public class ACFCitySettingsActivity extends FragmentActivity implements ACFModifyChoiceDialog.ChoiceDialogListener {

    ACFExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<ACFCityGroup> listDataHeader;
    HashMap<Integer, List<ACFCity>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_settings);

        expListView = (ExpandableListView) findViewById(R.id.lv_groups);

    }

    @Override
    protected void onResume() {
        super.onResume();
        prepareListData();

        listAdapter = new ACFExpandableListAdapter(this, listDataHeader, listDataChild);

        expListView.setAdapter(listAdapter);
        expListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int itemType = ExpandableListView.getPackedPositionType(id);

                if (itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    int childPosition = ExpandableListView.getPackedPositionChild(id);
                    int groupPosition = ExpandableListView.getPackedPositionGroup(id);
                    ACFCity city = (ACFCity) listAdapter.getChild(groupPosition, childPosition);
                    if(city.getDeviceId() == 1){
                        Toast.makeText(ACFCitySettingsActivity.this,"System Städte können nicht bearbeitet werden",Toast.LENGTH_SHORT).show();
                    } else {
                        ACFModifyChoiceDialog dialog = new ACFModifyChoiceDialog();
                        dialog.setCity(city);
                        dialog.show(getSupportFragmentManager(), "ACFModifyChoiceDialog");
                    }
                    return true;

                } else if (itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                    int groupPosition = ExpandableListView.getPackedPositionGroup(id);
                    ACFCityGroup group = (ACFCityGroup)listAdapter.getGroup(groupPosition);
                    ACFModifyChoiceDialog dialog = new ACFModifyChoiceDialog();
                    if(group.getDeviceId() == 1){
                        Toast.makeText(ACFCitySettingsActivity.this,"System Städte können nicht bearbeitet werden",Toast.LENGTH_SHORT).show();
                    } else {
                        dialog.setGroup(group);
                        dialog.show(getSupportFragmentManager(), "ACFModifyChoiceDialog");
                    }
                    return true;

                } else {
                    return false;
                }
            }

        });
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
        Intent intent = new Intent(this, ACFEditCityActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        if(((ACFModifyChoiceDialog)dialog).getCity() != null){
            Intent intent = new Intent(this, ACFEditCityActivity.class);
            Bundle arguments = new Bundle();
            arguments.putInt("city_id",((ACFModifyChoiceDialog)dialog).getCity().getId());
            intent.putExtras(arguments);
            startActivity(intent);
        } else if(((ACFModifyChoiceDialog)dialog).getGroup() != null){
            Intent intent = new Intent(this, ACFEditGroupActivity.class);
            Bundle arguments = new Bundle();
            arguments.putInt("group_id",((ACFModifyChoiceDialog)dialog).getGroup().getId());
            intent.putExtras(arguments);
            startActivity(intent);
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        AsyncTask task = new AsyncTask<Object,Void,Void>() {
            @Override
            protected Void doInBackground(Object... object) {
                ACFCitiesDatabaseController dbController = new ACFCitiesDatabaseController(ACFCitySettingsActivity.this);
                ACFRestDELETEController deleteController = new ACFRestDELETEController(ACFCitySettingsActivity.this);
                ACFJSONHandler jsonHandler = new ACFJSONHandler(ACFCitySettingsActivity.this);
                ACFCity city = ((ACFModifyChoiceDialog)object[0]).getCity();
                ACFCityGroup group = ((ACFModifyChoiceDialog)object[0]).getGroup();
                if(city != null){
                    String response = deleteController.deleteCityFromServer(city);
                    if(jsonHandler.checkSuccess(response)){
                        try {
                            dbController.deleteCity(city);
                        } catch (ACFDatabaseException e) {
                            final String msg = e.getMessage();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ACFCitySettingsActivity.this, msg, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                } else if(group != null){
                    String response = deleteController.deleteGroupFromServer(group);
                    if(jsonHandler.checkSuccess(response)){
                        try {
                            dbController.deleteGroup(group);
                        } catch (ACFDatabaseException e) {
                            Toast.makeText(ACFCitySettingsActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void v) {
                super.onPostExecute(v);
                onResume();
            }
        };
        task.execute(dialog);

    }
}
