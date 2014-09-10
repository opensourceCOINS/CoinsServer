package nl.tno.coinsapi.tools;

/**
 * What do we want to validate
 */
public enum ValidationAspect {
	/**
	 * All aspects that have been implemented
	 */
	ALL,
	/**
	 * Primary orientation <= 1
	 */
	PRIMARYORIENATION,
	/**
	 * CurentState/StateOf
	 */
	STATES,
	/**
	 * Next parameter <= 1
	 */
	NEXTPARAMETER,
	/**
	 * Secondary orientation <= 1
	 */
	SECONDARYORIENTATION,
	/**
	 * Translation <= 1
	 */
	TRANSLATION,
	/**
	 * Minimum bounding box <= 1
	 */
	MINBOUNDINGBOX,
	/**
	 * Maximum bounding box <= 1
	 */
	MAXBOUNDINGBOX,
	/**
	 * Physical parents (<= 1)
	 */
	PHYSICALPARENT,
	/**
	 * Locators (<= 1)
	 */
	LOCATOR,
	/**
	 * First parameter (<= 1)
	 */
	FIRST_PARAMETER,
	/**
	 * Requirement of (<= 1)
	 */
	REQUIREMENT_OF,
	/**
	 * Super requirement (<= 1)
	 */
	SUPER_REQUIREMENT,
	/**
	 * Male terminal (<= 1)
	 */
	MALETERMINAL,
	/**
	 * Female terminal (<= 1)
	 */
	FEMALETERMINAL,
	/**
	 * Supertype (<= 1)
	 */
	SUPERTYPE,
	/**
	 * isFulfilledBy / fulfills
	 */
	FUNCTIONFULFILLERS,
	/**
	 * Literal object validation
	 */
	LITERALS,
	/**
	 * physicalParent/physicalChild
	 */
	PHYSICALOBJECT_PARENT_CHILD,
	/**
	 * spatialChild/spationParent
	 */
	SPACE_PARENT_CHILD,
	/**
	 * affects/isAffectedBy
	 */
	AFFECTS,
	/**
	 * situates/isSituatedBy
	 */
	SITUATES,
	/**
	 * requirement/requirementOf
	 */
	REQUIREMENT,
	/**
	 * performance
	 */
	PERFORMANCE;
}
