package nz.ac.auckland.cer.project.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import nz.ac.auckland.cer.project.pojo.APLink;
import nz.ac.auckland.cer.project.pojo.Affiliation;
import nz.ac.auckland.cer.project.pojo.Limitations;
import nz.ac.auckland.cer.project.pojo.Project;
import nz.ac.auckland.cer.project.pojo.ProjectFacility;
import nz.ac.auckland.cer.project.pojo.ProjectProperty;
import nz.ac.auckland.cer.project.pojo.ProjectWrapper;
import nz.ac.auckland.cer.project.pojo.RPLink;
import nz.ac.auckland.cer.project.pojo.ProjectRequest;
import nz.ac.auckland.cer.project.pojo.Researcher;
import nz.ac.auckland.cer.project.util.AffiliationUtil;
import nz.ac.auckland.cer.project.util.EmailUtil;
import nz.ac.auckland.cer.project.util.Person;
import nz.ac.auckland.cer.project.validation.ProjectRequestValidator;

@Controller
public class ProjectRequestController {

    private Logger log = Logger.getLogger(ProjectRequestController.class.getName());
    @Autowired private ProjectDatabaseDao projectDao;
    @Autowired private AffiliationUtil affilUtil;
    @Autowired private EmailUtil emailUtil;
    private String hostInstitution;
    private String redirectIfNoAccount;
    private Integer initialResearcherRoleOnProject;

    @RequestMapping(value = "request_project", method = RequestMethod.GET)
    public String showProjectRequestForm(
            Model m,
            HttpServletRequest request) throws Exception {

        ProjectRequest pr = new ProjectRequest();
        Person person = (Person) request.getAttribute("person");
        if (person == null) {
            return redirectIfNoAccount;
        } else if (!person.isResearcher()) {
            m.addAttribute("error_message", "Only researchers can apply for projects."
                    + " You appear to be an adviser.");
            return "request_project_response";
        }
        pr.setAskForSuperviser(this.askForSuperviser(person));
        m.addAttribute("projectrequest", pr);
        this.augmentModel(m);
        return "request_project";
    }

    @RequestMapping(value = "request_project", method = RequestMethod.POST)
    public String processProjectRequestForm(
            Model m,
            @Valid @ModelAttribute("projectrequest") ProjectRequest pr,
            BindingResult bResult,
            HttpServletRequest request) throws Exception {

        if (bResult.hasErrors()) {
            m.addAttribute("projectrequest", pr);
            this.augmentModel(m);
            return "request_project";
        }
        try {
            Person person = (Person) request.getAttribute("person");
            if (person == null) {
                return redirectIfNoAccount;
            } else if (!person.isResearcher()) {
                m.addAttribute("error_message", "Only researchers can apply for projects."
                        + " You appear to be an adviser.");
                return "request_project_response";
            }
            Researcher superviser = null;
            if (pr.getSuperviserId() != null && pr.getSuperviserId() > 0) {
                superviser = this.projectDao.getResearcherForId(pr.getSuperviserId());
            }
            Project p = this.createProject(pr, superviser, person);
            this.createProjectProperties(p, pr);
            if (this.askForSuperviser(person)) {
                this.emailUtil.sendProjectRequestWithSuperviserEmail(p, pr, superviser, person.getFullName());
            } else {
                this.emailUtil.sendProjectRequestEmail(p, person.getFullName());
            }
            return "request_project_response";
        } catch (Exception e) {
            log.error("Failed to create project", e);
            bResult.addError(new ObjectError(bResult.getObjectName(), e.getMessage()));
            return "request_project";
        }
    }

    private void augmentModel(
            Model m) {

        Affiliation[] afs = null;
        Map<Integer, String> superviserMap = null;
        String errorMessage = "";
        try {
            afs = this.projectDao.getAffiliations();
            if (afs == null || afs.length == 0) {
                throw new Exception();
            }
            List<String> tmp = this.affilUtil.getAffiliationStrings(afs);
            m.addAttribute("affiliations", tmp);
        } catch (Exception e) {
            String error = "Failed to load affiliations.";
            errorMessage += "Internal Error: " + error;
            log.error(error, e);
        }

        try {
            superviserMap = this.getSortedSuperviserMap();
            m.addAttribute("superviserDropdownMap", superviserMap);
        } catch (Exception e) {
            String error = "Failed to load superviser map.";
            errorMessage += "Internal Error: " + error;
            log.error(error, e);
        }

        if (errorMessage.trim().length() > 0) {
            m.addAttribute("unexpected_error", errorMessage);
        }
    }

