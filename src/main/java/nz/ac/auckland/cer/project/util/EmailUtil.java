package nz.ac.auckland.cer.project.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nz.ac.auckland.cer.common.util.TemplateEmail;
import nz.ac.auckland.cer.project.pojo.Project;
import nz.ac.auckland.cer.project.pojo.ProjectRequest;
import nz.ac.auckland.cer.project.pojo.ProjectWrapper;
import nz.ac.auckland.cer.project.pojo.RPLink;
import nz.ac.auckland.cer.project.pojo.ResearchOutput;
import nz.ac.auckland.cer.project.pojo.Researcher;
import nz.ac.auckland.cer.project.pojo.survey.PerfImpBigger;
import nz.ac.auckland.cer.project.pojo.survey.PerfImpFaster;
import nz.ac.auckland.cer.project.pojo.survey.PerfImpMore;
import nz.ac.auckland.cer.project.pojo.survey.Survey;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

public class EmailUtil {

	@Autowired
	private AffiliationUtil affUtil;
	private String emailFrom;
	private String emailTo;
	private final Logger log = Logger.getLogger(EmailUtil.class.getName());
	private Resource membershipRequestEmailBodyResource;
	private String membershipRequestEmailSubject;
	private Resource membershipRequestProblemEmailBodyResource;
	private Resource newFollowUpEmailBodyResource;
	private String newFollowUpEmailSubject;
	private Resource newResearchOutputEmailBodyResource;
	private String newResearchOutputEmailSubject;
	private Resource otherAffiliationEmailBodyResource;
	private String otherAffiliationEmailSubject;
	private String projectBaseUrl;
	private Resource projectRequestEmailBodyResource;
	private String projectRequestEmailSubject;
	private Resource projectRequestWithSuperviserEmailBodyResource;
	private String replyTo;
	private Resource surveyNoticeBodyResource;
	private String surveyNoticeEmailSubject;
	@Autowired
	private TemplateEmail templateEmail;

	public void sendMembershipRequestRequestEmail(ProjectWrapper pw,
			String researcherName, String researcherEmail) throws Exception {

		Project p = pw.getProject();
		String projectOwnerEmail = null;
		String problem = null;
		String cc = null;
		Resource emailBodyResource = this.membershipRequestEmailBodyResource;

		Map<String, String> templateParams = new HashMap<String, String>();
		templateParams.put("__RESEARCHER_NAME__", researcherName);
		templateParams.put("__RESEARCHER_EMAIL__", researcherEmail);
		templateParams.put("__PROJECT_TITLE__", p.getName());
		templateParams.put("__PROJECT_DESCRIPTION__", p.getDescription());
		templateParams.put("__PROJECT_CODE__", p.getProjectCode());

		Researcher projectOwner = this.getProjectOwner(pw);
		if (projectOwner == null) {
			problem = "No project owner is registered with this project.";
		} else {
			projectOwnerEmail = projectOwner.getEmail();
			if (projectOwnerEmail == null || projectOwnerEmail.trim().isEmpty()) {
				problem = "No e-mail address is registered with the owner of this project.";
			}
		}

		if (problem == null) {
			String ownerFullName = projectOwner.getFullName();
			if (ownerFullName != null) {
				templateParams.put("__PROJECT_OWNER_FIRST_NAME__",
						ownerFullName.split(" ")[0]);
			}
			cc = projectOwner.getEmail();
		} else {
			templateParams.put("__PROBLEM__", problem);
			templateParams.put("__PROJECT_LINK__",
					this.projectBaseUrl + p.getId());
			emailBodyResource = this.membershipRequestProblemEmailBodyResource;
		}

		try {
			this.templateEmail.sendFromResource(this.emailFrom, this.emailTo,
					cc, this.replyTo, this.membershipRequestEmailSubject,
					emailBodyResource, templateParams);
		} catch (Exception e) {
			log.error("Failed to send project membership request email", e);
			throw new Exception(
					"Failed to notify CeR staff about the project membership request");
		}
	}

