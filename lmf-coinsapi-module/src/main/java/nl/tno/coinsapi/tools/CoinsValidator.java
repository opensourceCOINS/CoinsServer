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
				if (aspect!=ValidationAspect.ALL) {
					mValidators.add(CoinsValidatorFactory.getValidator(aspect, mContext, mSparqlService));
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
		
		private static final String HTTP_WWW_W3_ORG_2001_XML_SCHEMA_INT = "http://www.w3.org/2001/XMLSchema#int";
		private static final String HTTP_WWW_W3_ORG_2001_XML_SCHEMA_STRING = "http://www.w3.org/2001/XMLSchema#string";
		private static final String HTTP_WWW_W3_ORG_2001_XML_SCHEMA_DATE_TIME = "http://www.w3.org/2001/XMLSchema#dateTime";

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
			result.add(new ResourceRelation(CoinsApiService.CBIM_CREATOR, CoinsApiService.CBIM_PERSON_OR_ORGANISATION));
			result.add(new ResourceRelation(CoinsApiService.CBIM_MODIFIER, CoinsApiService.CBIM_PERSON_OR_ORGANISATION));
			result.add(new ResourceRelation(CoinsApiService.CBIM_SPATIAL_CHILD, CoinsApiService.CBIM_SPACE));
			result.add(new ResourceRelation(CoinsApiService.CBIM_SPATIAL_PARENT, CoinsApiService.CBIM_SPACE));
			result.add(new ResourceRelation(CoinsApiService.CBIM_PHYSICAL_PARENT, CoinsApiService.CBIM_PHYSICAL_OBJECT));
			result.add(new ResourceRelation(CoinsApiService.CBIM_PHYSICAL_CHILD, CoinsApiService.CBIM_PHYSICAL_OBJECT));
			result.add(new ResourceRelation(CoinsApiService.CBIM_IS_FULFILLED_BY, CoinsApiService.CBIM_FUNCTION_FULFILLER));
			result.add(new ResourceRelation(CoinsApiService.CBIM_FULFILLS, CoinsApiService.CBIM_FUNCTION));
			result.add(new ResourceRelation(CoinsApiService.CBIM_REQUIREMENT_OF, CoinsApiService.CBIM_FUNCTION));
			result.add(new ResourceRelation(CoinsApiService.CBIM_REQUIREMENT_RELATION, CoinsApiService.CBIM_REQUIREMENT));
			result.add(new ResourceRelation(CoinsApiService.CBIM_IS_SITUATED_IN, CoinsApiService.CBIM_SPACE));
			result.add(new ResourceRelation(CoinsApiService.CBIM_SITUATES, CoinsApiService.CBIM_PHYSICAL_OBJECT));
			result.add(new ResourceRelation(CoinsApiService.CBIM_AFFECTS, CoinsApiService.CBIM_FUNCTION_FULFILLER));
			result.add(new ResourceRelation(CoinsApiService.CBIM_IS_AFFECTED_BY, CoinsApiService.CBIM_TASK));
			result.add(new ResourceRelation(CoinsApiService.CBIM_AMOUNT, CoinsApiService.CBIM_CATALOGUE_PART));
			result.add(new ResourceRelation(CoinsApiService.CBIM_SHAPE, CoinsApiService.CBIM_EXPLICIT3D_REPRESENTATION));
			result.add(new ResourceRelation(CoinsApiService.CBIM_MAX_BOUNDING_BOX, CoinsApiService.CBIM_VECTOR));
			result.add(new ResourceRelation(CoinsApiService.CBIM_MIN_BOUNDING_BOX, CoinsApiService.CBIM_VECTOR));
			result.add(new ResourceRelation(CoinsApiService.CBIM_PRIMARY_ORIENTATION, CoinsApiService.CBIM_VECTOR));
			result.add(new ResourceRelation(CoinsApiService.CBIM_SECONDARY_ORIENTATION, CoinsApiService.CBIM_VECTOR));
			result.add(new ResourceRelation(CoinsApiService.CBIM_TRANSLATION, CoinsApiService.CBIM_VECTOR));
			result.add(new ResourceRelation(CoinsApiService.CBIM_LOCATOR_RELATION, CoinsApiService.CBIM_LOCATOR));
			result.add(new ResourceRelation(CoinsApiService.CBIM_FIRST_PARAMETER, CoinsApiService.CBIM_PARAMETER));
			result.add(new ResourceRelation(CoinsApiService.CBIM_NEXT_PARAMETER, CoinsApiService.CBIM_PARAMETER));
			result.add(new ResourceRelation(CoinsApiService.CBIM_DOCUMENT_URI, null));

			
			result.add(new LiteralRelation(CoinsApiService.CBIM_END_DATE_ACTUAL, HTTP_WWW_W3_ORG_2001_XML_SCHEMA_DATE_TIME));
			result.add(new LiteralRelation(CoinsApiService.CBIM_START_DATE, HTTP_WWW_W3_ORG_2001_XML_SCHEMA_DATE_TIME));
			result.add(new LiteralRelation(CoinsApiService.CBIM_START_DATE_ACTUAL, HTTP_WWW_W3_ORG_2001_XML_SCHEMA_DATE_TIME));
			result.add(new LiteralRelation(CoinsApiService.CBIM_START_DATE_PLANNED, HTTP_WWW_W3_ORG_2001_XML_SCHEMA_DATE_TIME));
			result.add(new LiteralRelation(CoinsApiService.CBIM_END_DATE, HTTP_WWW_W3_ORG_2001_XML_SCHEMA_DATE_TIME));
			result.add(new LiteralRelation(CoinsApiService.CBIM_END_DATE_PLANNED, HTTP_WWW_W3_ORG_2001_XML_SCHEMA_DATE_TIME));
			result.add(new LiteralRelation(CoinsApiService.CBIM_RELEASE_DATE, HTTP_WWW_W3_ORG_2001_XML_SCHEMA_DATE_TIME));
			result.add(new LiteralRelation(CoinsApiService.CBIM_CREATION_DATE, HTTP_WWW_W3_ORG_2001_XML_SCHEMA_DATE_TIME));
			result.add(new LiteralRelation(CoinsApiService.CBIM_MODIFICATION_DATE, HTTP_WWW_W3_ORG_2001_XML_SCHEMA_DATE_TIME));
			result.add(new LiteralRelation(CoinsApiService.CBIM_NAME, HTTP_WWW_W3_ORG_2001_XML_SCHEMA_STRING));
			result.add(new LiteralRelation(CoinsApiService.CBIM_DOCUMENT_TYPE, HTTP_WWW_W3_ORG_2001_XML_SCHEMA_STRING));
			result.add(new LiteralRelation(CoinsApiService.CBIM_DOCUMENT_ALIAS_FILE_PATH, HTTP_WWW_W3_ORG_2001_XML_SCHEMA_STRING));
			result.add(new LiteralRelation(CoinsApiService.CBIM_USER_ID, HTTP_WWW_W3_ORG_2001_XML_SCHEMA_STRING));
			result.add(new LiteralRelation(CoinsApiService.CBIM_LAYER_INDEX, HTTP_WWW_W3_ORG_2001_XML_SCHEMA_INT));
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
				if (mDataType.equals(CoinsApiService.CBIM_FUNCTION_FULFILLER)) {
					return new String[] {CoinsApiService.CBIM_PHYSICAL_OBJECT, CoinsApiService.CBIM_SPACE};
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
	
	protected static abstract class CoinsMaxCardinalityOneValidator extends CoinsValidator {

		protected abstract String getPrefix();

		protected abstract String getRelation();

		@Override
		public boolean validate() {
			boolean isOk = true;
			mErrors.clear();
			Set<String> children = new HashSet<String>();
			StringBuilder query = new StringBuilder();
			query.append("PREFIX ");
			query.append(getPrefix());
			query.append("\n\nSELECT ?child ?parent WHERE {\n\tGRAPH <");
			query.append(mContext);
			query.append("> {\n\t\t?child ");
			query.append(getRelation());
			query.append(" ?parent }}");
			List<Map<String, Value>> result = null;
			try {
				result = mSparqlService.query(QueryLanguage.SPARQL,
						query.toString());
			} catch (MarmottaException e) {
				e.printStackTrace();
				mErrors.add("MarmottaException");
				return false;
			}
			for (Map<String, Value> item : result) {
				String child = item.get("child").stringValue();
				if (!children.add(child)) {
					mErrors.add("Object <" + child
							+ "> has multiple relations of type "
							+ getRelation() + ".");
					isOk = false;
				}
			}
			return isOk;
		}

	}

	/**
	 * A requirement can be the requirement of at most one Function
	 */
	public static class CoinsRequirementOfValidator extends CoinsMaxCardinalityOneValidator {

		@Override
		protected String getPrefix() {
			return CoinsApiService.PREFIX_CBIM;
		}

		@Override
		protected String getRelation() {
			return CoinsApiService.CBIM_REQUIREMENT_OF;
		}
		
	}

	/**
	 * A requirement can be the super requirement of at most one requirement
	 */
	public static class CoinsSuperRequirement extends CoinsMaxCardinalityOneValidator {

		@Override
		protected String getPrefix() {
			return CoinsApiService.PREFIX_CBIMFS;
		}

		@Override
		protected String getRelation() {
			return CoinsApiService.CBIMFS_SUPER_REQUIREMENT;
		}
		
	}

	/**
	 * A Connection can have at most one male terminal
	 */
	public static class CoinsMaleTerminalValidator extends CoinsMaxCardinalityOneValidator {

		@Override
		protected String getPrefix() {
			return CoinsApiService.PREFIX_CBIM;
		}

		@Override
		protected String getRelation() {
			return CoinsApiService.CBIM_MALE_TERMINAL;
		}
		
	}

	/**
	 * A Connection can have at most one female terminal
	 */
	public static class CoinsFemaleTerminalValidator extends CoinsMaxCardinalityOneValidator {

		@Override
		protected String getPrefix() {
			return CoinsApiService.PREFIX_CBIM;
		}

		@Override
		protected String getRelation() {
			return CoinsApiService.CBIM_FEMALE_TERMINAL;
		}
		
	}

	/**
	 * A CatalogPart can have at most one super type
	 */
	public static class CoinsSupertypeValidator extends CoinsMaxCardinalityOneValidator {

		@Override
		protected String getPrefix() {
			return CoinsApiService.PREFIX_CBIM;
		}

		@Override
		protected String getRelation() {
			return CoinsApiService.CBIM_SUPER_TYPE;
		}
		
	}
	
	/**
	 * An amount can have at most one locator
	 */
	public static class CoinsLocatorValidator extends CoinsMaxCardinalityOneValidator {

		@Override
		protected String getPrefix() {
			return CoinsApiService.PREFIX_CBIM;
		}

		@Override
		protected String getRelation() {
			return CoinsApiService.CBIM_LOCATOR_RELATION;
		}
		
	}
	
	/**
	 * An Explicit3DRepresentation can only have one first parameter
	 */
	public static class CoinsFirstParamaterValidator extends CoinsMaxCardinalityOneValidator {

		@Override
		protected String getPrefix() {
			return CoinsApiService.PREFIX_CBIM;
		}

		@Override
		protected String getRelation() {
			return CoinsApiService.CBIM_FIRST_PARAMETER;
		}
		
	}

	/**
	 * A paramater can only have one next parameter
	 */
	public static class CoinsNextParamaterValidator extends CoinsMaxCardinalityOneValidator {

		@Override
		protected String getPrefix() {
			return CoinsApiService.PREFIX_CBIM;
		}

		@Override
		protected String getRelation() {
			return CoinsApiService.CBIM_NEXT_PARAMETER;
		}
		
	}

	/**
	 * Locator can have at most one max bounding box
	 */
	public static class CoinsMaxBoundingBoxValidator extends CoinsMaxCardinalityOneValidator {

		@Override
		protected String getPrefix() {
			return CoinsApiService.PREFIX_CBIM;
		}

		@Override
		protected String getRelation() {
			return CoinsApiService.CBIM_MAX_BOUNDING_BOX;
		}
		
	}

	/**
	 * Locator can have at most one min bounding box
	 */
	public static class CoinsMinBoundingBoxValidator extends CoinsMaxCardinalityOneValidator {

		@Override
		protected String getPrefix() {
			return CoinsApiService.PREFIX_CBIM;
		}

		@Override
		protected String getRelation() {
			return CoinsApiService.CBIM_MIN_BOUNDING_BOX;
		}
		
	}

	/**
	 * Primary orientation <= 1
	 */
	public static class CoinsPrimaryOrientationValidator extends CoinsMaxCardinalityOneValidator {

		@Override
		protected String getPrefix() {
			return CoinsApiService.PREFIX_CBIM;
		}

		@Override
		protected String getRelation() {
			return CoinsApiService.CBIM_PRIMARY_ORIENTATION;
		}
		
	}

	/**
	 * Secondary orientation <= 1
	 */
	public static class CoinsSecondaryOrientationValidator extends CoinsMaxCardinalityOneValidator {

		@Override
		protected String getPrefix() {
			return CoinsApiService.PREFIX_CBIM;
		}

		@Override
		protected String getRelation() {
			return CoinsApiService.CBIM_SECONDARY_ORIENTATION;
		}
		
	}

	/**
	 * Translation <= 1
	 */
	public static class CoinsTranslationValidator extends CoinsMaxCardinalityOneValidator {

		@Override
		protected String getPrefix() {
			return CoinsApiService.PREFIX_CBIM;
		}

		@Override
		protected String getRelation() {
			return CoinsApiService.CBIM_TRANSLATION;
		}
		
	}

	/**
	 * Only one or zero Physical parents allowed
	 * Physical Parent validator
	 */
	public static class CoinsPhysicalParentValidator extends CoinsMaxCardinalityOneValidator {

		@Override
		protected String getPrefix() {
			return CoinsApiService.PREFIX_CBIM;
		}

		@Override
		protected String getRelation() {
			return CoinsApiService.CBIM_PHYSICAL_PARENT;
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
			return CoinsApiService.CBIM_IS_FULFILLED_BY;
		}

		@Override
		protected String getToDescription() {
			return "Function";
		}

		@Override
		protected String getFrom() {
			return CoinsApiService.CBIM_FULFILLS;
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
			return CoinsApiService.CBIM_PHYSICAL_PARENT;
		}

		@Override
		protected String getToDescription() {
			return "Physical parent";
		}

		@Override
		protected String getFrom() {
			return CoinsApiService.CBIM_PHYSICAL_CHILD;
		}

		@Override
		protected String getFromDescription() {
			return "physical child";
		}
		
	}
	
	/**
	 * 	cbim:currentState
	 * 	cbim:stateOf
	 */
	public static class CoinsCurrentStateStateOfValidator extends CoinsTwoWayRelationValidator {

		@Override
		protected String getTo() {
			return CoinsApiService.CBIM_CURRENT_STATE;
		}

		@Override
		protected String getToDescription() {
			return "Current state";
		}

		@Override
		protected String getFrom() {
			return CoinsApiService.CBIM_STATE_OF;
		}

		@Override
		protected String getFromDescription() {
			return "state of";
		}
		
	}
	
	/**
	 * 	cbim:spatialChild
	 * 	cbim:spatialParent
	 */
	public static class CoinsSpaceParentChildValidator extends CoinsTwoWayRelationValidator {

		@Override
		protected String getTo() {
			return CoinsApiService.CBIM_SPATIAL_PARENT;
		}

		@Override
		protected String getToDescription() {
			return "spatial parent";
		}

		@Override
		protected String getFrom() {
			return CoinsApiService.CBIM_SPATIAL_CHILD;
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
			return CoinsApiService.CBIM_REQUIREMENT_OF;
		}

		@Override
		protected String getToDescription() {
			return "Function";
		}

		@Override
		protected String getFrom() {
			return CoinsApiService.CBIM_REQUIREMENT;
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
			return CoinsApiService.CBIM_IS_SITUATED_IN;
		}

		@Override
		protected String getToDescription() {
			return "Physical object";
		}

		@Override
		protected String getFrom() {
			return CoinsApiService.CBIM_SITUATES;
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
			return CoinsApiService.CBIM_AFFECTS;
		}

		@Override
		protected String getToDescription() {
			return "Function fulfiller";
		}

		@Override
		protected String getFrom() {
			return CoinsApiService.CBIM_IS_AFFECTED_BY;
		}

		@Override
		protected String getFromDescription() {
			return "task";
		}
		
	}
	
}
