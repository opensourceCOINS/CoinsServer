package nl.tno.coinsapi.webservices;

import static com.jayway.restassured.RestAssured.given;

import java.util.Map;

import nl.tno.coinsapi.util.TestUtil;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.response.ResponseBody;

/**
 * Tests for Non functional requirements
 */
public class TestNonFunctionalRequirement extends TestCoinsApiWebService {

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
		Assert.assertTrue(id
				.startsWith("http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl"));
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
				.queryParam("modifier", mModifierId)
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
		Assert.assertEquals(mModifierId,
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
	 * @return the id of a new NonFunctionalRequirement
	 */
	public static String createNonFunctionalRequirement() {
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
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
		return body.asString();
	}

}