	public void sendNewFollowUpEmail(String researcherName,
			String researcherEmail, String followUp, Integer projectId)
			throws Exception {

		Map<String, String> templateParams = new HashMap<String, String>();
		templateParams.put("__RESEARCHER_NAME__", researcherName);
		templateParams.put("__RESEARCHER_EMAIL__", researcherEmail);
		templateParams.put("__FOLLOW_UP__", followUp);
		templateParams.put("__PROJECT_LINK__", this.projectBaseUrl + projectId);
		try {
			this.templateEmail.sendFromResource(this.emailFrom, this.emailTo,
					null, this.replyTo, this.newFollowUpEmailSubject,
					this.newFollowUpEmailBodyResource, templateParams);
		} catch (Exception e) {
			log.error("Failed to send new followup email.", e);
			throw new Exception(
					"Failed to notify CeR staff about the new feedback.");
		}
	}

	public void sendNewResearchOutputEmail(String researcherName,
			String researcherEmail, String researchOutputType,
			String researchOutputDescription, Integer projectId)
			throws Exception {

		Map<String, String> templateParams = new HashMap<String, String>();
		templateParams.put("__RESEARCHER_NAME__", researcherName);
		templateParams.put("__RESEARCHER_EMAIL__", researcherEmail);
		templateParams.put("__RESEARCH_OUTPUT_TYPE__", researchOutputType);
		templateParams.put("__RESEARCH_OUTPUT_DESCRIPTION__",
				researchOutputDescription);
		templateParams.put("__PROJECT_LINK__", this.projectBaseUrl + projectId);
		try {
			this.templateEmail.sendFromResource(this.emailFrom, this.emailTo,
					null, this.replyTo, this.newResearchOutputEmailSubject,
					this.newResearchOutputEmailBodyResource, templateParams);
		} catch (Exception e) {
			log.error("Failed to send new followup email.", e);
			throw new Exception(
					"Failed to notify CeR staff about the new feedback.");
		}
	}

	public void sendOtherAffiliationEmail(String institution, String division,
			String department, String researcherEmail) throws Exception {

		Map<String, String> templateParams = new HashMap<String, String>();
		templateParams.put("__INSTITUTION__", institution);
		templateParams.put("__DIVISION__", division);
		templateParams.put("__DEPARTMENT__", department);
		templateParams.put("__RESEARCHER_EMAIL__", researcherEmail);
		try {
			this.templateEmail.sendFromResource(this.emailFrom, this.emailTo,
					null, this.replyTo, this.otherAffiliationEmailSubject,
					this.otherAffiliationEmailBodyResource, templateParams);
		} catch (Exception e) {
			log.error("Failed to send other institution email.", e);
			throw new Exception(
					"Failed to notify CeR staff about the other institution.");
		}
	}

	public void sendProjectRequestEmail(Project p, ProjectRequest pr,
			String researcherName) throws Exception {

		Map<String, String> templateParams = new HashMap<String, String>();
		templateParams.put("__RESEARCHER_NAME__", researcherName);
		templateParams.put("__PROJECT_TITLE__", p.getName());
		templateParams.put("__PROJECT_DESCRIPTION__", p.getDescription());
		templateParams.put("__SCIENCE_STUDY__", pr.getScienceStudyName());
		templateParams.put("__PROJECT_LINK__",
				this.projectBaseUrl + p.getProjectId());
		try {
			this.templateEmail.sendFromResource(this.emailFrom, this.emailTo,
					null, this.replyTo, this.projectRequestEmailSubject,
					this.projectRequestEmailBodyResource, templateParams);
		} catch (Exception e) {
			log.error("Failed to send project request email", e);
			throw new Exception(
					"Failed to notify CeR staff about the new project request");
		}
	}

