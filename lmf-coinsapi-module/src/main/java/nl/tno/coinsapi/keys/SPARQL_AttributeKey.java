package nl.tno.coinsapi.keys;


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
}
