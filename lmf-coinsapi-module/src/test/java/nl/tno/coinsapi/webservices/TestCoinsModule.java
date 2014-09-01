package nl.tno.coinsapi.webservices;

import static com.jayway.restassured.RestAssured.given;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import nl.tno.coinsapi.util.TestUtil;

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

	private static JettyMarmotta mMarmotta;

	private final String MODEL_URI = "http://www.coinsweb.nl/testcase/zitbank.owl";

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
	}

	/**
	 * Tear down
	 */
	@AfterClass
	public static void tearDown() {
		mMarmotta.shutdown();
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

		ResponseBody body = given()
				.queryParam("context", mContext)
				.queryParam("modelURI", MODEL_URI)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_INITIALIZE_CONTEXT).body();

		// POST PersonOrOrganisation (creator/modifier)
		body = given()
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

		// PhysicalObjects
		String zitBankId = addPhysicalObject("Zitbank", "B1", 0);
		String zitSysteemId = addPhysicalObject("Zitsysteem", "B1.1", 1);
		String fundatieSysteemId = addPhysicalObject("Fundatiesysteem", "B1.2",
				1);
		String ligger = addPhysicalObject("Ligger", "B1.1.1", 2);
		String steunLinksId = addPhysicalObject("Steun links", "B1.2.1", 2);
		String steunRechtsId = addPhysicalObject("Steun rechts", "B1.2.2", 2);
		// Functions
		String voorzieningZittenId = addFunction(
				"Voorziening voor meerdere personen om te kunnen zitten", "F1",
				0);
		String biedenZitgelegenheid = addFunction("Bieden zitgelegenheid",
				"F1.1", 1);
		String hechtenAanBodemId = addFunction("Hechten aan bodem", "F1.2", 1);
		// Documents
		String d0001 = addDocument("Alle van toepassing zijnde NEN-normen",
				"D0001");
		String d0002 = addDocument(
				"Handboek inrichting openbare ruimte Utrecht", "D0002");
		// Non function requirements
		String nr1 = addNonFunctionalRequirement(
				"Te gebruiken materiaal: beton en glas", "NR1",
				"cbimfs:AspectExecution");
		String nr2 = addNonFunctionalRequirement(
				"Situering: zie document xxxx", "NR2",
				"cbimfs:AspectEnvironment");
		// Requirements
		// String sterkGenoeg =
		addRequirement(
				"Sterk genoeg om een belasting van 300 kg te kunnen dragen",
				"RF1.2", voorzieningZittenId, 0);
		// String geschiktVoorDrie =
		addRequirement("Geschikt voor tenminste drie personen", "RF1.1",
				voorzieningZittenId, 0);
		// Explicit3DRepresentations
		String explicit3DSteunLinks = addExplicit3DRepresentation(
				"3D Element - Steun links", "IFC",
				"Een zeer eenvoudige casus.ifc", "#1Mre5XRrn72hoJ9l3zDDeS");
		// Vectors
		String tmpPrmOrientation = addVector("tmpPrmOrientation", 0.0, 0.0, 1.0);
		String tmpSecOrientation = addVector("tmpSecOrientation", 1.0, 0.0, 0.0);
		String tmpTranslation = addVector("tmpTranslation", 0.0, 0.0, 0.0);
		// locator
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("modelURI", MODEL_URI)
				.queryParam("name", "Tmp_Locator")
				.queryParam("creator", mCreatorId)
				.queryParam("primaryOrientation", tmpPrmOrientation)
				.queryParam("secondaryOrientation", tmpSecOrientation)
				.queryParam("translation", tmpTranslation)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH + CoinsApiWebService.PATH_LOCATOR)
				.body();
		// String locatorId = body.asString();
		// tasks
		String t001 = addTask("Plaatsen steun links", "T001",
				"http://www.coinsweb.nl/c-bim.owl#Constructing",
				new String[] { steunLinksId }, "2010-09-01T15:13:04.000Z",
				"2010-09-01T15:13:04.000Z");
		addTask("Plaatsen steun rechts", "T002",
				"http://www.coinsweb.nl/c-bim.owl#Constructing",
				new String[] { steunRechtsId }, "2010-09-01T15:13:04.000Z",
				"2010-09-01T15:13:04.000Z");
		addTask("Plaatsen ligger", "T003",
				"http://www.coinsweb.nl/c-bim.owl#Constructing",
				new String[] {}, "2010-09-01T15:13:04.000Z",
				"2010-09-01T15:13:04.000Z");
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("physiscalobject", ligger)
				.queryParam("isAffectedBy", t001)
				.queryParam("modifier", mCreatorId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_LINK_ISAFFECTEDBY).body();
		// Set function fulfiller
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("function", biedenZitgelegenheid)
				.queryParam("isFulfilledBy", zitSysteemId)
				.queryParam("modifier", mCreatorId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_LINK_FULFILLED_BY).body();
		// Set physical parents
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
				.queryParam("child", ligger)
				.queryParam("parent", zitSysteemId)
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
		// Link nr1 and nr2 to zitbank
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("physicalobject", zitBankId)
				.queryParam("nonfunctionalrequirement", nr1, nr2)
				.queryParam("modifier", mCreatorId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_LINK_NON_FUNCTIONAL_REQUIREMENT)
				.body();
		// Link documents to zitbank
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("physicalobject", zitBankId)
				.queryParam("document", d0001, d0002)
				.queryParam("modifier", mCreatorId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_LINK_DOCUMENT).body();
		// Zitbank fulfills voorziening om te kunnen zitten
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("physicalobject", zitBankId)
				.queryParam("fulfills", voorzieningZittenId)
				.queryParam("modifier", mCreatorId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_LINK_FULFILLS).body();
		// Hechten aan bodem is fulfilled by Fundatiesysteem
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("function", hechtenAanBodemId)
				.queryParam("isFulfilledBy", fundatieSysteemId)
				.queryParam("modifier", mCreatorId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_LINK_FULFILLED_BY).body();
		// Set shape
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("physicalobject", steunLinksId)
				.queryParam("shape", explicit3DSteunLinks)
				.queryParam("modifier", mCreatorId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_LINK_SHAPE).body();
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("child", fundatieSysteemId)
				.queryParam("parent", steunLinksId, steunRechtsId)
				.queryParam("modifier", mCreatorId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_LINK_PHYSICAL_PARENT).body();
		
		validate();
		
		body = given().queryParam("format", "application/rdf+xml")
				.queryParam("context", mContext).expect().statusCode(OK).when()
				.get("export/download").body();
		String owl = body.asString();
		write(owl);
		Assert.assertTrue(owl
				.contains("http://www.coinsweb.nl/c-bim-fs.owl#AspectExecution"));
		Assert.assertTrue(owl.contains("cbim:document"));
		Assert.assertTrue(owl.contains("cbim:fulfills"));
		Assert.assertTrue(owl.contains("cbim:isFulfilledBy"));
		Assert.assertTrue(owl.contains("cbim:affects"));
		Assert.assertTrue(owl.contains("cbim:isAffectedBy"));
		Assert.assertTrue(owl.contains("cbim:physicalParent"));
		Assert.assertTrue(owl.contains("cbim:shape"));
		Assert.assertTrue(owl.contains("cbimfs:nonFunctionalRequirement"));
	}

	private void validate() {
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
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
	
	private String addRequirement(String name, String userId,
			String requirementOf, int layerIndex) {
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("modelURI", MODEL_URI)
				.queryParam("name", name)
				.queryParam("layerIndex", layerIndex)
				.queryParam("creator", mCreatorId)
				.queryParam("requirementOf", requirementOf)
				.queryParam("userID", userId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_REQUIREMENT).body();
		return body.asString();
	}

	private String addNonFunctionalRequirement(String name, String userId,
			String type) {
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("modelURI", MODEL_URI)
				.queryParam("name", name)
				.queryParam("layerIndex", 1)
				.queryParam("creator", mCreatorId)
				.queryParam("nonFunctionalRequirementType", type)
				.queryParam("userID", userId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_NON_FUNCTIONAL_REQUIREMENT)
				.body();
		return body.asString();
	}

	private String addDocument(String name, String userId) {
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("modelURI", MODEL_URI)
				.queryParam("name", name)
				.queryParam("creator", mCreatorId)
				.queryParam("userID", userId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_DOCUMENT).body();
		return body.asString();
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

	private String addExplicit3DRepresentation(String name,
			String documentType, String documentAliasFilePath,
			String documentUri) {
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam(MODEL_URI)
				.queryParam("name", name)
				.queryParam("creator", mCreatorId)
				.queryParam("documentType", documentType)
				.queryParam("documentAliasFilePath", documentAliasFilePath)
				.queryParam("documentUri", documentUri)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_EXPLICIT_3D_REPRESENTATION)
				.body();
		return body.asString();
	}

	private String addVector(String pName, double x, double y, double z) {
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("modelURI", MODEL_URI).queryParam("name", pName)
				.queryParam("creator", mCreatorId).queryParam("xCoordinate", x)
				.queryParam("yCoordinate", y).queryParam("zCoordinate", z)
				.expect().statusCode(OK).when()
				.post(CoinsApiWebService.PATH + CoinsApiWebService.PATH_VECTOR)
				.body();
		return body.asString();
	}

	private String addTask(String name, String userId, String taskType,
			Object[] affects, String startDatePlanned, String endDatePlanned) {
		ResponseBody body = null;
		if (affects.length == 0) {
			body = given()
					.header("Content-Type", CoinsApiWebService.MIME_TYPE)
					.queryParam("context", mContext)
					.queryParam("modelURI", MODEL_URI)
					.queryParam("name", name)
					.queryParam("creator", mCreatorId)
					.queryParam("userID", userId)
					.queryParam("taskType", taskType)
					.queryParam("startDatePlanned", startDatePlanned)
					.queryParam("endDatePlanned", endDatePlanned)
					.expect()
					.statusCode(OK)
					.when()
					.post(CoinsApiWebService.PATH
							+ CoinsApiWebService.PATH_TASK).body();
		} else {
			body = given()
					.header("Content-Type", CoinsApiWebService.MIME_TYPE)
					.queryParam("context", mContext)
					.queryParam("modelURI", MODEL_URI)
					.queryParam("name", name)
					.queryParam("creator", mCreatorId)
					.queryParam("userID", userId)
					.queryParam("taskType", taskType)
					.queryParam("affects", affects)
					.queryParam("startDatePlanned", startDatePlanned)
					.queryParam("endDatePlanned", endDatePlanned)
					.expect()
					.statusCode(OK)
					.when()
					.post(CoinsApiWebService.PATH
							+ CoinsApiWebService.PATH_TASK).body();
		}
		return body.asString();
	}

	private void write(String pString) throws IOException {
		FileWriter writer = new FileWriter(new File("E:/test.owl"));
		writer.write(pString);
		writer.close();
	}
}
