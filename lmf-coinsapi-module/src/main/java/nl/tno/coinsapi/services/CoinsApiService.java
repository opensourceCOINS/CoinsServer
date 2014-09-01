package nl.tno.coinsapi.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.inject.Inject;

import nl.tno.coinsapi.tools.CoinsValidator;
import nl.tno.coinsapi.tools.CoinsValidator.CoinsAffectsValidator;
import nl.tno.coinsapi.tools.CoinsValidator.CoinsAllValidator;
import nl.tno.coinsapi.tools.CoinsValidator.CoinsFunctionFulfillerValidator;
import nl.tno.coinsapi.tools.CoinsValidator.CoinsLiteralValidator;
import nl.tno.coinsapi.tools.CoinsValidator.CoinsPhysicalObjectParentChildValidator;
import nl.tno.coinsapi.tools.CoinsValidator.CoinsPhysicalParentValidator;
import nl.tno.coinsapi.tools.CoinsValidator.CoinsRequirementValidator;
import nl.tno.coinsapi.tools.CoinsValidator.CoinsSituatedValidator;
import nl.tno.coinsapi.tools.CoinsValidator.CoinsSpaceParentChildValidator;
import nl.tno.coinsapi.tools.QueryBuilder;
import nl.tno.coinsapi.tools.QueryBuilder.InsertQueryBuilder;
import nl.tno.coinsapi.tools.QueryBuilder.UpdateQueryBuilder;

