package nz.ac.auckland.cer.project.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import nz.ac.auckland.cer.project.dao.ProjectDatabaseDao;
import nz.ac.auckland.cer.project.pojo.MembershipRequest;
import nz.ac.auckland.cer.project.pojo.Project;
import nz.ac.auckland.cer.project.pojo.ProjectWrapper;
import nz.ac.auckland.cer.project.pojo.RPLink;
import nz.ac.auckland.cer.project.util.EmailUtil;
import nz.ac.auckland.cer.project.util.Person;
import nz.ac.auckland.cer.project.validation.MembershipRequestValidator;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class MembershipRequestController {

    private final String adviserWarning = "In our books you are an adviser but not a researcher. Only researchers may use this tool.";
    @Autowired private EmailUtil emailUtil;
    private final Logger log = Logger.getLogger(ProjectRequestController.class.getName());
    @Autowired private ProjectDatabaseDao projectDao;
    private String redirectIfNoAccount;

    private void addResearcherToProject(
            Integer researcherDatabaseId,
            Project p) throws Exception {

        try {
            // TODO: configure status pending rather than hard-coding it with 5
            RPLink rpl = new RPLink();
            rpl.setProjectId(p.getId());
            rpl.setResearcherId(researcherDatabaseId);
            rpl.setResearcherRoleId(5);
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

    @RequestMapping(value = "request_membership", method = RequestMethod.POST)
    public ModelAndView processMembershipRequestForm(
            @Valid @ModelAttribute("membershiprequest") MembershipRequest mr,
            BindingResult bResult,
            HttpServletRequest request) throws Exception {

        ModelAndView mav = new ModelAndView("request_membership");
        if (bResult.hasErrors()) {
            mav.addObject("membershiprequest", mr);
            return mav;
        }
        try {
            Person person = (Person) request.getAttribute("person");
            if (person == null) {
                return new ModelAndView(new RedirectView(redirectIfNoAccount, false));
            } else if (!person.isResearcher()) {
                Map<String, Object> m = new HashMap<String, Object>();
                m.put("error_message", adviserWarning);
                return new ModelAndView("request_membership_response", m);
            }
            ProjectWrapper pw = projectDao.getProjectForIdOrCode(mr.getProjectCode());
            this.addResearcherToProject(person.getId(), pw.getProject());
            // TODO: send e-mail to zendesk, cc = superviser, replyto = zendesk
            //pw.getRpLinks();
            this.emailUtil.sendMembershipRequestRequestEmail(pw, person.getFullName(), person.getEmail());
            return new ModelAndView("request_membership_response");
        } catch (Exception e) {
            log.error("Failed to create project membership request", e);
            bResult.addError(new ObjectError(bResult.getObjectName(), e.getMessage()));
            return new ModelAndView("request_membership");
        }
    }

    public void setRedirectIfNoAccount(
            String redirectIfNoAccount) {

        this.redirectIfNoAccount = redirectIfNoAccount;
    }

    @RequestMapping(value = "request_membership", method = RequestMethod.GET)
    public ModelAndView showMembershipRequestForm(
            HttpServletRequest request) throws Exception {

        ModelAndView mav = new ModelAndView("request_membership");
        MembershipRequest mr = new MembershipRequest();
        Person person = (Person) request.getAttribute("person");
        if (person == null) {
            return new ModelAndView(new RedirectView(redirectIfNoAccount, false));
        } else if (!person.isResearcher()) {
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("error_message", adviserWarning);
            return new ModelAndView("request_membership_response", m);
        }
        mav.addObject("membershiprequest", mr);
        return mav;
    }

}
