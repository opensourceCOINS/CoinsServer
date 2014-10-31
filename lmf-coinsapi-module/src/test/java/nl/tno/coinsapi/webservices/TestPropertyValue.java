package nl.tno.coinsapi.webservices;

import static com.jayway.restassured.RestAssured.given;

import java.util.Map;

import nl.tno.coinsapi.util.TestUtil;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.response.ResponseBody;

/**
 * Test for property value
 */
public class TestPropertyValue extends TestCoinsApiWebService {

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
		String propertyTypeId = body.asString();
		// POST PropertyValue
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("name", "property value")
				.queryParam("userID", "pv1")
				.queryParam("value", true)
				.queryParam("propertyType", propertyTypeId)
				.queryParam("creator", mCreatorId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_PROPERTY_VALUE).body();
		String id = body.asString();
		Assert.assertTrue(id
				.startsWith("http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl"));
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
						+ CoinsApiWebService.PATH_PROPERTY_VALUE).body();
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
						+ CoinsApiWebService.PATH_PROPERTY_VALUE).body();
		Assert.assertTrue(TestUtil.isEmpty(body.asString()));
	}

}