	public void sendProjectRequestWithSuperviserEmail(Project p,
			ProjectRequest pr, Researcher superviser, String researcherName)
			throws Exception {

		Map<String, String> templateParams = new HashMap<String, String>();
		String extraInfos = "The supervisor does not yet exist in the database.";
		if (superviser != null) {
			extraInfos = "The supervisor already exists in the database.";
			templateParams.put("__SUPERVISER_NAME__", superviser.getFullName());
			templateParams.put("__SUPERVISER_EMAIL__", superviser.getEmail());
			templateParams.put("__SUPERVISER_PHONE__", superviser.getPhone());
			templateParams.put("__SUPERVISER_INSTITUTION__",
					superviser.getInstitution());
			templateParams.put("__SUPERVISER_DIVISION__",
					superviser.getDivision());
			templateParams.put("__SUPERVISER_DEPARTMENT__",
					superviser.getDepartment());
		} else {
			templateParams.put("__SUPERVISER_NAME__", pr.getSuperviserName());
			templateParams.put("__SUPERVISER_EMAIL__", pr.getSuperviserEmail());
			templateParams.put("__SUPERVISER_PHONE__", pr.getSuperviserPhone());
			boolean otherAffil = pr.getSuperviserAffiliation().toLowerCase()
					.equals("other");
			templateParams.put(
					"__SUPERVISER_INSTITUTION__",
					otherAffil ? pr.getSuperviserOtherInstitution()
							: this.affUtil
									.getInstitutionFromAffiliationString(pr
											.getSuperviserAffiliation()));
			templateParams.put(
					"__SUPERVISER_DIVISION__",
					otherAffil ? pr.getSuperviserOtherDivision() : this.affUtil
							.getDivisionFromAffiliationString(pr
									.getSuperviserAffiliation()));
			templateParams.put(
					"__SUPERVISER_DEPARTMENT__",
					otherAffil ? pr.getSuperviserOtherDepartment()
							: this.affUtil
									.getDepartmentFromAffiliationString(pr
											.getSuperviserAffiliation()));
		}
		templateParams.put("__RESEARCHER_NAME__", researcherName);
		templateParams.put("__PROJECT_TITLE__", p.getName());
		templateParams.put("__PROJECT_DESCRIPTION__", p.getDescription());
		templateParams.put("__PROJECT_LINK__", this.projectBaseUrl + p.getId());
		templateParams.put("__SUPERVISER_EXTRA_INFOS__", extraInfos);

		try {
			this.templateEmail.sendFromResource(this.emailFrom, this.emailTo,
					null, this.replyTo, this.projectRequestEmailSubject,
					this.projectRequestWithSuperviserEmailBodyResource,
					templateParams);
		} catch (Exception e) {
			log.error("Failed to send project request email", e);
			throw new Exception(
					"Failed to notify CeR staff about the new project request");
		}
	}

	public void sendSurveyEmail(String researcherName, String researcherEmail,
			ProjectWrapper pw, Survey survey) throws Exception {

		Map<String, String> templateParams = new HashMap<String, String>();
		templateParams.put("__RESEARCHER_NAME__", researcherName);
		templateParams.put("__RESEARCHER_EMAIL__", researcherEmail);
		templateParams
				.put("__PROJECT_CODE__", pw.getProject().getProjectCode());
		templateParams.put("__PROJECT_TITLE__", pw.getProject().getName());
		templateParams.put("__PROJECT_DESCRIPTION__", pw.getProject()
				.getDescription());
		templateParams.put("__PROJECT_LINK__", this.projectBaseUrl
				+ pw.getProject().getId());
		String perfImp = "";
		PerfImpFaster faster = survey.getPerfImpFaster();
		PerfImpBigger bigger = survey.getPerfImpBigger();
		PerfImpMore more = survey.getPerfImpMore();
		if (faster == null && bigger == null && more == null) {
			perfImp = "N/A";
		} else {
			if (faster != null) {
				perfImp += faster.toString() + " ";
			}
			if (bigger != null) {
				perfImp += bigger.toString() + " ";
			}
			if (more != null) {
				perfImp += more.toString();
			}
		}
		templateParams.put("__PERFORMANCE_IMPROVEMENTS__", perfImp);
		templateParams.put("__FUTURE_NEEDS__", survey.getFutureNeeds()
				.toString());
		String tmp = "";
		Integer noResearchOutput = survey.getResearchOutcome()
				.getNoResearchOutput();
		if (noResearchOutput != null && noResearchOutput > 0) {
			tmp = "N/A";
		} else {
			for (ResearchOutput ro : survey.getResearchOutcome()
					.getResearchOutputs()) {
				if (ro.getDescription() != null
						&& !ro.getDescription().trim().isEmpty()) {
					tmp += ro.getDescription()
							+ System.getProperty("line.separator")
							+ System.getProperty("line.separator");
				}
			}
		}
		templateParams.put("__RESEARCH_OUTCOME__", tmp.trim());
		templateParams.put("__FEEDBACK__", survey.getFeedback().toString());

		try {
			this.templateEmail.sendFromResource(this.emailFrom, this.emailTo,
					null, this.replyTo, this.surveyNoticeEmailSubject,
					this.surveyNoticeBodyResource, templateParams);
		} catch (Exception e) {
			log.error("Failed to send survey email.", e);
			throw new Exception(
					"Failed to notify CeR staff about your survey response.");
		}
	}

