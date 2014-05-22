package nz.ac.auckland.cer.project.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import nz.ac.auckland.cer.project.dao.ProjectDatabaseDao;
import nz.ac.auckland.cer.project.pojo.FollowUp;
import nz.ac.auckland.cer.project.pojo.ProjectWrapper;
import nz.ac.auckland.cer.project.util.EmailUtil;
import nz.ac.auckland.cer.project.util.Person;
import nz.ac.auckland.cer.project.validation.FollowUpValidator;

@Controller
public class FollowUpController {

    private Logger log = Logger.getLogger(FollowUpController.class.getName());
    @Autowired private ProjectDatabaseDao projectDao;
    @Autowired private EmailUtil emailUtil;
    private String redirectIfNoAccount;

    @RequestMapping(value = "add_followup", method = RequestMethod.GET)
    public String addFollowUp(
            Model m,
            @RequestParam(value = "pid", required = false) Integer projectId,
            HttpServletRequest request) throws Exception {

        Person person = (Person) request.getAttribute("person");
        if (person == null) {
            return redirectIfNoAccount;
        } else if (!person.isResearcher()) {
            m.addAttribute("error_message", "Only researchers, but not advisers can give feedback");
        } else if (projectId == null) {
            m.addAttribute("error_message", "No project id specified");
        }
        FollowUp fu = new FollowUp();
        fu.setProjectId(projectId);
        m.addAttribute("followUp", fu);
        return "add_followup";
    }

    @RequestMapping(value = "add_followup", method = RequestMethod.POST)
    public String processAddFollowUp(
            Model m,
            @Valid @ModelAttribute("followUp") FollowUp fu,
            BindingResult bResult,
            HttpServletRequest request) throws Exception {

        if (bResult.hasErrors()) {
            m.addAttribute("followUp", fu);
            return "add_followup";
        }
        Person person = (Person) request.getAttribute("person");
        if (person == null) {
            return redirectIfNoAccount;
        } else if (!person.isResearcher()) {
            m.addAttribute("error_message", "Only researchers, but not advisers can give feedback");
            return "add_followup";
        } else {
            fu.setDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            fu.setResearcherId(person.getId());
            try {
                this.projectDao.addOrUpdateFollowUp(fu);
                this.emailUtil.sendNewFollowUpEmail(person.getFullName(), fu.getNotes(), fu.getProjectId());
                return "redirect:view_project?id=" + fu.getProjectId();
            } catch (Exception e) {
                m.addAttribute("error_message", e.getMessage());
                return "add_followup";
            }
        }
    }

    @RequestMapping(value = "edit_followup", method = RequestMethod.GET)
    public String editFollowUp(
            Model m,
            @RequestParam(value = "pid", required = false) Integer projectId,
            @RequestParam(value = "fid", required = false) Integer followUpId,
            HttpServletRequest request) throws Exception {

        Person person = (Person) request.getAttribute("person");
        if (person == null) {
            return redirectIfNoAccount;
        } else if (!person.isResearcher()) {
            m.addAttribute("error_message", "Only researchers, but not advisers can give feedback");
        } else if (projectId == null) {
            m.addAttribute("error_message", "No project id specified");
        }
        ProjectWrapper pw = this.projectDao.getProjectForIdOrCode(Integer.toString(projectId));
        FollowUp fu = null;
        for (FollowUp tmp: pw.getFollowUps()) {
            if (tmp.getId().equals(followUpId)) {
                fu = tmp;
                break;
            }
        }
        m.addAttribute("followUp", fu);
        return "edit_followup";
    }

    @RequestMapping(value = "edit_followup", method = RequestMethod.POST)
    public String processEditFollowUp(
            Model m,
            @Valid @ModelAttribute("followUp") FollowUp fu,
            BindingResult bResult,
            HttpServletRequest request) throws Exception {

        if (bResult.hasErrors()) {
            m.addAttribute("followUp", fu);
            return "edit_followup";
        }
        Person person = (Person) request.getAttribute("person");
        if (person == null) {
            return redirectIfNoAccount;
        } else if (!person.isResearcher()) {
            m.addAttribute("error_message", "Only researchers, but not advisers can give feedback");
            return "edit_followup";
        } else {
            try {
                this.projectDao.addOrUpdateFollowUp(fu);
                return "redirect:view_project?id=" + fu.getProjectId();
            } catch (Exception e) {
                m.addAttribute("error_message", e.getMessage());
                m.addAttribute("followUp", fu);
                return "edit_followup";
            }
        }
    }

    /**
     * Configure validator
     */
    @InitBinder("followUp")
    protected void initBinder(
            WebDataBinder binder) {

        binder.addValidators(new FollowUpValidator());
    }

    public void setRedirectIfNoAccount(
            String redirectIfNoAccount) {

        this.redirectIfNoAccount = redirectIfNoAccount;
    }

}
