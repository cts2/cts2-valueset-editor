package mayo.edu.cts2.editor.server.rest;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

public interface EntityClient {

	@GET
	@Path("/entities")
	@Produces(MediaType.APPLICATION_XML)
	String getMatchingEntities(@HeaderParam("Authorization") String auth,
	                           @QueryParam("maxtoreturn") int maxRecordsToReturn,
	                           @QueryParam("matchvalue") String matchValue);
}
