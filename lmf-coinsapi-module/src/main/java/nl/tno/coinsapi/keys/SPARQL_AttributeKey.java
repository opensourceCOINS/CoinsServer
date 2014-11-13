package nl.tno.coinsapi.keys;

import nl.tno.coinsapi.CoinsPrefix;


/**
 * Attribute keys for SPARQL
 */
public enum SPARQL_AttributeKey implements IAttributeKey {
	
	/**
	 * A 
	 */
	A("a");

	final String mStringRepresenation;
	
	SPARQL_AttributeKey(String pString) {
		mStringRepresenation = pString;
	}
	
	@Override
	public String getNonPrefixedName() {
		return mStringRepresenation;
	}

	public String toString() {
		return mStringRepresenation;
	}

	@Override
	public CoinsPrefix getPrefix() {
		int index = mStringRepresenation.indexOf(":");
		if (index<0) {
			return null;
		}
		return CoinsPrefix.convertFromString(mStringRepresenation.substring(0, index));
	}
}
