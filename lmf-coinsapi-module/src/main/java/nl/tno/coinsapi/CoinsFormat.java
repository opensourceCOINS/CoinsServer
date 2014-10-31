package nl.tno.coinsapi;

import java.nio.charset.Charset;
import java.util.Arrays;

import org.openrdf.rio.RDFFormat;

/**
 * Coins format
 */
public class CoinsFormat {

	/**
	 * Coins RDF format
	 */
	public static final RDFFormat FORMAT = new RDFFormat("Coins container",
			Arrays.asList("application/ccr"), Charset.forName("UTF-8"),
			Arrays.asList("ccr"), true, true);

}
