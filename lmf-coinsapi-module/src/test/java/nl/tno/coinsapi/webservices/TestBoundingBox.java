package nl.tno.coinsapi.webservices;

import static com.jayway.restassured.RestAssured.given;

import java.util.Map;

import nl.tno.coinsapi.util.TestUtil;

import org.junit.Assert;
import org.junit.Test;

import com.jayway.restassured.response.ResponseBody;

/**
 * Testing BoundingBox
 */
public class TestBoundingBox extends TestCoinsApiWebService {

	/**
	 * Test bounding box
	 */
	@Test
	public void testBoundingBox() {
		String locatorId = TestLocator.createLocator();
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("name", "max bounding box")
				.queryParam("creator", mCreatorId)
				.queryParam("xCoordinate", "100")
				.queryParam("yCoordinate", "120")
				.queryParam("zCoordinate", "1000").expect().statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH + CoinsApiWebService.PATH_VECTOR)
				.body();
		String maxBoundingBoxId = body.asString();
		body = given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
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
				.queryParam("minBoundingBox", minBoundingBoxId)
				.queryParam("modifier", mModifierId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_LINK_MIN_BOUNDING_BOX).body();
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("locator", locatorId)
				.queryParam("maxBoundingBox", maxBoundingBoxId)
				.queryParam("modifier", mModifierId)
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
		Assert.assertEquals(maxBoundingBoxId, result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#maxBoundingBox"));
		Assert.assertEquals(minBoundingBoxId, result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#minBoundingBox"));
	}

}
