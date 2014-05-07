package nz.ac.auckland.cer.project.pojo;

public class APLink {

    private Integer adviserRoleId;
    private Integer projectId;
    private Integer adviserId;
    private String notes = "";

    public APLink() {

    }

    public APLink(Integer projectId, Integer adviserId, Integer adviserRoleId) {
        this.projectId = projectId;
        this.adviserId = adviserId;
        this.adviserRoleId = adviserRoleId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getAdviserId() {
        return adviserId;
    }

    public void setAdviserId(Integer adviserId) {
        this.adviserId = adviserId;
    }

    public Integer getAdviserRoleId() {
        return adviserRoleId;
    }

    public void setAdviserRoleId(Integer adviserRoleId) {
        this.adviserRoleId = adviserRoleId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

}
