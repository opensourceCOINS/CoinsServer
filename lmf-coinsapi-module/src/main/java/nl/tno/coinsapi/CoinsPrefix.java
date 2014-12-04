package nl.tno.coinsapi;

/**
 * Coins prefixes used in SPARQL Queries
 */
public enum CoinsPrefix {

	/**
	 * PREFIX for sparql query cbim: <http://www.coinsweb.nl/c-bim.owl#>
	 */
	CBIM1_0("cbim", "http://www.coinsweb.nl/c-bim.owl#"),

	/**
	 * PREFIX for sparql query cbim: <http://www.coinsweb.nl/cbim-1.1.owl#>
	 */
	CBIM1_1("cbim", "http://www.coinsweb.nl/cbim-1.1.owl#"),

	/**
	 * PREFIX for sparql query
	 */
	CBIM("cbim", "http://www.coinsweb.nl/cbim-1.1.owl#"),

	/**
	 * PREFIX for sparql query cbimfs: <http://www.coinsweb.nl/c-bim-fs.owl#>
	 */
	CBIMFS("cbimfs", "http://www.coinsweb.nl/c-bim-fs.owl#"),

	/**
	 * PREFIX for sparql query cbimotl:
	 * <http://www.coinsweb.nl/cbim-otl-1.1.owl#>
	 */
	CBIMOTL("cbimotl", "http://www.coinsweb.nl/cbim-otl-1.1.owl#"),

	/**
	 * PREFIX for sparql query owl: <http://www.w3.org/2002/07/owl#>
	 */
	OWL("owl", "http://www.w3.org/2002/07/owl#"),

	/**
	 * rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
	 */
	RDF("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#"),
	
	/**
	 * xsd: <http://www.w3.org/2001/XMLSchema#>
	 */
	XSD("xsd", "http://www.w3.org/2001/XMLSchema#"),
	
	/**
	 * rdfs: <http://www.w3.org/2000/01/rdf-schema#> 
	 */
	RDFS("rdfs", "http://www.w3.org/2000/01/rdf-schema#");

	private final String mStringRepresentation;
	private final String mKey;
	private final String mUrl;

	CoinsPrefix(String pKey, String pLink) {
		mKey = pKey;
		mUrl = pLink;
		mStringRepresentation = pKey + ": <" + pLink + ">";
	}

	@Override
	public String toString() {
		return mStringRepresentation;
	}

	/**
	 * @param pValue like cbim
	 * @return maybe null
	 */
	public static CoinsPrefix convertFromString(String pValue) {
		final String value = pValue.toLowerCase();
		for (CoinsPrefix result : values()) {
			if (result.getKey().equals(value)) {
				if (result == CBIM1_0) {
					return CBIM;
				}
				return result;
			}
		}
		return null;
	}
	
	/**
	 * @return http://www.w3.org/2002/07/owl# etc
	 */
	public String getURL() {
		return mUrl;
	}
	
	/**
	 * @return the key (cbim / cbimfs etc)
	 */
	public String getKey() {
		return mKey;
	}
	
	/**
	 * @return true if it is a numbered prefix used for older versions 
	 */
	public boolean isNumbered() {
		return (this==CoinsPrefix.CBIM1_0) || (this==CoinsPrefix.CBIM1_1); 
	}
}
