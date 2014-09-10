package nl.tno.coinsapi.webservices;

import static com.jayway.restassured.RestAssured.given;

import java.util.List;
import java.util.Map;

import nl.tno.coinsapi.util.TestUtil;

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

	private static JettyMarmotta mMarmotta;

	private static String mContext;

	private static String mCreatorId;

	/**
	 * Setting up Marmotta test service and RestAssured
	 */
	@BeforeClass
	public static void setUp() {
		mMarmotta = new JettyMarmotta("/marmotta", CoinsApiWebService.class);

		RestAssured.baseURI = "http://localhost";
		RestAssured.port = mMarmotta.getPort();
		RestAssured.basePath = mMarmotta.getContext();

		mContext = RestAssured.baseURI + ":" + RestAssured.port
				+ RestAssured.basePath;

		// POST PersonOrOrganisation (creator/modifier)
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "Pietje Puk")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_PERSON_OR_ORGANISATION)
				.body();
		mCreatorId = body.asString();
	}

	/**
	 * Tear down
	 */
	@AfterClass
	public static void tearDown() {
		mMarmotta.shutdown();
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
		Assert.assertEquals("v0.1", resp.getBody().asString());
	}

	/**
	 * Testing Requirement
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	public void testRequirement() throws JsonProcessingException {
		// Post function
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "This is a function")
				.queryParam("layerIndex", 3)
				.queryParam("creator", mCreatorId)
				.queryParam("userID", "C12")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_FUNCTION).body();
		String functionId = body.asString();
		// POST requirement
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "requirement name")
				.queryParam("layerIndex", 3)
				.queryParam("creator", mCreatorId)
				.queryParam("requirementOf", functionId)
				.queryParam("userID", "B1.1")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_REQUIREMENT).body();
		String id = body.asString();
		// GET requirement
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_REQUIREMENT).body();
		Map<String, String> result = TestUtil
				.toKeyValueMapping(body.asString());
		Assert.assertEquals("requirement name",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#name"));
		Assert.assertEquals("B1.1",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#userID"));
		Assert.assertEquals("http://www.coinsweb.nl/cbim-1.1.owl#Requirement",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals("3",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#layerIndex"));
		Assert.assertEquals(functionId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#requirementOf"));
		Assert.assertEquals(mCreatorId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#creator"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#creationDate"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#modificationDate"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#modifier"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#description"));
		// Set description
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("description", "This is a nice description!")
				.queryParam("modifier", mCreatorId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_DESCRIPTION);
		// Check it
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_REQUIREMENT).body();
		result = TestUtil.toKeyValueMapping(body.asString());
		Assert.assertEquals("requirement name",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#name"));
		Assert.assertEquals("B1.1",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#userID"));
		Assert.assertEquals("http://www.coinsweb.nl/cbim-1.1.owl#Requirement",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals("3",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#layerIndex"));
		Assert.assertEquals(functionId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#requirementOf"));
		Assert.assertEquals(mCreatorId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#creator"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#creationDate"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#modificationDate"));
		Assert.assertEquals(mCreatorId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#modifier"));
		Assert.assertEquals("This is a nice description!",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#description"));
		// Set the description again
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("description", "This description is even nicer...")
				.queryParam("modifier", mCreatorId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_DESCRIPTION);
		// Check for duplicate descriptions...
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_REQUIREMENT).body();
		result = TestUtil.toKeyValueMapping(body.asString());
		Assert.assertEquals("requirement name",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#name"));
		Assert.assertEquals("B1.1",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#userID"));
		Assert.assertEquals("http://www.coinsweb.nl/cbim-1.1.owl#Requirement",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals("3",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#layerIndex"));
		Assert.assertEquals(functionId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#requirementOf"));
		Assert.assertEquals(mCreatorId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#creator"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#creationDate"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#modificationDate"));
		Assert.assertEquals(mCreatorId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#modifier"));
		Assert.assertEquals("This description is even nicer...",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#description"));
		validate(mContext);
		// DELETE requirement
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.expect()
				.statusCode(OK)
				.when()
				.delete(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_REQUIREMENT);
		// GET requirement to check it has been removed
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_REQUIREMENT).body();
		Assert.assertTrue(TestUtil.isEmpty(body.asString()));
	}

	/**
	 * Only fill in the base path for the context. Coins can fill in the rest of
	 * it
	 */
	@Test
	public void testContext() {
		// POST requirement
		String context = "marmotta";
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "requirement name")
				.queryParam("layerIndex", 3)
				.queryParam("creator", mCreatorId)
				.queryParam(
						"requirementOf",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_3eb587eb-0de9-4a1a-a136-aea85266ce3c")
				.queryParam("userID", "B1.1")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_REQUIREMENT).body();
		String id = body.asString();
		// GET requirement to check it is available removed
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
		Assert.assertFalse(TestUtil.isEmpty(body.asString()));
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
		Assert.assertTrue(TestUtil.isEmpty(body.asString()));
	}

	/**
	 * Testing NonFunctionalRequirement
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	public void testNonFunctionalRequirement() throws JsonProcessingException {
		// POST NonFunctionalRequirement
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "non functional requirement name")
				.queryParam("layerIndex", 2)
				.queryParam("creator", mCreatorId)
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
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_NON_FUNCTIONAL_REQUIREMENT)
				.body();
		Map<String, String> result = TestUtil
				.toKeyValueMapping(body.asString());
		Assert.assertEquals("non functional requirement name",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#name"));
		Assert.assertEquals("B1.1",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#userID"));
		Assert.assertEquals(
				"http://www.coinsweb.nl/c-bim-fs.owl#NonFunctionalRequirement",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals("2",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#layerIndex"));
		Assert.assertEquals(
				"A8",
				result.get("http://www.coinsweb.nl/c-bim-fs.owl#nonFunctionalRequirementType"));
		Assert.assertEquals(mCreatorId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#creator"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#creationDate"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#modificationDate"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#modifier"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#description"));
		// Set description
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("description", "This is a nice description!")
				.queryParam("modifier", mCreatorId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_DESCRIPTION);
		// Check it
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_NON_FUNCTIONAL_REQUIREMENT)
				.body();
		result = TestUtil.toKeyValueMapping(body.asString());
		Assert.assertEquals("non functional requirement name",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#name"));
		Assert.assertEquals("B1.1",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#userID"));
		Assert.assertEquals(
				"http://www.coinsweb.nl/c-bim-fs.owl#NonFunctionalRequirement",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals("2",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#layerIndex"));
		Assert.assertEquals(
				"A8",
				result.get("http://www.coinsweb.nl/c-bim-fs.owl#nonFunctionalRequirementType"));
		Assert.assertEquals(mCreatorId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#creator"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#creationDate"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#modificationDate"));
		Assert.assertEquals(mCreatorId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#modifier"));
		Assert.assertEquals("This is a nice description!",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#description"));
		validate(mContext);
		// DELETE requirement
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.expect()
				.statusCode(OK)
				.when()
				.delete(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_NON_FUNCTIONAL_REQUIREMENT);
		// GET requirement to check it has been removed
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_NON_FUNCTIONAL_REQUIREMENT)
				.body();
		Assert.assertTrue(TestUtil.isEmpty(body.asString()));
	}

	/**
	 * Testing PersonOrOrganisation
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	public void testPersonOrOrganisation() throws JsonProcessingException {
		// POST PersonOrOrganisation
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
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
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_PERSON_OR_ORGANISATION)
				.body();
		Map<String, String> result = TestUtil
				.toKeyValueMapping(body.asString());
		Assert.assertEquals("Leon van Berlo",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#name"));
		Assert.assertEquals(
				"http://www.coinsweb.nl/cbim-1.1.owl#PersonOrOrganisation",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		validate(mContext);
		// DELETE PersonOrOrganisation
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.expect()
				.statusCode(OK)
				.when()
				.delete(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_PERSON_OR_ORGANISATION);
		// GET PersonOrOrganisation to check it has been removed
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_PERSON_OR_ORGANISATION)
				.body();
		Assert.assertTrue(TestUtil.isEmpty(body.asString()));
	}

	/**
	 * Testing PropertyType
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	public void testPropertyType() throws JsonProcessingException {
		// POST PropertyType
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "property type")
				.queryParam("userID", "pt1")
				.queryParam("unit", "meters")
				.queryParam("valuedomain", "cbim:XsdBoolean")
				.queryParam("creator", mCreatorId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_PROPERTY_TYPE)
				.body();
		// GET Property type
		String id = body.asString();
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_PROPERTY_TYPE)
				.body();
		Map<String, String> result = TestUtil
				.toKeyValueMapping(body.asString());
		
		Assert.assertEquals("property type",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#name"));
		Assert.assertEquals("pt1",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#userID"));
		Assert.assertEquals("meters",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#unit"));
		Assert.assertEquals("http://www.coinsweb.nl/cbim-1.1.owl#XsdBoolean",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#valueDomain"));
		Assert.assertEquals(
				"http://www.coinsweb.nl/cbim-1.1.owl#PropertyType",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		validate(mContext);
		// DELETE PropertyType
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.expect()
				.statusCode(OK)
				.when()
				.delete(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_PROPERTY_TYPE);
		// GET PropertyType to check it has been removed
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_PROPERTY_TYPE)
				.body();
		Assert.assertTrue(TestUtil.isEmpty(body.asString()));
	}

	/**
	 * Testing PropertyValue
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	public void testPropertyValue() throws JsonProcessingException {
		// POST PropertyType
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "property type")
				.queryParam("userID", "pt1")
				.queryParam("unit", "meters")
				.queryParam("valuedomain", "cbim:XsdBoolean")
				.queryParam("creator", mCreatorId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_PROPERTY_TYPE)
				.body();
		String propertyTypeId = body.asString();
		// POST PropertyValue
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "property value")
				.queryParam("userID", "pv1")
				.queryParam("value", true)
				.queryParam("propertytype", propertyTypeId)
				.queryParam("creator", mCreatorId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_PROPERTY_VALUE)
				.body();
		String id = body.asString();
		// GET Property value
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_PROPERTY_VALUE)
				.body();
		Map<String, String> result = TestUtil
				.toKeyValueMapping(body.asString());		
		Assert.assertEquals("property value",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#name"));
		Assert.assertEquals("pv1",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#userID"));
		Assert.assertEquals("true",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#value"));
		Assert.assertEquals(propertyTypeId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#propertyType"));
		Assert.assertEquals(
				"http://www.coinsweb.nl/cbim-1.1.owl#PropertyValue",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		validate(mContext);
		// DELETE PropertyValue
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.expect()
				.statusCode(OK)
				.when()
				.delete(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_PROPERTY_VALUE);
		// GET PropertyType to check it has been removed
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_PROPERTY_VALUE)
				.body();
		Assert.assertTrue(TestUtil.isEmpty(body.asString()));
	}

	/**
	 * Testing PhysicalObject
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	public void testPhysicalObject() throws JsonProcessingException {
		// POST PhysicalObject
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "Zitbank")
				.queryParam("layerIndex", 2)
				.queryParam("creator", mCreatorId)
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
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_PHYSICAL_OBJECT).body();
		Map<String, String> result = TestUtil
				.toKeyValueMapping(body.asString());
		Assert.assertEquals("Zitbank",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#name"));
		Assert.assertEquals("B1",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#userID"));
		Assert.assertEquals("http://www.coinsweb.nl/cbim-1.1.owl#PhysicalObject",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals("2",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#layerIndex"));
		Assert.assertEquals(mCreatorId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#creator"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#creationDate"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#modificationDate"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#modifier"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#description"));
		// Set description
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("description", "Prima zitbank")
				.queryParam("modifier", mCreatorId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_DESCRIPTION);
		// Check it
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_PHYSICAL_OBJECT).body();
		result = TestUtil.toKeyValueMapping(body.asString());
		Assert.assertEquals("Zitbank",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#name"));
		Assert.assertEquals("B1",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#userID"));
		Assert.assertEquals("http://www.coinsweb.nl/cbim-1.1.owl#PhysicalObject",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals("2",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#layerIndex"));
		Assert.assertEquals(mCreatorId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#creator"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#creationDate"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#modificationDate"));
		Assert.assertEquals(mCreatorId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#modifier"));
		Assert.assertEquals("Prima zitbank",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#description"));
		validate(mContext);
		// DELETE PhysicalObject
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.expect()
				.statusCode(OK)
				.when()
				.delete(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_PHYSICAL_OBJECT);
		// GET PhysicalObject to check it has been removed
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_PHYSICAL_OBJECT).body();
		Assert.assertTrue(TestUtil.isEmpty(body.asString()));
	}

	/**
	 * Testing Space
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	public void testSpace() throws JsonProcessingException {
		// POST PhysicalObject
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "Foyer").queryParam("layerIndex", 2)
				.queryParam("creator", mCreatorId).queryParam("userID", "S1")
				.expect().statusCode(OK).when()
				.post(CoinsApiWebService.PATH + CoinsApiWebService.PATH_SPACE)
				.body();
		// GET Space
		String id = body.asString();
		body = given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext).queryParam("id", id)
				.queryParam("output", "csv").expect().statusCode(OK).when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_SPACE)
				.body();
		Map<String, String> result = TestUtil
				.toKeyValueMapping(body.asString());
		Assert.assertEquals("Foyer",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#name"));
		Assert.assertEquals("S1",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#userID"));
		Assert.assertEquals("http://www.coinsweb.nl/cbim-1.1.owl#Space",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals("2",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#layerIndex"));
		Assert.assertEquals(mCreatorId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#creator"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#creationDate"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#modificationDate"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#modifier"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#description"));
		// Set description
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("description", "Een ruime foyer")
				.queryParam("modifier", mCreatorId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_DESCRIPTION);
		// Check it
		body = given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext).queryParam("id", id)
				.queryParam("output", "csv").expect().statusCode(OK).when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_SPACE)
				.body();
		result = TestUtil.toKeyValueMapping(body.asString());
		Assert.assertEquals("Foyer",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#name"));
		Assert.assertEquals("S1",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#userID"));
		Assert.assertEquals("http://www.coinsweb.nl/cbim-1.1.owl#Space",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals("2",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#layerIndex"));
		Assert.assertEquals(mCreatorId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#creator"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#creationDate"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#modificationDate"));
		Assert.assertEquals(mCreatorId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#modifier"));
		Assert.assertEquals("Een ruime foyer",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#description"));
		validate(mContext);
		// DELETE Space
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.expect()
				.statusCode(OK)
				.when()
				.delete(CoinsApiWebService.PATH + CoinsApiWebService.PATH_SPACE);
		// GET Space to check it has been removed
		body = given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext).queryParam("id", id)
				.queryParam("output", "csv").expect().statusCode(OK).when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_SPACE)
				.body();
		Assert.assertTrue(TestUtil.isEmpty(body.asString()));
	}

	/**
	 * Testing Function
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	public void testFunction() throws JsonProcessingException {
		// POST function
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "This is a function")
				.queryParam("layerIndex", 3)
				.queryParam("creator", mCreatorId)
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
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_FUNCTION)
				.body();
		Map<String, String> result = TestUtil
				.toKeyValueMapping(body.asString());
		Assert.assertEquals("This is a function",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#name"));
		Assert.assertEquals("C12",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#userID"));
		Assert.assertEquals("http://www.coinsweb.nl/cbim-1.1.owl#Function",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals("3",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#layerIndex"));
		Assert.assertEquals(mCreatorId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#creator"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#creationDate"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#modificationDate"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#modifier"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#description"));
		// Set description
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("description",
						"This is the description of a function")
				.queryParam("modifier", mCreatorId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_DESCRIPTION);
		// Check it
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_FUNCTION)
				.body();
		result = TestUtil.toKeyValueMapping(body.asString());
		Assert.assertEquals("This is a function",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#name"));
		Assert.assertEquals("C12",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#userID"));
		Assert.assertEquals("http://www.coinsweb.nl/cbim-1.1.owl#Function",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals("3",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#layerIndex"));
		Assert.assertEquals(mCreatorId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#creator"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#creationDate"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#modificationDate"));
		Assert.assertEquals(mCreatorId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#modifier"));
		Assert.assertEquals("This is the description of a function",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#description"));
		validate(mContext);
		// DELETE function
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.expect()
				.statusCode(OK)
				.when()
				.delete(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_FUNCTION);
		// GET function to check it has been removed
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_FUNCTION)
				.body();
		Assert.assertTrue(TestUtil.isEmpty(body.asString()));
	}

	/**
	 * Testing Document
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	public void testDocument() throws JsonProcessingException {
		// POST document
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "Document name")
				.queryParam("creator", mCreatorId)
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
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_DOCUMENT)
				.body();
		Map<String, String> result = TestUtil
				.toKeyValueMapping(body.asString());
		Assert.assertEquals("Document name",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#name"));
		Assert.assertEquals("D16",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#userID"));
		Assert.assertEquals("http://www.coinsweb.nl/cbim-1.1.owl#Document",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals(mCreatorId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#creator"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#creationDate"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#modificationDate"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#modifier"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#description"));
		validate(mContext);
		// DELETE document
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.expect()
				.statusCode(OK)
				.when()
				.delete(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_DOCUMENT);
		// GET document to check it has been removed
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_DOCUMENT)
				.body();
		Assert.assertTrue(TestUtil.isEmpty(body.asString()));
	}

	/**
	 * Testing Explicit3DRepresentation
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	public void testExplicit3DRepresentation() throws JsonProcessingException {
		// POST document
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "3D Element - Ligger")
				.queryParam("creator", mCreatorId)
				.queryParam("documentType", "IFC")
				.queryParam("documentAliasFilePath",
						"een zeer eenvoudige casus.ifc")
				.queryParam(
						"documentUri",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#2K1FrCvpnAIuVj8HuCg0ML")
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
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_EXPLICIT_3D_REPRESENTATION)
				.body();
		Map<String, String> result = TestUtil
				.toKeyValueMapping(body.asString());
		Assert.assertEquals("3D Element - Ligger",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#name"));
		Assert.assertEquals(
				"http://www.coinsweb.nl/cbim-1.1.owl#Explicit3DRepresentation",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals(mCreatorId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#creator"));
		Assert.assertEquals("IFC",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#documentType"));
		Assert.assertEquals("een zeer eenvoudige casus.ifc", result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#documentAliasFilePath"));
		Assert.assertEquals(
				"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#2K1FrCvpnAIuVj8HuCg0ML",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#documentUri"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#creationDate"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#modificationDate"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#modifier"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#description"));
		validate(mContext);
		// DELETE Explicit3DRepresentation
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.expect()
				.statusCode(OK)
				.when()
				.delete(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_EXPLICIT_3D_REPRESENTATION);
		// GET Explicit3DRepresentation to check it has been removed
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_EXPLICIT_3D_REPRESENTATION)
				.body();
		Assert.assertTrue(TestUtil.isEmpty(body.asString()));
	}

	/**
	 * Testing Vector
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	public void testVector() throws JsonProcessingException {
		// POST vector
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "tmpPrmOrientation")
				.queryParam("creator", mCreatorId)
				.queryParam("xCoordinate", "1.1")
				.queryParam("yCoordinate", "0.0")
				.queryParam("zCoordinate", "-1.0").expect().statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH + CoinsApiWebService.PATH_VECTOR)
				.body();
		// GET vector
		String id = body.asString();
		body = given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext).queryParam("id", id)
				.queryParam("output", "csv").expect().statusCode(OK).when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_VECTOR)
				.body();
		Map<String, String> result = TestUtil
				.toKeyValueMapping(body.asString());
		Assert.assertEquals("tmpPrmOrientation",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#name"));
		Assert.assertEquals("http://www.coinsweb.nl/cbim-1.1.owl#Vector",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals(mCreatorId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#creator"));
		Assert.assertEquals("1.1",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#xCoordinate"));
		Assert.assertEquals("0",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#yCoordinate"));
		Assert.assertEquals("-1",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#zCoordinate"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#creationDate"));
		validate(mContext);
		// DELETE Vector
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.expect()
				.statusCode(OK)
				.when()
				.delete(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_VECTOR);
		// GET Vector
		body = given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext).queryParam("id", id)
				.queryParam("output", "csv").expect().statusCode(OK).when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_VECTOR)
				.body();
		Assert.assertTrue(TestUtil.isEmpty(body.asString()));
	}

	/**
	 * Testing Locator
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	public void testLocator() throws JsonProcessingException {
		// POST vectors
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "tmpPrmOrientation")
				.queryParam("creator", mCreatorId)
				.queryParam("xCoordinate", "1.1")
				.queryParam("yCoordinate", "0.0")
				.queryParam("zCoordinate", "-1.0").expect().statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH + CoinsApiWebService.PATH_VECTOR)
				.body();
		String tmpPrmOrientation = body.asString();
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "tmpSecOrientation")
				.queryParam("creator", mCreatorId)
				.queryParam("xCoordinate", "1.1")
				.queryParam("yCoordinate", "1.0")
				.queryParam("zCoordinate", "0.0").expect().statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH + CoinsApiWebService.PATH_VECTOR)
				.body();
		String tmpSecOrientation = body.asString();
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "tmpTranslation")
				.queryParam("creator", mCreatorId)
				.queryParam("xCoordinate", "0.0")
				.queryParam("yCoordinate", "0.0")
				.queryParam("zCoordinate", "0.0").expect().statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH + CoinsApiWebService.PATH_VECTOR)
				.body();
		String tmpTranslation = body.asString();
		// POST locator
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "Temporary locator")
				.queryParam("creator", mCreatorId)
				.queryParam("primaryOrientation", tmpPrmOrientation)
				.queryParam("secondaryOrientation", tmpSecOrientation)
				.queryParam("translation", tmpTranslation)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH + CoinsApiWebService.PATH_LOCATOR)
				.body();
		// GET locator
		String id = body.asString();
		body = given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext).queryParam("id", id)
				.queryParam("output", "csv").expect().statusCode(OK).when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_LOCATOR)
				.body();
		Map<String, String> result = TestUtil
				.toKeyValueMapping(body.asString());
		Assert.assertEquals("Temporary locator",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#name"));
		Assert.assertEquals("http://www.coinsweb.nl/cbim-1.1.owl#Locator",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals(mCreatorId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#creator"));
		Assert.assertEquals(tmpPrmOrientation, result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#primaryOrientation"));
		Assert.assertEquals(tmpSecOrientation, result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#secondaryOrientation"));
		Assert.assertEquals(tmpTranslation,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#translation"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#creationDate"));
		validate(mContext);
		// DELETE Locator
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.expect()
				.statusCode(OK)
				.when()
				.delete(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_LOCATOR);
		// GET Locator
		body = given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext).queryParam("id", id)
				.queryParam("output", "csv").expect().statusCode(OK).when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_LOCATOR)
				.body();
		Assert.assertTrue(TestUtil.isEmpty(body.asString()));
	}

	/**
	 * Test bounding box
	 */
	@Test
	public void testBoundingBox() {
		String locatorId = createLocator();
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "max bounding box")
				.queryParam("creator", mCreatorId)
				.queryParam("xCoordinate", "100")
				.queryParam("yCoordinate", "120")
				.queryParam("zCoordinate", "1000").expect().statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH + CoinsApiWebService.PATH_VECTOR)
				.body();
		String maxBoundingBoxId = body.asString();
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "min bounding box")
				.queryParam("creator", mCreatorId)
				.queryParam("xCoordinate", "10")
				.queryParam("yCoordinate", "12")
				.queryParam("zCoordinate", "100").expect().statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH + CoinsApiWebService.PATH_VECTOR)
				.body();
		String minBoundingBoxId = body.asString();
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("locator", locatorId)
				.queryParam("minboundingbox", minBoundingBoxId)
				.queryParam("modifier", mCreatorId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_LINK_MIN_BOUNDING_BOX).body();
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("locator", locatorId)
				.queryParam("maxboundingbox", maxBoundingBoxId)
				.queryParam("modifier", mCreatorId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_LINK_MAX_BOUNDING_BOX).body();
		validate(mContext);
		body = given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext).queryParam("id", locatorId)
				.queryParam("output", "csv").expect().statusCode(OK).when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_LOCATOR)
				.body();
		Map<String, String> result = TestUtil
				.toKeyValueMapping(body.asString());
		Assert.assertEquals("Temporary locator",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#name"));
		Assert.assertEquals("http://www.coinsweb.nl/cbim-1.1.owl#Locator",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals(mCreatorId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#creator"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#primaryOrientation"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#secondaryOrientation"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#translation"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#creationDate"));
		Assert.assertEquals(maxBoundingBoxId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#maxBoundingBox"));
		Assert.assertEquals(minBoundingBoxId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#minBoundingBox"));
	}

	/**
	 * Testing Task
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	public void testTask() throws JsonProcessingException {
		// POST PhysicalObject
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "Zitbank")
				.queryParam("layerIndex", 2)
				.queryParam("creator", mCreatorId)
				.queryParam("userID", "B1")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_PHYSICAL_OBJECT).body();
		String physicalObjectId = body.asString();
		// POST task
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "Plaatsen steun links")
				.queryParam("creator", mCreatorId)
				.queryParam("userID", "T001")
				.queryParam("taskType",
						"http://www.coinsweb.nl/cbim-1.1.owl#Constructing")
				.queryParam("affects", physicalObjectId)
				.queryParam("startDatePlanned", "2010-09-01T08:00:00.000Z")
				.queryParam("endDatePlanned", "2010-09-03T18:00:00.000Z")
				.expect().statusCode(OK).when()
				.post(CoinsApiWebService.PATH + CoinsApiWebService.PATH_TASK)
				.body();
		// GET task
		String id = body.asString();
		body = given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext).queryParam("id", id)
				.queryParam("output", "csv").expect().statusCode(OK).when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_TASK)
				.body();
		Map<String, String> result = TestUtil
				.toKeyValueMapping(body.asString());
		Assert.assertEquals("Plaatsen steun links",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#name"));
		Assert.assertEquals("http://www.coinsweb.nl/cbim-1.1.owl#Task",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals(mCreatorId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#creator"));
		Assert.assertEquals("2010-09-03T18:00:00.000Z",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#endDatePlanned"));
		Assert.assertEquals("2010-09-01T08:00:00.000Z",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#startDatePlanned"));
		Assert.assertEquals(physicalObjectId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#affects"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#creationDate"));
		validate(mContext);
		// DELETE Task
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext).queryParam("id", id).expect()
				.statusCode(OK).when()
				.delete(CoinsApiWebService.PATH + CoinsApiWebService.PATH_TASK);
		// GET Task
		body = given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext).queryParam("id", id)
				.queryParam("output", "csv").expect().statusCode(OK).when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_TASK)
				.body();
		Assert.assertTrue(TestUtil.isEmpty(body.asString()));
	}

	/**
	 * Testing Amount
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	public void testAmount() throws JsonProcessingException {
		// POST amount
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "amount")
				.queryParam("creator", mCreatorId)
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
				.queryParam("context", mContext).queryParam("id", id)
				.queryParam("output", "csv").expect().statusCode(OK).when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_AMOUNT)
				.body();
		Map<String, String> result = TestUtil
				.toKeyValueMapping(body.asString());
		Assert.assertEquals("amount",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#name"));
		Assert.assertEquals("http://www.coinsweb.nl/cbim-1.1.owl#Amount",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals(mCreatorId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#creator"));
		Assert.assertEquals("2",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#value"));
		Assert.assertEquals("A 25.3",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#userID"));
		Assert.assertEquals(
				"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_844d271b-844b-4f05-a971-7894664e32b8",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#cataloguePart"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#creationDate"));
		validate(mContext);
		// DELETE amount
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.expect()
				.statusCode(OK)
				.when()
				.delete(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_AMOUNT);
		// GET Amount
		body = given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext).queryParam("id", id)
				.queryParam("output", "csv").expect().statusCode(OK).when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_AMOUNT)
				.body();
		Assert.assertTrue(TestUtil.isEmpty(body.asString()));
	}

	/**
	 * Testing CataloguePart
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	public void testCataloguePart() throws JsonProcessingException {
		// POST CataloguePart
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "A catalogue part")
				.queryParam("creator", mCreatorId)
				.queryParam("userID", "C 123")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_CATALOGUE_PART).body();
		// GET CataloguePart
		String id = body.asString();
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_CATALOGUE_PART).body();
		Map<String, String> result = TestUtil
				.toKeyValueMapping(body.asString());
		Assert.assertEquals("A catalogue part",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#name"));
		Assert.assertEquals("C 123",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#userID"));
		Assert.assertEquals("http://www.coinsweb.nl/cbim-1.1.owl#CataloguePart",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals(mCreatorId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#creator"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#creationDate"));
		validate(mContext);
		// DELETE CataloguePart
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.expect()
				.statusCode(OK)
				.when()
				.delete(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_CATALOGUE_PART);
		// GET CataloguePart
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_CATALOGUE_PART).body();
		Assert.assertTrue(TestUtil.isEmpty(body.asString()));
	}

	/**
	 * Testing Connection
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	public void testConnection() throws JsonProcessingException {
		// POST Connection
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "Connection")
				.queryParam("creator", mCreatorId)
				.queryParam("userID", "CC 294s")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_CONNECTION).body();
		// GET Connection
		String id = body.asString();
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_CONNECTION).body();
		Map<String, String> result = TestUtil
				.toKeyValueMapping(body.asString());
		Assert.assertEquals("Connection",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#name"));
		Assert.assertEquals("CC 294s",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#userID"));
		Assert.assertEquals("http://www.coinsweb.nl/cbim-1.1.owl#Connection",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals(mCreatorId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#creator"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#creationDate"));
		validate(mContext);
		// DELETE Connection
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.expect()
				.statusCode(OK)
				.when()
				.delete(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_CONNECTION);
		// GET Connection
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_CONNECTION).body();
		Assert.assertTrue(TestUtil.isEmpty(body.asString()));
	}

	/**
	 * Testing State
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	public void testState() throws JsonProcessingException {
		// POST Connection
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "State")
				.queryParam("creator", mCreatorId)
				.queryParam("userID", "ST 1")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_STATE).body();
		// GET State
		String id = body.asString();
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_STATE).body();
		Map<String, String> result = TestUtil
				.toKeyValueMapping(body.asString());
		Assert.assertEquals("State",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#name"));
		Assert.assertEquals("ST 1",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#userID"));
		Assert.assertEquals("http://www.coinsweb.nl/cbim-1.1.owl#State",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals(mCreatorId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#creator"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#creationDate"));
		validate(mContext);
		// DELETE State
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.expect()
				.statusCode(OK)
				.when()
				.delete(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_STATE);
		// GET Connection
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_STATE).body();
		Assert.assertTrue(TestUtil.isEmpty(body.asString()));
	}
		
	/**
	 * Test the physical parent relation between two PhysicalObjects
	 */
	@Test
	public void testPhysicalParent() {
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "Parent A")
				.queryParam("layerIndex", 2)
				.queryParam("creator", mCreatorId)
				.queryParam("userID", "A")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_PHYSICAL_OBJECT).body();
		String parentA = body.asString();
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "Parent B")
				.queryParam("layerIndex", 2)
				.queryParam("creator", mCreatorId)
				.queryParam("userID", "B")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_PHYSICAL_OBJECT).body();
		String parentB = body.asString();
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "Child")
				.queryParam("layerIndex", 2)
				.queryParam("creator", mCreatorId)
				.queryParam("userID", "C 1")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_PHYSICAL_OBJECT).body();
		String child = body.asString();
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("child", child)
				.queryParam("parent", parentA)
				.queryParam("modifier", mCreatorId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_LINK_PHYSICAL_PARENT).body();
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", child)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_PHYSICAL_OBJECT).body();
		Map<String, String> result = TestUtil
				.toKeyValueMapping(body.asString());
		Assert.assertEquals(parentA,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#physicalParent"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#modificationDate"));
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("child", child)
				.queryParam("parent", parentB)
				.queryParam("modifier", mCreatorId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_LINK_PHYSICAL_PARENT).body();
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", child)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_PHYSICAL_OBJECT).body();
		result = TestUtil.toKeyValueMapping(body.asString());
		Assert.assertEquals(parentB,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#physicalParent"));
		validate(mContext);
	}

	/**
	 * Test the spatial parent relation between two Space objects
	 */
	@Test
	public void testSpatialParent() {
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "Parent A").queryParam("layerIndex", 2)
				.queryParam("creator", mCreatorId).queryParam("userID", "A")
				.expect().statusCode(OK).when()
				.post(CoinsApiWebService.PATH + CoinsApiWebService.PATH_SPACE)
				.body();
		String parentA = body.asString();
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "Parent B").queryParam("layerIndex", 2)
				.queryParam("creator", mCreatorId).queryParam("userID", "B")
				.expect().statusCode(OK).when()
				.post(CoinsApiWebService.PATH + CoinsApiWebService.PATH_SPACE)
				.body();
		String parentB = body.asString();
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "Child").queryParam("layerIndex", 2)
				.queryParam("creator", mCreatorId).queryParam("userID", "C 1")
				.expect().statusCode(OK).when()
				.post(CoinsApiWebService.PATH + CoinsApiWebService.PATH_SPACE)
				.body();
		String child = body.asString();
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("child", child)
				.queryParam("parent", parentA)
				.queryParam("modifier", mCreatorId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_LINK_SPATIAL_PARENT).body();
		body = given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext).queryParam("id", child)
				.queryParam("output", "csv").expect().statusCode(OK).when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_SPACE)
				.body();
		Map<String, String> result = TestUtil
				.toKeyValueMapping(body.asString());
		Assert.assertEquals(parentA,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#spatialParent"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#modificationDate"));
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("child", child)
				.queryParam("parent", parentB)
				.queryParam("modifier", mCreatorId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_LINK_SPATIAL_PARENT).body();
		body = given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext).queryParam("id", child)
				.queryParam("output", "csv").expect().statusCode(OK).when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_SPACE)
				.body();
		result = TestUtil.toKeyValueMapping(body.asString());
		Assert.assertEquals(parentB,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#spatialParent"));
		validate(mContext);
	}

	/**
	 * Test Parameter
	 */
	@Test
	public void testParameter() {
		// POST Parameter
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "Parameter 1")
				.queryParam("defaultValue", "21.2")
				.queryParam("creator", mCreatorId)
				.queryParam("userID", "P1")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_PARAMETER).body();
		// GET Parameter
		String id = body.asString();
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_PARAMETER).body();
		Map<String, String> result = TestUtil
				.toKeyValueMapping(body.asString());
		Assert.assertEquals("Parameter 1",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#name"));
		Assert.assertEquals("P1",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#userID"));
		Assert.assertEquals("http://www.coinsweb.nl/cbim-1.1.owl#Parameter",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals("21.2",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#defaultValue"));
		Assert.assertEquals(mCreatorId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#creator"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#creationDate"));
		validate(mContext);
		// DELETE Parameter
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.expect()
				.statusCode(OK)
				.when()
				.delete(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_PARAMETER);
		// GET Parameter to check it has been removed
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_PARAMETER).body();
		Assert.assertTrue(TestUtil.isEmpty(body.asString()));
	}

	/**
	 * Testing Terminal
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	public void testTerminal() throws JsonProcessingException {
		String locatorId = createLocator();
		// POST Terminal
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "Terminal")
				.queryParam("layerIndex", 2)
				.queryParam("creator", mCreatorId)
				.queryParam("userID", "T1")
				.queryParam("locator", locatorId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_TERMINAL).body();
		// GET terminal
		String id = body.asString();
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_TERMINAL)
				.body();
		Map<String, String> result = TestUtil
				.toKeyValueMapping(body.asString());
		Assert.assertEquals("Terminal",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#name"));
		Assert.assertEquals("T1",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#userID"));
		Assert.assertEquals(locatorId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#locator"));
		Assert.assertEquals("http://www.coinsweb.nl/cbim-1.1.owl#Terminal",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals(mCreatorId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#creator"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#creationDate"));
		validate(mContext);
		// DELETE Terminal
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.expect()
				.statusCode(OK)
				.when()
				.delete(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_TERMINAL);
		// GET Terminal
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_TERMINAL)
				.body();
		Assert.assertTrue(TestUtil.isEmpty(body.asString()));
	}

	private String createLocator() {
		// POST vectors
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "tmpPrmOrientation")
				.queryParam("creator", mCreatorId)
				.queryParam("xCoordinate", "1.1")
				.queryParam("yCoordinate", "0.0")
				.queryParam("zCoordinate", "-1.0").expect().statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH + CoinsApiWebService.PATH_VECTOR)
				.body();
		String tmpPrmOrientation = body.asString();
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "tmpSecOrientation")
				.queryParam("creator", mCreatorId)
				.queryParam("xCoordinate", "1.1")
				.queryParam("yCoordinate", "1.0")
				.queryParam("zCoordinate", "0.0").expect().statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH + CoinsApiWebService.PATH_VECTOR)
				.body();
		String tmpSecOrientation = body.asString();
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "tmpTranslation")
				.queryParam("creator", mCreatorId)
				.queryParam("xCoordinate", "0.0")
				.queryParam("yCoordinate", "0.0")
				.queryParam("zCoordinate", "0.0").expect().statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH + CoinsApiWebService.PATH_VECTOR)
				.body();
		String tmpTranslation = body.asString();
		// POST locator
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.queryParam("name", "Temporary locator")
				.queryParam("creator", mCreatorId)
				.queryParam("primaryOrientation", tmpPrmOrientation)
				.queryParam("secondaryOrientation", tmpSecOrientation)
				.queryParam("translation", tmpTranslation)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH + CoinsApiWebService.PATH_LOCATOR)
				.body();
		// GET locator
		return body.asString();
	}

	private void validate(String pContext) {
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", pContext)
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_VALIDATEALL).body();
		List<String> result = TestUtil.getStringList(body);
		if (result.size() != 1) {
			System.err.println(result);
		}
		Assert.assertEquals(1, result.size());
		Assert.assertEquals("OK", result.get(0));
	}

}
