package nz.ac.auckland.cer.project.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import nz.ac.auckland.cer.common.util.TemplateEmail;
import nz.ac.auckland.cer.project.pojo.Project;
import nz.ac.auckland.cer.project.pojo.ProjectRequest;
import nz.ac.auckland.cer.project.pojo.Researcher;

public class EmailUtil {

    private Logger log = Logger.getLogger(EmailUtil.class.getName());
    @Autowired private TemplateEmail templateEmail;
    @Autowired private AffiliationUtil affUtil;
    private Resource projectRequestEmailBodyResource;
    private Resource projectRequestWithSuperviserEmailBodyResource;
    private Resource membershipRequestEmailBodyResource;
    private Resource otherAffiliationEmailBodyResource;
    private Resource newFollowUpEmailBodyResource;
    private Resource newResearchOutputEmailBodyResource;
    private String projectBaseUrl;
    private String projectRequestEmailSubject;
    private String membershipRequestEmailSubject;
    private String otherAffiliationEmailSubject;
    private String newFollowUpEmailSubject;
    private String newResearchOutputEmailSubject;
    private String emailFrom;
    private String emailTo;
    private String replyTo;

    public void sendProjectRequestEmail(
            Project p,
            String researcherName) throws Exception {

        Map<String, String> templateParams = new HashMap<String, String>();
        templateParams.put("__RESEARCHER_NAME__", researcherName);
        templateParams.put("__PROJECT_TITLE__", p.getName());
        templateParams.put("__PROJECT_DESCRIPTION__", p.getDescription());
        templateParams.put("__PROJECT_LINK__", this.projectBaseUrl + "?id=" + p.getProjectId());
        try {
            this.templateEmail.sendFromResource(this.emailFrom, this.emailTo, null, null,
                    this.projectRequestEmailSubject, this.projectRequestEmailBodyResource, templateParams);
        } catch (Exception e) {
            log.error("Failed to send project request email", e);
            throw new Exception("Failed to notify CeR staff about the new project request");
        }
    }

    public void sendProjectRequestWithSuperviserEmail(
            Project p,
            ProjectRequest pr,
            Researcher superviser,
            String researcherName) throws Exception {

        Map<String, String> templateParams = new HashMap<String, String>();
        String extraInfos = "The supervisor does not yet exist in the database.";
        if (superviser != null) {
            extraInfos = "The supervisor already exists in the database.";
            templateParams.put("__SUPERVISER_NAME__", superviser.getFullName());
            templateParams.put("__SUPERVISER_EMAIL__", superviser.getEmail());
            templateParams.put("__SUPERVISER_PHONE__", superviser.getPhone());
            templateParams.put("__SUPERVISER_INSTITUTION__", superviser.getInstitution());
            templateParams.put("__SUPERVISER_DIVISION__", superviser.getDivision());
            templateParams.put("__SUPERVISER_DEPARTMENT__", superviser.getDepartment());
        } else {
            templateParams.put("__SUPERVISER_NAME__", pr.getSuperviserName());
            templateParams.put("__SUPERVISER_EMAIL__", pr.getSuperviserEmail());
            templateParams.put("__SUPERVISER_PHONE__", pr.getSuperviserPhone());
            boolean otherAffil = pr.getSuperviserAffiliation().toLowerCase().equals("other");
            templateParams.put("__SUPERVISER_INSTITUTION__", otherAffil ? pr.getSuperviserOtherInstitution()
                    : this.affUtil.getInstitutionFromAffiliationString(pr.getSuperviserAffiliation()));
            templateParams.put(
                    "__SUPERVISER_DIVISION__",
                    otherAffil ? pr.getSuperviserOtherDivision() : this.affUtil.getDivisionFromAffiliationString(pr
                            .getSuperviserAffiliation()));
            templateParams.put("__SUPERVISER_DEPARTMENT__", otherAffil ? pr.getSuperviserOtherDepartment()
                    : this.affUtil.getDepartmentFromAffiliationString(pr.getSuperviserAffiliation()));
        }
        templateParams.put("__RESEARCHER_NAME__", researcherName);
        templateParams.put("__PROJECT_TITLE__", p.getName());
        templateParams.put("__PROJECT_DESCRIPTION__", p.getDescription());
        templateParams.put("__PROJECT_LINK__", this.projectBaseUrl + "?id=" + p.getId());
        templateParams.put("__SUPERVISER_EXTRA_INFOS__", extraInfos);

        try {
            this.templateEmail
                    .sendFromResource(this.emailFrom, this.emailTo, null, null, this.projectRequestEmailSubject,
                            this.projectRequestWithSuperviserEmailBodyResource, templateParams);
        } catch (Exception e) {
            log.error("Failed to send project request email", e);
            throw new Exception("Failed to notify CeR staff about the new project request");
        }
    }

    public void sendMembershipRequestRequestEmail(
            Project p,
            String researcherName) throws Exception {

        Map<String, String> templateParams = new HashMap<String, String>();
        templateParams.put("__RESEARCHER_NAME__", researcherName);
        templateParams.put("__PROJECT_TITLE__", p.getName());
        templateParams.put("__PROJECT_DESCRIPTION__", p.getDescription());
        templateParams.put("__PROJECT_LINK__", this.projectBaseUrl + "?id=" + p.getId());
        try {
            this.templateEmail.sendFromResource(this.emailFrom, this.emailTo, null, null,
                    this.membershipRequestEmailSubject, this.membershipRequestEmailBodyResource, templateParams);
        } catch (Exception e) {
            log.error("Failed to send project membership request email", e);
            throw new Exception("Failed to notify CeR staff about the project membership request");
        }
    }

