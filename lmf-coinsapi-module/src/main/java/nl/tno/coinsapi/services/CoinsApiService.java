package nl.tno.coinsapi.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.inject.Inject;

import nl.tno.coinsapi.tools.CoinsValidator;
import nl.tno.coinsapi.tools.CoinsValidatorFactory;
import nl.tno.coinsapi.tools.QueryBuilder;
import nl.tno.coinsapi.tools.QueryBuilder.InsertQueryBuilder;
import nl.tno.coinsapi.tools.QueryBuilder.UpdateQueryBuilder;

import org.apache.marmotta.platform.core.api.config.ConfigurationService;
import org.apache.marmotta.platform.core.exception.InvalidArgumentException;
import org.apache.marmotta.platform.core.exception.MarmottaException;
import org.apache.marmotta.platform.sparql.api.sparql.SparqlService;
import org.openrdf.model.Value;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.UpdateExecutionException;

/**
 * Implementation of ICoinsApiService
 */
public class CoinsApiService implements ICoinsApiService {

	/**
	 * a
	 */
	public static final String A = "a";
	/**
	 * CBIM
	 */
	private static final String CBIM = "cbim"; 
	/**
	 * cbim:affects
	 */
	public static final String CBIM_AFFECTS = CBIM + ":affects";

	/**
	 * cbim:Amount
	 */
	public static final String CBIM_AMOUNT = CBIM + ":Amount";
	
	/**
	 * cbim:CataloguePart
	 */
	public static final String CBIM_CATALOGUE_PART = CBIM + ":CataloguePart";

	/**
	 * cbim:cataloguePart
	 */
	public static final String CBIM_CATALOGUE_PART_RELATION = CBIM + ":cataloguePart";

	/**
	 * cbim:Connection 
	 */
	public static final String CBIM_CONNECTION = CBIM + ":Connection";

	/**
	 * cbim:creationDate
	 */
	public static final String CBIM_CREATION_DATE = CBIM + ":creationDate";

	/**
	 * cbim:creator
	 */
	public static final String CBIM_CREATOR = CBIM + ":creator";

	/**
	 * cbim:defaultValue
	 */
	public static final String CBIM_DEFAULT_VALUE = CBIM + ":defaultValue";

	/**
	 * cbim:description
	 */
	public static final String CBIM_DESCRIPTION = CBIM + ":description";

	/**
	 * cbim:Document
	 */
	public static final String CBIM_DOCUMENT = CBIM + ":Document";

	/**
	 * cbim:documentAliasFilePath
	 */
	public static final String CBIM_DOCUMENT_ALIAS_FILE_PATH = CBIM + ":documentAliasFilePath";

	/**
	 * cbim:document
	 */
	public static final String CBIM_DOCUMENT_RELATION = CBIM + ":document";

	/**
	 * cbim:documentType
	 */
	public static final String CBIM_DOCUMENT_TYPE = CBIM + ":documentType";

	/**
	 * cbim:documentUri
	 */
	public static final String CBIM_DOCUMENT_URI = CBIM + ":documentUri";

	/**
	 * cbim:endDate
	 */
	public static final String CBIM_END_DATE = CBIM + ":endDate";

	/**
	 * cbim:endDataActual
	 */
	public static final String CBIM_END_DATE_ACTUAL = CBIM + ":endDataActual";

	/**
	 * cbim:endDatePlanned
	 */
	public static final String CBIM_END_DATE_PLANNED = CBIM + ":endDatePlanned";

	/**
	 * cbim:Explicit3DRepresentation
	 */
	public static final String CBIM_EXPLICIT3D_REPRESENTATION = CBIM + ":Explicit3DRepresentation";

	/**
	 * cbim:femaleTerminal 
	 */
	public static final String CBIM_FEMALE_TERMINAL = CBIM + ":femaleTerminal";

	/**
	 * cbim:firstParameter
	 */
	public static final String CBIM_FIRST_PARAMETER = CBIM + ":firstParameter";
	
	/**
	 * cbim:fulfills
	 */
	public static final String CBIM_FULFILLS = CBIM + ":fulfills";

	/**
	 * cbim:Function
	 */
	public static final String CBIM_FUNCTION = CBIM + ":Function";

	/**
	 * cbim:FunctionFulfiller
	 */
	public static final String CBIM_FUNCTION_FULFILLER = CBIM + ":FunctionFulfiller";

	/**
	 * cbim:isAffectedBy
	 */
	public static final String CBIM_IS_AFFECTED_BY = CBIM + ":isAffectedBy";

	/**
	 * cbim:isFulfilledBy
	 */
	public static final String CBIM_IS_FULFILLED_BY = CBIM + ":isFulfilledBy";

	/**
	 * cbim:isSituatedIn
	 */
	public static final String CBIM_IS_SITUATED_IN = CBIM + ":isSituatedIn";

	/**
	 * cbim:layerIndex
	 */
	public static final String CBIM_LAYER_INDEX = CBIM + ":layerIndex";

	/**
	 * cbim:Locator
	 */
	public static final String CBIM_LOCATOR = CBIM + ":Locator";

	/**
	 * cbim:Locator
	 */
	public static final String CBIM_LOCATOR_RELATION = CBIM + ":locator";
	
	/**
	 * cbim:maleTerminal
	 */
	public static final String CBIM_MALE_TERMINAL = CBIM + ":maleTerminal";
	
	/**
	 * Max bounding box
	 */
	public static final String CBIM_MAX_BOUNDING_BOX = CBIM + ":maxBoundingBox";

	/**
	 * Min bounding box 
	 */
	public static final String CBIM_MIN_BOUNDING_BOX = CBIM + ":minBoundingBox";

	/**
	 * cbim:modificationDate
	 */
	public static final String CBIM_MODIFICATION_DATE = CBIM + ":modificationDate";

