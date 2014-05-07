package nz.ac.auckland.cer.project.validation;

import nz.ac.auckland.cer.project.pojo.ProjectRequest;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class ProjectRequestValidator implements Validator {

    @Override
    public boolean supports(
            Class<?> clazz) {

        return ProjectRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(
            Object projectRequest,
            Errors errors) {

        ProjectRequest pr = (ProjectRequest) projectRequest;
        
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "projectTitle", "project.title.required");
        if (!errors.hasFieldErrors("projectTitle") && pr.getProjectTitle().trim().length() > 160) {
            errors.rejectValue("projectTitle", "project.title.length.limits");
        }
        
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "projectDescription", "project.description.required");
        if (!errors.hasFieldErrors("projectDescription")) {
            Integer descriptionLength = pr.getProjectDescription().trim().length();
            if (descriptionLength < 100 || descriptionLength > 2500) {
                errors.rejectValue("projectDescription", "project.description.length.limits");
            }            
        }
        
        // validate superviser information
        if (pr.getAskForSuperviser() != null && pr.getAskForSuperviser()) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "superviserId", "project.superviser.name.required");
            if (!errors.hasFieldErrors("superviserId")) {
                if (pr.getSuperviserId() == -2) {
                    // "Please Select"
                    errors.rejectValue("superviserId", "project.superviser.name.required");
                } else if (pr.getSuperviserId() == -1) {
                    // "Other"
                    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "superviserName", "project.superviser.name.required");
                    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "superviserPhone", "project.superviser.phone.required");
                    EmailValidator ev = EmailValidator.getInstance(false);
                    if (!ev.isValid(pr.getSuperviserEmail())) {
                        errors.rejectValue("superviserEmail", "project.superviser.email.invalid");
                    }
                    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "superviserAffiliation", "project.superviser.affiliation.required");
                    if (!errors.hasFieldErrors("superviserAffiliation") && pr.getSuperviserAffiliation().equals("OTHER")) {
                        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "superviserOtherAffiliation", "project.superviser.affiliation.required");
                    }
                }                
            }
        }

        // validate motivation
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "motivation", "project.motivation.required");
        if (!errors.hasFieldErrors("motivation") && pr.getMotivation().startsWith("__OTHER__")) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "otherMotivation", "project.motivation.required");
            if (!errors.hasFieldErrors("otherMotivation")) {
                pr.setMotivation(pr.getOtherMotivation());
            }
        }

        // validate current computational environment
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "currentEnv", "project.currentCompEnv.required");
        if (!errors.hasFieldErrors("currentEnv") && pr.getCurrentEnv().equals("cluster_or_set_of_computers")) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "limitations.cpuCores", "project.currentCompEnv.cpucores.required");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "limitations.memory", "project.currentCompEnv.memory.required");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "limitations.concurrency", "project.currentCompEnv.concurrrency.required");
        }
    }

}
