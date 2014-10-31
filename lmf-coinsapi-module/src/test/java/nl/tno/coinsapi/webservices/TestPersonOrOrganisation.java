package nl.tno.coinsapi.webservices;

import static com.jayway.restassured.RestAssured.given;

import java.util.Map;

import nl.tno.coinsapi.util.TestUtil;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.response.ResponseBody;

/**
 * Tests for PersonOrOrganisation
 */
public class TestPersonOrOrganisation extends TestCoinsApiWebService {

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
				.queryParam("name", "Leon van Berlo")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_PERSON_OR_ORGANISATION)
				.body();
		// GET PersonOrOrganisation
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
	 * @param pName
	 * @return id of the PersonOrOrganisation
	 */
	public static String createPersonOrOrganisation(String pName) {
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("name", pName)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_PERSON_OR_ORGANISATION)
				.body();
		return body.asString();
	}
	
}
