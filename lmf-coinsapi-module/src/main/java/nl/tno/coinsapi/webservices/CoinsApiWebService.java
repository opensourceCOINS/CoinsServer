package nl.tno.coinsapi.webservices;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Vector;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import nl.tno.coinsapi.services.ICoinsApiService;

import org.apache.marmotta.platform.core.api.config.ConfigurationService;
import org.apache.marmotta.platform.core.api.prefix.PrefixService;
import org.apache.marmotta.platform.core.exception.InvalidArgumentException;
import org.apache.marmotta.platform.core.exception.MarmottaException;
import org.apache.marmotta.platform.sparql.webservices.SparqlWebService;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.UpdateExecutionException;

@ApplicationScoped
@Path("/" + CoinsApiWebService.PATH)
public class CoinsApiWebService {

	private static final String[][] PREFIXES = new String[][] {
			{ "rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#" },
			{ "owl", "http://www.w3.org/2002/07/owl#" },
			{ "xsd", "http://www.w3.org/2001/XMLSchema#" },
			{ "cbimfs", "http://www.coinsweb.nl/c-bim-fs.owl#" },
			{ "cbim", "http://www.coinsweb.nl/c-bim.owl#" },
			{ "rdfs", "http://www.w3.org/2000/01/rdf-schema#" } };

	public static final String PATH = "coinsapi";
	public static final String PATH_VERSION = "/version";
	public static final String PATH_PREFIXES = "/prefixes";
	public static final String PATH_REQUIREMENT = "/requirement";
	public static final String PATH_REQUIREMENT_FORM = "/requirementform";
	public static final String MIME_TYPE = "application/json";

	@Inject
	private ConfigurationService configurationService;

	@Inject
	private ICoinsApiService coinsService;

	@Inject
	private PrefixService prefixService;

	@Inject
	private SparqlWebService sparqlWebService;

	/**
	 * Get the version of the COINS API
	 * 
	 * @return The actual version of the API
	 */
	@Path(PATH_VERSION)
	@GET
	@Produces(MIME_TYPE)
	public Response getVersion() {
		return Response.ok().entity("0.1 premature").build();
	}

	/**
	 * Add the default COINS prefixes
	 * 
	 * @return a list of added prefixes
	 */
	@Path(PATH_PREFIXES)
	@GET
	@Produces(MIME_TYPE)
	public Response setPrefixes() {
		List<String> addedPrefixes = new Vector<String>();
		for (int i = 0; i < PREFIXES.length; i++) {
			String nameSpace = prefixService.getNamespace(PREFIXES[i][0]);
			if (nameSpace == null) {
				try {
					prefixService.add(PREFIXES[i][0], PREFIXES[i][1]);
					addedPrefixes.add(PREFIXES[i][0]);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}
		}
		return Response.ok().entity(addedPrefixes).build();
	}	
	
	/**
	 * Create a new requirement
	 * 
	 * @param context
	 *            or graph
	 * @param name
	 *            The name of the requirement
	 * @param layerIndex
	 *            A description of the requirement
	 * @param userID
	 *            A user defined identifier (for convenience)
	 * @param creator
	 *            URL referring to the user that created this requirement
	 * @param requirementOf
	 *  		  URL referring to the function this requirement is part of             
	 * @return The id of the created requirement
	 */
	@POST
	@Path(PATH_REQUIREMENT)
	@Consumes(MIME_TYPE)
	public Response createRequirement(@QueryParam("context") String context,
			@QueryParam("name") String name,
			@QueryParam("layerIndex") int layerIndex,
			@QueryParam("userID") String userID,
			@QueryParam("creator") String creator,
			@QueryParam("requirementOf") String requirementOf) {
		if (name == null) {
			return Response.serverError().entity("Name cannot be null").build();
		}
		if (userID == null) {
			return Response.serverError().entity("Userid cannot be null")
					.build();
		}
		if (context == null) {
			context = configurationService.getDefaultContext();
		}
		String identifier;
		try {
			identifier = coinsService.createRequirement(context, name,
					layerIndex, userID, creator, requirementOf);
			return Response.ok().entity(identifier).build();
		} catch (MarmottaException e) {
			e.printStackTrace();
		} catch (InvalidArgumentException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (UpdateExecutionException e) {
			e.printStackTrace();
		}
		return Response.serverError()
				.entity("Something went wrong when creating the requirement")
				.build();
	}

	/**
	 * Create a new requirement
	 * 
	 * @param context
	 *            or graph
	 * @param name
	 *            The name of the requirement
	 * @param layerIndex
	 *            The layer index
	 * @param userId
	 *            A user defined identifier (for convenience)
	 * @param creator
	 *            URL referring to the user that created this requirement
	 * @param requirementOf
	 *  		  URL referring to the function this requirement is part of
	 * @return The id of the created requirement
	 */	
	@POST
	@Path(PATH_REQUIREMENT_FORM)
	public Response createRequirementForm(@FormParam("context") String context,
			@FormParam("name") String name,
			@FormParam("layerIndex") int layerIndex,
			@FormParam("userID") String userId,
			@FormParam("creator") String creator,
			@FormParam("requirementOf") String requirementOf) {
		if (name == null) {
			return Response.serverError().entity("Name cannot be null").build();
		}
		if (userId == null) {
			return Response.serverError().entity("Userid cannot be null")
					.build();
		}
		if (context == null) {
			context = configurationService.getDefaultContext();
		}
		String identifier;
		try {
			identifier = coinsService.createRequirement(context, name,
					layerIndex, userId, creator, requirementOf);
			return Response.ok().entity(identifier).build();
		} catch (MarmottaException e) {
			e.printStackTrace();
		} catch (InvalidArgumentException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (UpdateExecutionException e) {
			e.printStackTrace();
		}
		return Response.serverError()
				.entity("Something went wrong when creating the requirement")
				.build();		
	}
	
	/**
	 * Delete a requirement
	 * @param context
	 * @param id
	 * @return OK if the requirement was deleted
	 */
	@DELETE
	@Path(PATH_REQUIREMENT)
	@Consumes(MIME_TYPE)
	public Response deleteRequirement(@QueryParam("context") String context,
			@QueryParam("id") String id) {
		if (coinsService.deleteItem(context, id)) {
			return Response.ok().build();
		}
		return Response.serverError().entity("Cannot delete requirement")
				.build();
	}

	/**
	 * Get a requirement
	 * 
	 * @param context / graph
	 * @param id The id of the requirement
	 * @param output The way the output should be formatted (json/xml/csv/html/tabs)
	 * @param request
	 * @return the requirement formatted the way specified by means of the output
	 */
	@GET
	@Path(PATH_REQUIREMENT)
	public Response getRequirement(@QueryParam("context") String context,
			@QueryParam("id") String id, @QueryParam("output") String output, @Context HttpServletRequest request) {		
		String query = coinsService.getRequirementQuery(context, id);
		return sparqlWebService.selectPostForm(query, output, request);
	}

}
