package com.project.mobop.augmentedcityfinder;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tom on 06.04.2015.
 */
public class ACFEditGroupActivity extends Activity{

    private ListView lv_edit_cities;
    ACFCitiesDatabaseController dbController;
    ACFRestPOSTController postController;
    ACFCityGroup group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_group);

        dbController = new ACFCitiesDatabaseController(this);
        Bundle b = getIntent().getExtras();
        if(b == null){
            group = new ACFCityGroup();
            group.setDeviceId(dbController.getDeviceId());
        } else {
            group = dbController.getGroup(b.getInt("group_id"));
        }

        postController = new ACFRestPOSTController(this,dbController);

        lv_edit_cities = (ListView)findViewById(R.id.lv_edit_group_cities);
        List<ACFCity> cities = dbController.getAllCities();
        EditGroupCitiesAdapter adapter = new EditGroupCitiesAdapter(this,cities);
        lv_edit_cities.setAdapter(adapter);


    }

    public void onButtonEditGroupSaveClicked(View v){
        if(group.getId() == 0){
            String response = postController.postGroupToServer(group);
        }
        finish();
    }

    private class EditGroupCitiesAdapter implements ListAdapter {

        List<ACFCity> cities;
        List<Integer> checkedItems;
        Context context;

        public EditGroupCitiesAdapter(Context context, List<ACFCity> cities){
            this.cities = cities;
            this.context = context;
            checkedItems = new ArrayList<Integer>();
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

            if(checkedItems.contains(position)){
                cbCity.setChecked(true);
            }

            cbCity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        checkedItems.add(position);
                    } else {
                        checkedItems.remove((Integer)position);
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


    }

}
