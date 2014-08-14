package nl.tno.coinsapi.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.marmotta.platform.core.api.config.ConfigurationService;
import org.apache.marmotta.platform.core.api.importer.ImportService;
import org.apache.marmotta.platform.core.api.importer.Importer;
import org.apache.marmotta.platform.core.api.task.Task;
import org.apache.marmotta.platform.core.api.task.TaskManagerService;
import org.apache.marmotta.platform.core.exception.io.MarmottaImportException;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.slf4j.Logger;

/**
 * Importer specific for COINS containters
 */
@ApplicationScoped
public class CoinsImporter implements Importer {

	private static String COINS_TYPE = "application/ccr";

	@Inject
	private Logger mLog;

	@Inject
	private ConfigurationService mConfigurationService;

	@Inject
	private TaskManagerService mTaskManagerService;

	@Inject
	private ImportService mImportService;	
	
	private static long mTaskCounter = 0;

	private List<String> mAcceptTypes;

	@Override
	public String getName() {
		return "ccr";
	}

	@Override
	public String getDescription() {
		return "Importer for coins containers";
	}

	@Override
	public Set<String> getAcceptTypes() {
		return new HashSet<String>(mAcceptTypes);
	}

	@Override
	public int importData(URL url, String format, Resource user, URI context)
			throws MarmottaImportException {
		try {
			return importData(url.openStream(), format, user, context,
					url.toString());
		} catch (IOException ex) {
			mLog.error("I/O error while importing data from URL {}: {}", url,
					ex.getMessage());
			return 0;
		}
	}

	@Override
	public int importData(InputStream pInputStream, String pFormat,
			Resource pUser, URI pContext) throws MarmottaImportException {
		String baseUri = mConfigurationService.getBaseUri() + "resource/";
		return importData(pInputStream, pFormat, pUser, pContext, baseUri);
	}

	private String createTempFolder() {
		String folderName = System.getProperty("java.io.tmpdir")
				+ File.separator + "CoinsImporter";
		File f = new File(folderName);
		f.mkdirs();
		int i = 1;
		f = new File(folderName + File.separator + "container" + i);
		while (f.exists()) {
			i++;
			f = new File(folderName + File.separator + "container" + i);
		}
		return f.getAbsolutePath();
	}

	private int importData(InputStream pInputStream, String pFormat,
			Resource pUser, URI pContext, String pBaseUri)
			throws MarmottaImportException {
		final String taskName = String.format("Coins Importer Task %d (%s)",
				++mTaskCounter, pFormat);
		Task task = mTaskManagerService.createSubTask(taskName, "Importer");
		task.updateMessage("importing data into Apache Marmotta repository");
		task.updateDetailMessage("format", pFormat);
		task.updateDetailMessage("baseUri", pBaseUri);

		int count = 0;
		try {
			byte[] buffer = new byte[1024];
			if (COINS_TYPE.equals(pFormat)) {
				String tempFolder = createTempFolder();
				ZipInputStream zipStream = new ZipInputStream(pInputStream);
				ZipEntry entry;
				entry = zipStream.getNextEntry();
				while (entry != null) {
					String fileName = entry.getName();
					File newFile = new File(tempFolder + File.separator
							+ fileName);
					new File(newFile.getParent()).mkdirs();
					FileOutputStream fos = new FileOutputStream(newFile);
					int len;
					while ((len = zipStream.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}
					fos.close();
					entry = zipStream.getNextEntry();
				}
				zipStream.closeEntry();
				zipStream.close();

				// A coins container is a zip file containing a bim folder and a
				// doc folder...
				File f = new File(tempFolder + File.separator + "bim");
				if (f.exists() && f.isDirectory()) {
					for (File file : f.listFiles()) {
						count += importData(file, pUser, pContext, pBaseUri);
					}
				}
//				f = new File(tempFolder + File.separator + "doc");
//				if (f.exists() && f.isDirectory()) {
//					for (File file : f.listFiles()) {
//						count += importData(file, pUser, pContext, pBaseUri);
//					}
//				}
				deleteFolder(new File(tempFolder));				
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			mTaskManagerService.endTask(task);
		}
		return count;
	}

	private void deleteFolder(File pFolder) {
		for (File file : pFolder.listFiles()) {
			if (file.isDirectory()) {
				deleteFolder(file);
			}
			else {
				file.delete();
			}
		}
	}

	private int importData(File pFile, Resource pUser, URI pContext,
			String pBaseUri) throws MarmottaImportException {
		int result = 0;
		String format = "text/turtle";
		if (pFile.getName().endsWith("owl")) {
			format = "application/rdf+xml";
		}
		try {
			FileInputStream fis = new FileInputStream(pFile);
			result = mImportService.importData(fis, format, pUser, pContext);
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public int importData(Reader reader, String format, Resource user,
			URI context) throws MarmottaImportException {
		return 0;
	}

	/**
	 * Registering Coins importer 
	 */
	@PostConstruct
	public void initialise() {
		mLog.info("registering Coins importer ...");

		mAcceptTypes = new ArrayList<String>();
		mAcceptTypes.add(COINS_TYPE);

		mLog.info(" - available parsers: {}",
				Arrays.toString(mAcceptTypes.toArray()));
	}

}
