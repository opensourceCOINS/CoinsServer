package nl.tno.coinsapi.webservices;

import java.util.Calendar;
import java.util.Date;

import junit.framework.Assert;
import nl.tno.coinsapi.services.CoinsDateConverter;
import nl.tno.coinsapi.services.ICoinsDateConversion;

import org.junit.Test;

public class TestDateConversion {

	private ICoinsDateConversion mDateConversion = new CoinsDateConverter();
	
	@Test
	public void testFrom() {
		Date result = mDateConversion.fromString("2010-08-26T12:34:11.000Z");
		Calendar cal = Calendar.getInstance();
		cal.setTime(result);
		Assert.assertEquals(2010, cal.get(Calendar.YEAR));
		Assert.assertEquals(8 - 1, cal.get(Calendar.MONTH));
		Assert.assertEquals(26, cal.get(Calendar.DAY_OF_MONTH));
		Assert.assertEquals(12, cal.get(Calendar.HOUR_OF_DAY));
		Assert.assertEquals(34, cal.get(Calendar.MINUTE));
		Assert.assertEquals(11, cal.get(Calendar.SECOND));
		Assert.assertEquals(0, cal.get(Calendar.MILLISECOND));
	}
	
	@Test
	public void testTo() {
		Date testDate = new Date(1407323085309l);
		String result = mDateConversion.toString(testDate);
		Assert.assertEquals("2014-08-06T13:04:45.309Z", result);
	}

}
