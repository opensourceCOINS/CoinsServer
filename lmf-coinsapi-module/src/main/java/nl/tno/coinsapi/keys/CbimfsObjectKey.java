package nl.tno.coinsapi.keys;

import nl.tno.coinsapi.CoinsPrefix;


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
		return CoinsPrefix.CBIMFS;
	}
}
