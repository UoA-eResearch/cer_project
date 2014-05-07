package nz.ac.auckland.cer.project.validation;

import nz.ac.auckland.cer.project.pojo.MembershipRequest;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class MembershipRequestValidator implements Validator {

    @Override
    public boolean supports(
            Class<?> clazz) {

        return MembershipRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(
            Object membershipRequest,
            Errors errors) {

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "projectCode", "project.code.required");
    }

}
