package nl.tno.coinsapi.webservices;

import static com.jayway.restassured.RestAssured.given;

import java.util.Map;

import nl.tno.coinsapi.util.TestUtil;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.response.ResponseBody;

/**
 * Test Explicit 3D Representation
 */
public class TestExplicit3DRepresentation extends TestCoinsApiWebService {

	/**
	 * Testing Explicit3DRepresentation
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	public void testExplicit3DRepresentation() throws JsonProcessingException {
		// POST parameter
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("name", "Parameter 1")
				.queryParam("defaultValue", "21.2")
				.queryParam("creator", mCreatorId)
				.queryParam("userID", "P1")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_PARAMETER).body();
		String parameterId = body.asString();
		// POST Explicit3dRepresentation
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("name", "3D Element - Ligger")
				.queryParam("creator", mCreatorId)
				.queryParam("documentType", "IFC")
				.queryParam("documentAliasFilePath",
						"een zeer eenvoudige casus.ifc")
				.queryParam(
						"documentUri",
						"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#2K1FrCvpnAIuVj8HuCg0ML")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_EXPLICIT_3D_REPRESENTATION)
				.body();
		// GET Explicit3dRepresentation
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
						+ CoinsApiWebService.PATH_EXPLICIT_3D_REPRESENTATION)
				.body();
		Map<String, String> result = TestUtil
				.toKeyValueMapping(body.asString());
		Assert.assertEquals("3D Element - Ligger",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#name"));
		Assert.assertEquals(
				"http://www.coinsweb.nl/cbim-1.1.owl#Explicit3DRepresentation",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals(mCreatorId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#creator"));
		Assert.assertEquals("IFC",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#documentType"));
		Assert.assertEquals(
				"een zeer eenvoudige casus.ifc",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#documentAliasFilePath"));
		Assert.assertEquals(
				"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#2K1FrCvpnAIuVj8HuCg0ML",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#documentUri"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#creationDate"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#modificationDate"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#modifier"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#description"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#firstParameter"));
		// Link first parameter
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("explicit3DRepresentation", id)
				.queryParam("parameter", parameterId)
				.queryParam("modifier", mModifierId)
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_LINK_FIRST_PARAMETER);
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_EXPLICIT_3D_REPRESENTATION)
				.body();
		// Check it
		result = TestUtil.toKeyValueMapping(body.asString());
		Assert.assertEquals("3D Element - Ligger",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#name"));
		Assert.assertEquals(
				"http://www.coinsweb.nl/cbim-1.1.owl#Explicit3DRepresentation",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals(mCreatorId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#creator"));
		Assert.assertEquals("IFC",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#documentType"));
		Assert.assertEquals(
				"een zeer eenvoudige casus.ifc",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#documentAliasFilePath"));
		Assert.assertEquals(
				"http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl#2K1FrCvpnAIuVj8HuCg0ML",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#documentUri"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#creationDate"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#modificationDate"));
		Assert.assertEquals(mModifierId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#modifier"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#description"));
		Assert.assertEquals(parameterId, result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#firstParameter"));
		validate(mContext);
		// DELETE Explicit3DRepresentation
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.expect()
				.statusCode(OK)
				.when()
				.delete(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_EXPLICIT_3D_REPRESENTATION);
		// GET Explicit3DRepresentation to check it has been removed
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_EXPLICIT_3D_REPRESENTATION)
				.body();
		Assert.assertTrue(TestUtil.isEmpty(body.asString()));
	}

}
