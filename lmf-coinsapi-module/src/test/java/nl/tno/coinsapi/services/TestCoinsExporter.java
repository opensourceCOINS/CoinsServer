package nl.tno.coinsapi.services;

import static com.jayway.restassured.RestAssured.given;
import nl.tno.coinsapi.webservices.CoinsApiWebService;

import org.apache.marmotta.platform.core.test.base.JettyMarmotta;
import org.apache.marmotta.platform.core.webservices.io.ExportWebService;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.ResponseBody;

/**
 * Tests for Coins exporter
 */
public class TestCoinsExporter {

	protected static final int OK = 200;
	private static JettyMarmotta mMarmotta;
	protected static String mContext;
	
	/**
	 * Setup
	 */
	@BeforeClass
	public static void setUp() {
		mMarmotta = new JettyMarmotta("/marmotta", CoinsApiWebService.class, ExportWebService.class);

		RestAssured.baseURI = "http://localhost";
		RestAssured.port = mMarmotta.getPort();
		RestAssured.basePath = mMarmotta.getContext();

		mContext = RestAssured.baseURI + ":" + RestAssured.port
				+ RestAssured.basePath;

	}

	/**
	 * Export Initialized context
	 */
	@Test
	public void testExportInititializedContext() {
		// Initialize the context
		given().queryParam("context", mContext)
		.queryParam("modelURI",
				"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_INITIALIZE_CONTEXT);
				
		ResponseBody body = given()
				.queryParam("context", mContext)
				.queryParam("format", "application/ccr")
				.expect()
				.statusCode(OK)
				.when()
				.get("export/download").body();

		Assert.assertTrue("At least the ontology must be exported", body.asByteArray().length > 0);
	}

	/**
	 * Tear down
	 */
	@AfterClass
	public static void tearDown() {
		mMarmotta.shutdown();
	}

}
