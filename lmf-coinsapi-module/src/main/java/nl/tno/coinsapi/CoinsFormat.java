package nl.tno.coinsapi;

import java.nio.charset.Charset;
import java.util.Arrays;

import org.openrdf.rio.RDFFormat;

/**
 * Coins format
 */
public class CoinsFormat {

	/**
	 * a
	 */
	public static final String A = "a";

	/**
	 * CBIM
	 */
	private static final String CBIM = "cbim";

	/**
	 * cbim:affects
	 */
	public static final String CBIM_AFFECTS = CBIM + ":affects";

	/**
	 * cbim:Amount
	 */
	public static final String CBIM_AMOUNT = CBIM + ":Amount";

	/**
	 * cbim:CataloguePart
	 */
	public static final String CBIM_CATALOGUE_PART = CBIM + ":CataloguePart";

	/**
	 * cbim:cataloguePart
	 */
	public static final String CBIM_CATALOGUE_PART_RELATION = CBIM
			+ ":cataloguePart";
		
	/**
	 * cbim:Connection
	 */
	public static final String CBIM_CONNECTION = CBIM + ":Connection";
	/**
	 * cbim:creationDate
	 */
	public static final String CBIM_CREATION_DATE = CBIM + ":creationDate";
	/**
	 * cbim:creator
	 */
	public static final String CBIM_CREATOR = CBIM + ":creator";
	/**
	 * cbim:currentState
	 */
	public static final String CBIM_CURRENT_STATE = CBIM + ":currentState";
	/**
	 * cbim:defaultValue
	 */
	public static final String CBIM_DEFAULT_VALUE = CBIM + ":defaultValue";

	/**
	 * cbim:description
	 */
	public static final String CBIM_DESCRIPTION = CBIM + ":description";
	/**
	 * cbim:Document
	 */
	public static final String CBIM_DOCUMENT = CBIM + ":Document";
	/**
	 * cbim:documentAliasFilePath
	 */
	public static final String CBIM_DOCUMENT_ALIAS_FILE_PATH = CBIM
			+ ":documentAliasFilePath";

	/**
	 * cbim:document
	 */
	public static final String CBIM_DOCUMENT_RELATION = CBIM + ":document";

	/**
	 * cbim:documentType
	 */
	public static final String CBIM_DOCUMENT_TYPE = CBIM + ":documentType";

	/**
	 * cbim:documentUri
	 */
	public static final String CBIM_DOCUMENT_URI = CBIM + ":documentUri";

	/**
	 * cbim:endDate
	 */
	public static final String CBIM_END_DATE = CBIM + ":endDate";

	/**
	 * cbim:endDataActual
	 */
	public static final String CBIM_END_DATE_ACTUAL = CBIM + ":endDataActual";

	/**
	 * cbim:endDatePlanned
	 */
	public static final String CBIM_END_DATE_PLANNED = CBIM + ":endDatePlanned";

	/**
	 * cbim:Explicit3DRepresentation
	 */
	public static final String CBIM_EXPLICIT3D_REPRESENTATION = CBIM
			+ ":Explicit3DRepresentation";

	/**
	 * cbim:femaleTerminal
	 */
	public static final String CBIM_FEMALE_TERMINAL = CBIM + ":femaleTerminal";

	/**
	 * cbim:firstParameter
	 */
	public static final String CBIM_FIRST_PARAMETER = CBIM + ":firstParameter";

	/**
	 * cbim:fulfills
	 */
	public static final String CBIM_FULFILLS = CBIM + ":fulfills";

	/**
	 * cbim:Function
	 */
	public static final String CBIM_FUNCTION = CBIM + ":Function";

	/**
	 * cbim:FunctionFulfiller
	 */
	public static final String CBIM_FUNCTION_FULFILLER = CBIM
			+ ":FunctionFulfiller";

	/**
	 * cbim:isAffectedBy
	 */
	public static final String CBIM_IS_AFFECTED_BY = CBIM + ":isAffectedBy";

	/**
	 * cbim:isFulfilledBy
	 */
	public static final String CBIM_IS_FULFILLED_BY = CBIM + ":isFulfilledBy";

	/**
	 * cbim:isSituatedIn
	 */
	public static final String CBIM_IS_SITUATED_IN = CBIM + ":isSituatedIn";

	/**
	 * cbim:layerIndex
	 */
	public static final String CBIM_LAYER_INDEX = CBIM + ":layerIndex";

	/**
	 * cbim:Locator
	 */
	public static final String CBIM_LOCATOR = CBIM + ":Locator";

	/**
	 * cbim:Locator
	 */
	public static final String CBIM_LOCATOR_RELATION = CBIM + ":locator";

	/**
	 * cbim:maleTerminal
	 */
	public static final String CBIM_MALE_TERMINAL = CBIM + ":maleTerminal";

	/**
	 * Max bounding box
	 */
	public static final String CBIM_MAX_BOUNDING_BOX = CBIM + ":maxBoundingBox";

	/**
	 * Min bounding box
	 */
	public static final String CBIM_MIN_BOUNDING_BOX = CBIM + ":minBoundingBox";

