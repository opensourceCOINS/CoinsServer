package nl.tno.coinsapi.services;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.openrdf.model.URI;

/**
 * Interface for BIM file server
 */
public interface IBimFileService {

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
	 * @return the path to the BIM documents
	 */
	public String getDocsPath(URI pContext);

	/**
	 * @param pContext
	 * @return the context part of a complete URI
	 */
	public String getContextPart(URI pContext);

}
