package nl.tno.coinsapi.services;

import java.util.Date;

import javax.inject.Inject;

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
	
	private static final String PREFIX_CBIM = "PREFIX cbim: <http://www.coinsweb.nl/c-bim.owl#>\n";
	
	@Override
	public String createRequirement(String context, String name,
			int layerindex, String userId, String creator, String requirementOf) throws MarmottaException, InvalidArgumentException, MalformedQueryException, UpdateExecutionException {		
		String id = "http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl" + "#" + java.util.UUID.randomUUID().toString();		
		String query = PREFIX_CBIM + "INSERT DATA { GRAPH <" + context
				+ "> { <" + id + "> " + "cbim:name \"" + name + "\" ; "
				+ "cbim:userID \"" + userId + "\" ; " + "cbim:creationDate \""
				+ dateConversion.toString(new Date()) + "\" ; "
				+ "cbim:layerIndex \"" + layerindex + "\" ; "
				+ "cbim:creator \"" + creator + "\" ; "
				+ "cbim:requirementOf \"" + requirementOf + "\" ; "
				+ "a cbim:Requirement } }";
		sparqlService.update(QueryLanguage.SPARQL, query);
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
	
}

 