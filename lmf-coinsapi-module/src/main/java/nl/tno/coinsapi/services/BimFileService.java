package nl.tno.coinsapi.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import javax.inject.Inject;

import org.apache.marmotta.platform.core.api.config.ConfigurationService;
import org.openrdf.model.URI;

import com.google.common.io.Files;

/**
 * Service for storing and retrieving BIM files 
 */
public class BimFileService implements IBimFileService {

	/**
	 * TODO make it configurable 
	 */
	private final static String DOCS_FOLDER = "e:/CoinsDocs";
	
	@Inject
	private ConfigurationService mConfigurationService;
	
	@Override
	public byte[] getFile(String pFileName) throws URISyntaxException, IOException {
//		String fileName = "";
//		java.net.URI u = new java.net.URI("file://" + DOCS_FOLDER + "/" + pFileName);
//		fileName = Paths.get(u.getPath()).toString();
//		String driveLabel = getDriveLabel();
//		if (driveLabel!=null) {
//			fileName = driveLabel + ":" + fileName;
//		}
		File file = new File(DOCS_FOLDER + File.separator + pFileName);
		if (file.exists()) {
			return createByteArray(file);
		}
		return null;
	}

	private String getDriveLabel() {
		int index = DOCS_FOLDER.indexOf(':');
		if (index < 0) {
			return null;
		}
		if (index > 1) {
			return null;
		}
		return DOCS_FOLDER.substring(0, 1);
	}

	private byte[] createByteArray(File pFile) throws IOException {
		FileInputStream stream = new FileInputStream(pFile);
		int size = 0;
		while (stream.read() != -1) {
			size++;
		}
		stream.close();

		byte[] result = new byte[size];
		stream = new FileInputStream(pFile);
		stream.read(result);
		stream.close();

		return result;
	}

	@Override
	public void importFile(File pFile, URI pContext) {
		File folder = new File(getDocsPath(pContext));
		folder.mkdirs();
		File newFile = new File(folder.getAbsolutePath() + File.separator + pFile.getName());
		try {
			Files.move(pFile, newFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public String getDocsPath(URI pContext) {
		return DOCS_FOLDER + getContextPart(pContext);
	}

	public String getContextPart(URI pContext) {
		String base = mConfigurationService.getBaseUri();
		String context = pContext.stringValue();
		if (context.length() <= base.length()) {
			return "";
		}
		if (context.startsWith(base)) {
			String result = context.substring(base.length()) + "/";
			if (result.startsWith("context")) {
				return result.substring(8);
			}
			return result;
		}
		return "";		
	}

}
