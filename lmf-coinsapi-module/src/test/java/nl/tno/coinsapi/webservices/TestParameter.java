package nl.tno.coinsapi.webservices;

import static com.jayway.restassured.RestAssured.given;

import java.util.Map;

import nl.tno.coinsapi.util.TestUtil;

import org.junit.Assert;
import org.junit.Test;

import com.jayway.restassured.response.ResponseBody;

/**
 * Testing Parameter
 */
public class TestParameter extends TestCoinsApiWebService {

	/**
	 * Test Parameter
	 */
	@Test
	public void testParameter() {
		// POST Parameter
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("name", "Parameter 1")
				.queryParam("defaultValue", "21.2")
				.queryParam("creator", mCreatorId)
				.queryParam("userID", "P1")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_PARAMETER).body();
		String id = body.asString();
		Assert.assertTrue(id
				.startsWith("http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl"));
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("name", "Parameter 2")
				.queryParam("defaultValue", "21.5")
				.queryParam("creator", mCreatorId)
				.queryParam("userID", "P2")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_PARAMETER).body();
		String nextParameterId = body.asString();
		// GET Parameter
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
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#nextParameter"));
		Assert.assertEquals(mCreatorId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#creator"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#creationDate"));
		// Set next parameter
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("parameter", id)
				.queryParam("nextParameter", nextParameterId)
				.queryParam("modifier", mModifierId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_LINK_NEXT_PARAMETER);
		// Check Parameter
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
		result = TestUtil.toKeyValueMapping(body.asString());
		Assert.assertEquals("Parameter 1",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#name"));
		Assert.assertEquals("P1",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#userID"));
		Assert.assertEquals("http://www.coinsweb.nl/cbim-1.1.owl#Parameter",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals("21.2",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#defaultValue"));
		Assert.assertEquals(nextParameterId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#nextParameter"));
		Assert.assertEquals(mCreatorId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#creator"));
		Assert.assertEquals(mModifierId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#modifier"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#creationDate"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#modificationDate"));
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

}
