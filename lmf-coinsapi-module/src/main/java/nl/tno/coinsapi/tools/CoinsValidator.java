package nl.tno.coinsapi.tools;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import nl.tno.coinsapi.services.CoinsApiService;
import nl.tno.coinsapi.services.ICoinsApiService.ValidationAspect;

import org.apache.marmotta.platform.core.exception.MarmottaException;
import org.apache.marmotta.platform.sparql.api.sparql.SparqlService;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.QueryLanguage;

/**
 * Coins Validator Object
 */
public abstract class CoinsValidator {

	protected String mContext = null;
	protected SparqlService mSparqlService = null;
	protected List<String> mErrors = new Vector<String>();
	
	/**
	 * @return true if OK
	 */
	public abstract boolean validate();
	
	/**
	 * Set the context
	 * @param pContext
	 */
	public void setContext(String pContext) {
		mContext = pContext; 
	}

	/**
	 * @param pSparqlService
	 */
	public void setSparqlService(SparqlService pSparqlService) {
		mSparqlService = pSparqlService;
	}

	/**
     * @return a list of errors (only if validate returns false)
	 */
	public List<String> getValidationErrors() {
		return mErrors;
	}

	/**
	 * Validator validating all aspects
	 */
	public static class CoinsAllValidator extends CoinsValidator {

		private List<CoinsValidator> mValidators = new Vector<CoinsValidator>();
		
		/**
		 * Constructor
		 */
		public CoinsAllValidator() {
			for (ValidationAspect aspect : ValidationAspect.values()) {
				switch (aspect) {
				case ALL:
					break;
				case FUNCTIONFULFILLERS:
					mValidators.add(new CoinsFunctionFulfillerValidator());
					break;
				case PHYSICALPARENT:
					mValidators.add(new CoinsPhysicalParentValidator());
					break;
				case LITERALS:
					mValidators.add(new CoinsLiteralValidator());
					break;
				case PHYSICALOBJECT_PARENT_CHILD:
					mValidators.add(new CoinsPhysicalObjectParentChildValidator());
					break;
				case SPACE_PARENT_CHILD:
					mValidators.add(new CoinsSpaceParentChildValidator());
					break;
				case AFFECTS:
					mValidators.add(new CoinsAffectsValidator());
					break;
				case REQUIREMENT:
					mValidators.add(new CoinsRequirementValidator());
					break;
				case SITUATES:
					mValidators.add(new CoinsSituatedValidator());
					break;
				}				
			}
		}
		
		@Override
		public boolean validate() {
			mErrors.clear();
			for (CoinsValidator validator : mValidators) {
				if (!validator.validate()) {
					for (String error : validator.getValidationErrors()) {
						mErrors.add(error);
					}
				}
			}
			return (mErrors.size() == 0);
		}

		@Override
		public void setContext(String pContext) {
			for (CoinsValidator validator : mValidators) {
				validator.setContext(pContext);
			}
		}

		@Override
		public void setSparqlService(SparqlService pSparqlService) {
			for (CoinsValidator validator : mValidators) {
				validator.setSparqlService(pSparqlService);
			}
		}

		@Override
		public List<String> getValidationErrors() {
			return mErrors;
		}
	}

	/**
	 * Validator for literals
	 */
	public static class CoinsLiteralValidator extends CoinsValidator {
		
		@Override
		public boolean validate() {
			boolean isOk = true;
			mErrors.clear();
			StringBuilder queryBuilder = new StringBuilder();
			queryBuilder.append("PREFIX ");
			queryBuilder.append(CoinsApiService.PREFIX_CBIM);
			queryBuilder.append("\nPREFIX ");
			queryBuilder.append(CoinsApiService.PREFIX_CBIMFS);
			queryBuilder.append("\n\nSELECT ?object ?value WHERE {\n\tGRAPH <");
			queryBuilder.append(mContext);
			queryBuilder.append("> {\n\t\t?object %s ?value }}");
			String query = queryBuilder.toString();
			List<Map<String, Value>> result = null;
			for (Relation rel : getRelations()) {
				try {
					result = mSparqlService.query(QueryLanguage.SPARQL,
							String.format(query, rel.getRelation()));
				} catch (MarmottaException e) {
					e.printStackTrace();
					mErrors.add("MarmottaException");
					return false;
				}
				for (Map<String, Value> item : result) {
					String message = rel.check(item.get("object"),
							item.get("value"));
					if (message != null) {
						isOk = false;
						mErrors.add(message);
					}
				}
			}
			return isOk;
		}
		
