package nl.tno.coinsapi;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 * Enumeration for some W3 schema constants
 */
public enum W3Schema {

	/**
	 * http://www.w3.org/2001/XMLSchema#boolean
	 */
	HTTP_WWW_W3_ORG_2001_XML_SCHEMA_BOOLEAN ("http://www.w3.org/2001/XMLSchema#boolean"),

	/**
	 * http://www.w3.org/2001/XMLSchema#dateTime
	 */
	HTTP_WWW_W3_ORG_2001_XML_SCHEMA_DATE_TIME ("http://www.w3.org/2001/XMLSchema#dateTime"),

	/**
	 * http://www.w3.org/2001/XMLSchema#boolean
	 */
	HTTP_WWW_W3_ORG_2001_XML_SCHEMA_FLOAT ("http://www.w3.org/2001/XMLSchema#float"),

	/**
	 * http://www.w3.org/2001/XMLSchema#int
	 */
	HTTP_WWW_W3_ORG_2001_XML_SCHEMA_INT ("http://www.w3.org/2001/XMLSchema#int"),

	/**
	 * http://www.w3.org/2001/XMLSchema#string
	 */
	HTTP_WWW_W3_ORG_2001_XML_SCHEMA_STRING ("http://www.w3.org/2001/XMLSchema#string");

	private final String mStringRepresentation;
	private final URI mURI;

	W3Schema(String pStringRepresentation) {
		mStringRepresentation = pStringRepresentation;
		mURI = new URIImpl(pStringRepresentation);
	}
	
	public String toString() {
		return mStringRepresentation;
	}
	
	/**
	 * @return the enum as an URI
	 */
	public URI toUri() {
		return mURI;
	}
}
