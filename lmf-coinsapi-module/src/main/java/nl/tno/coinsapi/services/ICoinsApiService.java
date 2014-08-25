package nl.tno.coinsapi.services;

import java.util.List;

import org.apache.marmotta.platform.core.exception.InvalidArgumentException;
import org.apache.marmotta.platform.core.exception.MarmottaException;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.UpdateExecutionException;

/**
 * Interface to CoinsApiService used for managing COINS objects
 */
public interface ICoinsApiService {

	/**
	 * What do we want to validate
	 */
	public enum ValidationAspect {
		/**
		 * All aspects that have been implemented 
		 */
		ALL,
		/**
		 * Physical parents
		 */
		PHYSICALPARENT;
	}
	
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
	 * @return the query to retrieve a requirement
	 */
	public String getRequirementQuery(String context, String id);

	/**
	 * @param context
	 * @param id
	 * @return true if success
	 */
	public boolean deleteRequirement(String context, String id);

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
	 * @param context
	 * @param id
	 * @return true if success
	 */
	public boolean deletePersonOrOrganisation(String context, String id);

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
	 * @return true if success
	 */
	public boolean deletePhysicalObject(String context, String id);

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
	 * @return id of the created function
	 * @throws MarmottaException
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 */
	public String createFunction(String context, String modelURI, String name,
			int layerIndex, String userID, String creator) throws MarmottaException, InvalidArgumentException, MalformedQueryException, UpdateExecutionException;

	/**
	 * @param context
	 * @param id
	 * @return true if success
	 */
	public boolean deleteFunction(String context, String id);

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
	 * @return true if success
	 */
	public boolean deleteDocument(String context, String id);

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
	 * @param id
	 * @return true if success
	 */
	public boolean deleteExplicit3DRepresentation(String context, String id);

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

	/**
	 * @param context
	 * @param id
	 * @return true if success
	 */
	public boolean deleteVector(String context, String id);

	/**
	 * @param context
	 * @param id
	 * @return the locator query
	 */
	public String getLocatorQuery(String context, String id);

	/**
	 * @param context
	 * @param modelURI
	 * @param name
	 * @param primaryOrientation
	 * @param secondaryOrientation
	 * @param translation
	 * @param creator
	 * @return the id of the created locator
	 * @throws MarmottaException
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 */
	public String createLocator(String context, String modelURI, String name,
			String primaryOrientation, String secondaryOrientation,
			String translation, String creator) throws MarmottaException, InvalidArgumentException, MalformedQueryException, UpdateExecutionException;

	/**
	 * @param context
	 * @param id
	 * @return true if success
	 */
	public boolean deleteLocator(String context, String id);

	/**
	 * @param context
	 * @param modelURI
	 * @param name
	 * @param affects
	 * @param userID
	 * @param taskType
	 * @param startDatePlanned
	 * @param endDatePlanned
	 * @param creator
	 * @return id of the new Task that was created
	 * @throws MarmottaException
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 */
	public String createTask(String context, String modelURI, String name,
			String[] affects, String userID, String taskType,
			String startDatePlanned, String endDatePlanned, String creator) throws MarmottaException, InvalidArgumentException, MalformedQueryException, UpdateExecutionException;

	/**
	 * @param context
	 * @param id
	 * @return Task Query
	 */
	public String getTaskQuery(String context, String id);

	/**
	 * @param context
	 * @param id
	 * @return true if success
	 */
	public boolean deleteTask(String context, String id);

	/**
	 * @param context
	 * @param id
	 * @return NonFunctionalRequirementQuery
	 */
	public String getNonFunctionalRequirementQuery(String context, String id);

	/**
	 * @param context
	 * @param modelURI
	 * @param name
	 * @param layerIndex
	 * @param userID
	 * @param creator
	 * @param nonFunctionalRequirementType
	 * @return the id of the created NonFunctionalRequirement
	 * @throws MarmottaException
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 */
	public String createNonFunctionalRequirement(String context,
			String modelURI, String name, int layerIndex, String userID,
			String creator, String nonFunctionalRequirementType) throws MarmottaException, InvalidArgumentException, MalformedQueryException, UpdateExecutionException;

	/**
	 * @param context
	 * @param id
	 * @return true if success
	 */
	public boolean deleteNonFunctionalRequirement(String context, String id);

	/**
	 * @param context
	 * @param id
	 * @return Amount query
	 */
	public String getAmountQuery(String context, String id);

	/**
	 * @param context
	 * @param modelURI
	 * @param name
	 * @param userID
	 * @param value
	 * @param catalogPart
	 * @param creator
	 * @return id of the created amount
	 * @throws MarmottaException
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 */
	public String createAmount(String context, String modelURI, String name,
			String userID, int value, String catalogPart, String creator)
			throws MarmottaException, InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException;

	/**
	 * @param context
	 * @param id
	 * @return true if success
	 */
	public boolean deleteAmount(String context, String id);

