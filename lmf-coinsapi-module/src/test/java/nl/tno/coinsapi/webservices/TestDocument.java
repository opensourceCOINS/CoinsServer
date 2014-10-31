package nl.tno.coinsapi.webservices;

import static com.jayway.restassured.RestAssured.given;

import java.util.Map;

import nl.tno.coinsapi.util.TestUtil;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.response.ResponseBody;

/**
 * Tests for Document
 */
public class TestDocument extends TestCoinsApiWebService {

	/**
	 * Testing Document
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	public void testDocument() throws JsonProcessingException {
		// POST document
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("name", "Document name")
				.queryParam("creator", mCreatorId)
				.queryParam("userID", "D16")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_DOCUMENT).body();
		// GET document
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
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_DOCUMENT)
				.body();
		Map<String, String> result = TestUtil
				.toKeyValueMapping(body.asString());
		Assert.assertEquals("Document name",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#name"));
		Assert.assertEquals("D16",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#userID"));
		Assert.assertEquals("http://www.coinsweb.nl/cbim-1.1.owl#Document",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
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
		validate(mContext);
		// DELETE document
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.expect()
				.statusCode(OK)
				.when()
				.delete(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_DOCUMENT);
		// GET document to check it has been removed
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_DOCUMENT)
				.body();
		Assert.assertTrue(TestUtil.isEmpty(body.asString()));
	}

}
