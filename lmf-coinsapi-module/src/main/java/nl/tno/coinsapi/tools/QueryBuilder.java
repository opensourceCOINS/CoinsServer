package nl.tno.coinsapi.tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

/**
 * Class used for creating SPARQL Queries
 */
public abstract class QueryBuilder {

	protected List<String> mPrefixes = new Vector<String>();
	protected String mGraph;
	
	/**
	 * Add a prefix 
	 * @param pPrefix 
	 *  	For instance cbim: <http://www.coinsweb.nl/c-bim.owl#>
	 */
	public void addPrefix(String pPrefix) {
		mPrefixes.add(pPrefix);
	}
	
	/**
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
	 * Class for creating SPARQL InsertQueries
	 */
	public static class InsertQueryBuilder extends QueryBuilder{

		private String mId;
		private Map<String, Object> mAttributes = new HashMap<String, Object>();

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
			for (Entry<String, Object> entry : mAttributes.entrySet()) {
				if (!isFirst) {
					result.append(" ;\n");					
				}
				result.append(tab);
				result.append(entry.getKey());
				result.append(" ");
				appendValue(result, entry);
				isFirst = false;
			}
			result.append("\n");
			if (mGraph != null) {				
				result.append("\t}\n");
			}			
			result.append("}");
			return result.toString();
		}

		private void appendValue(StringBuilder stringBuilder,
				Entry<String, Object> entry) {
			if (entry.getKey().equals("a")) {
				stringBuilder.append(entry.getValue());
			}
			else {
				stringBuilder.append('"');
				stringBuilder.append(entry.getValue());
				stringBuilder.append('"');
			}
		}

		/**
		 * @param pId
		 */
		public void setId(String pId) {
			mId = pId;
		}
		
		/**
		 * Add an attribute
		 * @param pName
		 * @param pValue
		 */
		public void addAttribute(String pName, String pValue) {
			mAttributes.put(pName, pValue);
		}
		
		/**
		 * Add an attribute
		 * @param pName
		 * @param pValue
		 */
		public void addAttribute(String pName, int pValue) {
			mAttributes.put(pName, pValue);
		}
		
		/**
		 * Add an attribute
		 * @param pName
		 * @param pValue
		 */
		public void addAttribute(String pName, double pValue) {
			mAttributes.put(pName, pValue);
		}
		
	}

}
