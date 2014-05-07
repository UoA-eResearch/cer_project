package nz.ac.auckland.cer.project.pojo;

public class ProjectFacility {

	private Integer facilityId;
	private Integer projectId;
	private String facilityName;

	public ProjectFacility() {
		
	}
	
	public ProjectFacility(Integer facilityId) {
		this.facilityId = facilityId;
	}
	
	public Integer getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(Integer facilityId) {
		this.facilityId = facilityId;
	}

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public String getFacilityName() {
		return facilityName;
	}

	public void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
	}

}
