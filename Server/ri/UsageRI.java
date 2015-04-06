package tsm.mobop.project.acf.server.ri;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import tsm.mobop.project.acf.server.db.DBFacade;
import tsm.mobop.project.acf.server.ro.City;
import tsm.mobop.project.acf.server.ro.CityUsage;
import tsm.mobop.project.acf.server.ro.UserLocation;
import tsm.mobop.project.acf.server.to.ResponseBuilder;
import tsm.mobop.project.acf.server.to.Response;

@Path("/usage")
public class UsageRI {
    private DBFacade db = new DBFacade();
    private ResponseBuilder rb = new ResponseBuilder();

    @POST
    @Path("/userlocation")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createLocation(UserLocation location){
        try{
            return rb.buildUserLocationResponse(this.db.createUserLocation(location));
        }
        catch(Exception e){
            return rb.buildExceptionResponse(e);
        }
    }

    @POST
    @Path("/city")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCityUsage(CityUsage cityUsage){
        try{
            return rb.buildCityUsageResponse(this.db.createCityUsage(cityUsage));
        }
        catch(Exception e){
            return rb.buildExceptionResponse(e);
        }
    }
}