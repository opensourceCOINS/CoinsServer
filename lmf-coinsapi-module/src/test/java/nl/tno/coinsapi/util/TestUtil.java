package nl.tno.coinsapi.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;

import com.jayway.restassured.response.ResponseBody;

/**
 * Utilities for testing
 */
public class TestUtil {

	/**
	 * @param pBody
	 * @return a String list
	 */
	public static List<String> getStringList(ResponseBody pBody) {
		@SuppressWarnings("unchecked")
		List<String> result = pBody.as(List.class);
		return result;
	}

	/**
	 * @param pCsvString
	 * @return a Key - Value mapping from a comma separated String. Key is
	 *         checked upon uniqueness.
	 */
	public static Map<String, String> toKeyValueMapping(String pCsvString) {
		String[] items = pCsvString.replaceAll("\n", ",").split(",");
		Assert.assertEquals(0, items.length % 2);
		Map<String, String> result = new HashMap<String, String>();
		for (int i = 0; i < items.length - 1; i += 2) {
			final String key = items[i].trim();
			final String value = items[i + 1].trim();
			Assert.assertFalse(result.containsKey(key));
			result.put(key, value);
		}
		return result;
	}

	/**
	 * @param pCsvString
	 * @return true if string contains only headers (name,value)
	 */
	public static boolean isEmpty(String pCsvString) {
		return (pCsvString.replace("name", "").replace(",", "")
				.replace("value", "").trim().length() == 0);
	}

}
