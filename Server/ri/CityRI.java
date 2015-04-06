package tsm.mobop.project.acf.server.ri;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import tsm.mobop.project.acf.server.db.DBFacade;
import tsm.mobop.project.acf.server.ro.City;
import tsm.mobop.project.acf.server.to.ResponseBuilder;
import tsm.mobop.project.acf.server.to.Response;

@Path("/city")
public class CityRI {
    private DBFacade db = new DBFacade();
    private ResponseBuilder rb = new ResponseBuilder();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCities(){
        try{
            return rb.buildCityResponse(this.db.getCities(1));
        }
        catch(Exception e){
            return rb.buildExceptionResponse(e);
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCity(City city){
        try{
            return rb.buildCityResponse(this.db.createCity(city));
        }
        catch(Exception e){
            return rb.buildExceptionResponse(e);
        }
    }

    @GET
    @Path("/{deviceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCitiesById(@PathParam("deviceId") long deviceId) {
        try{
            return rb.buildCityResponse(this.db.getCities(deviceId));
        }
        catch(Exception e){
            return rb.buildExceptionResponse(e);
        }
    }

    @PUT
    @Path("/{deviceId}/{cityId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCity(@PathParam("deviceId") long deviceId,
                               @PathParam("cityId") long cityId, City city){
        try{
            return rb.buildCityResponse(this.db.updateCity(deviceId, cityId, city));
        }
        catch(Exception e){
            return rb.buildExceptionResponse(e);
        }
    }

    @DELETE
    @Path("/{deviceId}/{cityId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCity(@PathParam("deviceId") long deviceId,
                               @PathParam("cityId") long cityId){
        try{
            return rb.buildCityResponse(this.db.deleteCity(deviceId, cityId));
        }
        catch(Exception e){
            return rb.buildExceptionResponse(e);
        }
    }

}