package nl.tno.coinsapi.services;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.text.DateFormatter;

/**
 * Date converter for COINS
 */
public class CoinsDateConverter implements ICoinsDateConversion {

	private DateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	private DateFormatter mFormatter = new DateFormatter(mFormat);
	
	@Override
	public String toString(Date pDate) {
		try {
			return mFormatter.valueToString(pDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public Date fromString(String pDate) {
		Object result = null;
		try {
			result = mFormatter.stringToValue(pDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (result instanceof Date) {
			return (Date)result;
		}
		return null;
	}

}
