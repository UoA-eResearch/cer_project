package nz.ac.auckland.cer.project.validation;

import nz.ac.auckland.cer.common.db.project.pojo.ResearchOutput;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class ResearchOutputValidator implements Validator {

    @Override
    public boolean supports(
            Class<?> clazz) {

        return ResearchOutput.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(
            Object researchOutput,
            Errors errors) {

        ResearchOutput ro = (ResearchOutput) researchOutput;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "typeId", "project.researchOutput.type.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "project.researchOutput.description.required");
        if (!errors.hasFieldErrors("typeId") && ro.getTypeId() < 0) {
            errors.rejectValue("typeId", "project.researchOutput.type.required");            
        }
        if (ro.getProjectId() == null) {
            errors.rejectValue("projectId", "project.id.required");
        }
    }

}
