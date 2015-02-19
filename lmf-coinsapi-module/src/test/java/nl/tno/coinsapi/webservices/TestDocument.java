package nl.tno.coinsapi.webservices;

import static com.jayway.restassured.RestAssured.given;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import nl.tno.coinsapi.util.TestUtil;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.response.ResponseBody;

/**
 * Tests for Document
 */
public class TestDocument extends TestCoinsApiWebService {

	/**
	 * Testing Document
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	public void testDocument() throws JsonProcessingException {
		String id = createDocument();
		Assert.assertTrue(id
				.startsWith("http://www.coinsweb.nl/zeer-eenvoudige-casus/zitbank.owl"));
		// GET document
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_DOCUMENT)
				.body();
		Map<String, String> result = TestUtil
				.toKeyValueMapping(body.asString());
		Assert.assertEquals("Document name",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#name"));
		Assert.assertEquals("D16",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#userID"));
		Assert.assertEquals("http://www.coinsweb.nl/cbim-1.1.owl#Document",
				result.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		Assert.assertEquals(mCreatorId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#creator"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#creationDate"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#modificationDate"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#modifier"));
		Assert.assertNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#description"));
		validate(mContext);
		// DELETE document
		given().header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.expect()
				.statusCode(OK)
				.when()
				.delete(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_DOCUMENT);
		// GET document to check it has been removed
		body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", id)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_DOCUMENT)
				.body();
		Assert.assertTrue(TestUtil.isEmpty(body.asString()));
	}

	/**
	 * Testing of the uploading of a document
	 * @throws IOException 
	 */
	@Test
	public void testUploadDocument() throws IOException {
		String documentId = createDocument();
		File file = new File(System.getProperty("user.dir") + File.separator
				+ "src/test/resources" + File.separator + "Een zeer eenvoudige casus.ifc");
		Assert.assertTrue(file.exists());
		byte[] outputfile = TestUtil.createByteArray(file);
		given().content(outputfile)
				.queryParam("context", mContext)
				.queryParam("id", documentId)
				.queryParam("modifier", mCreatorId)
				.queryParam("filename", "Een zeer eenvoudige casus.ifc")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_DOCUMENT_UPLOAD);
		
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("id", documentId)
				.queryParam("output", "csv")
				.expect()
				.statusCode(OK)
				.when()
				.get(CoinsApiWebService.PATH + CoinsApiWebService.PATH_DOCUMENT)
				.body();
		Map<String, String> result = TestUtil.toKeyValueMapping(body.asString());
		Assert.assertEquals(mCreatorId,
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#modifier"));
		Assert.assertNotNull(result
				.get("http://www.coinsweb.nl/cbim-1.1.owl#modificationDate"));
		Assert.assertEquals("ifc",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#documentType"));
		String documentURI = result.get(
				"http://www.coinsweb.nl/cbim-1.1.owl#documentUri").replaceAll(
				"%20", " ");
		Assert.assertNotNull(result);
		Assert.assertEquals(
				"Een zeer eenvoudige casus.ifc",
				result.get("http://www.coinsweb.nl/cbim-1.1.owl#documentAliasFilePath"));
		// Now check if we are able to download the file and check the contents of this file...
		body = given().expect().statusCode(OK).when().get(documentURI).body();
		InputStream stream = body.asInputStream(); 
		int b = stream.read();
		int i = 0;
		while (b != -1) {
			Assert.assertTrue(i < outputfile.length);
			Assert.assertEquals(outputfile[i], b);
			b = stream.read();
			i++;
		}
		stream.close();
	}
	
	/**
	 * @return the identifier of the created document
	 */
	public static String createDocument() {
		ResponseBody body = given()
				.header("Content-Type", CoinsApiWebService.MIME_TYPE)
				.queryParam("context", mContext)
				.queryParam("name", "Document name")
				.queryParam("creator", mCreatorId)
				.queryParam("userID", "D16")
				.expect()
				.statusCode(OK)
				.when()
				.post(CoinsApiWebService.PATH
						+ CoinsApiWebService.PATH_DOCUMENT).body();
		return body.asString();
	}
}
