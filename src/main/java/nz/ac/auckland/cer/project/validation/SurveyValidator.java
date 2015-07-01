package nz.ac.auckland.cer.project.validation;

import java.util.LinkedList;
import java.util.List;

import nz.ac.auckland.cer.common.db.project.pojo.ResearchOutput;
import nz.ac.auckland.cer.project.pojo.survey.PerfImpBigger;
import nz.ac.auckland.cer.project.pojo.survey.PerfImpFaster;
import nz.ac.auckland.cer.project.pojo.survey.ResearchOutcome;
import nz.ac.auckland.cer.project.pojo.survey.Survey;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class SurveyValidator implements Validator {

    @Override
    public boolean supports(
            Class<?> clazz) {

        return Survey.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(
            Object surveyObject,
            Errors errors) {

        Survey survey = (Survey) surveyObject;
        if (survey.getAddResearchOutputRow() == 0) {
            this.validatePerformanceSection(survey, surveyObject, errors);
            this.validateResearchOutcomeSection(survey, surveyObject, errors);
            this.validateYourViewsSection(survey, surveyObject, errors);
        }
    }

    private void validateResearchOutcomeSection(
            Survey survey,
            Object surveyObject,
            Errors errors) {
    	
    	ResearchOutcome ro = survey.getResearchOutcome();
        if (ro == null) {
        	errors.rejectValue("researchOutcome.researchOutputs", "project.survey.researchoutcome.required");
        } else {
            Boolean hasNoResearchOutput = ro.getHasNoResearchOutput();
            List<ResearchOutput> ros = ro.getResearchOutputs();
            List<ResearchOutput> tmpOutput = new LinkedList<ResearchOutput>();
            for (ResearchOutput tmp : ros) {
                if (tmp.getTypeId() > -1 || (tmp.getDescription() != null && tmp.getDescription().trim().length() > 0)) {
                    tmpOutput.add(tmp);
                }
            }

            if (hasNoResearchOutput == null || !hasNoResearchOutput) {
                if (ros == null || ros.size() == 0 || tmpOutput.size() == 0) {
                    errors.rejectValue("researchOutcome.researchOutputs", "project.survey.researchoutcome.required");
                } else {
                    for (ResearchOutput tmp2 : tmpOutput) {
                        if (tmp2.getTypeId() < 0) {
                            errors.rejectValue("researchOutcome.researchOutputs",
                                    "project.survey.researchoutcome.typemissing");
                        } else if (tmp2.getDescription() == null || tmp2.getDescription().trim().length() == 0) {
                            errors.rejectValue("researchOutcome.researchOutputs",
                                    "project.survey.researchoutcome.descmissing");
                        }
                    }
                }
            } else {
                if (tmpOutput.size() > 0) {
                    errors.rejectValue("researchOutcome.researchOutputs",
                            "project.survey.researchoutcome.ambiguous");
                }
            }        	
        }
    }

    /*
     * Validate the "More" subsection of the Performance section of the survey
     */
    private void validateYourViewsSection(
            Survey survey,
            Object surveyObject,
            Errors errors) {

    	String tmp1 = survey.getYourViews().getRecommendChoice();
    	String tmp2 = survey.getYourViews().getMeetNeedChoice();
    	String tmp3 = survey.getYourViews().getAdequateSupportChoice();
    	if (tmp1 == null || tmp1.trim().isEmpty() ||
    		tmp2 == null || tmp2.trim().isEmpty() ||
    		tmp3 == null || tmp3.trim().isEmpty()) {
    		errors.rejectValue("yourViews.recommendChoice", "project.survey.yourviews.required");    	
    	}
    }


    private void validatePerformanceSection(
            Survey survey,
            Object surveyObject,
            Errors errors) {

        ValidationUtils.rejectIfEmpty(errors, "improvements", "project.survey.improvements.required");
        if (!errors.hasFieldErrors("improvements")) {
            List<String> improvs = survey.getImprovements();
            if (improvs.contains("same") && improvs.size() > 1) {
                errors.rejectValue("improvements", "project.survey.improvements.incompatible");
            } else {
                for (String improv : survey.getImprovements()) {
                    if (improv.equals("faster")) {
                        this.validatePerfImpFaster(survey, errors);
                    } else if (improv.equals("bigger")) {
                        this.validatePerformanceBigger(survey, errors);
                    } else if (improv.equals("more")) {
                        this.validatePerformanceMore(survey, errors);
                    }
                }
            }
        }
    }

    /*
     * Validate the "Faster" subsection of the Performance section of the survey
     */
    private void validatePerfImpFaster(
            Survey survey,
            Errors errors) {

    	PerfImpFaster f = survey.getPerfImpFaster();
    	if (f.getFactor().trim().isEmpty()) {
    		errors.rejectValue("perfImpFaster.factor", "project.survey.improvements.faster.factor.required");
    	} else {
            this.checkFloat("perfImpFaster.factor", f.getFactor(),
                "project.survey.improvements.faster.factor.required", "project.survey.improvements.faster.factor.nan",
                errors);    		
    	}
        if (!f.hasOptions()) {
        	errors.rejectValue("perfImpFaster.otherReason", "project.survey.improvements.faster.option.required");
        }
    }

    /*
     * Validate the "Bigger" subsection of the Performance section of the survey
     */
    private void validatePerformanceBigger(
            Survey survey,
            Errors errors) {

    	PerfImpBigger b = survey.getPerfImpBigger();
        this.checkFloat("perfImpBigger.factor", b.getFactor(),
        		"project.survey.improvements.bigger.factor.required", "project.survey.improvements.bigger.factor.nan",
                errors);
        if (!b.hasOptions()) {
        	errors.rejectValue("perfImpBigger.otherReason", "project.survey.improvements.bigger.option.required");
        }
    }

    /*
     * Validate the "More" subsection of the Performance section of the survey
     */
    private void validatePerformanceMore(
            Survey survey,
            Errors errors) {

        this.checkInteger("perfImpMore.number", survey.getPerfImpMore().getNumber(),
                "project.survey.improvements.more.number.required", "project.survey.improvements.more.number.nan",
                errors);
        this.checkFloat("perfImpMore.factor", survey.getPerfImpMore().getFactor(),
                "project.survey.improvements.more.factor.required", "project.survey.improvements.more.factor.nan",
                errors);
    }

    private void checkInteger(
            String fieldName,
            String number,
            String requiredProp,
            String nanProp,
            Errors errors) {

        ValidationUtils.rejectIfEmpty(errors, fieldName, requiredProp);
        if (!errors.hasFieldErrors(fieldName)) {
            try {
                Integer.parseInt(number);
            } catch (NumberFormatException e) {
                errors.rejectValue(fieldName, nanProp);
            }
        }
    }

    private void checkFloat(
            String fieldName,
            String number,
            String requiredProp,
            String nanProp,
            Errors errors) {

        ValidationUtils.rejectIfEmpty(errors, fieldName, requiredProp);
        if (!errors.hasFieldErrors(fieldName)) {
            try {
                Float.parseFloat(number);
            } catch (NumberFormatException e) {
                errors.rejectValue(fieldName, nanProp);
            }
        }
    }

}
