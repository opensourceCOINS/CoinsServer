package nl.tno.coinsapi.model;

import java.util.List;
import java.util.Vector;

import nl.tno.coinsapi.keys.CbimObjectKey;
import nl.tno.coinsapi.keys.CbimfsObjectKey;
import nl.tno.coinsapi.keys.CbimotlObjectKey;
import nl.tno.coinsapi.keys.IObjectKey;

/**
 * Factory for retrieving information about the Hierarchy of the COINS model
 */
public class CoinsHierarchyFactory {

	/**
	 * @param pParentKey
	 * @return the children, grandchildren etc etc.
	 */
	public static List<IObjectKey> getChildKeys(IObjectKey pParentKey) {
		List<IObjectKey> result = new Vector<IObjectKey>();
		addChildKeysFromParent(result, pParentKey);
		return result;
	}

	/**
	 * @param pChildKey
	 * @return the parents, grandparents etc
	 */
	public static List<IObjectKey> getParentKeys(IObjectKey pChildKey) {
		List<IObjectKey> result = new Vector<IObjectKey>();
		addParentKeysFromChild(result, pChildKey);
		return result;
	}
	
	private static void addParentKeysFromChild(List<IObjectKey> pKeyList,
			IObjectKey pKey) {
		if (pKey instanceof CbimObjectKey) {
			CbimObjectKey key = (CbimObjectKey)pKey;
			switch (key) {
			case AMOUNT:
				addParentKeys(pKeyList, CbimObjectKey.PROPERTY_VALUE);
				break;
			case BASELINE:
			case CONNECTION:
			case DOCUMENT:
			case FUNCTION:
			case FUNCTION_FULFILLER:
			case LOCATOR:
			case PARAMETER:
			case PERFORMANCE:
			case PERSON_OR_ORGANISATION:
			case PROPERTY_TYPE:
			case PROPERTY_VALUE:
			case REQUIREMENT:
			case STATE:
			case TASK:
			case TERMINAL:
			case VECTOR:
			case VERIFICATION:
				addParentKeys(pKeyList, CbimObjectKey.CBIM_OBJECT);
				break;
			case BUILDING:
				addParentKeys(pKeyList, CbimObjectKey.PHYSICAL_OBJECT);
				break;
			case CATALOGUE_PART:
				addParentKeys(pKeyList, CbimObjectKey.PROPERTY_TYPE);
				break;
			case CBIM_OBJECT:
			case VALUE_DOMAIN:
				//owl:Thing
				break;
			case EXPLICIT3D_REPRESENTATION:
			case LIBRARY_REFERENCE:
			case VISI_MESSAGE:
				addParentKeys(pKeyList, CbimObjectKey.DOCUMENT);
				break;
			case PHYSICAL_OBJECT:
			case SPACE:
				addParentKeys(pKeyList, CbimObjectKey.FUNCTION_FULFILLER);
				break;
			}
		}
		else if (pKey instanceof CbimfsObjectKey) {
			CbimfsObjectKey key = (CbimfsObjectKey)pKey;
			switch (key) {
			case NON_FUNCTIONAL_REQUIREMENT :
				addParentKeys(pKeyList, CbimObjectKey.CBIM_OBJECT);
				break;
			}
		}
		else if (pKey instanceof CbimotlObjectKey) {
			CbimotlObjectKey key = (CbimotlObjectKey)pKey;
			switch (key) {
			case FUNCTION_TYPE_REFERENCE :
			case PERFORMANCE_TYPE_REFERENCE:
			case REQUIREMENT_TYPE_REFERENCE:
				addParentKeys(pKeyList, CbimObjectKey.LIBRARY_REFERENCE);
				break;
			case FUNCTION_TYPE:
			case PERFORMANCE_TYPE:
			case REQUIREMENT_TYPE:
				addParentKeys(pKeyList, CbimObjectKey.CATALOGUE_PART);
				break;
			}
		}		
	}

