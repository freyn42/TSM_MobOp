package tsm.mobop.project.acf.server.db;

import tsm.mobop.project.acf.server.ro.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class DBFacade{

    public ArrayList<City> getCities(long deviceId) throws Exception{
        ArrayList<City> cityList = new ArrayList<City>();

        Context initialContext = new InitialContext();
        DataSource datasource = (DataSource)initialContext.lookup("java:jboss/datasources/PostgreSQLDS");
        Connection con = datasource.getConnection();

        String query = "SELECT \"cityId\", \"cityName\", latitude, longitude, \"countryId\", \"deviceId\" FROM city " +
                "WHERE \"deviceId\" = ? AND \"active\" = TRUE;" ;
        PreparedStatement stmt = con.prepareStatement(query);
        stmt.setLong(1, deviceId);

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            cityList.add(new City(rs.getLong("cityId"), rs.getString("cityName"), rs.getDouble("latitude"),
                    rs.getDouble("longitude"), rs.getLong("countryId"), rs.getLong("deviceId")));
        }

        return cityList;
    }

    public ArrayList<City> createCity(City city) throws Exception{
        ArrayList<City> cityList = new ArrayList<City>();

        Context initialContext = new InitialContext();
        DataSource datasource = (DataSource)initialContext.lookup("java:jboss/datasources/PostgreSQLDS");
        Connection con = datasource.getConnection();

        String query = "INSERT INTO city(\"cityName\", latitude, longitude, \"countryId\", \"deviceId\")" +
                "    VALUES (?, ?, ?, ?, ?) RETURNING \"cityId\", \"cityName\", latitude, longitude, " +
                "\"countryId\", \"deviceId\";";
        PreparedStatement stmt = con.prepareStatement(query);

        stmt.setString(1, city.getName());
        stmt.setDouble(2,city.getLatitude());
        stmt.setDouble(3, city.getLongitude());
        stmt.setLong(4,city.getCountryId());
        stmt.setLong(5,city.getDeviceId());

        stmt.execute();
        ResultSet rs = stmt.getResultSet();
        rs.next();

        cityList.add(new City(rs.getLong("cityId"), rs.getString("cityName"), rs.getDouble("latitude"),
                rs.getDouble("longitude"), rs.getLong("countryId"), rs.getLong("deviceId")));
        return cityList;
    }

    public ArrayList<City> updateCity(long deviceId, long cityId, City city) throws Exception{
        ArrayList<City> cityList = new ArrayList<City>();

        Context initialContext = new InitialContext();
        DataSource datasource = (DataSource)initialContext.lookup("java:jboss/datasources/PostgreSQLDS");
        Connection con = datasource.getConnection();

        String query = "UPDATE city SET \"cityName\"=?, latitude=?, longitude=?, \"countryId\"=?" +
                "   WHERE \"cityId\"=? AND \"deviceId\"=? " +
                "RETURNING \"cityId\", \"cityName\", latitude, longitude, \"countryId\", \"deviceId\";";
        PreparedStatement stmt = con.prepareStatement(query);

        stmt.setString(1, city.getName());
        stmt.setDouble(2, city.getLatitude());
        stmt.setDouble(3,city.getLongitude());
        stmt.setLong(4, city.getCountryId());
        stmt.setLong(5,cityId);
        stmt.setLong(6,deviceId);


        stmt.execute();
        ResultSet rs = stmt.getResultSet();
        rs.next();

        cityList.add(new City(rs.getLong("cityId"), rs.getString("cityName"), rs.getDouble("latitude"),
                rs.getDouble("longitude"), rs.getLong("countryId"), rs.getLong("deviceId")));
        return cityList;
    }

    public ArrayList<City> deleteCity(long deviceId, long cityId) throws Exception{
        ArrayList<City> cityList = new ArrayList<City>();

        Context initialContext = new InitialContext();
        DataSource datasource = (DataSource)initialContext.lookup("java:jboss/datasources/PostgreSQLDS");
        Connection con = datasource.getConnection();

        String query = "UPDATE city SET \"active\"=FALSE WHERE \"cityId\"=? AND \"deviceId\"=?;";
        PreparedStatement stmt = con.prepareStatement(query);

        String query1 = "SELECT \"groupId\" FROM \"groupMembership\" WHERE \"cityId\" = ?;";
        PreparedStatement stmt1 = con.prepareStatement(query1);

        String query2 = "UPDATE \"cityGroup\" SET \"modificationDate\"=now() WHERE \"groupId\"=?;";
        PreparedStatement stmt2 = con.prepareStatement(query2);

        stmt.setLong(1, cityId);
        stmt.setLong(2, deviceId);

        stmt1.setLong(1, cityId);

        con.setAutoCommit(false);
        stmt.execute();
        stmt1.execute();

        ResultSet rs = stmt1.getResultSet();
        while(rs.next()){
            stmt2.setLong(1, rs.getLong("groupId"));
            stmt2.execute();
        }

        con.commit();

        cityList.add(new City());
        return cityList;
    }

    public ArrayList<Continent> getContinents() throws Exception{
        ArrayList<Continent> continentList = new ArrayList<Continent>();

        Context initialContext = new InitialContext();
        DataSource datasource = (DataSource)initialContext.lookup("java:jboss/datasources/PostgreSQLDS");
        Connection con = datasource.getConnection();

        String query = "SELECT \"continentId\", \"continentName\" FROM continent;" ;
        PreparedStatement stmt = con.prepareStatement(query);

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            continentList.add(new Continent(rs.getLong("continentId"), rs.getString("continentName")));
        }

        return continentList;
    }

    public ArrayList<Country> getCountries() throws Exception{
        ArrayList<Country> countryList = new ArrayList<Country>();

        Context initialContext = new InitialContext();
        DataSource datasource = (DataSource)initialContext.lookup("java:jboss/datasources/PostgreSQLDS");
        Connection con = datasource.getConnection();

        String query = "SELECT \"countryId\", \"countryName\", \"continentId\", \"countryCode\"\n FROM country;" ;
        PreparedStatement stmt = con.prepareStatement(query);

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            countryList.add(new Country(rs.getLong("countryId"), rs.getString("countryName"),
                    rs.getString("countryCode"), rs.getLong("continentId")));
        }

        return countryList;
    }

    public ArrayList<Group> getGroups(long deviceId) throws Exception{
        ArrayList<Group> groupList = new ArrayList<Group>();

        Context initialContext = new InitialContext();
        DataSource datasource = (DataSource)initialContext.lookup("java:jboss/datasources/PostgreSQLDS");
        Connection con = datasource.getConnection();

        String query = "SELECT cg.\"groupId\", cg.\"deviceId\", cg.\"groupName\", cg.\"creationDate\", " +
                "cg.\"modificationDate\", array(SELECT \"cityId\" FROM \"groupMembership\" " +
                "WHERE \"groupId\" = cg.\"groupId\" AND \"active\"=TRUE) as values" +
                "  FROM \"cityGroup\" AS cg WHERE cg.\"deviceId\" = ? AND \"active\"=TRUE;";

        PreparedStatement stmt = con.prepareStatement(query);
        stmt.setLong(1, deviceId);

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            groupList.add(new Group(rs.getLong("groupId"), rs.getLong("deviceId"), rs.getString("groupName"),
                    rs.getDate("creationDate"), rs.getDate("modificationDate"), (Long[]) rs.getArray("values").getArray()));
        }

        return groupList;
    }

    public ArrayList<Group> createGroup(Group group) throws Exception{
        ArrayList<Group> groupList = new ArrayList<Group>();

        Context initialContext = new InitialContext();
        DataSource datasource = (DataSource)initialContext.lookup("java:jboss/datasources/PostgreSQLDS");
        Connection con = datasource.getConnection();

        String query = "INSERT INTO \"cityGroup\"(\"deviceId\", \"groupName\")" +
                "    VALUES (?, ?) RETURNING \"groupId\", \"deviceId\", \"groupName\", \"creationDate\"," +
                "\"modificationDate\"";
        PreparedStatement stmt = con.prepareStatement(query);

        String query2 = "INSERT INTO \"groupMembership\"(\"cityId\", \"groupId\")" +
                "    VALUES (?, ?)";
        PreparedStatement stmt2 = con.prepareStatement(query2);

        stmt.setLong(1, group.getDeviceId());
        stmt.setString(2, group.getName());

        con.setAutoCommit(false);
        stmt.execute();
        ResultSet rs = stmt.getResultSet();
        rs.next();

        for (Long temp : group.getMembers()) {
            stmt2.setLong(1, temp);
            stmt2.setLong(2, rs.getLong("groupId"));
            stmt2.execute();
        }

        con.commit();

        groupList.add(new Group(rs.getLong("groupId"), rs.getLong("deviceId"), rs.getString("groupName"),
                rs.getDate("creationDate"), rs.getDate("modificationDate"),
                group.getMembers().toArray(new Long[group.getMembers().size()])));
        return groupList;
    }

    public ArrayList<Group> updateGroup(long deviceId, long groupId, Group group) throws Exception{
        ArrayList<Group> groupList = new ArrayList<Group>();

        Context initialContext = new InitialContext();
        DataSource datasource = (DataSource)initialContext.lookup("java:jboss/datasources/PostgreSQLDS");
        Connection con = datasource.getConnection();

        String query = "UPDATE \"cityGroup\" SET \"groupName\"=?, \"modificationDate\"=now() WHERE \"groupId\"=?" +
                        "RETURNING \"groupId\", \"deviceId\", \"groupName\", \"creationDate\", \"modificationDate\"";
        PreparedStatement stmt = con.prepareStatement(query);

        String query1 = "DELETE FROM \"groupMembership\" WHERE \"groupId\" = ?;";
        PreparedStatement stmt1 = con.prepareStatement(query1);

        String query2 = "INSERT INTO \"groupMembership\"(\"cityId\", \"groupId\")" +
                "    VALUES (?, ?)";
        PreparedStatement stmt2 = con.prepareStatement(query2);

        stmt.setString(1, group.getName());
        stmt.setLong(2, groupId);

        stmt1.setLong(1, groupId);

        con.setAutoCommit(false);

        stmt.execute();
        stmt1.execute();

        ResultSet rs = stmt.getResultSet();
        rs.next();

        for (Long temp : group.getMembers()) {
            stmt2.setLong(1, temp);
            stmt2.setLong(2, rs.getLong("groupId"));
            stmt2.execute();
        }

        con.commit();

        groupList.add(new Group(rs.getLong("groupId"), rs.getLong("deviceId"), rs.getString("groupName"),
                rs.getDate("creationDate"), rs.getDate("modificationDate"),
                group.getMembers().toArray(new Long[group.getMembers().size()])));
        return groupList;
    }

    public ArrayList<Group> deleteGroup(long groupId, long deviceId) throws Exception{
        ArrayList<Group> groupList = new ArrayList<Group>();

        Context initialContext = new InitialContext();
        DataSource datasource = (DataSource)initialContext.lookup("java:jboss/datasources/PostgreSQLDS");
        Connection con = datasource.getConnection();

        String query = "UPDATE \"cityGroup\" SET \"active\"=FALSE WHERE \"groupId\"=? AND \"deviceId\"=?;";
        PreparedStatement stmt = con.prepareStatement(query);

        stmt.setLong(1, groupId);
        stmt.setLong(2, deviceId);

        stmt.execute();
        groupList.add(new Group());
        return groupList;
    }

    public Device createDevice() throws Exception{
        Connection con;

        Context initialContext = new InitialContext();
        DataSource datasource = (DataSource)initialContext.lookup("java:jboss/datasources/PostgreSQLDS");
        con = datasource.getConnection();

        String query = "INSERT INTO device DEFAULT VALUES RETURNING \"deviceId\", \"registrationDate\";";

        PreparedStatement stmt = con.prepareStatement(query);
        stmt.execute();
        ResultSet rs = stmt.getResultSet();
        rs.next();

        return new Device(rs.getLong("deviceId"), rs.getDate("registrationDate"));
    }

    public UserLocation createUserLocation(UserLocation location) throws Exception{
        Connection con;

        Context initialContext = new InitialContext();
        DataSource datasource = (DataSource)initialContext.lookup("java:jboss/datasources/PostgreSQLDS");
        con = datasource.getConnection();

        String query = "INSERT INTO \"userLocation\"(\"deviceId\", latitude, longitude) VALUES (?, ?, ?)" +
                " RETURNING \"userLocationId\", \"deviceId\", \"creationDate\", latitude, longitude;";
        PreparedStatement stmt = con.prepareStatement(query);

        stmt.setLong(1, location.getDeviceId());
        stmt.setDouble(2, location.getLatitude());
        stmt.setDouble(3, location.getLongitude());

        stmt.execute();
        ResultSet rs = stmt.getResultSet();
        rs.next();

        return new UserLocation(rs.getLong("userLocationId"), rs.getLong("deviceId"), rs.getDate("creationDate"),
                rs.getDouble("latitude"), rs.getDouble("longitude"));
    }

    public CityUsage createCityUsage(CityUsage cityUsage) throws Exception{
        Connection con;

        Context initialContext = new InitialContext();
        DataSource datasource = (DataSource)initialContext.lookup("java:jboss/datasources/PostgreSQLDS");
        con = datasource.getConnection();

        String query = "INSERT INTO \"cityUsage\"(\"cityId\", \"userLocationId\") VALUES (?, ?)" +
                " RETURNING \"cityUsageId\", \"cityId\", \"userLocationId\";";
        PreparedStatement stmt = con.prepareStatement(query);

        stmt.setLong(1, cityUsage.getCityId());
        stmt.setLong(2, cityUsage.getUserLocationId());

        stmt.execute();
        ResultSet rs = stmt.getResultSet();
        rs.next();

        return new CityUsage(rs.getLong("cityUsageId"), rs.getLong("userLocationId"), rs.getLong("cityId"));
    }
}