package nl.tno.coinsapi.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.inject.Inject;

import nl.tno.coinsapi.tools.CoinsValidator;
import nl.tno.coinsapi.tools.CoinsValidator.CoinsAllValidator;
import nl.tno.coinsapi.tools.CoinsValidator.CoinsFunctionFulfillerValidator;
import nl.tno.coinsapi.tools.CoinsValidator.CoinsLiteralValidator;
import nl.tno.coinsapi.tools.CoinsValidator.CoinsPhysicalObjectParentChildValidator;
import nl.tno.coinsapi.tools.CoinsValidator.CoinsPhysicalParentValidator;
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

	@Inject
	private SparqlService sparqlService;

	@Inject
	private ICoinsDateConversion dateConversion;
	
	@Inject
	private ConfigurationService mConfigurationService;

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
	
	private String getFullContext(String pContext) {
		if (pContext == null) {
			return mConfigurationService.getDefaultContext();
		}
		if (pContext.startsWith("http")) {
			return pContext;
		}
		return mConfigurationService.getBaseContext() + "/" + pContext;
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
	public String createRequirement(String context, String modelURI, String name,
			int layerindex, String userId,String creator, String requirementOf) throws MarmottaException, InvalidArgumentException, MalformedQueryException, UpdateExecutionException {		
		String id = modelURI + "#" + java.util.UUID.randomUUID().toString();
		InsertQueryBuilder builder = new InsertQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString("cbim:name", name);
		builder.addAttributeString("cbim:userID", userId);
		builder.addAttributeDate("cbim:creationDate", new Date());
		builder.addAttributeInteger("cbim:layerIndex", layerindex);
		builder.addAttributeLink("cbim:creator", creator);
		builder.addAttributeLink("cbim:requirementOf", requirementOf);
		builder.addAttributeString("a", "cbim:Requirement");
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}
	
	@Override
	public String getRequirementQuery(String context, String id) {
		return getSelectQuery(context, id, "cbim:Requirement", PREFIX_CBIM);
	}

	@Override
	public boolean deleteRequirement(String context, String id) {
		return deleteItem(context, id, "cbim:Requirement", PREFIX_CBIM);
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
		builder.addAttributeString("cbim:name", name);
		builder.addAttributeDate("cbim:creationDate", new Date());
		builder.addAttributeString("a", "cbim:PersonOrOrganisation");
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public String getPersonOrOrganisationQuery(String context, String id) {
		return getSelectQuery(context, id, "cbim:PersonOrOrganisation", PREFIX_CBIM);
	}

	@Override
	public boolean deletePersonOrOrganisation(String context, String id) {
		return deleteItem(context, id, "cbim:PersonOrOrganisation", PREFIX_CBIM);
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
		builder.addAttributeString("cbim:description", description);
		builder.addAttributeDate("cbim:modificationDate", new Date());
		builder.addAttributeLink("cbim:modifier", modifier);
		sparqlService.update(QueryLanguage.SPARQL, builder.build());

		builder = new UpdateQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString("cbim:description", description);
		builder.addAttributeDate("cbim:modificationDate", new Date());
		builder.addAttributeLink("cbim:modifier", modifier);		
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
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
		builder.addAttributeString("cbim:name", name);
		builder.addAttributeDate("cbim:creationDate", new Date());
		builder.addAttributeString("cbim:userID", userID);
		builder.addAttributeInteger("cbim:layerIndex", layerIndex);
		builder.addAttributeLink("cbim:creator", creator);		
		builder.addAttributeString("a", "cbim:PhysicalObject");
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public String getPhysicalObjectQuery(String context, String id) {
		return getSelectQuery(context, id, "cbim:PhysicalObject", PREFIX_CBIM);
	}

	@Override
	public boolean deletePhysicalObject(String context, String id) {
		return deleteItem(context, id, "cbim:PhysicalObject", PREFIX_CBIM);
	}

	@Override
	public String getFunctionQuery(String context, String id) {
		return getSelectQuery(context, id, "cbim:Function", PREFIX_CBIM);
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
	public String createFunction(String context, String modelURI, String name,
			int layerIndex, String userID, String creator)
			throws MarmottaException, InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException {
		String id = modelURI + "#" + java.util.UUID.randomUUID().toString();		
		InsertQueryBuilder builder = new InsertQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(id);
		builder.addAttributeString("cbim:name", name);
		builder.addAttributeDate("cbim:creationDate", new Date());
		builder.addAttributeString("cbim:userID", userID);
		builder.addAttributeInteger("cbim:layerIndex", layerIndex);
		builder.addAttributeLink("cbim:creator", creator);
		builder.addAttributeString("a", "cbim:Function");
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public boolean deleteFunction(String context, String id) {
		return deleteItem(context, id, "cbim:Function", PREFIX_CBIM);
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
		builder.addAttributeString("cbim:name", name);
		builder.addAttributeDate("cbim:creationDate", new Date());
		builder.addAttributeString("cbim:userID", userID);
		builder.addAttributeLink("cbim:creator", creator);
		builder.addAttributeString("a", "cbim:Document");
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public String getDocumentQuery(String context, String id) {
		return getSelectQuery(context, id, "cbim:Document", PREFIX_CBIM);
	}

	@Override
	public boolean deleteDocument(String context, String id) {
		return deleteItem(context, id, "cbim:Document", PREFIX_CBIM);
	}

	@Override
	public String getExplicit3DRepresentationQuery(String context, String id) {
		return getSelectQuery(context, id, "cbim:Explicit3DRepresentation", PREFIX_CBIM);
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
		builder.addAttributeString("cbim:name", name);
		builder.addAttributeDate("cbim:creationDate", new Date());
		builder.addAttributeString("cbim:documentType", documentType);
		builder.addAttributeString("cbim:documentAliasFilePath", documentAliasFilePath);
		builder.addAttributeLink("cbim:documentUri", documentUri);
		builder.addAttributeLink("cbim:creator", creator);
		builder.addAttributeString("a", "cbim:Explicit3DRepresentation");
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public boolean deleteExplicit3DRepresentation(String context, String id) {
		return deleteItem(context, id, "cbim:Explicit3DRepresentation", PREFIX_CBIM);
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
		builder.addAttributeString("cbim:name", name);
		builder.addAttributeDate("cbim:creationDate", new Date());
		builder.addAttributeDouble("cbim:xCoordinate", xCoordinate);
		builder.addAttributeDouble("cbim:yCoordinate", yCoordinate);
		builder.addAttributeDouble("cbim:zCoordinate", zCoordinate);
		builder.addAttributeLink("cbim:creator", creator);
		builder.addAttributeString("a", "cbim:Vector");
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public String getVectorQuery(String context, String id) {
		return getSelectQuery(context, id, "cbim:Vector", PREFIX_CBIM);
	}

	@Override
	public boolean deleteVector(String context, String id) {
		return deleteItem(context, id, "cbim:Vector", PREFIX_CBIM);
	}

	@Override
	public String getLocatorQuery(String context, String id) {
		return getSelectQuery(context, id, "cbim:Locator", PREFIX_CBIM);
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
		builder.addAttributeString("cbim:name", name);
		builder.addAttributeDate("cbim:creationDate", new Date());
		builder.addAttributeLink("cbim:primaryOrientation", primaryOrientation);
		builder.addAttributeLink("cbim:secondaryOrientation", secondaryOrientation);
		builder.addAttributeLink("cbim:translation", translation);
		builder.addAttributeLink("cbim:creator", creator);
		builder.addAttributeString("a", "cbim:Locator");
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public boolean deleteLocator(String context, String id) {
		return deleteItem(context, id, "cbim:Locator", PREFIX_CBIM);
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
		builder.addAttributeString("cbim:name", name);
		builder.addAttributeDate("cbim:creationDate", new Date());
		builder.addAttributeDate("cbim:startDatePlanned", startDatePlanned);
		builder.addAttributeDate("cbim:endDatePlanned", endDatePlanned);
		builder.addAttributeString("cbim:taskType", taskType);
		for (String physicalObjecId : affects) {			
			builder.addAttributeString("cbim:affects", physicalObjecId);
		}
		builder.addAttributeLink("cbim:creator", creator);
		builder.addAttributeString("a", "cbim:Task");
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public boolean deleteTask(String context, String id) {
		return deleteItem(context, id, "cbim:Task", PREFIX_CBIM);
	}

	@Override
	public String getTaskQuery(String context, String id) {
		return getSelectQuery(context, id, "cbim:Task", PREFIX_CBIM);
	}

	@Override
	public String getNonFunctionalRequirementQuery(String context, String id) {
		return getSelectQuery(context, id, "cbimfs:NonFunctionalRequirement", PREFIX_CBIMFS);
	}

	@Override
	public boolean deleteNonFunctionalRequirement(String context, String id) {
		return deleteItem(context, id, "cbimfs:NonFunctionalRequirement", PREFIX_CBIMFS);
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
		builder.addAttributeString("cbim:name", name);
		builder.addAttributeString("cbim:userID", userID);
		builder.addAttributeDate("cbim:creationDate", new Date());
		builder.addAttributeInteger("cbim:layerIndex", layerIndex);
		builder.addAttributeLink("cbim:creator", creator);
		if (isURI(nonFunctionalRequirementType)) {
			builder.addAttributeLink("cbimfs:nonFunctionalRequirementType", nonFunctionalRequirementType);
		}
		else {
			builder.addAttributeString("cbimfs:nonFunctionalRequirementType", nonFunctionalRequirementType);
		}
		builder.addAttributeString("a", "cbimfs:NonFunctionalRequirement");
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
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
	public String getAmountQuery(String context, String id) {
		return getSelectQuery(context, id, "cbim:Amount", PREFIX_CBIM);
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
		builder.addAttributeString("cbim:name", name);
		builder.addAttributeString("cbim:userID", userID);
		builder.addAttributeString("cbim:cataloguePart", catalogPart);
		builder.addAttributeInteger("cbim:value", value);
		builder.addAttributeDate("cbim:creationDate", new Date());
		builder.addAttributeLink("cbim:creator", creator);
		builder.addAttributeString("a", "cbim:Amount");
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public boolean deleteAmount(String context, String id) {
		return deleteItem(context, id, "cbim:Amount", PREFIX_CBIM);
	}

	@Override
	public String getCataloguePartQuery(String context, String id) {
		return getSelectQuery(context, id, "cbim:CataloguePart", PREFIX_CBIM);
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
		builder.addAttributeString("cbim:name", name);
		builder.addAttributeString("cbim:userID", userID);
		builder.addAttributeDate("cbim:creationDate", new Date());
		builder.addAttributeLink("cbim:creator", creator);
		builder.addAttributeString("a", "cbim:CataloguePart");
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public boolean deleteCataloguePart(String context, String id) {
		return deleteItem(context, id, "cbim:CataloguePart", PREFIX_CBIM);
	}

	@Override
	public void initializeContext(String context, String modelUri) throws InvalidArgumentException, MalformedQueryException, UpdateExecutionException, MarmottaException {
		InsertQueryBuilder builder = new InsertQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_OWL);
		builder.addGraph(getFullContext(context));
		builder.setId(modelUri);
		builder.addAttributeString("owl:versionInfo", "Created with Marmotta COINS module");
		builder.addAttributeLink("owl:imports", "http://www.coinsweb.nl/c-bim.owl");
		builder.addAttributeLink("owl:imports", "http://www.coinsweb.nl/c-bim-fs.owl");
		builder.addAttributeString("a", "owl:Ontology");
		
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
		builder.addAttributeLink("cbim:physicalParent", parent);
		builder.addAttributeDate("cbim:modificationDate", new Date());
		builder.addAttributeLink("cbim:modifier", modifier);
		sparqlService.update(QueryLanguage.SPARQL, builder.build());

		builder = new UpdateQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(child);
		builder.addAttributeLink("cbim:physicalParent", parent);
		builder.addAttributeDate("cbim:modificationDate", new Date());
		builder.addAttributeLink("cbim:modifier", modifier);		
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void linkNonFunctionalRequirement(String context,
			String physicalobject, String[] nonfunctionalrequirement,
			String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException {
		QueryBuilder builder = new InsertQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(physicalobject);
		for (String nfr : nonfunctionalrequirement) {
			builder.addAttributeLink("cbim:nonFunctionalRequirement", nfr);
		}
		builder.addAttributeDate("cbim:modificationDate", new Date());
		builder.addAttributeLink("cbim:modifier", modifier);
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
		// The previous query might have caused two modificationDates/modifiers
		// so...
		builder = new UpdateQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(physicalobject);
		builder.addAttributeDate("cbim:modificationDate", new Date());
		builder.addAttributeLink("cbim:modifier", modifier);
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
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
			builder.addAttributeLink("cbim:document", docId);
		}
		builder.addAttributeDate("cbim:modificationDate", new Date());
		builder.addAttributeLink("cbim:modifier", modifier);
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
		// The previous query might have caused two modificationDates/modifiers
		// so...
		builder = new UpdateQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(physicalobject);
		builder.addAttributeDate("cbim:modificationDate", new Date());
		builder.addAttributeLink("cbim:modifier", modifier);
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
			builder.addAttributeLink("cbim:isFulfilledBy", physicalObjectId);
		}
		builder.addAttributeDate("cbim:modificationDate", new Date());
		builder.addAttributeLink("cbim:modifier", modifier);
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
		// The previous query might have caused two modificationDates/modifiers
		// so...
		builder = new UpdateQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(function);
		builder.addAttributeDate("cbim:modificationDate", new Date());
		builder.addAttributeLink("cbim:modifier", modifier);
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
			builder.addAttributeLink("cbim:fulfills", function);
		}
		builder.addAttributeDate("cbim:modificationDate", new Date());
		builder.addAttributeLink("cbim:modifier", modifier);
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
		// The previous query might have caused two modificationDates/modifiers
		// so...
		builder = new UpdateQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(physicalobject);
		builder.addAttributeDate("cbim:modificationDate", new Date());
		builder.addAttributeLink("cbim:modifier", modifier);
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
		builder.addAttributeLink("cbim:shape", shape);
		builder.addAttributeDate("cbim:modificationDate", new Date());
		builder.addAttributeLink("cbim:modifier", modifier);
		sparqlService.update(QueryLanguage.SPARQL, builder.build());

		builder = new UpdateQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(physicalobject);
		builder.addAttributeLink("cbim:physicalParent", shape);
		builder.addAttributeDate("cbim:modificationDate", new Date());
		builder.addAttributeLink("cbim:modifier", modifier);		
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public void linkIsAffectedBy(String context, String physicalobject,
			String isAffectedBy, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		// TODO one query?
		QueryBuilder builder = new InsertQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(physicalobject);
		builder.addAttributeLink("cbim:isAffectedBy", isAffectedBy);
		builder.addAttributeDate("cbim:modificationDate", new Date());
		builder.addAttributeLink("cbim:modifier", modifier);
		sparqlService.update(QueryLanguage.SPARQL, builder.build());

		builder = new UpdateQueryBuilder(dateConversion);
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(getFullContext(context));
		builder.setId(physicalobject);
		builder.addAttributeLink("cbim:isAffectedBy", isAffectedBy);
		builder.addAttributeDate("cbim:modificationDate", new Date());
		builder.addAttributeLink("cbim:modifier", modifier);		
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
		}
		validator.setContext(getFullContext(pContext));
		validator.setSparqlService(sparqlService);
		if (validator.validate()) {
			List<String> result = new Vector<String>();
			result.add("OK");
			return result;
		}
		return validator.getValidationErrors();
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

}

 