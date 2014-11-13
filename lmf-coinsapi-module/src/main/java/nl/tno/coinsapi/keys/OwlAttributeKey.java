package nl.tno.coinsapi.keys;

import nl.tno.coinsapi.CoinsPrefix;


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
		return CoinsPrefix.OWL;
	}

	@Override
	public String getNonPrefixedName() {
		return mNonPrefixedName;
	}

}
