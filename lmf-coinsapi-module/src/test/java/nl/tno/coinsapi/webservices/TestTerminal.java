package nl.tno.coinsapi.webservices;

import static com.jayway.restassured.RestAssured.given;

import java.util.Map;

import nl.tno.coinsapi.util.TestUtil;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.response.ResponseBody;

/**
 * Testing Terminal
 */
public class TestTerminal extends TestCoinsApiWebService {

	/**
	 * Testing Terminal
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	public void testTerminal() throws JsonProcessingException {
		String locatorId = TestLocator.createLocator();
		String functionFulfillerId = TestPhysicalObject.createPhysicalObject();
		// POST Terminal
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
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
		String id = body.asString();
		Assert.assertTrue(id
				.startsWith("http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl"));
		// Link function fullfiller
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("terminal", id)
				.queryParam("functionFulfiller", functionFulfillerId)
				.queryParam("modifier", mCreatorId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_LINK_TERMINAL_FUNCTION_FULFILLER)
				.body();
		// GET terminal
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
		Assert.assertEquals(functionFulfillerId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#terminalOf"));		
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

	/**
	 * @return the id of the terminal
	 */
	public static String createTerminal() {
		String locatorId = TestLocator.createLocator();
		// POST Terminal
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
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
		return body.asString();		
	}
}