	/**
	 * cbim:modificationDate
	 */
	public static final String CBIM_MODIFICATION_DATE = CBIM
			+ ":modificationDate";

	/**
	 * cbim:modifier
	 */
	public static final String CBIM_MODIFIER = CBIM + ":modifier";

	/**
	 * cbim:name
	 */
	public static final String CBIM_NAME = CBIM + ":name";

	/**
	 * cbim:nextParameter
	 */
	public static final String CBIM_NEXT_PARAMETER = CBIM + ":nextParameter";

	/**
	 * cbim:Parameter
	 */
	public static final String CBIM_PARAMETER = CBIM + ":Parameter";

	/**
	 * cbim:Performance
	 */
	public static final String CBIM_PERFORMANCE = CBIM + ":Performance";

	/**
	 * cbim:PerformanceOf
	 */
	public static final String CBIM_PERFORMANCE_OF = CBIM + ":performanceOf";

	/**
	 * cbim:performace
	 */
	public static final String CBIM_PERFORMANCE_RELATION = CBIM
			+ ":performance";

	/**
	 * cbim:PersonOrOrganisation
	 */
	public static final String CBIM_PERSON_OR_ORGANISATION = CBIM
			+ ":PersonOrOrganisation";

	/**
	 * cbim:physicalChild
	 */
	public static final String CBIM_PHYSICAL_CHILD = CBIM + ":physicalChild";

	/**
	 * cbim:PhysicalObject
	 */
	public static final String CBIM_PHYSICAL_OBJECT = CBIM + ":PhysicalObject";

	/**
	 * cbim:physicalParent
	 */
	public static final String CBIM_PHYSICAL_PARENT = CBIM + ":physicalParent";

	/**
	 * cbim:previousState
	 */
	public static final String CBIM_PREVIOUS_STATE = CBIM + ":previousState";

	/**
	 * cbim:primaryOrientation
	 */
	public static final String CBIM_PRIMARY_ORIENTATION = CBIM
			+ ":primaryOrientation";

	/**
	 * cbim:PropertyType
	 */
	public static final String CBIM_PROPERTY_TYPE = CBIM + ":PropertyType";

	/**
	 * cbim:propertyType
	 */
	public static final String CBIM_PROPERTY_TYPE_RELATION = CBIM
			+ ":propertyType";

	/**
	 * cbim:PropertyValue
	 */
	public static final String CBIM_PROPERTY_VALUE = CBIM + ":PropertyValue";

	/**
	 * cbim:releaseDate
	 */
	public static final String CBIM_RELEASE_DATE = CBIM + ":releaseDate";

	/**
	 * cbim:Requirement
	 */
	public static final String CBIM_REQUIREMENT = CBIM + ":Requirement";

	/**
	 * cbim:requirementOf
	 */
	public static final String CBIM_REQUIREMENT_OF = CBIM + ":requirementOf";

	/**
	 * cbim:requirement
	 */
	public static final String CBIM_REQUIREMENT_RELATION = CBIM
			+ ":requirement";

	/**
	 * cbim:secondaryOrientation
	 */
	public static final String CBIM_SECONDARY_ORIENTATION = CBIM
			+ ":secondaryOrientation";

	/**
	 * cbim:shape
	 */
	public static final String CBIM_SHAPE = CBIM + ":shape";

	/**
	 * cbim:situates
	 */
	public static final String CBIM_SITUATES = CBIM + ":situates";

	/**
	 * cbim:Space
	 */
	public static final String CBIM_SPACE = CBIM + ":Space";

	/**
	 * cbim:spatialChild
	 */
	public static final String CBIM_SPATIAL_CHILD = CBIM + ":spatialChild";

	/**
	 * cbim:spatialParent
	 */
	public static final String CBIM_SPATIAL_PARENT = CBIM + ":spatialParent";

	/**
	 * cbim:startDate
	 */
	public static final String CBIM_START_DATE = CBIM + ":startDate";
	/**
	 * cbim:startDateActual
	 */
	public static final String CBIM_START_DATE_ACTUAL = CBIM
			+ ":startDateActual";

	/**
	 * cbim:startDatePlanned
	 */
	public static final String CBIM_START_DATE_PLANNED = CBIM
			+ ":startDatePlanned";

	/**
	 * cbim:State
	 */
	public static final String CBIM_STATE = CBIM + ":State";

	/**
	 * cbim:stateOf
	 */
	public static final String CBIM_STATE_OF = CBIM + ":stateOf";

	/**
	 * cbim:superType
	 */
	public static final String CBIM_SUPER_TYPE = CBIM + ":superType";

	/**
	 * cbim:Task
	 */
	public static final String CBIM_TASK = CBIM + ":Task";

	/**
	 * cbim:TaskType
	 */
	public static final String CBIM_TASK_TYPE = CBIM + ":taskType";

	/**
	 * cbim:Terminal
	 */
	public static final String CBIM_TERMINAL = CBIM + ":Terminal";

	/**
	 * cbim:translation
	 */
	public static final String CBIM_TRANSLATION = CBIM + ":translation";

