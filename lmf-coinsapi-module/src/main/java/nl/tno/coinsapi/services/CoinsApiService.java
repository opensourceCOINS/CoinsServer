package nl.tno.coinsapi.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.inject.Inject;

import nl.tno.coinsapi.CoinsPrefix;
import nl.tno.coinsapi.keys.AttributeKey;
import nl.tno.coinsapi.keys.CbimAttributeKey;
import nl.tno.coinsapi.keys.CbimObjectKey;
import nl.tno.coinsapi.keys.CbimfsAttributeKey;
import nl.tno.coinsapi.keys.CbimfsObjectKey;
import nl.tno.coinsapi.keys.CbimotlAttributeKey;
import nl.tno.coinsapi.keys.IAttributeKey;
import nl.tno.coinsapi.keys.IObjectKey;
import nl.tno.coinsapi.keys.OwlAttributeKey;
import nl.tno.coinsapi.keys.OwlObjectKey;
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
		builder.addPrefix(CoinsPrefix.CBIM);
		builder.addPrefix(CoinsPrefix.CBIMFS);
		builder.addGraph(getFullContext(context));
		builder.setId(object);
		builder.addAttributeDate(new AttributeKey(name), value);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void addAttributeFloat(String context, String object, String name,
			double value) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		QueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsPrefix.CBIM);
		builder.addPrefix(CoinsPrefix.CBIMFS);
		builder.addGraph(getFullContext(context));
		builder.setId(object);
		builder.addAttributeDouble(new AttributeKey(name), value);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void addAttributeInt(String context, String object, String name,
			int value) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		QueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsPrefix.CBIM);
		builder.addPrefix(CoinsPrefix.CBIMFS);
		builder.addGraph(getFullContext(context));
		builder.setId(object);
		builder.addAttributeInteger(new AttributeKey(name), value);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void addAttributeResource(String context, String object,
			String name, String value) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		QueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsPrefix.CBIM);
		builder.addPrefix(CoinsPrefix.CBIMFS);
		builder.addGraph(getFullContext(context));
		builder.setId(object);
		builder.addAttributeResource(new AttributeKey(name), value);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void addAttributeString(String context, String object, String name,
			String value) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		QueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsPrefix.CBIM);
		builder.addPrefix(CoinsPrefix.CBIMFS);
		builder.addGraph(getFullContext(context));
		builder.setId(object);
		builder.addAttributeString(new AttributeKey(name), value);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public String createAmount(String context, String name, String userID,
			int value, String catalogPart, String creator)
			throws MarmottaException, InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException {
		String id = constructId(context);
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsPrefix.CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CbimAttributeKey.NAME, name);
		builder.addAttributeString(CbimAttributeKey.USER_ID, userID);
		builder.addAttributeString(CbimAttributeKey.CATALOGUE_PART_RELATION,
				catalogPart);
		builder.addAttributeInteger(CbimAttributeKey.VALUE, value);
		builder.addAttributeDate(CbimAttributeKey.CREATION_DATE, new Date());
		builder.addAttributeResource(CbimAttributeKey.CREATOR, creator);
		builder.addAttributeType(CbimObjectKey.AMOUNT);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public boolean deleteAmount(String context, String id) {
		return deleteItem(context, id, CbimObjectKey.AMOUNT,
				CoinsPrefix.CBIM);
	}

	@Override
	public String getAmountQuery(String context, String id) {
		return getSelectQuery(context, id, CbimObjectKey.AMOUNT,
				CoinsPrefix.CBIM);
	}

	@Override
	public String createCataloguePart(String context, String name,
			String userID, String creator) throws MarmottaException,
			InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException {
		String id = constructId(context);
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsPrefix.CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CbimAttributeKey.NAME, name);
		builder.addAttributeString(CbimAttributeKey.USER_ID, userID);
		builder.addAttributeDate(CbimAttributeKey.CREATION_DATE, new Date());
		builder.addAttributeResource(CbimAttributeKey.CREATOR, creator);
		builder.addAttributeType(CbimObjectKey.CATALOGUE_PART);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public boolean deleteCataloguePart(String context, String id) {
		return deleteItem(context, id, CbimObjectKey.CATALOGUE_PART,
				CoinsPrefix.CBIM);
	}

	@Override
	public String getCataloguePartQuery(String context, String id) {
		return getSelectQuery(context, id, CbimObjectKey.CATALOGUE_PART,
				CoinsPrefix.CBIM);
	}

	@Override
	public String createConnection(String context, String name, String userID,
			String creator) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		String id = constructId(context);
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsPrefix.CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CbimAttributeKey.NAME, name);
		builder.addAttributeString(CbimAttributeKey.USER_ID, userID);
		builder.addAttributeDate(CbimAttributeKey.CREATION_DATE, new Date());
		builder.addAttributeResource(CbimAttributeKey.CREATOR, creator);
		builder.addAttributeType(CbimObjectKey.CONNECTION);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public boolean deleteConnection(String context, String id) {
		return deleteItem(context, id, CbimObjectKey.CONNECTION,
				CoinsPrefix.CBIM);
	}

	@Override
	public String getConnectionQuery(String context, String id) {
		return getSelectQuery(context, id, CbimObjectKey.CONNECTION,
				CoinsPrefix.CBIM);
	}

	@Override
	public String createDocument(String context, String name, String userID,
			String creator) throws MarmottaException, InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException {
		String id = constructId(context);
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsPrefix.CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CbimAttributeKey.NAME, name);
		builder.addAttributeDate(CbimAttributeKey.CREATION_DATE, new Date());
		builder.addAttributeString(CbimAttributeKey.USER_ID, userID);
		builder.addAttributeResource(CbimAttributeKey.CREATOR, creator);
		builder.addAttributeType(CbimObjectKey.DOCUMENT);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public boolean deleteDocument(String context, String id) {
		return deleteItem(context, id, CbimObjectKey.DOCUMENT,
				CoinsPrefix.CBIM);
	}

	@Override
	public String getDocumentQuery(String context, String id) {
		return getSelectQuery(context, id, CbimObjectKey.DOCUMENT,
				CoinsPrefix.CBIM);
	}

	@Override
	public String createExplicit3DRepresentation(String context, String name,
			String documentType, String documentAliasFilePath,
			String documentUri, String creator) throws MarmottaException,
			InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException {
		String id = constructId(context);
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsPrefix.CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CbimAttributeKey.NAME, name);
		builder.addAttributeDate(CbimAttributeKey.CREATION_DATE, new Date());
		builder.addAttributeString(CbimAttributeKey.DOCUMENT_TYPE, documentType);
		builder.addAttributeString(CbimAttributeKey.DOCUMENT_ALIAS_FILE_PATH,
				documentAliasFilePath);
		builder.addAttributeResource(CbimAttributeKey.DOCUMENT_URI, documentUri);
		builder.addAttributeResource(CbimAttributeKey.CREATOR, creator);
		builder.addAttributeType(CbimObjectKey.EXPLICIT3D_REPRESENTATION);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public boolean deleteExplicit3DRepresentation(String context, String id) {
		return deleteItem(context, id, CbimObjectKey.EXPLICIT3D_REPRESENTATION,
				CoinsPrefix.CBIM);
	}

	@Override
	public String getExplicit3DRepresentationQuery(String context, String id) {
		return getSelectQuery(context, id,
				CbimObjectKey.EXPLICIT3D_REPRESENTATION,
				CoinsPrefix.CBIM);
	}

	@Override
	public String createFunction(String context, String name, int layerIndex,
			String userID, String creator) throws MarmottaException,
			InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException {
		String id = constructId(context);
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsPrefix.CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CbimAttributeKey.NAME, name);
		builder.addAttributeDate(CbimAttributeKey.CREATION_DATE, new Date());
		builder.addAttributeString(CbimAttributeKey.USER_ID, userID);
		builder.addAttributeInteger(CbimAttributeKey.LAYER_INDEX, layerIndex);
		builder.addAttributeResource(CbimAttributeKey.CREATOR, creator);
		builder.addAttributeType(CbimObjectKey.FUNCTION);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public boolean deleteFunction(String context, String id) {
		return deleteItem(context, id, CbimObjectKey.FUNCTION,
				CoinsPrefix.CBIM);
	}

	@Override
	public String getFunctionQuery(String context, String id) {
		return getSelectQuery(context, id, CbimObjectKey.FUNCTION,
				CoinsPrefix.CBIM);
	}

	@Override
	public String createLocator(String context, String name,
			String primaryOrientation, String secondaryOrientation,
			String translation, String creator) throws MarmottaException,
			InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException {
		String id = constructId(context);
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsPrefix.CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CbimAttributeKey.NAME, name);
		builder.addAttributeDate(CbimAttributeKey.CREATION_DATE, new Date());
		builder.addAttributeResource(CbimAttributeKey.PRIMARY_ORIENTATION,
				primaryOrientation);
		builder.addAttributeResource(CbimAttributeKey.SECONDARY_ORIENTATION,
				secondaryOrientation);
		builder.addAttributeResource(CbimAttributeKey.TRANSLATION, translation);
		builder.addAttributeResource(CbimAttributeKey.CREATOR, creator);
		builder.addAttributeType(CbimObjectKey.LOCATOR);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public boolean deleteLocator(String context, String id) {
		return deleteItem(context, id, CbimObjectKey.LOCATOR,
				CoinsPrefix.CBIM);
	}

	@Override
	public String getLocatorQuery(String context, String id) {
		return getSelectQuery(context, id, CbimObjectKey.LOCATOR,
				CoinsPrefix.CBIM);
	}

	@Override
	public String createNonFunctionalRequirement(String context, String name,
			int layerIndex, String userID, String creator,
			String nonFunctionalRequirementType) throws MarmottaException,
			InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException {
		String id = constructId(context);
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsPrefix.CBIM);
		builder.addPrefix(CoinsPrefix.CBIMFS);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CbimAttributeKey.NAME, name);
		builder.addAttributeString(CbimAttributeKey.USER_ID, userID);
		builder.addAttributeDate(CbimAttributeKey.CREATION_DATE, new Date());
		builder.addAttributeInteger(CbimAttributeKey.LAYER_INDEX, layerIndex);
		builder.addAttributeResource(CbimAttributeKey.CREATOR, creator);
		if (isURI(nonFunctionalRequirementType)) {
			builder.addAttributeResource(
					CbimfsAttributeKey.NON_FUNCTIONAL_REQUIREMENT_TYPE,
					nonFunctionalRequirementType);
		} else {
			builder.addAttributeString(
					CbimfsAttributeKey.NON_FUNCTIONAL_REQUIREMENT_TYPE,
					nonFunctionalRequirementType);
		}
		builder.addAttributeType(CbimfsObjectKey.NON_FUNCTIONAL_REQUIREMENT);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public boolean deleteNonFunctionalRequirement(String context, String id) {
		return deleteItem(context, id,
				CbimfsObjectKey.NON_FUNCTIONAL_REQUIREMENT,
				CoinsPrefix.CBIMFS);
	}

	@Override
	public String getNonFunctionalRequirementQuery(String context, String id) {
		return getSelectQuery(context, id,
				CbimfsObjectKey.NON_FUNCTIONAL_REQUIREMENT,
				CoinsPrefix.CBIMFS);
	}

	@Override
	public String createParameter(String context, String name, String userID,
			String defaultValue, String creator)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		String id = constructId(context);
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsPrefix.CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CbimAttributeKey.NAME, name);
		builder.addAttributeDate(CbimAttributeKey.CREATION_DATE, new Date());
		builder.addAttributeString(CbimAttributeKey.USER_ID, userID);
		builder.addAttributeResource(CbimAttributeKey.CREATOR, creator);
		builder.addAttributeString(CbimAttributeKey.DEFAULT_VALUE, defaultValue);
		builder.addAttributeType(CbimObjectKey.PARAMETER);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public boolean deleteParameter(String context, String id) {
		return deleteItem(context, id, CbimObjectKey.PARAMETER,
				CoinsPrefix.CBIM);
	}

	@Override
	public String getParameterQuery(String context, String id) {
		return getSelectQuery(context, id, CbimObjectKey.PARAMETER,
				CoinsPrefix.CBIM);
	}

	@Override
	public String createPerformance(String context, String name, String userID,
			String creator) throws MarmottaException, InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException {
		String id = constructId(context);
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsPrefix.CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CbimAttributeKey.NAME, name);
		builder.addAttributeString(CbimAttributeKey.USER_ID, userID);
		builder.addAttributeDate(CbimAttributeKey.CREATION_DATE, new Date());
		builder.addAttributeResource(CbimAttributeKey.CREATOR, creator);
		builder.addAttributeType(CbimObjectKey.PERFORMANCE);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public boolean deletePerformance(String context, String id) {
		return deleteItem(context, id, CbimObjectKey.PERFORMANCE,
				CoinsPrefix.CBIM);
	}

	@Override
	public String getPerformanceQuery(String context, String id) {
		return getSelectQuery(context, id, CbimObjectKey.PERFORMANCE,
				CoinsPrefix.CBIM);
	}

	@Override
	public String createPersonOrOrganisation(String context, String name)
			throws MarmottaException, InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException {
		String id = constructId(context);
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsPrefix.CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CbimAttributeKey.NAME, name);
		builder.addAttributeDate(CbimAttributeKey.CREATION_DATE, new Date());
		builder.addAttributeType(CbimObjectKey.PERSON_OR_ORGANISATION);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public boolean deletePersonOrOrganisation(String context, String id) {
		return deleteItem(context, id, CbimObjectKey.PERSON_OR_ORGANISATION,
				CoinsPrefix.CBIM);
	}

	@Override
	public String getPersonOrOrganisationQuery(String context, String id) {
		return getSelectQuery(context, id,
				CbimObjectKey.PERSON_OR_ORGANISATION, CoinsPrefix.CBIM);
	}

	@Override
	public String createPhysicalObject(String context, String name,
			int layerIndex, String userID, String creator)
			throws MarmottaException, InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException {
		String id = constructId(context);
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsPrefix.CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CbimAttributeKey.NAME, name);
		builder.addAttributeDate(CbimAttributeKey.CREATION_DATE, new Date());
		builder.addAttributeString(CbimAttributeKey.USER_ID, userID);
		builder.addAttributeInteger(CbimAttributeKey.LAYER_INDEX, layerIndex);
		builder.addAttributeResource(CbimAttributeKey.CREATOR, creator);
		builder.addAttributeType(CbimObjectKey.PHYSICAL_OBJECT);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public String getPhysicalObjectQuery(String context, String id) {
		return getSelectQuery(context, id, CbimObjectKey.PHYSICAL_OBJECT,
				CoinsPrefix.CBIM);
	}

	@Override
	public boolean deletePhysicalObject(String context, String id) {
		return deleteItem(context, id, CbimObjectKey.PHYSICAL_OBJECT,
				CoinsPrefix.CBIM);
	}

	@Override
	public String createPropertyType(String context, String name,
			String userID, String unit, String valuedomain, String creator)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		String id = constructId(context);
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsPrefix.CBIM);
		builder.addPrefix(CoinsPrefix.CBIMFS);
		builder.addPrefix(CoinsPrefix.CBIMOTL);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CbimAttributeKey.NAME, name);
		builder.addAttributeString(CbimAttributeKey.USER_ID, userID);
		builder.addAttributeString(CbimAttributeKey.UNIT, unit);
		builder.addAttributeResource(CbimAttributeKey.VALUE_DOMAIN,
				valuedomain);
		builder.addAttributeDate(CbimAttributeKey.CREATION_DATE, new Date());
		builder.addAttributeResource(CbimAttributeKey.CREATOR, creator);
		builder.addAttributeType(CbimObjectKey.PROPERTY_TYPE);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public boolean deletePropertyType(String context, String id) {
		return deleteItem(context, id, CbimObjectKey.PROPERTY_TYPE,
				CoinsPrefix.CBIM);
	}

	@Override
	public String getPropertyTypeQuery(String context, String id) {
		return getSelectQuery(context, id, CbimObjectKey.PROPERTY_TYPE,
				CoinsPrefix.CBIM);
	}

	@Override
	public String createPropertyValue(String context, String name,
			String userID, String propertytype, String value, String creator)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		String id = constructId(context);
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsPrefix.CBIM);
		builder.addPrefix(CoinsPrefix.CBIMFS);
		builder.addPrefix(CoinsPrefix.CBIMOTL);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CbimAttributeKey.NAME, name);
		builder.addAttributeString(CbimAttributeKey.USER_ID, userID);
		builder.addAttributeResource(CbimAttributeKey.PROPERTY_TYPE,
				propertytype);
		builder.addAttribute(CbimAttributeKey.VALUE, value,
				getFieldType(propertytype, context));
		builder.addAttributeDate(CbimAttributeKey.CREATION_DATE, new Date());
		builder.addAttributeResource(CbimAttributeKey.CREATOR, creator);
		builder.addAttributeType(CbimObjectKey.PROPERTY_VALUE);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public boolean deletePropertyValue(String context, String id) {
		return deleteItem(context, id, CbimObjectKey.PROPERTY_VALUE,
				CoinsPrefix.CBIM);
	}

	@Override
	public String getPropertyValueQuery(String context, String id) {
		return getSelectQuery(context, id, CbimObjectKey.PROPERTY_VALUE,
				CoinsPrefix.CBIM);
	}

	@Override
	public String createRequirement(String context, String name,
			int layerindex, String userId, String creator, String requirementOf)
			throws MarmottaException, InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException {
		String id = constructId(context);
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsPrefix.CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CbimAttributeKey.NAME, name);
		builder.addAttributeString(CbimAttributeKey.USER_ID, userId);
		builder.addAttributeDate(CbimAttributeKey.CREATION_DATE, new Date());
		builder.addAttributeInteger(CbimAttributeKey.LAYER_INDEX, layerindex);
		builder.addAttributeResource(CbimAttributeKey.CREATOR, creator);
		builder.addAttributeResource(CbimAttributeKey.REQUIREMENT_OF,
				requirementOf);
		builder.addAttributeType(CbimObjectKey.REQUIREMENT);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public boolean deleteRequirement(String context, String id) {
		return deleteItem(context, id, CbimObjectKey.REQUIREMENT,
				CoinsPrefix.CBIM);
	}

	@Override
	public String getRequirementQuery(String context, String id) {
		return getSelectQuery(context, id, CbimObjectKey.REQUIREMENT,
				CoinsPrefix.CBIM);
	}

	@Override
	public String createSpace(String context, String name, int layerIndex,
			String userID, String creator) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		String id = constructId(context);
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsPrefix.CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CbimAttributeKey.NAME, name);
		builder.addAttributeDate(CbimAttributeKey.CREATION_DATE, new Date());
		builder.addAttributeString(CbimAttributeKey.USER_ID, userID);
		builder.addAttributeInteger(CbimAttributeKey.LAYER_INDEX, layerIndex);
		builder.addAttributeResource(CbimAttributeKey.CREATOR, creator);
		builder.addAttributeType(CbimObjectKey.SPACE);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public boolean deleteSpace(String context, String id) {
		return deleteItem(context, id, CbimObjectKey.SPACE,
				CoinsPrefix.CBIM);
	}

	@Override
	public String getSpaceQuery(String context, String id) {
		return getSelectQuery(context, id, CbimObjectKey.SPACE,
				CoinsPrefix.CBIM);
	}

	@Override
	public String createState(String context, String name, String userID,
			String creator) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		String id = constructId(context);
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsPrefix.CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CbimAttributeKey.NAME, name);
		builder.addAttributeString(CbimAttributeKey.USER_ID, userID);
		builder.addAttributeDate(CbimAttributeKey.CREATION_DATE, new Date());
		builder.addAttributeResource(CbimAttributeKey.CREATOR, creator);
		builder.addAttributeType(CbimObjectKey.STATE);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public boolean deleteState(String context, String id) {
		return deleteItem(context, id, CbimObjectKey.STATE,
				CoinsPrefix.CBIM);
	}

	@Override
	public String getStateQuery(String context, String id) {
		return getSelectQuery(context, id, CbimObjectKey.STATE,
				CoinsPrefix.CBIM);
	}

	@Override
	public String createTask(String context, String name, String[] affects,
			String userID, String taskType, String startDatePlanned,
			String endDatePlanned, String creator) throws MarmottaException,
			InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException {
		String id = constructId(context);
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsPrefix.CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CbimAttributeKey.NAME, name);
		builder.addAttributeDate(CbimAttributeKey.CREATION_DATE, new Date());
		builder.addAttributeDate(CbimAttributeKey.START_DATE_PLANNED,
				startDatePlanned);
		builder.addAttributeDate(CbimAttributeKey.END_DATE_PLANNED,
				endDatePlanned);
		builder.addAttributeString(CbimAttributeKey.TASK_TYPE, taskType);
		for (String physicalObjecId : affects) {
			builder.addAttributeResource(CbimAttributeKey.AFFECTS,
					physicalObjecId);
		}
		builder.addAttributeResource(CbimAttributeKey.CREATOR, creator);
		builder.addAttributeType(CbimObjectKey.TASK);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public boolean deleteTask(String context, String id) {
		return deleteItem(context, id, CbimObjectKey.TASK,
				CoinsPrefix.CBIM);
	}

	@Override
	public String getTaskQuery(String context, String id) {
		return getSelectQuery(context, id, CbimObjectKey.TASK,
				CoinsPrefix.CBIM);
	}

	@Override
	public String createTerminal(String context, String name, String userID,
			String locator, int layerindex, String creator)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		String id = constructId(context);
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsPrefix.CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CbimAttributeKey.NAME, name);
		builder.addAttributeString(CbimAttributeKey.USER_ID, userID);
		builder.addAttributeDate(CbimAttributeKey.CREATION_DATE, new Date());
		builder.addAttributeInteger(CbimAttributeKey.LAYER_INDEX, layerindex);
		builder.addAttributeResource(CbimAttributeKey.CREATOR, creator);
		builder.addAttributeResource(CbimAttributeKey.LOCATOR, locator);
		builder.addAttributeType(CbimObjectKey.TERMINAL);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public boolean deleteTerminal(String context, String id) {
		return deleteItem(context, id, CbimObjectKey.TERMINAL,
				CoinsPrefix.CBIM);
	}

	@Override
	public String getTerminalQuery(String context, String id) {
		return getSelectQuery(context, id, CbimObjectKey.TERMINAL,
				CoinsPrefix.CBIM);
	}

	@Override
	public String createVector(String context, String name, Double xCoordinate,
			Double yCoordinate, Double zCoordinate, String creator)
			throws MarmottaException, InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException {
		String id = constructId(context);
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsPrefix.CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CbimAttributeKey.NAME, name);
		builder.addAttributeDate(CbimAttributeKey.CREATION_DATE, new Date());
		builder.addAttributeDouble(CbimAttributeKey.X_COORDINATE, xCoordinate);
		builder.addAttributeDouble(CbimAttributeKey.Y_COORDINATE, yCoordinate);
		builder.addAttributeDouble(CbimAttributeKey.Z_COORDINATE, zCoordinate);
		builder.addAttributeResource(CbimAttributeKey.CREATOR, creator);
		builder.addAttributeType(CbimObjectKey.VECTOR);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public boolean deleteVector(String context, String id) {
		return deleteItem(context, id, CbimObjectKey.VECTOR,
				CoinsPrefix.CBIM);
	}

	@Override
	public String getVectorQuery(String context, String id) {
		return getSelectQuery(context, id, CbimObjectKey.VECTOR,
				CoinsPrefix.CBIM);
	}

	@Override
	public String createVerification(String context, String name,
			String userID, String verificationDate, String verificationMethod,
			boolean verificationResult, String creator)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		String id = constructId(context);
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsPrefix.CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CbimAttributeKey.NAME, name);
		builder.addAttributeString(CbimAttributeKey.USER_ID, userID);
		builder.addAttributeDate(CbimAttributeKey.CREATION_DATE, new Date());
		builder.addAttributeDate(CbimAttributeKey.VERIFICATION_DATE,
				verificationDate);
		builder.addAttributeString(CbimAttributeKey.VERIFICATION_METHOD,
				verificationMethod);
		builder.addAttributeBoolean(CbimAttributeKey.VERIFICATION_RESULT,
				verificationResult);
		builder.addAttributeResource(CbimAttributeKey.CREATOR, creator);
		builder.addAttributeType(CbimObjectKey.VERIFICATION);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public boolean deleteVerification(String context, String id) {
		return deleteItem(context, id, CbimObjectKey.VERIFICATION,
				CoinsPrefix.CBIM);
	}

	@Override
	public String getVerificationQuery(String context, String id) {
		return getSelectQuery(context, id, CbimObjectKey.VERIFICATION,
				CoinsPrefix.CBIM);
	}

	@Override
	public String initializeContext(String context, String modelUri)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		String existingModelUri = getModelURI(context);
		if (existingModelUri == null) {
			InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
			builder.addPrefix(CoinsPrefix.OWL);
			builder.addGraph(getFullContext(context));
			builder.setId(modelUri);
			builder.addAttributeString(OwlAttributeKey.VERSION_INFO,
					"Created with Marmotta COINS module");
			builder.addAttributeResource(OwlAttributeKey.IMPORTS,
					"http://www.coinsweb.nl/cbim-1.1.owl");
			builder.addAttributeResource(OwlAttributeKey.IMPORTS,
					"http://www.coinsweb.nl/c-bim-fs.owl");
			builder.addAttributeType(OwlObjectKey.ONTOLOGY);
			mSparqlService.update(QueryLanguage.SPARQL, builder.build());
			return null;
		}
		if (existingModelUri != modelUri) {
			return existingModelUri;
		}
		return null;
	}

	@Override
	public void addReferenceFrame(String context, String referenceFrame)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.setId(getModelURI(context));
		builder.addGraph(getFullContext(context));
		builder.addAttributeResource(OwlAttributeKey.IMPORTS, referenceFrame);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	private String getModelURI(String pContext) throws MarmottaException {
		String query = "SELECT ?modelURI WHERE { GRAPH <"
				+ getFullContext(pContext)
				+ "> { ?modelURI ?b []; a <http://www.w3.org/2002/07/owl#Ontology> }} LIMIT 1";
		List<Map<String, Value>> result = mSparqlService.query(
				QueryLanguage.SPARQL, query);
		if (result.isEmpty()) {
			return null;
		}
		return result.get(0).get("modelURI").stringValue();
	}

	@Override
	public void linkBoundingBox(String context, IAttributeKey cbim_id,
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
		builder.addPrefix(CoinsPrefix.CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(physicalobject);
		for (String docId : document) {
			builder.addAttributeResource(CbimAttributeKey.DOCUMENT,
					docId);
		}
		builder.addAttributeDate(CbimAttributeKey.MODIFICATION_DATE, new Date());
		builder.addAttributeResource(CbimAttributeKey.MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		// The previous query might have caused two modificationDates/modifiers
		// so...
		builder = new UpdateQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsPrefix.CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(physicalobject);
		builder.addAttributeDate(CbimAttributeKey.MODIFICATION_DATE, new Date());
		builder.addAttributeResource(CbimAttributeKey.MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void linkFunctionIsFulfilledBy(String context, String function,
			String[] fulfilledby, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		QueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsPrefix.CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(function);
		for (String physicalObjectId : fulfilledby) {
			builder.addAttributeResource(CbimAttributeKey.IS_FULFILLED_BY,
					physicalObjectId);
		}
		builder.addAttributeDate(CbimAttributeKey.MODIFICATION_DATE, new Date());
		builder.addAttributeResource(CbimAttributeKey.MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		// The previous query might have caused two modificationDates/modifiers
		// so...
		builder = new UpdateQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsPrefix.CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(function);
		builder.addAttributeDate(CbimAttributeKey.MODIFICATION_DATE, new Date());
		builder.addAttributeResource(CbimAttributeKey.MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void linkIsAffectedBy(String context, String functionfulfiller,
			String isAffectedBy, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		updateAttribute(context, functionfulfiller,
				CbimAttributeKey.IS_AFFECTED_BY, isAffectedBy, modifier,
				FieldType.RESOURCE);
	}

	@Override
	public void linkLocator(String context, String object, String locator,
			String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		updateAttribute(context, object, CbimAttributeKey.LOCATOR,
				locator, modifier, FieldType.RESOURCE);
	}

	@Override
	public void linkNonFunctionalRequirement(String context,
			String functionfulfiller, String[] nonfunctionalrequirement,
			String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		QueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsPrefix.CBIM);
		builder.addPrefix(CoinsPrefix.CBIMFS);
		builder.addGraph(getFullContext(context));
		builder.setId(functionfulfiller);
		for (String nfr : nonfunctionalrequirement) {
			builder.addAttributeResource(
					CbimfsAttributeKey.NON_FUNCTIONAL_REQUIREMENT, nfr);
		}
		builder.addAttributeDate(CbimAttributeKey.MODIFICATION_DATE, new Date());
		builder.addAttributeResource(CbimAttributeKey.MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		// The previous query might have caused two modificationDates/modifiers
		// so...
		builder = new UpdateQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsPrefix.CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(functionfulfiller);
		builder.addAttributeDate(CbimAttributeKey.MODIFICATION_DATE, new Date());
		builder.addAttributeResource(CbimAttributeKey.MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void linkPropertyValue(String context, String performance,
			String propertyvalue, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		updateAttribute(context, performance,
				CbimfsAttributeKey.PROPERTY_VALUE, propertyvalue,
				modifier, FieldType.RESOURCE);
	}

	@Override
	public void linkPerformanceOf(String context, String performance,
			String performanceof, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		updateAttribute(context, performance, CbimAttributeKey.PERFORMANCE_OF,
				performanceof, modifier, FieldType.RESOURCE);
	}

	@Override
	public void linkPerformance(String context, String object,
			String performance, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		QueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsPrefix.CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(object);
		builder.addAttributeResource(CbimAttributeKey.PERFORMANCE_RELATION,
				performance);
		builder.addAttributeDate(CbimAttributeKey.MODIFICATION_DATE, new Date());
		builder.addAttributeResource(CbimAttributeKey.MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		// The previous query might have caused two modificationDates/modifiers
		// so...
		builder = new UpdateQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsPrefix.CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(object);
		builder.addAttributeDate(CbimAttributeKey.MODIFICATION_DATE, new Date());
		builder.addAttributeResource(CbimAttributeKey.MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void linkPhysicalObjectFulfills(String context,
			String physicalobject, String[] fulfills, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		QueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsPrefix.CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(physicalobject);
		for (String function : fulfills) {
			builder.addAttributeResource(CbimAttributeKey.FULFILLS, function);
		}
		builder.addAttributeDate(CbimAttributeKey.MODIFICATION_DATE, new Date());
		builder.addAttributeResource(CbimAttributeKey.MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		// The previous query might have caused two modificationDates/modifiers
		// so...
		builder = new UpdateQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsPrefix.CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(physicalobject);
		builder.addAttributeDate(CbimAttributeKey.MODIFICATION_DATE, new Date());
		builder.addAttributeResource(CbimAttributeKey.MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void linkTerminal(String context, IAttributeKey cbim_id,
			String terminal, String connection, String modifier)
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
		updateAttribute(context, functionfulfiller,
				CbimAttributeKey.CURRENT_STATE, state, modifier,
				FieldType.RESOURCE);
	}

	@Override
	public void setDescription(String context, String id, String description,
			String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		updateAttribute(context, id, CbimAttributeKey.DESCRIPTION, description,
				modifier, FieldType.STRING);
	}

	@Override
	public void setLayerIndex(String context, String id, int layerindex,
			String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		updateAttribute(context, id, CbimAttributeKey.LAYER_INDEX,
				String.valueOf(layerindex), modifier, FieldType.INT);
	}

	@Override
	public void setFirstParameter(String context,
			String explicit3dRepresentation, String firstParameter,
			String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		updateAttribute(context, explicit3dRepresentation,
				CbimAttributeKey.FIRST_PARAMETER, firstParameter, modifier,
				FieldType.RESOURCE);
	}

	@Override
	public void setNextParameter(String context, String parameter,
			String nextParameter, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		updateAttribute(context, parameter, CbimAttributeKey.NEXT_PARAMETER,
				nextParameter, modifier, FieldType.RESOURCE);
	}

	@Override
	public void setPreviousState(String context, String state,
			String previousstate, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		updateAttribute(context, state, CbimAttributeKey.PREVIOUS_STATE,
				previousstate, modifier, FieldType.RESOURCE);
	}

	@Override
	public void setPysicalChild(String context, String child, String parent,
			String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		QueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsPrefix.CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(parent);
		builder.addAttributeResource(CbimAttributeKey.PHYSICAL_CHILD, child);
		builder.addAttributeDate(CbimAttributeKey.MODIFICATION_DATE, new Date());
		builder.addAttributeResource(CbimAttributeKey.MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void setPysicalParent(String context, String child, String parent,
			String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		updateAttribute(context, child, CbimAttributeKey.PHYSICAL_PARENT,
				parent, modifier, FieldType.RESOURCE);
	}

	@Override
	public void setShape(String context, String physicalobject, String shape,
			String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		updateAttribute(context, physicalobject, CbimAttributeKey.SHAPE, shape,
				modifier, FieldType.RESOURCE);
	}

	@Override
	public void setSpatialChild(String context, String child, String parent,
			String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		QueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsPrefix.CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(parent);
		builder.addAttributeResource(CbimAttributeKey.SPATIAL_CHILD, child);
		builder.addAttributeDate(CbimAttributeKey.MODIFICATION_DATE, new Date());
		builder.addAttributeResource(CbimAttributeKey.MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void setSpatialParent(String context, String child, String parent,
			String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		updateAttribute(context, child, CbimAttributeKey.SPATIAL_PARENT,
				parent, modifier, FieldType.RESOURCE);
	}

	@Override
	public void setStateOf(String context, String state,
			String functionfulfiller, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		updateAttribute(context, state, CbimAttributeKey.STATE_OF,
				functionfulfiller, modifier, FieldType.RESOURCE);
	}

	@Override
	public List<String> validate(String pContext, ValidationAspect aspect) {
		CoinsValidator validator = CoinsValidatorFactory.getValidator(aspect,
				getFullContext(pContext), mSparqlService);
		if (validator.validate()) {
			List<String> result = new Vector<String>();
			result.add(OK);
			return result;
		}
		return validator.getValidationErrors();
	}

	private String constructId(String pContext) throws MarmottaException {
		String modelURI = getModelURI(pContext);
		if (modelURI == null) {
			throw new MarmottaException(
					"Please initialize the context first and define a modelURI before creating COINS objects.");
		}
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

	private String getSelectQuery(String context, String id, IObjectKey type,
			CoinsPrefix prefix) {
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

	private boolean deleteItem(String context, String id, IObjectKey type,
			CoinsPrefix prefix) {
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
			IAttributeKey argumentIdentifier, String argumentValue,
			String modifier, FieldType fieldType)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		// TODO one query?
		for (QueryBuilder builder : new QueryBuilder[] {
				new InsertQueryBuilder(mDateConversion),
				new UpdateQueryBuilder(mDateConversion) }) {
			builder.addPrefix(CoinsPrefix.CBIM);
			if (argumentIdentifier.toString().startsWith("cbimfs")) {
				builder.addPrefix(CoinsPrefix.CBIMFS);
			}
			builder.addGraph(getFullContext(context));
			builder.setId(objectId);
			builder.addAttribute(argumentIdentifier, argumentValue, fieldType);
			builder.addAttributeDate(CbimAttributeKey.MODIFICATION_DATE,
					new Date());
			builder.addAttributeResource(CbimAttributeKey.MODIFIER, modifier);
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
		query.append(CoinsPrefix.CBIM);
		query.append("\n\nSELECT ?valueType WHERE {\n\tGRAPH <");
		query.append(getFullContext(pContext));
		query.append("> {\n\t\t<");
		query.append(pPropertytype);
		query.append("> ?name ?valueType ;\n\t\t\ta ");
		query.append(CbimObjectKey.PROPERTY_TYPE);
		query.append("; ");
		query.append(CbimAttributeKey.VALUE_DOMAIN);
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

	@Override
	public void setVerificationAttribute(String context, String verification,
			String objectId, IAttributeKey objectTypeId, String modifier,
			FieldType pFieldType) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		updateAttribute(context, verification, objectTypeId, objectId,
				modifier, pFieldType);
	}

	@Override
	public void linkIsSituatedIn(String context, String physicalObject,
			String space, String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		updateAttribute(context, physicalObject,
				CbimAttributeKey.IS_SITUATED_IN, space, modifier,
				FieldType.RESOURCE);
	}

	@Override
	public void linkSituates(String context, String[] physicalObject,
			String space, String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		QueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsPrefix.CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(space);
		for (String physicalObj : physicalObject) {
			builder.addAttributeResource(CbimAttributeKey.SITUATES, physicalObj);
		}
		builder.addAttributeDate(CbimAttributeKey.MODIFICATION_DATE, new Date());
		builder.addAttributeResource(CbimAttributeKey.MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		// The previous query might have caused two modificationDates/modifiers
		// so...
		builder = new UpdateQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsPrefix.CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(space);
		builder.addAttributeDate(CbimAttributeKey.MODIFICATION_DATE, new Date());
		builder.addAttributeResource(CbimAttributeKey.MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void updateAttributeString(String context, String object,
			String name, String value, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		updateAttribute(context, object, new AttributeKey(name), value,
				modifier, FieldType.STRING);
	}

	@Override
	public void updateAttributeResource(String context, String object,
			String name, String value, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		updateAttribute(context, object, new AttributeKey(name), value,
				modifier, FieldType.RESOURCE);
	}

	@Override
	public void updateAttributeFloat(String context, String object,
			String name, double value, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		updateAttribute(context, object, new AttributeKey(name),
				QueryBuilder.doubleToString(value), modifier, FieldType.DOUBLE);
	}

	@Override
	public void updateAttributeInt(String context, String object, String name,
			int value, String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		updateAttribute(context, object, new AttributeKey(name),
				Integer.toString(value), modifier, FieldType.INT);
	}

	@Override
	public void updateAttributeDate(String context, String object, String name,
			String value, String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		updateAttribute(context, object, new AttributeKey(name), value,
				modifier, FieldType.DATE);
	}

	@Override
	public void updateAttributeBoolean(String context, String object,
			String name, boolean value, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		updateAttribute(context, object, new AttributeKey(name),
				String.valueOf(value), modifier, FieldType.BOOLEAN);
	}

	@Override
	public void addAttributeBoolean(String context, String object, String name,
			boolean value) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		QueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsPrefix.CBIM);
		builder.addPrefix(CoinsPrefix.CBIMFS);
		builder.addGraph(getFullContext(context));
		builder.setId(object);
		builder.addAttributeBoolean(new AttributeKey(name), value);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public String createBaseline(String context, String name, int layerIndex,
			String userID, boolean baselineStatus, String creator)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		String id = constructId(context);
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsPrefix.CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CbimAttributeKey.NAME, name);
		builder.addAttributeDate(CbimAttributeKey.CREATION_DATE, new Date());
		builder.addAttributeString(CbimAttributeKey.USER_ID, userID);
		builder.addAttributeInteger(CbimAttributeKey.LAYER_INDEX, layerIndex);
		builder.addAttributeBoolean(CbimAttributeKey.BASELINE_STATUS,
				baselineStatus);
		builder.addAttributeResource(CbimAttributeKey.CREATOR, creator);
		builder.addAttributeType(CbimObjectKey.BASELINE);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public String getBaselineQuery(String context, String id) {
		return getSelectQuery(context, id, CbimObjectKey.BASELINE,
				CoinsPrefix.CBIM);
	}

	@Override
	public boolean deleteBaseline(String context, String id) {
		return deleteItem(context, id, CbimObjectKey.BASELINE,
				CoinsPrefix.CBIM);
	}

	@Override
	public void linkBaseline(String context, String cbimObject,
			String baseline, String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		updateAttribute(context, cbimObject,
				CbimAttributeKey.BASELINE, baseline, modifier,
				FieldType.RESOURCE);
	}

	@Override
	public void linkCbimObject(String context, String cbimObject,
			String baseline, String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		QueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsPrefix.CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(baseline);
		builder.addAttributeResource(CbimAttributeKey.BASELINE_OBJECT,
				cbimObject);
		builder.addAttributeDate(CbimAttributeKey.MODIFICATION_DATE, new Date());
		builder.addAttributeResource(CbimAttributeKey.MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		// The previous query might have caused two modificationDates/modifiers
		// so...
		builder = new UpdateQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsPrefix.CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(baseline);
		builder.addAttributeDate(CbimAttributeKey.MODIFICATION_DATE, new Date());
		builder.addAttributeResource(CbimAttributeKey.MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public String getLibraryReferenceQuery(String context, String id) {
		return getSelectQuery(context, id, CbimObjectKey.LIBRARY_REFERENCE,
				CoinsPrefix.CBIM);
	}

	@Override
	public boolean deleteLibraryReference(String context, String id) {
		return deleteItem(context, id, CbimObjectKey.LIBRARY_REFERENCE,
				CoinsPrefix.CBIM);
	}

	@Override
	public String createLibraryReference(String context, String name,
			String userID, String creator) throws MarmottaException,
			InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException {
		String id = constructId(context);
		InsertQueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsPrefix.CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString(CbimAttributeKey.NAME, name);
		builder.addAttributeDate(CbimAttributeKey.CREATION_DATE, new Date());
		builder.addAttributeString(CbimAttributeKey.USER_ID, userID);
		builder.addAttributeResource(CbimAttributeKey.CREATOR, creator);
		builder.addAttributeType(CbimObjectKey.LIBRARY_REFERENCE);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public void linkCbimObjectToLibraryReference(String context,
			String cbimObject, String libraryReference, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		QueryBuilder builder = new InsertQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsPrefix.CBIM);
		builder.addPrefix(CoinsPrefix.CBIMOTL);
		builder.addGraph(getFullContext(context));
		builder.setId(libraryReference);
		builder.addAttributeResource(CbimotlAttributeKey.OBJECT_REFERENCE,
				cbimObject);
		builder.addAttributeDate(CbimAttributeKey.MODIFICATION_DATE, new Date());
		builder.addAttributeResource(CbimAttributeKey.MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
		// The previous query might have caused two modificationDates/modifiers
		// so...
		builder = new UpdateQueryBuilder(mDateConversion);
		builder.addPrefix(CoinsPrefix.CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(libraryReference);
		builder.addAttributeDate(CbimAttributeKey.MODIFICATION_DATE, new Date());
		builder.addAttributeResource(CbimAttributeKey.MODIFIER, modifier);
		mSparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

}
