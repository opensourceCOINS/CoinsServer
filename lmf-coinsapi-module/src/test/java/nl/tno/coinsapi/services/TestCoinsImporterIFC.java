package nl.tno.coinsapi.services;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;
import nl.tno.coinsapi.util.ReflectionUtils;

import org.junit.Test;

import com.jayway.restassured.RestAssured;

/**
 * Testing the Coins importer BIM documents 
 */
public class TestCoinsImporterIFC extends TestCoinsImporter {

	/**
	 * @throws IOException
	 */
	@Test
	public void testImportIFCDefaultContext() throws IOException {
		String context = RestAssured.baseURI + ":" + RestAssured.port
				+ RestAssured.basePath;
		testImportIFC(context);
	}
	
	/**
	 * Test creating and removing of temp folder
	 * @throws IOException 
	 */
	@Test
	public void testTempFolder() throws IOException {
		CoinsImporter importer = new CoinsImporter();
		// Create temp folder and add some dummy contents
		String folderName = ReflectionUtils.getPrivateFunctionResult(importer, "createTempFolder", String.class);
		Assert.assertNotNull(folderName);
		File file = new File(folderName + File.separator + "bim");
		file.mkdirs();
		file = new File(folderName + File.separator + "bim" + File.separator + "test.owl");
		Assert.assertTrue(file.createNewFile());
		file = new File(folderName + File.separator + "doc");
		file.mkdirs();
		file = new File(folderName + File.separator + "doc" + File.separator + "test.doc");
		Assert.assertTrue(file.createNewFile());
		// Remove contents
		File tempFolder = new File(folderName);
		Assert.assertTrue(tempFolder.exists());
		Assert.assertTrue(ReflectionUtils.callPrivateMethod(importer, "deleteFolder", tempFolder));		
		// Check for removed contents
		Assert.assertFalse(tempFolder.exists());
	}

}
