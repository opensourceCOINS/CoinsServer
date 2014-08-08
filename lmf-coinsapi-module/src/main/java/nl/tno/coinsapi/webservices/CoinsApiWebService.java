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
	public static final String PATH_PERSON_OR_ORGANISATION = "/persons";
	public static final String PATH_DESCRIPTION = "/description";
	public static final String PATH_PHYSICAL_OBJECT = "/PhysicalObject";
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
	 * Create a new <B>Requirement</B>
	 * 
	 * @param context 
	 *            The context or graph
	 * @param modelURI
	 * 			  The URI of the model
	 * @param name
	 *            The name of the <B>Requirement</B>
	 * @param layerIndex
	 *            The layer index of the <B>Requirement</B>
	 * @param userID
	 *            A user defined identifier (for convenience)
	 * @param creator
	 *            URI referring to the user that created this <B>Requirement</B>
	 * @param requirementOf
	 *  		  URI referring to the function this <B>Requirement</B> is part of             
	 * @return The id of the created <B>Requirement</B>
	 */
	@POST
	@Path(PATH_REQUIREMENT)
	@Consumes(MIME_TYPE)
	public Response createRequirement(@QueryParam("context") String context,
			@QueryParam("modelURI") String modelURI,
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
			identifier = coinsService.createRequirement(context, modelURI, name,
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
	 * Create a new <B>Requirement</B> by means of a HTML form
	 * 
	 * @param context
	 *            Context or graph
	 * @param modelURI
	 * 			  The URI of the model
	 * @param name
	 *            The name of the <B>Requirement</B>
	 * @param layerIndex
	 *            The layer index
	 * @param userId
	 *            A user defined identifier (for convenience)
	 * @param creator
	 *            URI referring to the user that created this <B>Requirement</B>
	 * @param requirementOf
	 *  		  URI referring to the function this <B>Requirement</B> is part of
	 * @return The id of the created requirement
	 */	
	@POST
	@Path(PATH_REQUIREMENT_FORM)
	public Response createRequirementForm(@FormParam("context") String context,
			@FormParam("modelURI") String modelURI,			
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
			identifier = coinsService.createRequirement(context, modelURI, name,
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
	 * Delete a <B>Requirement</B>
	 * @param context
	 * @param id
	 * @return OK if the <B>Requirement</B> was deleted
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
	 * Get a <B>Requirement</B>
	 * 
	 * @param context / graph
	 * @param id The id of the <B>Requirement</B>
	 * @param output The way the output should be formatted (json/xml/csv/html/tabs)
	 * @param request
	 * @return the <B>Requirement</B> formatted the way specified by means of the output
	 */
	@GET
	@Path(PATH_REQUIREMENT)
	public Response getRequirement(@QueryParam("context") String context,
			@QueryParam("id") String id, @QueryParam("output") String output, @Context HttpServletRequest request) {		
		String query = coinsService.getRequirementQuery(context, id);
		return sparqlWebService.selectPostForm(query, output, request);
	}

	/**
	 * Create a new <B>PersonOrOrganisation</B>
	 * 
	 * @param context
	 *            The context or named graph
	 * @param modelURI
	 * 			  The URI of the model
	 * @param name
	 *            The name of the <B>PersonOrOrganisation</B>
	 * @return The id of the created <B>PersonOrOrganisation</B>
	 */
	@POST
	@Path(PATH_PERSON_OR_ORGANISATION)
	@Consumes(MIME_TYPE)
	public Response createPersonOrOrganisation(@QueryParam("context") String context,
			@QueryParam("modelURI") String modelURI,			
			@QueryParam("name") String name) {
		if (name == null) {
			return Response.serverError().entity("Name cannot be null").build();
		}
		if (context == null) {
			context = configurationService.getDefaultContext();
		}
		String identifier;
		try {
			identifier = coinsService.createPersonOrOrganisation(context, modelURI, name);
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
				.entity("Something went wrong when creating the PersonOrOrganisation")
				.build();
	}

	/**
	 * Get a <B>PersonOrOrganisation</B>
	 * 
	 * @param context The context or graph
	 * @param id The id of the <B>PersonOrOrganisation</B>
	 * @param output The way the output should be formatted (json/xml/csv/html/tabs)
	 * @param request
	 * @return the <B>PersonOrOrganisation</B> formatted the way specified by means of the output
	 */
	@GET
	@Path(PATH_PERSON_OR_ORGANISATION)
	public Response getPersonOrOrganisation(@QueryParam("context") String context,
			@QueryParam("id") String id, @QueryParam("output") String output, @Context HttpServletRequest request) {		
		String query = coinsService.getPersonOrOrganisationQuery(context, id);
		return sparqlWebService.selectPostForm(query, output, request);
	}

	/**
	 * Delete a <B>PersonOrOrganisation</B>
	 * @param context
	 * @param id
	 * @return OK if the <B>PersonOrOrganisation</B> was deleted
	 */
	@DELETE
	@Path(PATH_PERSON_OR_ORGANISATION)
	@Consumes(MIME_TYPE)
	public Response deletePersonOrOrganisation(@QueryParam("context") String context,
			@QueryParam("id") String id) {
		if (coinsService.deleteItem(context, id)) {
			return Response.ok().build();
		}
		return Response.serverError().entity("Cannot delete PersonOrOrganisation")
				.build();
	}

	/**
	 * Set the description of an object. 
	 * The description is not mandatory and therefore no default argument
	 * @param context The context or graph
	 * @param id The identifier of the object
	 * @param description The description
	 * @param modifier URI to the modifier of the object
	 * @return OK if successful
	 */
	@POST
	@Path(PATH_DESCRIPTION)
	@Consumes(MIME_TYPE)	
	public Response setDescription(@QueryParam("context") String context, @QueryParam("id") String id, @QueryParam("description") String description, @QueryParam("modifier") String modifier) {
		try {
			coinsService.setDescription(context, id, description, modifier);
			return Response.ok().build();
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
				.entity("Something went wrong when creating the PersonOrOrganisation")
				.build();
	}
	
	/**
	 * Create a new <B>PhysicalObject</B>
	 * 
	 * @param context
	 *            The context or named graph
	 * @param modelURI
	 * 			  The URI of the model
	 * @param name
	 *            The name of the <B>PhysicalObject</B>
	 * @param layerIndex
	 *            The layer index of the <B>PhysicalObject</B>
	 * @param userID
	 *            A user defined identifier (for convenience)
	 * @param creator
	 *            URI referring to the user that created this <B>PhysicalObject</B>
	 * @return The id of the created <B>PhysicalObject</B>
	 */
	@POST
	@Path(PATH_PHYSICAL_OBJECT)
	@Consumes(MIME_TYPE)
	public Response createPhysicalObject(@QueryParam("context") String context,
			@QueryParam("modelURI") String modelURI,			
			@QueryParam("name") String name,
			@QueryParam("layerIndex") int layerIndex,
			@QueryParam("userID") String userID,
			@QueryParam("creator") String creator) {
		if (name == null) {
			return Response.serverError().entity("Name cannot be null").build();
		}
		if (context == null) {
			context = configurationService.getDefaultContext();
		}
		String identifier;
		try {
			identifier = coinsService.createPhysicalObject(context, modelURI, name, layerIndex, userID, creator);
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
				.entity("Something went wrong when creating the PhysicalObject")
				.build();
	}

	/**
	 * Get a <B>PhysicalObject</B>
	 * 
	 * @param context The context or graph
	 * @param id The id of the <B>PhysicalObject</B>
	 * @param output The way the output should be formatted (json/xml/csv/html/tabs)
	 * @param request
	 * @return the <B>PhysicalObject</B> formatted the way specified by means of the output
	 */
	@GET
	@Path(PATH_PHYSICAL_OBJECT)
	public Response getPhysicalObject(@QueryParam("context") String context,
			@QueryParam("id") String id, @QueryParam("output") String output, @Context HttpServletRequest request) {		
		String query = coinsService.getPhysicalObjectQuery(context, id);
		return sparqlWebService.selectPostForm(query, output, request);
	}

	/**
	 * Delete a <B>PhysicalObject</B>
	 * @param context
	 * @param id
	 * @return OK if the <B>PhysicalObject</B> was deleted
	 */
	@DELETE
	@Path(PATH_PHYSICAL_OBJECT)
	@Consumes(MIME_TYPE)
	public Response deletePhysicalObject(@QueryParam("context") String context,
			@QueryParam("id") String id) {
		if (coinsService.deleteItem(context, id)) {
			return Response.ok().build();
		}
		return Response.serverError().entity("Cannot delete PhysicalObject")
				.build();
	}	
}
