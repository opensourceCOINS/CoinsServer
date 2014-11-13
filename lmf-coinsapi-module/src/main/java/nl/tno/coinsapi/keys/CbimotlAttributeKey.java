package nl.tno.coinsapi.keys;

import nl.tno.coinsapi.CoinsPrefix;

/**
 * Attribute keys for cbim otl
 */
public enum CbimotlAttributeKey implements IAttributeKey {

	/**
	 * cbimotl:objectReference
	 */
	OBJECT_REFERENCE("objectReference"),

	/**
	 * cbimotl:functionTypeReference
	 */
	FUNCTION_TYPE_REFERENCE("functionTypeReference"),

	/**
	 * cbimotl:performanceTypeReference
	 */
	PERFORMANCE_TYPE_REFERENCE("performanceTypeReference"),

	/**
	 * cbimotl:requirementTypeReference
	 */
	REQUIREMENT_TYPE_REFERENCE("requirementTypeReference");

	private final String mStringRepresentation;
	private final String mNonPrefixedName;

	CbimotlAttributeKey(String pName) {
		mStringRepresentation = getPrefix().getKey() + ":" + pName;
		mNonPrefixedName = pName;
	}

	@Override
	public String toString() {
		return mStringRepresentation;
	}

	/**
	 * @return the prefix (cbimotl)
	 */
	public CoinsPrefix getPrefix() {
		return CoinsPrefix.CBIMOTL;
	}

	@Override
	public String getNonPrefixedName() {
		return mNonPrefixedName;
	}

}
