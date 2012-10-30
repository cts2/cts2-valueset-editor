package mayo.edu.cts2.editor.server.rest;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

public interface Cts2Client {

	@GET
	@Path("/valuesets?maxtoreturn={maxtoreturn}")
	@Produces(MediaType.APPLICATION_XML)
	String getValueSets(@HeaderParam("Authorization") String auth,
	                    @PathParam("maxtoreturn") int maxRecordsToReturn);

	@GET
	@Path("/valueset/{oid}")
	@Produces(MediaType.APPLICATION_XML)
	String getValueSet(@HeaderParam("Authorization") String auth,
	                   @PathParam("oid") String oid);

	@GET
	@Path("/valueset/{oid}/definitions?maxtoreturn={maxtoreturn}")
	@Produces(MediaType.APPLICATION_XML)
	String getDefinitions(@HeaderParam("Authorization") String auth,
	                      @PathParam("oid") String oid,
	                      @PathParam("maxtoreturn") int maxRecordsToReturn);

	@GET
	@Path("/valueset/{oid}/definition/{version}")
	@Produces(MediaType.APPLICATION_XML)
	String getValueSetDefinition(@HeaderParam("Authorization") String auth,
	                             @PathParam("oid") String oid,
	                             @PathParam("version") String version);

	@GET
	@Path("/valueset/{oid}/definition/{version}/resolution?maxtoreturn={maxtoreturn}")
	@Produces(MediaType.APPLICATION_XML)
	String getResolvedValueSet(@HeaderParam("Authorization") String auth,
	                           @PathParam("oid") String oid,
	                           @PathParam("version") String version,
	                           @PathParam("maxtoreturn") int maxRecordsToReturn);
}