package nl.tno.coinsapi.services;

import org.apache.marmotta.platform.core.exception.InvalidArgumentException;
import org.apache.marmotta.platform.core.exception.MarmottaException;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.UpdateExecutionException;

public interface ICoinsApiService {

	/**
	 * @param context
	 * @param name
	 * @param layerIndex
	 * @param userId
	 * @param creator
	 * @param requirementOf
	 * @return the identifier of the created requirement
	 * @throws MarmottaException
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 */
	public String createRequirement(String context, String name, String modelURI, int layerIndex,
			String userId, String creator, String requirementOf) throws MarmottaException, InvalidArgumentException, MalformedQueryException, UpdateExecutionException;

	/**
	 * @param context
	 * @param id
	 * @return true if successful
	 */
	public boolean deleteItem(String context, String id);

	/**
	 * @param context
	 * @param id
	 * @return the query to retrieve a requirement
	 */
	public String getRequirementQuery(String context, String id);

	/**
	 * @param context
	 * @param name
	 * @return the id of the created person or organisation
	 * @throws MarmottaException
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 */
	public String createPersonOrOrganisation(String context, String modelURI, String name) throws MarmottaException, InvalidArgumentException, MalformedQueryException, UpdateExecutionException;

	/**
	 * @param context
	 * @param id
	 * @return the query to retrieve a PersonOrOrganisation
	 */
	public String getPersonOrOrganisationQuery(String context, String id);

	/**
	 * Set the description
	 * 
	 * @param context
	 * @param id
	 * @param description
	 * @param modifier
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 * @throws MarmottaException
	 */
	public void setDescription(String context, String id, String description, String modifier) throws InvalidArgumentException, MalformedQueryException, UpdateExecutionException, MarmottaException;

}