	/**
	 * cbim:modifier 
	 */
	public static final String CBIM_MODIFIER = CBIM + ":modifier";

	/**
	 * cbim:name
	 */
	public static final String CBIM_NAME = CBIM + ":name";

	/**
	 * cbim:nextParameter
	 */
	public static final String CBIM_NEXT_PARAMETER = CBIM + ":nextParameter";

	/**
	 * cbim:Parameter
	 */
	public static final String CBIM_PARAMETER = CBIM + ":Parameter";

	/**
	 * cbim:PersonOrOrganisation
	 */
	public static final String CBIM_PERSON_OR_ORGANISATION = CBIM + ":PersonOrOrganisation";

	/**
	 * cbim:physicalChild
	 */
	public static final String CBIM_PHYSICAL_CHILD = CBIM + ":physicalChild";

	/**
	 * cbim:PhysicalObject
	 */
	public static final String CBIM_PHYSICAL_OBJECT = CBIM + ":PhysicalObject";

	/**
	 * cbim:physicalParent
	 */
	public static final String CBIM_PHYSICAL_PARENT = CBIM + ":physicalParent";

	/**
	 * cbim:primaryOrientation
	 */
	public static final String CBIM_PRIMARY_ORIENTATION = CBIM + ":primaryOrientation";

	/**
	 * cbim:PropertyType 
	 */
	public static final String CBIM_PROPERTY_TYPE = CBIM + ":PropertyType";

	/**
	 * cbim:propertyType 
	 */
	public static final String CBIM_PROPERTY_TYPE_RELATION = CBIM + ":propertyType";

	/**
	 * cbim:PropertyValue
	 */
	public static final String CBIM_PROPERTY_VALUE = CBIM + ":PropertyValue";

	/**
	 * cbim:releaseDate
	 */
	public static final String CBIM_RELEASE_DATE = CBIM + ":releaseDate";
	
	/**
	 * cbim:Requirement
	 */
	public static final String CBIM_REQUIREMENT = CBIM + ":Requirement";
	/**
	 * cbim:requirementOf
	 */
	public static final String CBIM_REQUIREMENT_OF = CBIM + ":requirementOf";

	/**
	 * cbim:requirement
	 */
	public static final String CBIM_REQUIREMENT_RELATION = CBIM + ":requirement";

	/**
	 * cbim:secondaryOrientation
	 */
	public static final String CBIM_SECONDARY_ORIENTATION = CBIM + ":secondaryOrientation";

	/**
	 * cbim:shape
	 */
	public static final String CBIM_SHAPE = CBIM + ":shape";

	/**
	 * cbim:situates 
	 */
	public static final String CBIM_SITUATES = CBIM + ":situates";

	/**
	 * cbim:Space
	 */
	public static final String CBIM_SPACE = CBIM + ":Space";

	/**
	 * cbim:spatialChild
	 */
	public static final String CBIM_SPATIAL_CHILD = CBIM + ":spatialChild";

	/**
	 * cbim:spatialParent 
	 */
	public static final String CBIM_SPATIAL_PARENT = CBIM + ":spatialParent";

	/**
	 * cbim:startDate
	 */
	public static final String CBIM_START_DATE = CBIM + ":startDate";

	/**
	 * cbim:startDateActual
	 */
	public static final String CBIM_START_DATE_ACTUAL = CBIM + ":startDateActual";

	/**
	 * cbim:startDatePlanned
	 */
	public static final String CBIM_START_DATE_PLANNED = CBIM + ":startDatePlanned";

	/**
	 * cbim:superType
	 */
	public static final String CBIM_SUPER_TYPE = CBIM + ":superType";

	/**
	 * cbim:Task
	 */
	public static final String CBIM_TASK = CBIM + ":Task";

	/**
	 * cbim:TaskType
	 */
	public static final String CBIM_TASK_TYPE = CBIM + ":taskType";

	/**
	 * cbim:Terminal
	 */
	public static final String CBIM_TERMINAL = CBIM + ":Terminal";

	/**
	 * cbim:translation
	 */
	public static final String CBIM_TRANSLATION = CBIM + ":translation";

	/**
	 * cbim:unit
	 */
	public static final String CBIM_UNIT = CBIM + ":unit";

	/**
	 * cbim:userID
	 */
	public static final String CBIM_USER_ID = CBIM + ":userID";

	/**
	 * cbim:value
	 */
	public static final String CBIM_VALUE = CBIM + ":value";
	
	/**
	 * cbim:ValueDomain
	 */
	public static final String CBIM_VALUE_DOMAIN = CBIM + ":ValueDomain";

	/**
	 * cbim:valueDomain
	 */
	private static final String CBIM_VALUE_DOMAIN_REFERENCE = CBIM + ":valueDomain";

	/**
	 * cbim:Vector
	 */
	public static final String CBIM_VECTOR = CBIM + ":Vector";

	/**
	 * cbim:xCoordinate
	 */
	public static final String CBIM_X_COORDINATE = CBIM + ":xCoordinate";

	/**
	 * cbim:yCoordinate
	 */
	public static final String CBIM_Y_COORDINATE = CBIM + ":yCoordinate";

	/**
	 * cbim:zCoordinate
	 */
	public static final String CBIM_Z_COORDINATE = CBIM + ":zCoordinate";

	/**
	 * cbimfs:NonFunctionalRequirement
	 */
	public static final String CBIMFS_NON_FUNCTIONAL_REQUIREMENT = "cbimfs:NonFunctionalRequirement";

	/**
	 * cbimfs:nonFunctionalRequirement
	 */
	public static final String CBIMFS_NON_FUNCTIONAL_REQUIREMENT_RELATION = "cbimfs:nonFunctionalRequirement";

