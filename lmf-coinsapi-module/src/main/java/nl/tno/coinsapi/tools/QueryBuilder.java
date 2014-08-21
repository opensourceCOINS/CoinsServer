package nl.tno.coinsapi.tools;

import java.text.NumberFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;

import nl.tno.coinsapi.services.ICoinsDateConversion;

/**
 * Class used for creating SPARQL Queries
 */
public abstract class QueryBuilder {

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
	 */
	public void setId(String pId) {
		mId = pId;
	}

	/**
	 * Add an attribute
	 * 
	 * @param pName
	 * @param pValue
	 */
	public void addAttributeString(String pName, String pValue) {
		if (pName.equals("a")) {
			mAttributes.add(new Item(pName, pValue));
		}
		else {			
			mAttributes.add(new Item(pName, "\"" + pValue + "\"^^<http://www.w3.org/2001/XMLSchema#string>"));
		}
	}

	/**
	 * Add an attribute
	 * 
	 * @param pName
	 * @param pValue
	 */
	public void addAttributeLink(String pName, String pValue) {
		if (isPrefixedValue(pValue)) {
			mAttributes.add(new Item(pName, pValue));			
		}
		else {			
			mAttributes.add(new Item(pName, "<" + pValue + ">"));
		}
	}

	private boolean isPrefixedValue(String pValue) {
		String items[] = pValue.split(":");
		if (items.length == 2) {
			return mPrefixKeys.contains(items[0]);
		}
		return false;
	}

	/**
	 * Add an attribute
	 * 
	 * @param pName
	 * @param pValue
	 */
	public void addAttributeDate(String pName, Date pValue) {
		mAttributes.add(new Item(pName, "\"" + mDateConversion.toString(pValue)
				+ "\"^^<http://www.w3.org/2001/XMLSchema#dateTime>"));
	}

	/**
	 * Add an attribute
	 * 
	 * @param pName
	 * @param pValue
	 */
	public void addAttributeDate(String pName, String pValue) {
		mAttributes.add(new Item(pName, "\"" + pValue
				+ "\"^^<http://www.w3.org/2001/XMLSchema#dateTime>"));
	}

	/**
	 * Add an attribute
	 * 
	 * @param pName
	 * @param pValue
	 */
	public void addAttributeInteger(String pName, int pValue) {
		mAttributes.add(new Item(pName, "\"" + pValue + "\"^^<http://www.w3.org/2001/XMLSchema#int>"));
	}

	/**
	 * Add an attribute
	 * 
	 * @param pName
	 * @param pValue
	 */
	public void addAttributeDouble(String pName, double pValue) {
		mAttributes.add(new Item(pName, "\""+ DOUBLE_NUMBER_FORMAT.format(pValue) + "\"^^<http://www.w3.org/2001/XMLSchema#float>"));
	}

	protected void appendValue(StringBuilder stringBuilder,
			Item item) {
		stringBuilder.append(item.getValue());
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
	 * Resulting in a query similar to
	 * PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
	 * PREFIX ex: <http://example.org/> 
	 * 
	 * DELETE {ex:subject1 ex:title ?t ;
	 *                     ex:description ?d ; 
	 *                     rdf:type ?c . 
	 *        }
	 * INSERT {ex:subject1 ex:title "foo" ;
	 *                     ex:description "bar" ;
	 *                     rdf:type ex:FooBar .  
	 * }
	 * WHERE  { 
	 *         ex:subject1 ex:title ?t ;
	 *                     ex:description ?d ;
	 *                     rdf:type ?c . 
	 * }
	 *
	 * Note that this query only works if all fields (title/description/type) must be present
	 * before modification is possible...
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
			if (index==-1) {
				index = item.getName().indexOf('#');
			}
			return item.getName().substring(index + 1);
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