	/**
	 * cbim:unit
	 */
	public static final String CBIM_UNIT = CBIM + ":unit";

	/**
	 * cbim:userID
	 */
	public static final String CBIM_USER_ID = CBIM + ":userID";

	/**
	 * cbim:value
	 */
	public static final String CBIM_VALUE = CBIM + ":value";

	/**
	 * cbim:ValueDomain
	 */
	public static final String CBIM_VALUE_DOMAIN = CBIM + ":ValueDomain";

	/**
	 * cbim:valueDomain
	 */
	public static final String CBIM_VALUE_DOMAIN_REFERENCE = CBIM
			+ ":valueDomain";

	/**
	 * cbim:Vector
	 */
	public static final String CBIM_VECTOR = CBIM + ":Vector";

	/**
	 * cbim:xCoordinate
	 */
	public static final String CBIM_X_COORDINATE = CBIM + ":xCoordinate";

	/**
	 * cbim:yCoordinate
	 */
	public static final String CBIM_Y_COORDINATE = CBIM + ":yCoordinate";

	/**
	 * cbim:zCoordinate
	 */
	public static final String CBIM_Z_COORDINATE = CBIM + ":zCoordinate";

	/**
	 * cbimfs:NonFunctionalRequirement
	 */
	public static final String CBIMFS_NON_FUNCTIONAL_REQUIREMENT = "cbimfs:NonFunctionalRequirement";

	/**
	 * cbimfs:nonFunctionalRequirement
	 */
	public static final String CBIMFS_NON_FUNCTIONAL_REQUIREMENT_RELATION = "cbimfs:nonFunctionalRequirement";

	/**
	 * cbimfs:nonFunctionalRequirementType
	 */
	public static final String CBIMFS_NON_FUNCTIONAL_REQUIREMENT_TYPE = "cbimfs:nonFunctionalRequirementType";

	/**
	 * cbimfs:superRequirement
	 */
	public static final String CBIMFS_SUPER_REQUIREMENT = "cbimfs:superRequirement";

	/**
	 * Coins RDF format
	 */
	public static final RDFFormat FORMAT = new RDFFormat("Coins container",
			Arrays.asList("application/ccr"), Charset.forName("UTF-8"),
			Arrays.asList("ccr"), true, true);

	/**
	 * http://www.w3.org/2001/XMLSchema#boolean
	 */
	public static final String HTTP_WWW_W3_ORG_2001_XML_SCHEMA_BOOLEAN = "http://www.w3.org/2001/XMLSchema#boolean";

	/**
	 * http://www.w3.org/2001/XMLSchema#dateTime
	 */
	public static final String HTTP_WWW_W3_ORG_2001_XML_SCHEMA_DATE_TIME = "http://www.w3.org/2001/XMLSchema#dateTime";

	/**
	 * http://www.w3.org/2001/XMLSchema#boolean
	 */
	public static final String HTTP_WWW_W3_ORG_2001_XML_SCHEMA_FLOAT = "http://www.w3.org/2001/XMLSchema#float";

	/**
	 * http://www.w3.org/2001/XMLSchema#int
	 */
	public static final String HTTP_WWW_W3_ORG_2001_XML_SCHEMA_INT = "http://www.w3.org/2001/XMLSchema#int";

	/**
	 * http://www.w3.org/2001/XMLSchema#string
	 */
	public static final String HTTP_WWW_W3_ORG_2001_XML_SCHEMA_STRING = "http://www.w3.org/2001/XMLSchema#string";

	/**
	 * PREFIX for sparql query cbim: <http://www.coinsweb.nl/c-bim.owl#>
	 */
	public static final String PREFIX_CBIM1_0 = "cbim: <http://www.coinsweb.nl/c-bim.owl#>";

	/**
	 * PREFIX for sparql query cbim: <http://www.coinsweb.nl/cbim-1.1.owl#>
	 */
	public static final String PREFIX_CBIM1_1 = "cbim: <http://www.coinsweb.nl/cbim-1.1.owl#>";

	/**
	 * PREFIX for sparql query
	 */
	public static final String PREFIX_CBIM = PREFIX_CBIM1_1;
	
	/**
	 * PREFIX for sparql query cbimfs: <http://www.coinsweb.nl/c-bim-fs.owl#>
	 */
	public static final String PREFIX_CBIMFS = "cbimfs: <http://www.coinsweb.nl/c-bim-fs.owl#>";

	/**
	 * PREFIX for sparql query cbimotl:
	 * <http://www.coinsweb.nl/cbim-otl-1.1.owl#>
	 */
	public static final String PREFIX_CBIMOTL = "cbimotl: <http://www.coinsweb.nl/cbim-otl-1.1.owl#>";

	/**
	 * PREFIX for sparql query owl: <http://www.w3.org/2002/07/owl#>
	 */
	public static final String PREFIX_OWL = "owl: <http://www.w3.org/2002/07/owl#>";

	/**
	 * cbimfs:propertyValue 
	 */
	public static final String CBIMFS_PROPERTY_VALUE_RELATION = "cbimfs:propertyValue";

}
