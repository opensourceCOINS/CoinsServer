package nl.tno.coinsapi.keys;


/**
 * Attribute keys for cbim otl
 */
public enum CbimotlAttributeKey implements IAttributeKey {

	/**
	 * cbimotl:objectReference 
	 */
	OBJECT_REFERENCE ("objectReference");
	
	private final String mStringRepresentation;
	private final String mNonPrefixedName;

	CbimotlAttributeKey(String pName) {
		mStringRepresentation = getPrefix() + ":" + pName;
		mNonPrefixedName = pName;
	}

	@Override
	public String toString() {
		return mStringRepresentation;
	}

	/**
	 * @return the prefix (cbimotl)
	 */
	public String getPrefix() {
		return "cbimotl";
	}

	@Override
	public String getNonPrefixedName() {
		return mNonPrefixedName;
	}

}
