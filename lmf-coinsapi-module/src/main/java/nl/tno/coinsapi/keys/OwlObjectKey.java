package nl.tno.coinsapi.keys;


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
		mStringRepresentation = getPrefix() + ":" + pName;
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

}
