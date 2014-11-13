package nl.tno.coinsapi.keys;

import nl.tno.coinsapi.CoinsPrefix;

/**
 * Object keys for cbimotl
 */
public enum CbimotlObjectKey implements IObjectKey {

	/**
	 * cbimotl:RequirementTypeReference
	 */
	REQUIREMENT_TYPE_REFERENCE("RequirementTypeReference"),

	/**
	 * cbimotl:PerformanceTypeReference
	 */
	PERFORMANCE_TYPE_REFERENCE("PerformanceTypeReference"),

	/**
	 * cbimotl:PerformanceType
	 */
	PERFORMANCE_TYPE("PerformanceType"),

	/**
	 * cbimotl:FunctionTypeReference
	 */
	FUNCTION_TYPE_REFERENCE("FunctionTypeReference"),

	/**
	 * cbimotl:FunctionType
	 */
	FUNCTION_TYPE("FunctionType"),
	
	/**
	 * cbimotl:RequirementType
	 */
	REQUIREMENT_TYPE ("RequirementType");

	private final String mStringRepresentation;

	CbimotlObjectKey(String pName) {
		mStringRepresentation = getPrefix().getKey() + ":" + pName;
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
}