	/**
	 * cbimfs:nonFunctionalRequirementType
	 */
	public static final String CBIMFS_NON_FUNCTIONAL_REQUIREMENT_TYPE = "cbimfs:nonFunctionalRequirementType";

	/**
	 * cbimfs:superRequirement 
	 */
	public static final String CBIMFS_SUPER_REQUIREMENT = "cbimfs:superRequirement";

	/**
	 * OK
	 */
	public static final String OK = "OK";
	
	/**
	 * owl:imports
	 */
	public static final String OWL_IMPORTS = "owl:imports";

	/**
	 * PREFIX for sparql query
	 * cbim: <http://www.coinsweb.nl/c-bim.owl#>
	 */
	public static final String PREFIX_CBIM1_0 = "cbim: <http://www.coinsweb.nl/c-bim.owl#>";

	/**
	 * PREFIX for sparql query
	 * cbim: <http://www.coinsweb.nl/cbim-1.1.owl#>
	 */
	public static final String PREFIX_CBIM1_1 = "cbim: <http://www.coinsweb.nl/cbim-1.1.owl#>";

	/**
	 * PREFIX for sparql query
	 */
	public static final String PREFIX_CBIM = PREFIX_CBIM1_1;

	/**
	 * PREFIX for sparql query
	 * cbimfs: <http://www.coinsweb.nl/c-bim-fs.owl#>
	 */
	public static final String PREFIX_CBIMFS = "cbimfs: <http://www.coinsweb.nl/c-bim-fs.owl#>";

	/**
	 * PREFIX for sparql query
	 * cbimotl: <http://www.coinsweb.nl/cbim-otl-1.1.owl#>
	 */
	public static final String PREFIX_CBIMOTL = "cbimotl: <http://www.coinsweb.nl/cbim-otl-1.1.owl#>";

	/**
	 * PREFIX for sparql query
	 * owl: <http://www.w3.org/2002/07/owl#>
	 */
	public static final String PREFIX_OWL = "owl: <http://www.w3.org/2002/07/owl#>";

	@Inject
	private ConfigurationService mConfigurationService;
	
	@Inject
	private ICoinsDateConversion mDateConversion;
	
	@Inject
	private SparqlService mSparqlService;

	/**
	 * cbim:State
	 */
	public static final String CBIM_STATE = CBIM + ":State";

	/**
	 * cbim:currentState
	 */
	public static final String CBIM_CURRENT_STATE = CBIM + ":currentState";

	/**
	 * cbim:stateOf
	 */
	public static final String CBIM_STATE_OF = CBIM + ":stateOf";

	/**
	 * cbim:previousState
	 */
	public static final String CBIM_PREVIOUS_STATE = CBIM + ":previousState";
	
