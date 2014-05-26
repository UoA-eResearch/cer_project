package nz.ac.auckland.cer.project.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import nz.ac.auckland.cer.project.dao.ProjectDatabaseDao;
import nz.ac.auckland.cer.project.pojo.ProjectWrapper;
import nz.ac.auckland.cer.project.util.Person;

@Controller
public class ProjectController {

    private Logger log = Logger.getLogger(ProjectController.class.getName());
    @Autowired private ProjectDatabaseDao projectDao;
    private String redirectIfNoAccount;
    private String adviserWarning = "In our books you are an adviser but not a researcher. Only researchers may use this tool.";

    @RequestMapping(value = "view_projects", method = RequestMethod.GET)
    public ModelAndView showProjects(
            Model m,
            HttpServletRequest request) throws Exception {

        ModelAndView mav = new ModelAndView("show_projects");
        Person person = (Person) request.getAttribute("person");
        if (person == null) {
            return new ModelAndView(new RedirectView(redirectIfNoAccount, false));
        } else if (!person.isResearcher()) {
            mav.addObject("error_message", adviserWarning);
        } else {
            try {
                mav.addObject("projects", this.projectDao.getProjectsOfResearcher(person.getId()));                
            } catch (Exception e) {
                log.error("Failed to fetch projects for researcher.", e);
                mav.addObject("error_message", "Failed to fetch projects from database.");
            }
        }
        return mav;
    }

    @RequestMapping(value = "view_project", method = RequestMethod.GET)
    public ModelAndView showProject(
            @RequestParam(value="id", required=false) String projectId, 
            HttpServletRequest request) throws Exception {

        ModelAndView mav = new ModelAndView("show_project");
        // TODO: check if person is on the project
        Person person = (Person) request.getAttribute("person");
        if (person == null) {
            System.err.println("redirecting to " + redirectIfNoAccount);
            return new ModelAndView(new RedirectView(redirectIfNoAccount, false));
        } else if (!person.isResearcher()) {
            mav.addObject("error_message", "Advisers don't see their project here");
        } else if (projectId == null || projectId.trim().isEmpty()) {
            mav.addObject("error_message", "No project id specified");
        } else {
            try {
                ProjectWrapper pw = this.projectDao.getProjectForIdOrCode(projectId);
                mav.addObject("pw", pw);
            } catch (Exception e) {
                log.error("Failed to fetch project " + projectId, e);
                mav.addObject("error_message", "Failed to fetch project from database.");
            }
        }
        return mav;
    }

    public void setRedirectIfNoAccount(
            String redirectIfNoAccount) {
    
        this.redirectIfNoAccount = redirectIfNoAccount;
    }

}
