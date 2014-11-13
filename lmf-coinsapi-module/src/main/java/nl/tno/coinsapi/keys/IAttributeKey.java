package nl.tno.coinsapi.keys;

import nl.tno.coinsapi.CoinsPrefix;

/**
 * Interface to attribute key
 */
public interface IAttributeKey {

	/**
	 * @return the name without bim: and <> and # 
	 */
	public String getNonPrefixedName();

	/**
	 * @return the prefix for the attribute
	 */
	public CoinsPrefix getPrefix();
	
}
