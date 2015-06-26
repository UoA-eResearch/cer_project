package nz.ac.auckland.cer.project.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import nz.ac.auckland.cer.common.db.project.dao.ProjectDbDao;
import nz.ac.auckland.cer.common.db.project.pojo.FollowUp;
import nz.ac.auckland.cer.common.db.project.pojo.ProjectWrapper;
import nz.ac.auckland.cer.common.db.project.pojo.ResearchOutput;
import nz.ac.auckland.cer.common.db.project.pojo.ResearchOutputType;
import nz.ac.auckland.cer.common.db.project.util.Person;
import nz.ac.auckland.cer.project.pojo.survey.ResearchOutcome;
import nz.ac.auckland.cer.project.pojo.survey.Survey;
import nz.ac.auckland.cer.project.util.EmailUtil;
import nz.ac.auckland.cer.project.util.SurveyUtil;
import nz.ac.auckland.cer.project.validation.SurveyValidator;

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

@Controller
public class SurveyController {

	private final String adviserWarning = "In our books you are an adviser but not a researcher. Only researchers may use this tool.";
	private final Logger log = Logger.getLogger(SurveyController.class
			.getName());
	@Autowired
	private EmailUtil emailUtil;
	@Autowired
	private ProjectDbDao projectDao;
	@Autowired
	private SurveyUtil surveyUtil;

	private void storeSurvey(Survey survey, ProjectWrapper pw,
			Person person) throws Exception {

		String dateString = new SimpleDateFormat("yyyy-MM-dd")
				.format(new Date());

		String notes = surveyUtil.createFollowUpString(survey);
		FollowUp fu = new FollowUp();
		fu.setResearcherId(person.getId());
		fu.setNotes(notes);
		fu.setProjectId(pw.getProject().getId());
		fu.setDate(dateString);
		this.projectDao.addOrUpdateFollowUp(fu);

		ResearchOutcome ro = survey.getResearchOutcome();
		if (ro != null && ro.getResearchOutputs() != null) {
			for (ResearchOutput tmp : ro.getResearchOutputs()) {
				if (tmp.getDescription() != null
						&& !tmp.getDescription().trim().isEmpty()) {
					tmp.setProjectId(pw.getProject().getId());
					tmp.setDate(dateString);
					tmp.setResearcherId(person.getId());
					this.projectDao.addOrUpdateResearchOutput(tmp);
				}
			}
		}
	}

	private Map<Integer, String> getResearchOutputTypeMap() throws Exception {

		ResearchOutputType[] rots = this.projectDao.getResearchOutputTypes();
		Map<Integer, String> typeMap = new LinkedHashMap<Integer, String>();
		if (rots == null) {
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
	@InitBinder("survey")
	protected void initBinder(WebDataBinder binder) {

		binder.addValidators(new SurveyValidator());
	}

	@RequestMapping(value = "survey", method = RequestMethod.POST)
	public ModelAndView process_survey(
			@Valid @ModelAttribute("survey") Survey survey,
			BindingResult bResult, HttpServletRequest request) throws Exception {

		log.debug("entering process_survey");

		Map<String, Object> m = new HashMap<String, Object>();
		Person person = (Person) request.getAttribute("person");
		ProjectWrapper pw = this.projectDao.getProjectForIdOrCode(survey
				.getProjectCode());
		log.debug("person: " + person.getFullName());

		// only add another row for research output and return
		// FIXME: remove first condition again
		if (survey.getAddResearchOutputRow() > 0) {
			log.debug("adding another row to add more research output");
			survey.getResearchOutcome().getResearchOutputs()
    			.add(new ResearchOutput());
			m.put("pw", pw);
			m.put("researchOutputTypeMap", this.getResearchOutputTypeMap());
			m.put("survey", survey);
			return new ModelAndView("survey", m);
		}

		log.debug("checking for validation errors");
		if (bResult.hasErrors()) {
			log.debug("form has errors");
			m.put("pw", pw);
			m.put("researchOutputTypeMap", this.getResearchOutputTypeMap());
			m.put("survey", survey);
			return new ModelAndView("survey", m);
		}

		try {
			log.debug("sending e-mail");
			this.emailUtil.sendSurveyEmail(person.getFullName(),
					person.getEmail(), pw, survey);
		} catch (Exception e) {
			log.error(
					"Failed to send survey response email from "
							+ person.getEmail(), e);
		}

		try {
			log.debug("storing survey in database");
			this.storeSurvey(survey, pw, person);
		} catch (Exception e) {
			log.error(
					"Failed to store survey in database from "
							+ person.getEmail(), e);
		}
		log.debug("leaving process_survey");
		return new ModelAndView("survey_response");
	}

	@RequestMapping(value = "survey", method = RequestMethod.GET)
	public ModelAndView show_survey(
			@RequestParam(value = "pCode", required = false) String projectCode,
			HttpServletRequest request) throws Exception {

		Map<String, Object> m = new HashMap<String, Object>();
		Person person = (Person) request.getAttribute("person");
		if (person == null) {
			m.put("error_message",
					"Cannot display survey: You don't seem to have a cluster account. "
							+ "Only researchers with a cluster account can view the survey.");
		} else if (!person.isResearcher()) {
			m.put("error_message", adviserWarning);
		} else if (projectCode == null) {
			m.put("error_message",
					"Cannot display survey: No project code specified");
		} else {
			try {
				System.err.println(projectCode);
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
			} catch (Exception e) {
				m.put("error_message", e.getMessage());
			}
		}
		return new ModelAndView("survey", m);
	}
	
}
