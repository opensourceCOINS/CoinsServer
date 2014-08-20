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

/**
 * Testing COINS API web service
 */
public class TestCoinsApiWebService {

	private static final int OK = 200;

	private static JettyMarmotta marmotta;

	/**
	 * Setting up Marmotta test service and RestAssured
	 */
	@BeforeClass
	public static void setUp() {
		marmotta = new JettyMarmotta("/marmotta", CoinsApiWebService.class);

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
	 * Testing the version
	 */
	@Test
	public void testVersion() {
		Response resp = RestAssured.get(CoinsApiWebService.PATH
				+ CoinsApiWebService.PATH_VERSION);
		Assert.assertEquals(200, resp.getStatusCode());
		Assert.assertEquals(CoinsApiWebService.MIME_TYPE, resp.getContentType());
		Assert.assertEquals("0.1 premature", resp.getBody().asString());
	}

	/**
	 * Testing Requirement
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	public void testRequirement() throws JsonProcessingException {
		// POST requirement
		String context = RestAssured.baseURI + ":" + RestAssured.port
				+ RestAssured.basePath;
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "requirement name")
				.queryParam("layerIndex", 3)
				.queryParam(
						"creator",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_494d271b-844b-4f05-a971-7894664e32b8")
				.queryParam(
						"requirementOf",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_3eb587eb-0de9-4a1a-a136-aea85266ce3c")
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
		Assert.assertEquals("requirement name",
				result.get("http://www.coinsweb.nl/c-bim.owl#name"));
		Assert.assertEquals("B1.1",
				result.get("http://www.coinsweb.nl/c-bim.owl#userID"));
		Assert.assertEquals("http://www.coinsweb.nl/c-bim.owl#Requirement",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals("3",
				result.get("http://www.coinsweb.nl/c-bim.owl#layerIndex"));
		Assert.assertEquals(
				"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_3eb587eb-0de9-4a1a-a136-aea85266ce3c",
				result.get("http://www.coinsweb.nl/c-bim.owl#requirementOf"));
		Assert.assertEquals(
				"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_494d271b-844b-4f05-a971-7894664e32b8",
				result.get("http://www.coinsweb.nl/c-bim.owl#creator"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/c-bim.owl#creationDate"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/c-bim.owl#modificationDate"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/c-bim.owl#modifier"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/c-bim.owl#description"));
		// Set description
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("id", id)
				.queryParam("description", "This is a nice description!")
				.queryParam(
						"modifier",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_494d271b-844b-4f05-a971-7894664e32b8")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_DESCRIPTION);
		// Check it
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
		result = toKeyValueMapping(body.asString());
		Assert.assertEquals("requirement name",
				result.get("http://www.coinsweb.nl/c-bim.owl#name"));
		Assert.assertEquals("B1.1",
				result.get("http://www.coinsweb.nl/c-bim.owl#userID"));
		Assert.assertEquals("http://www.coinsweb.nl/c-bim.owl#Requirement",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals("3",
				result.get("http://www.coinsweb.nl/c-bim.owl#layerIndex"));
		Assert.assertEquals(
				"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_3eb587eb-0de9-4a1a-a136-aea85266ce3c",
				result.get("http://www.coinsweb.nl/c-bim.owl#requirementOf"));
		Assert.assertEquals(
				"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_494d271b-844b-4f05-a971-7894664e32b8",
				result.get("http://www.coinsweb.nl/c-bim.owl#creator"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/c-bim.owl#creationDate"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/c-bim.owl#modificationDate"));
		Assert.assertEquals(
				"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_494d271b-844b-4f05-a971-7894664e32b8",
				result.get("http://www.coinsweb.nl/c-bim.owl#modifier"));
		Assert.assertEquals("This is a nice description!",
				result.get("http://www.coinsweb.nl/c-bim.owl#description"));
		// Set the description again
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
		.queryParam("context", context)
		.queryParam("id", id)
		.queryParam("description", "This description is even nicer...")
		.queryParam(
				"modifier",
				"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_494d271b-844b-4f05-a971-7894664e52b8")
		.expect()
		.statusCode(OK)
		.when()
		.post(CoinsApiWebService.PATH
				+ CoinsApiWebService.PATH_DESCRIPTION);
		// Check for duplicate descriptions...
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
		result = toKeyValueMapping(body.asString());
		Assert.assertEquals("requirement name",
				result.get("http://www.coinsweb.nl/c-bim.owl#name"));
		Assert.assertEquals("B1.1",
				result.get("http://www.coinsweb.nl/c-bim.owl#userID"));
		Assert.assertEquals("http://www.coinsweb.nl/c-bim.owl#Requirement",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals("3",
				result.get("http://www.coinsweb.nl/c-bim.owl#layerIndex"));
		Assert.assertEquals(
				"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_3eb587eb-0de9-4a1a-a136-aea85266ce3c",
				result.get("http://www.coinsweb.nl/c-bim.owl#requirementOf"));
		Assert.assertEquals(
				"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_494d271b-844b-4f05-a971-7894664e32b8",
				result.get("http://www.coinsweb.nl/c-bim.owl#creator"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/c-bim.owl#creationDate"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/c-bim.owl#modificationDate"));
		Assert.assertEquals(
				"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_494d271b-844b-4f05-a971-7894664e52b8",
				result.get("http://www.coinsweb.nl/c-bim.owl#modifier"));
		Assert.assertEquals("This description is even nicer...",
				result.get("http://www.coinsweb.nl/c-bim.owl#description"));
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

	/**
	 * Testing NonFunctionalRequirement
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	public void testNonFunctionalRequirement() throws JsonProcessingException {
		// POST NonFunctionalRequirement
		String context = RestAssured.baseURI + ":" + RestAssured.port
				+ RestAssured.basePath;
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "non functional requirement name")
				.queryParam("layerIndex", 2)
				.queryParam(
						"creator",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_494d271b-844b-4f05-a971-7894664e32b8")
				.queryParam("nonFunctionalRequirementType", "A8")
				.queryParam("userID", "B1.1")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_NON_FUNCTIONAL_REQUIREMENT)
				.body();
		// GET non functional requirement
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
						+ CoinsApiWebService.PATH_NON_FUNCTIONAL_REQUIREMENT).body();
		Map<String, String> result = toKeyValueMapping(body.asString());
		Assert.assertEquals("non functional requirement name",
				result.get("http://www.coinsweb.nl/c-bim.owl#name"));
		Assert.assertEquals("B1.1",
				result.get("http://www.coinsweb.nl/c-bim.owl#userID"));
		Assert.assertEquals(
				"http://www.coinsweb.nl/c-bim-fs.owl#NonFunctionalRequirement",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals("2",
				result.get("http://www.coinsweb.nl/c-bim.owl#layerIndex"));
		Assert.assertEquals(
				"A8",
				result.get("http://www.coinsweb.nl/c-bim-fs.owl#nonFunctionalRequirementType"));
		Assert.assertEquals(
				"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_494d271b-844b-4f05-a971-7894664e32b8",
				result.get("http://www.coinsweb.nl/c-bim.owl#creator"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/c-bim.owl#creationDate"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/c-bim.owl#modificationDate"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/c-bim.owl#modifier"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/c-bim.owl#description"));
		// Set description
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("id", id)
				.queryParam("description", "This is a nice description!")
				.queryParam(
						"modifier",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_494d271b-844b-4f05-a971-7894664e32b8")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_DESCRIPTION);
		// Check it
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_NON_FUNCTIONAL_REQUIREMENT).body();
		result = toKeyValueMapping(body.asString());
		Assert.assertEquals("non functional requirement name",
				result.get("http://www.coinsweb.nl/c-bim.owl#name"));
		Assert.assertEquals("B1.1",
				result.get("http://www.coinsweb.nl/c-bim.owl#userID"));
		Assert.assertEquals("http://www.coinsweb.nl/c-bim-fs.owl#NonFunctionalRequirement",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals("2",
				result.get("http://www.coinsweb.nl/c-bim.owl#layerIndex"));
		Assert.assertEquals(
				"A8",
				result.get("http://www.coinsweb.nl/c-bim-fs.owl#nonFunctionalRequirementType"));
		Assert.assertEquals(
				"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_494d271b-844b-4f05-a971-7894664e32b8",
				result.get("http://www.coinsweb.nl/c-bim.owl#creator"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/c-bim.owl#creationDate"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/c-bim.owl#modificationDate"));
		Assert.assertEquals(
				"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_494d271b-844b-4f05-a971-7894664e32b8",
				result.get("http://www.coinsweb.nl/c-bim.owl#modifier"));
		Assert.assertEquals("This is a nice description!",
				result.get("http://www.coinsweb.nl/c-bim.owl#description"));

		// DELETE requirement
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("id", id)
				.expect()
				.statusCode(OK)
				.when()
				.delete(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_NON_FUNCTIONAL_REQUIREMENT);
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
						+ CoinsApiWebService.PATH_NON_FUNCTIONAL_REQUIREMENT).body();
		Assert.assertTrue(isEmpty(body.asString()));
	}

	/**
	 * Testing PersonOrOrganisation
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	public void testPersonOrOrganisation() throws JsonProcessingException {
		// POST PersonOrOrganisation
		String context = RestAssured.baseURI + ":" + RestAssured.port
				+ RestAssured.basePath;
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "Leon van Berlo")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_PERSON_OR_ORGANISATION)
				.body();
		// GET PersonOrOrganisation
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
						+ CoinsApiWebService.PATH_PERSON_OR_ORGANISATION)
				.body();
		Map<String, String> result = toKeyValueMapping(body.asString());
		Assert.assertEquals("Leon van Berlo",
				result.get("http://www.coinsweb.nl/c-bim.owl#name"));
		Assert.assertEquals(
				"http://www.coinsweb.nl/c-bim.owl#PersonOrOrganisation",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		// DELETE PersonOrOrganisation
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("id", id)
				.expect()
				.statusCode(OK)
				.when()
				.delete(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_PERSON_OR_ORGANISATION);
		// GET PersonOrOrganisation to check it has been removed
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_PERSON_OR_ORGANISATION)
				.body();
		Assert.assertTrue(isEmpty(body.asString()));
	}

	/**
	 * Testing PhysicalObject
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	public void testPhysicalObject() throws JsonProcessingException {
		// POST PhysicalObject
		String context = RestAssured.baseURI + ":" + RestAssured.port
				+ RestAssured.basePath;
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "Zitbank")
				.queryParam("layerIndex", 2)
				.queryParam(
						"creator",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_494d271b-844b-4f05-a971-7894664e32c8")
				.queryParam("userID", "B1")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_PHYSICAL_OBJECT).body();
		// GET PhysicalObject
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
						+ CoinsApiWebService.PATH_PHYSICAL_OBJECT).body();
		Map<String, String> result = toKeyValueMapping(body.asString());
		Assert.assertEquals("Zitbank",
				result.get("http://www.coinsweb.nl/c-bim.owl#name"));
		Assert.assertEquals("B1",
				result.get("http://www.coinsweb.nl/c-bim.owl#userID"));
		Assert.assertEquals("http://www.coinsweb.nl/c-bim.owl#PhysicalObject",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals("2",
				result.get("http://www.coinsweb.nl/c-bim.owl#layerIndex"));
		Assert.assertEquals(
				"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_494d271b-844b-4f05-a971-7894664e32c8",
				result.get("http://www.coinsweb.nl/c-bim.owl#creator"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/c-bim.owl#creationDate"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/c-bim.owl#modificationDate"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/c-bim.owl#modifier"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/c-bim.owl#description"));
		// Set description
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("id", id)
				.queryParam("description", "Prima zitbank")
				.queryParam(
						"modifier",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_494d271b-844b-4f05-a971-7894664e32b8")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_DESCRIPTION);
		// Check it
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_PHYSICAL_OBJECT).body();
		result = toKeyValueMapping(body.asString());
		Assert.assertEquals("Zitbank",
				result.get("http://www.coinsweb.nl/c-bim.owl#name"));
		Assert.assertEquals("B1",
				result.get("http://www.coinsweb.nl/c-bim.owl#userID"));
		Assert.assertEquals("http://www.coinsweb.nl/c-bim.owl#PhysicalObject",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals("2",
				result.get("http://www.coinsweb.nl/c-bim.owl#layerIndex"));
		Assert.assertEquals(
				"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_494d271b-844b-4f05-a971-7894664e32c8",
				result.get("http://www.coinsweb.nl/c-bim.owl#creator"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/c-bim.owl#creationDate"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/c-bim.owl#modificationDate"));
		Assert.assertEquals(
				"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_494d271b-844b-4f05-a971-7894664e32b8",
				result.get("http://www.coinsweb.nl/c-bim.owl#modifier"));
		Assert.assertEquals("Prima zitbank",
				result.get("http://www.coinsweb.nl/c-bim.owl#description"));
		// DELETE PhysicalObject
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("id", id)
				.expect()
				.statusCode(OK)
				.when()
				.delete(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_REQUIREMENT);
		// GET PhysicalObject to check it has been removed
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

	/**
	 * Testing Function
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	public void testFunction() throws JsonProcessingException {
		// POST function
		String context = RestAssured.baseURI + ":" + RestAssured.port
				+ RestAssured.basePath;
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "This is a function")
				.queryParam("layerIndex", 3)
				.queryParam(
						"creator",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_494d271b-844b-4f05-a971-7894664e32b8")
				.queryParam(
						"isFulfilledBy",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_3eb587eb-0de9-4a1a-a136-aea85266ce3c")
				.queryParam("userID", "C12")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_FUNCTION).body();
		// GET function
		String id = body.asString();
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_FUNCTION)
				.body();
		Map<String, String> result = toKeyValueMapping(body.asString());
		Assert.assertEquals("This is a function",
				result.get("http://www.coinsweb.nl/c-bim.owl#name"));
		Assert.assertEquals("C12",
				result.get("http://www.coinsweb.nl/c-bim.owl#userID"));
		Assert.assertEquals("http://www.coinsweb.nl/c-bim.owl#Function",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals("3",
				result.get("http://www.coinsweb.nl/c-bim.owl#layerIndex"));
		Assert.assertEquals(
				"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_3eb587eb-0de9-4a1a-a136-aea85266ce3c",
				result.get("http://www.coinsweb.nl/c-bim.owl#isFulfilledBy"));
		Assert.assertEquals(
				"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_494d271b-844b-4f05-a971-7894664e32b8",
				result.get("http://www.coinsweb.nl/c-bim.owl#creator"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/c-bim.owl#creationDate"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/c-bim.owl#modificationDate"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/c-bim.owl#modifier"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/c-bim.owl#description"));
		// Set description
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("id", id)
				.queryParam("description",
						"This is the description of a function")
				.queryParam(
						"modifier",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_494d271b-844b-4f05-a971-7894664e32b8")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_DESCRIPTION);
		// Check it
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_FUNCTION)
				.body();
		result = toKeyValueMapping(body.asString());
		Assert.assertEquals("This is a function",
				result.get("http://www.coinsweb.nl/c-bim.owl#name"));
		Assert.assertEquals("C12",
				result.get("http://www.coinsweb.nl/c-bim.owl#userID"));
		Assert.assertEquals("http://www.coinsweb.nl/c-bim.owl#Function",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals("3",
				result.get("http://www.coinsweb.nl/c-bim.owl#layerIndex"));
		Assert.assertEquals(
				"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_3eb587eb-0de9-4a1a-a136-aea85266ce3c",
				result.get("http://www.coinsweb.nl/c-bim.owl#isFulfilledBy"));
		Assert.assertEquals(
				"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_494d271b-844b-4f05-a971-7894664e32b8",
				result.get("http://www.coinsweb.nl/c-bim.owl#creator"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/c-bim.owl#creationDate"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/c-bim.owl#modificationDate"));
		Assert.assertEquals(
				"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_494d271b-844b-4f05-a971-7894664e32b8",
				result.get("http://www.coinsweb.nl/c-bim.owl#modifier"));
		Assert.assertEquals("This is the description of a function",
				result.get("http://www.coinsweb.nl/c-bim.owl#description"));

		// DELETE function
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("id", id)
				.expect()
				.statusCode(OK)
				.when()
				.delete(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_FUNCTION);
		// GET function to check it has been removed
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_FUNCTION)
				.body();
		Assert.assertTrue(isEmpty(body.asString()));
	}

	/**
	 * Testing Document
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	public void testDocument() throws JsonProcessingException {
		// POST document
		String context = RestAssured.baseURI + ":" + RestAssured.port
				+ RestAssured.basePath;
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "Document name")
				.queryParam(
						"creator",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_494d271b-844b-4f05-a971-7894664e32b8")
				.queryParam("userID", "D16")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_DOCUMENT).body();
		// GET document
		String id = body.asString();
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_DOCUMENT)
				.body();
		Map<String, String> result = toKeyValueMapping(body.asString());
		Assert.assertEquals("Document name",
				result.get("http://www.coinsweb.nl/c-bim.owl#name"));
		Assert.assertEquals("D16",
				result.get("http://www.coinsweb.nl/c-bim.owl#userID"));
		Assert.assertEquals("http://www.coinsweb.nl/c-bim.owl#Document",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals(
				"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_494d271b-844b-4f05-a971-7894664e32b8",
				result.get("http://www.coinsweb.nl/c-bim.owl#creator"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/c-bim.owl#creationDate"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/c-bim.owl#modificationDate"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/c-bim.owl#modifier"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/c-bim.owl#description"));
		// DELETE document
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("id", id)
				.expect()
				.statusCode(OK)
				.when()
				.delete(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_DOCUMENT);
		// GET document to check it has been removed
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_DOCUMENT)
				.body();
		Assert.assertTrue(isEmpty(body.asString()));
	}

	/**
	 * Testing Explicit3DRepresentation
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	public void testExplicit3DRepresentation() throws JsonProcessingException {
		// POST document
		String context = RestAssured.baseURI + ":" + RestAssured.port
				+ RestAssured.basePath;
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "3D Element - Ligger")
				.queryParam(
						"creator",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_494d271b-844b-4f05-a971-7894664e32b8")
				.queryParam("documentType", "IFC")
				.queryParam("documentAliasFilePath",
						"een zeer eenvoudige casus.ifc")
				.queryParam("documentUri", "http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#2K1FrCvpnAIuVj8HuCg0ML")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_EXPLICIT_3D_REPRESENTATION)
				.body();
		// GET document
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
						+ CoinsApiWebService.PATH_EXPLICIT_3D_REPRESENTATION)
				.body();
		Map<String, String> result = toKeyValueMapping(body.asString());
		Assert.assertEquals("3D Element - Ligger",
				result.get("http://www.coinsweb.nl/c-bim.owl#name"));
		Assert.assertEquals(
				"http://www.coinsweb.nl/c-bim.owl#Explicit3DRepresentation",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals(
				"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_494d271b-844b-4f05-a971-7894664e32b8",
				result.get("http://www.coinsweb.nl/c-bim.owl#creator"));
		Assert.assertEquals("IFC",
				result.get("http://www.coinsweb.nl/c-bim.owl#documentType"));
		Assert.assertEquals("een zeer eenvoudige casus.ifc", result
				.get("http://www.coinsweb.nl/c-bim.owl#documentAliasFilePath"));
		Assert.assertEquals("http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#2K1FrCvpnAIuVj8HuCg0ML",
				result.get("http://www.coinsweb.nl/c-bim.owl#documentUri"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/c-bim.owl#creationDate"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/c-bim.owl#modificationDate"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/c-bim.owl#modifier"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/c-bim.owl#description"));
		// DELETE Explicit3DRepresentation
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("id", id)
				.expect()
				.statusCode(OK)
				.when()
				.delete(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_EXPLICIT_3D_REPRESENTATION);
		// GET Explicit3DRepresentation to check it has been removed
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_EXPLICIT_3D_REPRESENTATION)
				.body();
		Assert.assertTrue(isEmpty(body.asString()));
	}

	/**
	 * Testing Vector
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	public void testVector() throws JsonProcessingException {
		// POST vector
		String context = RestAssured.baseURI + ":" + RestAssured.port
				+ RestAssured.basePath;
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "tmpPrmOrientation")
				.queryParam(
						"creator",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_494d271b-844b-4f05-a971-7894664e32b8")
				.queryParam("xCoordinate", "1.0")
				.queryParam("yCoordinate", "0.0")
				.queryParam("zCoordinate", "-1.0").expect().statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH + CoinsApiWebService.PATH_VECTOR)
				.body();
		// GET vector
		String id = body.asString();
		body = given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context).queryParam("id", id)
				.queryParam("output", "csv").expect().statusCode(OK).when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_VECTOR)
				.body();
		Map<String, String> result = toKeyValueMapping(body.asString());
		Assert.assertEquals("tmpPrmOrientation",
				result.get("http://www.coinsweb.nl/c-bim.owl#name"));
		Assert.assertEquals("http://www.coinsweb.nl/c-bim.owl#Vector",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals(
				"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_494d271b-844b-4f05-a971-7894664e32b8",
				result.get("http://www.coinsweb.nl/c-bim.owl#creator"));
		Assert.assertEquals("1.0",
				result.get("http://www.coinsweb.nl/c-bim.owl#xCoordinate"));
		Assert.assertEquals("0.0",
				result.get("http://www.coinsweb.nl/c-bim.owl#yCoordinate"));
		Assert.assertEquals("-1.0",
				result.get("http://www.coinsweb.nl/c-bim.owl#zCoordinate"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/c-bim.owl#creationDate"));
		// DELETE Vector
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("id", id)
				.expect()
				.statusCode(OK)
				.when()
				.delete(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_VECTOR);
		// GET Vector
		body = given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context).queryParam("id", id)
				.queryParam("output", "csv").expect().statusCode(OK).when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_VECTOR)
				.body();
		Assert.assertTrue(isEmpty(body.asString()));
	}

	/**
	 * Testing Locator
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	public void testLocator() throws JsonProcessingException {
		// POST locator
		String context = RestAssured.baseURI + ":" + RestAssured.port
				+ RestAssured.basePath;
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "Temporary locator")
				.queryParam(
						"creator",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_494d271b-844b-4f05-a971-7894664e32b8")
				.queryParam("primaryOrientation",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#tmpPrmOrientation")
				.queryParam("secondaryOrientation",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#tmpSecOrientation")
				.queryParam("translation",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#tmpTranslation")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH + CoinsApiWebService.PATH_LOCATOR)
				.body();
		// GET vector
		String id = body.asString();
		body = given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context).queryParam("id", id)
				.queryParam("output", "csv").expect().statusCode(OK).when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_LOCATOR)
				.body();
		Map<String, String> result = toKeyValueMapping(body.asString());
		Assert.assertEquals("Temporary locator",
				result.get("http://www.coinsweb.nl/c-bim.owl#name"));
		Assert.assertEquals("http://www.coinsweb.nl/c-bim.owl#Locator",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals(
				"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_494d271b-844b-4f05-a971-7894664e32b8",
				result.get("http://www.coinsweb.nl/c-bim.owl#creator"));
		Assert.assertEquals(
				"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#tmpPrmOrientation",
				result.get("http://www.coinsweb.nl/c-bim.owl#primaryOrientation"));
		Assert.assertEquals(
				"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#tmpSecOrientation",
				result.get("http://www.coinsweb.nl/c-bim.owl#secondaryOrientation"));
		Assert.assertEquals(
				"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#tmpTranslation",
				result.get("http://www.coinsweb.nl/c-bim.owl#translation"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/c-bim.owl#creationDate"));
		// DELETE Locator
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("id", id)
				.expect()
				.statusCode(OK)
				.when()
				.delete(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_LOCATOR);
		// GET Locator
		body = given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context).queryParam("id", id)
				.queryParam("output", "csv").expect().statusCode(OK).when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_LOCATOR)
				.body();
		Assert.assertTrue(isEmpty(body.asString()));
	}

	/**
	 * Testing Task
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	public void testTask() throws JsonProcessingException {
		// POST task
		String context = RestAssured.baseURI + ":" + RestAssured.port
				+ RestAssured.basePath;
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "Plaatsen steun links")
				.queryParam(
						"creator",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_494d271b-844b-4f05-a971-7894664e32b8")
				.queryParam("userID", "T001")
				.queryParam("taskType",
						"http://www.coinsweb.nl/c-bim.owl#Constructing")
				.queryParam(
						"affects",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_585e2f8e-33d4-41d3-bdaf-1bcbf085c827")
				.queryParam("startDatePlanned", "2010-09-01T08:00:00.000Z")
				.queryParam("endDatePlanned", "2010-09-03T18:00:00.000Z")
				.expect().statusCode(OK).when()
				.post(CoinsApiWebService.PATH + CoinsApiWebService.PATH_TASK)
				.body();
		// GET vector
		String id = body.asString();
		body = given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context).queryParam("id", id)
				.queryParam("output", "csv").expect().statusCode(OK).when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_TASK)
				.body();
		Map<String, String> result = toKeyValueMapping(body.asString());
		Assert.assertEquals("Plaatsen steun links",
				result.get("http://www.coinsweb.nl/c-bim.owl#name"));
		Assert.assertEquals("http://www.coinsweb.nl/c-bim.owl#Task",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals(
				"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_494d271b-844b-4f05-a971-7894664e32b8",
				result.get("http://www.coinsweb.nl/c-bim.owl#creator"));
		Assert.assertEquals("2010-09-03T18:00:00.000Z",
				result.get("http://www.coinsweb.nl/c-bim.owl#endDatePlanned"));
		Assert.assertEquals("2010-09-01T08:00:00.000Z",
				result.get("http://www.coinsweb.nl/c-bim.owl#startDatePlanned"));
		Assert.assertEquals(
				"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_585e2f8e-33d4-41d3-bdaf-1bcbf085c827",
				result.get("http://www.coinsweb.nl/c-bim.owl#affects"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/c-bim.owl#creationDate"));
		// DELETE Task
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context).queryParam("id", id).expect()
				.statusCode(OK).when()
				.delete(CoinsApiWebService.PATH + CoinsApiWebService.PATH_TASK);
		// GET Task
		body = given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context).queryParam("id", id)
				.queryParam("output", "csv").expect().statusCode(OK).when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_TASK)
				.body();
		Assert.assertTrue(isEmpty(body.asString()));
	}

	/**
	 * Testing Amount
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	public void testAmount() throws JsonProcessingException {
		// POST amount
		String context = RestAssured.baseURI + ":" + RestAssured.port
				+ RestAssured.basePath;
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "amount")
				.queryParam(
						"creator",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_494d271b-844b-4f05-a971-7894664e32b8")
				.queryParam("userID", "A 25.3")
				.queryParam("value", 2)
				.queryParam(
						"cataloguePart",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_844d271b-844b-4f05-a971-7894664e32b8")
				.expect().statusCode(OK).when()
				.post(CoinsApiWebService.PATH + CoinsApiWebService.PATH_AMOUNT)
				.body();
		// GET amount
		String id = body.asString();
		body = given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context).queryParam("id", id)
				.queryParam("output", "csv").expect().statusCode(OK).when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_AMOUNT)
				.body();
		Map<String, String> result = toKeyValueMapping(body.asString());
		Assert.assertEquals("amount",
				result.get("http://www.coinsweb.nl/c-bim.owl#name"));
		Assert.assertEquals("http://www.coinsweb.nl/c-bim.owl#Amount",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals(
				"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_494d271b-844b-4f05-a971-7894664e32b8",
				result.get("http://www.coinsweb.nl/c-bim.owl#creator"));
		Assert.assertEquals("2",
				result.get("http://www.coinsweb.nl/c-bim.owl#value"));
		Assert.assertEquals("A 25.3",
				result.get("http://www.coinsweb.nl/c-bim.owl#userID"));
		Assert.assertEquals("http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_844d271b-844b-4f05-a971-7894664e32b8",
				result.get("http://www.coinsweb.nl/c-bim.owl#cataloguePart"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/c-bim.owl#creationDate"));
		// DELETE amount
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("id", id)
				.expect()
				.statusCode(OK)
				.when()
				.delete(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_AMOUNT);
		// GET Amount
		body = given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context).queryParam("id", id)
				.queryParam("output", "csv").expect().statusCode(OK).when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_AMOUNT)
				.body();
		Assert.assertTrue(isEmpty(body.asString()));
	}

	/**
	 * Testing CataloguePart
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	public void testCataloguePart() throws JsonProcessingException {
		// POST CataloguePart
		String context = RestAssured.baseURI + ":" + RestAssured.port
				+ RestAssured.basePath;
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "A catalogue part")
				.queryParam(
						"creator",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_494d271b-844b-4f05-a971-7894664e32b8")
				.queryParam("userID", "C 123")
				.expect().statusCode(OK).when()
				.post(CoinsApiWebService.PATH + CoinsApiWebService.PATH_CATALOGUE_PART)
				.body();
		// GET CataloguePart
		String id = body.asString();
		body = given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context).queryParam("id", id)
				.queryParam("output", "csv").expect().statusCode(OK).when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_CATALOGUE_PART)
				.body();
		Map<String, String> result = toKeyValueMapping(body.asString());
		Assert.assertEquals("A catalogue part",
				result.get("http://www.coinsweb.nl/c-bim.owl#name"));
		Assert.assertEquals("C 123",
				result.get("http://www.coinsweb.nl/c-bim.owl#userID"));
		Assert.assertEquals("http://www.coinsweb.nl/c-bim.owl#CataloguePart",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals(
				"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_494d271b-844b-4f05-a971-7894664e32b8",
				result.get("http://www.coinsweb.nl/c-bim.owl#creator"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/c-bim.owl#creationDate"));
		// DELETE CataloguePart
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("id", id)
				.expect()
				.statusCode(OK)
				.when()
				.delete(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_CATALOGUE_PART);
		// GET CataloguePart
		body = given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context).queryParam("id", id)
				.queryParam("output", "csv").expect().statusCode(OK).when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_CATALOGUE_PART)
				.body();
		Assert.assertTrue(isEmpty(body.asString()));
	}
	
	private Map<String, String> toKeyValueMapping(String pCsvString) {
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

	private boolean isEmpty(String pCsvString) {
		return (pCsvString.replace("name", "").replace(",", "")
				.replace("value", "").trim().length() == 0);
	}
}
