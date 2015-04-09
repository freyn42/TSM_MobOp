package com.project.mobop.augmentedcityfinder;

/**
 * Created by tom on 09.04.2015.
 */
public class ACFCitiesDatabase {

    // ACF table names
    public static final String TABLE_CITY = "city";
    public static final String TABLE_COUNTRY = "country";
    public static final String TABLE_CONTINENT = "continent";
    public static final String TABLE_DEVICE = "device";
    public static final String TABLE_GROUP = "group_list";
    public static final String TABLE_CITY_GROUP = "city_group";
    public static final String TABLE_USER_LOCATION = "user_location";
    public static final String TABLE_USER_LOCATION_CITY = "user_location_city";

    // City Table Columns names
    public static final String KEY_CITY_ID = "city_id";
    public static final String KEY_CITY_NAME = "city_name";
    public static final String KEY_CITY_COUNTRY = "city_country";
    public static final String KEY_CITY_LATITUDE = "city_latitude";
    public static final String KEY_CITY_LONGITUDE = "city_longitude";
    public static final String KEY_CITY_DEVICE = "city_device";
    public static final String KEY_CITY_DELETED = "city_deleted";

    // Country Table Columns names
    public static final String KEY_COUNTRY_ID = "country_id";
    public static final String KEY_COUNTRY_CONTINENT = "country_continent";
    public static final String KEY_COUNTRY_NAME = "country_name";
    public static final String KEY_COUNTRY_CODE = "country_code";

    // Continent Table Columns names
    public static final String KEY_CONTINENT_ID = "continent_id";
    public static final String KEY_CONTINENT_NAME = "continent_name";

    // Device Table Columns names
    public static final String KEY_DEVICE_ID = "device_id";
    public static final String KEY_DEVICE_REGISTRATION_DATE = "device_registration_date";

    // Group Table Columns names
    public static final String KEY_GROUP_ID = "group_id";
    public static final String KEY_GROUP_DEVICE = "group_device";
    public static final String KEY_GROUP_NAME = "group_name";
    public static final String KEY_GROUP_CREATION_DATE = "group_creation_date";
    public static final String KEY_GROUP_MODIFICATION_DATE = "group_modification_date";
    public static final String KEY_GROUP_VISIBLE = "group_show";
    public static final String KEY_GROUP_DELETED = "group_deleted";

    //City Group Join Table Columns Names
    public static final String KEY_CITY_GROUP_ID = "city_group_id";
    public static final String KEY_CITY_GROUP_CITY = "city_group_city";
    public static final String KEY_CITY_GROUP_GROUP = "city_group_group";

    //User Location Table Columns Names
    public static final String KEY_USER_LOCATION_ID = "user_location_id";
    public static final String KEY_USER_LOCATION_DATE = "user_location_date";
    public static final String KEY_USER_LOCATION_DEVICE = "user_location_device";
    public static final String KEY_USER_LOCATION_LATITUDE = "user_location_latitude";
    public static final String KEY_USER_LOCATION_LONGITUDE = "user_location_longitude";

    //User Location City Join Table Columns Names
    public static final String KEY_USER_LOCATION_CITY_ID = "user_location_city_id";
    public static final String KEY_USER_LOCATION_CITY_CITY = "user_location_city_city";
    public static final String KEY_USER_LOCATION_CITY_USER_LOCATION = "user_location_city_user_location";

    public static final String CREATE_CONTINENT_TABLE = "CREATE TABLE " + TABLE_CONTINENT + "("
            + KEY_CONTINENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_CONTINENT_NAME + " TEXT"
            + ");";

    public static final String CREATE_COUNTRY_TABLE = "CREATE TABLE " + TABLE_COUNTRY + "("
            + KEY_COUNTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_COUNTRY_NAME + " TEXT,"
            + KEY_COUNTRY_CONTINENT + " INTEGER ,"
            + KEY_COUNTRY_CODE + " TEXT,"
            + "FOREIGN KEY (" + KEY_COUNTRY_CONTINENT + ") REFERENCES " + TABLE_CONTINENT + "(" + KEY_CONTINENT_ID + ")"
            + ");";

    public static final String CREATE_CITY_TABLE = "CREATE TABLE " + TABLE_CITY + "("
            + KEY_CITY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_CITY_NAME + " TEXT,"
            + KEY_CITY_COUNTRY + " INTEGER,"
            + KEY_CITY_LATITUDE + " DECIMAL,"
            + KEY_CITY_LONGITUDE + " DECIMAL,"
            + KEY_CITY_DEVICE + " INTEGER, "
            + KEY_CITY_DELETED + " BOOLEAN DEFAULT 0, "
            + "FOREIGN KEY (" + KEY_CITY_COUNTRY + ") REFERENCES " + TABLE_COUNTRY + "(" + KEY_COUNTRY_ID + "),"
            + "FOREIGN KEY (" + KEY_CITY_DEVICE + ") REFERENCES " + TABLE_DEVICE + "(" + KEY_DEVICE_ID + ")"
            + ");";

