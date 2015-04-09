package tsm.mobop.project.acf.server.to;

import tsm.mobop.project.acf.server.ro.*;

import java.util.ArrayList;


public class ResponseBuilder {

    public Response buildCityResponse(ArrayList<City> dbCities){
        Cities cities = new Cities(dbCities);
        return new Response("ok", true, cities);
    }

    public Response buildCountryResponse(ArrayList<Country> dbCountries){
        Countries countries = new Countries(dbCountries);
        return new Response("ok", true, countries);
    }

    public Response buildContinentResponse(ArrayList<Continent> dbContinents){
        Continents continent = new Continents(dbContinents);
        return new Response("ok", true, continent);
    }

    public Response buildGroupResponse(ArrayList<Group> dbGroups){
        Groups continent = new Groups(dbGroups);
        return new Response("ok", true, continent);
    }

    public Response buildExceptionResponse(Exception e){
        return new Response(e.getMessage(), false, null);
    }

    public Response buildDeviceResponse(Device device) {
        return new Response("ok", true, device);
    }

    public Response buildUserLocationResponse(UserLocation userLocation) {
        return new Response("ok", true, userLocation);
    }

    public Response buildCityUsageResponse(CityUsage cityUsage){
        return new Response("ok", true, cityUsage);
    }
}