import org.apache.marmotta.platform.core.api.config.ConfigurationService;
import org.apache.marmotta.platform.core.exception.InvalidArgumentException;
import org.apache.marmotta.platform.core.exception.MarmottaException;
import org.apache.marmotta.platform.sparql.api.sparql.SparqlService;
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
	 * cbim:affects
	 */
	public static final String CBIM_AFFECTS = "cbim:affects";

	/**
	 * cbim:Amount
	 */
	public static final String CBIM_AMOUNT = "cbim:Amount";

	/**
	 * cbim:CataloguePart
	 */
	public static final String CBIM_CATALOGUE_PART = "cbim:CataloguePart";

	/**
	 * cbim:cataloguePart
	 */
	public static final String CBIM_CATALOGUE_PART_RELATION = "cbim:cataloguePart";

	/**
	 * cbim:creationDate
	 */
	public static final String CBIM_CREATION_DATE = "cbim:creationDate";

	/**
	 * cbim:creator
	 */
	public static final String CBIM_CREATOR = "cbim:creator";

	/**
	 * cbim:defaultValue
	 */
	public static final String CBIM_DEFAULT_VALUE = "cbim:defaultValue";

	/**
	 * cbim:description
	 */
	public static final String CBIM_DESCRIPTION = "cbim:description";

	/**
	 * cbim:Document
	 */
	public static final String CBIM_DOCUMENT = "cbim:Document";

	/**
	 * cbim:documentAliasFilePath
	 */
	public static final String CBIM_DOCUMENT_ALIAS_FILE_PATH = "cbim:documentAliasFilePath";

	/**
	 * cbim:document
	 */
	public static final String CBIM_DOCUMENT_RELATION = "cbim:document";

	/**
	 * cbim:documentType
	 */
	public static final String CBIM_DOCUMENT_TYPE = "cbim:documentType";

	/**
	 * cbim:documentUri
	 */
	public static final String CBIM_DOCUMENT_URI = "cbim:documentUri";

	/**
	 * cbim:endDate
	 */
	public static final String CBIM_END_DATE = "cbim:endDate";

	/**
	 * cbim:endDataActual
	 */
	public static final String CBIM_END_DATE_ACTUAL = "cbim:endDataActual";

	/**
	 * cbim:endDatePlanned
	 */
	public static final String CBIM_END_DATE_PLANNED = "cbim:endDatePlanned";

	/**
	 * cbim:Explicit3DRepresentation
	 */
	public static final String CBIM_EXPLICIT3D_REPRESENTATION = "cbim:Explicit3DRepresentation";

	/**
	 * cbim:firstParameter
	 */
	public static final String CBIM_FIRST_PARAMETER = "cbim:firstParameter";

	/**
	 * cbim:fulfills
	 */
	public static final String CBIM_FULFILLS = "cbim:fulfills";
	
	/**
	 * cbim:Function
	 */
	public static final String CBIM_FUNCTION = "cbim:Function";

	/**
	 * cbim:FunctionFulfiller
	 */
	public static final String CBIM_FUNCTION_FULFILLER = "cbim:FunctionFulfiller";

	/**
	 * cbim:isAffectedBy
	 */
	public static final String CBIM_IS_AFFECTED_BY = "cbim:isAffectedBy";

	/**
	 * cbim:isFulfilledBy
	 */
	public static final String CBIM_IS_FULFILLED_BY = "cbim:isFulfilledBy";

	/**
	 * cbim:isSituatedIn
	 */
	public static final String CBIM_IS_SITUATED_IN = "cbim:isSituatedIn";

	/**
	 * cbim:layerIndex
	 */
	public static final String CBIM_LAYER_INDEX = "cbim:layerIndex";

	/**
	 * cbim:Locator
	 */
	public static final String CBIM_LOCATOR = "cbim:Locator";

	/**
	 * cbim:modificationDate
	 */
	public static final String CBIM_MODIFICATION_DATE = "cbim:modificationDate";

	/**
	 * cbim:modifier 
	 */
	public static final String CBIM_MODIFIER = "cbim:modifier";

	/**
	 * cbim:name
	 */
	public static final String CBIM_NAME = "cbim:name";

	/**
	 * cbim:nextParameter
	 */
	public static final String CBIM_NEXT_PARAMETER = "cbim:nextParameter";

	/**
	 * cbim:Parameter
	 */
	public static final String CBIM_PARAMETER = "cbim:Parameter";

	/**
	 * cbim:PersonOrOrganisation
	 */
	public static final String CBIM_PERSON_OR_ORGANISATION = "cbim:PersonOrOrganisation";

	/**
	 * cbim:physicalChild
	 */
	public static final String CBIM_PHYSICAL_CHILD = "cbim:physicalChild";

	/**
	 * cbim:PhysicalObject
	 */
	public static final String CBIM_PHYSICAL_OBJECT = "cbim:PhysicalObject";

	/**
	 * cbim:physicalParent
	 */
	public static final String CBIM_PHYSICAL_PARENT = "cbim:physicalParent";

	/**
	 * cbim:primaryOrientation
	 */
	public static final String CBIM_PRIMARY_ORIENTATION = "cbim:primaryOrientation";

	/**
	 * cbim:releaseDate
	 */
	public static final String CBIM_RELEASE_DATE = "cbim:releaseDate";

	/**
	 * cbim:Requirement
	 */
	public static final String CBIM_REQUIREMENT = "cbim:Requirement";

	/**
	 * cbim:requirementOf
	 */
	public static final String CBIM_REQUIREMENT_OF = "cbim:requirementOf";

	/**
	 * cbim:requirement
	 */
	public static final String CBIM_REQUIREMENT_RELATION = "cbim:requirement";

	/**
	 * cbim:secondaryOrientation
	 */
	public static final String CBIM_SECONDARY_ORIENTATION = "cbim:secondaryOrientation";

	/**
	 * cbim:shape
	 */
	public static final String CBIM_SHAPE = "cbim:shape";

	/**
	 * cbim:situates 
	 */
	public static final String CBIM_SITUATES = "cbim:situates";

	/**
	 * cbim:Space
	 */
	public static final String CBIM_SPACE = "cbim:Space";

	/**
	 * cbim:spatialChild
	 */
	public static final String CBIM_SPATIAL_CHILD = "cbim:spatialChild";

	/**
	 * cbim:spatialParent 
	 */
	public static final String CBIM_SPATIAL_PARENT = "cbim:spatialParent";

	/**
	 * cbim:startDate
	 */
	public static final String CBIM_START_DATE = "cbim:startDate";

	/**
	 * cbim:startDateActual
	 */
	public static final String CBIM_START_DATE_ACTUAL = "cbim:startDateActual";

	/**
	 * cbim:startDatePlanned
	 */
	public static final String CBIM_START_DATE_PLANNED = "cbim:startDatePlanned";

	/**
	 * cbim:Task
	 */
	public static final String CBIM_TASK = "cbim:Task";

	/**
	 * cbim:TaskType
	 */
	public static final String CBIM_TASK_TYPE = "cbim:taskType";

	/**
	 * cbim:Terminal
	 */
	public static final String CBIM_TERMINAL = "cbim:Terminal";

	/**
	 * cbim:translation
	 */
	public static final String CBIM_TRANSLATION = "cbim:translation";

	/**
	 * cbim:userID
	 */
	public static final String CBIM_USER_ID = "cbim:userID";

	/**
	 * cbim:value
	 */
	public static final String CBIM_VALUE = "cbim:value";

	/**
	 * cbim:Vector
	 */
	public static final String CBIM_VECTOR = "cbim:Vector";

	/**
	 * cbim:xCoordinate
	 */
	public static final String CBIM_X_COORDINATE = "cbim:xCoordinate";

	/**
	 * cbim:yCoordinate
	 */
	public static final String CBIM_Y_COORDINATE = "cbim:yCoordinate";

	/**
	 * cbim:zCoordinate
	 */
	public static final String CBIM_Z_COORDINATE = "cbim:zCoordinate";
	
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
	public static final String PREFIX_CBIM = "cbim: <http://www.coinsweb.nl/c-bim.owl#>";

	/**
	 * PREFIX for sparql query
	 * cbimfs: <http://www.coinsweb.nl/c-bim-fs.owl#>
	 */
	public static final String PREFIX_CBIMFS = "cbimfs: <http://www.coinsweb.nl/c-bim-fs.owl#>";

	/**
	 * PREFIX for sparql query
	 * owl: <http://www.w3.org/2002/07/owl#>
	 */
	public static final String PREFIX_OWL = "owl: <http://www.w3.org/2002/07/owl#>";

	@Inject
	private ICoinsDateConversion dateConversion;
	
	@Inject
	private ConfigurationService mConfigurationService;
	
	@Inject
	private SparqlService sparqlService;
	
	@Override
	public void addAttributeDate(String context, String object, String name,
			String value) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		QueryBuilder builder = new InsertQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addPrefix(PREFIX_CBIMFS);
		builder.addGraph(getFullContext(context));
		builder.setId(object);
		builder.addAttributeDate(name, value);
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
	}
	
	@Override
	public void addAttributeFloat(String context, String object, String name,
			double value) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		QueryBuilder builder = new InsertQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addPrefix(PREFIX_CBIMFS);
		builder.addGraph(getFullContext(context));
		builder.setId(object);
		builder.addAttributeDouble(name, value);
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void addAttributeInt(String context, String object, String name,
			int value) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		QueryBuilder builder = new InsertQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addPrefix(PREFIX_CBIMFS);
		builder.addGraph(getFullContext(context));
		builder.setId(object);
		builder.addAttributeInteger(name, value);
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
	}
	
	@Override
	public void addAttributeResource(String context, String object,
			String name, String value) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		QueryBuilder builder = new InsertQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addPrefix(PREFIX_CBIMFS);
		builder.addGraph(getFullContext(context));
		builder.setId(object);
		builder.addAttributeLink(name, value);
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void addAttributeString(String context, String object, String name,
			String value) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		QueryBuilder builder = new InsertQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addPrefix(PREFIX_CBIMFS);
		builder.addGraph(getFullContext(context));
		builder.setId(object);
		builder.addAttributeString(name, value);
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public String createAmount(String context, String modelURI, String name,
			String userID, int value, String catalogPart, String creator)
			throws MarmottaException, InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException {
		String id = modelURI + "#" + java.util.UUID.randomUUID().toString();		
		InsertQueryBuilder builder = new InsertQueryBuilder(dateConversion);
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
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public String createCataloguePart(String context, String modelURI,
			String name, String userID, String creator)
			throws MarmottaException, InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException {
		String id = modelURI + "#" + java.util.UUID.randomUUID().toString();		
		InsertQueryBuilder builder = new InsertQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CBIM_NAME, name);
		builder.addAttributeString(CBIM_USER_ID, userID);
		builder.addAttributeDate(CBIM_CREATION_DATE, new Date());
		builder.addAttributeLink(CBIM_CREATOR, creator);
		builder.addAttributeString(A, CBIM_CATALOGUE_PART);
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public String createDocument(String context, String modelURI, String name,
			String userID, String creator) throws MarmottaException,
			InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException {
		String id = modelURI + "#" + java.util.UUID.randomUUID().toString();		
		InsertQueryBuilder builder = new InsertQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CBIM_NAME, name);
		builder.addAttributeDate(CBIM_CREATION_DATE, new Date());
		builder.addAttributeString(CBIM_USER_ID, userID);
		builder.addAttributeLink(CBIM_CREATOR, creator);
		builder.addAttributeString(A, CBIM_DOCUMENT);
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public String createExplicit3DRepresentation(String context,
			String modelURI, String name, String documentType,
			String documentAliasFilePath, String documentUri, String creator)
					throws MarmottaException, InvalidArgumentException,
					MalformedQueryException, UpdateExecutionException {
		String id = modelURI + "#" + java.util.UUID.randomUUID().toString();		
		InsertQueryBuilder builder = new InsertQueryBuilder(dateConversion);
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
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public String createFunction(String context, String modelURI, String name,
			int layerIndex, String userID, String creator)
			throws MarmottaException, InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException {
		String id = modelURI + "#" + java.util.UUID.randomUUID().toString();		
		InsertQueryBuilder builder = new InsertQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CBIM_NAME, name);
		builder.addAttributeDate(CBIM_CREATION_DATE, new Date());
		builder.addAttributeString(CBIM_USER_ID, userID);
		builder.addAttributeInteger(CBIM_LAYER_INDEX, layerIndex);
		builder.addAttributeLink(CBIM_CREATOR, creator);
		builder.addAttributeString(A, CBIM_FUNCTION);
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public String createLocator(String context, String modelURI, String name,
			String primaryOrientation, String secondaryOrientation,
			String translation, String creator) throws MarmottaException,
			InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException {
		String id = modelURI + "#" + java.util.UUID.randomUUID().toString();		
		InsertQueryBuilder builder = new InsertQueryBuilder(dateConversion);
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
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public String createNonFunctionalRequirement(String context,
			String modelURI, String name, int layerIndex, String userID,
			String creator, String nonFunctionalRequirementType)
			throws MarmottaException, InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException {
		String id = modelURI + "#" + java.util.UUID.randomUUID().toString();		
		InsertQueryBuilder builder = new InsertQueryBuilder(dateConversion);
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
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public String createParameter(String context, String modelURI, String name,
			String userID, String defaultValue, String creator)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		String id = modelURI + "#" + java.util.UUID.randomUUID().toString();		
		InsertQueryBuilder builder = new InsertQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CBIM_NAME, name);
		builder.addAttributeDate(CBIM_CREATION_DATE, new Date());
		builder.addAttributeString(CBIM_USER_ID, userID);
		builder.addAttributeLink(CBIM_CREATOR, creator);
		builder.addAttributeString(CBIM_DEFAULT_VALUE, defaultValue);
		builder.addAttributeString(A, CBIM_PARAMETER);
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}
	
	@Override
	public String createPersonOrOrganisation(String context, String modelURI, String name)
			throws MarmottaException, InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException {
		String id = modelURI + "#" + java.util.UUID.randomUUID().toString();		
		InsertQueryBuilder builder = new InsertQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CBIM_NAME, name);
		builder.addAttributeDate(CBIM_CREATION_DATE, new Date());
		builder.addAttributeString(A, CBIM_PERSON_OR_ORGANISATION);
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}
	
	@Override
	public String createPhysicalObject(String context, String modelURI,
			String name, int layerIndex, String userID, String creator)
			throws MarmottaException, InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException {
		String id = modelURI + "#" + java.util.UUID.randomUUID().toString();		
		InsertQueryBuilder builder = new InsertQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CBIM_NAME, name);
		builder.addAttributeDate(CBIM_CREATION_DATE, new Date());
		builder.addAttributeString(CBIM_USER_ID, userID);
		builder.addAttributeInteger(CBIM_LAYER_INDEX, layerIndex);
		builder.addAttributeLink(CBIM_CREATOR, creator);		
		builder.addAttributeString(A, CBIM_PHYSICAL_OBJECT);
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public String createRequirement(String context, String modelURI, String name,
			int layerindex, String userId,String creator, String requirementOf) throws MarmottaException, InvalidArgumentException, MalformedQueryException, UpdateExecutionException {		
		String id = modelURI + "#" + java.util.UUID.randomUUID().toString();
		InsertQueryBuilder builder = new InsertQueryBuilder(dateConversion);
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
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public String createSpace(String context, String modelURI, String name,
			int layerIndex, String userID, String creator)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		String id = modelURI + "#" + java.util.UUID.randomUUID().toString();		
		InsertQueryBuilder builder = new InsertQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CBIM_NAME, name);
		builder.addAttributeDate(CBIM_CREATION_DATE, new Date());
		builder.addAttributeString(CBIM_USER_ID, userID);
		builder.addAttributeInteger(CBIM_LAYER_INDEX, layerIndex);
		builder.addAttributeLink(CBIM_CREATOR, creator);		
		builder.addAttributeString(A, CBIM_SPACE);
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public String createTask(String context, String modelURI, String name,
			String[] affects, String userID, String taskType,
			String startDatePlanned, String endDatePlanned, String creator)
			throws MarmottaException, InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException {
		String id = modelURI + "#" + java.util.UUID.randomUUID().toString();		
		InsertQueryBuilder builder = new InsertQueryBuilder(dateConversion);
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
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public String createTerminal(String context, String modelURI, String name,
			String userID, String locator, int layerindex, String creator)
					throws InvalidArgumentException, MalformedQueryException,
					UpdateExecutionException, MarmottaException {
		String id = modelURI + "#" + java.util.UUID.randomUUID().toString();
		InsertQueryBuilder builder = new InsertQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CBIM_NAME, name);
		builder.addAttributeString(CBIM_USER_ID, userID);
		builder.addAttributeDate(CBIM_CREATION_DATE, new Date());
		builder.addAttributeInteger(CBIM_LAYER_INDEX, layerindex);
		builder.addAttributeLink(CBIM_CREATOR, creator);
		builder.addAttributeString(A, CBIM_TERMINAL);
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public String createVector(String context, String modelURI, String name,
			Double xCoordinate, Double yCoordinate, Double zCoordinate,
			String creator) throws MarmottaException, InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException {
		String id = modelURI + "#" + java.util.UUID.randomUUID().toString();		
		InsertQueryBuilder builder = new InsertQueryBuilder(dateConversion);
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
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
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
			sparqlService.update(QueryLanguage.SPARQL, query);
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
		InsertQueryBuilder builder = new InsertQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_OWL);
		builder.addGraph(getFullContext(context));
		builder.setId(modelUri);
		builder.addAttributeString("owl:versionInfo", "Created with Marmotta COINS module");
		builder.addAttributeLink(OWL_IMPORTS, "http://www.coinsweb.nl/c-bim.owl");
		builder.addAttributeLink(OWL_IMPORTS, "http://www.coinsweb.nl/c-bim-fs.owl");
		builder.addAttributeString(A, "owl:Ontology");
		
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
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
	public void linkDocument(String context, String physicalobject,
			String[] document, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		QueryBuilder builder = new InsertQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(physicalobject);
		for (String docId : document) {
			builder.addAttributeLink(CBIM_DOCUMENT_RELATION, docId);
		}
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
		// The previous query might have caused two modificationDates/modifiers
		// so...
		builder = new UpdateQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(physicalobject);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void linkFunctionIsFulfilledBy(String context, String function,
			String[] fulfilledby, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		QueryBuilder builder = new InsertQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(function);
		for (String physicalObjectId : fulfilledby) {
			builder.addAttributeLink(CBIM_IS_FULFILLED_BY, physicalObjectId);
		}
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
		// The previous query might have caused two modificationDates/modifiers
		// so...
		builder = new UpdateQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(function);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void linkIsAffectedBy(String context, String functionfulfiller,
			String isAffectedBy, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		// TODO one query?
		QueryBuilder builder = new InsertQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(functionfulfiller);
		builder.addAttributeLink(CBIM_IS_AFFECTED_BY, isAffectedBy);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);
		sparqlService.update(QueryLanguage.SPARQL, builder.build());

		builder = new UpdateQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(functionfulfiller);
		builder.addAttributeLink(CBIM_IS_AFFECTED_BY, isAffectedBy);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);		
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void linkNonFunctionalRequirement(String context,
			String functionfulfiller, String[] nonfunctionalrequirement,
			String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		QueryBuilder builder = new InsertQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addPrefix(PREFIX_CBIMFS);
		builder.addGraph(getFullContext(context));
		builder.setId(functionfulfiller);
		for (String nfr : nonfunctionalrequirement) {
			builder.addAttributeLink(CBIMFS_NON_FUNCTIONAL_REQUIREMENT_RELATION, nfr);
		}
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
		// The previous query might have caused two modificationDates/modifiers
		// so...
		builder = new UpdateQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(functionfulfiller);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void linkPhysicalObjectFulfills(String context,
			String physicalobject, String[] fulfills, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		QueryBuilder builder = new InsertQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(physicalobject);
		for (String function : fulfills) {
			builder.addAttributeLink(CBIM_FULFILLS, function);
		}
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
		// The previous query might have caused two modificationDates/modifiers
		// so...
		builder = new UpdateQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(physicalobject);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void setDescription(String context, String id, String description, String modifier) throws InvalidArgumentException, MalformedQueryException, UpdateExecutionException, MarmottaException {
		// Only inserting the description may cause duplicate descriptions.
		// Only updating the description does not work if the description was not present yet.
		// Maybe someone is able to do this with one query. That would be more efficient...
		// TODO one query?
		QueryBuilder builder = new InsertQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CBIM_DESCRIPTION, description);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);
		sparqlService.update(QueryLanguage.SPARQL, builder.build());

		builder = new UpdateQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CBIM_DESCRIPTION, description);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);		
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void setFirstParameter(String context,
			String explicit3dRepresentation, String firstParameter,
			String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		// TODO one query?
		QueryBuilder builder = new InsertQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(explicit3dRepresentation);
		builder.addAttributeLink(CBIM_FIRST_PARAMETER, firstParameter);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);
		sparqlService.update(QueryLanguage.SPARQL, builder.build());

		builder = new UpdateQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(explicit3dRepresentation);
		builder.addAttributeLink(CBIM_FIRST_PARAMETER, firstParameter);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);		
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void setNextParameter(String context, String parameter,
			String nextParameter, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		// TODO one query?
		QueryBuilder builder = new InsertQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(parameter);
		builder.addAttributeLink(CBIM_NEXT_PARAMETER, nextParameter);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);
		sparqlService.update(QueryLanguage.SPARQL, builder.build());

		builder = new UpdateQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(parameter);
		builder.addAttributeLink(CBIM_NEXT_PARAMETER, nextParameter);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);		
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void setPysicalChild(String context, String child, String parent,
			String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		QueryBuilder builder = new InsertQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(parent);
		builder.addAttributeLink(CBIM_PHYSICAL_CHILD, child);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);
		sparqlService.update(QueryLanguage.SPARQL, builder.build());		
	}

	@Override
	public void setPysicalParent(String context, String child, String parent, String modifier) throws InvalidArgumentException, MalformedQueryException, UpdateExecutionException, MarmottaException {
		// By definition a child can only have one Physical Parent (contrary to the real world where we usually have two parents)
		// So either one needs to be inserted or an already existing one needs to be updated...
		// TODO one query?
		QueryBuilder builder = new InsertQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(child);
		builder.addAttributeLink(CBIM_PHYSICAL_PARENT, parent);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);
		sparqlService.update(QueryLanguage.SPARQL, builder.build());

		builder = new UpdateQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(child);
		builder.addAttributeLink(CBIM_PHYSICAL_PARENT, parent);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);		
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void setShape(String context, String physicalobject, String shape,
			String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		// TODO one query?
		QueryBuilder builder = new InsertQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(physicalobject);
		builder.addAttributeLink(CBIM_SHAPE, shape);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);
		sparqlService.update(QueryLanguage.SPARQL, builder.build());

		builder = new UpdateQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(physicalobject);
		builder.addAttributeLink(CBIM_PHYSICAL_PARENT, shape);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);		
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void setSpatialChild(String context, String child, String parent,
			String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		QueryBuilder builder = new InsertQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(parent);
		builder.addAttributeLink(CBIM_SPATIAL_CHILD, child);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);
		sparqlService.update(QueryLanguage.SPARQL, builder.build());		
	}

	@Override
	public void setSpatialParent(String context, String child, String parent,
			String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		// By definition a child can only have one Spatial Parent (contrary to the real world where we usually have two parents)
		// So either one needs to be inserted or an already existing one needs to be updated...
		// TODO one query?
		QueryBuilder builder = new InsertQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(child);
		builder.addAttributeLink(CBIM_SPATIAL_PARENT, parent);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);
		sparqlService.update(QueryLanguage.SPARQL, builder.build());

		builder = new UpdateQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(child);
		builder.addAttributeLink(CBIM_SPATIAL_PARENT, parent);
		builder.addAttributeDate(CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CBIM_MODIFIER, modifier);		
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public List<String> validate(String pContext, ValidationAspect aspect) {
		CoinsValidator validator = null;
		switch (aspect) {
		case ALL:
			validator = new CoinsAllValidator();
			break;
		case PHYSICALPARENT:
			validator = new CoinsPhysicalParentValidator();
			break;
		case FUNCTIONFULFILLERS:
			validator = new CoinsFunctionFulfillerValidator();
			break;
		case LITERALS:
			validator = new CoinsLiteralValidator();
			break;
		case PHYSICALOBJECT_PARENT_CHILD:
			validator = new CoinsPhysicalObjectParentChildValidator();
			break;
		case SPACE_PARENT_CHILD:
			validator = new CoinsSpaceParentChildValidator();
			break;
		case AFFECTS:
			validator = new CoinsAffectsValidator();
			break;			
		case SITUATES:
			validator = new CoinsSituatedValidator();
			break;			
		case REQUIREMENT:
			validator = new CoinsRequirementValidator();
			break;			
		}
		validator.setContext(getFullContext(pContext));
		validator.setSparqlService(sparqlService);
		if (validator.validate()) {
			List<String> result = new Vector<String>();
			result.add(OK);
			return result;
		}
		return validator.getValidationErrors();
	}

}

 