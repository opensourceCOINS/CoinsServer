package nl.tno.coinsapi.services;

import java.io.IOException;

import org.junit.Test;

import com.jayway.restassured.RestAssured;

/**
 * Testing the OWL data
 */
public class TestCoinsImporterOwl extends TestCoinsImporter {

	/**
	 * Testing the OWL contents of the container in the default context/graph
	 * 
	 * @throws IOException
	 */
	@Test
	public void testImportZitbankDDefaultContext() throws IOException {
		testImportZitbankD(RestAssured.baseURI + ":" + RestAssured.port
				+ RestAssured.basePath);
	}

	/**
	 * Testing the OWL contents of the container in an alternative context
	 * 
	 * @throws IOException
	 */
	@Test
	public void testImportZitbankDContext() throws IOException {
		testImportZitbankD(RestAssured.baseURI + ":" + RestAssured.port
				+ RestAssured.basePath + "/bankje");		
	}

}
