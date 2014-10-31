package nl.tno.coinsapi.webservices;

import static com.jayway.restassured.RestAssured.given;

import java.util.Map;

import nl.tno.coinsapi.util.TestUtil;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.response.ResponseBody;

/**
 * Testing Verification
 */
public class TestVerification extends TestCoinsApiWebService {

	/**
	 * Testing Verification
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	public void testVerification() throws JsonProcessingException {
		String requirementId = TestRequirement.createRequirement();
		String nonFunctionalRequirementId = TestNonFunctionalRequirement.createNonFunctionalRequirement();
		String physicalObjectId = TestPhysicalObject.createPhysicalObject();
		String performerId = TestPersonOrOrganisation.createPersonOrOrganisation("Bassie");
		String plannedPerformerId = TestPersonOrOrganisation.createPersonOrOrganisation("Adriaan");
		String authorizer = TestPersonOrOrganisation.createPersonOrOrganisation("Kees");
		// POST Verification
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("name", "Verification")
				.queryParam("creator", mCreatorId)
				.queryParam("userID", "V1")
				.queryParam("verificationResult", false)
				.queryParam("verificationMethod", "Slaan met hamer")
				.queryParam("verificationDate", "2010-09-01T08:00:00.000Z")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_VERIFICATION).body();
		// GET verification
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
						+ CoinsApiWebService.PATH_VERIFICATION).body();
		Map<String, String> result = TestUtil
				.toKeyValueMapping(body.asString());
		Assert.assertEquals("Verification",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#name"));
		Assert.assertEquals("V1",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#userID"));
		Assert.assertEquals("Slaan met hamer", result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#verificationMethod"));
		Assert.assertEquals("false", result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#verificationResult"));
		Assert.assertEquals("2010-09-01T08:00:00.000Z", result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#verificationDate"));
		Assert.assertEquals("http://www.coinsweb.nl/cbim-1.1.owl#Verification",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals(mCreatorId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#creator"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#creationDate"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/c-bim-fs.owl#verificationRequirement"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/cbimfs.owl#verificationRequirement"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/cbimfs.owl#plannedVerificationPerformer"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#verificationPerformer"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/c-bim-fs.owl#authorizedBy"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#verificationFunctionFulfiller"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#modificationDate"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/c-bim-fs.owl#authorizationDate"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/c-bim-fs.owl#authorizationDefects"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/c-bim-fs.owl#authorizationMeasures"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/c-bim-fs.owl#authorizationRemarks"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/c-bim-fs.owl#plannedRemarks"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/c-bim-fs.owl#plannedVerificationMethod"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/c-bim-fs.owl#plannedVerificationDate"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/c-bim-fs.owl#plannedWorkPackage"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/c-bim-fs.owl#verificationRisks"));
		// Link requirement
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("verification", id)
				.queryParam("requirement", requirementId)
				.queryParam("modifier", mModifierId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_LINK_VERIFICATION_REQUIREMENT);
		// Link physical object (function fulfiller)
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("verification", id)
				.queryParam("functionFulfiller", physicalObjectId)
				.queryParam("modifier", mModifierId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_LINK_VERIFICATION_FUNCTION_FULFILLER);
		// Link non functional requirement
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("verification", id)
				.queryParam("requirement", nonFunctionalRequirementId)
				.queryParam("modifier", mModifierId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_LINK_VERIFICATION_NON_FUNCTIONAL_REQUIREMENT);
		// Link performer
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("verification", id)
				.queryParam("performer", performerId)
				.queryParam("modifier", mModifierId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_LINK_VERIFICATION_PERFORMER);
		// Link planned performer
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("verification", id)
				.queryParam("performer", plannedPerformerId)
				.queryParam("modifier", mModifierId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_LINK_VERIFICATION_PLANNED_PERFORMER);
		// Link authorizer
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("verification", id)
				.queryParam("authorizer", authorizer)
				.queryParam("modifier", mModifierId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_LINK_VERIFICATION_AUTHORIZED_BY);
		// Set authorization date
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("verification", id)
				.queryParam("authorizationDate", "2004-09-01T08:00:00.000Z")
				.queryParam("modifier", mModifierId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_VERIFICATION_AUTHORIZATION_DATE);
		// Set authorization remarks
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("verification", id)
				.queryParam("authorizationRemarks", "Authorization remarks")
				.queryParam("modifier", mModifierId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_VERIFICATION_AUTHORIZATION_REMARKS);
		// Set authorization defects
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("verification", id)
				.queryParam("authorizationDefects", "Authorization defects")
				.queryParam("modifier", mModifierId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_VERIFICATION_AUTHORIZATION_DEFECTS);
		// Set authorization measures
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("verification", id)
				.queryParam("authorizationMeasures", "Authorization measures")
				.queryParam("modifier", mModifierId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_VERIFICATION_AUTHORIZATION_MEASURES);
		// Set planned remarks
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("verification", id)
				.queryParam("plannedRemarks", "Planned remarks")
				.queryParam("modifier", mModifierId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_VERIFICATION_PLANNED_REMARKS);
		// Set planned date
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("verification", id)
				.queryParam("plannedDate", "2007-09-01T08:00:00.000Z")
				.queryParam("modifier", mModifierId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_VERIFICATION_PLANNED_DATE);
		// Set planned method
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("verification", id)
				.queryParam("plannedMethod", "Planned method")
				.queryParam("modifier", mModifierId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_VERIFICATION_PLANNED_METHOD);
		// Set planned work package
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("verification", id)
				.queryParam("plannedWorkPackage", "Planned work package")
				.queryParam("modifier", mModifierId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_VERIFICATION_PLANNED_WORK_PACKAGE);
		// Set risks
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("verification", id)
				.queryParam("risks", "These are the risks")
				.queryParam("modifier", mModifierId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_VERIFICATION_RISKS);		

		// Test
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_VERIFICATION).body();
		result = TestUtil.toKeyValueMapping(body.asString());
		Assert.assertEquals("Verification",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#name"));
		Assert.assertEquals("V1",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#userID"));
		Assert.assertEquals("Slaan met hamer", result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#verificationMethod"));
		Assert.assertEquals("false", result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#verificationResult"));
		Assert.assertEquals("2010-09-01T08:00:00.000Z", result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#verificationDate"));
		Assert.assertEquals("http://www.coinsweb.nl/cbim-1.1.owl#Verification",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals(mCreatorId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#creator"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#creationDate"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#modificationDate"));
		Assert.assertEquals("2004-09-01T08:00:00.000Z", result
				.get("http://www.coinsweb.nl/c-bim-fs.owl#authorizationDate"));
		Assert.assertEquals(
				requirementId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#verificationRequirement"));
		Assert.assertEquals(
				nonFunctionalRequirementId,
				result.get("http://www.coinsweb.nl/c-bim-fs.owl#verificationRequirement"));
		Assert.assertEquals(
				performerId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#verificationPerformer"));
		Assert.assertNull(
				plannedPerformerId,
				result.get("http://www.coinsweb.nl/cbimfs.owl#plannedVerificationPerformer"));
		Assert.assertEquals(
				physicalObjectId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#verificationFunctionFulfiller"));
		Assert.assertEquals(authorizer,
				result.get("http://www.coinsweb.nl/c-bim-fs.owl#authorizedBy"));
		Assert.assertEquals("Authorization remarks", result
				.get("http://www.coinsweb.nl/c-bim-fs.owl#authorizationRemarks"));
		Assert.assertEquals("Authorization defects", result
				.get("http://www.coinsweb.nl/c-bim-fs.owl#authorizationDefects"));
		Assert.assertEquals("Authorization measures", result
				.get("http://www.coinsweb.nl/c-bim-fs.owl#authorizationMeasures"));
		Assert.assertEquals("Planned remarks", result
				.get("http://www.coinsweb.nl/c-bim-fs.owl#plannedRemarks"));
		Assert.assertEquals("2007-09-01T08:00:00.000Z", result
				.get("http://www.coinsweb.nl/c-bim-fs.owl#plannedVerificationDate"));
		Assert.assertEquals("Planned method", result
				.get("http://www.coinsweb.nl/c-bim-fs.owl#plannedVerificationMethod"));
		Assert.assertEquals("Planned work package",result
				.get("http://www.coinsweb.nl/c-bim-fs.owl#plannedWorkPackage"));
		Assert.assertEquals("These are the risks",result
				.get("http://www.coinsweb.nl/c-bim-fs.owl#verificationRisks"));
		validate(mContext);
		// DELETE Verification
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.expect()
				.statusCode(OK)
				.when()
				.delete(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_VERIFICATION);
		// GET Verification
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_VERIFICATION).body();
		Assert.assertTrue(TestUtil.isEmpty(body.asString()));
	}

}
