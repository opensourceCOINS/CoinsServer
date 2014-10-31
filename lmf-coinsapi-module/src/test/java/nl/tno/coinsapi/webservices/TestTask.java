package nl.tno.coinsapi.webservices;

import static com.jayway.restassured.RestAssured.given;

import java.util.Map;

import nl.tno.coinsapi.util.TestUtil;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.response.ResponseBody;

/**
 * Test Task
 */
public class TestTask extends TestCoinsApiWebService {

	/**
	 * Testing Task
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	public void testTask() throws JsonProcessingException {
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
		// POST task
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("name", "Plaatsen steun links")
				.queryParam("creator", mCreatorId)
				.queryParam("userID", "T001")
				.queryParam("taskType",
						"http://www.coinsweb.nl/cbim-1.1.owl#Constructing")
				.queryParam("affects", physicalObjectId)
				.queryParam("startDatePlanned", "2010-09-01T08:00:00.000Z")
				.queryParam("endDatePlanned", "2010-09-03T18:00:00.000Z")
				.expect().statusCode(OK).when()
				.post(CoinsApiWebService.PATH + CoinsApiWebService.PATH_TASK)
				.body();
		// GET task
		String id = body.asString();
		Assert.assertTrue(id
				.startsWith("http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl"));
		body = given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext).queryParam("id", id)
				.queryParam("output", "csv").expect().statusCode(OK).when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_TASK)
				.body();
		Map<String, String> result = TestUtil
				.toKeyValueMapping(body.asString());
		Assert.assertEquals("Plaatsen steun links",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#name"));
		Assert.assertEquals("http://www.coinsweb.nl/cbim-1.1.owl#Task",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals(mCreatorId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#creator"));
		Assert.assertEquals("2010-09-03T18:00:00.000Z", result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#endDatePlanned"));
		Assert.assertEquals("2010-09-01T08:00:00.000Z", result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#startDatePlanned"));
		Assert.assertEquals(physicalObjectId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#affects"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#creationDate"));
		validate(mContext);
		// DELETE Task
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext).queryParam("id", id).expect()
				.statusCode(OK).when()
				.delete(CoinsApiWebService.PATH + CoinsApiWebService.PATH_TASK);
		// GET Task
		body = given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext).queryParam("id", id)
				.queryParam("output", "csv").expect().statusCode(OK).when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_TASK)
				.body();
		Assert.assertTrue(TestUtil.isEmpty(body.asString()));
	}

}
