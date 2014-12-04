package nz.ac.auckland.cer.project.pojo;

public class ProjectRequest {

	private String projectTitle;
	private String projectDescription;
	private String scienceStudyId;
	private String scienceStudyName;
	private Boolean askForSuperviser;
	// superviser
	private Integer superviserId;
	private String superviserAffiliation;
	private String superviserName;
	private String superviserEmail;
	private String superviserPhone;
	private String superviserOtherInstitution;
	private String superviserOtherDivision;
	private String superviserOtherDepartment;
	// survey
	private String motivation;
	private String otherMotivation;
	private String currentCompEnv;
	private String otherCompEnv;
	private Limitations limitations;
	// funding
	private Boolean funded;
	private String fundingSource;

	public String getMotivation() {

		return motivation;
	}

	public void setMotivation(String motivation) {

		this.motivation = motivation;
	}

	public Limitations getLimitations() {

		return limitations;
	}

	public void setLimitations(Limitations limitations) {

		this.limitations = limitations;
	}

	public String getCurrentCompEnv() {

		return currentCompEnv;
	}

	public void setCurrentCompEnv(String currentCompEnv) {

		this.currentCompEnv = currentCompEnv;
	}

	public String getSuperviserName() {

		return superviserName;
	}

	public void setSuperviserName(String superviserName) {

		this.superviserName = superviserName;
	}

	public String getSuperviserEmail() {

		return superviserEmail;
	}

	public void setSuperviserEmail(String superviserEmail) {

		this.superviserEmail = superviserEmail;
	}

	public String getSuperviserPhone() {

		return superviserPhone;
	}

	public void setSuperviserPhone(String superviserPhone) {

		this.superviserPhone = superviserPhone;
	}

	public String getProjectTitle() {

		return projectTitle;
	}

	public void setProjectTitle(String projectTitle) {

		this.projectTitle = projectTitle;
	}

	public String getProjectDescription() {

		return projectDescription;
	}

	public void setProjectDescription(String projectDescription) {

		this.projectDescription = projectDescription;
	}

	public Boolean getAskForSuperviser() {

		return askForSuperviser;
	}

	public void setAskForSuperviser(Boolean askForSuperviser) {

		this.askForSuperviser = askForSuperviser;
	}

	public String getOtherMotivation() {

		return otherMotivation;
	}

	public void setOtherMotivation(String otherMotivation) {

		this.otherMotivation = otherMotivation;
	}

	public Integer getSuperviserId() {

		return superviserId;
	}

	public void setSuperviserId(Integer superviserId) {

		this.superviserId = superviserId;
	}

	public String getSuperviserAffiliation() {

		return superviserAffiliation;
	}

	public void setSuperviserAffiliation(String superviserAffiliation) {

		this.superviserAffiliation = superviserAffiliation;
	}

	public String getSuperviserOtherInstitution() {

		return superviserOtherInstitution;
	}

	public void setSuperviserOtherInstitution(String superviserOtherInstitution) {

		this.superviserOtherInstitution = superviserOtherInstitution;
	}

	public String getSuperviserOtherDivision() {

		return superviserOtherDivision;
	}

	public void setSuperviserOtherDivision(String superviserOtherDivision) {

		this.superviserOtherDivision = superviserOtherDivision;
	}

	public String getSuperviserOtherDepartment() {

		return superviserOtherDepartment;
	}

	public void setSuperviserOtherDepartment(String superviserOtherDepartment) {

		this.superviserOtherDepartment = superviserOtherDepartment;
	}

	public String getOtherCompEnv() {

		return otherCompEnv;
	}

	public void setOtherCompEnv(String otherCompEnv) {

		this.otherCompEnv = otherCompEnv;
	}

	public String getScienceStudyId() {

		return scienceStudyId;
	}

	public void setScienceStudyId(String scienceStudyId) {

		this.scienceStudyId = scienceStudyId;
	}

	public String getScienceStudyName() {

		return scienceStudyName;
	}

	public void setScienceStudyName(String scienceStudyName) {

		this.scienceStudyName = scienceStudyName;
	}

	public String getFundingSource() {
		return fundingSource;
	}

	public void setFundingSource(String fundingSource) {
		this.fundingSource = fundingSource;
	}

	public Boolean getFunded() {
		return funded;
	}

	public void setFunded(Boolean funded) {
		this.funded = funded;
	}

}
