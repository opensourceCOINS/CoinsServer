package nl.tno.coinsapi.keys;

import nl.tno.coinsapi.CoinsPrefix;


/**
 * Cbim object key
 */
public enum CbimObjectKey implements IObjectKey {

	/**
	 * cbim:Amount
	 */
	AMOUNT("Amount"),

	/**
	 * cbim:Baseline
	 */
	BASELINE("Baseline"),

	/**
	 * cbim:Building
	 */
	BUILDING("Building"),
	
	/**
	 * cbim:CataloguePart
	 */
	CATALOGUE_PART("CataloguePart"),

	/**
	 * cbim:CbimObject
	 */
	CBIM_OBJECT("CbimObject"),
	
	/**
	 * cbim:Connection
	 */
	CONNECTION("Connection"),

	/**
	 * cbim:Document
	 */
	DOCUMENT("Document"),

	/**
	 * cbim:Explicit3DRepresentation
	 */
	EXPLICIT3D_REPRESENTATION("Explicit3DRepresentation"),

	/**
	 * cbim:Function
	 */
	FUNCTION("Function"),

	/**
	 * cbim:FunctionFulfiller
	 */
	FUNCTION_FULFILLER("FunctionFulfiller"),

	/**
	 * cbim:LibraryReference
	 */
	LIBRARY_REFERENCE("LibraryReference"),

	/**
	 * cbim:Locator
	 */
	LOCATOR("Locator"),

	/**
	 * cbim:Parameter
	 */
	PARAMETER("Parameter"),

	/**
	 * cbim:Performance
	 */
	PERFORMANCE("Performance"),

	/**
	 * cbim:PersonOrOrganisation
	 */
	PERSON_OR_ORGANISATION("PersonOrOrganisation"),

	/**
	 * cbim:PhysicalObject
	 */
	PHYSICAL_OBJECT("PhysicalObject"),

	/**
	 * cbim:PropertyType
	 */
	PROPERTY_TYPE ("PropertyType"),

	/**
	 * cbim:PropertyValue
	 */
	PROPERTY_VALUE("PropertyValue"),

	/**
	 * cbim:Requirement
	 */
	REQUIREMENT("Requirement"),

	/**
	 * cbim:Space
	 */
	SPACE("Space"),

	/**
	 * cbim:State
	 */
	STATE("State"),

	/**
	 * cbim:Task
	 */
	TASK("Task"),

	/**
	 * cbim:Terminal
	 */
	TERMINAL("Terminal"),

	/**
	 * cbim:ValueDomain
	 */
	VALUE_DOMAIN("ValueDomain"),

	/**
	 * cbim:Vector
	 */
	VECTOR("Vector"),

	/**
	 * cbim:Verification
	 */
	VERIFICATION("Verification"),
	
	/**
	 * VISI Message
	 * cbim:VisiMessage
	 */
	VISI_MESSAGE("VisiMessage");

	private final String mStringRepresentation;

	CbimObjectKey(String pName) {
		mStringRepresentation = getPrefix().getKey() + ":" + pName;
	}

	@Override
	public String toString() {
		return mStringRepresentation;
	}

	/**
	 * @return the prefix (cbim)
	 */
	public CoinsPrefix getPrefix() {
		return CoinsPrefix.CBIM;
	}
}
