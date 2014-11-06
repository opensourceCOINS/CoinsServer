package nl.tno.coinsapi.webservices;

import static com.jayway.restassured.RestAssured.given;

import java.util.List;

import nl.tno.coinsapi.util.TestUtil;

import org.junit.Assert;
import org.junit.Test;

import com.jayway.restassured.response.ResponseBody;

/**
 * Tests with respect to the context
 */
public class TestContext extends TestCoinsApiWebService {

	/**
	 * Initialize
	 */
	@Test
	public void testInitializeContext() {
		String context = mContext + "e";
		given().queryParam("context", context)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_INITIALIZE_CONTEXT);
		// Try to initialize it again with a different context
		ResponseBody body = given()
				.queryParam("context", context)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/schommelstoel.owl")
				.expect()
				.statusCode(500)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_INITIALIZE_CONTEXT).body();
		Assert.assertTrue(body.asString().contains(
				"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl"));
	}

	/**
	 * Only fill in the base path for the context. Coins can fill in the rest of
	 * it
	 */
	@Test
	public void testContext() {
		// POST requirement
		String context = "marmotta";
		// Do not forget to initialize this context
		given().queryParam("context", context)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/leunstoel.owl")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_INITIALIZE_CONTEXT);
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("name", "requirement name")
				.queryParam("layerIndex", 3)
				.queryParam("creator", mCreatorId)
				.queryParam(
						"requirementOf",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#_3eb587eb-0de9-4a1a-a136-aea85266ce3c")
				.queryParam("userID", "B1.1")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_REQUIREMENT).body();
		String id = body.asString();
		Assert.assertTrue(id
				.startsWith("http://www.coinsweb.nl/zeer-eenvoudige-casus/leunstoel.owl"));
		// GET requirement to check it is available removed
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_REQUIREMENT).body();
		Assert.assertFalse(TestUtil.isEmpty(body.asString()));
		// DELETE requirement
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("id", id)
				.expect()
				.statusCode(OK)
				.when()
				.delete(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_REQUIREMENT);
		// GET requirement to check it has been removed
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_REQUIREMENT).body();
		Assert.assertTrue(TestUtil.isEmpty(body.asString()));
	}
	
	/**
	 * Test of validator with context / full context
	 */
	@Test
	public void testValidator() {
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("name", "Parent B")
				.queryParam("layerIndex", 2)
				.queryParam("creator", mCreatorId)
				.queryParam("userID", "B")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_PHYSICAL_OBJECT).body();
		String parentB = body.asString();
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("name", "Child")
				.queryParam("layerIndex", 2)
				.queryParam("creator", mCreatorId)
				.queryParam("userID", "C 1")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_PHYSICAL_OBJECT).body();
		String child = body.asString();
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("child", child)
				.queryParam("parent", parentB)
				.queryParam("modifier", mCreatorId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_LINK_PHYSICAL_PARENT).body();
		// full context
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_VALIDATEALL)
				.body();
		List<String> result = TestUtil.getStringList(body);
		Assert.assertEquals(1, result.size());		
		Assert.assertEquals("OK", result.get(0));
		// Skip server name
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", "marmotta")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_VALIDATEALL)
				.body();
		result = TestUtil.getStringList(body);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals("OK", result.get(0));	
	}
	
}
