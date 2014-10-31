package nl.tno.coinsapi.keys;


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
	 * cbim:CataloguePart
	 */
	CATALOGUE_PART("CataloguePart"),

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
	VERIFICATION("Verification");

	private final String mStringRepresentation;

	CbimObjectKey(String pName) {
		mStringRepresentation = getPrefix() + ":" + pName;
	}

	@Override
	public String toString() {
		return mStringRepresentation;
	}

	/**
	 * @return the prefix (cbim)
	 */
	public String getPrefix() {
		return "cbim";
	}
}
