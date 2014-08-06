package nl.tno.coinsapi.services;

import java.util.Date;

public interface ICoinsDateConversion {

	/**
	 * @param pDate
	 * @return a Date formatted as 2010-08-26T12:34:34.000Z
	 */
	public String toString(Date pDate);
	
	/**
	 * @param pDate
	 * @return a Java Date
	 */
	public Date fromString(String pDate);
	
}
