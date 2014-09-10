package nl.tno.coinsapi.services;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import nl.tno.coinsapi.CoinsFormat;

import org.apache.marmotta.platform.core.exception.MarmottaException;
import org.apache.marmotta.platform.core.util.CDIContext;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.openrdf.rio.RioSetting;
import org.openrdf.rio.WriterConfig;

/**
 * Coins Writer
 */
public class CoinsWriter implements RDFWriter {

	private RDFWriter mDelegateWriter;
	private CoinsZipper mStream;
	private ICoinsDocFileService mDocFileService;
	private String mFileName;
	// This queue is needed because we need to know the filename in our first
	// action.
	// Unfortunately we will get it a while later...
	private List<RDFTask> mQueue;
	private Set<String> mDocumentUrls;

	/**
	 * Constructor
	 * 
	 * @param pOutputStream
	 */
	public CoinsWriter(OutputStream pOutputStream) {
		RDFFormat format = Rio
				.getParserFormatForMIMEType("application/rdf+xml");
		mStream = new CoinsZipper(pOutputStream);
		mDelegateWriter = Rio.createWriter(format, mStream);
		mQueue = new Vector<RDFTask>();
		mFileName = null;
		mDocumentUrls = new HashSet<String>(); 
		mDocFileService = CDIContext.getInstance(ICoinsDocFileService.class);
	}

	private void enqueue(RDFTask pTask) throws RDFHandlerException {
		mQueue.add(pTask);
		if (mFileName != null) {
			processQueue();
		}
	}

	private void processQueue() throws RDFHandlerException {
		while (mQueue.size() > 0) {
			RDFTask item = mQueue.remove(0);
			item.run();
		}
	}

	@Override
	public void startRDF() throws RDFHandlerException {
		enqueue(new RDFTask() {

			@Override
			public void run() throws RDFHandlerException {
				ZipEntry entry = new ZipEntry("bim/" + mFileName);
				try {
					mStream.putNextEntry(entry);
				} catch (IOException e) {
					e.printStackTrace();
				}
				mDelegateWriter.startRDF();
			}
		});
	}

	@Override
	public void endRDF() throws RDFHandlerException {
		// The delegate writer tries to close the stream.
		// It will not succeed because we blocked the close method and close it
		// later...
		enqueue(new RDFTask() {

			@Override
			public void run() throws RDFHandlerException {
				mDelegateWriter.endRDF();
				try {
					mStream.closeEntry();
					writeDocuments();
					mStream.doClose();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (MarmottaException e) {
					e.printStackTrace();
				}

			}
		});
	}

	private void writeDocuments() throws MarmottaException, IOException {
		for (String uriName : mDocumentUrls) {
			File f = mDocFileService.getLocalFile(uriName);
			if (f.exists()) {
				 ZipEntry entry = new ZipEntry("doc/" + f.getName());
				 mStream.putNextEntry(entry);
				 mStream.write(mDocFileService.getFile(f.getAbsolutePath()));
				 mStream.closeEntry();
			}
		}
	}

	@Override
	public void handleComment(final String pComment) throws RDFHandlerException {
		enqueue(new RDFTask() {

			@Override
			public void run() throws RDFHandlerException {
				mDelegateWriter.handleComment(pComment);
			}
		});
	}

	@Override
	public void handleNamespace(final String arg0, final String arg1)
			throws RDFHandlerException {
		enqueue(new RDFTask() {

			@Override
			public void run() throws RDFHandlerException {
				mDelegateWriter.handleNamespace(arg0, arg1);
			}
		});

	}

	@Override
	public void handleStatement(final Statement pStatement)
			throws RDFHandlerException {
		retrieveFileName(pStatement.getSubject().toString());
		if (pStatement.getPredicate() != null
				&& pStatement.getPredicate().getNamespace()
						.contains("http://www.coinsweb.nl/c-bim")
				&& pStatement.getPredicate().getLocalName()
						.equals("documentUri")) {
			if (pStatement.getObject() instanceof Resource) {
				mDocumentUrls.add(pStatement.getObject().toString());
			}
		}
		enqueue(new RDFTask() {

			@Override
			public void run() throws RDFHandlerException {
				mDelegateWriter.handleStatement(pStatement);
			}
		});

	}

	private void retrieveFileName(String pSubject) {
		if (mFileName != null) {
			return;
		}
		if (pSubject == null) {
			return;
		}
		String[] items = pSubject.split("#");
		if (items.length != 2) {
			return;
		}
		int index = items[0].lastIndexOf('/');
		if (index > 0) {
			mFileName = items[0].substring(index + 1);
		}
	}

	@Override
	public RDFFormat getRDFFormat() {
		return CoinsFormat.FORMAT;
	}

	@Override
	public Collection<RioSetting<?>> getSupportedSettings() {
		return mDelegateWriter.getSupportedSettings();
	}

	@Override
	public WriterConfig getWriterConfig() {
		return mDelegateWriter.getWriterConfig();
	}

	@Override
	public void setWriterConfig(WriterConfig pWriterConfig) {
		mDelegateWriter.setWriterConfig(pWriterConfig);
	}

	/**
	 * ZipOutputStream that cannot be closed by 'close' method
	 */
	private static class CoinsZipper extends ZipOutputStream {

		public CoinsZipper(OutputStream out) {
			super(out);
		}

		@Override
		public void close() throws IOException {
			// Ignore this
		}

		/**
		 * @throws IOException
		 */
		public void doClose() throws IOException {
			super.close();
		}
	}

	private interface RDFTask {
		public void run() throws RDFHandlerException;
	}
}
