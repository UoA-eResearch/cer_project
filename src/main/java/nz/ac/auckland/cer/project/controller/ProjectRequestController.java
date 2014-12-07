package nz.ac.auckland.cer.project.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import nz.ac.auckland.cer.project.dao.ProjectDatabaseDao;
import nz.ac.auckland.cer.project.pojo.APLink;
import nz.ac.auckland.cer.project.pojo.Affiliation;
import nz.ac.auckland.cer.project.pojo.Limitations;
import nz.ac.auckland.cer.project.pojo.Project;
import nz.ac.auckland.cer.project.pojo.ProjectFacility;
import nz.ac.auckland.cer.project.pojo.ProjectProperty;
import nz.ac.auckland.cer.project.pojo.ProjectRequest;
import nz.ac.auckland.cer.project.pojo.ProjectWrapper;
import nz.ac.auckland.cer.project.pojo.RPLink;
import nz.ac.auckland.cer.project.pojo.Researcher;
import nz.ac.auckland.cer.project.pojo.ScienceStudy;
import nz.ac.auckland.cer.project.util.AffiliationUtil;
import nz.ac.auckland.cer.project.util.EmailUtil;
import nz.ac.auckland.cer.project.util.Person;
import nz.ac.auckland.cer.project.validation.ProjectRequestValidator;

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
public class ProjectRequestController {

	@Autowired
	private AffiliationUtil affilUtil;
	@Autowired
	private EmailUtil emailUtil;
	@Autowired
	private ProjectDatabaseDao projectDao;
	private final String adviserWarning = "In our books you are an adviser but not a researcher. Only researchers may use this tool.";
	private String defaultHostInstitution;
	private Integer initialResearcherRoleOnProject;
	private final Logger log = Logger.getLogger(ProjectRequestController.class
			.getName());
	private String redirectIfNoAccount;

	/**
	 * Check whether we need to ask for superviser information or not.
	 */
	private boolean askForSuperviser(Person p) throws Exception {

		return p.isResearcher() && (p.getInstitutionalRoleId() > 1);
	}

	private void augmentModel(Map<String, Object> mav) {

		Affiliation[] afs = null;
		Map<Integer, String> superviserMap = null;
		Map<Integer, String> scienceStuyMap = null;
		String errorMessage = "";

		try {
			afs = this.projectDao.getAffiliations();
			if (afs == null || afs.length == 0) {
				throw new Exception();
			}
			List<String> tmp = this.affilUtil.getAffiliationStrings(afs);
			mav.put("affiliations", tmp);
		} catch (Exception e) {
			String error = "Failed to load affiliations.";
			errorMessage += "Internal Error: " + error;
			log.error(error, e);
		}

		try {
			superviserMap = this.getSortedSuperviserMap();
			mav.put("superviserDropdownMap", superviserMap);
		} catch (Exception e) {
			String error = "Failed to load superviser map.";
			errorMessage += "Internal Error: " + error;
			log.error(error, e);
		}

		try {
			scienceStuyMap = this.getScienceStudies();
			mav.put("scienceStudies", scienceStuyMap);
		} catch (Exception e) {
			String error = "Failed to load science study map.";
			errorMessage += "Internal Error: " + error;
			log.error(error, e);
		}

		if (errorMessage.trim().length() > 0) {
			mav.put("unexpected_error", errorMessage);
		}
	}

	private Project createProject(ProjectRequest pr, Researcher superviser,
			Person researcher) throws Exception {

		ProjectWrapper pw = new ProjectWrapper();
		Project p = new Project();
		List<RPLink> rpLinks = new LinkedList<RPLink>();
		ProjectFacility pf = new ProjectFacility(1);
		RPLink rpl = new RPLink();
		rpl.setResearcherId(researcher.getId());
		rpl.setResearcherRoleId(this.initialResearcherRoleOnProject);
		rpLinks.add(rpl);
		if (superviser != null) {
			// TODO: replace 2 with configured value for "Superviser"
			rpl = new RPLink();
			rpl.setResearcherId(superviser.getId());
			rpl.setResearcherRoleId(2);
			rpLinks.add(rpl);
		}
		APLink apl = new APLink();
		apl.setAdviserId(6);
		apl.setAdviserRoleId(1);

		p.setName(pr.getProjectTitle());
		p.setDescription(pr.getProjectDescription());
		String inst = researcher.getInstitution();
		if (inst == null || inst.trim().isEmpty()) {
			p.setHostInstitution(this.defaultHostInstitution);
		} else {
			p.setHostInstitution(inst);
		}

		if (pr.getFunded()) {
			p.setNotes("Funding Source: " + pr.getFundingSource());
		} else {
			p.setNotes("Funding Source: None");
		}

		Calendar now = Calendar.getInstance();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		p.setStartDate(df.format(now.getTime()));
		now.add(Calendar.MONTH, 3);
		p.setNextFollowUpDate(df.format(now.getTime()));
		now.add(Calendar.MONTH, 9);
		p.setNextReviewDate(df.format(now.getTime()));

		pw.setProject(p);
		pw.setProjectFacilities(new LinkedList<ProjectFacility>(Arrays
				.asList(pf)));
		pw.setRpLinks(rpLinks);
		pw.setApLinks(new LinkedList<APLink>(Arrays.asList(apl)));

		return this.projectDao.createProject(pw);
	}

