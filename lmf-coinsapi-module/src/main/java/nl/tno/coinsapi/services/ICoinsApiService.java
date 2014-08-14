package nl.tno.coinsapi.services;

import org.apache.marmotta.platform.core.exception.InvalidArgumentException;
import org.apache.marmotta.platform.core.exception.MarmottaException;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.UpdateExecutionException;

/**
 * Interface to CoinsApiService used for managing COINS objects
 */
public interface ICoinsApiService {

	/**
	 * @param context
	 * @param name
	 * @param modelURI 
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
	 * @param modelURI 
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

	/**
	 * @param context
	 * @param modelURI
	 * @param name
	 * @param layerIndex
	 * @param userID
	 * @param creator
	 * @return id of the PysicalObject
	 * @throws MarmottaException
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 */
	public String createPhysicalObject(String context, String modelURI,
			String name, int layerIndex, String userID, String creator) throws MarmottaException, InvalidArgumentException, MalformedQueryException, UpdateExecutionException;

	/**
	 * @param context
	 * @param id
	 * @return PhysicalObject Query
	 */
	public String getPhysicalObjectQuery(String context, String id);

	/**
	 * @param context
	 * @param id
	 * @return Function Query
	 */
	public String getFunctionQuery(String context, String id);

	/**
	 * @param context
	 * @param modelURI
	 * @param name
	 * @param layerIndex
	 * @param userID
	 * @param creator
	 * @param isFulfilledBy
	 * @return id of the created function
	 * @throws MarmottaException
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 */
	public String createFunction(String context, String modelURI, String name,
			int layerIndex, String userID, String creator, String isFulfilledBy) throws MarmottaException, InvalidArgumentException, MalformedQueryException, UpdateExecutionException;

	/**
	 * @param context
	 * @param id
	 * @return Document query
	 */
	public String getDocumentQuery(String context, String id);

	/**
	 * @param context
	 * @param modelURI
	 * @param name
	 * @param userID
	 * @param creator
	 * @return id of the created document 
	 * @throws MarmottaException
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 */
	public String createDocument(String context, String modelURI, String name,
			String userID, String creator) throws MarmottaException, InvalidArgumentException, MalformedQueryException, UpdateExecutionException;

	/**
	 * @param context
	 * @param id
	 * @return Explicit3DRepresentation query
	 */
	public String getExplicit3DRepresentationQuery(String context, String id);

	/**
	 * @param context
	 * @param modelURI
	 * @param name
	 * @param documentType
	 * @param documentAliasFilePath
	 * @param documentUri
	 * @param creator
	 * @return id of the Explicit#DRepresentation
	 * @throws MarmottaException
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 */
	public String createExplicit3DRepresentation(String context,
			String modelURI, String name, String documentType,
			String documentAliasFilePath, String documentUri, 
			String creator)  throws MarmottaException, InvalidArgumentException, MalformedQueryException, UpdateExecutionException;

	/**
	 * @param context
	 * @param modelURI
	 * @param name
	 * @param xCoordinate
	 * @param yCoordinate
	 * @param zCoordinate
	 * @param creator
	 * @return the id of the created vector
	 * @throws MarmottaException
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 */
	public String createVector(String context, String modelURI, String name,
			Double xCoordinate, Double yCoordinate, Double zCoordinate,
			String creator) throws MarmottaException, InvalidArgumentException, MalformedQueryException, UpdateExecutionException;

	/**
	 * @param context
	 * @param id
	 * @return a Vector query
	 */
	public String getVectorQuery(String context, String id);

}
