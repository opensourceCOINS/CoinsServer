package nl.tno.coinsapi.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Utility class for reflection that should only be used in test cases
 */
public class ReflectionUtils {

	/**
	 * @param pObjectToBeModified
	 * @param pFieldName
	 * @param pValue
	 * @return true if this operation was successful
	 */
	public static boolean changePrivateIntegerField(Object pObjectToBeModified,
			String pFieldName, int pValue) {
		return changePrivateObjectField(pObjectToBeModified, pFieldName,
				new Integer(pValue));
	}

	/**
	 * @param pObjectToBeModified
	 * @param pFieldName
	 * @param pValue
	 * @return true if this operation was successful
	 */
	public static boolean changePrivateObjectField(Object pObjectToBeModified,
			String pFieldName, Object pValue) {
		try {
			Field f = findField(pObjectToBeModified.getClass(), pFieldName);
			f.setAccessible(true);
			f.set(pObjectToBeModified, pValue);
		} catch (SecurityException e) {
			e.printStackTrace();
			return false;
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			return false;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return false;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * @param <T>
	 * @param pObject
	 *            Object that contains the private function
	 * @param pFunctionName
	 *            Name of the function
	 * @param pClass
	 *            Class of the expected return value
	 * @param pArgs
	 *            Object array containing the arguments
	 * @return the result
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Object> T getPrivateFunctionResult(Object pObject,
			String pFunctionName, Class<T> pClass, Object... pArgs) {
		Method m = findMethod(pObject.getClass(), pFunctionName);
		if (m != null) {
			try {
				return (T) m.invoke(pObject, pArgs);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * @param <T>
	 * @param pObject
	 *            Instance of the class containing the field
	 * @param pFieldName
	 *            Name of the field/member
	 * @param pClass
	 *            Class of the field expected as a return value
	 * @return the reference to this private field/member
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Object> T getPrivateField(Object pObject,
			String pFieldName, Class<T> pClass) {
		try {
			Field f = findField(pObject.getClass(), pFieldName);
			f.setAccessible(true);
			return (T) f.get(pObject);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Call a private method
	 * 
	 * @param pObject
	 * @param pMethodName
	 * @param pArgs
	 * @return true if succeeded
	 */
	public static boolean callPrivateMethod(Object pObject, String pMethodName,
			Object... pArgs) {
		Method m = findMethod(pObject.getClass(), pMethodName);
		if (m != null) {
			try {
				m.invoke(pObject, pArgs);
				return true;
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private static Method findMethod(Class<?> pClass, String pFunctionName) {
		for (Method m : pClass.getDeclaredMethods()) {
			if (m.getName().equals(pFunctionName)) {
				try {
					m.setAccessible(true);
					return m;
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
		}
		if (pClass.getSuperclass() != null) {
			return findMethod(pClass.getSuperclass(), pFunctionName);
		}
		return null;
	}

	private static Field findField(Class<?> pClass, String pFieldName)
			throws NoSuchFieldException {
		Field f = null;
		NoSuchFieldException exception = null;
		try {
			f = pClass.getDeclaredField(pFieldName);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			exception = e;
		}
		if (f == null) {
			if (pClass.getSuperclass() != null) {
				return findField(pClass.getSuperclass(), pFieldName);
			}
		}
		if (exception != null) {
			throw exception;
		}
		return f;
	}
}
