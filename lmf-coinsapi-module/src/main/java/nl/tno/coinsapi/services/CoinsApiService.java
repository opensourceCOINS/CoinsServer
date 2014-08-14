package nl.tno.coinsapi.services;

import java.util.Date;

import javax.inject.Inject;

import nl.tno.coinsapi.tools.QueryBuilder.InsertQueryBuilder;

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
	
	private static final String PREFIX_CBIM = "cbim: <http://www.coinsweb.nl/c-bim.owl#>";
	private static final String PREFIX_CBIMFS = "cbimfs: <http://www.coinsweb.nl/c-bim-fs.owl#>";
	
	@Override
	public String createRequirement(String context, String modelURI, String name,
			int layerindex, String userId,String creator, String requirementOf) throws MarmottaException, InvalidArgumentException, MalformedQueryException, UpdateExecutionException {		
		String id = modelURI + "#" + java.util.UUID.randomUUID().toString();		
		InsertQueryBuilder builder = new InsertQueryBuilder();
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(context);
		builder.setId(id);
		builder.addAttribute("cbim:name", name);
		builder.addAttribute("cbim:userID", userId);
		builder.addAttribute("cbim:creationDate", dateConversion.toString(new Date()));
		builder.addAttribute("cbim:layerIndex", layerindex);
		builder.addAttribute("cbim:creator", creator);
		builder.addAttribute("cbim:requirementOf", requirementOf);
		builder.addAttribute("a", "cbim:Requirement");
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}
	
	@Override
	public boolean deleteItem(String context, String id) {
		String query = "DELETE WHERE { GRAPH <" + context
				+ "> { <" + id + "> ?name ?value }}";
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
	public String getRequirementQuery(String context, String id) {
		return "SELECT ?name ?value WHERE { GRAPH <" + context
				+ "> { <" + id + "> ?name ?value }}";		
	}

	@Override
	public String createPersonOrOrganisation(String context, String modelURI, String name)
			throws MarmottaException, InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException {
		String id = modelURI + "#" + java.util.UUID.randomUUID().toString();		
		InsertQueryBuilder builder = new InsertQueryBuilder();
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(context);
		builder.setId(id);
		builder.addAttribute("cbim:name", name);
		builder.addAttribute("cbim:creationDate", dateConversion.toString(new Date()));
		builder.addAttribute("a", "cbim:PersonOrOrganisation");
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public String getPersonOrOrganisationQuery(String context, String id) {
		return "SELECT ?name ?value WHERE { GRAPH <" + context
				+ "> { <" + id + "> ?name ?value }}";		
	}

	@Override
	public void setDescription(String context, String id, String description, String modifier) throws InvalidArgumentException, MalformedQueryException, UpdateExecutionException, MarmottaException {
		InsertQueryBuilder builder = new InsertQueryBuilder();
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(context);
		builder.setId(id);
		builder.addAttribute("cbim:description", description);
		builder.addAttribute("cbim:modificationDate", dateConversion.toString(new Date()));
		builder.addAttribute("cbim:modifier", modifier);
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
	}

	@Override
	public String createPhysicalObject(String context, String modelURI,
			String name, int layerIndex, String userID, String creator)
			throws MarmottaException, InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException {
		String id = modelURI + "#" + java.util.UUID.randomUUID().toString();		
		InsertQueryBuilder builder = new InsertQueryBuilder();
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(context);
		builder.setId(id);
		builder.addAttribute("cbim:name", name);
		builder.addAttribute("cbim:creationDate", dateConversion.toString(new Date()));
		builder.addAttribute("cbim:userID", userID);
		builder.addAttribute("cbim:layerIndex", layerIndex);
		builder.addAttribute("cbim:creator", creator);		
		builder.addAttribute("a", "cbim:PhysicalObject");
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public String getPhysicalObjectQuery(String context, String id) {
		return "SELECT ?name ?value WHERE { GRAPH <" + context
				+ "> { <" + id + "> ?name ?value }}";		
	}

	@Override
	public String getFunctionQuery(String context, String id) {
		return "SELECT ?name ?value WHERE { GRAPH <" + context
				+ "> { <" + id + "> ?name ?value }}";		
	}

	@Override
	public String createFunction(String context, String modelURI, String name,
			int layerIndex, String userID, String creator, String isFulfilledBy)
			throws MarmottaException, InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException {
		String id = modelURI + "#" + java.util.UUID.randomUUID().toString();		
		InsertQueryBuilder builder = new InsertQueryBuilder();
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(context);
		builder.setId(id);
		builder.addAttribute("cbim:name", name);
		builder.addAttribute("cbim:creationDate", dateConversion.toString(new Date()));
		builder.addAttribute("cbim:userID", userID);
		builder.addAttribute("cbim:layerIndex", layerIndex);
		builder.addAttribute("cbim:creator", creator);
		builder.addAttribute("cbim:isFulfilledBy", isFulfilledBy);
		builder.addAttribute("a", "cbim:Function");
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public String getDocumentQuery(String context, String id) {
		return "SELECT ?name ?value WHERE { GRAPH <" + context
				+ "> { <" + id + "> ?name ?value }}";		
	}

	@Override
	public String createDocument(String context, String modelURI, String name,
			String userID, String creator) throws MarmottaException,
			InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException {
		String id = modelURI + "#" + java.util.UUID.randomUUID().toString();		
		InsertQueryBuilder builder = new InsertQueryBuilder();
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(context);
		builder.setId(id);
		builder.addAttribute("cbim:name", name);
		builder.addAttribute("cbim:creationDate", dateConversion.toString(new Date()));
		builder.addAttribute("cbim:userID", userID);
		builder.addAttribute("cbim:creator", creator);
		builder.addAttribute("a", "cbim:Document");
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public String getExplicit3DRepresentationQuery(String context, String id) {
		return "SELECT ?name ?value WHERE { GRAPH <" + context
				+ "> { <" + id + "> ?name ?value }}";		
	}

	@Override
	public String createExplicit3DRepresentation(String context,
			String modelURI, String name, String documentType,
			String documentAliasFilePath, String documentUri, String creator)
					throws MarmottaException, InvalidArgumentException,
					MalformedQueryException, UpdateExecutionException {
		String id = modelURI + "#" + java.util.UUID.randomUUID().toString();		
		InsertQueryBuilder builder = new InsertQueryBuilder();
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(context);
		builder.setId(id);
		builder.addAttribute("cbim:name", name);
		builder.addAttribute("cbim:creationDate", dateConversion.toString(new Date()));
		builder.addAttribute("cbim:documentType", documentType);
		builder.addAttribute("cbim:documentAliasFilePath", documentAliasFilePath);
		builder.addAttribute("cbim:documentUri", documentUri);
		builder.addAttribute("cbim:creator", creator);
		builder.addAttribute("a", "cbim:Explicit3DRepresentation");
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public String createVector(String context, String modelURI, String name,
			Double xCoordinate, Double yCoordinate, Double zCoordinate,
			String creator) throws MarmottaException, InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException {
		String id = modelURI + "#" + java.util.UUID.randomUUID().toString();		
		InsertQueryBuilder builder = new InsertQueryBuilder();
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(context);
		builder.setId(id);
		builder.addAttribute("cbim:name", name);
		builder.addAttribute("cbim:creationDate", dateConversion.toString(new Date()));
		builder.addAttribute("cbim:xCoordinate", xCoordinate);
		builder.addAttribute("cbim:yCoordinate", yCoordinate);
		builder.addAttribute("cbim:zCoordinate", zCoordinate);
		builder.addAttribute("cbim:creator", creator);
		builder.addAttribute("a", "cbim:Vector");
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public String getVectorQuery(String context, String id) {
		return "SELECT ?name ?value WHERE { GRAPH <" + context
				+ "> { <" + id + "> ?name ?value }}";		
	}

	@Override
	public String getLocatorQuery(String context, String id) {
		return "SELECT ?name ?value WHERE { GRAPH <" + context
				+ "> { <" + id + "> ?name ?value }}";		
	}

	@Override
	public String createLocator(String context, String modelURI, String name,
			String primaryOrientation, String secondaryOrientation,
			String translation, String creator) throws MarmottaException,
			InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException {
		String id = modelURI + "#" + java.util.UUID.randomUUID().toString();		
		InsertQueryBuilder builder = new InsertQueryBuilder();
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(context);
		builder.setId(id);
		builder.addAttribute("cbim:name", name);
		builder.addAttribute("cbim:creationDate", dateConversion.toString(new Date()));
		builder.addAttribute("cbim:primaryOrientation", primaryOrientation);
		builder.addAttribute("cbim:secondaryOrientation", secondaryOrientation);
		builder.addAttribute("cbim:translation", translation);
		builder.addAttribute("cbim:creator", creator);
		builder.addAttribute("a", "cbim:Locator");
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public String createTask(String context, String modelURI, String name,
			String affects, String userID, String taskType,
			String startDatePlanned, String endDatePlanned, String creator)
			throws MarmottaException, InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException {
		String id = modelURI + "#" + java.util.UUID.randomUUID().toString();		
		InsertQueryBuilder builder = new InsertQueryBuilder();
		builder.addPrefix(PREFIX_CBIM);
		builder.addGraph(context);
		builder.setId(id);
		builder.addAttribute("cbim:name", name);
		builder.addAttribute("cbim:creationDate", dateConversion.toString(new Date()));
		builder.addAttribute("cbim:startDatePlanned", startDatePlanned);
		builder.addAttribute("cbim:endDatePlanned", endDatePlanned);
		builder.addAttribute("cbim:taskType", taskType);
		builder.addAttribute("cbim:affects", affects);
		builder.addAttribute("cbim:creator", creator);
		builder.addAttribute("a", "cbim:Task");
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

	@Override
	public String getTaskQuery(String context, String id) {
		return "SELECT ?name ?value WHERE { GRAPH <" + context
				+ "> { <" + id + "> ?name ?value }}";		
	}

	@Override
	public String getNonFunctionalRequirementQuery(String context, String id) {
		return "SELECT ?name ?value WHERE { GRAPH <" + context
				+ "> { <" + id + "> ?name ?value }}";		
	}

	@Override
	public String createNonFunctionalRequirement(String context,
			String modelURI, String name, int layerIndex, String userID,
			String creator, String nonFunctionalRequirementType)
			throws MarmottaException, InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException {
		String id = modelURI + "#" + java.util.UUID.randomUUID().toString();		
		InsertQueryBuilder builder = new InsertQueryBuilder();
		builder.addPrefix(PREFIX_CBIM);
		builder.addPrefix(PREFIX_CBIMFS);
		builder.addGraph(context);
		builder.setId(id);
		builder.addAttribute("cbim:name", name);
		builder.addAttribute("cbim:userID", userID);
		builder.addAttribute("cbim:creationDate", dateConversion.toString(new Date()));
		builder.addAttribute("cbim:layerIndex", layerIndex);
		builder.addAttribute("cbim:creator", creator);
		builder.addAttribute("cbimfs:nonFunctionalRequirementType", nonFunctionalRequirementType);
		builder.addAttribute("a", "cbimfs:NonFunctionalRequirement");
		sparqlService.update(QueryLanguage.SPARQL, builder.build());
		return id;
	}

}

 