package nl.tno.coinsapi.keys;

import nl.tno.coinsapi.CoinsPrefix;


/**
 * Attribute key object
 */
public class AttributeKey implements IAttributeKey {
	
	private final String mPrefix;
	private final String mNonPrefixedName;
	
	/**
	 * Constructor
	 * @param pKey
	 */
	public AttributeKey(String pKey) {
		int index = pKey.indexOf(":");
		if (index < 0) {
			mPrefix = null;
			mNonPrefixedName = pKey;
		} else {
			mPrefix = pKey.substring(0, index);
			mNonPrefixedName = pKey.substring(index + 1);
		}
	}
	
	@Override
	public String getNonPrefixedName() {
		return mNonPrefixedName;
	}
	
	/**
	 * @return the prefix
	 */
	public CoinsPrefix getPrefix() {
		return (mPrefix == null) ? null : CoinsPrefix
				.convertFromString(mPrefix);
	}

	@Override
	public String toString() {
		if (mPrefix==null) {
			return mNonPrefixedName;
		}
		return mPrefix + ":" + mNonPrefixedName;
	}

}
