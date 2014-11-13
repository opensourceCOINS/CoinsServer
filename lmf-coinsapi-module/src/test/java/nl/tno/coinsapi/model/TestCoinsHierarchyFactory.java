package nl.tno.coinsapi.model;

import java.util.List;

import nl.tno.coinsapi.keys.CbimObjectKey;
import nl.tno.coinsapi.keys.CbimfsObjectKey;
import nl.tno.coinsapi.keys.CbimotlObjectKey;
import nl.tno.coinsapi.keys.IObjectKey;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test for coins Hierarchy factory
 */
public class TestCoinsHierarchyFactory {

	/**
	 * Test for cbim:Building 
	 */
	@Test
	public void testBuilding() {
		Assert.assertArrayEquals(new IObjectKey[] {
				CbimObjectKey.PHYSICAL_OBJECT,
				CbimObjectKey.FUNCTION_FULFILLER, CbimObjectKey.CBIM_OBJECT },
				CoinsHierarchyFactory.getParentKeys(CbimObjectKey.BUILDING)
						.toArray(new IObjectKey[] {}));
	}
	
	/**
	 * Test parent child relations cbim objects
	 */
	@Test
	public void testCbim() {
		for (CbimObjectKey key : CbimObjectKey.values()) {
			List<IObjectKey> children = CoinsHierarchyFactory.getChildKeys(key);
			for (IObjectKey child : children) {
				Assert.assertTrue( CoinsHierarchyFactory.getParentKeys(child).contains(key));
			}
		}
	}

	/**
	 * Test parent child relations cbimfs objects
	 */
	@Test
	public void testCbimfs() {
		for (CbimfsObjectKey key : CbimfsObjectKey.values()) {
			List<IObjectKey> children = CoinsHierarchyFactory.getChildKeys(key);
			for (IObjectKey child : children) {
				Assert.assertTrue( CoinsHierarchyFactory.getParentKeys(child).contains(key));
			}
		}
	}

	/**
	 * Test parent child relations cbimotl objects
	 */
	@Test
	public void testCbimotl() {
		for (CbimotlObjectKey key : CbimotlObjectKey.values()) {
			List<IObjectKey> children = CoinsHierarchyFactory.getChildKeys(key);
			for (IObjectKey child : children) {
				Assert.assertTrue( CoinsHierarchyFactory.getParentKeys(child).contains(key));
			}
		}
	}

}
