package nl.tno.coinsapi.tools;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import nl.tno.coinsapi.CoinsPrefix;
import nl.tno.coinsapi.W3Schema;
import nl.tno.coinsapi.keys.CbimAttributeKey;
import nl.tno.coinsapi.keys.CbimObjectKey;
import nl.tno.coinsapi.keys.CbimfsAttributeKey;
import nl.tno.coinsapi.keys.CbimfsObjectKey;
import nl.tno.coinsapi.keys.CbimotlAttributeKey;
import nl.tno.coinsapi.keys.CbimotlObjectKey;
import nl.tno.coinsapi.keys.IAttributeKey;
import nl.tno.coinsapi.keys.IObjectKey;
import nl.tno.coinsapi.model.CoinsHierarchyFactory;

import org.apache.marmotta.platform.core.exception.MarmottaException;
import org.apache.marmotta.platform.sparql.api.sparql.SparqlService;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Value;
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
		
		@Override
		public boolean validate() {
			boolean isOk = true;
			mErrors.clear();
			StringBuilder queryBuilder = new StringBuilder();
			queryBuilder.append("PREFIX ");
			queryBuilder.append(CoinsPrefix.CBIM);
			queryBuilder.append("\nPREFIX ");
			queryBuilder.append(CoinsPrefix.CBIMFS);
			queryBuilder.append("\nPREFIX ");
			queryBuilder.append(CoinsPrefix.CBIMOTL);
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
			result.add(new ResourceRelation(CbimAttributeKey.CREATOR, CbimObjectKey.PERSON_OR_ORGANISATION));
			result.add(new ResourceRelation(CbimAttributeKey.MODIFIER, CbimObjectKey.PERSON_OR_ORGANISATION));
			result.add(new ResourceRelation(CbimAttributeKey.SPATIAL_CHILD, CbimObjectKey.SPACE));
			result.add(new ResourceRelation(CbimAttributeKey.SPATIAL_PARENT, CbimObjectKey.SPACE));
			result.add(new ResourceRelation(CbimAttributeKey.PHYSICAL_PARENT, CbimObjectKey.PHYSICAL_OBJECT));
			result.add(new ResourceRelation(CbimAttributeKey.PHYSICAL_CHILD, CbimObjectKey.PHYSICAL_OBJECT));
			result.add(new ResourceRelation(CbimAttributeKey.IS_FULFILLED_BY, CbimObjectKey.FUNCTION_FULFILLER));
			result.add(new ResourceRelation(CbimAttributeKey.FULFILLS, CbimObjectKey.FUNCTION));
			result.add(new ResourceRelation(CbimAttributeKey.REQUIREMENT_OF, CbimObjectKey.FUNCTION));
			result.add(new ResourceRelation(CbimAttributeKey.REQUIREMENT, CbimObjectKey.REQUIREMENT));
			result.add(new ResourceRelation(CbimAttributeKey.IS_SITUATED_IN, CbimObjectKey.SPACE));
			result.add(new ResourceRelation(CbimAttributeKey.SITUATES, CbimObjectKey.PHYSICAL_OBJECT));
			result.add(new ResourceRelation(CbimAttributeKey.AFFECTS, CbimObjectKey.FUNCTION_FULFILLER));
			result.add(new ResourceRelation(CbimAttributeKey.IS_AFFECTED_BY, CbimObjectKey.TASK));
			result.add(new ResourceRelation(CbimAttributeKey.AMOUNT, CbimObjectKey.CATALOGUE_PART));
			result.add(new ResourceRelation(CbimAttributeKey.SHAPE, CbimObjectKey.EXPLICIT3D_REPRESENTATION));
			result.add(new ResourceRelation(CbimAttributeKey.MAX_BOUNDING_BOX, CbimObjectKey.VECTOR));
			result.add(new ResourceRelation(CbimAttributeKey.MIN_BOUNDING_BOX, CbimObjectKey.VECTOR));
			result.add(new ResourceRelation(CbimAttributeKey.PRIMARY_ORIENTATION, CbimObjectKey.VECTOR));
			result.add(new ResourceRelation(CbimAttributeKey.SECONDARY_ORIENTATION, CbimObjectKey.VECTOR));
			result.add(new ResourceRelation(CbimAttributeKey.TRANSLATION, CbimObjectKey.VECTOR));
			result.add(new ResourceRelation(CbimAttributeKey.LOCATOR, CbimObjectKey.LOCATOR));
			result.add(new ResourceRelation(CbimAttributeKey.FIRST_PARAMETER, CbimObjectKey.PARAMETER));
			result.add(new ResourceRelation(CbimAttributeKey.NEXT_PARAMETER, CbimObjectKey.PARAMETER));
			result.add(new ResourceRelation(CbimAttributeKey.TERMINAL, CbimObjectKey.TERMINAL));
			result.add(new ResourceRelation(CbimAttributeKey.TERMINAL_OF, CbimObjectKey.FUNCTION_FULFILLER));
			result.add(new ResourceRelation(CbimAttributeKey.PERFORMANCE_RELATION, CbimObjectKey.PERFORMANCE));
			result.add(new ResourceRelation(CbimAttributeKey.PERFORMANCE_OF, CbimObjectKey.STATE, CbimObjectKey.FUNCTION_FULFILLER));
			result.add(new ResourceRelation(CbimfsAttributeKey.VERIFICATION_REQUIREMENT, CbimfsObjectKey.NON_FUNCTIONAL_REQUIREMENT));
			result.add(new ResourceRelation(CbimAttributeKey.VERIFICATION_REQUIREMENT, CbimObjectKey.REQUIREMENT));
			result.add(new ResourceRelation(CbimAttributeKey.VERIFICATION_PERFORMER, CbimObjectKey.PERSON_OR_ORGANISATION));
			result.add(new ResourceRelation(CbimAttributeKey.DOCUMENT, CbimObjectKey.DOCUMENT));
			result.add(new ResourceRelation(CbimfsAttributeKey.VERIFICATION_AUTHORIZED_BY, CbimObjectKey.PERSON_OR_ORGANISATION));
			result.add(new ResourceRelation(CbimfsAttributeKey.VERIFICATION_PLANNED_PERFORMER, CbimObjectKey.PERSON_OR_ORGANISATION));
			result.add(new ResourceRelation(CbimAttributeKey.VERIFICATION_FUNCTION_FULFILLER, CbimObjectKey.FUNCTION_FULFILLER));
			result.add(new ResourceRelation(CbimotlAttributeKey.FUNCTION_TYPE_REFERENCE, CbimotlObjectKey.FUNCTION_TYPE));
			result.add(new ResourceRelation(CbimotlAttributeKey.PERFORMANCE_TYPE_REFERENCE, CbimotlObjectKey.PERFORMANCE_TYPE));
			result.add(new ResourceRelation(CbimotlAttributeKey.REQUIREMENT_TYPE_REFERENCE, CbimotlObjectKey.REQUIREMENT_TYPE));
			result.add(new ResourceRelation(CbimotlAttributeKey.OBJECT_REFERENCE, CbimObjectKey.CBIM_OBJECT));
			//result.add(new ResourceRelation(CbimAttributeKey.DOCUMENT_URI));
			
			result.add(new LiteralRelation(CbimAttributeKey.END_DATE_ACTUAL, W3Schema.HTTP_WWW_W3_ORG_2001_XML_SCHEMA_DATE_TIME));
			result.add(new LiteralRelation(CbimAttributeKey.START_DATE, W3Schema.HTTP_WWW_W3_ORG_2001_XML_SCHEMA_DATE_TIME));
			result.add(new LiteralRelation(CbimAttributeKey.START_DATE_ACTUAL, W3Schema.HTTP_WWW_W3_ORG_2001_XML_SCHEMA_DATE_TIME));
			result.add(new LiteralRelation(CbimAttributeKey.START_DATE_PLANNED, W3Schema.HTTP_WWW_W3_ORG_2001_XML_SCHEMA_DATE_TIME));
			result.add(new LiteralRelation(CbimAttributeKey.END_DATE, W3Schema.HTTP_WWW_W3_ORG_2001_XML_SCHEMA_DATE_TIME));
			result.add(new LiteralRelation(CbimAttributeKey.END_DATE_PLANNED, W3Schema.HTTP_WWW_W3_ORG_2001_XML_SCHEMA_DATE_TIME));
			result.add(new LiteralRelation(CbimAttributeKey.RELEASE_DATE, W3Schema.HTTP_WWW_W3_ORG_2001_XML_SCHEMA_DATE_TIME));
			result.add(new LiteralRelation(CbimAttributeKey.CREATION_DATE, W3Schema.HTTP_WWW_W3_ORG_2001_XML_SCHEMA_DATE_TIME));
			result.add(new LiteralRelation(CbimAttributeKey.MODIFICATION_DATE, W3Schema.HTTP_WWW_W3_ORG_2001_XML_SCHEMA_DATE_TIME));
			result.add(new LiteralRelation(CbimAttributeKey.NAME, W3Schema.HTTP_WWW_W3_ORG_2001_XML_SCHEMA_STRING));
			result.add(new LiteralRelation(CbimAttributeKey.DOCUMENT_TYPE, W3Schema.HTTP_WWW_W3_ORG_2001_XML_SCHEMA_STRING));
			result.add(new LiteralRelation(CbimAttributeKey.DOCUMENT_ALIAS_FILE_PATH, W3Schema.HTTP_WWW_W3_ORG_2001_XML_SCHEMA_STRING));
			result.add(new LiteralRelation(CbimAttributeKey.USER_ID, W3Schema.HTTP_WWW_W3_ORG_2001_XML_SCHEMA_STRING));
			result.add(new LiteralRelation(CbimAttributeKey.LAYER_INDEX, W3Schema.HTTP_WWW_W3_ORG_2001_XML_SCHEMA_INT));
			result.add(new LiteralRelation(CbimAttributeKey.X_COORDINATE, W3Schema.HTTP_WWW_W3_ORG_2001_XML_SCHEMA_FLOAT));
			result.add(new LiteralRelation(CbimAttributeKey.Y_COORDINATE, W3Schema.HTTP_WWW_W3_ORG_2001_XML_SCHEMA_FLOAT));
			result.add(new LiteralRelation(CbimAttributeKey.Z_COORDINATE, W3Schema.HTTP_WWW_W3_ORG_2001_XML_SCHEMA_FLOAT));
			result.add(new LiteralRelation(CbimAttributeKey.VERIFICATION_DATE, W3Schema.HTTP_WWW_W3_ORG_2001_XML_SCHEMA_DATE_TIME));
			result.add(new LiteralRelation(CbimAttributeKey.VERIFICATION_METHOD, W3Schema.HTTP_WWW_W3_ORG_2001_XML_SCHEMA_STRING));
			result.add(new LiteralRelation(CbimAttributeKey.VERIFICATION_RESULT, W3Schema.HTTP_WWW_W3_ORG_2001_XML_SCHEMA_BOOLEAN));
			result.add(new LiteralRelation(CbimfsAttributeKey.AUTHORIZATION_DATE, W3Schema.HTTP_WWW_W3_ORG_2001_XML_SCHEMA_DATE_TIME));
			result.add(new LiteralRelation(CbimfsAttributeKey.AUTHORIZATION_DEFECTS, W3Schema.HTTP_WWW_W3_ORG_2001_XML_SCHEMA_STRING));
			result.add(new LiteralRelation(CbimfsAttributeKey.AUTHORIZATION_MEASURES, W3Schema.HTTP_WWW_W3_ORG_2001_XML_SCHEMA_STRING));
			result.add(new LiteralRelation(CbimfsAttributeKey.AUTHORIZATION_REMARKS, W3Schema.HTTP_WWW_W3_ORG_2001_XML_SCHEMA_STRING));
			result.add(new LiteralRelation(CbimfsAttributeKey.PLANNED_REMARKS, W3Schema.HTTP_WWW_W3_ORG_2001_XML_SCHEMA_STRING));
			result.add(new LiteralRelation(CbimfsAttributeKey.PLANNED_VERIFICATION_METHOD, W3Schema.HTTP_WWW_W3_ORG_2001_XML_SCHEMA_STRING));
			result.add(new LiteralRelation(CbimfsAttributeKey.PLANNED_VERIFICATION_DATE, W3Schema.HTTP_WWW_W3_ORG_2001_XML_SCHEMA_DATE_TIME));
			result.add(new LiteralRelation(CbimfsAttributeKey.VERIFICATION_RISKS, W3Schema.HTTP_WWW_W3_ORG_2001_XML_SCHEMA_STRING));
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

			private final IAttributeKey mRelation;
			
			public Relation(IAttributeKey pRelation) {
				mRelation = pRelation;
			}

			/**
			 * @return the relation
			 */
			public IAttributeKey getRelation() {
				return mRelation;
			}			
			
			public abstract String check(Value pObject, Value pValue);
		}
		
		private static class LiteralRelation extends Relation {

			private final W3Schema mDateType;

			public LiteralRelation(IAttributeKey pRelation, W3Schema pDateType) {
				super(pRelation);
				mDateType = pDateType;
			}

			@Override
			public String check(Value pObject, Value pValue) {
				if (pValue instanceof Literal) {
					Literal literal = (Literal)pValue;
					if (literal.getDatatype()==null) {
						return pObject.stringValue() + " : " + getRelation() + " has no data type (should be " + mDateType.toString() + ")";
					}
					if (!literal.getDatatype().equals(mDateType.toUri())) {
						return pObject.stringValue() + " : " + getRelation() + " has invalid data type (" + literal.getDatatype().stringValue() +" instead of " + mDateType.toString() + ")";
					}
				}
				else {
					return pValue.stringValue() + " : " + getRelation() + " should be literal";						
				}
				return null;
			}
			
		}
		
		private static class ResourceRelation extends Relation {

			private final IObjectKey[] mDataTypes;
			private String mContext = null;
			private SparqlService mSparqlService = null;
			
			public ResourceRelation(IAttributeKey pRelation, IObjectKey... pDataTypes) {
				super(pRelation);
				List<IObjectKey> dataTypeList = new Vector<IObjectKey>();
				for (IObjectKey dt : pDataTypes) {
					for (IObjectKey child : CoinsHierarchyFactory.getChildKeys(dt)){
						dataTypeList.add(child);
					}
					dataTypeList.add(dt);
				}
				mDataTypes = dataTypeList.toArray(new IObjectKey[dataTypeList.size()]);
			}

			private void setContext(String pContext) {
				mContext = pContext;
			}
			
			private void setSparqlService(SparqlService pSparqlService) {
				mSparqlService = pSparqlService;
			}
			
			private IObjectKey[] getDataTypes() {
				return mDataTypes;
			}
			
			@Override
			public String check(Value pObject, Value pValue) {
				if (pValue instanceof Resource) {
					if (mDataTypes==null) {
						// No check on reference
						return null;
					}
					StringBuilder query = new StringBuilder();
					query.append("PREFIX ");
					query.append(CoinsPrefix.CBIM);
					query.append("\nPREFIX ");
					query.append(CoinsPrefix.CBIMFS);
					query.append("\nPREFIX ");
					query.append(CoinsPrefix.CBIMOTL);
					query.append("\nSELECT (COUNT(?value) as ?counter) WHERE {\n\tGRAPH <");
					query.append(mContext);
					query.append("> {\n");
					boolean isFirst = true;
					for (IObjectKey dataType : getDataTypes()) {
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
							return pObject.stringValue() + " : " + getRelation() + " should refer to <" + mDataTypes + ">. No values of that type found.";
						}
					}

					return null;
				}
				return pObject.stringValue() + " : " + getRelation() + " should be resource";
			}
			
		}
	}
	
	protected static abstract class CoinsMaxCardinalityOneValidator extends CoinsValidator {

		protected abstract IAttributeKey getRelation();

		@Override
		public boolean validate() {
			boolean isOk = true;
			mErrors.clear();
			Set<String> children = new HashSet<String>();
			StringBuilder query = new StringBuilder();
			query.append("PREFIX ");
			query.append(getRelation().getPrefix());
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
		protected IAttributeKey getRelation() {
			return CbimAttributeKey.REQUIREMENT_OF;
		}
		
	}

	/**
	 * A requirement can be the super requirement of at most one requirement
	 */
	public static class CoinsSuperRequirement extends CoinsMaxCardinalityOneValidator {

		@Override
		protected IAttributeKey getRelation() {
			return CbimfsAttributeKey.SUPER_REQUIREMENT;
		}
		
	}

	/**
	 * A Connection can have at most one male terminal
	 */
	public static class CoinsMaleTerminalValidator extends CoinsMaxCardinalityOneValidator {

		@Override
		protected IAttributeKey getRelation() {
			return CbimAttributeKey.MALE_TERMINAL;
		}
		
	}

	/**
	 * A Connection can have at most one female terminal
	 */
	public static class CoinsFemaleTerminalValidator extends CoinsMaxCardinalityOneValidator {

		@Override
		protected IAttributeKey getRelation() {
			return CbimAttributeKey.FEMALE_TERMINAL;
		}
		
	}

	/**
	 * A CatalogPart can have at most one super type
	 */
	public static class CoinsSupertypeValidator extends CoinsMaxCardinalityOneValidator {

		@Override
		protected IAttributeKey getRelation() {
			return CbimAttributeKey.SUPER_TYPE;
		}
		
	}
	
	/**
	 * An amount can have at most one locator
	 */
	public static class CoinsLocatorValidator extends CoinsMaxCardinalityOneValidator {

		@Override
		protected IAttributeKey getRelation() {
			return CbimAttributeKey.LOCATOR;
		}
		
	}
	
	/**
	 * An Explicit3DRepresentation can only have one first parameter
	 */
	public static class CoinsFirstParamaterValidator extends CoinsMaxCardinalityOneValidator {

		@Override
		protected IAttributeKey getRelation() {
			return CbimAttributeKey.FIRST_PARAMETER;
		}
		
	}

	/**
	 * A paramater can only have one next parameter
	 */
	public static class CoinsNextParamaterValidator extends CoinsMaxCardinalityOneValidator {

		@Override
		protected IAttributeKey getRelation() {
			return CbimAttributeKey.NEXT_PARAMETER;
		}
		
	}

	/**
	 * Locator can have at most one max bounding box
	 */
	public static class CoinsMaxBoundingBoxValidator extends CoinsMaxCardinalityOneValidator {

		@Override
		protected IAttributeKey getRelation() {
			return CbimAttributeKey.MAX_BOUNDING_BOX;
		}
		
	}

	/**
	 * Locator can have at most one min bounding box
	 */
	public static class CoinsMinBoundingBoxValidator extends CoinsMaxCardinalityOneValidator {

		@Override
		protected IAttributeKey getRelation() {
			return CbimAttributeKey.MIN_BOUNDING_BOX;
		}
		
	}

	/**
	 * Primary orientation <= 1
	 */
	public static class CoinsPrimaryOrientationValidator extends CoinsMaxCardinalityOneValidator {

		@Override
		protected IAttributeKey getRelation() {
			return CbimAttributeKey.PRIMARY_ORIENTATION;
		}
		
	}

	/**
	 * Secondary orientation <= 1
	 */
	public static class CoinsSecondaryOrientationValidator extends CoinsMaxCardinalityOneValidator {

		@Override
		protected IAttributeKey getRelation() {
			return CbimAttributeKey.SECONDARY_ORIENTATION;
		}
		
	}

	/**
	 * Translation <= 1
	 */
	public static class CoinsTranslationValidator extends CoinsMaxCardinalityOneValidator {

		@Override
		protected IAttributeKey getRelation() {
			return CbimAttributeKey.TRANSLATION;
		}
		
	}

	/**
	 * Only one or zero Physical parents allowed
	 * Physical Parent validator
	 */
	public static class CoinsPhysicalParentValidator extends CoinsMaxCardinalityOneValidator {

		@Override
		protected IAttributeKey getRelation() {
			return CbimAttributeKey.PHYSICAL_PARENT;
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
			query.append(CoinsPrefix.CBIM);
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
			query.append(CoinsPrefix.CBIM);
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
		protected abstract IAttributeKey getTo();
		
		/**
		 * @return example: Function
		 */
		protected abstract String getToDescription();
		
		/**
		 * @return example: cbim:fulfills
		 */
		protected abstract IAttributeKey getFrom();
		
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
		protected IAttributeKey getTo() {
			return CbimAttributeKey.IS_FULFILLED_BY;
		}

		@Override
		protected String getToDescription() {
			return "Function";
		}

		@Override
		protected IAttributeKey getFrom() {
			return CbimAttributeKey.FULFILLS;
		}

		@Override
		protected String getFromDescription() {			
			return "function fulfiller";
		}
		
	}	
	
	/**
	 * Validator for cbim:terminal and cbim:terminalOf
	 */
	public static class CoinsTerminalValidator extends CoinsTwoWayRelationValidator {

		@Override
		protected IAttributeKey getTo() {
			return CbimAttributeKey.TERMINAL_OF;
		}

		@Override
		protected String getToDescription() {
			return "Terminal";
		}

		@Override
		protected IAttributeKey getFrom() {
			return CbimAttributeKey.TERMINAL;
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
		protected IAttributeKey getTo() {
			return CbimAttributeKey.PHYSICAL_PARENT;
		}

		@Override
		protected String getToDescription() {
			return "Physical parent";
		}

		@Override
		protected IAttributeKey getFrom() {
			return CbimAttributeKey.PHYSICAL_CHILD;
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
		protected IAttributeKey getTo() {
			return CbimAttributeKey.CURRENT_STATE;
		}

		@Override
		protected String getToDescription() {
			return "Current state";
		}

		@Override
		protected IAttributeKey getFrom() {
			return CbimAttributeKey.STATE_OF;
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
		protected IAttributeKey getTo() {
			return CbimAttributeKey.SPATIAL_PARENT;
		}

		@Override
		protected String getToDescription() {
			return "spatial parent";
		}

		@Override
		protected IAttributeKey getFrom() {
			return CbimAttributeKey.SPATIAL_CHILD;
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
		protected IAttributeKey getTo() {
			return CbimAttributeKey.REQUIREMENT_OF;
		}

		@Override
		protected String getToDescription() {
			return "Function";
		}

		@Override
		protected IAttributeKey getFrom() {
			return CbimAttributeKey.REQUIREMENT;
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
		protected IAttributeKey getTo() {
			return CbimAttributeKey.IS_SITUATED_IN;
		}

		@Override
		protected String getToDescription() {
			return "Physical object";
		}

		@Override
		protected IAttributeKey getFrom() {
			return CbimAttributeKey.SITUATES;
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
		protected IAttributeKey getTo() {
			return CbimAttributeKey.AFFECTS;
		}

		@Override
		protected String getToDescription() {
			return "Function fulfiller";
		}

		@Override
		protected IAttributeKey getFrom() {
			return CbimAttributeKey.IS_AFFECTED_BY;
		}

		@Override
		protected String getFromDescription() {
			return "task";
		}
		
	}
	
	/**
	 * cbim:performance
	 * cbim:performanceOf
	 */
	public static class CoinsPerformanceValidator extends CoinsTwoWayRelationValidator {

		@Override
		protected IAttributeKey getTo() {
			return CbimAttributeKey.PERFORMANCE_OF;
		}

		@Override
		protected String getToDescription() {
			return "State/Funcionfulfuller";
		}

		@Override
		protected IAttributeKey getFrom() {
			return CbimAttributeKey.PERFORMANCE_RELATION;
		}

		@Override
		protected String getFromDescription() {
			return "performance";
		}
		
	}

	/**
	 * cbim:baseline
	 * cbim:baselineObject
	 */
	public static class CoinsBaselineValidator extends CoinsTwoWayRelationValidator {

		@Override
		protected IAttributeKey getTo() {
			return CbimAttributeKey.BASELINE;
		}

		@Override
		protected String getToDescription() {
			return "Baseline";
		}

		@Override
		protected IAttributeKey getFrom() {
			return CbimAttributeKey.BASELINE_OBJECT;
		}

		@Override
		protected String getFromDescription() {
			return "CbimObject";
		}
		
	}

}
