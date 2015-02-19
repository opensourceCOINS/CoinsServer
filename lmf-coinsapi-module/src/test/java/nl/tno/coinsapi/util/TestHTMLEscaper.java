package nl.tno.coinsapi.util;

import nl.tno.coinsapi.tools.HTMLEscaper;

import org.junit.Assert;
import org.junit.Test;

/**
 * Class for testing html escapes
 */
public class TestHTMLEscaper {

	/**
	 * Test
	 */
	@Test
	public void doTheTests() {
		Assert.assertEquals("Hallo &#60;Dit is een testje&#62;", HTMLEscaper.escape("Hallo <Dit is een testje>"));
		// &sub2;
		//Assert.assertEquals("Wat levert &#178; eigenlijk op?", HTMLEscaper.escape("Wat levert ² eigenlijk op?"));
	}
}
