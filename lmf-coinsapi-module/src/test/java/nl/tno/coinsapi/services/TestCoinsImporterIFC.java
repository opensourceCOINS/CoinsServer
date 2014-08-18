package nl.tno.coinsapi.services;

import java.io.IOException;

import org.junit.Test;

import com.jayway.restassured.RestAssured;

/**
 * Testing the Coins importer BIM documents 
 */
public class TestCoinsImporterIFC extends TestCoinsImporter {

	/**
	 * @throws IOException
	 */
	@Test
	public void testImportIFCDefaultContext() throws IOException {
		String context = RestAssured.baseURI + ":" + RestAssured.port
				+ RestAssured.basePath;
		testImportIFC(context);
	}
}
