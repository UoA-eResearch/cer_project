package nz.ac.auckland.cer.project.pojo;

import java.io.Serializable;

public class ResearchOutput implements Serializable {

    private Integer adviserId;
    private String adviserName;
    private String date;
    private String description;
    private Integer id;
    private String link;
    private String projectCode;
    private Integer projectId;
    private Integer researcherId;
    private String researcherName;
    private String type;
    private Integer typeId;

    public Integer getAdviserId() {
        return adviserId;
    }

    public String getAdviserName() {
        return adviserName;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public Integer getId() {
        return id;
    }

    public String getLink() {
        return link;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public Integer getResearcherId() {
        return researcherId;
    }

    public String getResearcherName() {
        return researcherName;
    }

    public String getType() {
        return type;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setAdviserId(final Integer adviserId) {
        this.adviserId = adviserId;
    }

    public void setAdviserName(final String adviserName) {
        this.adviserName = adviserName;
    }

    public void setDate(final String date) {
        this.date = date;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public void setLink(final String link) {
        this.link = link;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public void setProjectId(final Integer projectId) {
        this.projectId = projectId;
    }

    public void setResearcherId(Integer researcherId) {
        this.researcherId = researcherId;
    }

    public void setResearcherName(String researcherName) {
        this.researcherName = researcherName;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public void setTypeId(final Integer typeId) {
        this.typeId = typeId;
    }

}
