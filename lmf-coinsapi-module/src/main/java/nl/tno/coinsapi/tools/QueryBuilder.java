package nl.tno.coinsapi.tools;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

/**
 * Class used for creating SPARQL Queries
 */
public abstract class QueryBuilder {

	protected final static Set<String> URI_NAMES = new HashSet<String>();

	protected List<String> mPrefixes = new Vector<String>();
	protected String mGraph;
	protected String mId;
	protected Map<String, Object> mAttributes = new HashMap<String, Object>();

	/**
	 * Add a prefix
	 * 
	 * @param pPrefix
	 *            For instance cbim: <http://www.coinsweb.nl/c-bim.owl#>
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
	public void addAttribute(String pName, String pValue) {
		mAttributes.put(pName, pValue);
	}

	/**
	 * Add an attribute
	 * 
	 * @param pName
	 * @param pValue
	 */
	public void addAttribute(String pName, int pValue) {
		mAttributes.put(pName, pValue);
	}

	/**
	 * Add an attribute
	 * 
	 * @param pName
	 * @param pValue
	 */
	public void addAttribute(String pName, double pValue) {
		mAttributes.put(pName, pValue);
	}

	protected void appendValue(StringBuilder stringBuilder,
			Entry<String, Object> entry) {
		if (entry.getKey().equals("a")) {
			stringBuilder.append(entry.getValue());
		} else {
			if (URI_NAMES.contains(entry.getKey())) {
				stringBuilder.append('<');
				stringBuilder.append(entry.getValue());
				stringBuilder.append('>');
			} else {
				stringBuilder.append('"');
				stringBuilder.append(entry.getValue());
				stringBuilder.append('"');
			}
		}
	}

	/**
	 * Class for creating SPARQL InsertQueries
	 */
	public static class InsertQueryBuilder extends QueryBuilder {

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
			for (Entry<String, Object> entry : mAttributes.entrySet()) {
				if (!isFirst) {
					result.append(" ;\n ");
				}
				result.append(entry.getKey());
				result.append(" ?");
				result.append(composeVariableName(entry));
				isFirst = false;
			}
			result.append(". }\nINSERT {\n <");
			result.append(mId);
			result.append("> ");
			isFirst = true;
			for (Entry<String, Object> entry : mAttributes.entrySet()) {
				if (!isFirst) {
					result.append(" ;\n ");
				}
				result.append(entry.getKey());
				result.append(" ");
				appendValue(result, entry);
				isFirst = false;
			}
			result.append(" }\nWHERE {\n <");
			result.append(mId);
			result.append("> ");
			isFirst = true;
			for (Entry<String, Object> entry : mAttributes.entrySet()) {
				if (!isFirst) {
					result.append(" ;\n ");
				}
				result.append(entry.getKey());
				result.append(" ?");
				result.append(composeVariableName(entry));
				isFirst = false;
			}
			result.append(". }");
			return result.toString();
		}
		
		private String composeVariableName(Entry<String, Object> entry) {
			int index = entry.getKey().indexOf(':');
			if (index==-1) {
				index = entry.getKey().indexOf('#');
			}
			return entry.getKey().substring(index + 1);
		}
	}
	
	static {
		URI_NAMES.add("cbim:creator");
		URI_NAMES.add("cbim:modifier");
		URI_NAMES.add("cbim:isFulfilledBy");
		URI_NAMES.add("cbim:physicalParent");
		URI_NAMES.add("cbim:shape");
		URI_NAMES.add("cbim:primaryOrientation");
		URI_NAMES.add("cbim:secondaryOrientation");
		URI_NAMES.add("cbim:translation");
		URI_NAMES.add("cbim:requirementOf");
		URI_NAMES.add("cbim:documentUri");
		URI_NAMES.add("cbim:nonFunctionalRequirementType");
	}
}