	public void setEmailFrom(String emailFrom) {

		this.emailFrom = emailFrom;
	}

	public void setEmailTo(String emailTo) {

		this.emailTo = emailTo;
	}

	public void setMembershipRequestEmailBodyResource(
			Resource membershipRequestEmailBodyResource) {

		this.membershipRequestEmailBodyResource = membershipRequestEmailBodyResource;
	}

	public void setMembershipRequestEmailSubject(
			String membershipRequestEmailSubject) {

		this.membershipRequestEmailSubject = membershipRequestEmailSubject;
	}

	public void setNewFollowUpEmailBodyResource(
			Resource newFollowUpEmailBodyResource) {

		this.newFollowUpEmailBodyResource = newFollowUpEmailBodyResource;
	}

	public void setNewFollowUpEmailSubject(String newFollowUpEmailSubject) {

		this.newFollowUpEmailSubject = newFollowUpEmailSubject;
	}

	public void setNewResearchOutputEmailBodyResource(
			Resource newResearchOutputEmailBodyResource) {

		this.newResearchOutputEmailBodyResource = newResearchOutputEmailBodyResource;
	}

	public void setNewResearchOutputEmailSubject(
			String newResearchOutputEmailSubject) {

		this.newResearchOutputEmailSubject = newResearchOutputEmailSubject;
	}

	public void setOtherAffiliationEmailBodyResource(
			Resource otherAffiliationEmailBodyResource) {

		this.otherAffiliationEmailBodyResource = otherAffiliationEmailBodyResource;
	}

	public void setOtherAffiliationEmailSubject(
			String otherAffiliationEmailSubject) {

		this.otherAffiliationEmailSubject = otherAffiliationEmailSubject;
	}

	public void setProjectBaseUrl(String projectBaseUrl) {

		this.projectBaseUrl = projectBaseUrl;
	}

	public void setProjectRequestEmailBodyResource(
			Resource projectRequestEmailBodyResource) {

		this.projectRequestEmailBodyResource = projectRequestEmailBodyResource;
	}

	public void setProjectRequestEmailSubject(String projectRequestEmailSubject) {

		this.projectRequestEmailSubject = projectRequestEmailSubject;
	}

	public void setProjectRequestWithSuperviserEmailBodyResource(
			Resource projectRequestWithSuperviserEmailBodyResource) {

		this.projectRequestWithSuperviserEmailBodyResource = projectRequestWithSuperviserEmailBodyResource;
	}

	public void setReplyTo(String replyTo) {

		this.replyTo = replyTo;
	}

	public void setSurveyNoticeBodyResource(Resource surveyNoticeBodyResource) {

		this.surveyNoticeBodyResource = surveyNoticeBodyResource;
	}

	public void setSurveyNoticeEmailSubject(String surveyNoticeEmailSubject) {

		this.surveyNoticeEmailSubject = surveyNoticeEmailSubject;
	}

	public void setMembershipRequestProblemEmailBodyResource(
			Resource membershipRequestProblemEmailBodyResource) {

		this.membershipRequestProblemEmailBodyResource = membershipRequestProblemEmailBodyResource;
	}

	public String getEmailFrom() {
		return emailFrom;
	}

	public String getEmailTo() {
		return emailTo;
	}

	public String getReplyTo() {
		return replyTo;
	}

	public String getMembershipRequestEmailSubject() {
		return membershipRequestEmailSubject;
	}

	public String getNewFollowUpEmailSubject() {
		return newFollowUpEmailSubject;
	}

	public String getNewResearchOutputEmailSubject() {
		return newResearchOutputEmailSubject;
	}

	public String getOtherAffiliationEmailSubject() {
		return otherAffiliationEmailSubject;
	}

	public String getProjectRequestEmailSubject() {
		return projectRequestEmailSubject;
	}

	public String getSurveyNoticeEmailSubject() {
		return surveyNoticeEmailSubject;
	}

	private Researcher getProjectOwner(ProjectWrapper pw) {

		Researcher r = null;
		if (pw != null) {
			List<RPLink> rpLinks = pw.getRpLinks();
			if (rpLinks != null && rpLinks.size() > 0) {
				for (RPLink tmp : rpLinks) {
					if (tmp.getResearcherRoleId() == 1) {
						r = tmp.getResearcher();
						break;
					}
				}
			}
		}
		return r;
	}
}
