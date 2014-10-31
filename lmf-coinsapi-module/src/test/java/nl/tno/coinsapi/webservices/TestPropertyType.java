package nl.tno.coinsapi.webservices;

import static com.jayway.restassured.RestAssured.given;

import java.util.Map;

import nl.tno.coinsapi.util.TestUtil;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.response.ResponseBody;

/**
 * Tests for property type
 */
public class TestPropertyType extends TestCoinsApiWebService {

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
				.queryParam("name", "property type")
				.queryParam("userID", "pt1")
				.queryParam("unit", "meters")
				.queryParam("valueDomain", "cbim:XsdBoolean")
				.queryParam("creator", mCreatorId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_PROPERTY_TYPE).body();
		// GET Property type
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
						+ CoinsApiWebService.PATH_PROPERTY_TYPE).body();
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
		Assert.assertEquals("http://www.coinsweb.nl/cbim-1.1.owl#PropertyType",
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
						+ CoinsApiWebService.PATH_PROPERTY_TYPE).body();
		Assert.assertTrue(TestUtil.isEmpty(body.asString()));
	}

}
