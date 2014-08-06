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
	 * @return
	 * @throws MarmottaException
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 */
	public String createRequirement(String context, String name, int layerIndex,
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

}
