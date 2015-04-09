package tsm.mobop.project.acf.server.ri;

import tsm.mobop.project.acf.server.db.DBFacade;
import tsm.mobop.project.acf.server.ro.City;
import tsm.mobop.project.acf.server.ro.Group;
import tsm.mobop.project.acf.server.to.Response;
import tsm.mobop.project.acf.server.to.ResponseBuilder;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/group")
public class GroupRI {
    private DBFacade db = new DBFacade();
    private ResponseBuilder rb = new ResponseBuilder();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGroups() {
        try {
            return rb.buildGroupResponse(this.db.getGroups(1));
        } catch (Exception e) {
            return rb.buildExceptionResponse(e);
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createGroup(Group group){
        try{
            return rb.buildGroupResponse(this.db.createGroup(group));
        }
        catch(Exception e){
            return rb.buildExceptionResponse(e);
        }
    }

    @GET
    @Path("/{deviceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGroupsById(@PathParam("deviceId") long deviceId) {
        try{
            return rb.buildGroupResponse(this.db.getGroups(deviceId));
        }
        catch(Exception e){
            return rb.buildExceptionResponse(e);
        }
    }

    @PUT
    @Path("/{deviceId}/{groupId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateGroup(@PathParam("deviceId") long deviceId, @PathParam("groupId") long groupId, Group group){
        try{
            return rb.buildGroupResponse(this.db.updateGroup(deviceId, groupId, group));
        }
        catch(Exception e){
            return rb.buildExceptionResponse(e);
        }
    }

    @DELETE
    @Path("/{deviceId}/{groupId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteGroup(@PathParam("deviceId") long deviceId, @PathParam("groupId") long groupId){
        try{
            return rb.buildGroupResponse(this.db.deleteGroup(groupId, deviceId));
        }
        catch(Exception e){
            return rb.buildExceptionResponse(e);
        }
    }
}
