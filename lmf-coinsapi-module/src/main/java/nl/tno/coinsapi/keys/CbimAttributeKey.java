package nl.tno.coinsapi.keys;


/**
 * cbim attribute keys
 */
public enum CbimAttributeKey implements IAttributeKey {

	/**
	 * cbim:affects
	 */
	AFFECTS("affects"),

	/**
	 * cbim:baseline
	 */
	BASELINE("baseline"),

	/**
	 * cbim:baselineObject 
	 */
	BASELINE_OBJECT("baselineObject"),

	/**
	 * cbim:baselineStatus (boolean)
	 */
	BASELINE_STATUS("baselineStatus"),

	/**
	 * cbim:cataloguePart
	 */
	CATALOGUE_PART_RELATION ("cataloguePart"),
		
	/**
	 * cbim:creationDate
	 */
	CREATION_DATE("creationDate"),
	/**
	 * cbim:creator
	 */
	CREATOR("creator"),
	/**
	 * cbim:currentState
	 */
	CURRENT_STATE("currentState"),
	/**
	 * cbim:defaultValue
	 */
	DEFAULT_VALUE("defaultValue"),

	/**
	 * cbim:description
	 */
	DESCRIPTION("description"),
	
	/**
	 * cbim:documentAliasFilePath
	 */
	DOCUMENT_ALIAS_FILE_PATH ("documentAliasFilePath"),

	/**
	 * cbim:document
	 */
	DOCUMENT("document"),

	/**
	 * cbim:documentType
	 */
	DOCUMENT_TYPE("documentType"),

	/**
	 * cbim:documentUri
	 */
	DOCUMENT_URI("documentUri"),

	/**
	 * cbim:endDate
	 */
	END_DATE("endDate"),

	/**
	 * cbim:endDataActual
	 */
	END_DATE_ACTUAL("endDataActual"),

	/**
	 * cbim:endDatePlanned
	 */
	END_DATE_PLANNED("endDatePlanned"),

	/**
	 * cbim:femaleTerminal
	 */
	FEMALE_TERMINAL("femaleTerminal"),

	/**
	 * cbim:firstParameter
	 */
	FIRST_PARAMETER("firstParameter"),

	/**
	 * cbim:fulfills
	 */
	FULFILLS("fulfills"),

	/**
	 * cbim:isAffectedBy
	 */
	IS_AFFECTED_BY("isAffectedBy"),

	/**
	 * cbim:isFulfilledBy
	 */
	IS_FULFILLED_BY("isFulfilledBy"),

	/**
	 * cbim:isSituatedIn
	 */
	IS_SITUATED_IN("isSituatedIn"),

	/**
	 * cbim:layerIndex
	 */
	LAYER_INDEX("layerIndex"),

	/**
	 * cbim:Locator
	 */
	LOCATOR("locator"),

	/**
	 * cbim:maleTerminal
	 */
	MALE_TERMINAL("maleTerminal"),

	/**
	 * Max bounding box
	 */
	MAX_BOUNDING_BOX("maxBoundingBox"),

	/**
	 * Min bounding box
	 */
	MIN_BOUNDING_BOX("minBoundingBox"),

	/**
	 * cbim:modificationDate
	 */
	MODIFICATION_DATE ("modificationDate"),

	/**
	 * cbim:modifier
	 */
	MODIFIER("modifier"),

	/**
	 * cbim:name
	 */
	NAME("name"),

	/**
	 * cbim:nextParameter
	 */
	NEXT_PARAMETER("nextParameter"),

	/**
	 * cbim:PerformanceOf
	 */
	PERFORMANCE_OF("performanceOf"),

	/**
	 * cbim:performace
	 */
	PERFORMANCE_RELATION ("performance"),

	/**
	 * cbim:physicalChild
	 */
	PHYSICAL_CHILD("physicalChild"),

	/**
	 * cbim:physicalParent
	 */
	PHYSICAL_PARENT("physicalParent"),

	/**
	 * cbim:previousState
	 */
	PREVIOUS_STATE("previousState"),

	/**
	 * cbim:primaryOrientation
	 */
	PRIMARY_ORIENTATION ("primaryOrientation"),

	/**
	 * cbim:propertyType
	 */
	PROPERTY_TYPE ("propertyType"),

	/**
	 * cbim:releaseDate
	 */
	RELEASE_DATE("releaseDate"),

	/**
	 * cbim:requirementOf
	 */
	REQUIREMENT_OF("requirementOf"),

	/**
	 * cbim:requirement
	 */
	REQUIREMENT ("requirement"),

	/**
	 * cbim:secondaryOrientation
	 */
	SECONDARY_ORIENTATION ("secondaryOrientation"),

	/**
	 * cbim:shape
	 */
	SHAPE("shape"),

	/**
	 * cbim:situates
	 */
	SITUATES("situates"),

	/**
	 * cbim:spatialChild
	 */
	SPATIAL_CHILD("spatialChild"),

	/**
	 * cbim:spatialParent
	 */
	SPATIAL_PARENT("spatialParent"),

	/**
	 * cbim:startDate
	 */
	START_DATE("startDate"),
	/**
	 * cbim:startDateActual
	 */
	START_DATE_ACTUAL ("startDateActual"),

	/**
	 * cbim:startDatePlanned
	 */
	START_DATE_PLANNED ("startDatePlanned"),

	/**
	 * cbim:stateOf
	 */
	STATE_OF("stateOf"),

	/**
	 * cbim:superType
	 */
	SUPER_TYPE("superType"),

	/**
	 * cbim:TaskType
	 */
	TASK_TYPE("taskType"),

	/**
	 * cbim:translation
	 */
	TRANSLATION("translation"),

	/**
	 * cbim:unit
	 */
	UNIT("unit"),

	/**
	 * cbim:userID
	 */
	USER_ID("userID"),

	/**
	 * cbim:value
	 */
	VALUE("value"),

	/**
	 * cbim:valueDomain
	 */
	VALUE_DOMAIN ("valueDomain"),

	/**
	 * cbim:verificationDate 
	 */	
	VERIFICATION_DATE("verificationDate"),

	/**
	 * cbim:verificationFunctionFulfiller 
	 */
	VERIFICATION_FUNCTION_FULFILLER("verificationFunctionFulfiller"),

	/**
	 * cbim:verificationMethod 
	 */
	VERIFICATION_METHOD("verificationMethod"),

	/**
	 * cbim:verificationPerformer 
	 */
	VERIFICATION_PERFORMER("verificationPerformer"),

	/**
	 * cbim:verificationRequirement 
	 */
	VERIFICATION_REQUIREMENT("verificationRequirement"),

	/**
	 * cbim:verificationResult 
	 */
	VERIFICATION_RESULT("verificationResult"),

	/**
	 * cbim:xCoordinate
	 */
	X_COORDINATE("xCoordinate"),

	/**
	 * cbim:yCoordinate
	 */
	Y_COORDINATE("yCoordinate"),

	/**
	 * cbim:zCoordinate
	 */
	Z_COORDINATE("zCoordinate"),

	/**
	 * cbim:amount 
	 */
	AMOUNT("amount");

	private final String mStringRepresentation;
	private final String mNonPrefixedName;

	CbimAttributeKey(String pName) {
		mStringRepresentation = getPrefix() + ":" + pName;
		mNonPrefixedName = pName;
	}

	@Override
	public String toString() {
		return mStringRepresentation;
	}

	/**
	 * @return the prefix (cbimfs)
	 */
	public String getPrefix() {
		return "cbim";
	}

	@Override
	public String getNonPrefixedName() {
		return mNonPrefixedName;
	}

}
