package com.project.mobop.augmentedcityfinder;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by tom on 06.04.2015.
 */
public class ACFEditGroupActivity extends Activity{

    private ListView lv_edit_cities;
    private ACFCitiesDatabaseController dbController;
    private ACFRestPOSTController postController;
    private ACFRestPUTController putController;
    private ACFCityGroup group;
    private EditGroupCitiesAdapter adapter;
    private EditText etGroupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_group);

        dbController = new ACFCitiesDatabaseController(this);

        postController = new ACFRestPOSTController(this,dbController);
        putController = new ACFRestPUTController(this,dbController);

        lv_edit_cities = (ListView)findViewById(R.id.lv_edit_group_cities);
        List<ACFCity> cities = dbController.getAllCities();
        adapter = new EditGroupCitiesAdapter(this,cities);
        lv_edit_cities.setAdapter(adapter);
        etGroupName = (EditText)findViewById(R.id.et_edit_group_name);

        Bundle b = getIntent().getExtras();
        if(b == null){
            group = new ACFCityGroup();
            group.setDeviceId(dbController.getDeviceId());
        } else {
            group = dbController.getGroup(b.getInt("group_id"));
            adapter.getCheckedCities().addAll(group.getCityList());
            etGroupName.setText(group.getName());
        }

    }

    public void onButtonEditGroupSaveClicked(View v){
        if(group.getId() == 0){
            String groupName = etGroupName.getText().toString();
            group.setName(groupName);
            group.setCityList(adapter.getCheckedCities());
            String response = postController.postGroupToServer(group);
            try {
                dbController.addGroups(response);
            } catch (JSONException e) {
                Toast.makeText(this, "Error saving City to Database:\n" + response, Toast.LENGTH_SHORT).show();
            }
        } else {
            String response = putController.putGroupToServer(group);
            if(response != null && !response.isEmpty()) {
                try {
                    dbController.updateGroups(response);
                } catch (JSONException e) {
                    Toast.makeText(this, "Error saving City to Database:\n" + response, Toast.LENGTH_SHORT).show();
                }
            }
        }
        finish();
    }

    private class EditGroupCitiesAdapter implements ListAdapter {

        private List<ACFCity> cities;
        private List<ACFCity> checkedCities;
        private Context context;

        public EditGroupCitiesAdapter(Context context, List<ACFCity> cities){
            Collections.sort(cities);
            this.cities = cities;
            this.context = context;
            checkedCities = new ArrayList<ACFCity>();
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public int getCount() {
            return cities.size();
        }

        @Override
        public Object getItem(int position) {
            return cities.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.list_item_edit_group_cities, parent, false);
            CheckBox cbCity = (CheckBox) convertView.findViewById(R.id.cb_edit_group_city);
            cbCity.setText(((ACFCity) getItem(position)).getCityName());

            if(checkedCities.contains(getItem(position))){
                cbCity.setChecked(true);
            }

            cbCity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        checkedCities.add((ACFCity)getItem(position));
                    } else {
                        checkedCities.remove(getItem(position));
                    }
                }
            });
            return convertView;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        public List<ACFCity> getCheckedCities() {
            return checkedCities;
        }

    }

}
