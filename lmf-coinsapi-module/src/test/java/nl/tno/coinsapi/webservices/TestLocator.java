package nl.tno.coinsapi.webservices;

import static com.jayway.restassured.RestAssured.given;

import java.util.Map;

import nl.tno.coinsapi.util.TestUtil;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.response.ResponseBody;

/**
 * Testing Locator
 */
public class TestLocator extends TestCoinsApiWebService {

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
				.queryParam("name", "tmpPrmOrientation")
				.queryParam("creator", mCreatorId)
				.queryParam("xCoordinate", "1.1")
				.queryParam("yCoordinate", "0.0")
				.queryParam("zCoordinate", "-1.0").expect().statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH + CoinsApiWebService.PATH_VECTOR)
				.body();
		String tmpPrmOrientation = body.asString();
		body = given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("name", "tmpSecOrientation")
				.queryParam("creator", mCreatorId)
				.queryParam("xCoordinate", "1.1")
				.queryParam("yCoordinate", "1.0")
				.queryParam("zCoordinate", "0.0").expect().statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH + CoinsApiWebService.PATH_VECTOR)
				.body();
		String tmpSecOrientation = body.asString();
		body = given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
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
		Assert.assertTrue(id
				.startsWith("http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl"));
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
		Assert.assertEquals(
				tmpSecOrientation,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#secondaryOrientation"));
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
	 * @return the id of a new Locator object
	 */
	public static String createLocator() {
		// POST vectors
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("name", "tmpPrmOrientation")
				.queryParam("creator", mCreatorId)
				.queryParam("xCoordinate", "1.1")
				.queryParam("yCoordinate", "0.0")
				.queryParam("zCoordinate", "-1.0").expect().statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH + CoinsApiWebService.PATH_VECTOR)
				.body();
		String tmpPrmOrientation = body.asString();
		body = given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("name", "tmpSecOrientation")
				.queryParam("creator", mCreatorId)
				.queryParam("xCoordinate", "1.1")
				.queryParam("yCoordinate", "1.0")
				.queryParam("zCoordinate", "0.0").expect().statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH + CoinsApiWebService.PATH_VECTOR)
				.body();
		String tmpSecOrientation = body.asString();
		body = given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
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

}
