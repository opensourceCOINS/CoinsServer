package nl.tno.coinsapi.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import org.openrdf.model.URI;

/**
 * Interface for Coins Container file server
 */
public interface ICoinsDocFileService {

	/**
	 * @param pFileName
	 * @return a byte array containing the contents of the file
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public byte[] getFile(String pFileName) throws IOException;

	/**
	 * Move a file to the correct location
	 * @param pFile
	 * @param pContext
	 */
	public void importFile(File pFile, URI pContext);

	/**
	 * @param pContext
	 * @return the path to the Coins documents
	 */
	public String getDocsPath(URI pContext);

	/**
	 * @param pCompleteUri
	 * @return the context part of a complete URI
	 */
	public String getContextPart(URI pCompleteUri);

	/**
	 * @param pUri
	 * @return the local file
	 */
	public File getLocalFile(String pUri);

	/**
	 * @param inputStream
	 * @param fileName
	 * @param context
	 * @return document URI
	 * @throws IOException 
	 */
	public String importStream(InputStream inputStream, String fileName,
			String context) throws IOException;

	/**
	 * @param fileName
	 * @param mContext
	 * @return URL
	 */
	public String composeUrl(String fileName, URI mContext);

}
