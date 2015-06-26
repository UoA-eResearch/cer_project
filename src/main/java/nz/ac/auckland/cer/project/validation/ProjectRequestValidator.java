package nz.ac.auckland.cer.project.validation;

import nz.ac.auckland.cer.common.db.project.pojo.ProjectRequest;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class ProjectRequestValidator implements Validator {

    public boolean supports(
            Class<?> clazz) {

        return ProjectRequest.class.isAssignableFrom(clazz);
    }

    public void validate(
            Object projectRequest,
            Errors errors) {

        ProjectRequest pr = (ProjectRequest) projectRequest;
        
        // validate project title
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "projectTitle", "project.title.required");
        if (!errors.hasFieldErrors("projectTitle") && pr.getProjectTitle().trim().length() > 160) {
            errors.rejectValue("projectTitle", "project.title.length.limits");
        }
        
        // validate project description
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "projectDescription", "project.description.required");
        if (!errors.hasFieldErrors("projectDescription")) {
            Integer descriptionLength = pr.getProjectDescription().trim().length();
            if (descriptionLength < 500 || descriptionLength > 2500) {
                errors.rejectValue("projectDescription", "project.description.length.limits");
            }            
        }
        
        // validate field of science
        Integer scienceStudyId = null;
        try {
            scienceStudyId = Integer.valueOf(pr.getScienceStudyId());
            if (scienceStudyId < 0) {
                errors.rejectValue("scienceStudyId", "project.sciencestudyid.required");
            } 
        } catch (Exception e) {
            errors.rejectValue("scienceStudyId", "project.sciencestudyid.required");
        }
        
        // validate superviser information
        if (pr.getAskForSuperviser() != null && pr.getAskForSuperviser()) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "superviserId", "project.superviser.name.required");
            if (!errors.hasFieldErrors("superviserId")) {
                if (pr.getSuperviserId() == -2) {
                    // "Please select"
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
                    if (!errors.hasFieldErrors("superviserAffiliation") && pr.getSuperviserAffiliation().toLowerCase().equals("other")) {
                        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "superviserOtherInstitution", "project.superviser.affiliation.required");
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
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "currentCompEnv", "project.currentCompEnv.required");
        if (!errors.hasFieldErrors("currentCompEnv")) {
            if (pr.getCurrentCompEnv().equals("OTHER")) {
                ValidationUtils.rejectIfEmptyOrWhitespace(errors, "otherCompEnv", "project.currentCompEnv.required");
            }
            if (!pr.getCurrentCompEnv().equals("standard_computer")) {
                ValidationUtils.rejectIfEmptyOrWhitespace(errors, "limitations.cpuCores", "project.currentCompEnv.cpucores.required");
                ValidationUtils.rejectIfEmptyOrWhitespace(errors, "limitations.memory", "project.currentCompEnv.memory.required");
                ValidationUtils.rejectIfEmptyOrWhitespace(errors, "limitations.concurrency", "project.currentCompEnv.concurrrency.required");
            }
        }
        
        // TODO: validate funding
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "funded", "project.funded.required");
        if (!errors.hasFieldErrors("funded") && pr.getFunded()) {
        	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "fundingSource", "project.fundingSource.required");
        }
    }

}
