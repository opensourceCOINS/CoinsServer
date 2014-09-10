package nl.tno.coinsapi.tools;

import java.text.NumberFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;

import org.apache.marmotta.platform.core.exception.MarmottaException;

import nl.tno.coinsapi.CoinsFormat;
import nl.tno.coinsapi.services.ICoinsDateConversion;

/**
 * Class used for creating SPARQL Queries
 */
public abstract class QueryBuilder {

	/**
	 * Field type
	 */
	public enum FieldType {
		/**
		 * String
		 */
		STRING,
		/**
		 * Resource
		 */
		RESOURCE,
		/**
		 * Integer
		 */
		INT,
		/**
		 * Double/Float
		 */
		DOUBLE,
		/**
		 * Date
		 */
		DATE,
		/**
		 * Boolean
		 */
		BOOLEAN;
	}

	protected List<String> mPrefixes = new Vector<String>();
	private Set<String> mPrefixKeys = new HashSet<String>();
	protected String mGraph;
	protected String mId;
	protected List<Item> mAttributes = new Vector<Item>();
	protected final ICoinsDateConversion mDateConversion;

	protected static NumberFormat DOUBLE_NUMBER_FORMAT;

	protected QueryBuilder(ICoinsDateConversion pDateConversion) {
		super();
		mDateConversion = pDateConversion;
	}

	/**
	 * Add a prefix
	 * 
	 * @param pPrefix
	 *            For instance cbim: <http://www.coinsweb.nl/c-bim.owl#>
	 */
	public void addPrefix(String pPrefix) {
		mPrefixKeys.add(pPrefix.split(":")[0]);
		mPrefixes.add(pPrefix);
	}

	/**
	 * @param pDateConversion
	 * @return a query
	 */
	public abstract String build();

	/**
	 * @param pGraph
	 */
	public void addGraph(String pGraph) {
		mGraph = pGraph;
	}

	/**
	 * @param pId
	 * @throws MarmottaException
	 */
	public void setId(String pId) throws MarmottaException {
		if (pId == null) {
			throw new MarmottaException("Id must not be null");
		}
		mId = pId;
	}

	/**
	 * Add an attribute of a certain type
	 * 
	 * @param pName
	 * @param pValue
	 * @param pType
	 */
	public void addAttribute(String pName, String pValue, FieldType pType) {
		switch (pType) {
		case BOOLEAN:
			mAttributes.add(new TypedItem(pName, pValue,
					CoinsFormat.HTTP_WWW_W3_ORG_2001_XML_SCHEMA_BOOLEAN));
			break;
		case DATE:
			mAttributes.add(new TypedItem(pName, pValue,
					CoinsFormat.HTTP_WWW_W3_ORG_2001_XML_SCHEMA_DATE_TIME));
			break;
		case DOUBLE:
			mAttributes.add(new TypedItem(pName, pValue,
					CoinsFormat.HTTP_WWW_W3_ORG_2001_XML_SCHEMA_FLOAT));
			break;
		case INT:
			mAttributes.add(new TypedItem(pName, pValue,
					CoinsFormat.HTTP_WWW_W3_ORG_2001_XML_SCHEMA_INT));
			break;
		case RESOURCE:
			if (isPrefixedValue(pValue)) {
				mAttributes.add(new Item(pName, pValue));
			} else {
				mAttributes.add(new Item(pName, "<" + pValue + ">"));
			}
			break;
		case STRING:
			mAttributes.add(new TypedItem(pName, pValue,
					CoinsFormat.HTTP_WWW_W3_ORG_2001_XML_SCHEMA_STRING));
			break;
		}
	}

	/**
	 * @param pValue
	 */
	public void addAttributeType(String pValue) {
		mAttributes.add(new Item("a", pValue));
	}

	/**
	 * Add an attribute
	 * 
	 * @param pName
	 * @param pValue
	 */
	public void addAttributeString(String pName, String pValue) {
		addAttribute(pName, pValue, FieldType.STRING);
	}

	/**
	 * Add an attribute
	 * 
	 * @param pName
	 * @param pValue
	 */
	public void addAttributeLink(String pName, String pValue) {
		addAttribute(pName, pValue, FieldType.RESOURCE);
	}

	/**
	 * Add an attribute
	 * 
	 * @param pName
	 * @param pValue
	 */
	public void addAttributeDate(String pName, Date pValue) {
		addAttribute(pName, mDateConversion.toString(pValue), FieldType.DATE);
	}

	/**
	 * Add an attribute
	 * 
	 * @param pName
	 * @param pValue
	 */
	public void addAttributeDate(String pName, String pValue) {
		addAttribute(pName, pValue, FieldType.DATE);
	}

	/**
	 * Add an attribute
	 * 
	 * @param pName
	 * @param pValue
	 */
	public void addAttributeInteger(String pName, int pValue) {
		addAttribute(pName, String.valueOf(pValue), FieldType.INT);
	}

	/**
	 * Add an attribute
	 * 
	 * @param pName
	 * @param pValue
	 */
	public void addAttributeBoolean(String pName, boolean pValue) {
		addAttribute(pName, String.valueOf(pValue), FieldType.BOOLEAN);
	}

