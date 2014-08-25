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
				switch (aspect) {
				case ALL:
					break;
				case FUNCTIONFULFILLERS:
					mValidators.add(new CoinsFunctionFulfillerValidator());
					break;
				case PHYSICALPARENT:
					mValidators.add(new CoinsPhysicalParentValidator());
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

	/**
	 * Validator for validating function fulfillers
	 */
	public static class CoinsFunctionFulfillerValidator extends CoinsValidator {

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
			query.append("> {\n\t\t?function cbim:isFulfilledBy ?fulfiller }}");
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
			query.append("> {\n\t\t?fulfiller cbim:fulfills ?function }}");
			try {
				result = mSparqlService.query(QueryLanguage.SPARQL, query.toString());
				for (Map<String, Value> item : result) {
					String couple = item.get("function").toString() + " - " + item.get("fulfiller").toString();
					if (!couples.add(couple)) {						
						isOk=false;
						mErrors.add("Function <" +item.get("function").toString() + "> is linked to function fulfiller <" + item.get("fulfiller") + "> and vice versa (both fulfulls and isFulfilledBy)");
					}
				}
			} catch (MarmottaException e) {
				e.printStackTrace();
				mErrors.add("MarmottaException");
				return false;
			}
			
			return isOk;
		}
		
	}
}
