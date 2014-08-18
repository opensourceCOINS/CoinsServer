package nl.tno.coinsapi.services;

import static com.jayway.restassured.RestAssured.given;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Vector;

import nl.tno.coinsapi.webservices.CoinsApiWebService;

import org.apache.marmotta.platform.core.test.base.JettyMarmotta;
import org.apache.marmotta.platform.core.webservices.io.ImportWebService;
import org.apache.marmotta.platform.sparql.webservices.SparqlWebService;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

/**
 * Test the importing of a COINS container
 */
public class TestCoinsImporter {

	private static final int OK = 200;

	private static JettyMarmotta marmotta;

	/**
	 * Setting up Marmotta test service and RestAssured
	 */
	@BeforeClass
	public static void setUp() {
		marmotta = new JettyMarmotta("/marmotta", ImportWebService.class,
				SparqlWebService.class, CoinsApiWebService.class);

		RestAssured.baseURI = "http://localhost";
		RestAssured.port = marmotta.getPort();
		RestAssured.basePath = marmotta.getContext();
	}

	/**
	 * Tear down
	 */
	@AfterClass
	public static void tearDown() {
		marmotta.shutdown();
	}

	/**
	 * Testing the OWL contents of the container
	 * 
	 * @throws IOException
	 */
	@Test
	public void testImportZitbankD() throws IOException {
		File file = new File(System.getProperty("user.dir") + File.separator
				+ "test" + File.separator + "D.ccr");
		Assert.assertTrue(file.exists());

		String context = RestAssured.baseURI + ":" + RestAssured.port
				+ RestAssured.basePath;

		given().content(createByteArray(file)).given()
				.header("Content-Type", "application/ccr")
				.queryParam("context", context).expect().statusCode(OK).when()
				.post("import/upload");

		String query = "PREFIX cbim: <http://www.coinsweb.nl/c-bim.owl#>\n\n"
				+ "SELECT DISTINCT ?name WHERE {\n"
				+ "?object cbim:name ?name .\n"
				+ "?object a cbim:PhysicalObject .\n}";

		Response response = given().queryParam("query", query)
				.queryParam("output", "csv").expect().statusCode(OK).when()
				.get("sparql/select");
		String result[] = response.asString().split("\n");
		List<String> list = new Vector<String>();
		for (String s : result) {
			list.add(s.trim());
		}
		Assert.assertTrue(list.contains("Zitbank"));
	}

	/**
	 * Testing the BIM contents of the container
	 * 
	 * @throws IOException
	 */
	@Test
	public void testImportIFC() throws IOException {
		File file = new File(System.getProperty("user.dir") + File.separator
				+ "test" + File.separator + "D.ccr");
		Assert.assertTrue(file.exists());

		String context = RestAssured.baseURI + ":" + RestAssured.port
				+ RestAssured.basePath;

		given().content(createByteArray(file)).given()
				.header("Content-Type", "application/ccr")
				.queryParam("context", context).expect().statusCode(OK).when()
				.post("import/upload");

		String query = "PREFIX cbim: <http://www.coinsweb.nl/c-bim.owl#>\n\n"
				+ "SELECT ?object ?documentUri ?documentAliasFilePath WHERE {\n"
				+ "?object cbim:documentUri ?documentUri .\n"
				+ "?object cbim:documentAliasFilePath ?documentAliasFilePath .\n"
				+ "?object a cbim:Explicit3DRepresentation .\n}";

		Response response = given().queryParam("query", query)
				.queryParam("output", "csv").expect().statusCode(OK).when()
				.get("sparql/select");
		String result = response.asString();
		result = result.replaceAll("\n", ",");
		String[] items = result.split(",");
		Assert.assertEquals(4 * 3, items.length);
		Assert.assertEquals("documentAliasFilePath", items[2].trim());
		for (int i = 0; i < 3; i++) {
			Assert.assertEquals("Een zeer eenvoudige casus.ifc",
					items[5 + 3 * i].trim());
			Assert.assertTrue(items[4 + 3 * i].trim().endsWith(
					"coinsapi/bim/Een%20zeer%20eenvoudige%20casus.ifc"));
		}		
		response = given().expect().statusCode(OK).when()
				.get("coinsapi/bim/Een zeer eenvoudige casus.ifc");
		InputStream s = response.asInputStream();
		StringBuilder sb = new StringBuilder();
		int i = s.read();
		while (i != -1) {
			sb.append((char) i);
			i = s.read();
		}
		s.close();
		String ifcFileContents = sb.toString().trim();
		Assert.assertTrue(ifcFileContents.startsWith("ISO-10303-21;"));
		Assert.assertTrue(ifcFileContents.endsWith("END-ISO-10303-21;"));
	}
	
	private byte[] createByteArray(File pFile) throws IOException {
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