    private Project createProject(
            ProjectRequest pr,
            Researcher superviser,
            Person researcher) throws Exception {

        ProjectWrapper pw = new ProjectWrapper();
        Project p = new Project();
        List<RPLink> rpLinks = new LinkedList<RPLink>();
        ProjectFacility pf = new ProjectFacility(1);
        rpLinks.add(new RPLink(null, researcher.getId(), this.initialResearcherRoleOnProject));
        if (superviser != null) {
            // TODO: replace 2 with configured value for "Superviser"
            rpLinks.add(new RPLink(null, superviser.getId(), 2));
        }
        APLink apLink = new APLink(null, 6, 1);

        p.setName(pr.getProjectTitle());
        p.setDescription(pr.getProjectDescription());
        p.setHostInstitution(this.hostInstitution);

        Calendar now = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        p.setStartDate(df.format(now.getTime()));
        now.add(Calendar.MONTH, 3);
        p.setNextFollowUpDate(df.format(now.getTime()));
        now.add(Calendar.MONTH, 9);
        p.setNextReviewDate(df.format(now.getTime()));

        pw.setProject(p);
        pw.setProjectFacilities(new ProjectFacility[] { pf });
        pw.setRpLinks(rpLinks.toArray(new RPLink[0]));
        pw.setApLinks(new APLink[] { apLink });

        return this.projectDao.createProject(pw);
    }

    private void createProjectProperties(
            Project project,
            ProjectRequest pr) throws Exception {

        ProjectProperty pp = new ProjectProperty();
        pp.setFacilityId(1);
        pp.setProjectId(project.getId());

        pp.setPropname("motivation_for_using_pan");
        pp.setPropvalue(pr.getMotivation());
        this.projectDao.createProjectProperty(pp);

        pp.setPropname("system_available_before_using_pan");
        if (pr.getCurrentCompEnv().equals("OTHER")) {
            pp.setPropvalue(pr.getOtherCompEnv());
        } else {
            pp.setPropvalue(pr.getCurrentCompEnv());
        }
        this.projectDao.createProjectProperty(pp);

        Limitations l = pr.getLimitations();
        if (pr.getCurrentCompEnv().equals("standard_computer")) {
            l = new Limitations();
            l.setCpuCores("4");
            l.setMemory("8GB");
            l.setConcurrency("4");
        }

        pp.setPropname("max_cpu_cores_before_using_pan");
        pp.setPropvalue(l.getCpuCores());
        this.projectDao.createProjectProperty(pp);

        pp.setPropname("max_memory_before_using_pan");
        pp.setPropvalue(l.getMemory());
        this.projectDao.createProjectProperty(pp);

        pp.setPropname("max_concurrent_jobs_before_using_pan");
        pp.setPropvalue(l.getConcurrency());
        this.projectDao.createProjectProperty(pp);
    }

    /**
     * Check whether we need to ask for superviser information or not.
     */
    private boolean askForSuperviser(
            Person p) throws Exception {

        return p.isResearcher() && (p.getInstitutionalRoleId() > 1);
    }

    private Map<Integer, String> getSortedSuperviserMap() throws Exception {

        Map<Integer, String> superviserMap = new LinkedHashMap<Integer, String>();
        Researcher[] staffOrPostDocs = this.projectDao.getAllStaffOrPostDocs();
        for (Researcher r : staffOrPostDocs) {
            String affilString = affilUtil.createAffiliationString(r.getInstitution(), r.getDivision(),
                    r.getDepartment());
            superviserMap.put(r.getId(), r.getFullName() + " (" + affilString + ")");
        }
        return superviserMap;
    }

    /**
     * Configure validator for cluster project and membership request form
     */
    @InitBinder
    protected void initBinder(
            WebDataBinder binder) {

        binder.addValidators(new ProjectRequestValidator());
    }

    public void setInitialResearcherRoleOnProject(
            Integer initialResearcherRoleOnProject) {

        this.initialResearcherRoleOnProject = initialResearcherRoleOnProject;
    }

    public void setHostInstitution(
            String hostInstitution) {

        this.hostInstitution = hostInstitution;
    }

    public void setRedirectIfNoAccount(
            String redirectIfNoAccount) {

        this.redirectIfNoAccount = redirectIfNoAccount;
    }

}
