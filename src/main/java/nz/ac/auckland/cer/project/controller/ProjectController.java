package nz.ac.auckland.cer.project.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import nz.ac.auckland.cer.project.dao.ProjectDatabaseDao;
import nz.ac.auckland.cer.project.pojo.ProjectWrapper;
import nz.ac.auckland.cer.project.util.Person;

@Controller
public class ProjectController {

    private Logger log = Logger.getLogger(ProjectController.class.getName());
    @Autowired private ProjectDatabaseDao projectDao;
    private String redirectIfNoAccount;

    @RequestMapping(value = "view_projects", method = RequestMethod.GET)
    public String showProjects(
            Model m,
            HttpServletRequest request) throws Exception {

        Person person = (Person) request.getAttribute("person");
        if (person == null) {
            return redirectIfNoAccount;
        } else if (!person.isResearcher()) {
            m.addAttribute("error_message", "Only researchers, but not adviseres can view their projects here");
        } else {
            try {
                m.addAttribute("projects", this.projectDao.getProjectsOfResearcher(person.getId()));                
            } catch (Exception e) {
                log.error("Failed to fetch projects for researcher.", e);
                m.addAttribute("error_message", "Failed to fetch projects from database.");
            }
        }
        return "show_projects";
    }

    @RequestMapping(value = "view_project", method = RequestMethod.GET)
    public String showProject(
            Model m,
            @RequestParam(value="id", required=false) String projectId, 
            HttpServletRequest request) throws Exception {

        // TODO: check if person is on the project
        Person person = (Person) request.getAttribute("person");
        if (person == null) {
            return redirectIfNoAccount;
        } else if (!person.isResearcher()) {
            m.addAttribute("error_message", "Advisers don't see their project here");
        } else if (projectId == null || projectId.trim().isEmpty()) {
            m.addAttribute("error_message", "No project id specified");
        } else {
            try {
                ProjectWrapper pw = this.projectDao.getProjectForIdOrCode(projectId);
                m.addAttribute("pw", pw);
            } catch (Exception e) {
                log.error("Failed to fetch project " + projectId, e);
                m.addAttribute("error_message", "Failed to fetch project from database.");
            }
        }
        return "show_project";
    }

    public void setRedirectIfNoAccount(
            String redirectIfNoAccount) {
    
        this.redirectIfNoAccount = redirectIfNoAccount;
    }

}
