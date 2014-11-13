package nl.tno.coinsapi.keys;

import nl.tno.coinsapi.CoinsPrefix;


/**
 * Attribute keys for cbimfs
 */
public enum CbimfsAttributeKey implements IAttributeKey {
	
	/**
	 * cbimfs:authorizedBy
	 */
	VERIFICATION_AUTHORIZED_BY ("authorizedBy"),

	/**
	 * cbimfs:authorizationDate
	 */
	AUTHORIZATION_DATE ("authorizationDate"),

	/**
	 * cbimfs:authorizationMeasures
	 */
	AUTHORIZATION_MEASURES ("authorizationMeasures"),
	
	/**
	 * cbimfs:authorizationRemarks
	 */
	AUTHORIZATION_REMARKS ("authorizationRemarks"),
	
	/**
	 * cbimfs:authorizationDefects
	 */
	AUTHORIZATION_DEFECTS ("authorizationDefects"),

	/**
	 * cbimfs:plannedRemarks
	 */
	PLANNED_REMARKS ("plannedRemarks"),

	/**
	 * cbimfs:plannedVerificationDate
	 */
	PLANNED_VERIFICATION_DATE ("plannedVerificationDate"),

	/**
	 * cbimfs:plannedVerificationMethod
	 */
	PLANNED_VERIFICATION_METHOD ("plannedVerificationMethod"),

	/**
	 * cbimfs:plannedWorkPackage
	 */
	PLANNED_WORK_PACKAGE ("plannedWorkPackage"),

	/**
	 * cbimfs:verificationRisks
	 */
	VERIFICATION_RISKS ("verificationRisks"),

	/**
	 * cbimfs:plannedVerificationPerformer
	 */
	VERIFICATION_PLANNED_PERFORMER ("plannedVerificationPerformer"),

	/**
	 * cbimfs:nonFunctionalRequirement
	 */
	NON_FUNCTIONAL_REQUIREMENT ("nonFunctionalRequirement"),

	/**
	 * cbimfs:nonFunctionalRequirementType
	 */
	NON_FUNCTIONAL_REQUIREMENT_TYPE ("nonFunctionalRequirementType"),

	/**
	 * cbimfs:propertyValue 
	 */
	PROPERTY_VALUE ("propertyValue"),

	/**
	 * cbimfs:superRequirement
	 */
	SUPER_REQUIREMENT ("superRequirement"),

	/**
	 * cbimfs:verificationRequirement (NonFunctionalRequirement)
	 */
	VERIFICATION_REQUIREMENT ("verificationRequirement");

	private final String mStringRepresentation;
	private final String mNonPrefixedName;

	CbimfsAttributeKey(String pName) {
		mStringRepresentation = getPrefix().getKey() + ":" + pName;
		mNonPrefixedName = pName;
	}

	@Override
	public String toString() {
		return mStringRepresentation;
	}

	/**
	 * @return the prefix (cbimfs)
	 */
	public CoinsPrefix getPrefix() {
		return CoinsPrefix.CBIMFS;
	}

	@Override
	public String getNonPrefixedName() {
		return mNonPrefixedName;
	}

}