	/**
	 * Add an attribute
	 * 
	 * @param pName
	 * @param pValue
	 */
	public void addAttributeDouble(String pName, double pValue) {
		addAttribute(pName, DOUBLE_NUMBER_FORMAT.format(pValue),
				FieldType.DOUBLE);
	}

	protected void appendValue(StringBuilder stringBuilder, Item item) {
		stringBuilder.append(item.getValue());
	}

	private boolean isPrefixedValue(String pValue) {
		String items[] = pValue.split(":");
		if (items.length == 2) {
			return mPrefixKeys.contains(items[0]);
		}
		return false;
	}

	/**
	 * Class for creating SPARQL InsertQueries
	 */
	public static class InsertQueryBuilder extends QueryBuilder {

		/**
		 * @param pDateConversion
		 */
		public InsertQueryBuilder(ICoinsDateConversion pDateConversion) {
			super(pDateConversion);
		}

		@Override
		public String build() {
			StringBuilder result = new StringBuilder();
			for (String prefix : mPrefixes) {
				result.append("PREFIX ");
				result.append(prefix);
				result.append("\n");
			}
			result.append("INSERT DATA {\n");
			String tab = "\t";
			if (mGraph != null) {
				result.append("\tGRAPH <");
				result.append(mGraph);
				result.append("> {\n");
				tab = "\t\t";
			}
			result.append(tab);
			result.append("<");
			result.append(mId);
			result.append(">\n");
			boolean isFirst = true;
			for (Item item : mAttributes) {
				if (!isFirst) {
					result.append(" ;\n");
				}
				result.append(tab);
				result.append(item.getName());
				result.append(" ");
				appendValue(result, item);
				isFirst = false;
			}
			result.append("\n");
			if (mGraph != null) {
				result.append("\t}\n");
			}
			result.append("}");
			return result.toString();
		}

	}

	/**
	 * Query builder for update queries
	 * 
	 * Resulting in a query similar to PREFIX rdf:
	 * <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX ex:
	 * <http://example.org/>
	 * 
	 * DELETE {ex:subject1 ex:title ?t ; ex:description ?d ; rdf:type ?c . }
	 * INSERT {ex:subject1 ex:title "foo" ; ex:description "bar" ; rdf:type
	 * ex:FooBar . } WHERE { ex:subject1 ex:title ?t ; ex:description ?d ;
	 * rdf:type ?c . }
	 * 
	 * Note that this query only works if all fields (title/description/type)
	 * must be present before modification is possible...
	 */
	public static class UpdateQueryBuilder extends QueryBuilder {

		/**
		 * @param pDateConversion
		 */
		public UpdateQueryBuilder(ICoinsDateConversion pDateConversion) {
			super(pDateConversion);
		}

		@Override
		public String build() {
			StringBuilder result = new StringBuilder();
			for (String prefix : mPrefixes) {
				result.append("PREFIX ");
				result.append(prefix);
				result.append("\n");
			}
			result.append("WITH <");
			result.append(mGraph);
			result.append("> \nDELETE {\n <");
			result.append(mId);
			result.append("> ");
			boolean isFirst = true;
			for (Item item : mAttributes) {
				if (!isFirst) {
					result.append(" ;\n ");
				}
				result.append(item.getName());
				result.append(" ?");
				result.append(composeVariableName(item));
				isFirst = false;
			}
			result.append(". }\nINSERT {\n <");
			result.append(mId);
			result.append("> ");
			isFirst = true;
			for (Item item : mAttributes) {
				if (!isFirst) {
					result.append(" ;\n ");
				}
				result.append(item.getName());
				result.append(" ");
				appendValue(result, item);
				isFirst = false;
			}
			result.append(" }\nWHERE {\n <");
			result.append(mId);
			result.append("> ");
			isFirst = true;
			for (Item item : mAttributes) {
				if (!isFirst) {
					result.append(" ;\n ");
				}
				result.append(item.getName());
				result.append(" ?");
				result.append(composeVariableName(item));
				isFirst = false;
			}
			result.append(". }");
			return result.toString();
		}

		private String composeVariableName(Item item) {
			int index = item.getName().indexOf(':');
			if (index == -1) {
				index = item.getName().indexOf('#');
			}
			return item.getName().substring(index + 1);
		}
	}

	private static class TypedItem extends Item {

		public TypedItem(String pName, String pValue, String pType) {
			super(pName, "\"" + pValue + "\"^^<" + pType + ">");
		}

	}

	private static class Item {
		private final String mName;
		private final String mValue;

		public Item(String pName, String pValue) {
			mName = pName;
			mValue = pValue;
		}

		public String getName() {
			return mName;
		}

		public String getValue() {
			return mValue;
		}

		@Override
		public String toString() {
			return mName + " : " + mValue;
		}
	}

	static {
		DOUBLE_NUMBER_FORMAT = NumberFormat.getNumberInstance(Locale.US);
		DOUBLE_NUMBER_FORMAT.setGroupingUsed(false);
	}

}
