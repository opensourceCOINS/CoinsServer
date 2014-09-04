package nl.tno.coinsapi.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.inject.Inject;

import nl.tno.coinsapi.webservices.CoinsApiWebService;

import org.apache.marmotta.platform.core.api.config.ConfigurationService;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

import com.google.common.io.Files;

/**
 * Service for storing and retrieving BIM files 
 */
public class DocFileService implements ICoinsDocFileService {

	/**
	 * Coins documents path
	 */
	private final static String DOCS_FOLDER = "coinsapi.docspath";
	
	@Inject
	private ConfigurationService mConfigurationService;
	
	@Override
	public byte[] getFile(String pFileName) throws IOException {
		File file = new File(pFileName);
		if (!file.exists()) {
			file = new File(mConfigurationService.getStringConfiguration(DOCS_FOLDER) + File.separator + pFileName);
		}
		if (file.exists()) {
			return createByteArray(file);
		}
		return null;
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
		String context = getContextPart(pContext);
		if (context=="") {			
			return mConfigurationService.getStringConfiguration(DOCS_FOLDER);
		}
		return mConfigurationService.getStringConfiguration(DOCS_FOLDER) + "/" + context;
	}

	public String getContextPart(URI pContext) {
		String base = mConfigurationService.getBaseUri();
		String context = pContext.toString();
		if (context.length() <= base.length()) {
			return "";
		}
		if (context.startsWith(base)) {
			String result = context.substring(base.length()) + "/";
			if (result.startsWith("context")) {
				return result.substring(8);
			}
			String apiPrefix = CoinsApiWebService.PATH
					+ CoinsApiWebService.PATH_DOCUMENT_REFERENCE;
			if (result.startsWith(apiPrefix)) {
				result = result.substring(apiPrefix.length() + 1);
			}
			return result;
		}
		
		return "";
	}

	@Override
	public File getLocalFile(String pUri) {
		URI uri = new URIImpl(pUri);
		String fileName = getDocsPath(new URIImpl(uri.getNamespace())) + File.separator + pUri.substring(pUri.lastIndexOf('/')+1);
		// FileName may contain %20 etc...
		try {
			URL u = new URL("file:////" + fileName);
			fileName = u.toURI().getPath();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return new File(fileName);
	}

}
