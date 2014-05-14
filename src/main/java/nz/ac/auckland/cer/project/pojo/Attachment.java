package nz.ac.auckland.cer.project.pojo;

import java.io.Serializable;

public class Attachment implements Serializable {

    Integer adviserActionId;
    String date;
    String description;
    Integer followUpId;
    Integer id;
    String link;
    Integer projectId;
    Integer reviewId;

    public Integer getAdviserActionId() {
        return adviserActionId;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public Integer getFollowUpId() {
        return followUpId;
    }

    public Integer getId() {
        return id;
    }

    public String getLink() {
        return link;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public Integer getReviewId() {
        return reviewId;
    }

    public void setAdviserActionId(final Integer adviserActionId) {
        this.adviserActionId = adviserActionId;
    }

    public void setDate(final String date) {
        this.date = date;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public void setFollowUpId(final Integer followUpId) {
        this.followUpId = followUpId;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public void setLink(final String link) {
        this.link = link;
    }

    public void setProjectId(final Integer projectId) {
        this.projectId = projectId;
    }

    public void setReviewId(final Integer reviewId) {
        this.reviewId = reviewId;
    }

}