	@Override
	public void addAttributeDate(String context, String object, String name,
			String value) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		QueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addPrefix(PREFIX_CBIMFS);
		builder.addGraph(getFullContext(context));
		builder.setId(object);
		builder.addAttributeDate(name, value);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
	}
	
	@Override
	public void addAttributeFloat(String context, String object, String name,
			double value) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		QueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addPrefix(PREFIX_CBIMFS);
		builder.addGraph(getFullContext(context));
		builder.setId(object);
		builder.addAttributeDouble(name, value);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void addAttributeInt(String context, String object, String name,
			int value) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		QueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addPrefix(PREFIX_CBIMFS);
		builder.addGraph(getFullContext(context));
		builder.setId(object);
		builder.addAttributeInteger(name, value);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
	}
	
	@Override
	public void addAttributeResource(String context, String object,
			String name, String value) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		QueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addPrefix(PREFIX_CBIMFS);
		builder.addGraph(getFullContext(context));
		builder.setId(object);
		builder.addAttributeLink(name, value);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void addAttributeString(String context, String object, String name,
			String value) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		QueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addPrefix(PREFIX_CBIMFS);
		builder.addGraph(getFullContext(context));
		builder.setId(object);
		builder.addAttributeString(name, value);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public String createAmount(String context, String modelURI, String name,
			String userID, int value, String catalogPart, String creator)
			throws MarmottaException, InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException {
		String id = constructId(modelURI);		
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CBIM_NAME, name);
		builder.addAttributeString(CBIM_USER_ID, userID);
		builder.addAttributeString(CBIM_CATALOGUE_PART_RELATION, catalogPart);
		builder.addAttributeInteger(CBIM_VALUE, value);
		builder.addAttributeDate(CBIM_CREATION_DATE, new Date());
		builder.addAttributeLink(CBIM_CREATOR, creator);
		builder.addAttributeString(A, CBIM_AMOUNT);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public String createCataloguePart(String context, String modelURI,
			String name, String userID, String creator)
			throws MarmottaException, InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException {
		String id = constructId(modelURI);		
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CBIM_NAME, name);
		builder.addAttributeString(CBIM_USER_ID, userID);
		builder.addAttributeDate(CBIM_CREATION_DATE, new Date());
		builder.addAttributeLink(CBIM_CREATOR, creator);
		builder.addAttributeString(A, CBIM_CATALOGUE_PART);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public String createConnection(String context, String modelURI,
			String name, String userID, String creator)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		String id = constructId(modelURI);		
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CBIM_NAME, name);
		builder.addAttributeString(CBIM_USER_ID, userID);
		builder.addAttributeDate(CBIM_CREATION_DATE, new Date());
		builder.addAttributeLink(CBIM_CREATOR, creator);
		builder.addAttributeString(A, CBIM_CONNECTION);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public String createDocument(String context, String modelURI, String name,
			String userID, String creator) throws MarmottaException,
			InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException {
		String id = constructId(modelURI);		
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CBIM_NAME, name);
		builder.addAttributeDate(CBIM_CREATION_DATE, new Date());
		builder.addAttributeString(CBIM_USER_ID, userID);
		builder.addAttributeLink(CBIM_CREATOR, creator);
		builder.addAttributeString(A, CBIM_DOCUMENT);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public String createExplicit3DRepresentation(String context,
			String modelURI, String name, String documentType,
			String documentAliasFilePath, String documentUri, String creator)
					throws MarmottaException, InvalidArgumentException,
					MalformedQueryException, UpdateExecutionException {
		String id = constructId(modelURI);		
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CBIM_NAME, name);
		builder.addAttributeDate(CBIM_CREATION_DATE, new Date());
		builder.addAttributeString(CBIM_DOCUMENT_TYPE, documentType);
		builder.addAttributeString(CBIM_DOCUMENT_ALIAS_FILE_PATH, documentAliasFilePath);
		builder.addAttributeLink(CBIM_DOCUMENT_URI, documentUri);
		builder.addAttributeLink(CBIM_CREATOR, creator);
		builder.addAttributeString(A, CBIM_EXPLICIT3D_REPRESENTATION);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public String createFunction(String context, String modelURI, String name,
			int layerIndex, String userID, String creator)
			throws MarmottaException, InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException {
		String id = constructId(modelURI);		
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CBIM_NAME, name);
		builder.addAttributeDate(CBIM_CREATION_DATE, new Date());
		builder.addAttributeString(CBIM_USER_ID, userID);
		builder.addAttributeInteger(CBIM_LAYER_INDEX, layerIndex);
		builder.addAttributeLink(CBIM_CREATOR, creator);
		builder.addAttributeString(A, CBIM_FUNCTION);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public String createLocator(String context, String modelURI, String name,
			String primaryOrientation, String secondaryOrientation,
			String translation, String creator) throws MarmottaException,
			InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException {
		String id = constructId(modelURI);		
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CBIM_NAME, name);
		builder.addAttributeDate(CBIM_CREATION_DATE, new Date());
		builder.addAttributeLink(CBIM_PRIMARY_ORIENTATION, primaryOrientation);
		builder.addAttributeLink(CBIM_SECONDARY_ORIENTATION, secondaryOrientation);
		builder.addAttributeLink(CBIM_TRANSLATION, translation);
		builder.addAttributeLink(CBIM_CREATOR, creator);
		builder.addAttributeString(A, CBIM_LOCATOR);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public String createNonFunctionalRequirement(String context,
			String modelURI, String name, int layerIndex, String userID,
			String creator, String nonFunctionalRequirementType)
			throws MarmottaException, InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException {
		String id = constructId(modelURI);		
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addPrefix(PREFIX_CBIMFS);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CBIM_NAME, name);
		builder.addAttributeString(CBIM_USER_ID, userID);
		builder.addAttributeDate(CBIM_CREATION_DATE, new Date());
		builder.addAttributeInteger(CBIM_LAYER_INDEX, layerIndex);
		builder.addAttributeLink(CBIM_CREATOR, creator);
		if (isURI(nonFunctionalRequirementType)) {
			builder.addAttributeLink(CBIMFS_NON_FUNCTIONAL_REQUIREMENT_TYPE, nonFunctionalRequirementType);
		}
		else {
			builder.addAttributeString(CBIMFS_NON_FUNCTIONAL_REQUIREMENT_TYPE, nonFunctionalRequirementType);
		}
		builder.addAttributeString(A, CBIMFS_NON_FUNCTIONAL_REQUIREMENT);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}
	
	@Override
	public String createParameter(String context, String modelURI, String name,
			String userID, String defaultValue, String creator)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		String id = constructId(modelURI);		
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CBIM_NAME, name);
		builder.addAttributeDate(CBIM_CREATION_DATE, new Date());
		builder.addAttributeString(CBIM_USER_ID, userID);
		builder.addAttributeLink(CBIM_CREATOR, creator);
		builder.addAttributeString(CBIM_DEFAULT_VALUE, defaultValue);
		builder.addAttributeString(A, CBIM_PARAMETER);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}
	
	@Override
	public String createPersonOrOrganisation(String context, String modelURI, String name)
			throws MarmottaException, InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException {
		String id = constructId(modelURI);		
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CBIM_NAME, name);
		builder.addAttributeDate(CBIM_CREATION_DATE, new Date());
		builder.addAttributeString(A, CBIM_PERSON_OR_ORGANISATION);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public String createPhysicalObject(String context, String modelURI,
			String name, int layerIndex, String userID, String creator)
			throws MarmottaException, InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException {
		String id = constructId(modelURI);		
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CBIM_NAME, name);
		builder.addAttributeDate(CBIM_CREATION_DATE, new Date());
		builder.addAttributeString(CBIM_USER_ID, userID);
		builder.addAttributeInteger(CBIM_LAYER_INDEX, layerIndex);
		builder.addAttributeLink(CBIM_CREATOR, creator);		
		builder.addAttributeString(A, CBIM_PHYSICAL_OBJECT);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public String createPropertyType(String context, String modelURI,
			String name, String userID, String unit, String valuedomain, String creator)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		String id = constructId(modelURI);		
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addPrefix(PREFIX_CBIMFS);
		builder.addPrefix(PREFIX_CBIMOTL);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CBIM_NAME, name);
		builder.addAttributeString(CBIM_USER_ID, userID);
		builder.addAttributeString(CBIM_UNIT, unit);
		builder.addAttributeLink(CBIM_VALUE_DOMAIN_REFERENCE, valuedomain);
		builder.addAttributeDate(CBIM_CREATION_DATE, new Date());
		builder.addAttributeLink(CBIM_CREATOR, creator);
		builder.addAttributeString(A, CBIM_PROPERTY_TYPE);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public String createPropertyValue(String context, String modelURI, String name,
			String userID, String propertytype, String value, String creator)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		String id = constructId(modelURI);		
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addPrefix(PREFIX_CBIMFS);
		builder.addPrefix(PREFIX_CBIMOTL);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CBIM_NAME, name);
		builder.addAttributeString(CBIM_USER_ID, userID);
		builder.addAttributeLink(CBIM_PROPERTY_TYPE_RELATION, propertytype);
		setPropertyValue(builder, value, propertytype, context);
		builder.addAttributeDate(CBIM_CREATION_DATE, new Date());
		builder.addAttributeLink(CBIM_CREATOR, creator);
		builder.addAttributeString(A, CBIM_PROPERTY_VALUE);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public String createRequirement(String context, String modelURI, String name,
			int layerindex, String userId,String creator, String requirementOf) throws MarmottaException, InvalidArgumentException, MalformedQueryException, UpdateExecutionException {		
		String id = constructId(modelURI);
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CBIM_NAME, name);
		builder.addAttributeString(CBIM_USER_ID, userId);
		builder.addAttributeDate(CBIM_CREATION_DATE, new Date());
		builder.addAttributeInteger(CBIM_LAYER_INDEX, layerindex);
		builder.addAttributeLink(CBIM_CREATOR, creator);
		builder.addAttributeLink(CBIM_REQUIREMENT_OF, requirementOf);
		builder.addAttributeString(A, CBIM_REQUIREMENT);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public String createSpace(String context, String modelURI, String name,
			int layerIndex, String userID, String creator)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		String id = constructId(modelURI);		
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CBIM_NAME, name);
		builder.addAttributeDate(CBIM_CREATION_DATE, new Date());
		builder.addAttributeString(CBIM_USER_ID, userID);
		builder.addAttributeInteger(CBIM_LAYER_INDEX, layerIndex);
		builder.addAttributeLink(CBIM_CREATOR, creator);		
		builder.addAttributeString(A, CBIM_SPACE);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public String createTask(String context, String modelURI, String name,
			String[] affects, String userID, String taskType,
			String startDatePlanned, String endDatePlanned, String creator)
			throws MarmottaException, InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException {
		String id = constructId(modelURI);		
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CBIM_NAME, name);
		builder.addAttributeDate(CBIM_CREATION_DATE, new Date());
		builder.addAttributeDate(CBIM_START_DATE_PLANNED, startDatePlanned);
		builder.addAttributeDate(CBIM_END_DATE_PLANNED, endDatePlanned);
		builder.addAttributeString(CBIM_TASK_TYPE, taskType);
		for (String physicalObjecId : affects) {			
			builder.addAttributeLink(CBIM_AFFECTS, physicalObjecId);
		}
		builder.addAttributeLink(CBIM_CREATOR, creator);
		builder.addAttributeString(A, CBIM_TASK);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public String createTerminal(String context, String modelURI, String name,
			String userID, String locator, int layerindex, String creator)
					throws InvalidArgumentException, MalformedQueryException,
					UpdateExecutionException, MarmottaException {
		String id = constructId(modelURI);
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CBIM_NAME, name);
		builder.addAttributeString(CBIM_USER_ID, userID);
		builder.addAttributeDate(CBIM_CREATION_DATE, new Date());
		builder.addAttributeInteger(CBIM_LAYER_INDEX, layerindex);
		builder.addAttributeLink(CBIM_CREATOR, creator);
		builder.addAttributeLink(CBIM_LOCATOR_RELATION, locator);
		builder.addAttributeString(A, CBIM_TERMINAL);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public String createVector(String context, String modelURI, String name,
			Double xCoordinate, Double yCoordinate, Double zCoordinate,
			String creator) throws MarmottaException, InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException {
		String id = constructId(modelURI);		
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CBIM_NAME, name);
		builder.addAttributeDate(CBIM_CREATION_DATE, new Date());
		builder.addAttributeDouble(CBIM_X_COORDINATE, xCoordinate);
		builder.addAttributeDouble(CBIM_Y_COORDINATE, yCoordinate);
		builder.addAttributeDouble(CBIM_Z_COORDINATE, zCoordinate);
		builder.addAttributeLink(CBIM_CREATOR, creator);
		builder.addAttributeString(A, CBIM_VECTOR);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public boolean deleteAmount(String context, String id) {
		return deleteItem(context, id, CBIM_AMOUNT, PREFIX_CBIM);
	}

	@Override
	public boolean deleteCataloguePart(String context, String id) {
		return deleteItem(context, id, CBIM_CATALOGUE_PART, PREFIX_CBIM);
	}

	@Override
	public boolean deleteConnection(String context, String id) {
		return deleteItem(context, id, CBIM_CONNECTION, PREFIX_CBIM);
	}

	@Override
	public boolean deleteDocument(String context, String id) {
		return deleteItem(context, id, CBIM_DOCUMENT, PREFIX_CBIM);
	}

	@Override
	public boolean deleteExplicit3DRepresentation(String context, String id) {
		return deleteItem(context, id, CBIM_EXPLICIT3D_REPRESENTATION, PREFIX_CBIM);
	}

	@Override
	public boolean deleteFunction(String context, String id) {
		return deleteItem(context, id, CBIM_FUNCTION, PREFIX_CBIM);
	}

	private boolean deleteItem(String context, String id, String type, String prefix) {
		String query = "PREFIX " + prefix + "\nDELETE WHERE { GRAPH <" + getFullContext(context)
				+ "> { <" + id + "> ?name ?value ; a " + type + " }}";
		try {
			mSparqlService.update(QueryLanguage.SPARQL, query);
			return true;
		} catch (InvalidArgumentException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (UpdateExecutionException e) {
			e.printStackTrace();
		} catch (MarmottaException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean deleteLocator(String context, String id) {
		return deleteItem(context, id, CBIM_LOCATOR, PREFIX_CBIM);
	}

	@Override
	public boolean deleteNonFunctionalRequirement(String context, String id) {
		return deleteItem(context, id, CBIMFS_NON_FUNCTIONAL_REQUIREMENT, PREFIX_CBIMFS);
	}

	@Override
	public boolean deleteParameter(String context, String id) {
		return deleteItem(context, id, CBIM_PARAMETER, PREFIX_CBIM);
	}

	@Override
	public boolean deletePersonOrOrganisation(String context, String id) {
		return deleteItem(context, id, CBIM_PERSON_OR_ORGANISATION, PREFIX_CBIM);
	}

	@Override
	public boolean deletePhysicalObject(String context, String id) {
		return deleteItem(context, id, CBIM_PHYSICAL_OBJECT, PREFIX_CBIM);
	}
	
	@Override
	public boolean deletePropertyType(String context, String id) {
		return deleteItem(context, id, CBIM_PROPERTY_TYPE, PREFIX_CBIM);
	}

	@Override
	public boolean deletePropertyValue(String context, String id) {
		return deleteItem(context, id, CBIM_PROPERTY_VALUE, PREFIX_CBIM);
	}

	@Override
	public boolean deleteRequirement(String context, String id) {
		return deleteItem(context, id, CBIM_REQUIREMENT, PREFIX_CBIM);
	}

	@Override
	public boolean deleteSpace(String context, String id) {
		return deleteItem(context, id, CBIM_SPACE, PREFIX_CBIM);
	}

	@Override
	public boolean deleteTask(String context, String id) {
		return deleteItem(context, id, CBIM_TASK, PREFIX_CBIM);
	}

	@Override
	public boolean deleteTerminal(String context, String id) {
		return deleteItem(context, id, CBIM_TERMINAL, PREFIX_CBIM);
	}

	@Override
	public boolean deleteVector(String context, String id) {
		return deleteItem(context, id, CBIM_VECTOR, PREFIX_CBIM);
	}

	@Override
	public String getAmountQuery(String context, String id) {
		return getSelectQuery(context, id, CBIM_AMOUNT, PREFIX_CBIM);
	}
	
	@Override
	public String getCataloguePartQuery(String context, String id) {
		return getSelectQuery(context, id, CBIM_CATALOGUE_PART, PREFIX_CBIM);
	}

	@Override
	public String getConnectionQuery(String context, String id) {
		return getSelectQuery(context, id, CBIM_CONNECTION, PREFIX_CBIM);
	}

	@Override
	public String getDocumentQuery(String context, String id) {
		return getSelectQuery(context, id, CBIM_DOCUMENT, PREFIX_CBIM);
	}

	@Override
	public String getExplicit3DRepresentationQuery(String context, String id) {
		return getSelectQuery(context, id, CBIM_EXPLICIT3D_REPRESENTATION, PREFIX_CBIM);
	}

	private String getFullContext(String pContext) {
		if (pContext == null) {
			return mConfigurationService.getDefaultContext();
		}
		if (pContext.startsWith("http")) {
			return pContext;
		}
		if (mConfigurationService.getBaseContext().endsWith("/")) {
			return mConfigurationService.getBaseContext() + pContext;
		}
		return mConfigurationService.getBaseContext() + "/" + pContext;
	}

	@Override
	public String getFunctionQuery(String context, String id) {
		return getSelectQuery(context, id, CBIM_FUNCTION, PREFIX_CBIM);
	}

	@Override
	public String getLocatorQuery(String context, String id) {
		return getSelectQuery(context, id, CBIM_LOCATOR, PREFIX_CBIM);
	}

	@Override
	public String getNonFunctionalRequirementQuery(String context, String id) {
		return getSelectQuery(context, id, CBIMFS_NON_FUNCTIONAL_REQUIREMENT, PREFIX_CBIMFS);
	}

	@Override
	public String getParameterQuery(String context, String id) {
		return getSelectQuery(context, id, CBIM_PARAMETER, PREFIX_CBIM);
	}

	@Override
	public String getPersonOrOrganisationQuery(String context, String id) {
		return getSelectQuery(context, id, CBIM_PERSON_OR_ORGANISATION, PREFIX_CBIM);
	}

	@Override
	public String getPhysicalObjectQuery(String context, String id) {
		return getSelectQuery(context, id, CBIM_PHYSICAL_OBJECT, PREFIX_CBIM);
	}

	@Override
	public String getPropertyTypeQuery(String context, String id) {
		return getSelectQuery(context, id, CBIM_PROPERTY_TYPE, PREFIX_CBIM);
	}

	@Override
	public String getPropertyValueQuery(String context, String id) {
		return getSelectQuery(context, id, CBIM_PROPERTY_VALUE, PREFIX_CBIM);
	}

	@Override
	public String getRequirementQuery(String context, String id) {
		return getSelectQuery(context, id, CBIM_REQUIREMENT, PREFIX_CBIM);
	}

	private String getSelectQuery(String context, String id, String type, String prefix) {
		StringBuilder result = new StringBuilder();
		result.append("PREFIX ");
		result.append(prefix);
		result.append("\n\nSELECT ?name ?value WHERE {\n\tGRAPH <");
		result.append(getFullContext(context));
		result.append("> {\n\t\t<");
		result.append(id);
		result.append("> ?name ?value ;\n\t\t\ta ");
		result.append(type);
		result.append("\n\t}\n}");
		return result.toString();
	}

	@Override
	public String getSpaceQuery(String context, String id) {
		return getSelectQuery(context, id, CBIM_SPACE, PREFIX_CBIM);
	}

	@Override
	public String getTaskQuery(String context, String id) {
		return getSelectQuery(context, id, CBIM_TASK, PREFIX_CBIM);
	}

	@Override
	public String getTerminalQuery(String context, String id) {
		return getSelectQuery(context, id, CBIM_TERMINAL, PREFIX_CBIM);
	}

	@Override
	public String getVectorQuery(String context, String id) {
		return getSelectQuery(context, id, CBIM_VECTOR, PREFIX_CBIM);
	}

	@Override
	public void initializeContext(String context, String modelUri) throws InvalidArgumentException, MalformedQueryException, UpdateExecutionException, MarmottaException {
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_OWL);
		builder.addGraph(getFullContext(context));
		builder.setId(modelUri);
		builder.addAttributeString("owl:versionInfo", "Created with Marmotta COINS module");
		builder.addAttributeLink(OWL_IMPORTS, "http://www.coinsweb.nl/cbim-1.1.owl");
		builder.addAttributeLink(OWL_IMPORTS, "http://www.coinsweb.nl/c-bim-fs.owl");
		builder.addAttributeString(A, "owl:Ontology");
		
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	private boolean isURI(String pString) {
		try {
			URI uri = new URI(pString);
			if (uri.isAbsolute()) {
				return true;
			}
		} catch (URISyntaxException e) {
			return false;
		}
		return false;
	}

	@Override
	public void linkBoundingBox(String context, String cbim_id,
			String boundingBox, String locator, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		// TODO one query?
		QueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(locator);
		builder.addAttributeLink(cbim_id, boundingBox);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		
		builder = new UpdateQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(locator);
		builder.addAttributeLink(cbim_id, boundingBox);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void linkDocument(String context, String physicalobject,
			String[] document, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		QueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(physicalobject);
		for (String docId : document) {
			builder.addAttributeLink(CBIM_DOCUMENT_RELATION, docId);
		}
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		// The previous query might have caused two modificationDates/modifiers
		// so...
		builder = new UpdateQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(physicalobject);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void linkFunctionIsFulfilledBy(String context, String function,
			String[] fulfilledby, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		QueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(function);
		for (String physicalObjectId : fulfilledby) {
			builder.addAttributeLink(CBIM_IS_FULFILLED_BY, physicalObjectId);
		}
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		// The previous query might have caused two modificationDates/modifiers
		// so...
		builder = new UpdateQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(function);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void linkIsAffectedBy(String context, String functionfulfiller,
			String isAffectedBy, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		// TODO one query?
		QueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(functionfulfiller);
		builder.addAttributeLink(CBIM_IS_AFFECTED_BY, isAffectedBy);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());

		builder = new UpdateQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(functionfulfiller);
		builder.addAttributeLink(CBIM_IS_AFFECTED_BY, isAffectedBy);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);		
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void linkLocator(String context, String object, String locator,
			String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		// TODO one query?
		QueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(object);
		builder.addAttributeLink(CBIM_LOCATOR_RELATION, locator);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		
		builder = new UpdateQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(object);
		builder.addAttributeLink(CBIM_LOCATOR_RELATION, locator);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void linkNonFunctionalRequirement(String context,
			String functionfulfiller, String[] nonfunctionalrequirement,
			String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		QueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addPrefix(PREFIX_CBIMFS);
		builder.addGraph(getFullContext(context));
		builder.setId(functionfulfiller);
		for (String nfr : nonfunctionalrequirement) {
			builder.addAttributeLink(CBIMFS_NON_FUNCTIONAL_REQUIREMENT_RELATION, nfr);
		}
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		// The previous query might have caused two modificationDates/modifiers
		// so...
		builder = new UpdateQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(functionfulfiller);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void linkPhysicalObjectFulfills(String context,
			String physicalobject, String[] fulfills, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		QueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(physicalobject);
		for (String function : fulfills) {
			builder.addAttributeLink(CBIM_FULFILLS, function);
		}
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		// The previous query might have caused two modificationDates/modifiers
		// so...
		builder = new UpdateQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(physicalobject);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void linkTerminal(String context, String cbim_id, String terminal,
			String connection, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		// TODO one query?
		QueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(connection);
		builder.addAttributeLink(cbim_id, terminal);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		
		builder = new UpdateQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(connection);
		builder.addAttributeLink(cbim_id, terminal);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void setDescription(String context, String id, String description, String modifier) throws InvalidArgumentException, MalformedQueryException, UpdateExecutionException, MarmottaException {
		// Only inserting the description may cause duplicate descriptions.
		// Only updating the description does not work if the description was not present yet.
		// Maybe someone is able to do this with one query. That would be more efficient...
		// TODO one query?
		QueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CBIM_DESCRIPTION, description);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());

		builder = new UpdateQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CBIM_DESCRIPTION, description);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);		
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void setFirstParameter(String context,
			String explicit3dRepresentation, String firstParameter,
			String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		// TODO one query?
		QueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(explicit3dRepresentation);
		builder.addAttributeLink(CBIM_FIRST_PARAMETER, firstParameter);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());

		builder = new UpdateQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(explicit3dRepresentation);
		builder.addAttributeLink(CBIM_FIRST_PARAMETER, firstParameter);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);		
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void setNextParameter(String context, String parameter,
			String nextParameter, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		// TODO one query?
		QueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(parameter);
		builder.addAttributeLink(CBIM_NEXT_PARAMETER, nextParameter);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());

		builder = new UpdateQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(parameter);
		builder.addAttributeLink(CBIM_NEXT_PARAMETER, nextParameter);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);		
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	private void setPropertyValue(InsertQueryBuilder pBuilder, String pValue,
			String pPropertytype, String pContext) throws MarmottaException {
		StringBuilder query = new StringBuilder();
		query.append("PREFIX ");
		query.append(PREFIX_CBIM);
		query.append("\n\nSELECT ?valueType WHERE {\n\tGRAPH <");
		query.append(getFullContext(pContext));
		query.append("> {\n\t\t<");
		query.append(pPropertytype);
		query.append("> ?name ?valueType ;\n\t\t\ta ");
		query.append(CBIM_PROPERTY_TYPE);
		query.append("; ");
		query.append(CBIM_VALUE_DOMAIN_REFERENCE);
		query.append(" ?valueType \n\t}\n}");
		List<Map<String, Value>> result = mSparqlService.query(
				QueryLanguage.SPARQL, query.toString());
		if (result.isEmpty()) {
			pBuilder.addAttributeString(CBIM_VALUE, pValue);
		} else {
			String dataType = result.get(0).get("valueType").toString();
			if (dataType.contains("XsdString")) {
				pBuilder.addAttributeString(CBIM_VALUE, pValue);
			} else if (dataType.contains("XsdBoolean")) {
				pBuilder.addAttributeBoolean(CBIM_VALUE,
						Boolean.parseBoolean(pValue));
			} else if (dataType.contains("XsdFloat")) {
				pBuilder.addAttributeDouble(CBIM_VALUE,
						Double.parseDouble(pValue));
			} else if (dataType.contains("XsdInt")) {
				pBuilder.addAttributeInteger(CBIM_VALUE,
						Integer.parseInt(pValue));
			} else if (dataType.contains("CbimCataloguePart")
					|| dataType.contains("CbimDocument")
					|| dataType.contains("CbimParameter")) {
				pBuilder.addAttributeLink(CBIM_VALUE, pValue);
			} else if (dataType.contains("XsdDateTime")) {
				pBuilder.addAttributeDate(CBIM_VALUE, pValue);
			}
		}		
	}

	@Override
	public void setPysicalChild(String context, String child, String parent,
			String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		QueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(parent);
		builder.addAttributeLink(CBIM_PHYSICAL_CHILD, child);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());		
	}

	@Override
	public void setPysicalParent(String context, String child, String parent, String modifier) throws InvalidArgumentException, MalformedQueryException, UpdateExecutionException, MarmottaException {
		// By definition a child can only have one Physical Parent (contrary to the real world where we usually have two parents)
		// So either one needs to be inserted or an already existing one needs to be updated...
		// TODO one query?
		QueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(child);
		builder.addAttributeLink(CBIM_PHYSICAL_PARENT, parent);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());

		builder = new UpdateQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(child);
		builder.addAttributeLink(CBIM_PHYSICAL_PARENT, parent);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);		
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void setShape(String context, String physicalobject, String shape,
			String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		// TODO one query?
		QueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(physicalobject);
		builder.addAttributeLink(CBIM_SHAPE, shape);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());

		builder = new UpdateQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(physicalobject);
		builder.addAttributeLink(CBIM_PHYSICAL_PARENT, shape);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);		
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void setSpatialChild(String context, String child, String parent,
			String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		QueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(parent);
		builder.addAttributeLink(CBIM_SPATIAL_CHILD, child);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());		
	}

	@Override
	public void setSpatialParent(String context, String child, String parent,
			String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		// By definition a child can only have one Spatial Parent (contrary to the real world where we usually have two parents)
		// So either one needs to be inserted or an already existing one needs to be updated...
		// TODO one query?
		QueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(child);
		builder.addAttributeLink(CBIM_SPATIAL_PARENT, parent);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());

		builder = new UpdateQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(child);
		builder.addAttributeLink(CBIM_SPATIAL_PARENT, parent);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);		
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public List<String> validate(String pContext, ValidationAspect aspect) {
		CoinsValidator validator = CoinsValidatorFactory.getValidator(aspect, pContext, mSparqlService);
		if (validator.validate()) {
			List<String> result = new Vector<String>();
			result.add(OK);
			return result;
		}
		return validator.getValidationErrors();
	}

	@Override
	public String createState(String context, String modelURI, String name,
			String userID, String creator) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		String id = constructId(modelURI);		
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CBIM_NAME, name);
		builder.addAttributeString(CBIM_USER_ID, userID);
		builder.addAttributeDate(CBIM_CREATION_DATE, new Date());
		builder.addAttributeLink(CBIM_CREATOR, creator);
		builder.addAttributeString(A, CBIM_STATE);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}
	
	private String constructId(String modelURI) {
		return modelURI + "#m" + java.util.UUID.randomUUID().toString();
	}

	@Override
	public String getStateQuery(String context, String id) {
		return getSelectQuery(context, id, CBIM_STATE, PREFIX_CBIM);
	}

	@Override
	public boolean deleteState(String context, String id) {
		return deleteItem(context, id, CBIM_STATE, PREFIX_CBIM);
	}

	@Override
	public void setCurrentState(String context, String state,
			String functionfulfiller, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		// TODO one query?
		QueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(functionfulfiller);
		builder.addAttributeLink(CBIM_CURRENT_STATE, state);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());

		builder = new UpdateQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(functionfulfiller);
		builder.addAttributeLink(CBIM_CURRENT_STATE, state);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);		
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void setStateOf(String context, String state,
			String functionfulfiller, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		// TODO one query?
		QueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(state);
		builder.addAttributeLink(CBIM_STATE_OF, functionfulfiller);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());

		builder = new UpdateQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(state);
		builder.addAttributeLink(CBIM_STATE_OF, functionfulfiller);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);		
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void setPreviousState(String context, String state,
			String previousstate, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		// TODO one query?
		QueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(state);
		builder.addAttributeLink(CBIM_PREVIOUS_STATE, previousstate);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());

		builder = new UpdateQueryBuilder(mDateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(state);
		builder.addAttributeLink(CBIM_PREVIOUS_STATE, previousstate);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);		
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

}

 