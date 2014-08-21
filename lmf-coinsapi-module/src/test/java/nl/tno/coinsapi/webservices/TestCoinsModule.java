package nl.tno.coinsapi.webservices;

import static com.jayway.restassured.RestAssured.given;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.marmotta.platform.core.exception.InvalidArgumentException;
import org.apache.marmotta.platform.core.exception.MarmottaException;
import org.apache.marmotta.platform.core.test.base.JettyMarmotta;
import org.apache.marmotta.platform.core.webservices.io.ExportWebService;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.UpdateExecutionException;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.ResponseBody;

/**
 * Test for constructing a complete COINS object
 */
public class TestCoinsModule {

	private static final int OK = 200;

	private static JettyMarmotta marmotta;

	private final String MODEL_URI = "http://www.coinsweb.nl/testcase/zitbank.owl";

	/**
	 * Setting up Marmotta test service and RestAssured
	 */
	@BeforeClass
	public static void setUp() {
		marmotta = new JettyMarmotta("/marmotta", CoinsApiWebService.class,
				ExportWebService.class);

		RestAssured.baseURI = "http://localhost";
		RestAssured.port = marmotta.getPort();
		RestAssured.basePath = marmotta.getContext();
	}

	/**
	 * Tear down
	 */
	@AfterClass
	public static void tearDown() {
		marmotta.shutdown();
	}

	/**
	 * Test the creation of a CoinsObject
	 * 
	 * @throws IOException
	 * @throws MarmottaException
	 * @throws UpdateExecutionException
	 * @throws MalformedQueryException
	 * @throws InvalidArgumentException
	 */
	@Test
	public void testCreateCoinsObject() throws IOException,
			InvalidArgumentException, MalformedQueryException,
			UpdateExecutionException, MarmottaException {
		String context = RestAssured.baseURI + ":" + RestAssured.port
				+ RestAssured.basePath;

		ResponseBody body = given()
				.queryParam("context", context)
				.queryParam("modelURI", MODEL_URI)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_INITIALIZE_CONTEXT).body();

		// POST PersonOrOrganisation
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("modelURI", MODEL_URI)
				.queryParam("name", "Pietje Puk")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_PERSON_OR_ORGANISATION)
				.body();
		String creatorId = body.asString();
		// Post PhysicalObject
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("modelURI", MODEL_URI)
				.queryParam("name", "Zitsysteem")
				.queryParam("layerIndex", 1)
				.queryParam("creator", creatorId)
				.queryParam("userID", "B1.1")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_PHYSICAL_OBJECT).body();
		String zitSysteemId = body.asString();
		// Post function
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("modelURI", MODEL_URI)
				.queryParam("name", "Hechten aan bodem")
				.queryParam("layerIndex", 1)
				.queryParam("creator", creatorId)
				.queryParam("isFulfilledBy", zitSysteemId)
				.queryParam("userID", "F1.2")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_FUNCTION).body();
		// Post Physical object zitbank
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("modelURI", MODEL_URI)
				.queryParam("name", "Zitbank")
				.queryParam("layerIndex", 1)
				.queryParam("creator", creatorId)
				.queryParam("userID", "B1")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_PHYSICAL_OBJECT).body();
		String zitBankId = body.asString();
		// Set physical parent
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("child", zitSysteemId)
				.queryParam("parent", zitBankId)
				.queryParam("modifier", creatorId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_LINK_PHYSICAL_PARENT).body();
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("modelURI", MODEL_URI)
				.queryParam("name", "Te gebruiken materiaal: beton en glas")
				.queryParam("layerIndex", 1)
				.queryParam("creator", creatorId)
				.queryParam("nonFunctionalRequirementType",
						"cbimfs:AspectExecution")
				.queryParam("userID", "NR1")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_NON_FUNCTIONAL_REQUIREMENT)
				.body();
		String nr1 = body.asString();
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("modelURI", MODEL_URI)
				.queryParam("name", "Situering: zie document xxxx")
				.queryParam("layerIndex", 1)
				.queryParam("creator", creatorId)
				.queryParam("nonFunctionalRequirementType",
						"cbimfs:AspectEnvironment")
				.queryParam("userID", "NR2")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_NON_FUNCTIONAL_REQUIREMENT)
				.body();
		String nr2 = body.asString();
		// Link nr1 and nr2 to zitbank
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("physicalobject", zitBankId)
				.queryParam("nonfunctionalrequirement", nr1, nr2)
				.queryParam("modifier", creatorId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_LINK_NON_FUNCTIONAL_REQUIREMENT)
				.body();
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("modelURI", MODEL_URI)
				.queryParam("name", "Alle van toepassing zijnde NEN-normen")
				.queryParam("creator", creatorId)
				.queryParam("userID", "D0001")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_DOCUMENT).body();
		String d0001 = body.asString();
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("modelURI", MODEL_URI)
				.queryParam("name",
						"Handboek inrichting openbare ruimte Utrecht")
				.queryParam("creator", creatorId)
				.queryParam("userID", "D0002")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_DOCUMENT).body();
		String d0002 = body.asString();
		// Link documents to zitbank
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", context)
				.queryParam("physicalobject", zitBankId)
				.queryParam("document", d0001, d0002)
				.queryParam("modifier", creatorId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_LINK_DOCUMENT).body();

		body = given().queryParam("format", "application/rdf+xml")
				.queryParam("context", context).expect().statusCode(OK).when()
				.get("export/download").body();
		String owl = body.asString();
		Assert.assertTrue(owl
				.contains("http://www.coinsweb.nl/c-bim-fs.owl#AspectExecution"));
		write(owl);
	}

	private void write(String pString) throws IOException {
		FileWriter writer = new FileWriter(new File("E:/test.owl"));
		writer.write(pString);
		writer.close();
	}
}
