package nz.ac.auckland.cer.project.validation;

import java.util.LinkedList;
import java.util.List;

import nz.ac.auckland.cer.project.pojo.ResearchOutput;
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
        if (survey.getResearchOutcome().getAddResearchOutputRow() == 0) {
            this.validatePerformanceSection(survey, surveyObject, errors);
            this.validateResearchOutcomeSection(survey, surveyObject, errors);
        }
    }

    private void validateResearchOutcomeSection(
            Survey survey,
            Object surveyObject,
            Errors errors) {

        Integer noResearchOutput = survey.getResearchOutcome().getNoResearchOutput();
        List<ResearchOutput> ros = survey.getResearchOutcome().getResearchOutputs();
        List<ResearchOutput> tmpOutput = new LinkedList<ResearchOutput>();
        for (ResearchOutput tmp : ros) {
            if (tmp.getTypeId() > -1 || (tmp.getDescription() != null && tmp.getDescription().trim().length() > 0)) {
                tmpOutput.add(tmp);
            }
        }

        if (noResearchOutput == null || noResearchOutput == 0) {
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
                        this.validatePerformanceFaster(survey, errors);
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
    private void validatePerformanceFaster(
            Survey survey,
            Errors errors) {

        this.checkFloat("faster.factor", survey.getFaster().getFactor(),
                "project.survey.improvements.faster.factor.required", "project.survey.improvements.faster.factor.nan",
                errors);
        String otherReason = survey.getFaster().getOther();
        if (otherReason == null || otherReason.trim().length() == 0) {
            ValidationUtils.rejectIfEmpty(errors, "faster.reasons",
                    "project.survey.improvements.faster.reason.required");
        }
    }

    /*
     * Validate the "Bigger" subsection of the Performance section of the survey
     */
    private void validatePerformanceBigger(
            Survey survey,
            Errors errors) {

        this.checkFloat("bigger.factor", survey.getBigger().getFactor(),
                "project.survey.improvements.bigger.factor.required", "project.survey.improvements.bigger.factor.nan",
                errors);
        String otherReason = survey.getBigger().getOther();
        if (otherReason == null || otherReason.trim().length() == 0) {
            ValidationUtils.rejectIfEmpty(errors, "bigger.reasons",
                    "project.survey.improvements.bigger.reason.required");
        }
    }

    /*
     * Validate the "More" subsection of the Performance section of the survey
     */
    private void validatePerformanceMore(
            Survey survey,
            Errors errors) {

        this.checkInteger("more.number", survey.getMore().getNumber(),
                "project.survey.improvements.more.number.required", "project.survey.improvements.more.number.nan",
                errors);
        this.checkFloat("more.factor", survey.getMore().getFactor(),
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
