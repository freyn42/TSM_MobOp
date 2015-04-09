package tsm.mobop.project.acf.server.ri;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import tsm.mobop.project.acf.server.db.DBFacade;
import tsm.mobop.project.acf.server.to.ResponseBuilder;
import tsm.mobop.project.acf.server.to.Response;

@Path("/device")
public class DeviceRI {
    private DBFacade db = new DBFacade();
    private ResponseBuilder rb = new ResponseBuilder();

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDevices(){
        try{
            return rb.buildDeviceResponse(this.db.createDevice());
        }
        catch(Exception e){
            return rb.buildExceptionResponse(e);
        }
    }
}