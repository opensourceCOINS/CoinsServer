package nl.tno.coinsapi.webservices;

import static com.jayway.restassured.RestAssured.given;

import java.util.Map;

import nl.tno.coinsapi.util.TestUtil;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.response.ResponseBody;

/**
 * Testing LibraryReference
 */
public class TestLibraryReference extends TestCoinsApiWebService {

	/**
	 * Testing LibraryReference
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	public void testLibraryReference() throws JsonProcessingException {
		String cbimObjectId = TestPhysicalObject.createPhysicalObject();
		// POST Library Reference
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("name", "Library reference name")
				.queryParam("creator", mCreatorId)
				.queryParam("userID", "lr 2")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_LIBRARY_REFERENCE).body();
		// GET library reference
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
						+ CoinsApiWebService.PATH_LIBRARY_REFERENCE).body();
		Map<String, String> result = TestUtil
				.toKeyValueMapping(body.asString());
		Assert.assertEquals("Library reference name",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#name"));
		Assert.assertEquals("lr 2",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#userID"));
		Assert.assertEquals(
				"http://www.coinsweb.nl/cbim-1.1.owl#LibraryReference",
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
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/cbim-otl-1.1.owl#objectReference"));
		// Link CbimObject
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("libraryReference", id)
				.queryParam("cbimObject", cbimObjectId)
				.queryParam("modifier", mModifierId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_LINK_CBIM_OBJECT_TO_LIBRARY_REFERENCE);
		// Check
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_LIBRARY_REFERENCE).body();
		result = TestUtil.toKeyValueMapping(body.asString());
		Assert.assertEquals("Library reference name",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#name"));
		Assert.assertEquals("lr 2",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#userID"));
		Assert.assertEquals(
				"http://www.coinsweb.nl/cbim-1.1.owl#LibraryReference",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals(mCreatorId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#creator"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#creationDate"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#modificationDate"));
		Assert.assertEquals(mModifierId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#modifier"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#description"));
		Assert.assertEquals(cbimObjectId, result
				.get("http://www.coinsweb.nl/cbim-otl-1.1.owl#objectReference"));
		validate(mContext);
		// DELETE library reference
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.expect()
				.statusCode(OK)
				.when()
				.delete(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_LIBRARY_REFERENCE);
		// GET library reference to check it has been removed
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_LIBRARY_REFERENCE).body();
		Assert.assertTrue(TestUtil.isEmpty(body.asString()));
	}

}
