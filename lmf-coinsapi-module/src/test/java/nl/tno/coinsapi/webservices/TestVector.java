package nl.tno.coinsapi.webservices;

import static com.jayway.restassured.RestAssured.given;

import java.util.Map;

import nl.tno.coinsapi.util.TestUtil;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.response.ResponseBody;

/**
 * Test of Vector
 */
public class TestVector extends TestCoinsApiWebService {

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
		Assert.assertTrue(id
				.startsWith("http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl"));
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

}
