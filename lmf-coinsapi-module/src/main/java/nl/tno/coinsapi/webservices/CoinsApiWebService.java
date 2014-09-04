package nl.tno.coinsapi.webservices;

import java.io.IOException;
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
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import nl.tno.coinsapi.services.ICoinsDocFileService;
import nl.tno.coinsapi.services.ICoinsApiService;
import nl.tno.coinsapi.services.ICoinsApiService.ValidationAspect;

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

	private static final String OBJECT = "object";

	private static final String LAYERINDEX = "layerindex";

	private static final String LOCATOR = "locator";

	private static final String IS_AFFECTED_BY = "isAffectedBy";

	private static final String IS_FULFILLED_BY = "isFulfilledBy";

	private static final String FUNCTION = "function";

	private static final String FULFILLS = "fulfills";

	private static final String DOCUMENT = "document";

	private static final String NONFUNCTIONALREQUIREMENT = "nonfunctionalrequirement";

	private static final String FUNCTIONFULFILLER = "functionfulfiller";

	private static final String SHAPE = "shape";

	private static final String PHYSICALOBJECT = "physicalobject";

	private static final String PARENT = "parent";

	private static final String CHILD = "child";

	private static final String DEFAULT_VALUE = "defaultValue";

	private static final String VALUE = "value";

	private static final String CATALOGUE_PART = "cataloguePart";

	private static final String FILENAME = "filename";

	private static final String NON_FUNCTIONAL_REQUIREMENT_TYPE = "nonFunctionalRequirementType";

	private static final String END_DATE_PLANNED = "endDatePlanned";

	private static final String START_DATE_PLANNED = "startDatePlanned";

	private static final String TASK_TYPE = "taskType";

	private static final String AFFECTS = "affects";

	private static final String TRANSLATION = "translation";

	private static final String SECONDARY_ORIENTATION = "secondaryOrientation";

	private static final String PRIMARY_ORIENTATION = "primaryOrientation";

	private static final String Z_COORDINATE = "zCoordinate";

	private static final String Y_COORDINATE = "yCoordinate";

	private static final String X_COORDINATE = "xCoordinate";

	private static final String DOCUMENT_URI = "documentUri";

	private static final String DOCUMENT_ALIAS_FILE_PATH = "documentAliasFilePath";

	private static final String DOCUMENT_TYPE = "documentType";

	private static final String MODIFIER = "modifier";

	private static final String DESCRIPTION = "description";

	private static final String OUTPUT = "output";

	private static final String ID = "id";

	private static final String REQUIREMENT_OF = "requirementOf";

	private static final String CREATOR = "creator";

	private static final String USER_ID = "userID";

	private static final String LAYER_INDEX = "layerIndex";

	private static final String NAME = "name";

	private static final String CONTEXT = "context";

	private static final String MODEL_URI = "modelURI";

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
	 * Amount
	 */
	public static final String PATH_AMOUNT = "/amount";
	/**
	 * CataloguePart
	 */
	public static final String PATH_CATALOGUE_PART = "/cataloguepart";
	/**
	 * References to documents from COINS containers
	 */
	public static final String PATH_DOCUMENT_REFERENCE = "/doc";
	/**
	 * Initialize the context
	 */
	public static final String PATH_INITIALIZE_CONTEXT = "/initializecontext";
	/**
	 * Edit physical parent relation
	 */
	public static final String PATH_LINK_PHYSICAL_PARENT = "/link/physicalparent";
	/**
	 * Add a physical child relation
	 */
	public static final String PATH_LINK_PHYSICAL_CHILD = "/link/physicalchild";
	/**
	 * Edit spatial parent relation
	 */
	public static final String PATH_LINK_SPATIAL_PARENT = "/link/spatialparent";
	/**
	 * Add a spatial child relation
	 */
	public static final String PATH_LINK_SPATIAL_CHILD = "/link/spatialchild";
	/**
	 * Link NonFunctionalRequirements to a PhysicalObject
	 */
	public static final String PATH_LINK_NON_FUNCTIONAL_REQUIREMENT = "/link/nonfunctionalrequirement";
	/**
	 * Link Documents to a PhysicalObject
	 */
	public static final String PATH_LINK_DOCUMENT = "/link/document";

	/**
	 * Link a function to a function fulfiller (isFulfilledBy)
	 */
	public static final String PATH_LINK_FULFILLED_BY = "/link/isfulfilledby";

	/**
	 * Link a function fulfiller to a function (fulfills)
	 */
	public static final String PATH_LINK_FULFILLS = "/link/fulfills";

	/**
	 * Link an Explicit3DRepresentation to a PhysicalObject
	 */
	public static final String PATH_LINK_SHAPE = "/link/shape";

	/**
	 * A Physical object affected by a task
	 */
	public static final String PATH_LINK_ISAFFECTEDBY = "/link/isaffectedby";

	/**
	 * Add an Explicit3DRepresentation to a parameter
	 */
	public static final String PATH_LINK_FIRST_PARAMETER = "/link/firstparameter";

	/**
	 * Add a parameter to the next one
	 */
	public static final String PATH_LINK_NEXT_PARAMETER = "/link/nextparameter";

	/**
	 * Validate
	 */
	public static final String PATH_VALIDATE = "/validate";

	/**
	 * Validate all
	 */
	public static final String PATH_VALIDATEALL = "/validateall";

	/**
	 * Add attribute
	 */
	public static final String PATH_ADD_ATTRIBUTE = "/addattribute";

	/**
	 * Add attribute string
	 */
	public static final String PATH_ADD_ATTRIBUTE_STRING = PATH_ADD_ATTRIBUTE
			+ "/string";

	/**
	 * Add attribute float
	 */
	public static final String PATH_ADD_ATTRIBUTE_FLOAT = PATH_ADD_ATTRIBUTE
			+ "/foat";

	/**
	 * Add attribute int
	 */
	public static final String PATH_ADD_ATTRIBUTE_INTEGER = PATH_ADD_ATTRIBUTE
			+ "/int";

	/**
	 * Add attribute resource
	 */
	public static final String PATH_ADD_ATTRIBUTE_RESOURCE = PATH_ADD_ATTRIBUTE
			+ "/resource";

	/**
	 * Add attribute date
	 */
	public static final String PATH_ADD_ATTRIBUTE_DATE = PATH_ADD_ATTRIBUTE
			+ "/date";

	/**
	 * Add a space object
	 */
	public static final String PATH_SPACE = "/space";
	/**
	 * Parameter
	 */
	public static final String PATH_PARAMETER = "/parameter";
	/**
	 * Terminal
	 */
	public static final String PATH_TERMINAL = "/terminal";
	/**
	 * Application/json
	 */
	public static final String MIME_TYPE = "application/json";

	@Inject
	private ConfigurationService mConfigurationService;

	@Inject
	private ICoinsApiService mCoinsService;

	@Inject
	private ICoinsDocFileService mFileServer;

	@Inject
	private PrefixService mPrefixService;

	@Inject
	private SparqlWebService mSparqlWebService;

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
	 * Initialize the context
	 * 
	 * @param context
	 * @param modelURI
	 * @return true if it succeeded
	 */
	@POST
	@Path(PATH_INITIALIZE_CONTEXT)
	@Consumes(MIME_TYPE)
	public Response initializeContext(@QueryParam(CONTEXT) String context,
			@QueryParam(MODEL_URI) String modelURI) {
		try {
			mCoinsService.initializeContext(context, modelURI);
			return Response.ok().build();
		} catch (Exception e) {
			return Response.serverError().build();
		}
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
			String nameSpace = mPrefixService.getNamespace(PREFIXES[i][0]);
			if (nameSpace == null) {
				try {
					mPrefixService.add(PREFIXES[i][0], PREFIXES[i][1]);
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
	 *            The URI of the model
	 * @param name
	 *            The name of the <B>Requirement</B>
	 * @param layerIndex
	 *            The layer index of the <B>Requirement</B>
	 * @param userID
	 *            A user defined identifier (for convenience)
	 * @param creator
	 *            URI referring to the user that created this <B>Requirement</B>
	 * @param requirementOf
	 *            URI referring to the function this <B>Requirement</B> is part
	 *            of
	 * @return The id of the created <B>Requirement</B>
	 */
	@POST
	@Path(PATH_REQUIREMENT)
	@Consumes(MIME_TYPE)
	public Response createRequirement(@QueryParam(CONTEXT) String context,
			@QueryParam(MODEL_URI) String modelURI,
			@QueryParam(NAME) String name,
			@QueryParam(LAYER_INDEX) int layerIndex,
			@QueryParam(USER_ID) String userID,
			@QueryParam(CREATOR) String creator,
			@QueryParam(REQUIREMENT_OF) String requirementOf) {
		if (name == null) {
			return Response.serverError().entity("Name cannot be null").build();
		}
		if (userID == null) {
			return Response.serverError().entity("Userid cannot be null")
					.build();
		}
		if (context == null) {
			context = mConfigurationService.getDefaultContext();
		}
		String identifier;
		try {
			identifier = mCoinsService.createRequirement(context, modelURI,
					name, layerIndex, userID, creator, requirementOf);
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
	 *            The URI of the model
	 * @param name
	 *            The name of the <B>Requirement</B>
	 * @param layerIndex
	 *            The layer index
	 * @param userId
	 *            A user defined identifier (for convenience)
	 * @param creator
	 *            URI referring to the user that created this <B>Requirement</B>
	 * @param requirementOf
	 *            URI referring to the function this <B>Requirement</B> is part
	 *            of
	 * @return The id of the created requirement
	 */
	@POST
	@Path(PATH_REQUIREMENT_FORM)
	public Response createRequirementForm(@FormParam(CONTEXT) String context,
			@FormParam(MODEL_URI) String modelURI,
			@FormParam(NAME) String name,
			@FormParam(LAYER_INDEX) int layerIndex,
			@FormParam(USER_ID) String userId,
			@FormParam(CREATOR) String creator,
			@FormParam(REQUIREMENT_OF) String requirementOf) {
		if (name == null) {
			return Response.serverError().entity("Name cannot be null").build();
		}
		if (userId == null) {
			return Response.serverError().entity("Userid cannot be null")
					.build();
		}
		if (context == null) {
			context = mConfigurationService.getDefaultContext();
		}
		String identifier;
		try {
			identifier = mCoinsService.createRequirement(context, modelURI,
					name, layerIndex, userId, creator, requirementOf);
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
	 * 
	 * @param context
	 * @param id
	 * @return OK if the <B>Requirement</B> was deleted
	 */
	@DELETE
	@Path(PATH_REQUIREMENT)
	@Consumes(MIME_TYPE)
	public Response deleteRequirement(@QueryParam(CONTEXT) String context,
			@QueryParam(ID) String id) {
		if (mCoinsService.deleteRequirement(context, id)) {
			return Response.ok().build();
		}
		return Response.serverError().entity("Cannot delete requirement")
				.build();
	}

	/**
	 * Get a <B>Requirement</B>
	 * 
	 * @param context
	 *            / graph
	 * @param id
	 *            The id of the <B>Requirement</B>
	 * @param output
	 *            The way the output should be formatted
	 *            (json/xml/csv/html/tabs)
	 * @param request
	 * @return the <B>Requirement</B> formatted the way specified by means of
	 *         the output
	 */
	@GET
	@Path(PATH_REQUIREMENT)
	@Consumes(MIME_TYPE)
	public Response getRequirement(@QueryParam(CONTEXT) String context,
			@QueryParam(ID) String id, @QueryParam(OUTPUT) String output,
			@Context HttpServletRequest request) {
		String query = mCoinsService.getRequirementQuery(context, id);
		return mSparqlWebService.selectPostForm(query, output, request);
	}

	/**
	 * Create a new <B>PersonOrOrganisation</B>
	 * 
	 * @param context
	 *            The context or named graph
	 * @param modelURI
	 *            The URI of the model
	 * @param name
	 *            The name of the <B>PersonOrOrganisation</B>
	 * @return The id of the created <B>PersonOrOrganisation</B>
	 */
	@POST
	@Path(PATH_PERSON_OR_ORGANISATION)
	@Consumes(MIME_TYPE)
	public Response createPersonOrOrganisation(
			@QueryParam(CONTEXT) String context,
			@QueryParam(MODEL_URI) String modelURI,
			@QueryParam(NAME) String name) {
		if (name == null) {
			return Response.serverError().entity("Name cannot be null").build();
		}
		if (context == null) {
			context = mConfigurationService.getDefaultContext();
		}
		String identifier;
		try {
			identifier = mCoinsService.createPersonOrOrganisation(context,
					modelURI, name);
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
		return Response
				.serverError()
				.entity("Something went wrong when creating the PersonOrOrganisation")
				.build();
	}

	/**
	 * Get a <B>PersonOrOrganisation</B>
	 * 
	 * @param context
	 *            The context or graph
	 * @param id
	 *            The id of the <B>PersonOrOrganisation</B>
	 * @param output
	 *            The way the output should be formatted
	 *            (json/xml/csv/html/tabs)
	 * @param request
	 * @return the <B>PersonOrOrganisation</B> formatted the way specified by
	 *         means of the output
	 */
	@GET
	@Path(PATH_PERSON_OR_ORGANISATION)
	@Consumes(MIME_TYPE)
	public Response getPersonOrOrganisation(
			@QueryParam(CONTEXT) String context, @QueryParam(ID) String id,
			@QueryParam(OUTPUT) String output,
			@Context HttpServletRequest request) {
		String query = mCoinsService.getPersonOrOrganisationQuery(context, id);
		return mSparqlWebService.selectPostForm(query, output, request);
	}

	/**
	 * Delete a <B>PersonOrOrganisation</B>
	 * 
	 * @param context
	 * @param id
	 * @return OK if the <B>PersonOrOrganisation</B> was deleted
	 */
	@DELETE
	@Path(PATH_PERSON_OR_ORGANISATION)
	@Consumes(MIME_TYPE)
	public Response deletePersonOrOrganisation(
			@QueryParam(CONTEXT) String context, @QueryParam(ID) String id) {
		if (mCoinsService.deletePersonOrOrganisation(context, id)) {
			return Response.ok().build();
		}
		return Response.serverError()
				.entity("Cannot delete PersonOrOrganisation").build();
	}

	/**
	 * Set the description of an object. The description is not mandatory and
	 * therefore no default argument
	 * 
	 * @param context
	 *            The context or graph
	 * @param id
	 *            The identifier of the object
	 * @param description
	 *            The description
	 * @param modifier
	 *            URI to the modifier of the object
	 * @return OK if successful
	 */
	@POST
	@Path(PATH_DESCRIPTION)
	@Consumes(MIME_TYPE)
	public Response setDescription(@QueryParam(CONTEXT) String context,
			@QueryParam(ID) String id,
			@QueryParam(DESCRIPTION) String description,
			@QueryParam(MODIFIER) String modifier) {
		try {
			mCoinsService.setDescription(context, id, description, modifier);
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
				.entity("Something went wrong when setting the Description")
				.build();
	}

	/**
	 * Create a new <B>PhysicalObject</B>
	 * 
	 * @param context
	 *            The context or named graph
	 * @param modelURI
	 *            The URI of the model
	 * @param name
	 *            The name of the <B>PhysicalObject</B>
	 * @param layerIndex
	 *            The layer index of the <B>PhysicalObject</B>
	 * @param userID
	 *            A user defined identifier (for convenience)
	 * @param creator
	 *            URI referring to the user that created this
	 *            <B>PhysicalObject</B>
	 * @return The id of the created <B>PhysicalObject</B>
	 */
	@POST
	@Path(PATH_PHYSICAL_OBJECT)
	@Consumes(MIME_TYPE)
	public Response createPhysicalObject(@QueryParam(CONTEXT) String context,
			@QueryParam(MODEL_URI) String modelURI,
			@QueryParam(NAME) String name,
			@QueryParam(LAYER_INDEX) int layerIndex,
			@QueryParam(USER_ID) String userID,
			@QueryParam(CREATOR) String creator) {
		if (name == null) {
			return Response.serverError().entity("Name cannot be null").build();
		}
		if (context == null) {
			context = mConfigurationService.getDefaultContext();
		}
		String identifier;
		try {
			identifier = mCoinsService.createPhysicalObject(context, modelURI,
					name, layerIndex, userID, creator);
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
		return Response
				.serverError()
				.entity("Something went wrong when creating the PhysicalObject")
				.build();
	}

	/**
	 * Get a <B>PhysicalObject</B>
	 * 
	 * @param context
	 *            The context or graph
	 * @param id
	 *            The id of the <B>PhysicalObject</B>
	 * @param output
	 *            The way the output should be formatted
	 *            (json/xml/csv/html/tabs)
	 * @param request
	 * @return the <B>PhysicalObject</B> formatted the way specified by means of
	 *         the output
	 */
	@GET
	@Path(PATH_PHYSICAL_OBJECT)
	@Consumes(MIME_TYPE)
	public Response getPhysicalObject(@QueryParam(CONTEXT) String context,
			@QueryParam(ID) String id, @QueryParam(OUTPUT) String output,
			@Context HttpServletRequest request) {
		String query = mCoinsService.getPhysicalObjectQuery(context, id);
		return mSparqlWebService.selectPostForm(query, output, request);
	}

	/**
	 * Delete a <B>PhysicalObject</B>
	 * 
	 * @param context
	 * @param id
	 * @return OK if the <B>PhysicalObject</B> was deleted
	 */
	@DELETE
	@Path(PATH_PHYSICAL_OBJECT)
	@Consumes(MIME_TYPE)
	public Response deletePhysicalObject(@QueryParam(CONTEXT) String context,
			@QueryParam(ID) String id) {
		if (mCoinsService.deletePhysicalObject(context, id)) {
			return Response.ok().build();
		}
		return Response.serverError().entity("Cannot delete PhysicalObject")
				.build();
	}

	/**
	 * Create a new <B>Space</B>
	 * 
	 * @param context
	 *            The context or named graph
	 * @param modelURI
	 *            The URI of the model
	 * @param name
	 *            The name of the <B>Space</B>
	 * @param layerIndex
	 *            The layer index of the <B>Space</B>
	 * @param userID
	 *            A user defined identifier (for convenience)
	 * @param creator
	 *            URI referring to the user that created this <B>Space</B>
	 * @return The id of the created <B>Space</B>
	 */
	@POST
	@Path(PATH_SPACE)
	@Consumes(MIME_TYPE)
	public Response createSpace(@QueryParam(CONTEXT) String context,
			@QueryParam(MODEL_URI) String modelURI,
			@QueryParam(NAME) String name,
			@QueryParam(LAYER_INDEX) int layerIndex,
			@QueryParam(USER_ID) String userID,
			@QueryParam(CREATOR) String creator) {
		if (name == null) {
			return Response.serverError().entity("Name cannot be null").build();
		}
		if (context == null) {
			context = mConfigurationService.getDefaultContext();
		}
		String identifier;
		try {
			identifier = mCoinsService.createSpace(context, modelURI, name,
					layerIndex, userID, creator);
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
				.entity("Something went wrong when creating the Space").build();
	}

	/**
	 * Get a <B>Space</B>
	 * 
	 * @param context
	 *            The context or graph
	 * @param id
	 *            The id of the <B>Space</B>
	 * @param output
	 *            The way the output should be formatted
	 *            (json/xml/csv/html/tabs)
	 * @param request
	 * @return the <B>Space</B> formatted the way specified by means of the
	 *         output
	 */
	@GET
	@Path(PATH_SPACE)
	@Consumes(MIME_TYPE)
	public Response getSpace(@QueryParam(CONTEXT) String context,
			@QueryParam(ID) String id, @QueryParam(OUTPUT) String output,
			@Context HttpServletRequest request) {
		String query = mCoinsService.getSpaceQuery(context, id);
		return mSparqlWebService.selectPostForm(query, output, request);
	}

	/**
	 * Delete a <B>Space</B>
	 * 
	 * @param context
	 * @param id
	 * @return OK if the <B>Space</B> was deleted
	 */
	@DELETE
	@Path(PATH_SPACE)
	@Consumes(MIME_TYPE)
	public Response deleteSpace(@QueryParam(CONTEXT) String context,
			@QueryParam(ID) String id) {
		if (mCoinsService.deleteSpace(context, id)) {
			return Response.ok().build();
		}
		return Response.serverError().entity("Cannot delete Space").build();
	}

	/**
	 * Create a new <B>Function</B>
	 * 
	 * @param context
	 *            The context or graph
	 * @param modelURI
	 *            The URI of the model
	 * @param name
	 *            The name of the <B>Function</B>
	 * @param layerIndex
	 *            The layer index of the <B>Function</B>
	 * @param userID
	 *            A user defined identifier (for convenience)
	 * @param creator
	 *            URI referring to the user that created this <B>Function</B>
	 * @return The id of the created <B>Function</B>
	 */
	@POST
	@Path(PATH_FUNCTION)
	@Consumes(MIME_TYPE)
	public Response createFunction(@QueryParam(CONTEXT) String context,
			@QueryParam(MODEL_URI) String modelURI,
			@QueryParam(NAME) String name,
			@QueryParam(LAYER_INDEX) int layerIndex,
			@QueryParam(USER_ID) String userID,
			@QueryParam(CREATOR) String creator) {
		if (name == null) {
			return Response.serverError().entity("Name cannot be null").build();
		}
		if (userID == null) {
			return Response.serverError().entity("Userid cannot be null")
					.build();
		}
		if (context == null) {
			context = mConfigurationService.getDefaultContext();
		}
		String identifier;
		try {
			identifier = mCoinsService.createFunction(context, modelURI, name,
					layerIndex, userID, creator);
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
	 * @param context
	 *            The context or graph
	 * @param id
	 *            The id of the <B>Function</B>
	 * @param output
	 *            The way the output should be formatted
	 *            (json/xml/csv/html/tabs)
	 * @param request
	 * @return the <B>Function</B> formatted the way specified by means of the
	 *         output
	 */
	@GET
	@Path(PATH_FUNCTION)
	@Consumes(MIME_TYPE)
	public Response getFunction(@QueryParam(CONTEXT) String context,
			@QueryParam(ID) String id, @QueryParam(OUTPUT) String output,
			@Context HttpServletRequest request) {
		String query = mCoinsService.getFunctionQuery(context, id);
		return mSparqlWebService.selectPostForm(query, output, request);
	}

	/**
	 * Delete a <B>Function</B>
	 * 
	 * @param context
	 * @param id
	 * @return OK if the <B>Function</B> was deleted
	 */
	@DELETE
	@Path(PATH_FUNCTION)
	@Consumes(MIME_TYPE)
	public Response deleteFunction(@QueryParam(CONTEXT) String context,
			@QueryParam(ID) String id) {
		if (mCoinsService.deleteFunction(context, id)) {
			return Response.ok().build();
		}
		return Response.serverError().entity("Cannot delete Function").build();
	}

	/**
	 * Create a new <B>Document</B>
	 * 
	 * @param context
	 *            The context or graph
	 * @param modelURI
	 *            The URI of the model
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
	public Response createDocument(@QueryParam(CONTEXT) String context,
			@QueryParam(MODEL_URI) String modelURI,
			@QueryParam(NAME) String name, @QueryParam(USER_ID) String userID,
			@QueryParam(CREATOR) String creator) {
		if (name == null) {
			return Response.serverError().entity("Name cannot be null").build();
		}
		if (userID == null) {
			return Response.serverError().entity("Userid cannot be null")
					.build();
		}
		if (context == null) {
			context = mConfigurationService.getDefaultContext();
		}
		String identifier;
		try {
			identifier = mCoinsService.createDocument(context, modelURI, name,
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
	 * @param context
	 *            The context or graph
	 * @param id
	 *            The id of the <B>Document</B>
	 * @param output
	 *            The way the output should be formatted
	 *            (json/xml/csv/html/tabs)
	 * @param request
	 * @return the <B>Document</B> formatted the way specified by means of the
	 *         output
	 */
	@GET
	@Path(PATH_DOCUMENT)
	@Consumes(MIME_TYPE)
	public Response getDocument(@QueryParam(CONTEXT) String context,
			@QueryParam(ID) String id, @QueryParam(OUTPUT) String output,
			@Context HttpServletRequest request) {
		String query = mCoinsService.getDocumentQuery(context, id);
		return mSparqlWebService.selectPostForm(query, output, request);
	}

	/**
	 * Delete a <B>Document</B>
	 * 
	 * @param context
	 * @param id
	 * @return OK if the <B>Document</B> was deleted
	 */
	@DELETE
	@Path(PATH_DOCUMENT)
	@Consumes(MIME_TYPE)
	public Response deleteDocument(@QueryParam(CONTEXT) String context,
			@QueryParam(ID) String id) {
		if (mCoinsService.deleteDocument(context, id)) {
			return Response.ok().build();
		}
		return Response.serverError().entity("Cannot delete Document").build();
	}

	/**
	 * Create a new <B>Explicit3DRepresentation</B>
	 * 
	 * @param context
	 *            The context or graph
	 * @param modelURI
	 *            The URI of the model
	 * @param name
	 *            The name of the <B>Explicit3DRepresentation</B>
	 * @param userID
	 *            A user defined identifier (for convenience)
	 * @param documentType
	 *            Type of document (For instance IFC)
	 * @param documentAliasFilePath
	 *            File name of the document
	 * @param documentUri
	 *            URI to the document
	 * @param creator
	 *            URI referring to the user that created this
	 *            <B>Explicit3DRepresentation</B>
	 * @return The id of the created <B>Explicit3DRepresentation</B>
	 */
	@POST
	@Path(PATH_EXPLICIT_3D_REPRESENTATION)
	@Consumes(MIME_TYPE)
	public Response createExplicit3DRepresentation(
			@QueryParam(CONTEXT) String context,
			@QueryParam(MODEL_URI) String modelURI,
			@QueryParam(NAME) String name,
			@QueryParam(DOCUMENT_TYPE) String documentType,
			@QueryParam(DOCUMENT_ALIAS_FILE_PATH) String documentAliasFilePath,
			@QueryParam(DOCUMENT_URI) String documentUri,
			@QueryParam(CREATOR) String creator) {
		if (name == null) {
			return Response.serverError().entity("Name cannot be null").build();
		}
		if (context == null) {
			context = mConfigurationService.getDefaultContext();
		}
		String identifier;
		try {
			identifier = mCoinsService.createExplicit3DRepresentation(context,
					modelURI, name, documentType, documentAliasFilePath,
					documentUri, creator);
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
		return Response
				.serverError()
				.entity("Something went wrong when creating the Explicit3dRepresentation")
				.build();
	}

	/**
	 * Delete an <B>Explicit3DRepresentation</B>
	 * 
	 * @param context
	 * @param id
	 * @return OK if the <B>Explicit3DRepresentation</B> was deleted
	 */
	@DELETE
	@Path(PATH_EXPLICIT_3D_REPRESENTATION)
	@Consumes(MIME_TYPE)
	public Response deleteExplicit3DRepresentation(
			@QueryParam(CONTEXT) String context, @QueryParam(ID) String id) {
		if (mCoinsService.deleteExplicit3DRepresentation(context, id)) {
			return Response.ok().build();
		}
		return Response.serverError()
				.entity("Cannot delete Explicit3DRepresentation").build();
	}

	/**
	 * Get an <B>Explicit3DRepresentation</B>
	 * 
	 * @param context
	 *            Context or graph
	 * @param id
	 *            The id of the <B>Explicit3DRepresentation</B>
	 * @param output
	 *            The way the output should be formatted
	 *            (json/xml/csv/html/tabs)
	 * @param request
	 * @return the <B>Explicit3DRepresentation</B> formatted the way specified
	 *         by means of the output
	 */
	@GET
	@Path(PATH_EXPLICIT_3D_REPRESENTATION)
	@Consumes(MIME_TYPE)
	public Response getExplicit3DRepresentation(
			@QueryParam(CONTEXT) String context, @QueryParam(ID) String id,
			@QueryParam(OUTPUT) String output,
			@Context HttpServletRequest request) {
		String query = mCoinsService.getExplicit3DRepresentationQuery(context,
				id);
		return mSparqlWebService.selectPostForm(query, output, request);
	}

	/**
	 * Create a new <B>Vector</B>
	 * 
	 * @param context
	 *            The context or graph
	 * @param modelURI
	 *            The URI of the model
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
	public Response createVector(@QueryParam(CONTEXT) String context,
			@QueryParam(MODEL_URI) String modelURI,
			@QueryParam(NAME) String name,
			@QueryParam(X_COORDINATE) Double xCoordinate,
			@QueryParam(Y_COORDINATE) Double yCoordinate,
			@QueryParam(Z_COORDINATE) Double zCoordinate,
			@QueryParam(CREATOR) String creator) {
		if (name == null) {
			return Response.serverError().entity("Name cannot be null").build();
		}
		if (context == null) {
			context = mConfigurationService.getDefaultContext();
		}
		String identifier;
		try {
			identifier = mCoinsService.createVector(context, modelURI, name,
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
	 * 
	 * @param context
	 * @param id
	 * @return OK if the <B>Vector</B> was deleted
	 */
	@DELETE
	@Path(PATH_VECTOR)
	@Consumes(MIME_TYPE)
	public Response deleteVector(@QueryParam(CONTEXT) String context,
			@QueryParam(ID) String id) {
		if (mCoinsService.deleteVector(context, id)) {
			return Response.ok().build();
		}
		return Response.serverError().entity("Cannot delete Vector").build();
	}

	/**
	 * Get a <B>Vector</B>
	 * 
	 * @param context
	 *            / graph
	 * @param id
	 *            The id of the <B>Vector</B>
	 * @param output
	 *            The way the output should be formatted
	 *            (json/xml/csv/html/tabs)
	 * @param request
	 * @return the <B>Vector</B> formatted the way specified by means of the
	 *         output
	 */
	@GET
	@Path(PATH_VECTOR)
	@Consumes(MIME_TYPE)
	public Response getVector(@QueryParam(CONTEXT) String context,
			@QueryParam(ID) String id, @QueryParam(OUTPUT) String output,
			@Context HttpServletRequest request) {
		String query = mCoinsService.getVectorQuery(context, id);
		return mSparqlWebService.selectPostForm(query, output, request);
	}

	/**
	 * Create a new <B>Locator</B>
	 * 
	 * @param context
	 *            The context or graph
	 * @param modelURI
	 *            The URI of the model
	 * @param name
	 *            The name of the <B>Locator</B>
	 * @param primaryOrientation
	 *            URI referring to the primary orientation (Vector)
	 * @param secondaryOrientation
	 *            URI referring to the secondary orientation (Vector)
	 * @param translation
	 *            URI referring to the translation (Vector)
	 * @param creator
	 *            URI referring to the user that created this <B>Locator</B>
	 * @return The id of the created <B>Locator</B>
	 */
	@POST
	@Path(PATH_LOCATOR)
	@Consumes(MIME_TYPE)
	public Response createLocator(@QueryParam(CONTEXT) String context,
			@QueryParam(MODEL_URI) String modelURI,
			@QueryParam(NAME) String name,
			@QueryParam(PRIMARY_ORIENTATION) String primaryOrientation,
			@QueryParam(SECONDARY_ORIENTATION) String secondaryOrientation,
			@QueryParam(TRANSLATION) String translation,
			@QueryParam(CREATOR) String creator) {
		if (name == null) {
			return Response.serverError().entity("Name cannot be null").build();
		}
		if (context == null) {
			context = mConfigurationService.getDefaultContext();
		}
		String identifier;
		try {
			identifier = mCoinsService.createLocator(context, modelURI, name,
					primaryOrientation, secondaryOrientation, translation,
					creator);
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
	 * 
	 * @param context
	 * @param id
	 * @return OK if the <B>Locator</B> was deleted
	 */
	@DELETE
	@Path(PATH_LOCATOR)
	@Consumes(MIME_TYPE)
	public Response deleteLocator(@QueryParam(CONTEXT) String context,
			@QueryParam(ID) String id) {
		if (mCoinsService.deleteLocator(context, id)) {
			return Response.ok().build();
		}
		return Response.serverError().entity("Cannot delete Locator").build();
	}

	/**
	 * Get a <B>Locator</B>
	 * 
	 * @param context
	 *            / graph
	 * @param id
	 *            The id of the <B>Locator</B>
	 * @param output
	 *            The way the output should be formatted
	 *            (json/xml/csv/html/tabs)
	 * @param request
	 * @return the <B>Locator</B> formatted the way specified by means of the
	 *         output
	 */
	@GET
	@Path(PATH_LOCATOR)
	@Consumes(MIME_TYPE)
	public Response getLocator(@QueryParam(CONTEXT) String context,
			@QueryParam(ID) String id, @QueryParam(OUTPUT) String output,
			@Context HttpServletRequest request) {
		String query = mCoinsService.getLocatorQuery(context, id);
		return mSparqlWebService.selectPostForm(query, output, request);
	}

	/**
	 * Create a new <B>Task</B>
	 * 
	 * @param context
	 *            The context or graph
	 * @param modelURI
	 *            The URI of the model
	 * @param name
	 *            The name of the <B>Task</B>
	 * @param affects
	 *            List of URIs of the <B>FunctionFulfillers</B> this task
	 *            affects
	 * @param userID
	 *            A user defined identifier (for convenience)
	 * @param taskType
	 *            For instance http://www.coinsweb.nl/c-bim.owl#Constructing
	 * @param startDatePlanned
	 *            Start date for this <B>Task</B> formatted to
	 *            yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
	 * @param endDatePlanned
	 *            End date for this <B>Task</B> formatted to
	 *            yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
	 * @param creator
	 *            URI referring to the user that created this <B>Task</B>
	 * @return The id of the created <B>Task</B>
	 */
	@POST
	@Path(PATH_TASK)
	@Consumes(MIME_TYPE)
	public Response createTask(@QueryParam(CONTEXT) String context,
			@QueryParam(MODEL_URI) String modelURI,
			@QueryParam(NAME) String name,
			@QueryParam(AFFECTS) String[] affects,
			@QueryParam(USER_ID) String userID,
			@QueryParam(TASK_TYPE) String taskType,
			@QueryParam(START_DATE_PLANNED) String startDatePlanned,
			@QueryParam(END_DATE_PLANNED) String endDatePlanned,
			@QueryParam(CREATOR) String creator) {
		if (name == null) {
			return Response.serverError().entity("Name cannot be null").build();
		}
		if (userID == null) {
			return Response.serverError().entity("Userid cannot be null")
					.build();
		}
		if (context == null) {
			context = mConfigurationService.getDefaultContext();
		}
		String identifier;
		try {
			identifier = mCoinsService.createTask(context, modelURI, name,
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
				.entity("Something went wrong when creating the task").build();
	}

	/**
	 * Delete a <B>Task</B>
	 * 
	 * @param context
	 * @param id
	 * @return OK if the <B>Task</B> was deleted
	 */
	@DELETE
	@Path(PATH_TASK)
	@Consumes(MIME_TYPE)
	public Response deleteTask(@QueryParam(CONTEXT) String context,
			@QueryParam(ID) String id) {
		if (mCoinsService.deleteTask(context, id)) {
			return Response.ok().build();
		}
		return Response.serverError().entity("Cannot delete task").build();
	}

	/**
	 * Get a <B>Task</B>
	 * 
	 * @param context
	 *            / graph
	 * @param id
	 *            The id of the <B>Task</B>
	 * @param output
	 *            The way the output should be formatted
	 *            (json/xml/csv/html/tabs)
	 * @param request
	 * @return the <B>Task</B> formatted the way specified by means of the
	 *         output
	 */
	@GET
	@Path(PATH_TASK)
	@Consumes(MIME_TYPE)
	public Response getTask(@QueryParam(CONTEXT) String context,
			@QueryParam(ID) String id, @QueryParam(OUTPUT) String output,
			@Context HttpServletRequest request) {
		String query = mCoinsService.getTaskQuery(context, id);
		return mSparqlWebService.selectPostForm(query, output, request);
	}

	/**
	 * Create a new <B>NonFunctionalRequirement</B>
	 * 
	 * @param context
	 *            The context or graph
	 * @param modelURI
	 *            The URI of the model
	 * @param name
	 *            The name of the <B>NonFunctionalRequirement</B>
	 * @param layerIndex
	 *            The layer index of the <B>NonFunctionalRequirement</B>
	 * @param userID
	 *            A user defined identifier (for convenience)
	 * @param creator
	 *            URI referring to the user that created this
	 *            <B>NonFunctionalRequirement</B>
	 * @param nonFunctionalRequirementType
	 *            The type of NonFunctionalRequirement
	 * @return The id of the created <B>NonFunctionalRequirement</B>
	 */
	@POST
	@Path(PATH_NON_FUNCTIONAL_REQUIREMENT)
	@Consumes(MIME_TYPE)
	public Response createNonFunctionalRequirement(
			@QueryParam(CONTEXT) String context,
			@QueryParam(MODEL_URI) String modelURI,
			@QueryParam(NAME) String name,
			@QueryParam(LAYER_INDEX) int layerIndex,
			@QueryParam(USER_ID) String userID,
			@QueryParam(CREATOR) String creator,
			@QueryParam(NON_FUNCTIONAL_REQUIREMENT_TYPE) String nonFunctionalRequirementType) {
		if (name == null) {
			return Response.serverError().entity("Name cannot be null").build();
		}
		if (userID == null) {
			return Response.serverError().entity("Userid cannot be null")
					.build();
		}
		if (context == null) {
			context = mConfigurationService.getDefaultContext();
		}
		String identifier;
		try {
			identifier = mCoinsService.createNonFunctionalRequirement(context,
					modelURI, name, layerIndex, userID, creator,
					nonFunctionalRequirementType);
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
		return Response
				.serverError()
				.entity("Something went wrong when creating the NonFunctionalRequirement")
				.build();
	}

	/**
	 * Delete a <B>NonFunctionalRequirement</B>
	 * 
	 * @param context
	 * @param id
	 * @return OK if the <B>NonFunctionalRequirement</B> was deleted
	 */
	@DELETE
	@Path(PATH_NON_FUNCTIONAL_REQUIREMENT)
	@Consumes(MIME_TYPE)
	public Response deleteNonFunctionalRequirement(
			@QueryParam(CONTEXT) String context, @QueryParam(ID) String id) {
		if (mCoinsService.deleteNonFunctionalRequirement(context, id)) {
			return Response.ok().build();
		}
		return Response.serverError()
				.entity("Cannot delete NonFunctionalRequirement").build();
	}

	/**
	 * Get a <B>NonFunctionalRequirement</B>
	 * 
	 * @param context
	 *            / graph
	 * @param id
	 *            The id of the <B>NonFunctionalRequirement</B>
	 * @param output
	 *            The way the output should be formatted
	 *            (json/xml/csv/html/tabs)
	 * @param request
	 * @return the <B>NonFunctionalRequirement</B> formatted the way specified
	 *         by means of the output
	 */
	@GET
	@Path(PATH_NON_FUNCTIONAL_REQUIREMENT)
	@Consumes(MIME_TYPE)
	public Response getNonFunctionalRequirement(
			@QueryParam(CONTEXT) String context, @QueryParam(ID) String id,
			@QueryParam(OUTPUT) String output,
			@Context HttpServletRequest request) {
		String query = mCoinsService.getNonFunctionalRequirementQuery(context,
				id);
		return mSparqlWebService.selectPostForm(query, output, request);
	}

	/**
	 * Get a document retrieved from a Coins Container
	 * 
	 * @param pFileName
	 * @return the requested document
	 */
	@GET
	@Path(PATH_DOCUMENT_REFERENCE + "/{filename}")
	@Consumes(MIME_TYPE)
	public Response getCoinsDocument(@PathParam(FILENAME) String pFileName) {
		byte[] file = null;
		try {
			file = mFileServer.getFile(pFileName);
		} catch (IOException e) {
			e.printStackTrace();
			return Response.serverError()
					.entity("File cannot be read " + pFileName).build();
		}
		if (file == null) {
			return Response.serverError().entity("File not found " + pFileName)
					.build();
		}
		return Response.ok(file).build();
	}

	/**
	 * Get a document retrieved from a Coins Container
	 * 
	 * @param pContext
	 * @param pFileName
	 * @return the requested document
	 */
	@GET
	@Path(PATH_DOCUMENT_REFERENCE + "/{context}/{filename}")
	@Consumes(MIME_TYPE)
	public Response getCoinsDocumentContext(@PathParam(CONTEXT) String pContext,
			@PathParam(FILENAME) String pFileName) {
		return getCoinsDocument(pContext + "/" + pFileName);
	}

	/**
	 * Create a new <B>Amount</B>
	 * 
	 * @param context
	 *            The context or graph
	 * @param modelURI
	 *            The URI of the model
	 * @param name
	 *            The name of the <B>Amount</B>
	 * @param userID
	 *            A user defined identifier (for convenience)
	 * @param cataloguePart
	 *            URI referring to the catalogPart associated with this
	 *            <B>Amount</B>
	 * @param value
	 *            The value belonging to the <B>Amount</B>
	 * @param creator
	 *            URI referring to the user that created this <B>Amount</B>
	 * @return The id of the created <B>Amount</B>
	 */
	@POST
	@Path(PATH_AMOUNT)
	@Consumes(MIME_TYPE)
	public Response createAmount(@QueryParam(CONTEXT) String context,
			@QueryParam(MODEL_URI) String modelURI,
			@QueryParam(NAME) String name, @QueryParam(USER_ID) String userID,
			@QueryParam(CATALOGUE_PART) String cataloguePart,
			@QueryParam(VALUE) int value, @QueryParam(CREATOR) String creator) {
		if (name == null) {
			return Response.serverError().entity("Name cannot be null").build();
		}
		if (context == null) {
			context = mConfigurationService.getDefaultContext();
		}
		String identifier;
		try {
			identifier = mCoinsService.createAmount(context, modelURI, name,
					userID, value, cataloguePart, creator);
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
				.entity("Something went wrong when creating the amount")
				.build();
	}

	/**
	 * Delete an <B>Amount</B>
	 * 
	 * @param context
	 * @param id
	 * @return OK if the <B>Amount</B> was deleted
	 */
	@DELETE
	@Path(PATH_AMOUNT)
	@Consumes(MIME_TYPE)
	public Response deleteAmount(@QueryParam(CONTEXT) String context,
			@QueryParam(ID) String id) {
		if (mCoinsService.deleteAmount(context, id)) {
			return Response.ok().build();
		}
		return Response.serverError().entity("Cannot delete Amount").build();
	}

	/**
	 * Get an <B>Amount</B>
	 * 
	 * @param context
	 *            / graph
	 * @param id
	 *            The id of the <B>Amount</B>
	 * @param output
	 *            The way the output should be formatted
	 *            (json/xml/csv/html/tabs)
	 * @param request
	 * @return the <B>Amount</B> formatted the way specified by means of the
	 *         output
	 */
	@GET
	@Path(PATH_AMOUNT)
	@Consumes(MIME_TYPE)
	public Response getAmount(@QueryParam(CONTEXT) String context,
			@QueryParam(ID) String id, @QueryParam(OUTPUT) String output,
			@Context HttpServletRequest request) {
		String query = mCoinsService.getAmountQuery(context, id);
		return mSparqlWebService.selectPostForm(query, output, request);
	}

	/**
	 * Create a new <B>CataloguePart</B>
	 * 
	 * @param context
	 *            The context or graph
	 * @param modelURI
	 *            The URI of the model
	 * @param name
	 *            The name of the <B>CataloguePart</B>
	 * @param userID
	 *            A user defined identifier (for convenience)
	 * @param creator
	 *            URI referring to the user that created this <B>Amount</B>
	 * @return The id of the created <B>Amount</B>
	 */
	@POST
	@Path(PATH_CATALOGUE_PART)
	@Consumes(MIME_TYPE)
	public Response createCataloguePart(@QueryParam(CONTEXT) String context,
			@QueryParam(MODEL_URI) String modelURI,
			@QueryParam(NAME) String name, @QueryParam(USER_ID) String userID,
			@QueryParam(CREATOR) String creator) {
		if (name == null) {
			return Response.serverError().entity("Name cannot be null").build();
		}
		if (context == null) {
			context = mConfigurationService.getDefaultContext();
		}
		String identifier;
		try {
			identifier = mCoinsService.createCataloguePart(context, modelURI,
					name, userID, creator);
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
		return Response
				.serverError()
				.entity("Something went wrong when creating the catalogue part")
				.build();
	}

	/**
	 * Delete a <B>CataloguePart</B>
	 * 
	 * @param context
	 * @param id
	 * @return OK if the <B>Amount</B> was deleted
	 */
	@DELETE
	@Path(PATH_CATALOGUE_PART)
	@Consumes(MIME_TYPE)
	public Response deleteCataloguePart(@QueryParam(CONTEXT) String context,
			@QueryParam(ID) String id) {
		if (mCoinsService.deleteCataloguePart(context, id)) {
			return Response.ok().build();
		}
		return Response.serverError().entity("Cannot delete CataloguePart")
				.build();
	}

	/**
	 * Get a <B>CataloguePart</B>
	 * 
	 * @param context
	 *            context or graph
	 * @param id
	 *            The id of the <B>CataloguePart</B>
	 * @param output
	 *            The way the output should be formatted
	 *            (json/xml/csv/html/tabs)
	 * @param request
	 * @return the <B>CataloguePart</B> formatted the way specified by means of
	 *         the output
	 */
	@GET
	@Path(PATH_CATALOGUE_PART)
	@Consumes(MIME_TYPE)
	public Response getCataloguePart(@QueryParam(CONTEXT) String context,
			@QueryParam(ID) String id, @QueryParam(OUTPUT) String output,
			@Context HttpServletRequest request) {
		String query = mCoinsService.getCataloguePartQuery(context, id);
		return mSparqlWebService.selectPostForm(query, output, request);
	}

	/**
	 * Create a new <B>Parameter</B>
	 * 
	 * @param context
	 *            The context or graph
	 * @param modelURI
	 *            The URI of the model
	 * @param name
	 *            The name of the <B>Parameter</B>
	 * @param userID
	 *            A user defined identifier (for convenience)
	 * @param defaultValue
	 *            The default value
	 * @param creator
	 *            URI referring to the user that created this <B>Amount</B>
	 * @return The id of the created <B>Amount</B>
	 */
	@POST
	@Path(PATH_PARAMETER)
	@Consumes(MIME_TYPE)
	public Response createParameter(@QueryParam(CONTEXT) String context,
			@QueryParam(MODEL_URI) String modelURI,
			@QueryParam(NAME) String name, @QueryParam(USER_ID) String userID,
			@QueryParam(DEFAULT_VALUE) String defaultValue,
			@QueryParam(CREATOR) String creator) {
		if (name == null) {
			return Response.serverError().entity("Name cannot be null").build();
		}
		if (context == null) {
			context = mConfigurationService.getDefaultContext();
		}
		String identifier;
		try {
			identifier = mCoinsService.createParameter(context, modelURI, name,
					userID, defaultValue, creator);
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
		return Response
				.serverError()
				.entity("Something went wrong when creating the parameter part")
				.build();
	}

	/**
	 * Delete a <B>Parameter</B>
	 * 
	 * @param context
	 * @param id
	 * @return OK if the <B>Parameter</B> was deleted
	 */
	@DELETE
	@Path(PATH_PARAMETER)
	@Consumes(MIME_TYPE)
	public Response deleteParameter(@QueryParam(CONTEXT) String context,
			@QueryParam(ID) String id) {
		if (mCoinsService.deleteParameter(context, id)) {
			return Response.ok().build();
		}
		return Response.serverError().entity("Cannot delete Parameter").build();
	}

	/**
	 * Get a <B>Parameter</B>
	 * 
	 * @param context
	 *            context or graph
	 * @param id
	 *            The id of the <B>Parameter</B>
	 * @param output
	 *            The way the output should be formatted
	 *            (json/xml/csv/html/tabs)
	 * @param request
	 * @return the <B>Parameter</B> formatted the way specified by means of the
	 *         output
	 */
	@GET
	@Path(PATH_PARAMETER)
	@Consumes(MIME_TYPE)
	public Response getParameter(@QueryParam(CONTEXT) String context,
			@QueryParam(ID) String id, @QueryParam(OUTPUT) String output,
			@Context HttpServletRequest request) {
		String query = mCoinsService.getParameterQuery(context, id);
		return mSparqlWebService.selectPostForm(query, output, request);
	}

	/**
	 * Add a <B>Parameter</P> to an <B>Explicit3DRepresentation</B> object via
	 * the cbim:firstParameter relation. Only one parameter is allowed in this
	 * relation so any old firstParameter relation will be overwritten
	 * 
	 * @param context
	 *            context or graph
	 * @param explicit3DRepresentation
	 *            Identifier of the <B>Explicit3DRepresenation</B> object
	 * @param firstParameter
	 *            Identifier of first parameter
	 * @param modifier
	 *            Identifier of <B>PersonOrOrganisation</B> that modifies the
	 *            object
	 * @return OK if success
	 */
	@POST
	@Path(PATH_LINK_FIRST_PARAMETER)
	@Consumes(MIME_TYPE)
	public Response linkFirstParameter(
			@QueryParam(CONTEXT) String context,
			@QueryParam("explicit3DRepresentation") String explicit3DRepresentation,
			@QueryParam("firstParameter") String firstParameter,
			@QueryParam(MODIFIER) String modifier) {
		try {
			mCoinsService.setFirstParameter(context, explicit3DRepresentation,
					firstParameter, modifier);
		} catch (InvalidArgumentException | MalformedQueryException
				| UpdateExecutionException | MarmottaException e) {
			e.printStackTrace();
			return Response
					.serverError()
					.entity("Setting first parameter of <"
							+ explicit3DRepresentation + "> to <"
							+ firstParameter + "> failed").build();
		}
		return Response.ok().build();
	}

	/**
	 * Add a <B>Parameter</P> to another <B>Parameter</B> object via the
	 * cbim:nextParameter relation. Only one parameter is allowed in this
	 * relation so any old nextParameter relation will be overwritten
	 * 
	 * @param context
	 *            context or graph
	 * @param parameter
	 *            Identifier of <B>Parameter</B>
	 * @param nextParameter
	 *            Identifier of next <B>Parameter</B>
	 * @param modifier
	 *            Identifier of <B>PersonOrOrganisation</B> that modifies the
	 *            object
	 * @return OK if success
	 */
	@POST
	@Path(PATH_LINK_NEXT_PARAMETER)
	@Consumes(MIME_TYPE)
	public Response linkNextParameter(@QueryParam(CONTEXT) String context,
			@QueryParam("parameter") String parameter,
			@QueryParam("nextParameter") String nextParameter,
			@QueryParam(MODIFIER) String modifier) {
		try {
			mCoinsService.setNextParameter(context, parameter, nextParameter,
					modifier);
		} catch (InvalidArgumentException | MalformedQueryException
				| UpdateExecutionException | MarmottaException e) {
			e.printStackTrace();
			return Response
					.serverError()
					.entity("Setting next parameter of <" + parameter
							+ "> to <" + nextParameter + "> failed").build();
		}
		return Response.ok().build();
	}

	/**
	 * Add a Physical parent relation. A child can only have one Physical parent
	 * so if a physical parent relation already exists, it will be modified.
	 * Otherwise a new one will be added.
	 * 
	 * @param context
	 *            context or graph
	 * @param child
	 *            Child identifier
	 * @param parent
	 *            Parent identifier
	 * @param modifier
	 *            Identifier of <B>PersonOrOrganisation</B> that modifies the
	 *            object
	 * @return OK if success
	 */
	@POST
	@Path(PATH_LINK_PHYSICAL_PARENT)
	@Consumes(MIME_TYPE)
	public Response linkPhysicalParent(@QueryParam(CONTEXT) String context,
			@QueryParam(CHILD) String child, @QueryParam(PARENT) String parent,
			@QueryParam(MODIFIER) String modifier) {
		try {
			mCoinsService.setPysicalParent(context, child, parent, modifier);
		} catch (InvalidArgumentException | MalformedQueryException
				| UpdateExecutionException | MarmottaException e) {
			e.printStackTrace();
			return Response
					.serverError()
					.entity("Setting physical parent of <" + child + "> to <"
							+ parent + "> failed").build();
		}
		return Response.ok().build();
	}

	/**
	 * Add a Physical child relation.
	 * 
	 * @param context
	 *            context or graph
	 * @param child
	 *            Child identifier
	 * @param parent
	 *            Parent identifier
	 * @param modifier
	 *            Identifier of <B>PersonOrOrganisation</B> that modifies the
	 *            object
	 * @return OK if success
	 */
	@POST
	@Path(PATH_LINK_PHYSICAL_CHILD)
	@Consumes(MIME_TYPE)
	public Response linkPhysicalChild(@QueryParam(CONTEXT) String context,
			@QueryParam(CHILD) String child, @QueryParam(PARENT) String parent,
			@QueryParam(MODIFIER) String modifier) {
		try {
			mCoinsService.setPysicalChild(context, child, parent, modifier);
		} catch (InvalidArgumentException | MalformedQueryException
				| UpdateExecutionException | MarmottaException e) {
			e.printStackTrace();
			return Response
					.serverError()
					.entity("Setting physical child of <" + parent + "> to <"
							+ child + "> failed").build();
		}
		return Response.ok().build();
	}

	/**
	 * Add a Spatial parent relation. A child can only have one Spatial parent
	 * so if a physical parent relation already exists, it will be modified.
	 * Otherwise a new one will be added.
	 * 
	 * @param context
	 *            context or graph
	 * @param child
	 *            Child identifier
	 * @param parent
	 *            Parent identifier
	 * @param modifier
	 *            Identifier of <B>PersonOrOrganisation</B> that modifies the
	 *            object
	 * @return OK if success
	 */
	@POST
	@Path(PATH_LINK_SPATIAL_PARENT)
	@Consumes(MIME_TYPE)
	public Response linkSpatialParent(@QueryParam(CONTEXT) String context,
			@QueryParam(CHILD) String child, @QueryParam(PARENT) String parent,
			@QueryParam(MODIFIER) String modifier) {
		try {
			mCoinsService.setSpatialParent(context, child, parent, modifier);
		} catch (InvalidArgumentException | MalformedQueryException
				| UpdateExecutionException | MarmottaException e) {
			e.printStackTrace();
			return Response
					.serverError()
					.entity("Setting spatial parent of <" + child + "> to <"
							+ parent + "> failed").build();
		}
		return Response.ok().build();
	}

	/**
	 * Add a Spatial child relation.
	 * 
	 * @param context
	 *            context or graph
	 * @param child
	 *            Child identifier
	 * @param parent
	 *            Parent identifier
	 * @param modifier
	 *            Identifier of <B>PersonOrOrganisation</B> that modifies the
	 *            object
	 * @return OK if success
	 */
	@POST
	@Path(PATH_LINK_SPATIAL_CHILD)
	@Consumes(MIME_TYPE)
	public Response linkSpatialChild(@QueryParam(CONTEXT) String context,
			@QueryParam(CHILD) String child, @QueryParam(PARENT) String parent,
			@QueryParam(MODIFIER) String modifier) {
		try {
			mCoinsService.setSpatialChild(context, child, parent, modifier);
		} catch (InvalidArgumentException | MalformedQueryException
				| UpdateExecutionException | MarmottaException e) {
			e.printStackTrace();
			return Response
					.serverError()
					.entity("Setting spatial child of <" + parent + "> to <"
							+ child + "> failed").build();
		}
		return Response.ok().build();
	}

	/**
	 * Link an Explicit3DRepresentation to a <B>PhysicalObject</B> A Physical
	 * Object can only have one shape
	 * 
	 * @param context
	 *            Context / Graph
	 * @param physicalobject
	 *            id of <B>PhysicalObject</B>
	 * @param shape
	 *            id of Explicit3DRepresentation
	 * @param modifier
	 * @return OK if success
	 */
	@POST
	@Path(PATH_LINK_SHAPE)
	@Consumes(MIME_TYPE)
	public Response linkShape(@QueryParam(CONTEXT) String context,
			@QueryParam(PHYSICALOBJECT) String physicalobject,
			@QueryParam(SHAPE) String shape,
			@QueryParam(MODIFIER) String modifier) {
		try {
			mCoinsService.setShape(context, physicalobject, shape, modifier);
		} catch (InvalidArgumentException | MalformedQueryException
				| UpdateExecutionException | MarmottaException e) {
			e.printStackTrace();
			return Response
					.serverError()
					.entity("Setting physical parent of <" + physicalobject
							+ "> to <" + shape + "> failed").build();
		}
		return Response.ok().build();
	}

	/**
	 * Add <B>NonFunctionalRequirement</B>(s) to <B>FunctionFulfillers</B>.
	 * Leaves previously linked NonFunctionalRequirements untouched.
	 * 
	 * @param context
	 *            Context or Graph
	 * @param functionfulfiller
	 *            id of <B>FunctionFulfiller</B>
	 * @param nonfunctionalrequirement
	 *            list of <B>NonFunctionalRequirement</B> id's
	 * @param modifier
	 *            who did this modification
	 * @return OK if success
	 */
	@POST
	@Path(PATH_LINK_NON_FUNCTIONAL_REQUIREMENT)
	@Consumes(MIME_TYPE)
	public Response linkNonFunctionalRequirement(
			@QueryParam(CONTEXT) String context,
			@QueryParam(FUNCTIONFULFILLER) String functionfulfiller,
			@QueryParam(NONFUNCTIONALREQUIREMENT) String[] nonfunctionalrequirement,
			@QueryParam(MODIFIER) String modifier) {
		try {
			mCoinsService.linkNonFunctionalRequirement(context,
					functionfulfiller, nonfunctionalrequirement, modifier);
		} catch (InvalidArgumentException | MalformedQueryException
				| UpdateExecutionException | MarmottaException e) {
			e.printStackTrace();
			return Response.serverError()
					.entity("Linking NonFunctionalRequirements failed").build();
		}
		return Response.ok().build();
	}

	/**
	 * Add Document(s) to <B>PhysicalObjects</B>. Leaves previously linked
	 * Documents untouched.
	 * 
	 * @param context
	 *            Context or Graph
	 * @param physicalobject
	 *            id of <B>PhysicalObject</B>
	 * @param document
	 *            list of Document id's
	 * @param modifier
	 *            who did this modification
	 * @return OK if success
	 */
	@POST
	@Path(PATH_LINK_DOCUMENT)
	@Consumes(MIME_TYPE)
	public Response linkDocument(@QueryParam(CONTEXT) String context,
			@QueryParam(PHYSICALOBJECT) String physicalobject,
			@QueryParam(DOCUMENT) String[] document,
			@QueryParam(MODIFIER) String modifier) {
		try {
			mCoinsService.linkDocument(context, physicalobject, document,
					modifier);
		} catch (InvalidArgumentException | MalformedQueryException
				| UpdateExecutionException | MarmottaException e) {
			e.printStackTrace();
			return Response.serverError().entity("Linking Documents failed")
					.build();
		}
		return Response.ok().build();
	}

	/**
	 * Link a Function to one of more <B>PhysicalObjects</B> by the fulfills
	 * property Leaves previously linked functions untouched
	 * 
	 * @param context
	 *            Context or Graph
	 * @param physicalobject
	 *            <B>PhysicalObject</B> Id
	 * @param fulfills
	 *            List containing one or more Function ids
	 * @param modifier
	 * @return OK if success
	 */
	@POST
	@Path(PATH_LINK_FULFILLS)
	@Consumes(MIME_TYPE)
	public Response linkPhysicalObjectFulfills(
			@QueryParam(CONTEXT) String context,
			@QueryParam(PHYSICALOBJECT) String physicalobject,
			@QueryParam(FULFILLS) String[] fulfills,
			@QueryParam(MODIFIER) String modifier) {
		try {
			mCoinsService.linkPhysicalObjectFulfills(context, physicalobject,
					fulfills, modifier);
		} catch (InvalidArgumentException | MalformedQueryException
				| UpdateExecutionException | MarmottaException e) {
			e.printStackTrace();
			return Response.serverError().entity("Linking functions failed")
					.build();
		}
		return Response.ok().build();
	}

	/**
	 * Link a <B>FunctionFulfiller</B> to one or more functions by the
	 * fulFilledBy property Leaves previously linked <B>FunctionFulfillers</B>
	 * untouched
	 * 
	 * @param context
	 *            Context or Graph
	 * @param function
	 *            Function Id
	 * @param isFulfilledBy
	 *            List containing one or more <B>FunctionFulfiller</B> ids
	 * @param modifier
	 * @return OK if success
	 */
	@POST
	@Path(PATH_LINK_FULFILLED_BY)
	@Consumes(MIME_TYPE)
	public Response linkFunctionIsFulfilledBy(
			@QueryParam(CONTEXT) String context,
			@QueryParam(FUNCTION) String function,
			@QueryParam(IS_FULFILLED_BY) String[] isFulfilledBy,
			@QueryParam(MODIFIER) String modifier) {
		try {
			mCoinsService.linkFunctionIsFulfilledBy(context, function,
					isFulfilledBy, modifier);
		} catch (InvalidArgumentException | MalformedQueryException
				| UpdateExecutionException | MarmottaException e) {
			e.printStackTrace();
			return Response.serverError()
					.entity("Linking function fulfiller failed").build();
		}
		return Response.ok().build();
	}

	/**
	 * Link a task to a <B>FunctionFulfiller</B> by the isAffectedBy property
	 * 
	 * @param context
	 *            Context of Graph
	 * @param functionfulfiller
	 *            <B>FunctionFulfiller</B> id
	 * @param isAffectedBy
	 *            id of <B>Task<B> that affects the <B>FunctionFulfiller</B>
	 * @param modifier
	 * @return OK if success
	 */
	@POST
	@Path(PATH_LINK_ISAFFECTEDBY)
	@Consumes(MIME_TYPE)
	public Response linkIsAffectedBy(@QueryParam(CONTEXT) String context,
			@QueryParam(FUNCTIONFULFILLER) String functionfulfiller,
			@QueryParam(IS_AFFECTED_BY) String isAffectedBy,
			@QueryParam(MODIFIER) String modifier) {
		try {
			mCoinsService.linkIsAffectedBy(context, functionfulfiller,
					isAffectedBy, modifier);
		} catch (InvalidArgumentException | MalformedQueryException
				| UpdateExecutionException | MarmottaException e) {
			e.printStackTrace();
			return Response.serverError()
					.entity("Linking function fulfiller failed").build();
		}
		return Response.ok().build();
	}

	/**
	 * Validate the complete Coins model present in this context
	 * 
	 * @param context
	 * @return the validation result
	 */
	@GET
	@Path(PATH_VALIDATEALL)
	@Produces(MIME_TYPE)
	public Response validateAll(@QueryParam(CONTEXT) String context) {
		List<String> result = mCoinsService.validate(context,
				ValidationAspect.ALL);
		return Response.ok().entity(result).build();
	}

	/**
	 * Validate an aspect of the Coins model present in this context
	 * 
	 * @param context
	 *            The Context or Graph to be validated
	 * @param aspect
	 *            The aspect to be validated <B>PhysicalParent<\B> Check for
	 *            duplicate physical parents
	 * @return the validation result
	 */
	@GET
	@Path(PATH_VALIDATE)
	@Produces(MIME_TYPE)
	public Response validate(@QueryParam(CONTEXT) String context,
			@QueryParam("aspect") String aspect) {
		for (ValidationAspect as : ValidationAspect.values()) {
			if (as.name().equalsIgnoreCase(aspect)) {
				List<String> result = mCoinsService.validate(context, as);
				return Response.ok().entity(result).build();
			}
		}
		return Response.serverError()
				.entity("Unknown validation aspect " + aspect).build();
	}

	/**
	 * Create a new <B>Terminal</B>
	 * 
	 * @param context
	 *            The context or named graph
	 * @param modelURI
	 *            The URI of the model
	 * @param locator
	 *            Identifier of the locator
	 * @param name
	 *            The name of the <B>Terminal</B>
	 * @param userID
	 *            A user defined identifier (for convenience)
	 * @param layerindex
	 *            Layer index
	 * @param creator
	 *            Identifier of the <B>PersonOrOrganisation</B> that created the
	 *            terminal
	 * @return The id of the created <B>Terminal</B>
	 */
	@POST
	@Path(PATH_TERMINAL)
	@Consumes(MIME_TYPE)
	public Response createTerminal(@QueryParam(CONTEXT) String context,
			@QueryParam(MODEL_URI) String modelURI,
			@QueryParam(NAME) String name, @QueryParam(USER_ID) String userID,
			@QueryParam(LOCATOR) String locator,
			@QueryParam(LAYERINDEX) int layerindex,
			@QueryParam(CREATOR) String creator) {
		if (name == null) {
			return Response.serverError().entity("Name cannot be null").build();
		}
		if (context == null) {
			context = mConfigurationService.getDefaultContext();
		}
		String identifier;
		try {
			identifier = mCoinsService.createTerminal(context, modelURI, name,
					userID, locator, layerindex, creator);
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
				.entity("Something went wrong when creating the Terminal")
				.build();
	}

	/**
	 * Get a <B>Terminal</B>
	 * 
	 * @param context
	 *            The context or graph
	 * @param id
	 *            The id of the <B>Terminal</B>
	 * @param output
	 *            The way the output should be formatted
	 *            (json/xml/csv/html/tabs)
	 * @param request
	 * @return the <B>Terminal</B> formatted the way specified by means of the
	 *         output
	 */
	@GET
	@Path(PATH_TERMINAL)
	@Consumes(MIME_TYPE)
	public Response getTerminal(@QueryParam(CONTEXT) String context,
			@QueryParam(ID) String id, @QueryParam(OUTPUT) String output,
			@Context HttpServletRequest request) {
		String query = mCoinsService.getTerminalQuery(context, id);
		return mSparqlWebService.selectPostForm(query, output, request);
	}

	/**
	 * Delete a <B>Terminal</B>
	 * 
	 * @param context
	 * @param id
	 * @return OK if the <B>Terminal</B> was deleted
	 */
	@DELETE
	@Path(PATH_TERMINAL)
	@Consumes(MIME_TYPE)
	public Response deleteTerminal(@QueryParam(CONTEXT) String context,
			@QueryParam(ID) String id) {
		if (mCoinsService.deleteTerminal(context, id)) {
			return Response.ok().build();
		}
		return Response.serverError().entity("Cannot delete Terminal").build();
	}

	/**
	 * Insert an attribute of type String This method ignores the fact that
	 * duplicate entries may result if it is executed No validation on attribute
	 * names is done either The modification date is not updated
	 * 
	 * @param context
	 *            Context or Graph
	 * @param object
	 *            id of the object
	 * @param name
	 *            name of the attribute
	 * @param value
	 *            value of the attribute
	 * @return OK if success
	 */
	@POST
	@Path(PATH_ADD_ATTRIBUTE_STRING)
	@Consumes(MIME_TYPE)
	public Response addAttributeString(@QueryParam(CONTEXT) String context,
			@QueryParam(OBJECT) String object, @QueryParam(NAME) String name,
			@QueryParam(VALUE) String value) {
		try {
			mCoinsService.addAttributeString(context, object, name, value);
		} catch (InvalidArgumentException | MalformedQueryException
				| UpdateExecutionException | MarmottaException e) {
			e.printStackTrace();
			return Response.serverError().entity("Adding attribute failed")
					.build();
		}
		return Response.ok().build();
	}

	/**
	 * Insert an attribute of type Float This method ignores the fact that
	 * duplicate entries may result if it is executed No validation on attribute
	 * names is done either The modification date is not updated
	 * 
	 * @param context
	 *            Context or Graph
	 * @param object
	 *            id of the object
	 * @param name
	 *            name of the attribute
	 * @param value
	 *            value of the attribute
	 * @return OK if success
	 */
	@POST
	@Path(PATH_ADD_ATTRIBUTE_FLOAT)
	@Consumes(MIME_TYPE)
	public Response addAttributeFloat(@QueryParam(CONTEXT) String context,
			@QueryParam(OBJECT) String object, @QueryParam(NAME) String name,
			@QueryParam(VALUE) Double value) {
		try {
			mCoinsService.addAttributeFloat(context, object, name, value);
		} catch (InvalidArgumentException | MalformedQueryException
				| UpdateExecutionException | MarmottaException e) {
			e.printStackTrace();
			return Response.serverError().entity("Adding attribute failed")
					.build();
		}
		return Response.ok().build();
	}

	/**
	 * Insert an attribute of type Resource (URL) This method ignores the fact
	 * that duplicate entries may result if it is executed No validation on
	 * attribute names is done either The modification date is not updated
	 * 
	 * @param context
	 *            Context or Graph
	 * @param object
	 *            id of the object
	 * @param name
	 *            name of the attribute
	 * @param value
	 *            value of the attribute
	 * @return OK if success
	 */
	@POST
	@Path(PATH_ADD_ATTRIBUTE_RESOURCE)
	@Consumes(MIME_TYPE)
	public Response addAttributeResource(@QueryParam(CONTEXT) String context,
			@QueryParam(OBJECT) String object, @QueryParam(NAME) String name,
			@QueryParam(VALUE) String value) {
		try {
			mCoinsService.addAttributeResource(context, object, name, value);
		} catch (InvalidArgumentException | MalformedQueryException
				| UpdateExecutionException | MarmottaException e) {
			e.printStackTrace();
			return Response.serverError().entity("Adding attribute failed")
					.build();
		}
		return Response.ok().build();
	}

	/**
	 * Insert an attribute of type Integer This method ignores the fact that
	 * duplicate entries may result if it is executed No validation on attribute
	 * names is done either The modification date is not updated
	 * 
	 * @param context
	 *            Context or Graph
	 * @param object
	 *            id of the object
	 * @param name
	 *            name of the attribute
	 * @param value
	 *            value of the attribute
	 * @return OK if success
	 */
	@POST
	@Path(PATH_ADD_ATTRIBUTE_INTEGER)
	@Consumes(MIME_TYPE)
	public Response addAttributeInteger(@QueryParam(CONTEXT) String context,
			@QueryParam(OBJECT) String object, @QueryParam(NAME) String name,
			@QueryParam(VALUE) int value) {
		try {
			mCoinsService.addAttributeInt(context, object, name, value);
		} catch (InvalidArgumentException | MalformedQueryException
				| UpdateExecutionException | MarmottaException e) {
			e.printStackTrace();
			return Response.serverError().entity("Adding attribute failed")
					.build();
		}
		return Response.ok().build();
	}

	/**
	 * Insert an attribute of type Date This method ignores the fact that
	 * duplicate entries may result if it is executed No validation on attribute
	 * names is done either The modification date is not updated Formatting the
	 * date in the correct format is left to the user
	 * 
	 * @param context
	 *            Context or Graph
	 * @param object
	 *            id of the object
	 * @param name
	 *            name of the attribute
	 * @param value
	 *            value of the attribute
	 * @return OK if success
	 */
	@POST
	@Path(PATH_ADD_ATTRIBUTE_DATE)
	@Consumes(MIME_TYPE)
	public Response addAttributeDate(@QueryParam(CONTEXT) String context,
			@QueryParam(OBJECT) String object, @QueryParam(NAME) String name,
			@QueryParam(VALUE) String value) {
		try {
			mCoinsService.addAttributeDate(context, object, name, value);
		} catch (InvalidArgumentException | MalformedQueryException
				| UpdateExecutionException | MarmottaException e) {
			e.printStackTrace();
			return Response.serverError().entity("Adding attribute failed")
					.build();
		}
		return Response.ok().build();
	}
}