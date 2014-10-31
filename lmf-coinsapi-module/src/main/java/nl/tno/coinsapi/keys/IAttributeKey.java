package nl.tno.coinsapi.keys;

/**
 * Interface to attribute key
 */
public interface IAttributeKey {

	/**
	 * @return the name without bim: and <> and # 
	 */
	public String getNonPrefixedName();
	
}
