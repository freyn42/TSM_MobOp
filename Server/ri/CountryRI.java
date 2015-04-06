package tsm.mobop.project.acf.server.ri;

import tsm.mobop.project.acf.server.db.DBFacade;
import tsm.mobop.project.acf.server.to.Response;
import tsm.mobop.project.acf.server.to.ResponseBuilder;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/country")
public class CountryRI {
    private DBFacade db = new DBFacade();
    private ResponseBuilder rb = new ResponseBuilder();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCountries() {
        try {
            return rb.buildCountryResponse(this.db.getCountries());
        } catch (Exception e) {
            return rb.buildExceptionResponse(e);
        }
    }
}