    public static final String CREATE_DEVICE_TABLE = "CREATE TABLE " + TABLE_DEVICE + "("
            + KEY_DEVICE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_DEVICE_REGISTRATION_DATE + " DATETIME"
            + ");";

    public static final String CREATE_GROUP_TABLE = "CREATE TABLE " + TABLE_GROUP + "("
            + KEY_GROUP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_GROUP_NAME + " TEXT,"
            + KEY_GROUP_DEVICE + " INTEGER,"
            + KEY_GROUP_CREATION_DATE + " DATETIME,"
            + KEY_GROUP_MODIFICATION_DATE + " DATETIME,"
            + KEY_GROUP_VISIBLE + " BOOLEAN DEFAULT 1,"
            + KEY_GROUP_DELETED + " BOOLEAN DEFAULT 0, "
            + "FOREIGN KEY (" + KEY_GROUP_DEVICE + ") REFERENCES " + TABLE_DEVICE + "(" + KEY_DEVICE_ID + ")"
            + ");";

    public static final String CREATE_CITY_GROUP_TABLE = "CREATE TABLE " + TABLE_CITY_GROUP + "("
            + KEY_CITY_GROUP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_CITY_GROUP_CITY + " INTEGER,"
            + KEY_CITY_GROUP_GROUP + " INTEGER,"
            + "FOREIGN KEY (" + KEY_CITY_GROUP_CITY + ") REFERENCES " + TABLE_CITY + "(" + KEY_CITY_ID + "),"
            + "FOREIGN KEY (" + KEY_CITY_GROUP_GROUP + ") REFERENCES " + TABLE_GROUP + "(" + KEY_GROUP_ID + ")"
            + ");";

    public static final String CREATE_USER_LOCATION_TABLE = "CREATE TABLE " + TABLE_USER_LOCATION + "("
            + KEY_USER_LOCATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_USER_LOCATION_DATE + " DATETIME,"
            + KEY_USER_LOCATION_DEVICE + " INTEGER,"
            + KEY_USER_LOCATION_LONGITUDE + " DECIMAL,"
            + KEY_USER_LOCATION_LATITUDE + " DECIMAL,"
            + "FOREIGN KEY (" + KEY_USER_LOCATION_DEVICE + ") REFERENCES " + TABLE_DEVICE + "(" + KEY_DEVICE_ID + ")"
            + ");";

    public static final String CREATE_USER_LOCATION_CITY_TABLE = "CREATE TABLE " + TABLE_USER_LOCATION_CITY + "("
            + KEY_USER_LOCATION_CITY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_USER_LOCATION_CITY_CITY + " INTEGER,"
            + KEY_USER_LOCATION_CITY_USER_LOCATION + " INTEGER,"
            + "FOREIGN KEY (" + KEY_USER_LOCATION_CITY_CITY + ") REFERENCES " + TABLE_CITY + "(" + KEY_CITY_ID + "),"
            + "FOREIGN KEY (" + KEY_USER_LOCATION_CITY_USER_LOCATION + ") REFERENCES " + TABLE_USER_LOCATION + "(" + KEY_USER_LOCATION_ID + ")"
            + ");";

    public static final String SELECT_ALL_CITIES = "SELECT " + KEY_CITY_ID + "," + KEY_CITY_DEVICE + "," + KEY_CITY_NAME + "," + KEY_CITY_LONGITUDE + "," + KEY_CITY_LATITUDE + "," + KEY_COUNTRY_NAME + "," + KEY_CONTINENT_NAME
            + " FROM " + TABLE_CITY  + " JOIN " + TABLE_COUNTRY + " ON " + KEY_CITY_COUNTRY + " =" + KEY_COUNTRY_ID
            + " JOIN " + TABLE_CONTINENT + " ON " + KEY_COUNTRY_CONTINENT + " =" + KEY_CONTINENT_ID
            + " WHERE " + KEY_CITY_DELETED + " <> 1 "
            + ";";

    public static final String SELECT_ALL_GROUPS = "SELECT " + KEY_GROUP_ID + "," + KEY_GROUP_NAME + "," + KEY_GROUP_VISIBLE + "," + KEY_GROUP_DEVICE
            + " FROM " + TABLE_GROUP
            + " WHERE " + KEY_GROUP_DELETED + " <> 1 "
            + ";";

