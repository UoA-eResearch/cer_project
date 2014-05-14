package nz.ac.auckland.cer.project.pojo;

import java.io.Serializable;

public class APLink implements Serializable {

    private Adviser adviser;
    private Integer adviserId;
    private Integer adviserRoleId;
    private String adviserRoleName;
    private String notes;
    private Integer projectId;

    public Adviser getAdviser() {
        return adviser;
    }

    public Integer getAdviserId() {
        return adviserId;
    }

    public Integer getAdviserRoleId() {
        return adviserRoleId;
    }

    public String getAdviserRoleName() {
        return adviserRoleName;
    }

    public String getNotes() {
        return notes;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setAdviser(final Adviser adviser) {
        this.adviser = adviser;
    }

    public void setAdviserId(final Integer adviserId) {
        this.adviserId = adviserId;
    }

    public void setAdviserRoleId(final Integer adviserRoleId) {
        this.adviserRoleId = adviserRoleId;
    }

    public void setAdviserRoleName(final String adviserRoleName) {
        this.adviserRoleName = adviserRoleName;
    }

    public void setNotes(final String notes) {
        this.notes = notes;
    }

    public void setProjectId(final Integer projectId) {
        this.projectId = projectId;
    }

}
