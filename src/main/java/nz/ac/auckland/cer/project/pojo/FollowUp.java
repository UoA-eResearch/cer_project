package nz.ac.auckland.cer.project.pojo;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class FollowUp implements Serializable {

    private Integer adviserId;
    private Integer researcherId;
    private String researcherName;
    private String adviserName;
    private String attachmentDescription;
    private String attachmentLink;
    private List<Attachment> attachments;
    private String date;
    private Integer id;
    private String notes;
    private Integer projectId;

    public FollowUp() {

        attachments = new LinkedList<Attachment>();
    }

    public Integer getAdviserId() {

        return adviserId;
    }

    public String getAdviserName() {

        return adviserName;
    }

    public String getAttachmentDescription() {

        return attachmentDescription;
    }

    public String getAttachmentLink() {

        return attachmentLink;
    }

    public List<Attachment> getAttachments() {

        return attachments;
    }

    public String getDate() {

        return date;
    }

    public Integer getId() {

        return id;
    }

    public String getNotes() {

        return notes;
    }

    public Integer getProjectId() {

        return projectId;
    }

    public void setAdviserId(
            final Integer adviserId) {

        this.adviserId = adviserId;
    }

    public void setAdviserName(
            final String adviserName) {

        this.adviserName = adviserName;
    }

    public void setAttachmentDescription(
            final String attachmentDescription) {

        this.attachmentDescription = attachmentDescription;
    }

    public void setAttachmentLink(
            final String attachmentLink) {

        this.attachmentLink = attachmentLink;
    }

    public void setAttachments(
            final List<Attachment> attachments) {

        this.attachments = attachments;
    }

    public void setDate(
            final String date) {

        this.date = date;
    }

    public void setId(
            final Integer id) {

        this.id = id;
    }

    public void setNotes(
            final String notes) {

        this.notes = notes;
    }

    public void setProjectId(
            final Integer projectId) {

        this.projectId = projectId;
    }

    public Integer getResearcherId() {

        return researcherId;
    }

    public void setResearcherId(
            Integer researcherId) {

        this.researcherId = researcherId;
    }

    public String getResearcherName() {

        return researcherName;
    }

    public void setResearcherName(
            String researcherName) {

        this.researcherName = researcherName;
    }

}
