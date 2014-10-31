package nl.tno.coinsapi.webservices;

import org.junit.Assert;
import org.junit.Test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

/**
 * Test for the version
 */
public class TestVersion extends TestCoinsApiWebService {

	/**
	 * Testing the version
	 */
	@Test
	public void testVersion() {
		Response resp = RestAssured.get(CoinsApiWebService.PATH
				+ CoinsApiWebService.PATH_VERSION);
		Assert.assertEquals(200, resp.getStatusCode());
		Assert.assertEquals(CoinsApiWebService.MIME_TYPE, resp.getContentType());
		Assert.assertEquals("v0.2", resp.getBody().asString());
	}

}
