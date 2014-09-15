package nl.tno.coinsapi.services;

import java.util.List;

import nl.tno.coinsapi.tools.QueryBuilder.FieldType;
import nl.tno.coinsapi.tools.ValidationAspect;

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
	public String createRequirement(String context, String name,
			int layerIndex, String userId, String creator, String requirementOf)
			throws MarmottaException, InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException;

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
	 * @param name
	 * @return the id of the created person or organisation
	 * @throws MarmottaException
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 */
	public String createPersonOrOrganisation(String context, String name)
			throws MarmottaException, InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException;

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
	public String createPhysicalObject(String context, String name,
			int layerIndex, String userID, String creator)
			throws MarmottaException, InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException;

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
	public String createFunction(String context, String name, int layerIndex,
			String userID, String creator) throws MarmottaException,
			InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException;

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
	 * @param name
	 * @param userID
	 * @param creator
	 * @return id of the created document
	 * @throws MarmottaException
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 */
	public String createDocument(String context, String name, String userID,
			String creator) throws MarmottaException, InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException;

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
	public String createExplicit3DRepresentation(String context, String name,
			String documentType, String documentAliasFilePath,
			String documentUri, String creator) throws MarmottaException,
			InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException;

	/**
	 * @param context
	 * @param id
	 * @return true if success
	 */
	public boolean deleteExplicit3DRepresentation(String context, String id);

	/**
	 * @param context
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
	public String createVector(String context, String name, Double xCoordinate,
			Double yCoordinate, Double zCoordinate, String creator)
			throws MarmottaException, InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException;

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
	public String createLocator(String context, String name,
			String primaryOrientation, String secondaryOrientation,
			String translation, String creator) throws MarmottaException,
			InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException;

	/**
	 * @param context
	 * @param id
	 * @return true if success
	 */
	public boolean deleteLocator(String context, String id);

	/**
	 * @param context
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
	public String createTask(String context, String name, String[] affects,
			String userID, String taskType, String startDatePlanned,
			String endDatePlanned, String creator) throws MarmottaException,
			InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException;

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
	public String createNonFunctionalRequirement(String context, String name,
			int layerIndex, String userID, String creator,
			String nonFunctionalRequirementType) throws MarmottaException,
			InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException;

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
	public String createAmount(String context, String name, String userID,
			int value, String catalogPart, String creator)
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
	 * @param name
	 * @param userID
	 * @param creator
	 * @return id of the new CataloguePart
	 * @throws MarmottaException
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 */
	public String createCataloguePart(String context, String name,
			String userID, String creator) throws MarmottaException,
			InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException;

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
	public void setDescription(String context, String id, String description,
			String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException;

	/**
	 * @param context
	 * @param modelURI
	 * @return null if OK otherwise existing modelURI
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 * @throws MarmottaException
	 */
	public String initializeContext(String context, String modelURI)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException;

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
	public void setPysicalParent(String context, String child, String parent,
			String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException;

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
	public void setPysicalChild(String context, String child, String parent,
			String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException;

	/**
	 * @param context
	 * @param functionfulfiller
	 * @param nonfunctionalrequirement
	 * @param modifier
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 * @throws MarmottaException
	 */
	public void linkNonFunctionalRequirement(String context,
			String functionfulfiller, String[] nonfunctionalrequirement,
			String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException;

	/**
	 * @param context
	 * @param object
	 * @param locator
	 * @param modifier
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 * @throws MarmottaException
	 */
	public void linkLocator(String context, String object, String locator,
			String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException;

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
			String[] document, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException;

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
			String[] isFulfilledBy, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException;

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
	public void linkPhysicalObjectFulfills(String context,
			String physicalobject, String[] fulfills, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException;

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
			String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException;

	/**
	 * @param context
	 * @param functionfulfiller
	 * @param isAffectedBy
	 * @param modifier
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 * @throws MarmottaException
	 */
	public void linkIsAffectedBy(String context, String functionfulfiller,
			String isAffectedBy, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException;

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
			String value) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException;

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
	public void addAttributeResource(String context, String object,
			String name, String value) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException;

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
			double value) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException;

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
			int value) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException;

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
			String value) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException;

	/**
	 * @param context
	 * @param id
	 * @return a Space object
	 */
	public boolean deleteSpace(String context, String id);

	/**
	 * @param context
	 * @param id
	 * @return a query to retrieve a Space object
	 */
	public String getSpaceQuery(String context, String id);

	/**
	 * @param context
	 * @param name
	 * @param layerIndex
	 * @param userID
	 * @param creator
	 * @return a Space
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 * @throws MarmottaException
	 */
	public String createSpace(String context, String name, int layerIndex,
			String userID, String creator) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException;

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
	public void setSpatialChild(String context, String child, String parent,
			String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException;

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
	public void setSpatialParent(String context, String child, String parent,
			String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException;

	/**
	 * @param context
	 * @param name
	 * @param userID
	 * @param defaultValue
	 * @param creator
	 * @return id of created parameter
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 * @throws MarmottaException
	 */
	public String createParameter(String context, String name, String userID,
			String defaultValue, String creator)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException;

	/**
	 * @param context
	 * @param id
	 * @return the parameter
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 * @throws MarmottaException
	 */
	public String getParameterQuery(String context, String id);

	/**
	 * @param context
	 * @param id
	 * @return true if success
	 */
	public boolean deleteParameter(String context, String id);

	/**
	 * @param context
	 * @param explicit3dRepresentation
	 * @param firstParameter
	 * @param modifier
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 * @throws MarmottaException
	 */
	public void setFirstParameter(String context,
			String explicit3dRepresentation, String firstParameter,
			String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException;

	/**
	 * @param context
	 * @param parameter
	 * @param nextParameter
	 * @param modifier
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 * @throws MarmottaException
	 */
	public void setNextParameter(String context, String parameter,
			String nextParameter, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException;

	/**
	 * @param context
	 * @param id
	 * @return true if success
	 */
	public boolean deleteTerminal(String context, String id);

	/**
	 * @param context
	 * @param id
	 * @return the query
	 */
	public String getTerminalQuery(String context, String id);

	/**
	 * @param context
	 * @param name
	 * @param userID
	 * @param locator
	 * @param layerindex
	 * @param creator
	 * @return the id of the created terminal
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 * @throws MarmottaException
	 */
	public String createTerminal(String context, String name, String userID,
			String locator, int layerindex, String creator)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException;

	/**
	 * @param context
	 * @param cbim_id
	 * @param boundingBox
	 * @param locator
	 * @param modifier
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 * @throws MarmottaException
	 */
	public void linkBoundingBox(String context, String cbim_id,
			String boundingBox, String locator, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException;

	/**
	 * @param context
	 * @param cbim_id
	 * @param terminal
	 * @param connection
	 * @param modifier
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 * @throws MarmottaException
	 */
	public void linkTerminal(String context, String cbim_id, String terminal,
			String connection, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException;

	/**
	 * @param context
	 * @param id
	 * @return the connection query
	 */
	public String getConnectionQuery(String context, String id);

	/**
	 * @param context
	 * @param id
	 * @return true if success
	 */
	public boolean deleteConnection(String context, String id);

	/**
	 * @param context
	 * @param name
	 * @param userID
	 * @param creator
	 * @return the id of the connection
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 * @throws MarmottaException
	 */
	public String createConnection(String context, String name, String userID,
			String creator) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException;

	/**
	 * @param context
	 * @param id
	 * @return true if success
	 */
	public boolean deletePropertyType(String context, String id);

	/**
	 * @param context
	 * @param id
	 * @return property type query
	 */
	public String getPropertyTypeQuery(String context, String id);

	/**
	 * @param context
	 * @param name
	 * @param userID
	 * @param unit
	 * @param valuedomain
	 * @param creator
	 * @return id of the created property type
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 * @throws MarmottaException
	 */
	public String createPropertyType(String context, String name,
			String userID, String unit, String valuedomain, String creator)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException;

	/**
	 * @param context
	 * @param id
	 * @return true if ok
	 */
	public boolean deletePropertyValue(String context, String id);

	/**
	 * @param context
	 * @param id
	 * @return property value query
	 */
	public String getPropertyValueQuery(String context, String id);

	/**
	 * @param context
	 * @param name
	 * @param userID
	 * @param propertytype
	 * @param value
	 * @param creator
	 * @return id of created property
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 * @throws MarmottaException
	 */
	public String createPropertyValue(String context, String name,
			String userID, String propertytype, String value, String creator)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException;

	/**
	 * @param context
	 * @param name
	 * @param userID
	 * @param creator
	 * @return state id
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 * @throws MarmottaException
	 */
	public String createState(String context, String name, String userID,
			String creator) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException;

	/**
	 * @param context
	 * @param id
	 * @return state query
	 */
	public String getStateQuery(String context, String id);

	/**
	 * @param context
	 * @param id
	 * @return true if success
	 */
	public boolean deleteState(String context, String id);

	/**
	 * @param context
	 * @param state
	 * @param functionfulfiller
	 * @param modifier
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 * @throws MarmottaException
	 */
	public void setCurrentState(String context, String state,
			String functionfulfiller, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException;

	/**
	 * @param context
	 * @param state
	 * @param functionfulfiller
	 * @param modifier
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 * @throws MarmottaException
	 */
	public void setStateOf(String context, String state,
			String functionfulfiller, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException;

	/**
	 * @param context
	 * @param state
	 * @param previousstate
	 * @param modifier
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 * @throws MarmottaException
	 */
	public void setPreviousState(String context, String state,
			String previousstate, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException;

	/**
	 * @param context
	 * @param id
	 * @return deleted performance
	 */
	public boolean deletePerformance(String context, String id);

	/**
	 * @param context
	 * @param id
	 * @return performance query
	 */
	public String getPerformanceQuery(String context, String id);

	/**
	 * @param context
	 * @param name
	 * @param userID
	 * @param creator
	 * @return id of new performance
	 * @throws MarmottaException
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 */
	public String createPerformance(String context, String name, String userID,
			String creator) throws MarmottaException, InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException;

	/**
	 * @param context
	 * @param id
	 * @param layerindex
	 * @param modifier
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 * @throws MarmottaException
	 */
	public void setLayerIndex(String context, String id, int layerindex,
			String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException;

	/**
	 * @param context
	 * @param performance
	 * @param propertyvalue
	 * @param modifier
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 * @throws MarmottaException
	 */
	public void linkPropertyValue(String context, String performance,
			String propertyvalue, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException;

	/**
	 * @param context
	 * @param performance
	 * @param performanceof
	 * @param modifier
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 * @throws MarmottaException
	 */
	public void linkPerformanceOf(String context, String performance,
			String performanceof, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException;

	/**
	 * @param context
	 * @param object
	 * @param performance
	 * @param modifier
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 * @throws MarmottaException
	 */
	public void linkPerformance(String context, String object,
			String performance, String modifier)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException;

	/**
	 * @param context
	 * @param id
	 * @return true if success
	 */
	public boolean deleteVerification(String context, String id);

	/**
	 * @param context
	 * @param id
	 * @return a verification query
	 */
	public String getVerificationQuery(String context, String id);

	/**
	 * @param context
	 * @param name
	 * @param userID
	 * @param verificationDate
	 * @param verificationMethod
	 * @param verificationResult
	 * @param creator
	 * @return id of verification
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 * @throws MarmottaException
	 */
	public String createVerification(String context, String name,
			String userID, String verificationDate, String verificationMethod,
			boolean verificationResult, String creator)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException;

	/**
	 * @param context
	 * @param referenceFrame
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 * @throws MarmottaException
	 */
	public void addReferenceFrame(String context, String referenceFrame)
			throws InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException;

	/**
	 * @param context
	 * @param verification
	 * @param objectId
	 * @param objectTypeId
	 * @param modifier
	 * @param pFieldType 
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 * @throws MarmottaException
	 */
	public void setVerificationAttribute(String context, String verification,
			String objectId, String objectTypeId, String modifier,
			FieldType pFieldType) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException;

	/**
	 * @param context
	 * @param physicalObject
	 * @param space
	 * @param modifier
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 * @throws MarmottaException
	 */
	public void linkIsSituatedIn(String context, String physicalObject,
			String space, String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException;

	/**
	 * @param context
	 * @param physicalObject
	 * @param space
	 * @param modifier
	 * @throws InvalidArgumentException
	 * @throws MalformedQueryException
	 * @throws UpdateExecutionException
	 * @throws MarmottaException
	 */
	public void linkSituates(String context, String[] physicalObject,
			String space, String modifier) throws InvalidArgumentException,
			MalformedQueryException, UpdateExecutionException,
			MarmottaException;

}
