package nl.tno.coinsapi.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

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

	/**
	 * Print a Map
	 * @param pMap
	 */
	public static void print(Map<String, String> pMap) {
		List<String> items = new Vector<String>();
		int length = 0;
		for (Entry<String,String> entry : pMap.entrySet()) {
			items.add(entry.getKey());
			length = Math.max(length, entry.getKey().length());
		}
		Collections.sort(items, new Comparator<String>(){
			@Override
			public int compare(String o1, String o2) {
				if (o1.equals("name")) {
					return -1;
				}
				if (o2.equals("name")) {
					return 1;
				}
				return o1.compareTo(o2);
			}});
		for (String item : items) {
			StringBuilder sb = new StringBuilder(item);
			while (sb.length() < length) {
				sb.append(' ');
			}			
			sb.append(": ");
			sb.append(pMap.get(item));
			System.out.println(sb.toString());
		}
	}
	
	/**
	 * @param pFile
	 * @return byte[] containing the file as a blob
	 * @throws IOException
	 */
	public static byte[] createByteArray(File pFile) throws IOException {
		FileInputStream stream = new FileInputStream(pFile);
		int size = 0;
		while (stream.read() != -1) {
			size++;
		}
		stream.close();

		byte[] result = new byte[size];
		stream = new FileInputStream(pFile);
		stream.read(result);
		stream.close();

		return result;
	}
	
}
