package nl.tno.coinsapi.keys;


/**
 * Object keys for owl
 */
public enum OwlAttributeKey implements IAttributeKey {
	
	/**
	 * owl:versionInfo 
	 */
	VERSION_INFO("versionInfo"),
	
	/**
	 * owl:imports
	 */
	IMPORTS("imports");
	
	private final String mStringRepresentation;
	private final String mNonPrefixedName;

	OwlAttributeKey(String pName) {
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
		return "owl";
	}

	@Override
	public String getNonPrefixedName() {
		return mNonPrefixedName;
	}

}
