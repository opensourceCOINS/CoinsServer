package nl.tno.coinsapi.webservices;

import static com.jayway.restassured.RestAssured.given;

import java.util.Map;

import nl.tno.coinsapi.util.TestUtil;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.response.ResponseBody;

/**
 * Test Amount
 */
public class TestAmount extends TestCoinsApiWebService {

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
		Assert.assertTrue(id
				.startsWith("http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl"));
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

}