		private List<Relation> getRelations() {
			List<Relation> result = new Vector<Relation>();
			result.add(new ResourceRelation("cbim:creator", "cbim:PersonOrOrganisation"));
			result.add(new ResourceRelation("cbim:modifier", "cbim:PersonOrOrganisation"));
			result.add(new ResourceRelation("cbim:spatialChild", "cbim:Space"));
			result.add(new ResourceRelation("cbim:spatialParent", "cbim:Space"));
			result.add(new ResourceRelation("cbim:physicalParent", "cbim:PhysicalObject"));
			result.add(new ResourceRelation("cbim:physicalChild", "cbim:PhysicalObject"));
			result.add(new ResourceRelation("cbim:isFulfilledBy", "cbim:FunctionFulfiller"));
			result.add(new ResourceRelation("cbim:fulfills", "cbim:Function"));
			result.add(new ResourceRelation("cbim:requirementOf", "cbim:Function"));
			result.add(new ResourceRelation("cbim:requirement", "cbim:Requirement"));
			result.add(new ResourceRelation("cbim:isSituatedIn", "cbim:Space"));
			result.add(new ResourceRelation("cbim:situates", "cbim:PhysicalObject"));
			result.add(new ResourceRelation("cbim:affects", "cbim:FunctionFulfiller"));
			result.add(new ResourceRelation("cbim:isAffectedBy", "cbim:Task"));
			result.add(new ResourceRelation("cbim:amount", "cbim:CatalogPart"));
			result.add(new ResourceRelation("cbim:shape", "cbim:Explicit3DRepresentation"));
			result.add(new ResourceRelation("cbim:primaryOrientation", "cbim:Vector"));
			result.add(new ResourceRelation("cbim:secondaryOrientation", "cbim:Vector"));
			result.add(new ResourceRelation("cbim:translation", "cbim:Vector"));
			result.add(new ResourceRelation("cbim:firstParameter", "cbim:Parameter"));
			result.add(new ResourceRelation("cbim:nextParameter", "cbim:Parameter"));
			result.add(new ResourceRelation("cbim:documentUri", null));

			
			result.add(new LiteralRelation("cbim:endDateActual", "http://www.w3.org/2001/XMLSchema#dateTime"));
			result.add(new LiteralRelation("cbim:startDate", "http://www.w3.org/2001/XMLSchema#dateTime"));
			result.add(new LiteralRelation("cbim:startDateActual", "http://www.w3.org/2001/XMLSchema#dateTime"));
			result.add(new LiteralRelation("cbim:startDatePlanned", "http://www.w3.org/2001/XMLSchema#dateTime"));
			result.add(new LiteralRelation("cbim:endDate", "http://www.w3.org/2001/XMLSchema#dateTime"));
			result.add(new LiteralRelation("cbim:releaseDate", "http://www.w3.org/2001/XMLSchema#dateTime"));
			result.add(new LiteralRelation("cbim:creationDate", "http://www.w3.org/2001/XMLSchema#dateTime"));
			result.add(new LiteralRelation("cbim:modificationDate", "http://www.w3.org/2001/XMLSchema#dateTime"));
			result.add(new LiteralRelation("cbim:name", "http://www.w3.org/2001/XMLSchema#string"));
			result.add(new LiteralRelation("cbim:documentType", "http://www.w3.org/2001/XMLSchema#string"));
			result.add(new LiteralRelation("cbim:documentAliasFilePath", "http://www.w3.org/2001/XMLSchema#string"));
			result.add(new LiteralRelation("cbim:userID", "http://www.w3.org/2001/XMLSchema#string"));
			result.add(new LiteralRelation("cbim:layerIndex", "http://www.w3.org/2001/XMLSchema#int"));
			for (Relation rel : result) {
				if (rel instanceof ResourceRelation) {
					ResourceRelation relation = (ResourceRelation)rel;
					relation.setContext(mContext);
					relation.setSparqlService(mSparqlService);
				}
			}
			return result;
		}
		
		private abstract static class Relation {

			private final String mRelation;
			
			public Relation(String pRelation) {
				mRelation = pRelation;
			}

			/**
			 * @return the relation
			 */
			public String getRelation() {
				return mRelation;
			}			
			
			public abstract String check(Value pObject, Value pValue);
		}
		
		private static class LiteralRelation extends Relation {

			private final URI mDateType;

			public LiteralRelation(String pRelation, String pDateType) {
				super(pRelation);
				mDateType = new URIImpl(pDateType);
			}