	private static void addChildKeysFromParent(List<IObjectKey> pKeyList,
			IObjectKey pKey) {
		if (pKey instanceof CbimObjectKey) {
			CbimObjectKey key = (CbimObjectKey)pKey;
			switch (key) {
			case AMOUNT:
			case BASELINE:
			case BUILDING:
			case CONNECTION:
			case EXPLICIT3D_REPRESENTATION:
			case FUNCTION:
			case LOCATOR:
			case PARAMETER:
			case PERFORMANCE:
			case PERSON_OR_ORGANISATION:
			case REQUIREMENT:				
			case SPACE:
			case STATE:
			case TASK:
			case TERMINAL:
			case VALUE_DOMAIN:
			case VECTOR:
			case VERIFICATION:
			case VISI_MESSAGE:
				break;
			case CATALOGUE_PART:
				addChildKeys(pKeyList, CbimotlObjectKey.FUNCTION_TYPE);
				addChildKeys(pKeyList, CbimotlObjectKey.PERFORMANCE_TYPE);
				addChildKeys(pKeyList, CbimotlObjectKey.REQUIREMENT_TYPE);
				break;
			case CBIM_OBJECT:
				addChildKeys(pKeyList, CbimObjectKey.BASELINE);
				addChildKeys(pKeyList, CbimObjectKey.CONNECTION);
				addChildKeys(pKeyList, CbimObjectKey.DOCUMENT);
				addChildKeys(pKeyList, CbimObjectKey.FUNCTION);
				addChildKeys(pKeyList, CbimObjectKey.FUNCTION_FULFILLER);
				addChildKeys(pKeyList, CbimObjectKey.LOCATOR);
				addChildKeys(pKeyList, CbimObjectKey.PARAMETER);
				addChildKeys(pKeyList, CbimObjectKey.PERFORMANCE);
				addChildKeys(pKeyList, CbimObjectKey.PERSON_OR_ORGANISATION);
				addChildKeys(pKeyList, CbimObjectKey.PROPERTY_TYPE);
				addChildKeys(pKeyList, CbimObjectKey.PROPERTY_VALUE);
				addChildKeys(pKeyList, CbimObjectKey.REQUIREMENT);
				addChildKeys(pKeyList, CbimObjectKey.STATE);
				addChildKeys(pKeyList, CbimObjectKey.TASK);
				addChildKeys(pKeyList, CbimObjectKey.TERMINAL);
				addChildKeys(pKeyList, CbimObjectKey.VECTOR);
				addChildKeys(pKeyList, CbimObjectKey.VERIFICATION);
				addChildKeys(pKeyList, CbimfsObjectKey.NON_FUNCTIONAL_REQUIREMENT);
				break;
			case DOCUMENT:
				addChildKeys(pKeyList, CbimObjectKey.EXPLICIT3D_REPRESENTATION);
				addChildKeys(pKeyList, CbimObjectKey.VISI_MESSAGE);
				addChildKeys(pKeyList, CbimObjectKey.LIBRARY_REFERENCE);
				break;
			case FUNCTION_FULFILLER:
				addChildKeys(pKeyList, CbimObjectKey.SPACE);
				addChildKeys(pKeyList, CbimObjectKey.PHYSICAL_OBJECT);
				break;
			case LIBRARY_REFERENCE:
				addChildKeys(pKeyList, CbimotlObjectKey.FUNCTION_TYPE_REFERENCE);
				addChildKeys(pKeyList, CbimotlObjectKey.PERFORMANCE_TYPE_REFERENCE);
				addChildKeys(pKeyList, CbimotlObjectKey.REQUIREMENT_TYPE_REFERENCE);
				break;
			case PHYSICAL_OBJECT:
				addChildKeys(pKeyList, CbimObjectKey.BUILDING);
				break;
			case PROPERTY_TYPE:
				addChildKeys(pKeyList, CbimObjectKey.CATALOGUE_PART);
				break;
			case PROPERTY_VALUE:
				addChildKeys(pKeyList, CbimObjectKey.AMOUNT);
				break;
			}
		}
		else if (pKey instanceof CbimfsObjectKey) {
			CbimfsObjectKey key = (CbimfsObjectKey)pKey;
			switch (key) {
			case NON_FUNCTIONAL_REQUIREMENT :
				break;
			}
		}
		else if (pKey instanceof CbimotlObjectKey) {
			CbimotlObjectKey key = (CbimotlObjectKey)pKey;
			switch (key) {
			case FUNCTION_TYPE:
			case FUNCTION_TYPE_REFERENCE :
			case PERFORMANCE_TYPE:
			case PERFORMANCE_TYPE_REFERENCE:
			case REQUIREMENT_TYPE:
			case REQUIREMENT_TYPE_REFERENCE:
				break;
			}
		}
	}
	
	private static void addChildKeys(List<IObjectKey> pKeyList, IObjectKey pChildKey) {
		pKeyList.add(pChildKey);
		addChildKeysFromParent(pKeyList, pChildKey);
	}
	
	private static void addParentKeys(List<IObjectKey> pKeyList, IObjectKey pParentKey) {
		pKeyList.add(pParentKey);
		addParentKeysFromChild(pKeyList, pParentKey);
	}

}
