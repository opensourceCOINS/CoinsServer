package nl.tno.coinsapi.tools;

import java.text.NumberFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;

import nl.tno.coinsapi.CoinsPrefix;
import nl.tno.coinsapi.W3Schema;
import nl.tno.coinsapi.keys.IAttributeKey;
import nl.tno.coinsapi.keys.IObjectKey;
import nl.tno.coinsapi.keys.SPARQL_AttributeKey;
import nl.tno.coinsapi.services.ICoinsDateConversion;

import org.apache.marmotta.platform.core.exception.MarmottaException;

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

	protected Set<CoinsPrefix> mPrefixes = new HashSet<CoinsPrefix>();
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
	public void addPrefix(CoinsPrefix pPrefix) {
		if (pPrefix != null) {
			mPrefixKeys.add(pPrefix.getKey());
			mPrefixes.add(pPrefix);
		}
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
	public void addAttribute(IAttributeKey pName, String pValue, FieldType pType) {
		switch (pType) {
		case BOOLEAN:
			mAttributes.add(new TypedItem(pName, pValue,
					W3Schema.HTTP_WWW_W3_ORG_2001_XML_SCHEMA_BOOLEAN));
			break;
		case DATE:
			mAttributes.add(new TypedItem(pName, pValue,
					W3Schema.HTTP_WWW_W3_ORG_2001_XML_SCHEMA_DATE_TIME));
			break;
		case DOUBLE:
			mAttributes.add(new TypedItem(pName, pValue,
					W3Schema.HTTP_WWW_W3_ORG_2001_XML_SCHEMA_FLOAT));
			break;
		case INT:
			mAttributes.add(new TypedItem(pName, pValue,
					W3Schema.HTTP_WWW_W3_ORG_2001_XML_SCHEMA_INT));
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
					W3Schema.HTTP_WWW_W3_ORG_2001_XML_SCHEMA_STRING));
			break;
		}
	}

	/**
	 * @param pValue
	 */
	public void addAttributeType(IObjectKey pValue) {
		mAttributes.add(new Item(SPARQL_AttributeKey.A, pValue.toString()));
	}

	/**
	 * Add an attribute
	 * 
	 * @param pName
	 * @param pValue
	 */
	public void addAttributeString(IAttributeKey pName, String pValue) {
		addAttribute(pName, pValue, FieldType.STRING);
	}

	/**
	 * Add an attribute
	 * 
	 * @param pName
	 * @param pValue
	 */
	public void addAttributeResource(IAttributeKey pName, String pValue) {
		addAttribute(pName, pValue, FieldType.RESOURCE);
	}

	/**
	 * Add an attribute
	 * 
	 * @param pName
	 * @param pValue
	 */
	public void addAttributeDate(IAttributeKey pName, Date pValue) {
		addAttribute(pName, mDateConversion.toString(pValue), FieldType.DATE);
	}

	/**
	 * Add an attribute
	 * 
	 * @param pName
	 * @param pValue
	 */
	public void addAttributeDate(IAttributeKey pName, String pValue) {
		addAttribute(pName, pValue, FieldType.DATE);
	}

	/**
	 * Add an attribute
	 * 
	 * @param pName
	 * @param pValue
	 */
	public void addAttributeInteger(IAttributeKey pName, int pValue) {
		addAttribute(pName, String.valueOf(pValue), FieldType.INT);
	}

	/**
	 * Add an attribute
	 * 
	 * @param pName
	 * @param pValue
	 */
	public void addAttributeBoolean(IAttributeKey pName, boolean pValue) {
		addAttribute(pName, String.valueOf(pValue), FieldType.BOOLEAN);
	}

	/**
	 * Add an attribute
	 * 
	 * @param pName
	 * @param pValue
	 */
	public void addAttributeDouble(IAttributeKey pName, double pValue) {
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
			for (CoinsPrefix prefix : mPrefixes) {
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
			for (CoinsPrefix prefix : mPrefixes) {
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
				result.append(item.getName().getNonPrefixedName());
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
				result.append(item.getName().getNonPrefixedName());
				isFirst = false;
			}
			result.append(". }");
			return result.toString();
		}

	}

	private static class TypedItem extends Item {

		public TypedItem(IAttributeKey pName, String pValue, W3Schema pType) {
			super(pName, "\"" + pValue + "\"^^<" + pType.toString() + ">");
		}

	}

	private static class Item {
		private final IAttributeKey mName;
		private final String mValue;

		public Item(IAttributeKey pName, String pValue) {
			mName = pName;
			mValue = pValue;
		}

		public IAttributeKey getName() {
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

	/**
	 * @param pValue
	 * @return value as string for query
	 */
	public static String doubleToString(double pValue) {
		return DOUBLE_NUMBER_FORMAT.format(pValue);
	}

	static {
		DOUBLE_NUMBER_FORMAT = NumberFormat.getNumberInstance(Locale.US);
		DOUBLE_NUMBER_FORMAT.setGroupingUsed(false);
	}

	/**
	 * Append prefixes to the StringBuilder
	 * @param result
	 * @param keys
	 */
	public static void appendPrefixesForKeys(StringBuilder result,
			List<IObjectKey> keys) {
		Set<CoinsPrefix> prefixes = new HashSet<CoinsPrefix>();
		for (IObjectKey key : keys) {
			prefixes.add(key.getPrefix());
		}
		for (CoinsPrefix prefix : prefixes) {
			result.append("PREFIX ");
			result.append(prefix);
			result.append("\n");
		}
	}

}
