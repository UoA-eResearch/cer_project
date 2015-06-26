package nz.ac.auckland.cer.project.validation;

import nz.ac.auckland.cer.common.db.project.pojo.FollowUp;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class FollowUpValidator implements Validator {

    @Override
    public boolean supports(
            Class<?> clazz) {

        return FollowUp.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(
            Object followUp,
            Errors errors) {

        FollowUp fu = (FollowUp) followUp;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "notes", "project.followup.notes.required");
        if (fu.getProjectId() == null) {
            errors.rejectValue("projectId", "project.id.required");
        }
    }

}
