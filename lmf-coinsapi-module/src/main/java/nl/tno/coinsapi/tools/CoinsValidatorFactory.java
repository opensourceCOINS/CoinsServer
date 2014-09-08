package nl.tno.coinsapi.tools;

import nl.tno.coinsapi.services.ICoinsApiService.ValidationAspect;
import nl.tno.coinsapi.tools.CoinsValidator.CoinsAffectsValidator;
import nl.tno.coinsapi.tools.CoinsValidator.CoinsAllValidator;
import nl.tno.coinsapi.tools.CoinsValidator.CoinsFemaleTerminalValidator;
import nl.tno.coinsapi.tools.CoinsValidator.CoinsFirstParamaterValidator;
import nl.tno.coinsapi.tools.CoinsValidator.CoinsFunctionFulfillerValidator;
import nl.tno.coinsapi.tools.CoinsValidator.CoinsLiteralValidator;
import nl.tno.coinsapi.tools.CoinsValidator.CoinsLocatorValidator;
import nl.tno.coinsapi.tools.CoinsValidator.CoinsMaleTerminalValidator;
import nl.tno.coinsapi.tools.CoinsValidator.CoinsMaxBoundingBoxValidator;
import nl.tno.coinsapi.tools.CoinsValidator.CoinsMinBoundingBoxValidator;
import nl.tno.coinsapi.tools.CoinsValidator.CoinsNextParamaterValidator;
import nl.tno.coinsapi.tools.CoinsValidator.CoinsPhysicalObjectParentChildValidator;
import nl.tno.coinsapi.tools.CoinsValidator.CoinsPhysicalParentValidator;
import nl.tno.coinsapi.tools.CoinsValidator.CoinsPrimaryOrientationValidator;
import nl.tno.coinsapi.tools.CoinsValidator.CoinsRequirementOfValidator;
import nl.tno.coinsapi.tools.CoinsValidator.CoinsRequirementValidator;
import nl.tno.coinsapi.tools.CoinsValidator.CoinsSecondaryOrientationValidator;
import nl.tno.coinsapi.tools.CoinsValidator.CoinsSituatedValidator;
import nl.tno.coinsapi.tools.CoinsValidator.CoinsSpaceParentChildValidator;
import nl.tno.coinsapi.tools.CoinsValidator.CoinsSuperRequirement;
import nl.tno.coinsapi.tools.CoinsValidator.CoinsSupertypeValidator;
import nl.tno.coinsapi.tools.CoinsValidator.CoinsTranslationValidator;

import org.apache.marmotta.platform.sparql.api.sparql.SparqlService;

/**
 * Validator for coins validators
 */
public class CoinsValidatorFactory {

	/**
	 * @param pAspect
	 * @param pContext
	 * @param pSparqlService
	 * @return a validator for this aspect
	 */
	public static CoinsValidator getValidator(ValidationAspect pAspect, String pContext, SparqlService pSparqlService) {
		CoinsValidator validator = null;
		switch (pAspect) {
		case ALL:
			validator = new CoinsAllValidator();
			break;
		case MAXBOUNDINGBOX:
			validator = new CoinsMaxBoundingBoxValidator();
			break;
		case MINBOUNDINGBOX:
			validator = new CoinsMinBoundingBoxValidator();
			break;
		case PRIMARYORIENATION:
			validator = new CoinsPrimaryOrientationValidator();
			break;
		case SECONDARYORIENTATION:
			validator = new CoinsSecondaryOrientationValidator();
			break;
		case TRANSLATION:
			validator = new CoinsTranslationValidator();
			break;
		case MALETERMINAL:
			validator = new CoinsMaleTerminalValidator();
			break;
		case FEMALETERMINAL:
			validator = new CoinsFemaleTerminalValidator();
			break;
		case SUPERTYPE:
			validator = new CoinsSupertypeValidator();
			break;
		case PHYSICALPARENT:
			validator = new CoinsPhysicalParentValidator();
			break;
		case FUNCTIONFULFILLERS:
			validator = new CoinsFunctionFulfillerValidator();
			break;
		case LITERALS:
			validator = new CoinsLiteralValidator();
			break;
		case PHYSICALOBJECT_PARENT_CHILD:
			validator = new CoinsPhysicalObjectParentChildValidator();
			break;
		case FIRST_PARAMETER:
			validator = new CoinsFirstParamaterValidator();
			break;
		case LOCATOR:
			validator = new CoinsLocatorValidator();
			break;
		case SPACE_PARENT_CHILD:
			validator = new CoinsSpaceParentChildValidator();
			break;
		case AFFECTS:
			validator = new CoinsAffectsValidator();
			break;
		case SITUATES:
			validator = new CoinsSituatedValidator();
			break;
		case REQUIREMENT:
			validator = new CoinsRequirementValidator();
			break;
		case NEXTPARAMETER:
			validator = new CoinsNextParamaterValidator();
			break;
		case REQUIREMENT_OF:
			validator = new CoinsRequirementOfValidator();
			break;
		case SUPER_REQUIREMENT:
			validator = new CoinsSuperRequirement();
			break;
		}
		validator.setContext(pContext);
		validator.setSparqlService(pSparqlService);
		return validator;
	}

}
