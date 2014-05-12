package nz.ac.auckland.cer.project.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import nz.ac.auckland.cer.project.dao.ProjectDatabaseDao;
import nz.ac.auckland.cer.project.pojo.MembershipRequest;
import nz.ac.auckland.cer.project.pojo.Project;
import nz.ac.auckland.cer.project.pojo.RPLink;
import nz.ac.auckland.cer.project.util.EmailUtil;
import nz.ac.auckland.cer.project.util.Person;
import nz.ac.auckland.cer.project.validation.MembershipRequestValidator;

@Controller
public class MembershipRequestController {

    private Logger log = Logger.getLogger(ProjectRequestController.class.getName());
    @Autowired private ProjectDatabaseDao projectDao;
    @Autowired private EmailUtil emailUtil;

    @RequestMapping(value = "request_membership", method = RequestMethod.GET)
    public String showMembershipRequestForm(
            Model m,
            HttpServletRequest request) throws Exception {

        MembershipRequest mr = new MembershipRequest();
        Person person = (Person) request.getAttribute("person");
        if (person == null) {
            return "redirect:/ceraccount/html/request_account_info";
        } else if (!person.isResearcher()) {
            m.addAttribute("error_message", "Only researchers can request membership to projects."
                    + " You appear to be an adviser.");
            return "request_membership_response";
        }
        m.addAttribute("membershiprequest", mr);
        return "request_membership";
    }

    @RequestMapping(value = "request_membership", method = RequestMethod.POST)
    public String processMembershipRequestForm(
            Model m,
            @Valid @ModelAttribute("membershiprequest") MembershipRequest mr,
            BindingResult bResult,
            HttpServletRequest request) throws Exception {

        if (bResult.hasErrors()) {
            m.addAttribute("membershiprequest", mr);
            return "request_membership";
        }
        try {
            Person person = (Person) request.getAttribute("person");
            if (person == null) {
                return "redirect:/ceraccount/html/request_account_info";
            } else if (!person.isResearcher()) {
                m.addAttribute("error_message", "Only researchers can request membership to projects."
                        + " You appear to be an adviser.");
                return "request_membership_response";
            }
            Project p = projectDao.getProjectForCode(mr.getProjectCode());
            this.addResearcherToProject(person.getId(), p);
            this.emailUtil.sendMembershipRequestRequestEmail(p, person.getFullName());
            return "request_membership_response";
        } catch (Exception e) {
            log.error("Failed to create project membership request", e);
            bResult.addError(new ObjectError(bResult.getObjectName(), e.getMessage()));
            return "request_membership";
        }
    }

    private void addResearcherToProject(
            Integer researcherDatabaseId,
            Project p) throws Exception {

        try {
            // TODO: configure status pending rather than hard-coding it with 5
            RPLink rpl = new RPLink(p.getId(), researcherDatabaseId, 5);
            this.projectDao.addResearcherToProject(rpl);
        } catch (Exception e) {
            log.error("Failed to add researcher to project.", e);
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Configure validator for cluster project and membership request form
     */
    @InitBinder
    protected void initBinder(
            WebDataBinder binder) {

        binder.addValidators(new MembershipRequestValidator());
    }

}
