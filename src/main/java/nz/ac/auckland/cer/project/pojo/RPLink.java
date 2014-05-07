package nz.ac.auckland.cer.project.pojo;

public class RPLink {

	private Integer projectId;
	private Integer researcherId;
	private Integer researcherRoleId;
	private String notes = "";

	public RPLink() {

	}

	public RPLink(Integer projectId, Integer researcherId, Integer researcherRoleId) {
		this.projectId = projectId;
		this.researcherId = researcherId;
		this.researcherRoleId = researcherRoleId;
	}

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public Integer getResearcherId() {
		return researcherId;
	}

	public void setResearcherId(Integer researcherId) {
		this.researcherId = researcherId;
	}

	public Integer getResearcherRoleId() {
		return researcherRoleId;
	}

	public void setResearcherRoleId(Integer researcherRoleId) {
		this.researcherRoleId = researcherRoleId;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

}
