package nl.tno.coinsapi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }
    
    public static void main(String... args) {
    	try {
			Class<?> klaas = AppTest.class.getClassLoader().loadClass("nl.tno.coinsapi.App");
			if (klaas!=null) {
				try {
					Method m = klaas.getMethod("main", String[].class);
					m.invoke(null, (Object)new String[] {"-aap", "-beer"});
				} catch (NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
    }
}
