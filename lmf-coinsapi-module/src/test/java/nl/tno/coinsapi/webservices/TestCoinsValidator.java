package nl.tno.coinsapi.webservices;

import static com.jayway.restassured.RestAssured.given;

import java.util.List;

import junit.framework.Assert;
import nl.tno.coinsapi.util.TestUtil;

import org.apache.marmotta.platform.core.test.base.JettyMarmotta;
import org.apache.marmotta.platform.core.webservices.io.ExportWebService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.ResponseBody;

/**
 * Test of coins validator
 */
public class TestCoinsValidator {

	private static final int OK = 200;

	private static JettyMarmotta mMarmotta;

	private static final String MODEL_URI = "http://www.coinsweb.nl/testcase/zitbank.owl";

	private static String mContext;

	private static String mCreatorId;

	/**
	 * Setting up Marmotta test service and RestAssured
	 */
	@BeforeClass
	public static void setUp() {
		mMarmotta = new JettyMarmotta("/marmotta", CoinsApiWebService.class,
				ExportWebService.class);

		RestAssured.baseURI = "http://localhost";
		RestAssured.port = mMarmotta.getPort();
		RestAssured.basePath = mMarmotta.getContext();
		mContext = RestAssured.baseURI + ":" + RestAssured.port
				+ RestAssured.basePath;

		// POST PersonOrOrganisation (creator/modifier)
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("modelURI", MODEL_URI)
				.queryParam("name", "Pietje Puk")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_PERSON_OR_ORGANISATION)
				.body();
		mCreatorId = body.asString();
	}

	/**
	 * Tear down
	 */
	@AfterClass
	public static void tearDown() {
		mMarmotta.shutdown();
	}

	/**
	 * Duplicate parents
	 */
	@Test
	public void testDuplicateParents() {
		String zitBankId = addPhysicalObject("Zitbank", "B1", 0);
		String zitSysteemId = addPhysicalObject("Zitsysteem", "B1.1", 1);
		String fundatieSysteemId = addPhysicalObject("Fundatiesysteem", "B1.2",
				1);
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("child", zitSysteemId)
				.queryParam("parent", zitBankId)
				.queryParam("modifier", mCreatorId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_LINK_PHYSICAL_PARENT).body();
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("child", fundatieSysteemId)
				.queryParam("parent", zitBankId)
				.queryParam("modifier", mCreatorId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_LINK_PHYSICAL_PARENT).body();
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("aspect", "physicalParent")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_VALIDATE)
				.body();
		List<String> result = TestUtil.getStringList(body);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals("OK", result.get(0));
		// Now add a duplicate...
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("object", fundatieSysteemId)
				.queryParam("name", "cbim:physicalParent")
				.queryParam("value", zitSysteemId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_ADD_ATTRIBUTE_RESOURCE)
				.body();
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("aspect", "physicalParent")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_VALIDATE)
				.body();
		@SuppressWarnings("unchecked")
		List<String> result2 = body.as(List.class);
		Assert.assertEquals(1, result2.size());
		Assert.assertTrue(result2.get(0).contains("multiple parents"));
	}

	/**
	 * Test isFulfilledBy and fulfills
	 */
	@Test
	public void testFunctionFullfillers() {
		String zitBankId = addPhysicalObject("Zitbank", "B1", 0);
		String zitSysteemId = addPhysicalObject("Zitsysteem", "B1.1", 1);
		String zitten = addFunction("Bieden zitgelegenheid", "F1", 0);
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("function", zitten)
				.queryParam("isFulfilledBy", zitSysteemId)
				.queryParam("modifier", mCreatorId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_LINK_FULFILLED_BY).body();
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("physicalobject", zitBankId)
				.queryParam("fulfills", zitten)
				.queryParam("modifier", mCreatorId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_LINK_FULFILLS).body();
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("aspect", "functionfulfillers")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_VALIDATE)
				.body();
		List<String> result = TestUtil.getStringList(body);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals("OK", result.get(0));
		// Now add an invalid reference
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("physicalobject", zitSysteemId)
				.queryParam("fulfills", zitten)
				.queryParam("modifier", mCreatorId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_LINK_FULFILLS).body();
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("aspect", "functionfulfillers")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_VALIDATE)
				.body();
		result = TestUtil.getStringList(body);
		Assert.assertEquals(1, result.size());
	    Assert.assertTrue(result.get(0).contains("fulfiller"));	    
	}
	
	/**
	 * Test the literals
	 */
	@Test
	public void testLiterals() {
		String zitBankId = addPhysicalObject("Zitbank", "B1", 0);
		String zitSysteemId = addPhysicalObject("Zitsysteem", "B1.1", 1);
		String zitten = addFunction("Bieden zitgelegenheid", "F1", 0);
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("function", zitten)
				.queryParam("isFulfilledBy", zitSysteemId)
				.queryParam("modifier", mCreatorId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_LINK_FULFILLED_BY).body();
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("physicalobject", zitBankId)
				.queryParam("fulfills", zitten)
				.queryParam("modifier", mCreatorId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_LINK_FULFILLS).body();
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("aspect", "literals")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_VALIDATE)
				.body();
		List<String> result = TestUtil.getStringList(body);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals("OK", result.get(0));		
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("object", zitSysteemId)
				.queryParam("name", "cbim:creationDate")
				.queryParam("value", "StringValue")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_ADD_ATTRIBUTE_STRING)
				.body();
		// Validate again
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("aspect", "literals")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_VALIDATE)
				.body();		
		result = TestUtil.getStringList(body);
		Assert.assertEquals(1, result.size());
		Assert.assertTrue(result.get(0).contains("invalid data type"));		
	}
	
	private String addFunction(String name, String userId, int layerIndex) {
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("modelURI", MODEL_URI)
				.queryParam("name", name)
				.queryParam("layerIndex", layerIndex)
				.queryParam("creator", mCreatorId)
				.queryParam("userID", userId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_FUNCTION).body();
		return body.asString();
	}

	private String addPhysicalObject(String name, String userId, int layerIndex) {
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("modelURI", MODEL_URI)
				.queryParam("name", name)
				.queryParam("layerIndex", layerIndex)
				.queryParam("creator", mCreatorId)
				.queryParam("userID", userId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_PHYSICAL_OBJECT).body();
		return body.asString();
	}

}
