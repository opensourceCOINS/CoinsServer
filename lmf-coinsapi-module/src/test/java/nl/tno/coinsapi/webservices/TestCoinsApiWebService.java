package nl.tno.coinsapi.webservices;

import static com.jayway.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import org.apache.marmotta.platform.core.test.base.JettyMarmotta;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.response.ResponseBody;

public class TestCoinsApiWebService {

	private static final int OK = 200;

	private static JettyMarmotta marmotta;

	@BeforeClass
	public static void setUp() {
		marmotta = new JettyMarmotta("/marmotta", CoinsApiWebService.class);

		RestAssured.baseURI = "http://localhost";
		RestAssured.port = marmotta.getPort();
		RestAssured.basePath = marmotta.getContext();
	}

	@AfterClass
	public static void tearDown() {
		marmotta.shutdown();
	}

	@Test
	public void testVersion() {
		Response resp = RestAssured.get(CoinsApiWebService.PATH
				+ CoinsApiWebService.PATH_VERSION);
		Assert.assertEquals(200, resp.getStatusCode());
		Assert.assertEquals(CoinsApiWebService.MIME_TYPE, resp.getContentType());
		Assert.assertEquals("0.1 premature", resp.getBody().asString());
	}

	@Test
	public void testRequirements() throws JsonProcessingException {
		// POST requirement
		String context = RestAssured.baseURI + ":" + RestAssured.port
				+ RestAssured.basePath;
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("name", "requirement name")
				.queryParam("layerIndex", 3)
				.queryParam("creator", "http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_494d271b-844b-4f05-a971-7894664e32b8")
				.queryParam("requirementOf", "http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_3eb587eb-0de9-4a1a-a136-aea85266ce3c")
				.queryParam("userID", "B1.1")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_REQUIREMENT).body();
		// GET requirement
		String id = body.asString();
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_REQUIREMENT).body();
		Map<String, String> result = toKeyValueMapping(body.asString());
		System.out.println(result);
		Assert.assertEquals("requirement name",
				result.get("http://www.coinsweb.nl/c-bim.owl#name"));
		Assert.assertEquals("B1.1",
				result.get("http://www.coinsweb.nl/c-bim.owl#userID"));
		Assert.assertEquals("http://www.coinsweb.nl/c-bim.owl#Requirement",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals("3",
				result.get("http://www.coinsweb.nl/c-bim.owl#layerIndex"));
		Assert.assertEquals("http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_3eb587eb-0de9-4a1a-a136-aea85266ce3c",
				result.get("http://www.coinsweb.nl/c-bim.owl#requirementOf"));
		Assert.assertEquals("http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_494d271b-844b-4f05-a971-7894664e32b8",
				result.get("http://www.coinsweb.nl/c-bim.owl#creator"));		
		Assert.assertNotNull(result.get("http://www.coinsweb.nl/c-bim.owl#creationDate"));
		// DELETE requirement
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("id", id)				
				.expect()
				.statusCode(OK)
				.when()
				.delete(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_REQUIREMENT);
		// GET requirement to check it has been removed
		body = given()
		.header("Content-Type", CoinsApiWebService.MIME_TYPE)
		.queryParam("context", context)
		.queryParam("id", id)
		.queryParam("output", "csv")
		.expect()
		.statusCode(OK)
		.when()
		.get(CoinsApiWebService.PATH
				+ CoinsApiWebService.PATH_REQUIREMENT).body();
		Assert.assertTrue(isEmpty(body.asString()));
	}

	private Map<String, String> toKeyValueMapping(String pCsvString) {
		String[] items = pCsvString.replaceAll("\n", ",").split(",");
		Assert.assertEquals(0, items.length % 2);
		Map<String, String> result = new HashMap<String, String>();
		for (int i = 0; i < items.length - 1; i += 2) {
			result.put(items[i].trim(), items[i + 1].trim());
		}
		return result;
	}
	
	private boolean isEmpty(String pCsvString) {
		return (pCsvString.replace("name", "").replace(",","").replace("value", "").trim().length()==0);
	}
}
