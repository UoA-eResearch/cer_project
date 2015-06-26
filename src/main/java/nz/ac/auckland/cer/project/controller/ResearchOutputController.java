package nz.ac.auckland.cer.project.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import nz.ac.auckland.cer.common.db.project.dao.ProjectDbDao;
import nz.ac.auckland.cer.common.db.project.pojo.ProjectWrapper;
import nz.ac.auckland.cer.common.db.project.pojo.ResearchOutput;
import nz.ac.auckland.cer.common.db.project.pojo.ResearchOutputType;
import nz.ac.auckland.cer.common.db.project.util.Person;
import nz.ac.auckland.cer.project.util.EmailUtil;
import nz.ac.auckland.cer.project.validation.ResearchOutputValidator;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
public class ResearchOutputController {

    private final String adviserWarning = "In our books you are an adviser but not a researcher. Only researchers may use this tool.";
    @Autowired
    private EmailUtil emailUtil;
    private final Logger log = Logger.getLogger(ResearchOutputController.class
            .getName());
    @Autowired
    private ProjectDbDao projectDao;
    private String redirectIfNoAccount;

    @RequestMapping(value = "add_research_output", method = RequestMethod.GET)
    public ModelAndView addResearchOutput(
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
            ResearchOutput ro = new ResearchOutput();
            ro.setProjectId(projectId);
            m.put("researchOutput", ro);
        }
        this.augmentModel(m);
        return new ModelAndView("add_research_output", m);
    }

    private void augmentModel(Map<String, Object> m) {

        String errorMessage = "";
        try {
            m.put("researchOutputTypeMap", this.getResearchOutputTypeMap());
        } catch (Exception e) {
            String error = "Failed to load research output types.";
            errorMessage += "Internal Error: " + error;
            log.error(error, e);
        }

        if (errorMessage.trim().length() > 0) {
            m.put("unexpected_error", errorMessage);
        }
    }

    @RequestMapping(value = "edit_research_output", method = RequestMethod.GET)
    public ModelAndView editFollowUp(
            @RequestParam(value = "pid", required = false) Integer projectId,
            @RequestParam(value = "rid", required = false) Integer researchOutputId,
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
            ProjectWrapper pw = this.projectDao.getProjectForIdOrCode(Integer
                    .toString(projectId));
            ResearchOutput ro = null;
            for (ResearchOutput tmp : pw.getResearchOutputs()) {
                if (tmp.getId().equals(researchOutputId)) {
                    ro = tmp;
                    break;
                }
            }
            m.put("researchOutput", ro);
        }
        this.augmentModel(m);
        return new ModelAndView("edit_research_output", m);
    }

    private Map<Integer, String> getResearchOutputTypeMap() throws Exception {

        ResearchOutputType[] rots = this.projectDao.getResearchOutputTypes();
        Map<Integer, String> typeMap = new LinkedHashMap<Integer, String>();
        if (rots == null || rots.length == 0) {
            throw new Exception();
        }
        for (ResearchOutputType rot : rots) {
            typeMap.put(rot.getId(), rot.getName());
        }
        return typeMap;
    }

    /**
     * Configure validator
     */
    @InitBinder("researchOutput")
    protected void initBinder(WebDataBinder binder) {

        binder.addValidators(new ResearchOutputValidator());
    }

    @RequestMapping(value = "add_research_output", method = RequestMethod.POST)
    public ModelAndView processAddResearchOutput(
            @Valid @ModelAttribute("researchOutput") ResearchOutput ro,
            BindingResult bResult, HttpServletRequest request) throws Exception {

        Map<String, Object> m = new HashMap<String, Object>();
        if (bResult.hasErrors()) {
            m.put("researchOutput", ro);
            this.augmentModel(m);
            return new ModelAndView("add_research_output", m);
        }
        Person person = (Person) request.getAttribute("person");
        if (person == null) {
            return new ModelAndView(
                    new RedirectView(redirectIfNoAccount, false));
        } else if (!person.isResearcher()) {
            m.put("error_message", adviserWarning);
            return new ModelAndView("add_research_output", m);
        } else {
            ro.setDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            ro.setResearcherId(person.getId());
            try {
                this.projectDao.addOrUpdateResearchOutput(ro);
                this.emailUtil.sendNewResearchOutputEmail(person.getFullName(),
                        person.getEmail(),
                        this.getResearchOutputTypeMap().get(ro.getTypeId()),
                        ro.getDescription(), ro.getProjectId());
                return new ModelAndView(new RedirectView("view_project?id="
                        + ro.getProjectId(), true));
            } catch (Exception e) {
                m.put("error_message", e.getMessage());
                return new ModelAndView("add_research_output", m);
            }
        }
    }

    @RequestMapping(value = "edit_research_output", method = RequestMethod.POST)
    public ModelAndView processEditFollowUp(
            @Valid @ModelAttribute("researchOutput") ResearchOutput ro,
            BindingResult bResult, HttpServletRequest request) throws Exception {

        Map<String, Object> m = new HashMap<String, Object>();
        if (bResult.hasErrors()) {
            m.put("researchOutput", ro);
            this.augmentModel(m);
            return new ModelAndView("edit_research_output", m);
        }
        Person person = (Person) request.getAttribute("person");
        if (person == null) {
            return new ModelAndView(
                    new RedirectView(redirectIfNoAccount, false));
        } else if (!person.isResearcher()) {
            m.put("error_message", adviserWarning);
            return new ModelAndView("edit_research_output", m);
        } else {
            try {
                this.projectDao.addOrUpdateResearchOutput(ro);
                return new ModelAndView(new RedirectView("view_project?id="
                        + ro.getProjectId(), true));
            } catch (Exception e) {
                m.put("error_message", e.getMessage());
                m.put("researchOutput", ro);
                return new ModelAndView("edit_research_output", m);
            }
        }
    }

    public void setRedirectIfNoAccount(String redirectIfNoAccount) {

        this.redirectIfNoAccount = redirectIfNoAccount;
    }

}