	private void createProjectProperties(Project project, ProjectRequest pr)
			throws Exception {

		ProjectProperty pp = new ProjectProperty();
		pp.setFacilityId(1);
		pp.setProjectId(project.getId());

		String scienceStudyId = pr.getScienceStudyId();
		String scienceStudyName = "Other (Requires manual intervention. "
				+ "No science_domain and science_study project properties have been added to project database)";
		if (Integer.valueOf(scienceStudyId) > 0) {
			scienceStudyName = this.projectDao
					.getScienceStudyNameForId(scienceStudyId);
			if (scienceStudyName != null && !scienceStudyName.isEmpty()) {
				String scienceDomainName = this.projectDao
						.getScienceDomainNameForScienceStudyId(scienceStudyId);
				if (scienceDomainName != null && !scienceDomainName.isEmpty()) {
					pp.setPropname("science_domain");
					pp.setPropvalue(scienceDomainName);
					this.projectDao.createProjectProperty(pp);
					pp.setPropname("science_study");
					pp.setPropvalue(scienceStudyName);
					this.projectDao.createProjectProperty(pp);
					pr.setScienceStudyName(scienceStudyName);
				} else {
					this.log.warn("No science domain found for science study id "
							+ pr.getScienceStudyId()
							+ " (project id: "
							+ project.getProjectId() + ")");
				}
			} else {
				this.log.warn("No science study name found for id "
						+ pr.getScienceStudyId() + " (project id: "
						+ project.getProjectId() + ")");
			}
		}
		pr.setScienceStudyName(scienceStudyName);

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

	private Map<Integer, String> getSortedSuperviserMap() throws Exception {

		Map<Integer, String> superviserMap = new LinkedHashMap<Integer, String>();
		Researcher[] staffOrPostDocs = this.projectDao.getAllStaffOrPostDocs();
		for (Researcher r : staffOrPostDocs) {
			String affilString = affilUtil.createAffiliationString(
					r.getInstitution(), r.getDivision(), r.getDepartment());
			superviserMap.put(r.getId(), r.getFullName() + " (" + affilString
					+ ")");
		}
		return superviserMap;
	}

	private Map<Integer, String> getScienceStudies() throws Exception {

		Map<Integer, String> scienceStudyMap = new LinkedHashMap<Integer, String>();
		List<ScienceStudy> scienceStudies = this.projectDao.getScienceStudies();
		for (ScienceStudy ss : scienceStudies) {
			scienceStudyMap.put(ss.getId(), ss.getName());
		}
		return scienceStudyMap;
	}

	/**
	 * Configure validator for cluster project and membership request form
	 */
	@InitBinder
	protected void initBinder(WebDataBinder binder) {

		binder.addValidators(new ProjectRequestValidator());
	}

	@RequestMapping(value = "request_project", method = RequestMethod.POST)
	public ModelAndView processProjectRequestForm(
			@Valid @ModelAttribute("projectrequest") ProjectRequest pr,
			BindingResult bResult, HttpServletRequest request) throws Exception {

		Map<String, Object> m = new HashMap<String, Object>();
		if (bResult.hasErrors()) {
			m.put("projectrequest", pr);
			this.augmentModel(m);
			return new ModelAndView("request_project", m);
		}
		try {
			Person person = (Person) request.getAttribute("person");
			if (person == null) {
				return new ModelAndView(new RedirectView(redirectIfNoAccount,
						false));
			} else if (!person.isResearcher()) {
				m.put("error_message", adviserWarning);
				return new ModelAndView("request_project_response", m);
			}
			Researcher superviser = null;
			if (pr.getSuperviserId() != null && pr.getSuperviserId() > 0) {
				superviser = this.projectDao.getResearcherForId(pr
						.getSuperviserId());
			} else {
				if (pr.getSuperviserAffiliation() != null
						&& pr.getSuperviserAffiliation().toLowerCase()
								.equals("other")) {
					this.emailUtil.sendOtherAffiliationEmail(
							pr.getSuperviserOtherInstitution(),
							pr.getSuperviserOtherDivision(),
							pr.getSuperviserOtherDepartment(),
							person.getEmail());
				}
			}
			Project p = this.createProject(pr, superviser, person);
			this.createProjectProperties(p, pr);
			if (this.askForSuperviser(person)) {
				this.emailUtil.sendProjectRequestWithSuperviserEmail(p, pr,
						superviser, person.getFullName());
			} else {
				this.emailUtil.sendProjectRequestEmail(p, pr,
						person.getFullName());
			}
			return new ModelAndView("request_project_response");
		} catch (Exception e) {
			log.error("Failed to create project", e);
			bResult.addError(new ObjectError(bResult.getObjectName(), e
					.getMessage()));
			return new ModelAndView("request_project");
		}
	}

	public void setDefaultHostInstitution(String defaultHostInstitution) {

		this.defaultHostInstitution = defaultHostInstitution;
	}

	public void setInitialResearcherRoleOnProject(
			Integer initialResearcherRoleOnProject) {

		this.initialResearcherRoleOnProject = initialResearcherRoleOnProject;
	}

	public void setRedirectIfNoAccount(String redirectIfNoAccount) {

		this.redirectIfNoAccount = redirectIfNoAccount;
	}

	@RequestMapping(value = "request_project", method = RequestMethod.GET)
	public ModelAndView showProjectRequestForm(HttpServletRequest request)
			throws Exception {

		ProjectRequest pr = new ProjectRequest();
		Person person = (Person) request.getAttribute("person");
		if (person == null) {
			System.err.println("person is null!");
			return new ModelAndView(
					new RedirectView(redirectIfNoAccount, false));
		} else if (!person.isResearcher()) {
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("error_message", adviserWarning);
			return new ModelAndView("request_project_response", m);
		}
		pr.setAskForSuperviser(this.askForSuperviser(person));
		ModelAndView mav = new ModelAndView("request_project");
		mav.addObject("projectrequest", pr);
		this.augmentModel(mav.getModel());
		return mav;
	}

}