    public static final String SELECT_CITIES_FROM_GROUP = "SELECT " + KEY_CITY_ID + "," + KEY_CITY_DEVICE + "," + KEY_CITY_NAME + "," + KEY_CITY_LONGITUDE + "," + KEY_CITY_LATITUDE + "," + KEY_COUNTRY_NAME + "," + KEY_CONTINENT_NAME + "," + KEY_COUNTRY_ID
            + " FROM " + TABLE_GROUP + " JOIN " + TABLE_CITY_GROUP + " ON " + KEY_GROUP_ID + " =" + KEY_CITY_GROUP_GROUP
            + " JOIN " + TABLE_CITY + " ON " + KEY_CITY_GROUP_CITY + " =" + KEY_CITY_ID
            + " JOIN " + TABLE_COUNTRY + " ON " + KEY_CITY_COUNTRY + " =" + KEY_COUNTRY_ID
            + " JOIN " + TABLE_CONTINENT + " ON " + KEY_COUNTRY_CONTINENT + " =" + KEY_CONTINENT_ID
            + " WHERE " + KEY_CITY_DELETED + " <> 1 "
            + " AND " + KEY_GROUP_ID + " = ";

    public static final String SELECT_VISIBLE_CITIES = "SELECT " + KEY_CITY_ID + "," + KEY_CITY_DEVICE + "," + KEY_CITY_NAME + "," + KEY_CITY_LONGITUDE + "," + KEY_CITY_LATITUDE + "," + KEY_COUNTRY_NAME + "," + KEY_CONTINENT_NAME + "," + KEY_COUNTRY_ID
            + " FROM " + TABLE_GROUP + " JOIN " + TABLE_CITY_GROUP + " ON " + KEY_GROUP_ID + " =" + KEY_CITY_GROUP_GROUP
            + " JOIN " + TABLE_CITY + " ON " + KEY_CITY_GROUP_CITY + " =" + KEY_CITY_ID
            + " JOIN " + TABLE_COUNTRY + " ON " + KEY_CITY_COUNTRY + " =" + KEY_COUNTRY_ID
            + " JOIN " + TABLE_CONTINENT + " ON " + KEY_COUNTRY_CONTINENT + " =" + KEY_CONTINENT_ID
            + " WHERE " + KEY_GROUP_VISIBLE + " = 1"
            + " AND " + KEY_CITY_DELETED + " <> 1 "
            + ";";

    public static final String SELECT_DEVICE_ID = "SELECT " + KEY_DEVICE_ID
            + " FROM " + TABLE_DEVICE
            + " WHERE " + KEY_DEVICE_ID + " <> " + 1
            + ";";

    public static final String SELECT_GROUP = "SELECT " + KEY_GROUP_ID + "," + KEY_GROUP_NAME + "," + KEY_GROUP_VISIBLE + "," + KEY_GROUP_DEVICE
            + " FROM " + TABLE_GROUP
            + " WHERE " + KEY_GROUP_DELETED + " <> 1 "
            + " AND " + KEY_GROUP_ID + " = ";

    public static final String SELECT_ALL_COUNTRIES = "SELECT " + KEY_COUNTRY_ID + "," + KEY_COUNTRY_NAME + "," + KEY_COUNTRY_CODE
            + " FROM " +TABLE_COUNTRY + ";";

    public static final String SELECT_CITY = "SELECT " + KEY_CITY_ID + "," + KEY_CITY_DEVICE + "," + KEY_CITY_NAME + "," + KEY_CITY_LONGITUDE + "," + KEY_CITY_LATITUDE + "," + KEY_COUNTRY_NAME + "," + KEY_CONTINENT_NAME + "," + KEY_COUNTRY_ID
            + " FROM " + TABLE_CITY
            + " JOIN " + TABLE_COUNTRY + " ON " + KEY_CITY_COUNTRY + " =" + KEY_COUNTRY_ID
            + " JOIN " + TABLE_CONTINENT + " ON " + KEY_COUNTRY_CONTINENT + " =" + KEY_CONTINENT_ID
            + " WHERE " + KEY_CITY_DELETED + " <> 1 "
            + " AND " + KEY_CITY_ID + " = ";

    public static final String SELECT_COUNTRY = "SELECT " + KEY_COUNTRY_ID + "," + KEY_COUNTRY_CODE + "," + KEY_COUNTRY_NAME
            + " FROM " + TABLE_COUNTRY
            + " WHERE " + KEY_COUNTRY_ID + " = ";


}
