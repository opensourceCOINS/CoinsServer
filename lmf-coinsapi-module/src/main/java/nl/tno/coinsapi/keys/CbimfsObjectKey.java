package nl.tno.coinsapi.keys;


/**
 * cbimfs object keyt
 */
public enum CbimfsObjectKey implements IObjectKey {

	/**
	 * cbimfs:NonFunctionalRequirement
	 */
	NON_FUNCTIONAL_REQUIREMENT ("NonFunctionalRequirement");
	
	private final String mStringRepresentation;

	CbimfsObjectKey(String pName) {
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
		return "cbimfs";
	}
}
