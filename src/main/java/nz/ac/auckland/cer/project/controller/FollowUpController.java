package nz.ac.auckland.cer.project.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import nz.ac.auckland.cer.project.dao.ProjectDatabaseDao;
import nz.ac.auckland.cer.project.pojo.FollowUp;
import nz.ac.auckland.cer.project.pojo.ProjectWrapper;
import nz.ac.auckland.cer.project.util.EmailUtil;
import nz.ac.auckland.cer.project.util.Person;
import nz.ac.auckland.cer.project.validation.FollowUpValidator;

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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class FollowUpController {

    private final String adviserWarning = "In our books you are an adviser but not a researcher. Only researchers may use this tool.";
    @Autowired
    private EmailUtil emailUtil;
    private final Logger log = Logger.getLogger(FollowUpController.class
            .getName());
    @Autowired
    private ProjectDatabaseDao projectDao;
    private String redirectIfNoAccount;

    @RequestMapping(value = "add_followup", method = RequestMethod.GET)
    public ModelAndView addFollowUp(
            @RequestParam(value = "pid", required = false) Integer projectId,
            HttpServletRequest request) throws Exception {

        Map<String, Object> m = new HashMap<String, Object>();
        Person person = (Person) request.getAttribute("person");
        if (person == null) {
            return new ModelAndView(
                    new RedirectView(redirectIfNoAccount, false));
        } else if (!person.isResearcher()) {
            m.put("error_message", adviserWarning);
        } else if (projectId == null) {
            m.put("error_message", "No project id specified");
        } else {
            FollowUp fu = new FollowUp();
            fu.setProjectId(projectId);
            m.put("followUp", fu);
        }
        return new ModelAndView("add_followup", m);
    }

    @RequestMapping(value = "edit_followup", method = RequestMethod.GET)
    public ModelAndView editFollowUp(Model m,
            @RequestParam(value = "pid", required = false) Integer projectId,
            @RequestParam(value = "fid", required = false) Integer followUpId,
            HttpServletRequest request) throws Exception {

        ModelAndView mav = new ModelAndView("edit_followup");
        Person person = (Person) request.getAttribute("person");
        if (person == null) {
            return new ModelAndView(
                    new RedirectView(redirectIfNoAccount, false));
        } else if (!person.isResearcher()) {
            mav.addObject("error_message", adviserWarning);
        } else if (projectId == null) {
            mav.addObject("error_message", "No project id specified");
        }
        ProjectWrapper pw = this.projectDao.getProjectForIdOrCode(Integer
                .toString(projectId));
        FollowUp fu = null;
        for (FollowUp tmp : pw.getFollowUps()) {
            if (tmp.getId().equals(followUpId)) {
                fu = tmp;
                break;
            }
        }
        mav.addObject("followUp", fu);
        return mav;
    }

    /**
     * Configure validator
     */
    @InitBinder("followUp")
    protected void initBinder(WebDataBinder binder) {

        binder.addValidators(new FollowUpValidator());
    }

    @RequestMapping(value = "add_followup", method = RequestMethod.POST)
    public ModelAndView processAddFollowUp(
            @Valid @ModelAttribute("followUp") FollowUp fu,
            BindingResult bResult, HttpServletRequest request) throws Exception {

        ModelAndView mav = new ModelAndView("add_followup");
        if (bResult.hasErrors()) {
            mav.addObject("followUp", fu);
            return mav;
        }
        Person person = (Person) request.getAttribute("person");
        if (person == null) {
            return new ModelAndView(
                    new RedirectView(redirectIfNoAccount, false));
        } else if (!person.isResearcher()) {
            mav.addObject("error_message", adviserWarning);
            return mav;
        } else {
            fu.setDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            fu.setResearcherId(person.getId());
            try {
                this.projectDao.addOrUpdateFollowUp(fu);
                this.emailUtil.sendNewFollowUpEmail(person.getFullName(),
                        person.getEmail(), fu.getNotes(), fu.getProjectId());
                return new ModelAndView(new RedirectView("view_project?id="
                        + fu.getProjectId(), true));
            } catch (Exception e) {
                mav.addObject("error_message", e.getMessage());
                return mav;
            }
        }
    }

    @RequestMapping(value = "edit_followup", method = RequestMethod.POST)
    public ModelAndView processEditFollowUp(Model m,
            @Valid @ModelAttribute("followUp") FollowUp fu,
            BindingResult bResult, HttpServletRequest request) throws Exception {

        ModelAndView mav = new ModelAndView("edit_followup");
        if (bResult.hasErrors()) {
            mav.addObject("followUp", fu);
            return mav;
        }
        Person person = (Person) request.getAttribute("person");
        if (person == null) {
            return new ModelAndView(
                    new RedirectView(redirectIfNoAccount, false));
        } else if (!person.isResearcher()) {
            mav.addObject("error_message", adviserWarning);
            return mav;
        } else {
            try {
                this.projectDao.addOrUpdateFollowUp(fu);
                return new ModelAndView(new RedirectView("view_project?id="
                        + fu.getProjectId(), true));
            } catch (Exception e) {
                mav.addObject("error_message", e.getMessage());
                mav.addObject("followUp", fu);
                return mav;
            }
        }
    }

    public void setRedirectIfNoAccount(String redirectIfNoAccount) {

        this.redirectIfNoAccount = redirectIfNoAccount;
    }

}