    public void sendOtherAffiliationEmail(
            String institution,
            String division,
            String department) throws Exception {

        Map<String, String> templateParams = new HashMap<String, String>();
        templateParams.put("__INSTITUTION__", institution);
        templateParams.put("__DIVISION__", division);
        templateParams.put("__DEPARTMENT__", department);
        try {
            this.templateEmail.sendFromResource(this.emailFrom, this.emailTo, null, null,
                    this.otherAffiliationEmailSubject, this.otherAffiliationEmailBodyResource, templateParams);
        } catch (Exception e) {
            log.error("Failed to send other institution email.", e);
            throw new Exception("Failed to notify CeR staff about the other institution.");
        }
    }

    public void sendNewFollowUpEmail(
            String researcherName,
            String followUp,
            Integer projectId) throws Exception {

        Map<String, String> templateParams = new HashMap<String, String>();
        templateParams.put("__RESEARCHER_NAME__", researcherName);
        templateParams.put("__FOLLOW_UP__", followUp);
        templateParams.put("__PROJECT_LINK__", this.projectBaseUrl + "?id=" + projectId);
        try {
            this.templateEmail.sendFromResource(this.emailFrom, this.emailTo, null, null, this.newFollowUpEmailSubject,
                    this.newFollowUpEmailBodyResource, templateParams);
        } catch (Exception e) {
            log.error("Failed to send new followup email.", e);
            throw new Exception("Failed to notify CeR staff about the new feedback.");
        }
    }

    public void sendNewResearchOutputEmail(
            String researcherName,
            String researchOutputType,
            String researchOutputDescription,
            Integer projectId) throws Exception {

        Map<String, String> templateParams = new HashMap<String, String>();
        templateParams.put("__RESEARCHER_NAME__", researcherName);
        templateParams.put("__RESEARCH_OUTPUT_TYPE__", researchOutputType);
        templateParams.put("__RESEARCH_OUTPUT_DESCRIPTION__", researchOutputDescription);
        templateParams.put("__PROJECT_LINK__", this.projectBaseUrl + "?id=" + projectId);
        try {
            this.templateEmail.sendFromResource(this.emailFrom, this.emailTo, null, null,
                    this.newResearchOutputEmailSubject, this.newResearchOutputEmailBodyResource, templateParams);
        } catch (Exception e) {
            log.error("Failed to send new followup email.", e);
            throw new Exception("Failed to notify CeR staff about the new feedback.");
        }
    }

    public void setEmailFrom(
            String emailFrom) {

        this.emailFrom = emailFrom;
    }

    public void setEmailTo(
            String emailTo) {

        this.emailTo = emailTo;
    }

    public void setProjectRequestEmailSubject(
            String projectRequestEmailSubject) {

        this.projectRequestEmailSubject = projectRequestEmailSubject;
    }

    public void setProjectRequestEmailBodyResource(
            Resource projectRequestEmailBodyResource) {

        this.projectRequestEmailBodyResource = projectRequestEmailBodyResource;
    }

    public void setMembershipRequestEmailSubject(
            String membershipRequestEmailSubject) {

        this.membershipRequestEmailSubject = membershipRequestEmailSubject;
    }

    public void setMembershipRequestEmailBodyResource(
            Resource membershipRequestEmailBodyResource) {

        this.membershipRequestEmailBodyResource = membershipRequestEmailBodyResource;
    }

    public void setProjectRequestWithSuperviserEmailBodyResource(
            Resource projectRequestWithSuperviserEmailBodyResource) {

        this.projectRequestWithSuperviserEmailBodyResource = projectRequestWithSuperviserEmailBodyResource;
    }

    public void setNewFollowUpEmailBodyResource(
            Resource newFollowUpEmailBodyResource) {

        this.newFollowUpEmailBodyResource = newFollowUpEmailBodyResource;
    }

    public void setNewFollowUpEmailSubject(
            String newFollowUpEmailSubject) {

        this.newFollowUpEmailSubject = newFollowUpEmailSubject;
    }

    public void setOtherAffiliationEmailBodyResource(
            Resource otherAffiliationEmailBodyResource) {

        this.otherAffiliationEmailBodyResource = otherAffiliationEmailBodyResource;
    }

    public void setOtherAffiliationEmailSubject(
            String otherAffiliationEmailSubject) {

        this.otherAffiliationEmailSubject = otherAffiliationEmailSubject;
    }

    public void setNewResearchOutputEmailBodyResource(
            Resource newResearchOutputEmailBodyResource) {

        this.newResearchOutputEmailBodyResource = newResearchOutputEmailBodyResource;
    }

    public void setNewResearchOutputEmailSubject(
            String newResearchOutputEmailSubject) {

        this.newResearchOutputEmailSubject = newResearchOutputEmailSubject;
    }

    public void setProjectBaseUrl(
            String projectBaseUrl) {

        this.projectBaseUrl = projectBaseUrl;
    }

    public void setReplyTo(
            String replyTo) {

        this.replyTo = replyTo;
    }

}
