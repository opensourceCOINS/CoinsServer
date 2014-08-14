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

/**
 * Class defining the web service of the COINS API
 */
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

	/**
	 * API path
	 */
	public static final String PATH = "coinsapi";
	/**
	 * Version
	 */
	public static final String PATH_VERSION = "/version";
	/**
	 * Prefixes
	 */
	public static final String PATH_PREFIXES = "/prefixes";
	/**
	 * Requirement
	 */
	public static final String PATH_REQUIREMENT = "/requirement";
	/**
	 * NonFunctionRequirement
	 */
	public static final String PATH_NON_FUNCTIONAL_REQUIREMENT = "/nonfunctionalrequirement";
	/**
	 * Requirement (FORM)
	 */
	public static final String PATH_REQUIREMENT_FORM = "/requirementform";
	/**
	 * PersonOrOrganisation
	 */
	public static final String PATH_PERSON_OR_ORGANISATION = "/personorganisation";
	/**
	 * Description
	 */
	public static final String PATH_DESCRIPTION = "/description";
	/**
	 * Function
	 */
	public static final String PATH_FUNCTION = "/function";
	/**
	 * PhysicalObject
	 */
	public static final String PATH_PHYSICAL_OBJECT = "/physicalobject";
	/**
	 * Document
	 */
	public static final String PATH_DOCUMENT = "/document";
	/**
	 * Vector
	 */
	public static final String PATH_VECTOR = "/vector";
	/**
	 * Locator
	 */
	public static final String PATH_LOCATOR = "/locator";
	/**
	 * Explicit3DRepresentation
	 */
	public static final String PATH_EXPLICIT_3D_REPRESENTATION = "/explicit3drepresentation";
	/**
	 * Task 
	 */
	public static final String PATH_TASK = "/task";
	/**
	 * Application/json
	 */
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
	
	/**
	 * Create a new <B>Function</B>
	 * 
	 * @param context 
	 *            The context or graph
	 * @param modelURI
	 * 			  The URI of the model
	 * @param name
	 *            The name of the <B>Function</B>
	 * @param layerIndex
	 *            The layer index of the <B>Function</B>
	 * @param userID
	 *            A user defined identifier (for convenience)
	 * @param creator
	 *            URI referring to the user that created this <B>Function</B>
	 * @param isFulfilledBy
	 *  		  URI referring to the function fulfiller.              
	 * @return The id of the created <B>Function</B>
	 */
	@POST
	@Path(PATH_FUNCTION)
	@Consumes(MIME_TYPE)
	public Response createFunction(@QueryParam("context") String context,
			@QueryParam("modelURI") String modelURI,
			@QueryParam("name") String name,
			@QueryParam("layerIndex") int layerIndex,
			@QueryParam("userID") String userID,
			@QueryParam("creator") String creator,
			@QueryParam("isFulfilledBy") String isFulfilledBy) {
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
			identifier = coinsService.createFunction(context, modelURI, name,
					layerIndex, userID, creator, isFulfilledBy);
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
				.entity("Something went wrong when creating the function")
				.build();
	}

	/**
	 * Get a <B>Function</B>
	 * 
	 * @param context The context or graph
	 * @param id The id of the <B>Function</B>
	 * @param output The way the output should be formatted (json/xml/csv/html/tabs)
	 * @param request
	 * @return the <B>Function</B> formatted the way specified by means of the output
	 */
	@GET
	@Path(PATH_FUNCTION)
	public Response getFunction(@QueryParam("context") String context,
			@QueryParam("id") String id, @QueryParam("output") String output, @Context HttpServletRequest request) {		
		String query = coinsService.getFunctionQuery(context, id);
		return sparqlWebService.selectPostForm(query, output, request);
	}

	/**
	 * Delete a <B>Function</B>
	 * @param context
	 * @param id
	 * @return OK if the <B>Function</B> was deleted
	 */
	@DELETE
	@Path(PATH_FUNCTION)
	@Consumes(MIME_TYPE)
	public Response deleteFunction(@QueryParam("context") String context,
			@QueryParam("id") String id) {
		if (coinsService.deleteItem(context, id)) {
			return Response.ok().build();
		}
		return Response.serverError().entity("Cannot delete Function")
				.build();
	}

	/**
	 * Create a new <B>Document</B>
	 * 
	 * @param context 
	 *            The context or graph
	 * @param modelURI
	 * 			  The URI of the model
	 * @param name
	 *            The name of the <B>Document</B>
	 * @param userID
	 *            A user defined identifier (for convenience)
	 * @param creator
	 *            URI referring to the user that created this <B>Document</B>
	 * @return The id of the created <B>Document</B>
	 */
	@POST
	@Path(PATH_DOCUMENT)
	@Consumes(MIME_TYPE)
	public Response createDocument(@QueryParam("context") String context,
			@QueryParam("modelURI") String modelURI,
			@QueryParam("name") String name,
			@QueryParam("userID") String userID,
			@QueryParam("creator") String creator){
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
			identifier = coinsService.createDocument(context, modelURI, name,
					userID, creator);
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
				.entity("Something went wrong when creating the document")
				.build();
	}

	/**
	 * Get a <B>Document</B>
	 * 
	 * @param context The context or graph
	 * @param id The id of the <B>Document</B>
	 * @param output The way the output should be formatted (json/xml/csv/html/tabs)
	 * @param request
	 * @return the <B>Document</B> formatted the way specified by means of the output
	 */
	@GET
	@Path(PATH_DOCUMENT)
	public Response getDocument(@QueryParam("context") String context,
			@QueryParam("id") String id, @QueryParam("output") String output, @Context HttpServletRequest request) {		
		String query = coinsService.getDocumentQuery(context, id);
		return sparqlWebService.selectPostForm(query, output, request);
	}

	/**
	 * Delete a <B>Document</B>
	 * @param context
	 * @param id
	 * @return OK if the <B>Document</B> was deleted
	 */
	@DELETE
	@Path(PATH_DOCUMENT)
	@Consumes(MIME_TYPE)
	public Response deleteDocument(@QueryParam("context") String context,
			@QueryParam("id") String id) {
		if (coinsService.deleteItem(context, id)) {
			return Response.ok().build();
		}
		return Response.serverError().entity("Cannot delete Document")
				.build();
	}

	/**
	 * Create a new <B>Explicit3DRepresentation</B>
	 * 
	 * @param context 
	 *            The context or graph
	 * @param modelURI
	 * 			  The URI of the model
	 * @param name
	 *            The name of the <B>Explicit3DRepresentation</B>
	 * @param userID
	 *            A user defined identifier (for convenience)
	 * @param documentType
	 * 			  Type of document (For instance IFC)
	 * @param documentAliasFilePath
	 * 			  File name of the document
	 * @param documentUri
	 *            URI to the document 
	 * @param creator
	 *            URI referring to the user that created this <B>Explicit3DRepresentation</B>
	 * @return The id of the created <B>Explicit3DRepresentation</B>
	 */
	@POST
	@Path(PATH_EXPLICIT_3D_REPRESENTATION)
	@Consumes(MIME_TYPE)
	public Response createExplicit3DRepresentation(@QueryParam("context") String context,
			@QueryParam("modelURI") String modelURI,
			@QueryParam("name") String name,
			@QueryParam("documentType") String documentType,
			@QueryParam("documentAliasFilePath") String documentAliasFilePath,
			@QueryParam("documentUri") String documentUri,
			@QueryParam("creator") String creator) {
		if (name == null) {
			return Response.serverError().entity("Name cannot be null").build();
		}
		if (context == null) {
			context = configurationService.getDefaultContext();
		}
		String identifier;
		try {
			identifier = coinsService.createExplicit3DRepresentation(context, modelURI, name,
					documentType, documentAliasFilePath, documentUri, creator);
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
	 * Delete an <B>Explicit3DRepresentation</B>
	 * @param context
	 * @param id
	 * @return OK if the <B>Explicit3DRepresentation</B> was deleted
	 */
	@DELETE
	@Path(PATH_EXPLICIT_3D_REPRESENTATION)
	@Consumes(MIME_TYPE)
	public Response deleteExplicit3DRepresentation(@QueryParam("context") String context,
			@QueryParam("id") String id) {
		if (coinsService.deleteItem(context, id)) {
			return Response.ok().build();
		}
		return Response.serverError().entity("Cannot delete Explicit3DRepresentation")
				.build();
	}

	/**
	 * Get an <B>Explicit3DRepresentation</B>
	 * 
	 * @param context / graph
	 * @param id The id of the <B>Explicit3DRepresentation</B>
	 * @param output The way the output should be formatted (json/xml/csv/html/tabs)
	 * @param request
	 * @return the <B>Explicit3DRepresentation</B> formatted the way specified by means of the output
	 */
	@GET
	@Path(PATH_EXPLICIT_3D_REPRESENTATION)
	public Response getExplicit3DRepresentation(@QueryParam("context") String context,
			@QueryParam("id") String id, @QueryParam("output") String output, @Context HttpServletRequest request) {		
		String query = coinsService.getExplicit3DRepresentationQuery(context, id);
		return sparqlWebService.selectPostForm(query, output, request);
	}

	/**
	 * Create a new <B>Vector</B>
	 * 
	 * @param context 
	 *            The context or graph
	 * @param modelURI
	 * 			  The URI of the model
	 * @param name
	 *            The name of the <B>Vector</B>
	 * @param xCoordinate
	 *            X coordinate
	 * @param yCoordinate
	 *            Y coordinate
	 * @param zCoordinate
	 *            Z coordinate
	 * @param creator
	 *            URI referring to the user that created this <B>Vector</B>
	 * @return The id of the created <B>Vector</B>
	 */
	@POST
	@Path(PATH_VECTOR)
	@Consumes(MIME_TYPE)
	public Response createVector(@QueryParam("context") String context,
			@QueryParam("modelURI") String modelURI,
			@QueryParam("name") String name,
			@QueryParam("xCoordinate") Double xCoordinate,
			@QueryParam("yCoordinate") Double yCoordinate,
			@QueryParam("zCoordinate") Double zCoordinate,
			@QueryParam("creator") String creator) {
		if (name == null) {
			return Response.serverError().entity("Name cannot be null").build();
		}
		if (context == null) {
			context = configurationService.getDefaultContext();
		}
		String identifier;
		try {
			identifier = coinsService.createVector(context, modelURI, name,
					xCoordinate, yCoordinate, zCoordinate, creator);
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
				.entity("Something went wrong when creating the Vector")
				.build();
	}
	
	/**
	 * Delete a <B>Vector</B>
	 * @param context
	 * @param id
	 * @return OK if the <B>Vector</B> was deleted
	 */
	@DELETE
	@Path(PATH_VECTOR)
	@Consumes(MIME_TYPE)
	public Response deleteVector(@QueryParam("context") String context,
			@QueryParam("id") String id) {
		if (coinsService.deleteItem(context, id)) {
			return Response.ok().build();
		}
		return Response.serverError().entity("Cannot delete Vector")
				.build();
	}

	/**
	 * Get a <B>Vector</B>
	 * 
	 * @param context / graph
	 * @param id The id of the <B>Vector</B>
	 * @param output The way the output should be formatted (json/xml/csv/html/tabs)
	 * @param request
	 * @return the <B>Vector</B> formatted the way specified by means of the output
	 */
	@GET
	@Path(PATH_VECTOR)
	public Response getVector(@QueryParam("context") String context,
			@QueryParam("id") String id, @QueryParam("output") String output, @Context HttpServletRequest request) {		
		String query = coinsService.getVectorQuery(context, id);
		return sparqlWebService.selectPostForm(query, output, request);
	}

	/**
	 * Create a new <B>Locator</B>
	 * 
	 * @param context 
	 *            The context or graph
	 * @param modelURI
	 * 			  The URI of the model
	 * @param name
	 *            The name of the <B>Locator</B>
	 * @param primaryOrientation
	 *            URI referring to the primary orientation (Vector)  
	 * @param secondaryOrientation
	 *            URI referring to the secondary orientation (Vector)
	 * @param translation
	 *  		  URI referring to the translation (Vector)
	 * @param creator
	 *            URI referring to the user that created this <B>Locator</B>
	 * @return The id of the created <B>Locator</B>
	 */
	@POST
	@Path(PATH_LOCATOR)
	@Consumes(MIME_TYPE)
	public Response createLocator(@QueryParam("context") String context,
			@QueryParam("modelURI") String modelURI,
			@QueryParam("name") String name,
			@QueryParam("primaryOrientation") String primaryOrientation,
			@QueryParam("secondaryOrientation") String secondaryOrientation,
			@QueryParam("translation") String translation,
			@QueryParam("creator") String creator) {
		if (name == null) {
			return Response.serverError().entity("Name cannot be null").build();
		}
		if (context == null) {
			context = configurationService.getDefaultContext();
		}
		String identifier;
		try {
			identifier = coinsService.createLocator(context, modelURI, name,
					primaryOrientation, secondaryOrientation, translation, creator);
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
				.entity("Something went wrong when creating the Vector")
				.build();
	}
	
	/**
	 * Delete a <B>Locator</B>
	 * @param context
	 * @param id
	 * @return OK if the <B>Locator</B> was deleted
	 */
	@DELETE
	@Path(PATH_LOCATOR)
	@Consumes(MIME_TYPE)
	public Response deleteLocator(@QueryParam("context") String context,
			@QueryParam("id") String id) {
		if (coinsService.deleteItem(context, id)) {
			return Response.ok().build();
		}
		return Response.serverError().entity("Cannot delete Locator")
				.build();
	}

	/**
	 * Get a <B>Locator</B>
	 * 
	 * @param context / graph
	 * @param id The id of the <B>Locator</B>
	 * @param output The way the output should be formatted (json/xml/csv/html/tabs)
	 * @param request
	 * @return the <B>Locator</B> formatted the way specified by means of the output
	 */
	@GET
	@Path(PATH_LOCATOR)
	public Response getLocator(@QueryParam("context") String context,
			@QueryParam("id") String id, @QueryParam("output") String output, @Context HttpServletRequest request) {		
		String query = coinsService.getLocatorQuery(context, id);
		return sparqlWebService.selectPostForm(query, output, request);
	}

	/**
	 * Create a new <B>Task</B>
	 * 
	 * @param context 
	 *            The context or graph
	 * @param modelURI
	 * 			  The URI of the model
	 * @param name
	 *            The name of the <B>Task</B>
	 * @param affects 
	 *            URI of the PhysicalObject this task affects
	 * @param userID
	 *            A user defined identifier (for convenience)
	 * @param taskType
	 *            For instance http://www.coinsweb.nl/c-bim.owl#Constructing  
	 * @param startDatePlanned
	 *            Start date for this <B>Task</B> formatted to yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
	 * @param endDatePlanned
	 *            End date for this <B>Task</B> formatted to yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
	 * @param creator
	 *            URI referring to the user that created this <B>Task</B>
	 * @return The id of the created <B>Task</B>
	 */
	@POST
	@Path(PATH_TASK)
	@Consumes(MIME_TYPE)
	public Response createTask(@QueryParam("context") String context,
			@QueryParam("modelURI") String modelURI,
			@QueryParam("name") String name,
			@QueryParam("affects") String affects,
			@QueryParam("userID") String userID,
			@QueryParam("taskType") String taskType,
			@QueryParam("startDatePlanned") String startDatePlanned,
			@QueryParam("endDatePlanned") String endDatePlanned,
			@QueryParam("creator") String creator){
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
			identifier = coinsService.createTask(context, modelURI, name,
					affects, userID, taskType, startDatePlanned,
					endDatePlanned, creator);
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
				.entity("Something went wrong when creating the task")
				.build();
	}
	
	/**
	 * Delete a <B>Task</B>
	 * @param context
	 * @param id
	 * @return OK if the <B>Task</B> was deleted
	 */
	@DELETE
	@Path(PATH_TASK)
	@Consumes(MIME_TYPE)
	public Response deleteTask(@QueryParam("context") String context,
			@QueryParam("id") String id) {
		if (coinsService.deleteItem(context, id)) {
			return Response.ok().build();
		}
		return Response.serverError().entity("Cannot delete task")
				.build();
	}

	/**
	 * Get a <B>Task</B>
	 * 
	 * @param context / graph
	 * @param id The id of the <B>Task</B>
	 * @param output The way the output should be formatted (json/xml/csv/html/tabs)
	 * @param request
	 * @return the <B>Task</B> formatted the way specified by means of the output
	 */
	@GET
	@Path(PATH_TASK)
	public Response getTask(@QueryParam("context") String context,
			@QueryParam("id") String id, @QueryParam("output") String output, @Context HttpServletRequest request) {		
		String query = coinsService.getTaskQuery(context, id);
		return sparqlWebService.selectPostForm(query, output, request);
	}

	/**
	 * Create a new <B>NonFunctionalRequirement</B>
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
	 *            URI referring to the user that created this <B>NonFunctionalRequirement</B>
	 * @param nonFunctionalRequirementType
	 *  		  The type of NonFunctionalRequirement             
	 * @return The id of the created <B>NonFunctionalRequirement</B>
	 */
	@POST
	@Path(PATH_NON_FUNCTIONAL_REQUIREMENT)
	@Consumes(MIME_TYPE)
	public Response createNonFunctionalRequirement(@QueryParam("context") String context,
			@QueryParam("modelURI") String modelURI,
			@QueryParam("name") String name,
			@QueryParam("layerIndex") int layerIndex,
			@QueryParam("userID") String userID,
			@QueryParam("creator") String creator,
			@QueryParam("nonFunctionalRequirementType") String nonFunctionalRequirementType) {
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
			identifier = coinsService.createNonFunctionalRequirement(context, modelURI, name,
					layerIndex, userID, creator, nonFunctionalRequirementType);
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
				.entity("Something went wrong when creating the NonFunctionalRequirement")
				.build();
	}

	/**
	 * Delete a <B>NonFunctionalRequirement</B>
	 * @param context
	 * @param id
	 * @return OK if the <B>NonFunctionalRequirement</B> was deleted
	 */
	@DELETE
	@Path(PATH_NON_FUNCTIONAL_REQUIREMENT)
	@Consumes(MIME_TYPE)
	public Response deleteNonFunctionalRequirement(@QueryParam("context") String context,
			@QueryParam("id") String id) {
		if (coinsService.deleteItem(context, id)) {
			return Response.ok().build();
		}
		return Response.serverError().entity("Cannot delete NonFunctionalRequirement")
				.build();
	}

	/**
	 * Get a <B>NonFunctionalRequirement</B>
	 * 
	 * @param context / graph
	 * @param id The id of the <B>NonFunctionalRequirement</B>
	 * @param output The way the output should be formatted (json/xml/csv/html/tabs)
	 * @param request
	 * @return the <B>NonFunctionalRequirement</B> formatted the way specified by means of the output
	 */
	@GET
	@Path(PATH_NON_FUNCTIONAL_REQUIREMENT)
	public Response getNonFunctionalRequirement(@QueryParam("context") String context,
			@QueryParam("id") String id, @QueryParam("output") String output, @Context HttpServletRequest request) {		
		String query = coinsService.getNonFunctionalRequirementQuery(context, id);
		return sparqlWebService.selectPostForm(query, output, request);
	}

}