			@Override
			public String check(Value pObject, Value pValue) {
				if (pValue instanceof Literal) {
					Literal literal = (Literal)pValue;
					if (literal.getDatatype()==null) {
						return pObject.stringValue() + " : " + getRelation() + " has no data type (should be " + mDateType.stringValue() + ")";
					}
					if (!literal.getDatatype().equals(mDateType)) {
						return pObject.stringValue() + " : " + getRelation() + " has invalid data type (" + literal.getDatatype().stringValue() +" instead of " + mDateType.stringValue() + ")";
					}
				}
				else {
					return pValue.stringValue() + " : " + getRelation() + " should be literal";						
				}
				return null;
			}
			
		}
		
		private static class ResourceRelation extends Relation {

			private final String mDataType;
			private String mContext = null;
			private SparqlService mSparqlService = null;
			
			public ResourceRelation(String pRelation, String pDataType) {
				super(pRelation);
				mDataType = pDataType;
			}

			private void setContext(String pContext) {
				mContext = pContext;
			}
			
			private void setSparqlService(SparqlService pSparqlService) {
				mSparqlService = pSparqlService;
			}
			
			private String[] getDataTypes() {
				if (mDataType.equals("cbim:FunctionFulfiller")) {
					return new String[] {"cbim:PhysicalObject", "cbim:Space"};
				}
				return new String[] {mDataType};
			}
			
			@Override
			public String check(Value pObject, Value pValue) {
				if (pValue instanceof Resource) {
					if (mDataType==null) {
						// No check on reference
						return null;
					}
					StringBuilder query = new StringBuilder();
					query.append("PREFIX ");
					query.append(CoinsApiService.PREFIX_CBIM);
					query.append("\nPREFIX ");
					query.append(CoinsApiService.PREFIX_CBIMFS);
					query.append("\nSELECT (COUNT(?value) as ?counter) WHERE {\n\tGRAPH <");
					query.append(mContext);
					query.append("> {\n");
					boolean isFirst = true;
					for (String dataType : getDataTypes()) {
						if (!isFirst) {
							query.append("\n\tUNION\n");
						}
						query.append("\t\t{<");
						query.append(pValue.stringValue());
						query.append("> ?relation ?value ; a ");
						query.append(dataType);
						query.append("}");
						isFirst = false;
					}
					query.append("}}");
					List<Map<String, Value>> result;
					try {
						result = mSparqlService.query(QueryLanguage.SPARQL,
								query.toString());
					} catch (MarmottaException e) {
						e.printStackTrace();
						return "MarmottaException";
					}
					for (Map<String, Value> item : result) {
						Value v = item.get("counter");
						if (v.stringValue().equals("0")) {
							return pObject.stringValue() + " : " + getRelation() + " should refer to <" + mDataType + ">. No values of that type found.";
						}
					}

					return null;
				}
				return pObject.stringValue() + " : " + getRelation() + " should be resource";
			}
			
		}
	}
	
	/**
	 * Physical Parent validator
	 */
	public static class CoinsPhysicalParentValidator extends CoinsValidator {
		
		@Override
		public boolean validate() {
			boolean isOk = true;
			mErrors.clear();
			Set<String> children = new HashSet<String>(); 
			StringBuilder query = new StringBuilder();
			query.append("PREFIX ");
			query.append(CoinsApiService.PREFIX_CBIM);
			query.append("\n\nSELECT ?child ?parent WHERE {\n\tGRAPH <");
			query.append(mContext);
			query.append("> {\n\t\t?child cbim:physicalParent ?parent }}");
			List<Map<String, Value>> result = null;
			try {
				result = mSparqlService.query(QueryLanguage.SPARQL, query.toString());
			} catch (MarmottaException e) {
				e.printStackTrace();
				mErrors.add("MarmottaException");
				return false;
			}
			for (Map<String, Value> item : result) {
				String child = item.get("child").stringValue();
				if (!children.add(child)) {
					mErrors.add("Object <" + child + "> has multiple parents.");
					isOk = false;
				}
			}
			return isOk;
		}
		
	}

	protected static abstract class CoinsTwoWayRelationValidator extends CoinsValidator {
		
