package com.project.mobop.augmentedcityfinder;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.HashMap;
import java.util.List;

/**
 * Created by tom on 05.04.2015.
 */
public class ACFExpandableListAdapter extends BaseExpandableListAdapter {

    private final ACFCitiesDatabaseController dbController;
    private Context context;
    private List<ACFCityGroup> listGroups; // header titles
    // child data in format of header title, child title
    private HashMap<Integer, List<ACFCity>> listCities;

    public ACFExpandableListAdapter(Context context, List<ACFCityGroup> listDataHeader,
                                    HashMap<Integer, List<ACFCity>> listChildData) {
        this.context = context;
        this.listGroups = listDataHeader;
        this.listCities = listChildData;
        this.dbController = new ACFCitiesDatabaseController(context);
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.listCities.get(this.listGroups.get(groupPosition).getId())
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = ((ACFCity) getChild(groupPosition, childPosition)).getCityName();

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);

        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listCities.get(this.listGroups.get(groupPosition).getId())
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listGroups.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listGroups.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = ((ACFCityGroup)getGroup(groupPosition)).getName();
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lbl_list_header);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        ToggleButton tbListHeader = (ToggleButton)convertView.findViewById(R.id.tb_list_header);
        ToggleButtonListener listener = new ToggleButtonListener();
        listener.setGroup((ACFCityGroup)getGroup(groupPosition));
        tbListHeader.setOnCheckedChangeListener(listener);

        if(((ACFCityGroup) getGroup(groupPosition)).isVisible()){
            tbListHeader.setChecked(true);
        } else {
            tbListHeader.setChecked(false);
        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private class ToggleButtonListener implements CompoundButton.OnCheckedChangeListener{

        private ACFCityGroup group;

        public ACFCityGroup getGroup() {
            return group;
        }

        public void setGroup(ACFCityGroup group) {
            this.group = group;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                group.setVisible(true);
            } else {
                group.setVisible(false);
            }
            try {
                dbController.updateGroupVisibility(group);
            } catch (ACFDatabaseException e) {
                Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
    }

}
