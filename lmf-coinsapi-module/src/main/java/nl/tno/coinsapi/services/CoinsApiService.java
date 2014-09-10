package nl.tno.coinsapi.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.inject.Inject;

import nl.tno.coinsapi.CoinsFormat;
import nl.tno.coinsapi.tools.CoinsValidator;
import nl.tno.coinsapi.tools.CoinsValidatorFactory;
import nl.tno.coinsapi.tools.QueryBuilder;
import nl.tno.coinsapi.tools.QueryBuilder.FieldType;
import nl.tno.coinsapi.tools.QueryBuilder.InsertQueryBuilder;
import nl.tno.coinsapi.tools.QueryBuilder.UpdateQueryBuilder;
import nl.tno.coinsapi.tools.ValidationAspect;

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
	 * OK
	 */
	public static final String OK = "OK";

	/**
	 * owl:imports
	 */
	public static final String OWL_IMPORTS = "owl:imports";

	@Inject
	private ConfigurationService mConfigurationService;

	@Inject
	private ICoinsDateConversion mDateConversion;

	@Inject
	private SparqlService mSparqlService;

	@Override
	public void addAttributeDate(String context, String object, String name,
			String value) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		QueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsFormat.PREFIX_CBIM);
		builder.addPrefix(CoinsFormat.PREFIX_CBIMFS);
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
		builder.addPrefix(CoinsFormat.PREFIX_CBIM);
		builder.addPrefix(CoinsFormat.PREFIX_CBIMFS);
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
		builder.addPrefix(CoinsFormat.PREFIX_CBIM);
		builder.addPrefix(CoinsFormat.PREFIX_CBIMFS);
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
		builder.addPrefix(CoinsFormat.PREFIX_CBIM);
		builder.addPrefix(CoinsFormat.PREFIX_CBIMFS);
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
		builder.addPrefix(CoinsFormat.PREFIX_CBIM);
		builder.addPrefix(CoinsFormat.PREFIX_CBIMFS);
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
		builder.addPrefix(CoinsFormat.PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CoinsFormat.CBIM_NAME, name);
		builder.addAttributeString(CoinsFormat.CBIM_USER_ID, userID);
		builder.addAttributeString(CoinsFormat.CBIM_CATALOGUE_PART_RELATION, catalogPart);
		builder.addAttributeInteger(CoinsFormat.CBIM_VALUE, value);
		builder.addAttributeDate(CoinsFormat.CBIM_CREATION_DATE, new Date());
		builder.addAttributeLink(CoinsFormat.CBIM_CREATOR, creator);
		builder.addAttributeType(CoinsFormat.CBIM_AMOUNT);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public boolean deleteAmount(String context, String id) {
		return deleteItem(context, id, CoinsFormat.CBIM_AMOUNT, CoinsFormat.PREFIX_CBIM);
	}

	@Override
	public String getAmountQuery(String context, String id) {
		return getSelectQuery(context, id, CoinsFormat.CBIM_AMOUNT, CoinsFormat.PREFIX_CBIM);
	}

	@Override
	public String createCataloguePart(String context, String modelURI,
			String name, String userID, String creator)
			throws MarmottaException, InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException {
		String id = constructId(modelURI);
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsFormat.PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CoinsFormat.CBIM_NAME, name);
		builder.addAttributeString(CoinsFormat.CBIM_USER_ID, userID);
		builder.addAttributeDate(CoinsFormat.CBIM_CREATION_DATE, new Date());
		builder.addAttributeLink(CoinsFormat.CBIM_CREATOR, creator);
		builder.addAttributeType(CoinsFormat.CBIM_CATALOGUE_PART);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public boolean deleteCataloguePart(String context, String id) {
		return deleteItem(context, id, CoinsFormat.CBIM_CATALOGUE_PART, CoinsFormat.PREFIX_CBIM);
	}

	@Override
	public String getCataloguePartQuery(String context, String id) {
		return getSelectQuery(context, id, CoinsFormat.CBIM_CATALOGUE_PART, CoinsFormat.PREFIX_CBIM);
	}

	@Override
	public String createConnection(String context, String modelURI,
			String name, String userID, String creator)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		String id = constructId(modelURI);
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsFormat.PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CoinsFormat.CBIM_NAME, name);
		builder.addAttributeString(CoinsFormat.CBIM_USER_ID, userID);
		builder.addAttributeDate(CoinsFormat.CBIM_CREATION_DATE, new Date());
		builder.addAttributeLink(CoinsFormat.CBIM_CREATOR, creator);
		builder.addAttributeType(CoinsFormat.CBIM_CONNECTION);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public boolean deleteConnection(String context, String id) {
		return deleteItem(context, id, CoinsFormat.CBIM_CONNECTION, CoinsFormat.PREFIX_CBIM);
	}

	@Override
	public String getConnectionQuery(String context, String id) {
		return getSelectQuery(context, id, CoinsFormat.CBIM_CONNECTION, CoinsFormat.PREFIX_CBIM);
	}

	@Override
	public String createDocument(String context, String modelURI, String name,
			String userID, String creator) throws MarmottaException,
			InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException {
		String id = constructId(modelURI);
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsFormat.PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CoinsFormat.CBIM_NAME, name);
		builder.addAttributeDate(CoinsFormat.CBIM_CREATION_DATE, new Date());
		builder.addAttributeString(CoinsFormat.CBIM_USER_ID, userID);
		builder.addAttributeLink(CoinsFormat.CBIM_CREATOR, creator);
		builder.addAttributeType(CoinsFormat.CBIM_DOCUMENT);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public boolean deleteDocument(String context, String id) {
		return deleteItem(context, id, CoinsFormat.CBIM_DOCUMENT, CoinsFormat.PREFIX_CBIM);
	}

	@Override
	public String getDocumentQuery(String context, String id) {
		return getSelectQuery(context, id, CoinsFormat.CBIM_DOCUMENT, CoinsFormat.PREFIX_CBIM);
	}

	@Override
	public String createExplicit3DRepresentation(String context,
			String modelURI, String name, String documentType,
			String documentAliasFilePath, String documentUri, String creator)
			throws MarmottaException, InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException {
		String id = constructId(modelURI);
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsFormat.PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CoinsFormat.CBIM_NAME, name);
		builder.addAttributeDate(CoinsFormat.CBIM_CREATION_DATE, new Date());
		builder.addAttributeString(CoinsFormat.CBIM_DOCUMENT_TYPE, documentType);
		builder.addAttributeString(CoinsFormat.CBIM_DOCUMENT_ALIAS_FILE_PATH,
				documentAliasFilePath);
		builder.addAttributeLink(CoinsFormat.CBIM_DOCUMENT_URI, documentUri);
		builder.addAttributeLink(CoinsFormat.CBIM_CREATOR, creator);
		builder.addAttributeType(CoinsFormat.CBIM_EXPLICIT3D_REPRESENTATION);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public boolean deleteExplicit3DRepresentation(String context, String id) {
		return deleteItem(context, id, CoinsFormat.CBIM_EXPLICIT3D_REPRESENTATION,
				CoinsFormat.PREFIX_CBIM);
	}

	@Override
	public String getExplicit3DRepresentationQuery(String context, String id) {
		return getSelectQuery(context, id, CoinsFormat.CBIM_EXPLICIT3D_REPRESENTATION,
				CoinsFormat.PREFIX_CBIM);
	}

	@Override
	public String createFunction(String context, String modelURI, String name,
			int layerIndex, String userID, String creator)
			throws MarmottaException, InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException {
		String id = constructId(modelURI);
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsFormat.PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CoinsFormat.CBIM_NAME, name);
		builder.addAttributeDate(CoinsFormat.CBIM_CREATION_DATE, new Date());
		builder.addAttributeString(CoinsFormat.CBIM_USER_ID, userID);
		builder.addAttributeInteger(CoinsFormat.CBIM_LAYER_INDEX, layerIndex);
		builder.addAttributeLink(CoinsFormat.CBIM_CREATOR, creator);
		builder.addAttributeType(CoinsFormat.CBIM_FUNCTION);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public boolean deleteFunction(String context, String id) {
		return deleteItem(context, id, CoinsFormat.CBIM_FUNCTION, CoinsFormat.PREFIX_CBIM);
	}

	@Override
	public String getFunctionQuery(String context, String id) {
		return getSelectQuery(context, id, CoinsFormat.CBIM_FUNCTION, CoinsFormat.PREFIX_CBIM);
	}

	@Override
	public String createLocator(String context, String modelURI, String name,
			String primaryOrientation, String secondaryOrientation,
			String translation, String creator) throws MarmottaException,
			InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException {
		String id = constructId(modelURI);
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsFormat.PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CoinsFormat.CBIM_NAME, name);
		builder.addAttributeDate(CoinsFormat.CBIM_CREATION_DATE, new Date());
		builder.addAttributeLink(CoinsFormat.CBIM_PRIMARY_ORIENTATION, primaryOrientation);
		builder.addAttributeLink(CoinsFormat.CBIM_SECONDARY_ORIENTATION,
				secondaryOrientation);
		builder.addAttributeLink(CoinsFormat.CBIM_TRANSLATION, translation);
		builder.addAttributeLink(CoinsFormat.CBIM_CREATOR, creator);
		builder.addAttributeType(CoinsFormat.CBIM_LOCATOR);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public boolean deleteLocator(String context, String id) {
		return deleteItem(context, id, CoinsFormat.CBIM_LOCATOR, CoinsFormat.PREFIX_CBIM);
	}

	@Override
	public String getLocatorQuery(String context, String id) {
		return getSelectQuery(context, id, CoinsFormat.CBIM_LOCATOR, CoinsFormat.PREFIX_CBIM);
	}

	@Override
	public String createNonFunctionalRequirement(String context,
			String modelURI, String name, int layerIndex, String userID,
			String creator, String nonFunctionalRequirementType)
			throws MarmottaException, InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException {
		String id = constructId(modelURI);
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsFormat.PREFIX_CBIM);
		builder.addPrefix(CoinsFormat.PREFIX_CBIMFS);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CoinsFormat.CBIM_NAME, name);
		builder.addAttributeString(CoinsFormat.CBIM_USER_ID, userID);
		builder.addAttributeDate(CoinsFormat.CBIM_CREATION_DATE, new Date());
		builder.addAttributeInteger(CoinsFormat.CBIM_LAYER_INDEX, layerIndex);
		builder.addAttributeLink(CoinsFormat.CBIM_CREATOR, creator);
		if (isURI(nonFunctionalRequirementType)) {
			builder.addAttributeLink(CoinsFormat.CBIMFS_NON_FUNCTIONAL_REQUIREMENT_TYPE,
					nonFunctionalRequirementType);
		} else {
			builder.addAttributeString(CoinsFormat.CBIMFS_NON_FUNCTIONAL_REQUIREMENT_TYPE,
					nonFunctionalRequirementType);
		}
		builder.addAttributeType(CoinsFormat.CBIMFS_NON_FUNCTIONAL_REQUIREMENT);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public boolean deleteNonFunctionalRequirement(String context, String id) {
		return deleteItem(context, id, CoinsFormat.CBIMFS_NON_FUNCTIONAL_REQUIREMENT,
				CoinsFormat.PREFIX_CBIMFS);
	}

	@Override
	public String getNonFunctionalRequirementQuery(String context, String id) {
		return getSelectQuery(context, id, CoinsFormat.CBIMFS_NON_FUNCTIONAL_REQUIREMENT,
				CoinsFormat.PREFIX_CBIMFS);
	}

	@Override
	public String createParameter(String context, String modelURI, String name,
			String userID, String defaultValue, String creator)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		String id = constructId(modelURI);
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsFormat.PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CoinsFormat.CBIM_NAME, name);
		builder.addAttributeDate(CoinsFormat.CBIM_CREATION_DATE, new Date());
		builder.addAttributeString(CoinsFormat.CBIM_USER_ID, userID);
		builder.addAttributeLink(CoinsFormat.CBIM_CREATOR, creator);
		builder.addAttributeString(CoinsFormat.CBIM_DEFAULT_VALUE, defaultValue);
		builder.addAttributeType(CoinsFormat.CBIM_PARAMETER);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public boolean deleteParameter(String context, String id) {
		return deleteItem(context, id, CoinsFormat.CBIM_PARAMETER, CoinsFormat.PREFIX_CBIM);
	}

	@Override
	public String getParameterQuery(String context, String id) {
		return getSelectQuery(context, id, CoinsFormat.CBIM_PARAMETER, CoinsFormat.PREFIX_CBIM);
	}

	@Override
	public String createPerformance(String context, String modelURI,
			String name, String userID, String creator)
			throws MarmottaException, InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException {
		String id = constructId(modelURI);
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsFormat.PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CoinsFormat.CBIM_NAME, name);
		builder.addAttributeString(CoinsFormat.CBIM_USER_ID, userID);
		builder.addAttributeDate(CoinsFormat.CBIM_CREATION_DATE, new Date());
		builder.addAttributeLink(CoinsFormat.CBIM_CREATOR, creator);
		builder.addAttributeType(CoinsFormat.CBIM_PERFORMANCE);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public boolean deletePerformance(String context, String id) {
		return deleteItem(context, id, CoinsFormat.CBIM_PERFORMANCE, CoinsFormat.PREFIX_CBIM);
	}

	@Override
	public String getPerformanceQuery(String context, String id) {
		return getSelectQuery(context, id, CoinsFormat.CBIM_PERFORMANCE, CoinsFormat.PREFIX_CBIM);
	}

	@Override
	public String createPersonOrOrganisation(String context, String modelURI,
			String name) throws MarmottaException, InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException {
		String id = constructId(modelURI);
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsFormat.PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CoinsFormat.CBIM_NAME, name);
		builder.addAttributeDate(CoinsFormat.CBIM_CREATION_DATE, new Date());
		builder.addAttributeType(CoinsFormat.CBIM_PERSON_OR_ORGANISATION);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public boolean deletePersonOrOrganisation(String context, String id) {
		return deleteItem(context, id, CoinsFormat.CBIM_PERSON_OR_ORGANISATION, CoinsFormat.PREFIX_CBIM);
	}

	@Override
	public String getPersonOrOrganisationQuery(String context, String id) {
		return getSelectQuery(context, id, CoinsFormat.CBIM_PERSON_OR_ORGANISATION,
				CoinsFormat.PREFIX_CBIM);
	}

	@Override
	public String createPhysicalObject(String context, String modelURI,
			String name, int layerIndex, String userID, String creator)
			throws MarmottaException, InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException {
		String id = constructId(modelURI);
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsFormat.PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CoinsFormat.CBIM_NAME, name);
		builder.addAttributeDate(CoinsFormat.CBIM_CREATION_DATE, new Date());
		builder.addAttributeString(CoinsFormat.CBIM_USER_ID, userID);
		builder.addAttributeInteger(CoinsFormat.CBIM_LAYER_INDEX, layerIndex);
		builder.addAttributeLink(CoinsFormat.CBIM_CREATOR, creator);
		builder.addAttributeType(CoinsFormat.CBIM_PHYSICAL_OBJECT);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public String getPhysicalObjectQuery(String context, String id) {
		return getSelectQuery(context, id, CoinsFormat.CBIM_PHYSICAL_OBJECT, CoinsFormat.PREFIX_CBIM);
	}

	@Override
	public boolean deletePhysicalObject(String context, String id) {
		return deleteItem(context, id, CoinsFormat.CBIM_PHYSICAL_OBJECT, CoinsFormat.PREFIX_CBIM);
	}

	@Override
	public String createPropertyType(String context, String modelURI,
			String name, String userID, String unit, String valuedomain,
			String creator) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		String id = constructId(modelURI);
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsFormat.PREFIX_CBIM);
		builder.addPrefix(CoinsFormat.PREFIX_CBIMFS);
		builder.addPrefix(CoinsFormat.PREFIX_CBIMOTL);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CoinsFormat.CBIM_NAME, name);
		builder.addAttributeString(CoinsFormat.CBIM_USER_ID, userID);
		builder.addAttributeString(CoinsFormat.CBIM_UNIT, unit);
		builder.addAttributeLink(CoinsFormat.CBIM_VALUE_DOMAIN_REFERENCE, valuedomain);
		builder.addAttributeDate(CoinsFormat.CBIM_CREATION_DATE, new Date());
		builder.addAttributeLink(CoinsFormat.CBIM_CREATOR, creator);
		builder.addAttributeType(CoinsFormat.CBIM_PROPERTY_TYPE);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public boolean deletePropertyType(String context, String id) {
		return deleteItem(context, id, CoinsFormat.CBIM_PROPERTY_TYPE, CoinsFormat.PREFIX_CBIM);
	}

	@Override
	public String getPropertyTypeQuery(String context, String id) {
		return getSelectQuery(context, id, CoinsFormat.CBIM_PROPERTY_TYPE, CoinsFormat.PREFIX_CBIM);
	}

	@Override
	public String createPropertyValue(String context, String modelURI,
			String name, String userID, String propertytype, String value,
			String creator) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		String id = constructId(modelURI);
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsFormat.PREFIX_CBIM);
		builder.addPrefix(CoinsFormat.PREFIX_CBIMFS);
		builder.addPrefix(CoinsFormat.PREFIX_CBIMOTL);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CoinsFormat.CBIM_NAME, name);
		builder.addAttributeString(CoinsFormat.CBIM_USER_ID, userID);
		builder.addAttributeLink(CoinsFormat.CBIM_PROPERTY_TYPE_RELATION, propertytype);
		builder.addAttribute(CoinsFormat.CBIM_VALUE, value, getFieldType(propertytype, context));
		builder.addAttributeDate(CoinsFormat.CBIM_CREATION_DATE, new Date());
		builder.addAttributeLink(CoinsFormat.CBIM_CREATOR, creator);
		builder.addAttributeType(CoinsFormat.CBIM_PROPERTY_VALUE);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public boolean deletePropertyValue(String context, String id) {
		return deleteItem(context, id, CoinsFormat.CBIM_PROPERTY_VALUE, CoinsFormat.PREFIX_CBIM);
	}

	@Override
	public String getPropertyValueQuery(String context, String id) {
		return getSelectQuery(context, id, CoinsFormat.CBIM_PROPERTY_VALUE, CoinsFormat.PREFIX_CBIM);
	}

	@Override
	public String createRequirement(String context, String modelURI,
			String name, int layerindex, String userId, String creator,
			String requirementOf) throws MarmottaException,
			InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException {
		String id = constructId(modelURI);
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsFormat.PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CoinsFormat.CBIM_NAME, name);
		builder.addAttributeString(CoinsFormat.CBIM_USER_ID, userId);
		builder.addAttributeDate(CoinsFormat.CBIM_CREATION_DATE, new Date());
		builder.addAttributeInteger(CoinsFormat.CBIM_LAYER_INDEX, layerindex);
		builder.addAttributeLink(CoinsFormat.CBIM_CREATOR, creator);
		builder.addAttributeLink(CoinsFormat.CBIM_REQUIREMENT_OF, requirementOf);
		builder.addAttributeType(CoinsFormat.CBIM_REQUIREMENT);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public boolean deleteRequirement(String context, String id) {
		return deleteItem(context, id, CoinsFormat.CBIM_REQUIREMENT, CoinsFormat.PREFIX_CBIM);
	}

	@Override
	public String getRequirementQuery(String context, String id) {
		return getSelectQuery(context, id, CoinsFormat.CBIM_REQUIREMENT, CoinsFormat.PREFIX_CBIM);
	}

	@Override
	public String createSpace(String context, String modelURI, String name,
			int layerIndex, String userID, String creator)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		String id = constructId(modelURI);
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsFormat.PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CoinsFormat.CBIM_NAME, name);
		builder.addAttributeDate(CoinsFormat.CBIM_CREATION_DATE, new Date());
		builder.addAttributeString(CoinsFormat.CBIM_USER_ID, userID);
		builder.addAttributeInteger(CoinsFormat.CBIM_LAYER_INDEX, layerIndex);
		builder.addAttributeLink(CoinsFormat.CBIM_CREATOR, creator);
		builder.addAttributeType(CoinsFormat.CBIM_SPACE);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public boolean deleteSpace(String context, String id) {
		return deleteItem(context, id, CoinsFormat.CBIM_SPACE, CoinsFormat.PREFIX_CBIM);
	}

	@Override
	public String getSpaceQuery(String context, String id) {
		return getSelectQuery(context, id, CoinsFormat.CBIM_SPACE, CoinsFormat.PREFIX_CBIM);
	}

	@Override
	public String createState(String context, String modelURI, String name,
			String userID, String creator) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		String id = constructId(modelURI);
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsFormat.PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CoinsFormat.CBIM_NAME, name);
		builder.addAttributeString(CoinsFormat.CBIM_USER_ID, userID);
		builder.addAttributeDate(CoinsFormat.CBIM_CREATION_DATE, new Date());
		builder.addAttributeLink(CoinsFormat.CBIM_CREATOR, creator);
		builder.addAttributeType(CoinsFormat.CBIM_STATE);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public boolean deleteState(String context, String id) {
		return deleteItem(context, id, CoinsFormat.CBIM_STATE, CoinsFormat.PREFIX_CBIM);
	}

	@Override
	public String getStateQuery(String context, String id) {
		return getSelectQuery(context, id, CoinsFormat.CBIM_STATE, CoinsFormat.PREFIX_CBIM);
	}

	@Override
	public String createTask(String context, String modelURI, String name,
			String[] affects, String userID, String taskType,
			String startDatePlanned, String endDatePlanned, String creator)
			throws MarmottaException, InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException {
		String id = constructId(modelURI);
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsFormat.PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CoinsFormat.CBIM_NAME, name);
		builder.addAttributeDate(CoinsFormat.CBIM_CREATION_DATE, new Date());
		builder.addAttributeDate(CoinsFormat.CBIM_START_DATE_PLANNED, startDatePlanned);
		builder.addAttributeDate(CoinsFormat.CBIM_END_DATE_PLANNED, endDatePlanned);
		builder.addAttributeString(CoinsFormat.CBIM_TASK_TYPE, taskType);
		for (String physicalObjecId : affects) {
			builder.addAttributeLink(CoinsFormat.CBIM_AFFECTS, physicalObjecId);
		}
		builder.addAttributeLink(CoinsFormat.CBIM_CREATOR, creator);
		builder.addAttributeType(CoinsFormat.CBIM_TASK);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public boolean deleteTask(String context, String id) {
		return deleteItem(context, id, CoinsFormat.CBIM_TASK, CoinsFormat.PREFIX_CBIM);
	}

	@Override
	public String getTaskQuery(String context, String id) {
		return getSelectQuery(context, id, CoinsFormat.CBIM_TASK, CoinsFormat.PREFIX_CBIM);
	}

	@Override
	public String createTerminal(String context, String modelURI, String name,
			String userID, String locator, int layerindex, String creator)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		String id = constructId(modelURI);
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsFormat.PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CoinsFormat.CBIM_NAME, name);
		builder.addAttributeString(CoinsFormat.CBIM_USER_ID, userID);
		builder.addAttributeDate(CoinsFormat.CBIM_CREATION_DATE, new Date());
		builder.addAttributeInteger(CoinsFormat.CBIM_LAYER_INDEX, layerindex);
		builder.addAttributeLink(CoinsFormat.CBIM_CREATOR, creator);
		builder.addAttributeLink(CoinsFormat.CBIM_LOCATOR_RELATION, locator);
		builder.addAttributeType(CoinsFormat.CBIM_TERMINAL);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public boolean deleteTerminal(String context, String id) {
		return deleteItem(context, id, CoinsFormat.CBIM_TERMINAL, CoinsFormat.PREFIX_CBIM);
	}

	@Override
	public String getTerminalQuery(String context, String id) {
		return getSelectQuery(context, id, CoinsFormat.CBIM_TERMINAL, CoinsFormat.PREFIX_CBIM);
	}

	@Override
	public String createVector(String context, String modelURI, String name,
			Double xCoordinate, Double yCoordinate, Double zCoordinate,
			String creator) throws MarmottaException, InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException {
		String id = constructId(modelURI);
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsFormat.PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CoinsFormat.CBIM_NAME, name);
		builder.addAttributeDate(CoinsFormat.CBIM_CREATION_DATE, new Date());
		builder.addAttributeDouble(CoinsFormat.CBIM_X_COORDINATE, xCoordinate);
		builder.addAttributeDouble(CoinsFormat.CBIM_Y_COORDINATE, yCoordinate);
		builder.addAttributeDouble(CoinsFormat.CBIM_Z_COORDINATE, zCoordinate);
		builder.addAttributeLink(CoinsFormat.CBIM_CREATOR, creator);
		builder.addAttributeType(CoinsFormat.CBIM_VECTOR);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public boolean deleteVector(String context, String id) {
		return deleteItem(context, id, CoinsFormat.CBIM_VECTOR, CoinsFormat.PREFIX_CBIM);
	}

	@Override
	public String getVectorQuery(String context, String id) {
		return getSelectQuery(context, id, CoinsFormat.CBIM_VECTOR, CoinsFormat.PREFIX_CBIM);
	}

	@Override
	public void initializeContext(String context, String modelUri)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsFormat.PREFIX_OWL);
		builder.addGraph(getFullContext(context));
		builder.setId(modelUri);
		builder.addAttributeString("owl:versionInfo",
				"Created with Marmotta COINS module");
		builder.addAttributeLink(OWL_IMPORTS,
				"http://www.coinsweb.nl/cbim-1.1.owl");
		builder.addAttributeLink(OWL_IMPORTS,
				"http://www.coinsweb.nl/c-bim-fs.owl");
		builder.addAttributeType("owl:Ontology");

		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void linkBoundingBox(String context, String cbim_id, 
			String boundingBox, String locator, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		updateAttribute(context, locator, cbim_id, boundingBox, modifier,
				FieldType.RESOURCE);
	}

	@Override
	public void linkDocument(String context, String physicalobject,
			String[] document, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		QueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsFormat.PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(physicalobject);
		for (String docId : document) {
			builder.addAttributeLink(CoinsFormat.CBIM_DOCUMENT_RELATION, docId);
		}
		builder.addAttributeDate(CoinsFormat.CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CoinsFormat.CBIM_MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		// The previous query might have caused two modificationDates/modifiers
		// so...
		builder = new UpdateQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsFormat.PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(physicalobject);
		builder.addAttributeDate(CoinsFormat.CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CoinsFormat.CBIM_MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void linkFunctionIsFulfilledBy(String context, String function,
			String[] fulfilledby, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		QueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsFormat.PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(function);
		for (String physicalObjectId : fulfilledby) {
			builder.addAttributeLink(CoinsFormat.CBIM_IS_FULFILLED_BY, physicalObjectId);
		}
		builder.addAttributeDate(CoinsFormat.CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CoinsFormat.CBIM_MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		// The previous query might have caused two modificationDates/modifiers
		// so...
		builder = new UpdateQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsFormat.PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(function);
		builder.addAttributeDate(CoinsFormat.CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CoinsFormat.CBIM_MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void linkIsAffectedBy(String context, String functionfulfiller,
			String isAffectedBy, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		updateAttribute(context, functionfulfiller, CoinsFormat.CBIM_IS_AFFECTED_BY,
				isAffectedBy, modifier, FieldType.RESOURCE);
	}

	@Override
	public void linkLocator(String context, String object, String locator,
			String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		updateAttribute(context, object, CoinsFormat.CBIM_LOCATOR_RELATION, locator,
				modifier, FieldType.RESOURCE);
	}

	@Override
	public void linkNonFunctionalRequirement(String context,
			String functionfulfiller, String[] nonfunctionalrequirement,
			String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		QueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsFormat.PREFIX_CBIM);
		builder.addPrefix(CoinsFormat.PREFIX_CBIMFS);
		builder.addGraph(getFullContext(context));
		builder.setId(functionfulfiller);
		for (String nfr : nonfunctionalrequirement) {
			builder.addAttributeLink(
					CoinsFormat.CBIMFS_NON_FUNCTIONAL_REQUIREMENT_RELATION, nfr);
		}
		builder.addAttributeDate(CoinsFormat.CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CoinsFormat.CBIM_MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		// The previous query might have caused two modificationDates/modifiers
		// so...
		builder = new UpdateQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsFormat.PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(functionfulfiller);
		builder.addAttributeDate(CoinsFormat.CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CoinsFormat.CBIM_MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void linkPropertyValue(String context, String performance,
			String propertyvalue, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		updateAttribute(context, performance, CoinsFormat.CBIMFS_PROPERTY_VALUE_RELATION, propertyvalue, modifier, FieldType.RESOURCE);		
	}

	@Override
	public void linkPerformanceOf(String context, String performance,
			String performanceof, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		updateAttribute(context, performance, CoinsFormat.CBIM_PERFORMANCE_OF, performanceof, modifier, FieldType.RESOURCE);		
	}

	@Override
	public void linkPerformance(String context, String object,
			String performance, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		QueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsFormat.PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(object);
		builder.addAttributeLink(CoinsFormat.CBIM_PERFORMANCE_RELATION, performance);
		builder.addAttributeDate(CoinsFormat.CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CoinsFormat.CBIM_MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		// The previous query might have caused two modificationDates/modifiers
		// so...
		builder = new UpdateQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsFormat.PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(object);
		builder.addAttributeDate(CoinsFormat.CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CoinsFormat.CBIM_MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void linkPhysicalObjectFulfills(String context,
			String physicalobject, String[] fulfills, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		QueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsFormat.PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(physicalobject);
		for (String function : fulfills) {
			builder.addAttributeLink(CoinsFormat.CBIM_FULFILLS, function);
		}
		builder.addAttributeDate(CoinsFormat.CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CoinsFormat.CBIM_MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		// The previous query might have caused two modificationDates/modifiers
		// so...
		builder = new UpdateQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsFormat.PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(physicalobject);
		builder.addAttributeDate(CoinsFormat.CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CoinsFormat.CBIM_MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void linkTerminal(String context, String cbim_id, String terminal,
			String connection, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		updateAttribute(context, connection, cbim_id, terminal, modifier,
				FieldType.RESOURCE);
	}

	@Override
	public void setCurrentState(String context, String state,
			String functionfulfiller, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		updateAttribute(context, functionfulfiller, CoinsFormat.CBIM_CURRENT_STATE, state,
				modifier, FieldType.RESOURCE);
	}

	@Override
	public void setDescription(String context, String id, String description,
			String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		updateAttribute(context, id, CoinsFormat.CBIM_DESCRIPTION, description, modifier,
				FieldType.STRING);
	}

	@Override
	public void setLayerIndex(String context, String id, int layerindex,
			String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		updateAttribute(context, id, CoinsFormat.CBIM_LAYER_INDEX,
				String.valueOf(layerindex), modifier, FieldType.INT);
	}

	@Override
	public void setFirstParameter(String context,
			String explicit3dRepresentation, String firstParameter,
			String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		updateAttribute(context, explicit3dRepresentation,
				CoinsFormat.CBIM_FIRST_PARAMETER, firstParameter, modifier,
				FieldType.RESOURCE);
	}

	@Override
	public void setNextParameter(String context, String parameter,
			String nextParameter, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		updateAttribute(context, parameter, CoinsFormat.CBIM_NEXT_PARAMETER, nextParameter,
				modifier, FieldType.RESOURCE);
	}

	@Override
	public void setPreviousState(String context, String state,
			String previousstate, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		updateAttribute(context, state, CoinsFormat.CBIM_PREVIOUS_STATE, previousstate,
				modifier, FieldType.RESOURCE);
	}

	@Override
	public void setPysicalChild(String context, String child, String parent,
			String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		QueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsFormat.PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(parent);
		builder.addAttributeLink(CoinsFormat.CBIM_PHYSICAL_CHILD, child);
		builder.addAttributeDate(CoinsFormat.CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CoinsFormat.CBIM_MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void setPysicalParent(String context, String child, String parent,
			String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		updateAttribute(context, child, CoinsFormat.CBIM_PHYSICAL_PARENT, parent, modifier,
				FieldType.RESOURCE);
	}

	@Override
	public void setShape(String context, String physicalobject, String shape,
			String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		updateAttribute(context, physicalobject, CoinsFormat.CBIM_SHAPE, shape, modifier,
				FieldType.RESOURCE);
	}

	@Override
	public void setSpatialChild(String context, String child, String parent,
			String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		QueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsFormat.PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(parent);
		builder.addAttributeLink(CoinsFormat.CBIM_SPATIAL_CHILD, child);
		builder.addAttributeDate(CoinsFormat.CBIM_MODIFICATION_DATE, new Date());
		builder.addAttributeLink(CoinsFormat.CBIM_MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void setSpatialParent(String context, String child, String parent,
			String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		updateAttribute(context, child, CoinsFormat.CBIM_SPATIAL_PARENT, parent, modifier,
				FieldType.RESOURCE);
	}

	@Override
	public void setStateOf(String context, String state,
			String functionfulfiller, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		updateAttribute(context, state, CoinsFormat.CBIM_STATE_OF, functionfulfiller,
				modifier, FieldType.RESOURCE);
	}

	@Override
	public List<String> validate(String pContext, ValidationAspect aspect) {
		CoinsValidator validator = CoinsValidatorFactory.getValidator(aspect,
				pContext, mSparqlService);
		if (validator.validate()) {
			List<String> result = new Vector<String>();
			result.add(OK);
			return result;
		}
		return validator.getValidationErrors();
	}

	private String constructId(String modelURI) {
		return modelURI + "#m" + java.util.UUID.randomUUID().toString();
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

	private String getSelectQuery(String context, String id, String type,
			String prefix) {
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

	private boolean deleteItem(String context, String id, String type,
			String prefix) {
		String query = "PREFIX " + prefix + "\nDELETE WHERE { GRAPH <"
				+ getFullContext(context) + "> { <" + id
				+ "> ?name ?value ; a " + type + " }}";
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

	/**
	 * If the attribute does not exist, it will be added
	 * 
	 * @param context
	 * @param objectId
	 * @param argumentIdentifier
	 * @param argumentValue
	 * @param modifier
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 * @throws MarmottaException
	 */
	private void updateAttribute(String context, String objectId,
			String argumentIdentifier, String argumentValue, String modifier,
			FieldType fieldType) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		// TODO one query?
		for (QueryBuilder builder : new QueryBuilder[] {
				new InsertQueryBuilder(mDateConversion),
				new UpdateQueryBuilder(mDateConversion) }) {
			builder.addPrefix(CoinsFormat.PREFIX_CBIM);
			if (argumentIdentifier.startsWith("cbimfs")) {
				builder.addPrefix(CoinsFormat.PREFIX_CBIMFS);
			}
			builder.addGraph(getFullContext(context));
			builder.setId(objectId);
			builder.addAttribute(argumentIdentifier, argumentValue, fieldType);
			builder.addAttributeDate(CoinsFormat.CBIM_MODIFICATION_DATE,
					new Date());
			builder.addAttributeLink(CoinsFormat.CBIM_MODIFIER, modifier);
			mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		}

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

	private FieldType getFieldType(String pPropertytype, String pContext)
			throws MarmottaException {
		StringBuilder query = new StringBuilder();
		query.append("PREFIX ");
		query.append(CoinsFormat.PREFIX_CBIM);
		query.append("\n\nSELECT ?valueType WHERE {\n\tGRAPH <");
		query.append(getFullContext(pContext));
		query.append("> {\n\t\t<");
		query.append(pPropertytype);
		query.append("> ?name ?valueType ;\n\t\t\ta ");
		query.append(CoinsFormat.CBIM_PROPERTY_TYPE);
		query.append("; ");
		query.append(CoinsFormat.CBIM_VALUE_DOMAIN_REFERENCE);
		query.append(" ?valueType \n\t}\n}");
		List<Map<String, Value>> result = mSparqlService.query(
				QueryLanguage.SPARQL, query.toString());
		if (!result.isEmpty()) {
			String dataType = result.get(0).get("valueType").toString();
			if (dataType.contains("XsdString")) {
				return FieldType.STRING;
			}
			if (dataType.contains("XsdBoolean")) {
				return FieldType.BOOLEAN;
			}
			if (dataType.contains("XsdFloat")) {
				return FieldType.DOUBLE;
			}
			if (dataType.contains("XsdInt")) {
				return FieldType.INT;
			}
			if (dataType.contains("CbimCataloguePart")
					|| dataType.contains("CbimDocument")
					|| dataType.contains("CbimParameter")) {
				return FieldType.RESOURCE;
			}
			if (dataType.contains("XsdDateTime")) {
				return FieldType.DATE;
			}
		}
		return FieldType.STRING;
	}

}
