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

public class CoinsApiService implements ICoinsApiService {

	@Inject
	private SparqlService sparqlService;

	@Inject
	private ICoinsDateConversion dateConversion;
	
	private static final String PREFIX_CBIM = "cbim: <http://www.coinsweb.nl/c-bim.owl#>";
	
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
		builder.addAttribute("cbim:layerIndex", String.valueOf(layerindex));
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
		builder.addAttribute("cbim:layerIndex", String.valueOf(layerIndex));
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
	
}

 