	/**
	 * @param context
	 * @param id
	 * @return a catalogue part query
	 */
	public String getCataloguePartQuery(String context, String id);

	/**
	 * @param context
	 * @param modelURI
	 * @param name
	 * @param userID
	 * @param creator
	 * @return id of the new CataloguePart
	 * @throws MarmottaException
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 */
	public String createCataloguePart(String context, String modelURI,
			String name, String userID, String creator) throws MarmottaException, InvalidArgumentException, MalformedQueryException, UpdateExecutionException;

	/**
	 * @param context
	 * @param id
	 * @return true if success
	 */
	public boolean deleteCataloguePart(String context, String id);

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
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 * @throws MarmottaException
	 */
	public void initializeContext(String context, String modelURI) throws InvalidArgumentException, MalformedQueryException, UpdateExecutionException, MarmottaException;

	/**
	 * @param context
	 * @param child
	 * @param parent
	 * @param modifier
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 * @throws MarmottaException
	 */
	public void setPysicalParent(String context, String child, String parent, String modifier) throws InvalidArgumentException, MalformedQueryException, UpdateExecutionException, MarmottaException;

	/**
	 * @param context
	 * @param physicalobject
	 * @param nonfunctionalrequirement
	 * @param modifier
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 * @throws MarmottaException
	 */
	public void linkNonFunctionalRequirement(String context,
			String physicalobject, String[] nonfunctionalrequirement,
			String modifier) throws InvalidArgumentException, MalformedQueryException, UpdateExecutionException, MarmottaException;

	/**
	 * @param context
	 * @param physicalobject
	 * @param document
	 * @param modifier
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 * @throws MarmottaException
	 */
	public void linkDocument(String context, String physicalobject,
			String[] document, String modifier) throws InvalidArgumentException, MalformedQueryException, UpdateExecutionException, MarmottaException;

	/**
	 * @param context
	 * @param function
	 * @param isFulfilledBy
	 * @param modifier
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 * @throws MarmottaException
	 */
	public void linkFunctionIsFulfilledBy(String context, String function,
			String[] isFulfilledBy, String modifier) throws InvalidArgumentException, MalformedQueryException, UpdateExecutionException, MarmottaException;

	/**
	 * @param context
	 * @param physicalobject
	 * @param fulfills
	 * @param modifier
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 * @throws MarmottaException
	 */
	public void linkPhysicalObjectFulfills(String context, String physicalobject, String[] fulfills, String modifier) throws InvalidArgumentException, MalformedQueryException, UpdateExecutionException, MarmottaException;

	/**
	 * @param context
	 * @param physicalobject
	 * @param shape
	 * @param modifier
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 * @throws MarmottaException
	 */
	public void setShape(String context, String physicalobject, String shape,
			String modifier) throws InvalidArgumentException, MalformedQueryException, UpdateExecutionException, MarmottaException;

	/**
	 * @param context
	 * @param physicalobject
	 * @param isAffectedBy
	 * @param modifier
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 * @throws MarmottaException
	 */
	public void linkIsAffectedBy(String context, String physicalobject,
			String isAffectedBy, String modifier) throws InvalidArgumentException, MalformedQueryException, UpdateExecutionException, MarmottaException;

	/**
	 * @param pContext 
	 * @param aspect
	 * @return a List of "troubles"
	 */
	public List<String> validate(String pContext, ValidationAspect aspect);

	/**
	 * @param context
	 * @param object
	 * @param name
	 * @param value
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 * @throws MarmottaException
	 */
	public void addAttributeString(String context, String object, String name,
			String value) throws InvalidArgumentException, MalformedQueryException, UpdateExecutionException, MarmottaException;

	/**
	 * @param context
	 * @param object
	 * @param name
	 * @param value
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 * @throws MarmottaException
	 */
	public void addAttributeResource(String context, String object, String name,
			String value) throws InvalidArgumentException, MalformedQueryException, UpdateExecutionException, MarmottaException;

	/**
	 * @param context
	 * @param object
	 * @param name
	 * @param value
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 * @throws MarmottaException
	 */
	public void addAttributeFloat(String context, String object, String name,
			double value) throws InvalidArgumentException, MalformedQueryException, UpdateExecutionException, MarmottaException;

	/**
	 * @param context
	 * @param object
	 * @param name
	 * @param value
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 * @throws MarmottaException
	 */
	public void addAttributeInt(String context, String object, String name,
			int value) throws InvalidArgumentException, MalformedQueryException, UpdateExecutionException, MarmottaException;

	/**
	 * @param context
	 * @param object
	 * @param name
	 * @param value
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 * @throws MarmottaException
	 */
	public void addAttributeDate(String context, String object, String name,
			String value) throws InvalidArgumentException, MalformedQueryException, UpdateExecutionException, MarmottaException;

}
