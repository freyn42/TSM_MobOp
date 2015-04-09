package com.project.mobop.augmentedcityfinder;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by tom on 06.04.2015.
 */
public class ACFEditCityActivity extends Activity{

    private ACFCitiesDatabaseController dbController;
    private ACFRestPOSTController postController;
    private ACFRestPUTController putController;
    private ACFCity city;
    private EditText etCityName, etLongitude, etLatitude;
    private Spinner spCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_city);

        dbController = new ACFCitiesDatabaseController(this);

        postController = new ACFRestPOSTController(this);
        putController = new ACFRestPUTController(this);

        etCityName = (EditText)findViewById(R.id.et_edit_city_name);
        etLongitude = (EditText)findViewById(R.id.et_edit_city_longitude);
        etLatitude = (EditText)findViewById(R.id.et_edit_city_latitude);
        spCountry = (Spinner)findViewById(R.id.sp_edit_city_country);

        List<ACFCountry> countryList = dbController.getAllCountries();
        Collections.sort(countryList);
        ArrayAdapter<ACFCountry> dataAdapter = new CountryArrayAdapter
                (this, android.R.layout.simple_spinner_item,countryList);

        spCountry.setAdapter(dataAdapter);
        spCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                city.setCountryId(((ACFCountry) parent.getItemAtPosition(position)).getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        Bundle b = getIntent().getExtras();
        if(b == null){
            city = new ACFCity();
            city.setDeviceId(dbController.getDeviceId());
        } else {
            city = dbController.getCity(b.getInt("city_id"));
            etCityName.setText(city.getCityName());
            etLongitude.setText(String.valueOf(city.getLongitude()));
            etLatitude.setText(String.valueOf(city.getLatitude()));
            ACFCountry country = dbController.getCountry(city.getCountryId());
            spCountry.setSelection(dataAdapter.getPosition(country));
        }
    }

    public void onButtonEditCitySaveClicked(View v){
        if(city.getId() == 0){
            String cityName = etCityName.getText().toString();
            city.setCityName(cityName);
            city.setDeviceId(dbController.getDeviceId());
            Location location = new Location("city");
            location.setLatitude(Double.valueOf(etLatitude.getText().toString()));
            location.setLongitude(Double.valueOf(etLongitude.getText().toString()));
            city.setLocation(location);
            String response = postController.postCityToServer(city);
            if(response != null && !response.isEmpty()) {
                try {
                    dbController.addCities(response);
                } catch (JSONException e) {
                     Toast.makeText(this, "Fehler beim Erstellen der Stadt auf dem Server",Toast.LENGTH_SHORT).show();
                } catch (ACFDatabaseException e){
                    Toast.makeText(this, e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(this, "Leere Antwort des Servers",Toast.LENGTH_SHORT).show();
            }
        } else {
            String cityName = etCityName.getText().toString();
            city.setCityName(cityName);
            Location location = new Location("city");
            location.setLatitude(Double.valueOf(etLatitude.getText().toString()));
            location.setLongitude(Double.valueOf(etLongitude.getText().toString()));
            city.setLocation(location);
            String response = putController.putCityToServer(city);
            if(response != null && !response.isEmpty()) {
                try {
                    dbController.updateCities(response);
                } catch (JSONException e) {
                    Toast.makeText(this, "Fehler beim Erstellen der Stadt auf dem Server",Toast.LENGTH_SHORT).show();
                } catch (ACFDatabaseException e) {
                    Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Leere Antwort des Servers",Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    private class CountryArrayAdapter extends ArrayAdapter<ACFCountry>{

        public CountryArrayAdapter(Context context, int resource, List<ACFCountry> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            convertView = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
            TextView tvCountry = (TextView) convertView.findViewById(android.R.id.text1);
            tvCountry.setText(getItem(position).getName());
            tvCountry.setPadding(5,5,0,5);
            tvCountry.setTextSize(18);
            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            convertView = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
            TextView tvCountry = (TextView) convertView.findViewById(android.R.id.text1);
            tvCountry.setText(getItem(position).getName());
            tvCountry.setPadding(5,5,0,5);
            tvCountry.setTextSize(18);
            return convertView;
        }
    }
}
