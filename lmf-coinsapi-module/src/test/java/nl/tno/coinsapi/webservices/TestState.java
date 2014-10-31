package nl.tno.coinsapi.webservices;

import static com.jayway.restassured.RestAssured.given;

import java.util.Map;

import nl.tno.coinsapi.util.TestUtil;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.response.ResponseBody;

/**
 * Testing State
 */
public class TestState extends TestCoinsApiWebService {

	/**
	 * Testing State
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	public void testState() throws JsonProcessingException {
		// POST PhysicalObject
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("name", "Zitbank")
				.queryParam("layerIndex", 2)
				.queryParam("creator", mCreatorId)
				.queryParam("userID", "B1")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_PHYSICAL_OBJECT).body();
		String physicalObjectId = body.asString();
		// POST Performance
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("name", "Performance")
				.queryParam("creator", mCreatorId)
				.queryParam("userID", "P21")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_PERFORMANCE).body();
		String performanceId = body.asString();
		// POST State
		body = given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("name", "Previous state")
				.queryParam("creator", mCreatorId)
				.queryParam("userID", "ST 1 old").expect().statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH + CoinsApiWebService.PATH_STATE)
				.body();
		String previousStateId = body.asString();
		body = given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext).queryParam("name", "State")
				.queryParam("creator", mCreatorId).queryParam("userID", "ST 1")
				.expect().statusCode(OK).when()
				.post(CoinsApiWebService.PATH + CoinsApiWebService.PATH_STATE)
				.body();
		String id = body.asString();
		Assert.assertTrue(id
				.startsWith("http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl"));
		// GET State
		body = given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext).queryParam("id", id)
				.queryParam("output", "csv").expect().statusCode(OK).when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_STATE)
				.body();
		Map<String, String> result = TestUtil
				.toKeyValueMapping(body.asString());
		Assert.assertEquals("State",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#name"));
		Assert.assertEquals("ST 1",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#userID"));
		Assert.assertEquals("http://www.coinsweb.nl/cbim-1.1.owl#State",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals(mCreatorId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#creator"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#creationDate"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#modificationDate"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#previousState"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#performance"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#stateOf"));
		// Set previous state
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("state", id)
				.queryParam("modifier", mModifierId)
				.queryParam("previousState", previousStateId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_LINK_PREVIOUS_STATE);
		validate(mContext);
		// Set performance
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("state", id)
				.queryParam("modifier", mModifierId)
				.queryParam("performance", performanceId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_LINK_STATE_PERFORMANCE);
		// Set stateOf
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("functionFulfiller", physicalObjectId)
				.queryParam("modifier", mModifierId)
				.queryParam("state", id)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_LINK_STATE_OF);
		// GET State
		body = given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext).queryParam("id", id)
				.queryParam("output", "csv").expect().statusCode(OK).when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_STATE)
				.body();
		result = TestUtil.toKeyValueMapping(body.asString());
		Assert.assertEquals("State",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#name"));
		Assert.assertEquals("ST 1",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#userID"));
		Assert.assertEquals("http://www.coinsweb.nl/cbim-1.1.owl#State",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals(mCreatorId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#creator"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#creationDate"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#modificationDate"));
		Assert.assertEquals(mModifierId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#modifier"));
		Assert.assertEquals(previousStateId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#previousState"));
		Assert.assertEquals(performanceId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#performance"));
		Assert.assertEquals(physicalObjectId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#stateOf"));
		// DELETE State
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.expect()
				.statusCode(OK)
				.when()
				.delete(CoinsApiWebService.PATH + CoinsApiWebService.PATH_STATE);
		// GET Connection
		body = given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext).queryParam("id", id)
				.queryParam("output", "csv").expect().statusCode(OK).when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_STATE)
				.body();
		Assert.assertTrue(TestUtil.isEmpty(body.asString()));
	}

}
