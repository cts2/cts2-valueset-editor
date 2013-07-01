package mayo.edu.cts2.editor.server.rest;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

public interface EntityClient {

	@GET
	@Path("/entities")
	@Produces(MediaType.APPLICATION_XML)
	String getMatchingEntities(@HeaderParam("Authorization")String auth,
	                           @QueryParam("maxtoreturn") int maxRecordsToReturn,
	                           @QueryParam("matchvalue") String matchValue);

	@GET
	@Path("/codesystem/{codeSystem}/version/{codeSystemVersion}/entities")
	@Produces(MediaType.APPLICATION_XML)
	String getMatchingEntities(@HeaderParam("Authorization") String auth,
	                           @PathParam("codeSystem") String codeSystem,
	                           @PathParam("codeSystemVersion") String codeSystemVersion,
	                           @QueryParam("matchvalue") String matchValue,
	                           @QueryParam("maxtoreturn") int maxRecordsToReturn);

	@GET
	@Path("/codesystems")
	@Produces(MediaType.APPLICATION_XML)
	String getCodeSystems(@HeaderParam("Authorization") String auth,
	                      @QueryParam("maxtoreturn") int maxRecordsToReturn);

	@GET
	@Path("codesystem/{codeSystem}/versions")
	@Produces(MediaType.APPLICATION_XML)
	String getCodeSystemVersions(@HeaderParam("Authorization") String auth,
	                             @PathParam("codeSystem") String codeSystem,
	                             @QueryParam("maxtoreturn") int maxRecordsToReturn);
}
