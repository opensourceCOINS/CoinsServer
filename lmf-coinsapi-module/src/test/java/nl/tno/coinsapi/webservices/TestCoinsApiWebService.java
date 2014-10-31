package nl.tno.coinsapi.webservices;

import static com.jayway.restassured.RestAssured.given;

import java.util.List;

import nl.tno.coinsapi.util.TestUtil;

import org.apache.marmotta.platform.core.test.base.JettyMarmotta;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.ResponseBody;

/**
 * Base class for testing COINS API web service
 */
public class TestCoinsApiWebService {

	protected static final int OK = 200;

	private static JettyMarmotta mMarmotta;

	protected static String mContext;

	protected static String mCreatorId;

	protected static String mModifierId;

	/**
	 * Setting up Marmotta test service and RestAssured
	 */
	@BeforeClass
	public static void setUp() {
		mMarmotta = new JettyMarmotta("/marmotta", CoinsApiWebService.class);

		RestAssured.baseURI = "http://localhost";
		RestAssured.port = mMarmotta.getPort();
		RestAssured.basePath = mMarmotta.getContext();

		mContext = RestAssured.baseURI + ":" + RestAssured.port
				+ RestAssured.basePath;

		// Initialize the context
		given().queryParam("context", mContext)
				.queryParam("modelURI",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_INITIALIZE_CONTEXT);
		// POST PersonOrOrganisation (creator/modifier)
		mCreatorId = TestPersonOrOrganisation.createPersonOrOrganisation("Pietje Puk");
		mModifierId = TestPersonOrOrganisation.createPersonOrOrganisation("Klaas vaak");
	}

	/**
	 * Tear down
	 */
	@AfterClass
	public static void tearDown() {
		mMarmotta.shutdown();
	}

	/**
	 * Validate the current context
	 * @param pContext
	 */
	protected void validate(String pContext) {
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", pContext)
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_VALIDATEALL).body();
		List<String> result = TestUtil.getStringList(body);
		if (result.size() != 1) {
			System.err.println(result);
		}
		Assert.assertEquals(1, result.size());
		Assert.assertEquals("OK", result.get(0));
	}

}
