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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import nl.tno.coinsapi.CoinsFormat;
import nl.tno.coinsapi.services.ICoinsApiService;
import nl.tno.coinsapi.services.ICoinsDocFileService;
import nl.tno.coinsapi.tools.QueryBuilder.FieldType;
import nl.tno.coinsapi.tools.ValidationAspect;

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
			{ "cbimotl", "http://www.coinsweb.nl/cbim-otl-1.1.owl#" },
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
	 * PersonOrOrganisation
	 */
	public static final String PATH_PERSON_OR_ORGANISATION = "/personorganisation";
	/**
	 * State
	 */
	public static final String PATH_STATE = "/state";
	/**
	 * State stateOf FunctionFulfiller
	 */
	public static final String PATH_LINK_STATE_OF = PATH_STATE + "/stateof";

	/**
	 * Link Performance
	 */
	public static final String PATH_LINK_STATE_PERFORMANCE = PATH_STATE
			+ "/performance";

	/**
	 * previous state of state
	 */
	public static final String PATH_LINK_PREVIOUS_STATE = PATH_STATE
			+ "/previousstate";
	/**
	 * Description
	 */
	public static final String PATH_DESCRIPTION = "/description";
	/**
	 * Function
	 */
	public static final String PATH_FUNCTION = "/function";
	/**
	 * Link a function to a function fulfiller (isFulfilledBy)
	 */
	public static final String PATH_LINK_FULFILLED_BY = PATH_FUNCTION
			+ "/isfulfilledby";
	/**
	 * PhysicalObject
	 */
	public static final String PATH_PHYSICAL_OBJECT = "/physicalobject";
	/**
	 * PhysicalObject isSituatedIn Space
	 */
	public static final String PATH_LINK_IS_SITUATED_IN = PATH_PHYSICAL_OBJECT
			+ "/issituatedin";

	/**
	 * Edit physical parent relation
	 */
	public static final String PATH_LINK_PHYSICAL_PARENT = PATH_PHYSICAL_OBJECT
			+ "/physicalparent";
	/**
	 * Add a physical child relation
	 */
	public static final String PATH_LINK_PHYSICAL_CHILD = PATH_PHYSICAL_OBJECT
			+ "/physicalchild";

	/**
	 * Link Documents to a PhysicalObject
	 */
	public static final String PATH_LINK_DOCUMENT = PATH_PHYSICAL_OBJECT
			+ "/document";

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
	 * Link a max bounding box to a locator
	 */
	public static final String PATH_LINK_MAX_BOUNDING_BOX = PATH_LOCATOR
			+ "/maxboundingbox";

	/**
	 * Link a min bounding box to a locator
	 */
	public static final String PATH_LINK_MIN_BOUNDING_BOX = PATH_LOCATOR
			+ "/minboundingbox";

	/**
	 * Explicit3DRepresentation
	 */
	public static final String PATH_EXPLICIT_3D_REPRESENTATION = "/explicit3drepresentation";

	/**
	 * Add the first parameter to an Explicit3DRepresentation
	 */
	public static final String PATH_LINK_FIRST_PARAMETER = PATH_EXPLICIT_3D_REPRESENTATION
			+ "/firstparameter";

	/**
	 * Task
	 */
	public static final String PATH_TASK = "/task";
	/**
	 * Performance
	 */
	public static final String PATH_PERFORMANCE = "/performance";
	/**
	 * Performance of State/FunctionFulfiller
	 */
	public static final String PATH_LINK_PERFORMANCEOF = PATH_PERFORMANCE
			+ "/performanceof";
	/**
	 * Link a property value to Performance
	 */
	public static final String PATH_LINK_PROPERTY_VALUE = PATH_PERFORMANCE
			+ "/propertyvalue";
	/**
	 * Amount
	 */
	public static final String PATH_AMOUNT = "/amount";
	/**
	 * Link a Locator to an Amount/FunctionFulfiller/Terminal
	 */
	public static final String PATH_AMOUNT_LINK_LOCATOR = PATH_AMOUNT
			+ "/locator";

	/**
	 * CataloguePart
	 */
	public static final String PATH_CATALOGUE_PART = "/cataloguepart";
	/**
	 * Connection
	 */
	public static final String PATH_CONNECTION = "/connection";

	/**
	 * Link a male terminal to a connection
	 */
	public static final String PATH_LINK_MALE_TERMINAL = PATH_CONNECTION
			+ "/maleterminal";

	/**
	 * Link a female terminal to a connection
	 */
	public static final String PATH_LINK_FEMALE_TERMINAL = PATH_CONNECTION
			+ "/femaleterminal";

	/**
	 * References to documents from COINS containers
	 */
	public static final String PATH_DOCUMENT_REFERENCE = "/doc";

	/**
	 * Initialize the context
	 */
	public static final String PATH_INITIALIZE_CONTEXT = "/initializecontext";

	/**
	 * Set the layerindex
	 */
	public static final String PATH_LAYERINDEX = "/layerindex";

	/**
	 * Function fulfiller (Physical object/Space)
	 */
	public static final String PATH_FUNCTION_FULFILLER = "/functionfulfiller";

	/**
	 * Link NonFunctionalRequirements to a Function Fulfiller
	 */
	public static final String PATH_LINK_NON_FUNCTIONAL_REQUIREMENT = PATH_FUNCTION_FULFILLER
			+ "/nonfunctionalrequirement";

	/**
	 * Link Performance
	 */
	public static final String PATH_LINK_FUNCTION_FULFILLER_PERFORMANCE = PATH_FUNCTION_FULFILLER
			+ "/performance";

	/**
	 * currentState of FunctionFulfiller
	 */
	public static final String PATH_LINK_CURRENT_STATE = PATH_FUNCTION_FULFILLER
			+ "/currentstate";

	/**
	 * A Function fulfiller affected by a task
	 */
	public static final String PATH_LINK_ISAFFECTEDBY = PATH_FUNCTION_FULFILLER
			+ "/isaffectedby";

	/**
	 * Link a function fulfiller to a locator
	 */
	public static final String PATH_FUNCTIONFULFILLER_LINK_LOCATOR = PATH_FUNCTION_FULFILLER
			+ "/locator";

	/**
	 * Link an Explicit3DRepresentation to a FunctionFulfiller
	 */
	public static final String PATH_LINK_SHAPE = PATH_FUNCTION_FULFILLER
			+ "/shape";

	/**
	 * Link a function fulfiller to a function (fulfills)
	 */
	public static final String PATH_LINK_FULFILLS = PATH_FUNCTION_FULFILLER
			+ "/fulfills";

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
	 * PhysicalObject isSituatedIn Space
	 */
	public static final String PATH_LINK_SITUATES = PATH_SPACE
			+ "/situates";
	/**
	 * Edit spatial parent relation
	 */
	public static final String PATH_LINK_SPATIAL_PARENT = PATH_SPACE
			+ "/spatialparent";
	/**
	 * Add a spatial child relation
	 */
	public static final String PATH_LINK_SPATIAL_CHILD = PATH_SPACE
			+ "/spatialchild";
	/**
	 * Parameter
	 */
	public static final String PATH_PARAMETER = "/parameter";
	/**
	 * Add a parameter to the next one
	 */
	public static final String PATH_LINK_NEXT_PARAMETER = PATH_PARAMETER
			+ "/nextparameter";

	/**
	 * Terminal
	 */
	public static final String PATH_TERMINAL = "/terminal";
	/**
	 * Terminal locator
	 */
	public static final String PATH_TERMINAL_LINK_LOCATOR = PATH_TERMINAL
			+ "/locator";
	/**
	 * Property type
	 */
	public static final String PATH_PROPERTY_TYPE = "/propertytype";
	/**
	 * Property value
	 */
	public static final String PATH_PROPERTY_VALUE = "/propertyvalue";
	/**
	 * Reference Frame
	 */
	public static final String PATH_ADD_REFERENCE_FRAME = "/referenceframe";
	/**
	 * Verification
	 */
	public static final String PATH_VERIFICATION = "/verification";
	/**
	 * Link function fulfiller to Verification
	 * (cbim:verificationFunctionFulfiller)
	 */
	public static final String PATH_LINK_VERIFICATION_FUNCTION_FULFILLER = PATH_VERIFICATION
			+ "/functionfulfiller";

	/**
	 * Link PersonOrOrganisation to Verification
	 * (cbimfs:plannedVerificationPerformer)
	 */
	public static final String PATH_LINK_VERIFICATION_PLANNED_PERFORMER = PATH_VERIFICATION
			+ "/plannedperformer";

	/**
	 * Link PersonOrOrganisation to Verification
	 * (cbimfs:verificationAuthorizedBy)
	 */
	public static final String PATH_LINK_VERIFICATION_AUTHORIZED_BY = PATH_VERIFICATION
			+ "/authorizedby";

	/**
	 * Link NonFunctionalRequirement to Verification
	 * (cbimfs:verificationRequirement)
	 */
	public static final String PATH_LINK_VERIFICATION_NON_FUNCTIONAL_REQUIREMENT = PATH_VERIFICATION
			+ "/nonfunctionalrequirement";

	/**
	 * Link Requirement to Verification (cbim:verificationRequirement)
	 */
	public static final String PATH_LINK_VERIFICATION_REQUIREMENT = PATH_VERIFICATION
			+ "/requirement";

	/**
	 * Link performer(PersonOrOrganisation) to Verification
	 * (cbim:verificationPerformer)
	 */
	public static final String PATH_LINK_VERIFICATION_PERFORMER = PATH_VERIFICATION
			+ "/performer";

	/**
	 * Verification authorization date
	 */
	public static final String PATH_VERIFICATION_AUTHORIZATION_DATE = PATH_VERIFICATION
			+ "/authorizationdate";
	/**
	 * Verification authorization defects
	 */
	public static final String PATH_VERIFICATION_AUTHORIZATION_DEFECTS = PATH_VERIFICATION
			+ "/authorizationdefects";
	/**
	 * Verification authorization measures
	 */
	public static final String PATH_VERIFICATION_AUTHORIZATION_MEASURES = PATH_VERIFICATION
			+ "/authorizationmeasures";
	/**
	 * Verification authorization remarks
	 */
	public static final String PATH_VERIFICATION_AUTHORIZATION_REMARKS = PATH_VERIFICATION
			+ "/authorizationremarks";
	/**
	 * Verification planned remarks
	 */
	public static final String PATH_VERIFICATION_PLANNED_REMARKS = PATH_VERIFICATION
			+ "/plannedremarks";
	/**
	 * Planned verification date
	 */
	public static final String PATH_VERIFICATION_PLANNED_DATE = PATH_VERIFICATION
			+ "/planneddate";
	/**
	 * Planned verification method
	 */
	public static final String PATH_VERIFICATION_PLANNED_METHOD = PATH_VERIFICATION
			+ "/plannedmethod";
	/**
	 * Planned verification work package
	 */
	public static final String PATH_VERIFICATION_PLANNED_WORK_PACKAGE = PATH_VERIFICATION
			+ "/plannedworkpackage";
	/**
	 * Verification risks
	 */
	public static final String PATH_VERIFICATION_RISKS = PATH_VERIFICATION
			+ "/risks";
	/**
	 * Application/json
	 */
	public static final String MIME_TYPE = "application/json";

	private static final String MIN_BOUNDING_BOX = "minBoundingBox";
	private static final String MAX_BOUNDING_BOX = "maxBoundingBox";
	private static final String UNIT = "unit";
	private static final String VALUE_DOMAIN = "valueDomain";
	private static final String PROPERTY_TYPE = "propertyType";
	private static final String STATE = "state";
	private static final String NEXT_PARAMETER = "nextParameter";
	private static final String PARAMETER = "parameter";
	private static final String EXPLICIT_3D_REPRESENTATION = "explicit3DRepresentation";
	private static final String OBJECT = "object";
	private static final String LOCATOR = "locator";
	private static final String IS_AFFECTED_BY = "isAffectedBy";
	private static final String IS_FULFILLED_BY = "isFulfilledBy";
	private static final String FUNCTION = "function";
	private static final String FULFILLS = "fulfills";
	private static final String DOCUMENT = "document";
	private static final String NON_FUNCTIONAL_REQUIREMENT = "nonFunctionalRequirement";
	private static final String FUNCTION_FULFILLER = "functionFulfiller";
	private static final String SHAPE = "shape";
	private static final String PHYSICAL_OBJECT = "physicalObject";
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
	private static final String PREVIOUS_STATE = "previousState";
	private static final String PERFORMANCE = "performance";
	private static final String PROPERTY_VALUE = "propertyValue";
	private static final String PERFORMANCE_OF = "performanceOf";
	private static final String MALE_TERMINAL = "maleTerminal";
	private static final String CONNECTION = "connection";
	private static final String FEMALE_TERMINAL = "femaleTerminal";
	private static final String ASPECT = "aspect";
	private static final String VERIFICATION_DATE = "verificationDate";
	private static final String VERIFICATION_METHOD = "verificationMethod";
	private static final String VERIFICATION_RESULT = "verificationResult";
	private static final String REFERENCE_FRAME = "referenceFrame";
	private static final String VERIFICATION = "verification";
	private static final String PERFORMER = "performer";
	private static final String REQUIREMENT = "requirement";
	private static final String AUTHORIZER = "authorizer";
	private static final String AUTHORIZATION_DATE = "authorizationDate";
	private static final String AUTHORIZATION_DEFECTS = "authorizationDefects";
	private static final String AUTHORIZATION_MEASURES = "authorizationMeasures";
	private static final String AUTHORIZATION_REMARKS = "authorizationRemarks";
	private static final String PLANNED_REMARKS = "plannedRemarks";
	private static final String PLANNED_DATE = "plannedDate";
	private static final String PLANNED_METHOD = "plannedMethod";
	private static final String PLANNED_WORKPACKAGE = "plannedWorkPackage";
	private static final String RISKS = "risks";
	private static final String AMOUNT = "amount";
	private static final String TERMINAL = "terminal";
	private static final String SPACE = "space";

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
		return Response.ok().entity("v0.2").build();
	}

	/**
	 * Initialize the context. It is advised to use a separate context for each
	 * model you are constructing. A context can easily be exported as a COINS
	 * container. Each context is linked to a modelURI using this
	 * initializeContext method. Prior to creating COINS objects in a context,
	 * the context must be initialized because the COINS objects need the
	 * modelURI for their name space. If you try to initialize a context twice
	 * with different modelURIs you will get an error message.
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
			String result = mCoinsService.initializeContext(context, modelURI);
			if (result == null) {
				return Response.ok().build();
			}
			return Response
					.serverError()
					.entity("The context has been initialized before with modelURI <"
							+ result + ">").build();
		} catch (Exception e) {
			return Response.serverError().entity(e.toString()).build();
		}
	}

	/**
	 * Add a Reference Frame to the context For example
	 * http://www.rws.nl/reference_frameworks/rf-rws.20131101.owl
	 * 
	 * @param context
	 * @param referenceFrame
	 * @return OK if success
	 */
	@POST
	@Path(PATH_ADD_REFERENCE_FRAME)
	@Consumes(MIME_TYPE)
	public Response addReferenceFrame(@QueryParam(CONTEXT) String context,
			@QueryParam(REFERENCE_FRAME) String referenceFrame) {
		try {
			mCoinsService.addReferenceFrame(context, referenceFrame);
			return Response.ok().build();
		} catch (Exception e) {
			return Response.serverError().entity(e.toString()).build();
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
			identifier = mCoinsService.createRequirement(context, name,
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
	 * @param name
	 *            The name of the <B>PersonOrOrganisation</B>
	 * @return The id of the created <B>PersonOrOrganisation</B>
	 */
	@POST
	@Path(PATH_PERSON_OR_ORGANISATION)
	@Consumes(MIME_TYPE)
	public Response createPersonOrOrganisation(
			@QueryParam(CONTEXT) String context, @QueryParam(NAME) String name) {
		if (name == null) {
			return Response.serverError().entity("Name cannot be null").build();
		}
		if (context == null) {
			context = mConfigurationService.getDefaultContext();
		}
		String identifier;
		try {
			identifier = mCoinsService
					.createPersonOrOrganisation(context, name);
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
	 * Set the layer index of an object
	 * 
	 * @param context
	 *            The context or graph
	 * @param id
	 *            The identifier of the object
	 * @param layerIndex
	 *            The layer index
	 * @param modifier
	 *            URI to the modifier of the object
	 * @return OK if successful
	 */
	@POST
	@Path(PATH_LAYERINDEX)
	@Consumes(MIME_TYPE)
	public Response setLayerIndex(@QueryParam(CONTEXT) String context,
			@QueryParam(ID) String id, @QueryParam(LAYER_INDEX) int layerIndex,
			@QueryParam(MODIFIER) String modifier) {
		try {
			mCoinsService.setLayerIndex(context, id, layerIndex, modifier);
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
				.entity("Something went wrong when setting the Layerindex")
				.build();
	}

	/**
	 * Set the authorization date of a <B>Verification</B>
	 * 
	 * @param context
	 *            The context or graph
	 * @param verification
	 *            The identifier of the <B>Verification</B> object
	 * @param authorizationDate
	 *            Authorization date (xsd:dateTime formatted String)
	 * @param modifier
	 *            URI to the modifier of the object
	 * @return OK if successful
	 */
	@POST
	@Path(PATH_VERIFICATION_AUTHORIZATION_DATE)
	@Consumes(MIME_TYPE)
	public Response setVerificationAuthorizationDate(
			@QueryParam(CONTEXT) String context,
			@QueryParam(VERIFICATION) String verification,
			@QueryParam(AUTHORIZATION_DATE) String authorizationDate,
			@QueryParam(MODIFIER) String modifier) {
		try {
			mCoinsService.setVerificationAttribute(context, verification,
					authorizationDate, CoinsFormat.CBIMFS_AUTHORIZATION_DATE,
					modifier, FieldType.DATE);
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
		return Response
				.serverError()
				.entity("Something went wrong when setting the Authorization date")
				.build();
	}

	/**
	 * Set the authorization defects of a <B>Verification</B>
	 * 
	 * @param context
	 *            The context or graph
	 * @param verification
	 *            The identifier of the <B>Verification</B> object
	 * @param authorizationDefects
	 *            Authorization defects (String)
	 * @param modifier
	 *            URI to the modifier of the object
	 * @return OK if successful
	 */
	@POST
	@Path(PATH_VERIFICATION_AUTHORIZATION_DEFECTS)
	@Consumes(MIME_TYPE)
	public Response setVerificationAuthorizationDefects(
			@QueryParam(CONTEXT) String context,
			@QueryParam(VERIFICATION) String verification,
			@QueryParam(AUTHORIZATION_DEFECTS) String authorizationDefects,
			@QueryParam(MODIFIER) String modifier) {
		try {
			mCoinsService.setVerificationAttribute(context, verification,
					authorizationDefects,
					CoinsFormat.CBIMFS_AUTHORIZATION_DEFECTS, modifier,
					FieldType.STRING);
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
		return Response
				.serverError()
				.entity("Something went wrong when setting the Authorization defects")
				.build();
	}

	/**
	 * Set the authorization measures of a <B>Verification</B>
	 * 
	 * @param context
	 *            The context or graph
	 * @param verification
	 *            The identifier of the <B>Verification</B> object
	 * @param authorizationMeasures
	 *            Authorization measures (String)
	 * @param modifier
	 *            URI to the modifier of the object
	 * @return OK if successful
	 */
	@POST
	@Path(PATH_VERIFICATION_AUTHORIZATION_MEASURES)
	@Consumes(MIME_TYPE)
	public Response setVerificationAuthorizationMeasures(
			@QueryParam(CONTEXT) String context,
			@QueryParam(VERIFICATION) String verification,
			@QueryParam(AUTHORIZATION_MEASURES) String authorizationMeasures,
			@QueryParam(MODIFIER) String modifier) {
		try {
			mCoinsService.setVerificationAttribute(context, verification,
					authorizationMeasures,
					CoinsFormat.CBIMFS_AUTHORIZATION_MEASURES, modifier,
					FieldType.STRING);
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
		return Response
				.serverError()
				.entity("Something went wrong when setting the Authorization measures")
				.build();
	}

	/**
	 * Set the authorization remarks of a <B>Verification</B>
	 * 
	 * @param context
	 *            The context or graph
	 * @param verification
	 *            The identifier of the <B>Verification</B> object
	 * @param authorizationRemarks
	 *            Authorization remarks (String)
	 * @param modifier
	 *            URI to the modifier of the object
	 * @return OK if successful
	 */
	@POST
	@Path(PATH_VERIFICATION_AUTHORIZATION_REMARKS)
	@Consumes(MIME_TYPE)
	public Response setVerificationAuthorizationRemarks(
			@QueryParam(CONTEXT) String context,
			@QueryParam(VERIFICATION) String verification,
			@QueryParam(AUTHORIZATION_REMARKS) String authorizationRemarks,
			@QueryParam(MODIFIER) String modifier) {
		try {
			mCoinsService.setVerificationAttribute(context, verification,
					authorizationRemarks,
					CoinsFormat.CBIMFS_AUTHORIZATION_REMARKS, modifier,
					FieldType.STRING);
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
		return Response
				.serverError()
				.entity("Something went wrong when setting the Authorization remarks")
				.build();
	}

	/**
	 * Set the planned remarks of a <B>Verification</B>
	 * 
	 * @param context
	 *            The context or graph
	 * @param verification
	 *            The identifier of the <B>Verification</B> object
	 * @param plannedRemarks
	 *            Planned remarks (String)
	 * @param modifier
	 *            URI to the modifier of the object
	 * @return OK if successful
	 */
	@POST
	@Path(PATH_VERIFICATION_PLANNED_REMARKS)
	@Consumes(MIME_TYPE)
	public Response setVerificationPlannedRemarks(
			@QueryParam(CONTEXT) String context,
			@QueryParam(VERIFICATION) String verification,
			@QueryParam(PLANNED_REMARKS) String plannedRemarks,
			@QueryParam(MODIFIER) String modifier) {
		try {
			mCoinsService.setVerificationAttribute(context, verification,
					plannedRemarks, CoinsFormat.CBIMFS_PLANNED_REMARKS,
					modifier, FieldType.STRING);
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
		return Response
				.serverError()
				.entity("Something went wrong when setting the Planned remarks")
				.build();
	}

	/**
	 * Set the planned date of a <B>Verification</B>
	 * 
	 * @param context
	 *            The context or graph
	 * @param verification
	 *            The identifier of the <B>Verification</B> object
	 * @param plannedDate
	 *            Planned date (xsd:dateTime formatted String)
	 * @param modifier
	 *            URI to the modifier of the object
	 * @return OK if successful
	 */
	@POST
	@Path(PATH_VERIFICATION_PLANNED_DATE)
	@Consumes(MIME_TYPE)
	public Response setVerificationPlannedDate(
			@QueryParam(CONTEXT) String context,
			@QueryParam(VERIFICATION) String verification,
			@QueryParam(PLANNED_DATE) String plannedDate,
			@QueryParam(MODIFIER) String modifier) {
		try {
			mCoinsService.setVerificationAttribute(context, verification,
					plannedDate, CoinsFormat.CBIMFS_PLANNED_VERIFICATION_DATE,
					modifier, FieldType.DATE);
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
				.entity("Something went wrong when setting the Planned date")
				.build();
	}

	/**
	 * Set the planned verification method of a <B>Verification</B>
	 * 
	 * @param context
	 *            The context or graph
	 * @param verification
	 *            The identifier of the <B>Verification</B> object
	 * @param plannedMethod
	 *            Planned verification method (String)
	 * @param modifier
	 *            URI to the modifier of the object
	 * @return OK if successful
	 */
	@POST
	@Path(PATH_VERIFICATION_PLANNED_METHOD)
	@Consumes(MIME_TYPE)
	public Response setVerificationPlannedMethod(
			@QueryParam(CONTEXT) String context,
			@QueryParam(VERIFICATION) String verification,
			@QueryParam(PLANNED_METHOD) String plannedMethod,
			@QueryParam(MODIFIER) String modifier) {
		try {
			mCoinsService.setVerificationAttribute(context, verification,
					plannedMethod,
					CoinsFormat.CBIMFS_PLANNED_VERIFICATION_METHOD, modifier,
					FieldType.STRING);
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
		return Response
				.serverError()
				.entity("Something went wrong when setting the Planned verification method")
				.build();
	}

	/**
	 * Set the planned WorkPackage of a <B>Verification</B>
	 * 
	 * @param context
	 *            The context or graph
	 * @param verification
	 *            The identifier of the <B>Verification</B> object
	 * @param plannedWorkPackage
	 *            Planned WorkPakage (String)
	 * @param modifier
	 *            URI to the modifier of the object
	 * @return OK if successful
	 */
	@POST
	@Path(PATH_VERIFICATION_PLANNED_WORK_PACKAGE)
	@Consumes(MIME_TYPE)
	public Response setVerificationPlannedWorkPackage(
			@QueryParam(CONTEXT) String context,
			@QueryParam(VERIFICATION) String verification,
			@QueryParam(PLANNED_WORKPACKAGE) String plannedWorkPackage,
			@QueryParam(MODIFIER) String modifier) {
		try {
			mCoinsService.setVerificationAttribute(context, verification,
					plannedWorkPackage,
					CoinsFormat.CBIMFS_PLANNED_WORK_PACKAGE, modifier,
					FieldType.STRING);
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
		return Response
				.serverError()
				.entity("Something went wrong when setting the Planned WorkPackage")
				.build();
	}

	/**
	 * Set the Risks of a <B>Verification</B>
	 * 
	 * @param context
	 *            The context or graph
	 * @param verification
	 *            The identifier of the <B>Verification</B> object
	 * @param risks
	 *            Risks (String)
	 * @param modifier
	 *            URI to the modifier of the object
	 * @return OK if successful
	 */
	@POST
	@Path(PATH_VERIFICATION_RISKS)
	@Consumes(MIME_TYPE)
	public Response setVerificationRisks(@QueryParam(CONTEXT) String context,
			@QueryParam(VERIFICATION) String verification,
			@QueryParam(RISKS) String risks,
			@QueryParam(MODIFIER) String modifier) {
		try {
			mCoinsService.setVerificationAttribute(context, verification,
					risks, CoinsFormat.CBIMFS_VERIFICATION_RISKS, modifier,
					FieldType.STRING);
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
		return Response
				.serverError()
				.entity("Something went wrong when setting the verification risks")
				.build();
	}

	/**
	 * Create a new <B>State</B>
	 * 
	 * @param context
	 *            The context or named graph
	 * @param name
	 *            The name of the <B>State</B>
	 * @param userID
	 *            A user defined identifier (for convenience)
	 * @param creator
	 *            URI referring to the user that created this <B>State</B>
	 * @return The id of the created <B>State</B>
	 */
	@POST
	@Path(PATH_STATE)
	@Consumes(MIME_TYPE)
	public Response createState(@QueryParam(CONTEXT) String context,
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
			identifier = mCoinsService.createState(context, name, userID,
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
		return Response
				.serverError()
				.entity("Something went wrong when creating the PhysicalObject")
				.build();
	}

	/**
	 * Get a <B>State</B>
	 * 
	 * @param context
	 *            The context or graph
	 * @param id
	 *            The id of the <B>State</B>
	 * @param output
	 *            The way the output should be formatted
	 *            (json/xml/csv/html/tabs)
	 * @param request
	 * @return the <B>State</B> formatted the way specified by means of the
	 *         output
	 */
	@GET
	@Path(PATH_STATE)
	@Consumes(MIME_TYPE)
	public Response getState(@QueryParam(CONTEXT) String context,
			@QueryParam(ID) String id, @QueryParam(OUTPUT) String output,
			@Context HttpServletRequest request) {
		String query = mCoinsService.getStateQuery(context, id);
		return mSparqlWebService.selectPostForm(query, output, request);
	}

	/**
	 * Delete a <B>State</B>
	 * 
	 * @param context
	 * @param id
	 * @return OK if the <B>State</B> was deleted
	 */
	@DELETE
	@Path(PATH_STATE)
	@Consumes(MIME_TYPE)
	public Response deleteState(@QueryParam(CONTEXT) String context,
			@QueryParam(ID) String id) {
		if (mCoinsService.deleteState(context, id)) {
			return Response.ok().build();
		}
		return Response.serverError().entity("Cannot delete State").build();
	}

	/**
	 * Create a new <B>Verification</B>
	 * 
	 * @param context
	 *            The context or named graph
	 * @param name
	 *            The name of the <B>Verification</B>
	 * @param userID
	 *            A user defined identifier (for convenience)
	 * @param verificationDate
	 *            Verification date (xsd:dateTime formatted String)
	 * @param verificationMethod
	 *            Verification method (String)
	 * @param verificationResult
	 *            Verification result (boolean)
	 * @param creator
	 *            URI referring to the user that created this
	 *            <B>Verification</B>
	 * @return The id of the created <B>State</B>
	 */
	@POST
	@Path(PATH_VERIFICATION)
	@Consumes(MIME_TYPE)
	public Response createVerification(@QueryParam(CONTEXT) String context,
			@QueryParam(NAME) String name, @QueryParam(USER_ID) String userID,
			@QueryParam(VERIFICATION_DATE) String verificationDate,
			@QueryParam(VERIFICATION_METHOD) String verificationMethod,
			@QueryParam(VERIFICATION_RESULT) boolean verificationResult,
			@QueryParam(CREATOR) String creator) {
		if (name == null) {
			return Response.serverError().entity("Name cannot be null").build();
		}
		if (context == null) {
			context = mConfigurationService.getDefaultContext();
		}
		String identifier;
		try {
			identifier = mCoinsService.createVerification(context, name,
					userID, verificationDate, verificationMethod,
					verificationResult, creator);
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
				.entity("Something went wrong when creating the Verification")
				.build();
	}

	/**
	 * Get a <B>Verification</B>
	 * 
	 * @param context
	 *            The context or graph
	 * @param id
	 *            The id of the <B>Verification</B>
	 * @param output
	 *            The way the output should be formatted
	 *            (json/xml/csv/html/tabs)
	 * @param request
	 * @return the <B>State</B> formatted the way specified by means of the
	 *         output
	 */
	@GET
	@Path(PATH_VERIFICATION)
	@Consumes(MIME_TYPE)
	public Response getVerification(@QueryParam(CONTEXT) String context,
			@QueryParam(ID) String id, @QueryParam(OUTPUT) String output,
			@Context HttpServletRequest request) {
		String query = mCoinsService.getVerificationQuery(context, id);
		return mSparqlWebService.selectPostForm(query, output, request);
	}

	/**
	 * Delete a <B>Verification</B>
	 * 
	 * @param context
	 * @param id
	 * @return OK if the <B>Verification</B> was deleted
	 */
	@DELETE
	@Path(PATH_VERIFICATION)
	@Consumes(MIME_TYPE)
	public Response deleteVerification(@QueryParam(CONTEXT) String context,
			@QueryParam(ID) String id) {
		if (mCoinsService.deleteVerification(context, id)) {
			return Response.ok().build();
		}
		return Response.serverError().entity("Cannot delete Verification")
				.build();
	}

	/**
	 * Create a new <B>PhysicalObject</B>
	 * 
	 * @param context
	 *            The context or named graph
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
			identifier = mCoinsService.createPhysicalObject(context, name,
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
			identifier = mCoinsService.createSpace(context, name, layerIndex,
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
			identifier = mCoinsService.createFunction(context, name,
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
			identifier = mCoinsService.createDocument(context, name, userID,
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
			@QueryParam(CONTEXT) String context, @QueryParam(NAME) String name,
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
					name, documentType, documentAliasFilePath, documentUri,
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
			identifier = mCoinsService.createVector(context, name, xCoordinate,
					yCoordinate, zCoordinate, creator);
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
			identifier = mCoinsService.createLocator(context, name,
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
			identifier = mCoinsService
					.createTask(context, name, affects, userID, taskType,
							startDatePlanned, endDatePlanned, creator);
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
					name, layerIndex, userID, creator,
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
	public Response getCoinsDocumentContext(
			@PathParam(CONTEXT) String pContext,
			@PathParam(FILENAME) String pFileName) {
		return getCoinsDocument(pContext + "/" + pFileName);
	}

	/**
	 * Create a new <B>Amount</B>
	 * 
	 * @param context
	 *            The context or graph
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
			identifier = mCoinsService.createAmount(context, name, userID,
					value, cataloguePart, creator);
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
	 * get an <B>Amount</B>
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
			identifier = mCoinsService.createCataloguePart(context, name,
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
	 * Create a new <B>Connection</B>
	 * 
	 * @param context
	 *            The context or graph
	 * @param name
	 *            The name of the <B>Connection</B>
	 * @param userID
	 *            A user defined identifier (for convenience)
	 * @param creator
	 *            URI referring to the user that created this <B>Connection</B>
	 * @return The id of the created <B>Connection</B>
	 */
	@POST
	@Path(PATH_CONNECTION)
	@Consumes(MIME_TYPE)
	public Response createConnection(@QueryParam(CONTEXT) String context,
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
			identifier = mCoinsService.createConnection(context, name, userID,
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
				.entity("Something went wrong when creating the connection")
				.build();
	}

	/**
	 * Delete a <B>Connection</B>
	 * 
	 * @param context
	 * @param id
	 * @return OK if the <B>Connection</B> was deleted
	 */
	@DELETE
	@Path(PATH_CONNECTION)
	@Consumes(MIME_TYPE)
	public Response deleteConnection(@QueryParam(CONTEXT) String context,
			@QueryParam(ID) String id) {
		if (mCoinsService.deleteConnection(context, id)) {
			return Response.ok().build();
		}
		return Response.serverError().entity("Cannot delete Connection")
				.build();
	}

	/**
	 * Get a <B>Connection</B>
	 * 
	 * @param context
	 *            context or graph
	 * @param id
	 *            The id of the <B>Connection</B>
	 * @param output
	 *            The way the output should be formatted
	 *            (json/xml/csv/html/tabs)
	 * @param request
	 * @return the <B>Connection</B> formatted the way specified by means of the
	 *         output
	 */
	@GET
	@Path(PATH_CONNECTION)
	@Consumes(MIME_TYPE)
	public Response getConnection(@QueryParam(CONTEXT) String context,
			@QueryParam(ID) String id, @QueryParam(OUTPUT) String output,
			@Context HttpServletRequest request) {
		String query = mCoinsService.getConnectionQuery(context, id);
		return mSparqlWebService.selectPostForm(query, output, request);
	}

	/**
	 * Create a new <B>Parameter</B>
	 * 
	 * @param context
	 *            The context or graph
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
			identifier = mCoinsService.createParameter(context, name, userID,
					defaultValue, creator);
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
	 * @param parameter
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
			@QueryParam(EXPLICIT_3D_REPRESENTATION) String explicit3DRepresentation,
			@QueryParam(PARAMETER) String parameter,
			@QueryParam(MODIFIER) String modifier) {
		try {
			mCoinsService.setFirstParameter(context, explicit3DRepresentation,
					parameter, modifier);
		} catch (InvalidArgumentException | MalformedQueryException
				| UpdateExecutionException | MarmottaException e) {
			e.printStackTrace();
			return Response
					.serverError()
					.entity("Setting first parameter of <"
							+ explicit3DRepresentation + "> to <" + parameter
							+ "> failed").build();
		}
		return Response.ok().build();
	}

	/**
	 * Link a property value to <B>Performance</B> via cbimfs:propertyValue
	 * 
	 * @param context
	 *            context or graph
	 * @param performance
	 *            Identifier of the <B>Performance</B> object
	 * @param propertyValue
	 *            Identifier of <B>PropertyValue</B>
	 * @param modifier
	 *            Identifier of <B>PersonOrOrganisation</B> that modifies the
	 *            object
	 * @return OK if success
	 */
	@POST
	@Path(PATH_LINK_PROPERTY_VALUE)
	@Consumes(MIME_TYPE)
	public Response linkPropertyValue(@QueryParam(CONTEXT) String context,
			@QueryParam(PERFORMANCE) String performance,
			@QueryParam(PROPERTY_VALUE) String propertyValue,
			@QueryParam(MODIFIER) String modifier) {
		try {
			mCoinsService.linkPropertyValue(context, performance,
					propertyValue, modifier);
		} catch (InvalidArgumentException | MalformedQueryException
				| UpdateExecutionException | MarmottaException e) {
			e.printStackTrace();
			return Response
					.serverError()
					.entity("Setting property value of <" + performance
							+ "> to <" + propertyValue + "> failed").build();
		}
		return Response.ok().build();
	}

	/**
	 * Link <B>Performance</B> to <B>State</B> or <B>FunctionFulfiller</B> via
	 * cbim:performanceOf
	 * 
	 * @param context
	 *            context or graph
	 * @param performance
	 *            Identifier of the <B>Performance</B> object
	 * @param performanceOf
	 *            Identifier of <B>State</B> or <B>FunctionFulfiller</B>
	 * @param modifier
	 *            Identifier of <B>PersonOrOrganisation</B> that modifies the
	 *            object
	 * @return OK if success
	 */
	@POST
	@Path(PATH_LINK_PERFORMANCEOF)
	@Consumes(MIME_TYPE)
	public Response linkPerformanceOf(@QueryParam(CONTEXT) String context,
			@QueryParam(PERFORMANCE) String performance,
			@QueryParam(PERFORMANCE_OF) String performanceOf,
			@QueryParam(MODIFIER) String modifier) {
		try {
			mCoinsService.linkPerformanceOf(context, performance,
					performanceOf, modifier);
		} catch (InvalidArgumentException | MalformedQueryException
				| UpdateExecutionException | MarmottaException e) {
			e.printStackTrace();
			return Response
					.serverError()
					.entity("Setting performanceOf <" + performance + "> to <"
							+ performanceOf + "> failed").build();
		}
		return Response.ok().build();
	}

	/**
	 * Link <B>State</B> to <B>Performance</B> via cbim:performance
	 * 
	 * @param context
	 *            context or graph
	 * @param state
	 *            Identifier of <B>State</B>
	 * @param performance
	 *            Identifier of the <B>Performance</B> object
	 * @param modifier
	 *            Identifier of <B>PersonOrOrganisation</B> that modifies the
	 *            object
	 * @return OK if success
	 */
	@POST
	@Path(PATH_LINK_STATE_PERFORMANCE)
	@Consumes(MIME_TYPE)
	public Response linkStatePerformance(@QueryParam(CONTEXT) String context,
			@QueryParam(STATE) String state,
			@QueryParam(PERFORMANCE) String performance,
			@QueryParam(MODIFIER) String modifier) {
		return linkPerformance(context, state, performance, modifier);
	}

	/**
	 * Link <B>FunctionFulfiller</B> to <B>Performance</B> via cbim:performance
	 * 
	 * @param context
	 *            context or graph
	 * @param functionFulfiller
	 *            Identifier of <B>FunctionFulfiller</B>
	 * @param performance
	 *            Identifier of the <B>Performance</B> object
	 * @param modifier
	 *            Identifier of <B>PersonOrOrganisation</B> that modifies the
	 *            object
	 * @return OK if success
	 */
	@POST
	@Path(PATH_LINK_FUNCTION_FULFILLER_PERFORMANCE)
	@Consumes(MIME_TYPE)
	public Response linkFunctionFulfillerPerformance(
			@QueryParam(CONTEXT) String context,
			@QueryParam(FUNCTION_FULFILLER) String functionFulfiller,
			@QueryParam(PERFORMANCE) String performance,
			@QueryParam(MODIFIER) String modifier) {
		return linkPerformance(context, functionFulfiller, performance,
				modifier);
	}

	private Response linkPerformance(String context, String object,
			String performance, String modifier) {
		try {
			mCoinsService.linkPerformance(context, object, performance,
					modifier);
		} catch (InvalidArgumentException | MalformedQueryException
				| UpdateExecutionException | MarmottaException e) {
			e.printStackTrace();
			return Response
					.serverError()
					.entity("Setting performance of <" + object + "> to <"
							+ performance + "> failed").build();
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
			@QueryParam(PARAMETER) String parameter,
			@QueryParam(NEXT_PARAMETER) String nextParameter,
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
	 * Link an Explicit3DRepresentation to a <B>FunctionFulfiller</B>
	 * 
	 * @param context
	 *            Context / Graph
	 * @param functionFulfiller
	 *            id of <B>FunctionFulfiller</B>
	 * @param shape
	 *            id of Explicit3DRepresentation
	 * @param modifier
	 * @return OK if success
	 */
	@POST
	@Path(PATH_LINK_SHAPE)
	@Consumes(MIME_TYPE)
	public Response linkShape(@QueryParam(CONTEXT) String context,
			@QueryParam(FUNCTION_FULFILLER) String functionFulfiller,
			@QueryParam(SHAPE) String shape,
			@QueryParam(MODIFIER) String modifier) {
		try {
			mCoinsService.setShape(context, functionFulfiller, shape, modifier);
		} catch (InvalidArgumentException | MalformedQueryException
				| UpdateExecutionException | MarmottaException e) {
			e.printStackTrace();
			return Response
					.serverError()
					.entity("Setting physical parent of <" + functionFulfiller
							+ "> to <" + shape + "> failed").build();
		}
		return Response.ok().build();
	}

	/**
	 * Set the current <B>State</B> of a <B>FunctionFulfiller</B>
	 * 
	 * @param context
	 *            context or graph
	 * @param state
	 *            Identifier of the <B>State</B> object
	 * @param functionFulfiller
	 *            Identifier of <B>FunctionFulfiller</B>
	 * @param modifier
	 *            Identifier of <B>PersonOrOrganisation</B> that modifies the
	 *            object
	 * @return OK if success
	 */
	@POST
	@Path(PATH_LINK_CURRENT_STATE)
	@Consumes(MIME_TYPE)
	public Response linkCurrentState(@QueryParam(CONTEXT) String context,
			@QueryParam(STATE) String state,
			@QueryParam(FUNCTION_FULFILLER) String functionFulfiller,
			@QueryParam(MODIFIER) String modifier) {
		try {
			mCoinsService.setCurrentState(context, state, functionFulfiller,
					modifier);
		} catch (InvalidArgumentException | MalformedQueryException
				| UpdateExecutionException | MarmottaException e) {
			e.printStackTrace();
			return Response
					.serverError()
					.entity("Setting current state of <" + functionFulfiller
							+ "> to <" + state + "> failed").build();
		}
		return Response.ok().build();
	}

	/**
	 * Set the previous <B>State</B> of a <B>State</B>
	 * 
	 * @param context
	 *            context or graph
	 * @param state
	 *            Identifier of the <B>State</B> object
	 * @param previousState
	 *            Identifier of <B>PreviousState</B>
	 * @param modifier
	 *            Identifier of <B>PersonOrOrganisation</B> that modifies the
	 *            object
	 * @return OK if success
	 */
	@POST
	@Path(PATH_LINK_PREVIOUS_STATE)
	@Consumes(MIME_TYPE)
	public Response linkPreviousState(@QueryParam(CONTEXT) String context,
			@QueryParam(STATE) String state,
			@QueryParam(PREVIOUS_STATE) String previousState,
			@QueryParam(MODIFIER) String modifier) {
		try {
			mCoinsService.setPreviousState(context, state, previousState,
					modifier);
		} catch (InvalidArgumentException | MalformedQueryException
				| UpdateExecutionException | MarmottaException e) {
			e.printStackTrace();
			return Response
					.serverError()
					.entity("Setting previsous state of <" + state + "> to <"
							+ previousState + "> failed").build();
		}
		return Response.ok().build();
	}

	/**
	 * Set the stateOf property of a <B>State</B> to a <B>FunctionFulfiller</B>
	 * 
	 * @param context
	 *            context or graph
	 * @param state
	 *            Identifier of the <B>State</B> object
	 * @param functionFulfiller
	 *            Identifier of <B>FunctionFulfiller</B>
	 * @param modifier
	 *            Identifier of <B>PersonOrOrganisation</B> that modifies the
	 *            object
	 * @return OK if success
	 */
	@POST
	@Path(PATH_LINK_STATE_OF)
	@Consumes(MIME_TYPE)
	public Response linkStateOf(@QueryParam(CONTEXT) String context,
			@QueryParam(STATE) String state,
			@QueryParam(FUNCTION_FULFILLER) String functionFulfiller,
			@QueryParam(MODIFIER) String modifier) {
		try {
			mCoinsService.setStateOf(context, state, functionFulfiller,
					modifier);
		} catch (InvalidArgumentException | MalformedQueryException
				| UpdateExecutionException | MarmottaException e) {
			e.printStackTrace();
			return Response
					.serverError()
					.entity("Setting state of <" + state + "> to <"
							+ functionFulfiller + "> failed").build();
		}
		return Response.ok().build();
	}

	/**
	 * Situate a <B>PhysicalObject</B> in a <B>Space</B> (cbim:isSituatedIn)
	 * 
	 * @param context
	 *            Context or Graph
	 * @param physicalObject
	 *            id of <B>PhysicalObject</B>
	 * @param space
	 *            id of <B>Space</B>
	 * @param modifier
	 *            who did this modification
	 * @return OK if success
	 */
	@POST
	@Path(PATH_LINK_IS_SITUATED_IN)
	@Consumes(MIME_TYPE)
	public Response linkIsSituatedIn(@QueryParam(CONTEXT) String context,
			@QueryParam(PHYSICAL_OBJECT) String physicalObject,
			@QueryParam(SPACE) String space,
			@QueryParam(MODIFIER) String modifier) {
		try {
			mCoinsService.linkIsSituatedIn(context, physicalObject, space,
					modifier);
		} catch (InvalidArgumentException | MalformedQueryException
				| UpdateExecutionException | MarmottaException e) {
			e.printStackTrace();
			return Response.serverError()
					.entity("Linking NonFunctionalRequirements failed").build();
		}
		return Response.ok().build();
	}

	/**
	 * Situate a list of <B>PhysicalObject</B>s in a <B>Space</B> (cbim:situates)
	 * 
	 * @param context
	 *            Context or Graph
	 * @param physicalObject
	 *            id of <B>PhysicalObject</B>
	 * @param space
	 *            id of <B>Space</B>
	 * @param modifier
	 *            who did this modification
	 * @return OK if success
	 */
	@POST
	@Path(PATH_LINK_SITUATES)
	@Consumes(MIME_TYPE)
	public Response linkSituates(@QueryParam(CONTEXT) String context,
			@QueryParam(PHYSICAL_OBJECT) String[] physicalObject,
			@QueryParam(SPACE) String space,
			@QueryParam(MODIFIER) String modifier) {
		try {
			mCoinsService.linkSituates(context, physicalObject, space,
					modifier);
		} catch (InvalidArgumentException | MalformedQueryException
				| UpdateExecutionException | MarmottaException e) {
			e.printStackTrace();
			return Response.serverError()
					.entity("Linking NonFunctionalRequirements failed").build();
		}
		return Response.ok().build();
	}
	
	/**
	 * Add <B>NonFunctionalRequirement</B>(s) to <B>FunctionFulfillers</B>.
	 * Leaves previously linked NonFunctionalRequirements untouched.
	 * 
	 * @param context
	 *            Context or Graph
	 * @param functionFulfiller
	 *            id of <B>FunctionFulfiller</B>
	 * @param nonFunctionalRequirement
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
			@QueryParam(FUNCTION_FULFILLER) String functionFulfiller,
			@QueryParam(NON_FUNCTIONAL_REQUIREMENT) String[] nonFunctionalRequirement,
			@QueryParam(MODIFIER) String modifier) {
		try {
			mCoinsService.linkNonFunctionalRequirement(context,
					functionFulfiller, nonFunctionalRequirement, modifier);
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
	 * @param physicalObject
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
			@QueryParam(PHYSICAL_OBJECT) String physicalObject,
			@QueryParam(DOCUMENT) String[] document,
			@QueryParam(MODIFIER) String modifier) {
		try {
			mCoinsService.linkDocument(context, physicalObject, document,
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
	 * Link <B>Requirement</B> to <B>Verification</B>
	 * (cbim:verificationRequirement)
	 * 
	 * @param context
	 *            Context or Graph
	 * @param requirement
	 *            <B>Requirement</B> Id
	 * @param verification
	 *            Identifier of <B>Verification</B>
	 * @param modifier
	 * @return OK if success
	 */
	@POST
	@Path(PATH_LINK_VERIFICATION_REQUIREMENT)
	@Consumes(MIME_TYPE)
	public Response linkVerificationRequirement(
			@QueryParam(CONTEXT) String context,
			@QueryParam(VERIFICATION) String verification,
			@QueryParam(REQUIREMENT) String requirement,
			@QueryParam(MODIFIER) String modifier) {
		try {
			mCoinsService.setVerificationAttribute(context, verification,
					requirement, CoinsFormat.CBIM_VERIFICATION_REQUIREMENT,
					modifier, FieldType.RESOURCE);
		} catch (InvalidArgumentException | MalformedQueryException
				| UpdateExecutionException | MarmottaException e) {
			e.printStackTrace();
			return Response.serverError()
					.entity("Linking Requirement to Verification failed")
					.build();
		}
		return Response.ok().build();
	}

	/**
	 * Link <B>NonFunctionalRequirement</B> to <B>Verification</B>
	 * (cbimfs:verificationRequirement)
	 * 
	 * @param context
	 *            Context or Graph
	 * @param requirement
	 *            <B>NonFunctionalRequirement</B> Id
	 * @param verification
	 *            Identifier of <B>Verification</B>
	 * @param modifier
	 * @return OK if success
	 */
	@POST
	@Path(PATH_LINK_VERIFICATION_NON_FUNCTIONAL_REQUIREMENT)
	@Consumes(MIME_TYPE)
	public Response linkVerificationNonFunctionalRequirement(
			@QueryParam(CONTEXT) String context,
			@QueryParam(VERIFICATION) String verification,
			@QueryParam(REQUIREMENT) String requirement,
			@QueryParam(MODIFIER) String modifier) {
		try {
			mCoinsService.setVerificationAttribute(context, verification,
					requirement, CoinsFormat.CBIMFS_VERIFICATION_REQUIREMENT,
					modifier, FieldType.RESOURCE);
		} catch (InvalidArgumentException | MalformedQueryException
				| UpdateExecutionException | MarmottaException e) {
			e.printStackTrace();
			return Response.serverError()
					.entity("Linking Requirement to Verification failed")
					.build();
		}
		return Response.ok().build();
	}

	/**
	 * Link performer (<B>PersonOrOrganisation</B>) to <B>Verification</B>
	 * (cbim:verificationPerformer)
	 * 
	 * @param context
	 *            Context or Graph
	 * @param performer
	 *            <B>PersonOrOrganisation</B> Id
	 * @param verification
	 *            Identifier of <B>Verification</B>
	 * @param modifier
	 * @return OK if success
	 */
	@POST
	@Path(PATH_LINK_VERIFICATION_PERFORMER)
	@Consumes(MIME_TYPE)
	public Response linkVerificationPerformer(
			@QueryParam(CONTEXT) String context,
			@QueryParam(VERIFICATION) String verification,
			@QueryParam(PERFORMER) String performer,
			@QueryParam(MODIFIER) String modifier) {
		try {
			mCoinsService.setVerificationAttribute(context, verification,
					performer, CoinsFormat.CBIM_VERIFICATION_PERFORMER,
					modifier, FieldType.RESOURCE);
		} catch (InvalidArgumentException | MalformedQueryException
				| UpdateExecutionException | MarmottaException e) {
			e.printStackTrace();
			return Response.serverError()
					.entity("Linking Performer to Verification failed").build();
		}
		return Response.ok().build();
	}

	/**
	 * Link planned performer (<B>PersonOrOrganisation</B>) to
	 * <B>Verification</B> (cbimfs:verificationPlannedPerformer)
	 * 
	 * @param context
	 *            Context or Graph
	 * @param performer
	 *            <B>PersonOrOrganisation</B> Id
	 * @param verification
	 *            Identifier of <B>Verification</B>
	 * @param modifier
	 * @return OK if success
	 */
	@POST
	@Path(PATH_LINK_VERIFICATION_PLANNED_PERFORMER)
	@Consumes(MIME_TYPE)
	public Response linkVerificationPlannedPerformer(
			@QueryParam(CONTEXT) String context,
			@QueryParam(VERIFICATION) String verification,
			@QueryParam(PERFORMER) String performer,
			@QueryParam(MODIFIER) String modifier) {
		try {
			mCoinsService.setVerificationAttribute(context, verification,
					performer,
					CoinsFormat.CBIMFS_VERIFICATION_PLANNED_PERFORMER,
					modifier, FieldType.RESOURCE);
		} catch (InvalidArgumentException | MalformedQueryException
				| UpdateExecutionException | MarmottaException e) {
			e.printStackTrace();
			return Response.serverError()
					.entity("Linking planned Performer to Verification failed")
					.build();
		}
		return Response.ok().build();
	}

	/**
	 * Link authorizer (<B>PersonOrOrganisation</B>) to <B>Verification</B>
	 * (cbimfs:verificationAuthorizedBy)
	 * 
	 * @param context
	 *            Context or Graph
	 * @param authorizer
	 *            <B>PersonOrOrganisation</B> Id
	 * @param verification
	 *            Identifier of <B>Verification</B>
	 * @param modifier
	 * @return OK if success
	 */
	@POST
	@Path(PATH_LINK_VERIFICATION_AUTHORIZED_BY)
	@Consumes(MIME_TYPE)
	public Response linkVerificationAuthorizedBy(
			@QueryParam(CONTEXT) String context,
			@QueryParam(VERIFICATION) String verification,
			@QueryParam(AUTHORIZER) String authorizer,
			@QueryParam(MODIFIER) String modifier) {
		try {
			mCoinsService.setVerificationAttribute(context, verification,
					authorizer, CoinsFormat.CBIMFS_VERIFICATION_AUTHORIZED_BY,
					modifier, FieldType.RESOURCE);
		} catch (InvalidArgumentException | MalformedQueryException
				| UpdateExecutionException | MarmottaException e) {
			e.printStackTrace();
			return Response.serverError()
					.entity("Linking authorizer to Verification failed")
					.build();
		}
		return Response.ok().build();
	}

	/**
	 * Link <B>FunctionFulfiller</B> to <B>Verification</B>
	 * (cbim:verificationFunctionFulfiller)
	 * 
	 * @param context
	 *            Context or Graph
	 * @param functionFulfiller
	 *            <B>FunctionFulfiller</B> Id
	 * @param verification
	 *            Identifier of <B>Verification</B>
	 * @param modifier
	 * @return OK if success
	 */
	@POST
	@Path(PATH_LINK_VERIFICATION_FUNCTION_FULFILLER)
	@Consumes(MIME_TYPE)
	public Response linkVerificationFunctionFulfiller(
			@QueryParam(CONTEXT) String context,
			@QueryParam(VERIFICATION) String verification,
			@QueryParam(FUNCTION_FULFILLER) String functionFulfiller,
			@QueryParam(MODIFIER) String modifier) {
		try {
			mCoinsService.setVerificationAttribute(context, verification,
					functionFulfiller,
					CoinsFormat.CBIM_VERIFICATION_FUNCTION_FULFILLER, modifier,
					FieldType.RESOURCE);
		} catch (InvalidArgumentException | MalformedQueryException
				| UpdateExecutionException | MarmottaException e) {
			e.printStackTrace();
			return Response.serverError()
					.entity("Linking FunctionFulfiller to Verification failed")
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
	 * @param physicalObject
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
			@QueryParam(PHYSICAL_OBJECT) String physicalObject,
			@QueryParam(FULFILLS) String[] fulfills,
			@QueryParam(MODIFIER) String modifier) {
		try {
			mCoinsService.linkPhysicalObjectFulfills(context, physicalObject,
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
	 * @param functionFulfiller
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
			@QueryParam(FUNCTION_FULFILLER) String functionFulfiller,
			@QueryParam(IS_AFFECTED_BY) String isAffectedBy,
			@QueryParam(MODIFIER) String modifier) {
		try {
			mCoinsService.linkIsAffectedBy(context, functionFulfiller,
					isAffectedBy, modifier);
		} catch (InvalidArgumentException | MalformedQueryException
				| UpdateExecutionException | MarmottaException e) {
			e.printStackTrace();
			return Response.serverError()
					.entity("Linking function fulfiller failed").build();
		}
		return Response.ok().build();
	}

	private Response linkLocator(String pContext, String pObject,
			String pLocator, String pModifier) {
		try {
			mCoinsService.linkLocator(pContext, pObject, pLocator, pModifier);
		} catch (InvalidArgumentException | MalformedQueryException
				| UpdateExecutionException | MarmottaException e) {
			e.printStackTrace();
			return Response.serverError().entity("Linking locator failed")
					.build();
		}
		return Response.ok().build();
	}

	/**
	 * Link a locator to an <B>Amount</B> by the locator property
	 * 
	 * @param context
	 *            Context of Graph
	 * @param amount
	 *            <B>Amount</B> id
	 * @param locator
	 *            id of <B>Locator<B> of the <B>Amount</B>
	 * @param modifier
	 * @return OK if success
	 */
	@POST
	@Path(PATH_AMOUNT_LINK_LOCATOR)
	@Consumes(MIME_TYPE)
	public Response linkAmountLocator(@QueryParam(CONTEXT) String context,
			@QueryParam(AMOUNT) String amount,
			@QueryParam(LOCATOR) String locator,
			@QueryParam(MODIFIER) String modifier) {
		return linkLocator(context, amount, locator, modifier);
	}

	/**
	 * Link a locator to a <B>FunctionFulfiller</B> by the locator property
	 * 
	 * @param context
	 *            Context of Graph
	 * @param functionFulfiller
	 *            <B>FunctionFulfiller</B> id
	 * @param locator
	 *            id of <B>Locator<B> of the <B>FunctionFulfiller</B>
	 * @param modifier
	 * @return OK if success
	 */
	@POST
	@Path(PATH_FUNCTIONFULFILLER_LINK_LOCATOR)
	@Consumes(MIME_TYPE)
	public Response linkFunctionFulfillerLocator(
			@QueryParam(CONTEXT) String context,
			@QueryParam(FUNCTION_FULFILLER) String functionFulfiller,
			@QueryParam(LOCATOR) String locator,
			@QueryParam(MODIFIER) String modifier) {
		return linkLocator(context, functionFulfiller, locator, modifier);
	}

	/**
	 * Link a locator to a <B>Terminal</B> by the locator property
	 * 
	 * @param context
	 *            Context of Graph
	 * @param object
	 *            <B>Terminal</B> id
	 * @param locator
	 *            id of <B>Locator<B> of the <B>Terminal</B>
	 * @param modifier
	 * @return OK if success
	 */
	@POST
	@Path(PATH_TERMINAL_LINK_LOCATOR)
	@Consumes(MIME_TYPE)
	public Response linkTerminalLocator(@QueryParam(CONTEXT) String context,
			@QueryParam(TERMINAL) String object,
			@QueryParam(LOCATOR) String locator,
			@QueryParam(MODIFIER) String modifier) {
		return linkLocator(context, object, locator, modifier);
	}

	/**
	 * Link a minimum bounding box (<B>Vector</B>) to a <B>Locator</B>
	 * 
	 * @param context
	 *            Context of Graph
	 * @param locator
	 *            id of the <B>Locator<B>
	 * @param minBoundingBox
	 *            if of the <B>Vector</B> representing the minimum bounding box
	 * @param modifier
	 * @return OK if success
	 */
	@POST
	@Path(PATH_LINK_MIN_BOUNDING_BOX)
	@Consumes(MIME_TYPE)
	public Response linkMinBoundingBox(@QueryParam(CONTEXT) String context,
			@QueryParam(LOCATOR) String locator,
			@QueryParam(MIN_BOUNDING_BOX) String minBoundingBox,
			@QueryParam(MODIFIER) String modifier) {
		try {
			mCoinsService.linkBoundingBox(context,
					CoinsFormat.CBIM_MIN_BOUNDING_BOX, minBoundingBox, locator,
					modifier);
		} catch (InvalidArgumentException | MalformedQueryException
				| UpdateExecutionException | MarmottaException e) {
			e.printStackTrace();
			return Response.serverError()
					.entity("Linking min bounding box failed").build();
		}
		return Response.ok().build();
	}

	/**
	 * Link a female <B>Terminal</B> to a <B>Connection</B>
	 * 
	 * @param context
	 *            Context of Graph
	 * @param connection
	 *            id of the <B>Connection<B>
	 * @param femaleTerminal
	 *            if of the female <B>Terminal</B>
	 * @param modifier
	 * @return OK if success
	 */
	@POST
	@Path(PATH_LINK_FEMALE_TERMINAL)
	@Consumes(MIME_TYPE)
	public Response linkFemaleTerminal(@QueryParam(CONTEXT) String context,
			@QueryParam(CONNECTION) String connection,
			@QueryParam(FEMALE_TERMINAL) String femaleTerminal,
			@QueryParam(MODIFIER) String modifier) {
		try {
			mCoinsService.linkTerminal(context,
					CoinsFormat.CBIM_FEMALE_TERMINAL, femaleTerminal,
					connection, modifier);
		} catch (InvalidArgumentException | MalformedQueryException
				| UpdateExecutionException | MarmottaException e) {
			e.printStackTrace();
			return Response.serverError()
					.entity("Linking female terminal failed").build();
		}
		return Response.ok().build();
	}

	/**
	 * Link a male <B>Terminal</B> to a <B>Connection</B>
	 * 
	 * @param context
	 *            Context of Graph
	 * @param connection
	 *            id of the <B>Connection<B>
	 * @param maleTerminal
	 *            if of the male <B>Terminal</B>
	 * @param modifier
	 * @return OK if success
	 */
	@POST
	@Path(PATH_LINK_MALE_TERMINAL)
	@Consumes(MIME_TYPE)
	public Response linkMaleTerminal(@QueryParam(CONTEXT) String context,
			@QueryParam(CONNECTION) String connection,
			@QueryParam(MALE_TERMINAL) String maleTerminal,
			@QueryParam(MODIFIER) String modifier) {
		try {
			mCoinsService.linkTerminal(context, CoinsFormat.CBIM_MALE_TERMINAL,
					maleTerminal, connection, modifier);
		} catch (InvalidArgumentException | MalformedQueryException
				| UpdateExecutionException | MarmottaException e) {
			e.printStackTrace();
			return Response.serverError()
					.entity("Linking male terminal failed").build();
		}
		return Response.ok().build();
	}

	/**
	 * Link a maximum bounding box (<B>Vector</B>) to a <B>Locator</B>
	 * 
	 * @param context
	 *            Context of Graph
	 * @param locator
	 *            id of the <B>Locator<B>
	 * @param maxBoundingBox
	 *            if of the <B>Vector</B> representing the minimum bounding box
	 * @param modifier
	 * @return OK if success
	 */
	@POST
	@Path(PATH_LINK_MAX_BOUNDING_BOX)
	@Consumes(MIME_TYPE)
	public Response linkMaxBoundingBox(@QueryParam(CONTEXT) String context,
			@QueryParam(LOCATOR) String locator,
			@QueryParam(MAX_BOUNDING_BOX) String maxBoundingBox,
			@QueryParam(MODIFIER) String modifier) {
		try {
			mCoinsService.linkBoundingBox(context,
					CoinsFormat.CBIM_MAX_BOUNDING_BOX, maxBoundingBox, locator,
					modifier);
		} catch (InvalidArgumentException | MalformedQueryException
				| UpdateExecutionException | MarmottaException e) {
			e.printStackTrace();
			return Response.serverError()
					.entity("Linking max bounding box failed").build();
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
			@QueryParam(ASPECT) String aspect) {
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
	 * Create a new <B>Performance</B>
	 * 
	 * @param context
	 *            The context or named graph
	 * @param name
	 *            The name of the <B>Performance</B>
	 * @param userID
	 *            A user defined identifier (for convenience)
	 * @param creator
	 *            Identifier of the <B>PersonOrOrganisation</B> that created the
	 *            terminal
	 * @return The id of the created <B>Performance</B>
	 */
	@POST
	@Path(PATH_PERFORMANCE)
	@Consumes(MIME_TYPE)
	public Response createPerformance(@QueryParam(CONTEXT) String context,
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
			identifier = mCoinsService.createPerformance(context, name, userID,
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
				.entity("Something went wrong when creating the Performance")
				.build();
	}

	/**
	 * Get a <B>Performance</B>
	 * 
	 * @param context
	 *            The context or graph
	 * @param id
	 *            The id of the <B>Performance</B>
	 * @param output
	 *            The way the output should be formatted
	 *            (json/xml/csv/html/tabs)
	 * @param request
	 * @return the <B>Performance</B> formatted the way specified by means of
	 *         the output
	 */
	@GET
	@Path(PATH_PERFORMANCE)
	@Consumes(MIME_TYPE)
	public Response getPerformance(@QueryParam(CONTEXT) String context,
			@QueryParam(ID) String id, @QueryParam(OUTPUT) String output,
			@Context HttpServletRequest request) {
		String query = mCoinsService.getPerformanceQuery(context, id);
		return mSparqlWebService.selectPostForm(query, output, request);
	}

	/**
	 * Delete a <B>Performance</B>
	 * 
	 * @param context
	 * @param id
	 * @return OK if the <B>Terminal</B> was deleted
	 */
	@DELETE
	@Path(PATH_PERFORMANCE)
	@Consumes(MIME_TYPE)
	public Response deletePerformance(@QueryParam(CONTEXT) String context,
			@QueryParam(ID) String id) {
		if (mCoinsService.deletePerformance(context, id)) {
			return Response.ok().build();
		}
		return Response.serverError().entity("Cannot delete Performance")
				.build();
	}

	/**
	 * Create a new <B>Terminal</B>
	 * 
	 * @param context
	 *            The context or named graph
	 * @param locator
	 *            Identifier of the locator
	 * @param name
	 *            The name of the <B>Terminal</B>
	 * @param userID
	 *            A user defined identifier (for convenience)
	 * @param layerIndex
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
			@QueryParam(NAME) String name, @QueryParam(USER_ID) String userID,
			@QueryParam(LOCATOR) String locator,
			@QueryParam(LAYER_INDEX) int layerIndex,
			@QueryParam(CREATOR) String creator) {
		if (name == null) {
			return Response.serverError().entity("Name cannot be null").build();
		}
		if (context == null) {
			context = mConfigurationService.getDefaultContext();
		}
		String identifier;
		try {
			identifier = mCoinsService.createTerminal(context, name, userID,
					locator, layerIndex, creator);
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
	 * Create a new <B>PropertyType</B>
	 * 
	 * @param context
	 *            The context or named graph
	 * @param name
	 *            The name of the <B>PropertyType</B>
	 * @param userID
	 *            A user defined identifier (for convenience)
	 * @param unit
	 *            The unit (String) of this property type
	 * @param valueDomain
	 *            The valueDomain (cbim:XsdBoolean, cbim:XsdFloat,
	 *            cbim:XsdString, cbim:XsdInt, cbimfs:CbimCataloguePart,
	 *            cbimotl:XsdDateTime, cbimotl:CbimParameter)
	 * @param creator
	 *            URI referring to the user that created this
	 *            <B>PropertyType</B>
	 * @return The id of the created <B>PropertyType</B>
	 */
	@POST
	@Path(PATH_PROPERTY_TYPE)
	@Consumes(MIME_TYPE)
	public Response createPropertyType(@QueryParam(CONTEXT) String context,
			@QueryParam(NAME) String name, @QueryParam(USER_ID) String userID,
			@QueryParam(UNIT) String unit,
			@QueryParam(VALUE_DOMAIN) String valueDomain,
			@QueryParam(CREATOR) String creator) {
		if (name == null) {
			return Response.serverError().entity("Name cannot be null").build();
		}
		if (context == null) {
			context = mConfigurationService.getDefaultContext();
		}
		String identifier;
		try {
			identifier = mCoinsService.createPropertyType(context, name,
					userID, unit, valueDomain, creator);
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
				.entity("Something went wrong when creating the PropertyType")
				.build();
	}

	/**
	 * Get a <B>PropertyType</B>
	 * 
	 * @param context
	 *            The context or graph
	 * @param id
	 *            The id of the <B>PropertyType</B>
	 * @param output
	 *            The way the output should be formatted
	 *            (json/xml/csv/html/tabs)
	 * @param request
	 * @return the <B>PropertyType</B> formatted the way specified by means of
	 *         the output
	 */
	@GET
	@Path(PATH_PROPERTY_TYPE)
	@Consumes(MIME_TYPE)
	public Response getPropertyType(@QueryParam(CONTEXT) String context,
			@QueryParam(ID) String id, @QueryParam(OUTPUT) String output,
			@Context HttpServletRequest request) {
		String query = mCoinsService.getPropertyTypeQuery(context, id);
		return mSparqlWebService.selectPostForm(query, output, request);
	}

	/**
	 * Delete a <B>PropertyType</B>
	 * 
	 * @param context
	 * @param id
	 * @return OK if the <B>PropertyType</B> was deleted
	 */
	@DELETE
	@Path(PATH_PROPERTY_TYPE)
	@Consumes(MIME_TYPE)
	public Response deletePropertyType(@QueryParam(CONTEXT) String context,
			@QueryParam(ID) String id) {
		if (mCoinsService.deletePropertyType(context, id)) {
			return Response.ok().build();
		}
		return Response.serverError().entity("Cannot delete PropertyType")
				.build();
	}

	/**
	 * Create a new <B>PropertyValue</B>
	 * 
	 * @param context
	 *            The context or named graph
	 * @param name
	 *            The name of the <B>PropertyValue</B>
	 * @param userID
	 *            A user defined identifier (for convenience)
	 * @param propertyType
	 *            ID referring to the <B>PropertyType</B>
	 * @param value
	 *            Value of the property
	 * @param creator
	 *            URI referring to the user that created this
	 *            <B>PropertyType</B>
	 * @return The id of the created <B>PropertyType</B>
	 */
	@POST
	@Path(PATH_PROPERTY_VALUE)
	@Consumes(MIME_TYPE)
	public Response createPropertyValue(@QueryParam(CONTEXT) String context,
			@QueryParam(NAME) String name, @QueryParam(USER_ID) String userID,
			@QueryParam(PROPERTY_TYPE) String propertyType,
			@QueryParam(VALUE) String value, @QueryParam(CREATOR) String creator) {
		if (name == null) {
			return Response.serverError().entity("Name cannot be null").build();
		}
		if (context == null) {
			context = mConfigurationService.getDefaultContext();
		}
		String identifier;
		try {
			identifier = mCoinsService.createPropertyValue(context, name,
					userID, propertyType, value, creator);
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
				.entity("Something went wrong when creating the PropertyValue")
				.build();
	}

	/**
	 * Get a <B>PropertyValue</B>
	 * 
	 * @param context
	 *            The context or graph
	 * @param id
	 *            The id of the <B>PropertyValue</B>
	 * @param output
	 *            The way the output should be formatted
	 *            (json/xml/csv/html/tabs)
	 * @param request
	 * @return the <B>PropertyValue</B> formatted the way specified by means of
	 *         the output
	 */
	@GET
	@Path(PATH_PROPERTY_VALUE)
	@Consumes(MIME_TYPE)
	public Response getPropertyValue(@QueryParam(CONTEXT) String context,
			@QueryParam(ID) String id, @QueryParam(OUTPUT) String output,
			@Context HttpServletRequest request) {
		String query = mCoinsService.getPropertyValueQuery(context, id);
		return mSparqlWebService.selectPostForm(query, output, request);
	}

	/**
	 * Delete a <B>PropertyValue</B>
	 * 
	 * @param context
	 * @param id
	 * @return OK if the <B>PropertyValue</B> was deleted
	 */
	@DELETE
	@Path(PATH_PROPERTY_VALUE)
	@Consumes(MIME_TYPE)
	public Response deletePropertyValue(@QueryParam(CONTEXT) String context,
			@QueryParam(ID) String id) {
		if (mCoinsService.deletePropertyValue(context, id)) {
			return Response.ok().build();
		}
		return Response.serverError().entity("Cannot delete PropertyValue")
				.build();
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