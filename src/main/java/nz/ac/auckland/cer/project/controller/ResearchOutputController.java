package nz.ac.auckland.cer.project.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

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
import nz.ac.auckland.cer.project.pojo.ProjectWrapper;
import nz.ac.auckland.cer.project.pojo.ResearchOutput;
import nz.ac.auckland.cer.project.pojo.ResearchOutputType;
import nz.ac.auckland.cer.project.util.EmailUtil;
import nz.ac.auckland.cer.project.util.Person;
import nz.ac.auckland.cer.project.validation.ResearchOutputValidator;

@Controller
public class ResearchOutputController {

    private Logger log = Logger.getLogger(ResearchOutputController.class.getName());
    @Autowired private ProjectDatabaseDao projectDao;
    @Autowired private EmailUtil emailUtil;
    private String redirectIfNoAccount;

    @RequestMapping(value = "add_research_output", method = RequestMethod.GET)
    public String addResearcherOutput(
            Model m,
            @RequestParam(value = "pid", required = false) Integer projectId,
            HttpServletRequest request) throws Exception {

        Person person = (Person) request.getAttribute("person");
        if (person == null) {
            return redirectIfNoAccount;
        } else if (!person.isResearcher()) {
            m.addAttribute("error_message", "Only researchers, but not advisers can provide research output");
        } else if (projectId == null) {
            m.addAttribute("error_message", "No project id specified");
        }
        ResearchOutput ro = new ResearchOutput();
        ro.setProjectId(projectId);
        m.addAttribute("researchOutput", ro);
        this.augmentModel(m);
        return "add_research_output";
    }

    @RequestMapping(value = "add_research_output", method = RequestMethod.POST)
    public String processAddResearchOutput(
            Model m,
            @Valid @ModelAttribute("researchOutput") ResearchOutput ro,
            BindingResult bResult,
            HttpServletRequest request) throws Exception {

        if (bResult.hasErrors()) {
            m.addAttribute("researchOutput", ro);
            this.augmentModel(m);
            return "add_research_output";
        }
        Person person = (Person) request.getAttribute("person");
        if (person == null) {
            return redirectIfNoAccount;
        } else if (!person.isResearcher()) {
            m.addAttribute("error_message", "Only researchers, but not advisers can provide research output");
            return "add_research_output";
        } else {
            ro.setDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            try {
                this.projectDao.addOrUpdateResearchOutput(ro);
                this.emailUtil.sendNewResearchOutputEmail(person.getFullName(),
                        this.getResearchOutputTypeMap().get(ro.getTypeId()), ro.getDescription(), ro.getProjectId());
                return "redirect:view_project?id=" + ro.getProjectId();
            } catch (Exception e) {
                m.addAttribute("error_message", e.getMessage());
                return "add_research_output";
            }
        }
    }

    @RequestMapping(value = "edit_research_output", method = RequestMethod.GET)
    public String editFollowUp(
            Model m,
            @RequestParam(value = "pid", required = false) Integer projectId,
            @RequestParam(value = "rid", required = false) Integer researchOutputId,
            HttpServletRequest request) throws Exception {

        Person person = (Person) request.getAttribute("person");
        if (person == null) {
            return redirectIfNoAccount;
        } else if (!person.isResearcher()) {
            m.addAttribute("error_message", "Only researchers, but not advisers can provide research output.");
        } else if (projectId == null) {
            m.addAttribute("error_message", "No project id specified");
        }
        ProjectWrapper pw = this.projectDao.getProjectForIdOrCode(Integer.toString(projectId));
        ResearchOutput ro = null;
        for (ResearchOutput tmp : pw.getResearchOutputs()) {
            if (tmp.getId().equals(researchOutputId)) {
                ro = tmp;
                break;
            }
        }
        m.addAttribute("researchOutput", ro);
        this.augmentModel(m);
        return "edit_research_output";
    }

    @RequestMapping(value = "edit_research_output", method = RequestMethod.POST)
    public String processEditFollowUp(
            Model m,
            @Valid @ModelAttribute("researchOutput") ResearchOutput ro,
            BindingResult bResult,
            HttpServletRequest request) throws Exception {

        if (bResult.hasErrors()) {
            m.addAttribute("researchOutput", ro);
            this.augmentModel(m);
            return "edit_research_output";
        }
        Person person = (Person) request.getAttribute("person");
        if (person == null) {
            return redirectIfNoAccount;
        } else if (!person.isResearcher()) {
            m.addAttribute("error_message", "Only researchers, but not advisers can provide research output.");
            return "edit_research_output";
        } else {
            try {
                this.projectDao.addOrUpdateResearchOutput(ro);
                return "redirect:view_project?id=" + ro.getProjectId();
            } catch (Exception e) {
                m.addAttribute("error_message", e.getMessage());
                m.addAttribute("researchOutput", ro);
                return "edit_research_output";
            }
        }
    }

    /**
     * Configure validator
     */
    @InitBinder("researchOutput")
    protected void initBinder(
            WebDataBinder binder) {

        binder.addValidators(new ResearchOutputValidator());
    }

    private void augmentModel(
            Model m) {

        String errorMessage = "";
        try {
            m.addAttribute("researchOutputTypeMap", this.getResearchOutputTypeMap());
        } catch (Exception e) {
            String error = "Failed to load research output types.";
            errorMessage += "Internal Error: " + error;
            log.error(error, e);
        }

        if (errorMessage.trim().length() > 0) {
            m.addAttribute("unexpected_error", errorMessage);
        }
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

    public void setRedirectIfNoAccount(
            String redirectIfNoAccount) {

        this.redirectIfNoAccount = redirectIfNoAccount;
    }

}
