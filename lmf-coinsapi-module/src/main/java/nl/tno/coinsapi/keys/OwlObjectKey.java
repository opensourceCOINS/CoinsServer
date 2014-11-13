package nl.tno.coinsapi.keys;

import nl.tno.coinsapi.CoinsPrefix;


/**
 * Object keys for owl
 */
public enum OwlObjectKey implements IObjectKey {
	
	/**
	 * owl:Ontology 
	 */
	ONTOLOGY("Ontology");
		
	private final String mStringRepresentation;

	OwlObjectKey(String pName) {
		mStringRepresentation = getPrefix().getKey() + ":" + pName;
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

}
