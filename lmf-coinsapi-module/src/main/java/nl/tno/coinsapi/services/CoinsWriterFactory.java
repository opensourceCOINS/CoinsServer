package nl.tno.coinsapi.services;

import java.io.OutputStream;
import java.io.Writer;

import nl.tno.coinsapi.CoinsFormat;

import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.RDFWriterFactory;

/**
 * Coins writer factory
 */
public class CoinsWriterFactory implements RDFWriterFactory {

	@Override
	public RDFFormat getRDFFormat() {
		return CoinsFormat.FORMAT;
	}

	@Override
	public RDFWriter getWriter(OutputStream pOutputStream) {
		return new CoinsWriter(pOutputStream);
	}

	@Override
	public RDFWriter getWriter(Writer pWriter) {
		// Not implemented
		return null;
	}

}
