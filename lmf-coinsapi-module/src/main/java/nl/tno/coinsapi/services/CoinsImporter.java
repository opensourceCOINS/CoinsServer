package nl.tno.coinsapi.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import nl.tno.coinsapi.CoinsPrefix;
import nl.tno.coinsapi.keys.CbimAttributeKey;
import nl.tno.coinsapi.keys.CbimObjectKey;
import nl.tno.coinsapi.tools.HTMLEscaper;

import org.apache.marmotta.kiwi.loader.KiWiLoaderConfiguration;
import org.apache.marmotta.kiwi.loader.generic.KiWiHandler;
import org.apache.marmotta.kiwi.loader.mysql.KiWiMySQLHandler;
import org.apache.marmotta.kiwi.loader.pgsql.KiWiPostgresHandler;
import org.apache.marmotta.kiwi.persistence.mysql.MySQLDialect;
import org.apache.marmotta.kiwi.persistence.pgsql.PostgreSQLDialect;
import org.apache.marmotta.kiwi.sail.KiWiStore;
import org.apache.marmotta.platform.core.api.config.ConfigurationService;
import org.apache.marmotta.platform.core.api.importer.ImportService;
import org.apache.marmotta.platform.core.api.importer.Importer;
import org.apache.marmotta.platform.core.api.task.Task;
import org.apache.marmotta.platform.core.api.task.TaskManagerService;
import org.apache.marmotta.platform.core.api.triplestore.SesameService;
import org.apache.marmotta.platform.core.exception.InvalidArgumentException;
import org.apache.marmotta.platform.core.exception.MarmottaException;
import org.apache.marmotta.platform.core.exception.io.MarmottaImportException;
import org.apache.marmotta.platform.sparql.api.sparql.SparqlService;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.base.RepositoryWrapper;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.sail.Sail;
import org.openrdf.sail.helpers.SailWrapper;
import org.slf4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Importer specific for COINS containers
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
	private SesameService mSesameService;

	@Inject
	private ImportService mImportService;

	@Inject
	private SparqlService mSparqlService;

	@Inject
	private ICoinsDocFileService mFileServer;

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
					if (entry.isDirectory()) {
						File newDir = new File(tempFolder + File.separator
								+ entry.getName());
						newDir.mkdir();
					}
					else {
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
					}
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
				f = new File(tempFolder + File.separator + "doc");
				if (f.exists() && f.isDirectory()) {
					for (File file : f.listFiles()) {
						count += importData(file, pUser, pContext, pBaseUri);
					}
				}
				deleteFolder(new File(tempFolder));

				updateReferences(pContext);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			mTaskManagerService.endTask(task);
		}
		return count;
	}

	private void updateReferences(URI pContext) {
		// Update references for both version 1.0 and 1.1
		new ReferenceUpdater(pContext, mSparqlService, mFileServer) {
			@Override
			protected CoinsPrefix getPrefix() {
				return CoinsPrefix.CBIM1_0;
			}
		}.execute();
		new ReferenceUpdater(pContext, mSparqlService, mFileServer) {
			@Override
			protected CoinsPrefix getPrefix() {
				return CoinsPrefix.CBIM1_1;
			}
		}.execute();
	}

	private void deleteFolder(File pFolder) {
		if (pFolder != null) {
			File[] files = pFolder.listFiles();
			if (files != null) {
				for (File file : files) {
					if (file.isDirectory()) {
						deleteFolder(file);
					} else {
						file.delete();
					}
				}
			}
			pFolder.delete();
		}
	}

	protected KiWiStore getStore(Repository repository) {
		if (repository instanceof SailRepository) {
			return getStore(((SailRepository) repository).getSail());
		}
		if (repository instanceof RepositoryWrapper) {
			return getStore(((RepositoryWrapper) repository).getDelegate());
		}
		return null;
	}

	/**
	 * Get the root sail in the wrapped sail stack
	 * 
	 * @param sail
	 * @return
	 */
	protected KiWiStore getStore(Sail sail) {
		if (sail instanceof KiWiStore) {
			return (KiWiStore) sail;
		}
		if (sail instanceof SailWrapper) {
			return getStore(((SailWrapper) sail).getBaseSail());
		}
		return null;
	}

	private int importData(File pFile, Resource pUser, URI pContext,
			String pBaseUri) throws MarmottaImportException {
		int result = 0;
		if (pFile.getName().endsWith("owl")) {
			performEncoding(pFile);
			KiWiStore store = getStore(mSesameService.getRepository());
			if (store == null) {
				// Low performance...
				try {
					FileInputStream fis = new FileInputStream(pFile);
					result = mImportService.importData(fis,
							"application/rdf+xml", pUser, pContext);
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				// Much higher performance via KiWiLoader
				KiWiHandler handler;
				KiWiLoaderConfiguration c = new KiWiLoaderConfiguration();
				c.setContext(pContext.stringValue());
				if (store.getPersistence().getDialect() instanceof PostgreSQLDialect) {
					handler = new KiWiPostgresHandler(store, c);
				} else if (store.getPersistence().getDialect() instanceof MySQLDialect) {
					handler = new KiWiMySQLHandler(store, c);
				} else {
					handler = new KiWiHandler(store, c);
				}
				try {
					RDFParser parser = Rio.createParser(RDFFormat.RDFXML);
					parser.setRDFHandler(handler);
					
					Utf8FileInputStream fis = new Utf8FileInputStream(pFile);
					parser.parse(fis, "");
					fis.close();
				} catch (RDFParseException | RDFHandlerException | IOException e1) {
					e1.printStackTrace();
				} finally {
					try {
						handler.shutdown();
					} catch (RDFHandlerException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			mFileServer.importFile(pFile, pContext);
		}
		return result;
	}

	/**
	 * The file may contain illegal characters for the triple store so we perform
	 * HTML encoding on the values...
	 * @param pFile
	 */
	private void performEncoding(File pFile) {
		try {
			File tmpFile = new File(pFile.getPath() + ".tmp");
			final FileWriter writer = new FileWriter(tmpFile);
			XMLReader reader = XMLReaderFactory.createXMLReader();
			InputStream stream = new FileInputStream(pFile);
			InputSource source = new InputSource(stream);
			reader.setContentHandler(new ContentHandler() {
				private int mIndent = -1;
				private List<String> mPrefixes = new Vector<String>();
				private boolean mIsCloseNeeded = false;
				private boolean mIsNewLineWritten = false;
				private boolean mIsFirstNewLine = true;
				
				private void write(String pLine) {					
					mIsNewLineWritten = false;
					try {
						if (mIsCloseNeeded) {
							writer.write(">");
							mIsCloseNeeded = false;
						}
						writer.write(pLine);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				private void writeClose() {
					mIsCloseNeeded = true;
				}
				
				private void indent() {					
					mIndent++;
				}

				private void unindent() {
					mIndent--;
				}
				
				private void newLine() {
					if (mIsFirstNewLine) {
						mIsFirstNewLine = false;
						write("<?xml version=\"1.0\"?>\n");
						return;
					}
					if (mIsNewLineWritten) {
						write("  ");
						return;
					}
					write("\n");
					for (int i = 0; i < mIndent; i++) {
						write("  ");
					}
					mIsNewLineWritten  = true;
				}
				
				@Override
				public void startPrefixMapping(String prefix, String uri)
						throws SAXException {
					if (prefix != null && prefix.trim().length() > 0) {
						mPrefixes.add("xmlns:"+prefix + "=\"" + uri + "\"");
					}
					else {
						mPrefixes.add("xmlns=\"" + uri + "\"");
					}
				}
				
				@Override
				public void startElement(String uri, String localName, String qName,
						Attributes atts) throws SAXException {
					indent();
					newLine();
					if (atts==null || atts.getLength()==0) {
						write("<" + qName);
						writeClose();
					}
					else {
						write("<" + qName + " ");
						for (int i = 0; i < atts.getLength(); i++) {
							write(atts.getQName(i));
							write("=\"");
							write(atts.getValue(i));
							write("\" ");
						}
						if (qName.equals("rdf:RDF")) {
							for (String prefix : mPrefixes) {
								write(prefix + " ");
							}
						}
						writeClose();
					}
				}
				
				@Override
				public void startDocument() throws SAXException {
					//
				}
				
				@Override
				public void skippedEntity(String name) throws SAXException {
					//
				}
				
				@Override
				public void setDocumentLocator(Locator locator) {
					//
				}
				
				@Override
				public void processingInstruction(String target, String data)
						throws SAXException {
					System.err.println(target);
					System.err.println(data);
				}
				
				@Override
				public void ignorableWhitespace(char[] ch, int start, int length)
						throws SAXException {
					//
				}
				
				@Override
				public void endPrefixMapping(String prefix) throws SAXException {
					// Ignore
				}
				
				@Override
				public void endElement(String uri, String localName, String qName)
						throws SAXException {
					unindent();
					if (mIsCloseNeeded) {
						mIsCloseNeeded = false;
						write("/>");
					}
					else {						
						write("</" + qName + ">");
						newLine();
					}
				}
				
				@Override
				public void endDocument() throws SAXException {
					// ignore
				}
				
				@Override
				public void characters(char[] ch, int start, int length)
						throws SAXException {
					StringBuilder sb = new StringBuilder();
					for (int i = 0; i < length; i++) {
						char c = ch[start + i];
					    sb.append(c);
					}
					processValue(sb.toString());
				}
				
				private void processValue(String pValue) {
					if (pValue.replace('\n', ' ').trim().length() > 0) {
						write(HTMLEscaper.escape(pValue));
					}
				}
			});
			reader.parse(source);
			stream.close();
			writer.close();
			
			pFile.delete();
			tmpFile.renameTo(pFile);
		} catch (SAXException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	protected abstract static class ReferenceUpdater {

		private final URI mContext;
		private final SparqlService mSparqlService;
		private final ICoinsDocFileService mFileServer;

		/**
		 * @param pContext
		 * @param pSparqlService
		 * @param pFileServer
		 * @param pConfigurationService
		 */
		public ReferenceUpdater(URI pContext, SparqlService pSparqlService,
				ICoinsDocFileService pFileServer) {
			mSparqlService = pSparqlService;
			mContext = pContext;
			mFileServer = pFileServer;
		}

		protected abstract CoinsPrefix getPrefix();

		/**
		 * 
		 */
		public void execute() {
			File folder = new File(mFileServer.getDocsPath(mContext));
			if (!folder.exists()) {
				return;
			}
			String[] files = folder.list();
			if (files == null) {
				return;
			}
			Set<String> fileSet = new HashSet<String>();
			for (String s : files) {
				fileSet.add(s);
			}
			try {
				String query = "PREFIX " + getPrefix()
						+ "\n\nSELECT * WHERE { GRAPH <" + mContext + "> {\n"
						+ "?object " + CbimAttributeKey.DOCUMENT_URI
						+ " ?documentUri .\n" + "?object "
						+ CbimAttributeKey.DOCUMENT_ALIAS_FILE_PATH
						+ " ?documentAliasFilePath .\n" + "?object a "
						+ CbimObjectKey.EXPLICIT3D_REPRESENTATION + " .\n}}";
				List<Map<String, Value>> result = mSparqlService.query(
						QueryLanguage.SPARQL, query);
				for (Map<String, Value> item : result) {
					String fileName = item.get("documentAliasFilePath")
							.stringValue();
					String oldUri = item.get("documentUri").stringValue();
					if (oldUri.contains("http")) {
						oldUri = "\"" + oldUri + "\"";
					} else {
						// Usually it is no URI
						oldUri = "\""
								+ oldUri
								+ "\"^^<http://www.w3.org/2001/XMLSchema#string>";
					}
					if (fileSet.contains(fileName)) {
						query = "PREFIX " + getPrefix() + "\n\nWITH <"
								+ mContext + "> \nDELETE { ?object "
								+ CbimAttributeKey.DOCUMENT_URI + " " + oldUri
								+ " } \nINSERT { ?object "
								+ CbimAttributeKey.DOCUMENT_URI + " <"
								+ mFileServer.composeUrl(fileName, mContext)
								+ "> } \nWHERE\n { ?object a "
								+ CbimObjectKey.EXPLICIT3D_REPRESENTATION
								+ " .\n ?object "
								+ CbimAttributeKey.DOCUMENT_URI + " " + oldUri
								+ "\n}";
						try {
							mSparqlService.update(QueryLanguage.SPARQL, query);
						} catch (InvalidArgumentException
								| MalformedQueryException
								| UpdateExecutionException e) {
							e.printStackTrace();
						}
					}
				}
			} catch (MarmottaException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * The contents of the File should be passed to the database back end in UTF8. This is the most robust way for ProstgreSQL.
	 */
	private static class Utf8FileInputStream extends InputStream {

		private final BufferedReader mReader;
		private int mIndex = -1;
		private byte[] mBuffer = null;
		
		public Utf8FileInputStream(File pFile) throws FileNotFoundException {
			super();
			mReader = new BufferedReader(new FileReader(pFile));
		}

		@Override
		public int read() throws IOException {
			if (mIndex == -1) {
				String line = mReader.readLine();
				if (line == null) {
					return -1;
				}
				mBuffer = (line + '\n').getBytes("UTF-8");
				mIndex = 0;
			}
			if (mIndex < mBuffer.length) {
				byte result = mBuffer[mIndex];
				mIndex++;
				return result; 
			}
			mIndex = -1;
			return read();
		}
		
		public void close() throws IOException {
			mReader.close();
			super.close();
		}
	}
}
