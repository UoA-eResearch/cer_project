package nz.ac.auckland.cer.project.pojo;

import java.io.Serializable;

public class RPLink implements Serializable {

    private String notes;
    private Integer projectId;
    private Researcher researcher;
    private Integer researcherId;
    private Integer researcherRoleId;
    private String researcherRoleName;

    public String getNotes() {
        return notes;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public Researcher getResearcher() {
        return researcher;
    }

    public Integer getResearcherId() {
        return researcherId;
    }

    public Integer getResearcherRoleId() {
        return researcherRoleId;
    }

    public String getResearcherRoleName() {
        return researcherRoleName;
    }

    public void setNotes(final String notes) {
        this.notes = notes;
    }

    public void setProjectId(final Integer projectId) {
        this.projectId = projectId;
    }

    public void setResearcher(final Researcher researcher) {
        this.researcher = researcher;
    }

    public void setResearcherId(final Integer researcherId) {
        this.researcherId = researcherId;
    }

    public void setResearcherRoleId(final Integer researcherRoleId) {
        this.researcherRoleId = researcherRoleId;
    }

    public void setResearcherRoleName(final String researcherRoleName) {
        this.researcherRoleName = researcherRoleName;
    }

}
