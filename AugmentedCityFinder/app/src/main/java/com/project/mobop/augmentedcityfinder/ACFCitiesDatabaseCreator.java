package com.project.mobop.augmentedcityfinder;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by tom on 09.04.2015.
 */
public class ACFCitiesDatabaseCreator {

    public void createContinentTable(SQLiteDatabase db){
        try{
            db.beginTransaction();
            db.execSQL(ACFCitiesDatabase.CREATE_CONTINENT_TABLE);
            db.setTransactionSuccessful();
        } catch(SQLException sqlE){
            Log.e("ACFCitiesDatabase", sqlE.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    public void createCountryTable(SQLiteDatabase db){
        try {
            db.beginTransaction();
            db.execSQL(ACFCitiesDatabase.CREATE_COUNTRY_TABLE);
            db.setTransactionSuccessful();
        } catch(SQLException sqlE){
            Log.e("ACFCitiesDatabase",sqlE.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    public void createCityTable(SQLiteDatabase db){
        try{
            db.beginTransaction();
            db.execSQL(ACFCitiesDatabase.CREATE_CITY_TABLE);
            db.setTransactionSuccessful();
        } catch(SQLException sqlE){
            Log.e("ACFCitiesDatabase",sqlE.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    public void createDeviceTable(SQLiteDatabase db){
        try{
            db.beginTransaction();
            db.execSQL(ACFCitiesDatabase.CREATE_DEVICE_TABLE);
            db.setTransactionSuccessful();
        } catch(SQLException sqlE){
            Log.e("ACFCitiesDatabase",sqlE.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    public void createGroupTable(SQLiteDatabase db){
        try{
            db.beginTransaction();
            db.execSQL(ACFCitiesDatabase.CREATE_GROUP_TABLE);
            db.setTransactionSuccessful();
        } catch(SQLException sqlE){
            Log.e("ACFCitiesDatabase",sqlE.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    public void createCityGroupTable(SQLiteDatabase db){
        try{
            db.beginTransaction();
            db.execSQL(ACFCitiesDatabase.CREATE_CITY_GROUP_TABLE);
            db.setTransactionSuccessful();
        } catch(SQLException sqlE){
            Log.e("ACFCitiesDatabase",sqlE.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    public void createUserLocationTable(SQLiteDatabase db){
        try{
            db.beginTransaction();
            db.execSQL(ACFCitiesDatabase.CREATE_USER_LOCATION_TABLE);
            db.setTransactionSuccessful();
        } catch(SQLException sqlE){
            Log.e("ACFCitiesDatabase",sqlE.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    public void createUserLocationCityTable(SQLiteDatabase db){
        try{
            db.beginTransaction();
            db.execSQL(ACFCitiesDatabase.CREATE_USER_LOCATION_CITY_TABLE);
            db.setTransactionSuccessful();
        } catch(SQLException sqlE){
            Log.e("ACFCitiesDatabase",sqlE.getMessage());
        } finally {
            db.endTransaction();
        }
    }
}