		@Override
		public boolean validate() {
			boolean isOk = true;
			mErrors.clear();
			Set<String> couples = new HashSet<String>();
			StringBuilder query = new StringBuilder();
			query.append("PREFIX ");
			query.append(CoinsApiService.PREFIX_CBIM);
			query.append("\n\nSELECT ?function ?fulfiller WHERE {\n\tGRAPH <");
			query.append(mContext);
			query.append("> {\n\t\t?function ");
			query.append(getTo());
			query.append(" ?fulfiller }}");
			List<Map<String, Value>> result = null;
			try {
				result = mSparqlService.query(QueryLanguage.SPARQL, query.toString());
				for (Map<String, Value> item : result) {
					String couple = item.get("function").toString() + " - " + item.get("fulfiller").toString();
					couples.add(couple);
				}
			} catch (MarmottaException e) {
				e.printStackTrace();
				mErrors.add("MarmottaException");
				return false;
			}
			query = new StringBuilder();
			query.append("PREFIX ");
			query.append(CoinsApiService.PREFIX_CBIM);
			query.append("\n\nSELECT ?function ?fulfiller WHERE {\n\tGRAPH <");
			query.append(mContext);
			query.append("> {\n\t\t?fulfiller ");
			query.append(getFrom());
			query.append(" ?function }}");
			try {
				result = mSparqlService.query(QueryLanguage.SPARQL, query.toString());
				for (Map<String, Value> item : result) {
					String couple = item.get("function").toString() + " - " + item.get("fulfiller").toString();
					if (!couples.add(couple)) {						
						isOk=false;
						mErrors.add(getToDescription() + " <" + item.get("function").toString() + "> is linked to " + getFromDescription() + " <" + item.get("fulfiller") + "> and vice versa (both "+ getTo() +" and " + getFrom()+ ")");
					}
				}
			} catch (MarmottaException e) {
				e.printStackTrace();
				mErrors.add("MarmottaException");
				return false;
			}
			
			return isOk;
		}

		/**
		 * @return example: cbim:isFulfilledBy
		 */
		protected abstract String getTo();
		
		/**
		 * @return example: Function
		 */
		protected abstract String getToDescription();
		
		/**
		 * @return example: cbim:fulfills
		 */
		protected abstract String getFrom();
		
		/**
		 * @return example: function fulfiller
		 */
		protected abstract String getFromDescription();

	}
	
	/**
	 * Validator for validating function fulfillers
	 */
	public static class CoinsFunctionFulfillerValidator extends CoinsTwoWayRelationValidator {

		@Override
		protected String getTo() {
			return "cbim:isFulfilledBy";
		}

		@Override
		protected String getToDescription() {
			return "Function";
		}

		@Override
		protected String getFrom() {
			return "cbim:fulfills";
		}

		@Override
		protected String getFromDescription() {			
			return "function fulfiller";
		}
		
	}
	
	/**
	 * cbim:physicalParent
	 * cbim:physicalChild
	 */
	public static class CoinsPhysicalObjectParentChildValidator extends CoinsTwoWayRelationValidator {

		@Override
		protected String getTo() {
			return "cbim:physicalParent";
		}

		@Override
		protected String getToDescription() {
			return "Physical parent";
		}

		@Override
		protected String getFrom() {			
			return "cbim:physicalChild";
		}

		@Override
		protected String getFromDescription() {
			return "physical child";
		}
		
	}
	
	/**
	 * 	cbim:spatialChild
	 * 	cbim:spatialParent
	 */
	public static class CoinsSpaceParentChildValidator extends CoinsTwoWayRelationValidator {

		@Override
		protected String getTo() {
			return "cbim:spatialParent";
		}

		@Override
		protected String getToDescription() {
			return "spatial parent";
		}

		@Override
		protected String getFrom() {
			return "cbim:spatialChild";
		}

		@Override
		protected String getFromDescription() {
			return "spatial child";
		}
		
	}
	
	/**
	 * cbim:requirementOf
     * cbim:requirement
	 */
	public static class CoinsRequirementValidator extends CoinsTwoWayRelationValidator {

		@Override
		protected String getTo() {
			return "cbim:requirementOf";
		}

		@Override
		protected String getToDescription() {
			return "Function";
		}

		@Override
		protected String getFrom() {
			return "cbim:requirement";
		}

		@Override
		protected String getFromDescription() {
			return "requirement";
		}
		
	}
	
	/**
	 * cbim:isSituatedIn
	 * cbim:situates
	 */
	public static class CoinsSituatedValidator extends CoinsTwoWayRelationValidator {

		@Override
		protected String getTo() {
			return "cbim:isSituatedIn";
		}

		@Override
		protected String getToDescription() {
			return "Physical object";
		}

		@Override
		protected String getFrom() {
			return "cbim:situates";
		}

		@Override
		protected String getFromDescription() {
			return "space";
		}
		
	}

	/**
	 * cbim:affects
	 * cbim:isAffectedBy
	 */
	public static class CoinsAffectsValidator extends CoinsTwoWayRelationValidator {

		@Override
		protected String getTo() {
			return "cbim:affects";
		}

		@Override
		protected String getToDescription() {
			return "Function fulfiller";
		}

		@Override
		protected String getFrom() {
			return "cbim:isAffectedBy";
		}

		@Override
		protected String getFromDescription() {
			return "task";
		}
		
	}
	
}
