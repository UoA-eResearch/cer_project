package nz.ac.auckland.cer.project.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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

import nz.ac.auckland.cer.project.dao.ProjectDatabaseDao;
import nz.ac.auckland.cer.project.pojo.FollowUp;
import nz.ac.auckland.cer.project.pojo.ProjectWrapper;
import nz.ac.auckland.cer.project.pojo.ResearchOutput;
import nz.ac.auckland.cer.project.pojo.ResearchOutputType;
import nz.ac.auckland.cer.project.pojo.survey.Bigger;
import nz.ac.auckland.cer.project.pojo.survey.Faster;
import nz.ac.auckland.cer.project.pojo.survey.More;
import nz.ac.auckland.cer.project.pojo.survey.ResearchOutcome;
import nz.ac.auckland.cer.project.pojo.survey.Survey;
import nz.ac.auckland.cer.project.util.EmailUtil;
import nz.ac.auckland.cer.project.util.Person;
import nz.ac.auckland.cer.project.validation.SurveyValidator;

@Controller
public class SurveyController {

    private Logger log = Logger.getLogger(SurveyController.class.getName());
    @Autowired private ProjectDatabaseDao projectDao;
    @Autowired private EmailUtil emailUtil;
    private String adviserWarning = "In our books you are an adviser but not a researcher. Only researchers may use this tool.";

    @RequestMapping(value = "survey", method = RequestMethod.GET)
    public ModelAndView show_survey(
            @RequestParam(value = "pCode", required = false) String projectCode,
            HttpServletRequest request) throws Exception {

        Map<String, Object> m = new HashMap<String, Object>();
        Person person = (Person) request.getAttribute("person");
        if (person == null) {
            m.put("error_message", "Cannot display survey: You don't seem to have a cluster account. "
                    + "Only researchers with a cluster account can view the survey.");
        } else if (!person.isResearcher()) {
            m.put("error_message", adviserWarning);
        } else if (projectCode == null) {
            m.put("error_message", "Cannot display survey: No project code specified");
        } else {
            ProjectWrapper pw = this.projectDao.getProjectForIdOrCode(projectCode);
            Map<Integer, String> projectRoleMap = this.projectDao.getRolesOnProjectsForResearcher(person.getId());
            if (!projectRoleMap.containsKey(pw.getProject().getId())) {
                m.put("error_message", "Cannot display survey: You are not a member of the project covered by this survey");
            } else {
                m.put("pw", pw);
                m.put("researchOutputTypeMap", this.getResearchOutputTypeMap());
                Survey survey = new Survey();
                survey.setProjectCode(pw.getProject().getProjectCode());
                ResearchOutcome ro = new ResearchOutcome();
                ro.getResearchOutputs().add(new ResearchOutput());
                survey.setResearchOutcome(ro);
                m.put("survey", survey);                
            }
        }
        return new ModelAndView("survey", m);
    }

    @RequestMapping(value = "survey", method = RequestMethod.POST)
    public ModelAndView process_survey(
            @Valid @ModelAttribute("survey") Survey survey,
            BindingResult bResult,
            HttpServletRequest request) throws Exception {

        Map<String, Object> m = new HashMap<String, Object>();
        Person person = (Person) request.getAttribute("person");
        ProjectWrapper pw = this.projectDao.getProjectForIdOrCode(survey.getProjectCode());
        // only add another row for research output
        if (survey.getResearchOutcome().getAddResearchOutputRow() > 0) {
            m.put("pw", pw);
            m.put("survey", survey);
            m.put("researchOutputTypeMap", this.getResearchOutputTypeMap());
            survey.getResearchOutcome().getResearchOutputs().add(new ResearchOutput());
            return new ModelAndView("survey", m);
        }

        if (bResult.hasErrors()) {
            m.put("pw", pw);
            m.put("researchOutputTypeMap", this.getResearchOutputTypeMap());
            m.put("survey", survey);
            return new ModelAndView("survey", m);
        }
        this.emailUtil.sendSurveyEmail(person.getFullName(), person.getEmail(), pw, survey);
        this.addFeedbackToDatabase(survey, pw, person);
        return new ModelAndView("survey_response");
    }

    /**
     * Configure validator
     */
    @InitBinder("survey")
    protected void initBinder(
            WebDataBinder binder) {

        binder.addValidators(new SurveyValidator());
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

    private void addFeedbackToDatabase(Survey survey, ProjectWrapper pw, Person person) throws Exception {
        String dateString = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String feedback = "Performance Improvements:<br>";
        Faster faster = survey.getFaster();
        Bigger bigger = survey.getBigger();
        More more = survey.getMore();
        if (faster == null && bigger == null && more == null) {
            feedback += "N/A";
        } else {
            if (faster != null) {
                feedback += faster.toString() + " ";
            }
            if (bigger != null) {
                feedback += bigger.toString() + " ";
            }
            if (more != null) {
                feedback += more.toString();
            }
        }
        feedback += "<br><br>Future Needs:<br>" + survey.getFutureNeeds().toString() +
                "<br><br>Feedback:<br>" + survey.getFeedback().toString();
        FollowUp fu = new FollowUp();
        fu.setResearcherId(person.getId());
        fu.setNotes(feedback);
        fu.setProjectId(pw.getProject().getId());
        fu.setDate(dateString);
        this.projectDao.addOrUpdateFollowUp(fu);
        ResearchOutcome ro = survey.getResearchOutcome();
        if (ro != null && ro.getResearchOutputs() != null) {
            for (ResearchOutput tmp : ro.getResearchOutputs()) {
                if (tmp.getDescription() != null && !tmp.getDescription().trim().isEmpty()) {
                    tmp.setProjectId(pw.getProject().getId());
                    tmp.setDate(dateString);
                    this.projectDao.addOrUpdateResearchOutput(tmp);
                }
            }
        }
    }
}
