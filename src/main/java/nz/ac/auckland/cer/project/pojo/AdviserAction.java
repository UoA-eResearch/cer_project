package nz.ac.auckland.cer.project.pojo;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class AdviserAction implements Serializable {

    private String action;

    private Integer adviserId;
    private String adviserName;
    private String attachmentDescription;
    private String attachmentLink;
    private List<Attachment> attachments;
    private String date;
    private Integer id;
    private Integer projectId;

    public AdviserAction() {
        attachments = new LinkedList<Attachment>();
    }

    public String getAction() {
        return action;
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

    public Integer getProjectId() {
        return projectId;
    }

    public void setAction(final String action) {
        this.action = action;
    }

    public void setAdviserId(final Integer adviserId) {
        this.adviserId = adviserId;
    }

    public void setAdviserName(final String adviserName) {
        this.adviserName = adviserName;
    }

    public void setAttachmentDescription(final String attachmentDescription) {
        this.attachmentDescription = attachmentDescription;
    }

    public void setAttachmentLink(final String attachmentLink) {
        this.attachmentLink = attachmentLink;
    }

    public void setAttachments(final List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public void setDate(final String date) {
        this.date = date;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public void setProjectId(final Integer projectId) {
        this.projectId = projectId;
    }

}
