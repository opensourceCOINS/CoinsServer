package nl.tno.coinsapi.webservices;

import static com.jayway.restassured.RestAssured.given;

import java.util.Map;

import nl.tno.coinsapi.util.TestUtil;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.response.ResponseBody;

/**
 * Testing space
 */
public class TestSpace extends TestCoinsApiWebService {

	/**
	 * Testing Space
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	public void testSpace() throws JsonProcessingException {
		// POST Space
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext).queryParam("name", "Foyer")
				.queryParam("layerIndex", 2).queryParam("creator", mCreatorId)
				.queryParam("userID", "S1").expect().statusCode(OK).when()
				.post(CoinsApiWebService.PATH + CoinsApiWebService.PATH_SPACE)
				.body();
		// GET Space
		String id = body.asString();
		Assert.assertTrue(id
				.startsWith("http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl"));
		body = given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext).queryParam("id", id)
				.queryParam("output", "csv").expect().statusCode(OK).when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_SPACE)
				.body();
		Map<String, String> result = TestUtil
				.toKeyValueMapping(body.asString());
		Assert.assertEquals("Foyer",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#name"));
		Assert.assertEquals("S1",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#userID"));
		Assert.assertEquals("http://www.coinsweb.nl/cbim-1.1.owl#Space",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals("2",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#layerIndex"));
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
		// Set description
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("description", "Een ruime foyer")
				.queryParam("modifier", mModifierId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_DESCRIPTION);
		// Check it
		body = given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext).queryParam("id", id)
				.queryParam("output", "csv").expect().statusCode(OK).when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_SPACE)
				.body();
		result = TestUtil.toKeyValueMapping(body.asString());
		Assert.assertEquals("Foyer",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#name"));
		Assert.assertEquals("S1",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#userID"));
		Assert.assertEquals("http://www.coinsweb.nl/cbim-1.1.owl#Space",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals("2",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#layerIndex"));
		Assert.assertEquals(mCreatorId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#creator"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#creationDate"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#modificationDate"));
		Assert.assertEquals(mModifierId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#modifier"));
		Assert.assertEquals("Een ruime foyer",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#description"));
		validate(mContext);
		// DELETE Space
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.expect()
				.statusCode(OK)
				.when()
				.delete(CoinsApiWebService.PATH + CoinsApiWebService.PATH_SPACE);
		// GET Space to check it has been removed
		body = given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext).queryParam("id", id)
				.queryParam("output", "csv").expect().statusCode(OK).when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_SPACE)
				.body();
		Assert.assertTrue(TestUtil.isEmpty(body.asString()));
	}

	/**
	 * Test the spatial parent relation between two Space objects
	 */
	@Test
	public void testSpatialParent() {
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext).queryParam("name", "Parent A")
				.queryParam("layerIndex", 2).queryParam("creator", mCreatorId)
				.queryParam("userID", "A").expect().statusCode(OK).when()
				.post(CoinsApiWebService.PATH + CoinsApiWebService.PATH_SPACE)
				.body();
		String parentA = body.asString();
		body = given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext).queryParam("name", "Parent B")
				.queryParam("layerIndex", 2).queryParam("creator", mCreatorId)
				.queryParam("userID", "B").expect().statusCode(OK).when()
				.post(CoinsApiWebService.PATH + CoinsApiWebService.PATH_SPACE)
				.body();
		String parentB = body.asString();
		body = given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext).queryParam("name", "Child")
				.queryParam("layerIndex", 2).queryParam("creator", mCreatorId)
				.queryParam("userID", "C 1").expect().statusCode(OK).when()
				.post(CoinsApiWebService.PATH + CoinsApiWebService.PATH_SPACE)
				.body();
		String child = body.asString();
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("child", child)
				.queryParam("parent", parentA)
				.queryParam("modifier", mModifierId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_LINK_SPATIAL_PARENT).body();
		body = given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext).queryParam("id", child)
				.queryParam("output", "csv").expect().statusCode(OK).when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_SPACE)
				.body();
		Map<String, String> result = TestUtil
				.toKeyValueMapping(body.asString());
		Assert.assertEquals(parentA,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#spatialParent"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#modificationDate"));
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("child", child)
				.queryParam("parent", parentB)
				.queryParam("modifier", mModifierId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_LINK_SPATIAL_PARENT).body();
		body = given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext).queryParam("id", child)
				.queryParam("output", "csv").expect().statusCode(OK).when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_SPACE)
				.body();
		result = TestUtil.toKeyValueMapping(body.asString());
		Assert.assertEquals(parentB,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#spatialParent"));
		validate(mContext);
	}

}
