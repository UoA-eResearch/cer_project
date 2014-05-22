package nz.ac.auckland.cer.project.pojo;

public class Project {

    private Integer id;
    private String projectCode = "";
    private Integer projectTypeId = 1;
    private Integer statusId = 7;
    private String statusName;
    private String name;
    private String description;
    private String hostInstitution = "";
    private String startDate = "";
    private String nextReviewDate = "";
    private String nextFollowUpDate = "";
    private String endDate = "";
    private String notes = "";
    private String todo = "";
    private String requirements = "";
    private String projectTypeName;
    private String lastModified;

    public Integer getId() {

        return id;
    }

    public void setId(
            Integer id) {

        this.id = id;
    }

    public Integer getProjectTypeId() {

        return projectTypeId;
    }

    public void setProjectTypeId(
            Integer projectTypeId) {

        this.projectTypeId = projectTypeId;
    }

    public Integer getStatusId() {

        return statusId;
    }

    public void setStatusId(
            Integer statusId) {

        this.statusId = statusId;
    }

    public String getProjectCode() {

        return projectCode;
    }

    public void setProjectCode(
            String projectCode) {

        this.projectCode = projectCode;
    }

    public String getProjectTypeName() {

        return projectTypeName;
    }

    public void setProjectTypeName(
            String projectTypeName) {

        this.projectTypeName = projectTypeName;
    }

    public String getName() {

        return name;
    }

    public void setName(
            String name) {

        this.name = name;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(
            String description) {

        this.description = description;
    }

    public String getHostInstitution() {

        return hostInstitution;
    }

    public void setHostInstitution(
            String hostInstitution) {

        this.hostInstitution = hostInstitution;
    }

    public String getStartDate() {

        return startDate;
    }

    public void setStartDate(
            String startDate) {

        this.startDate = startDate;
    }

    public String getEndDate() {

        return endDate;
    }

    public void setEndDate(
            String endDate) {

        this.endDate = endDate;
    }

    public String getNotes() {

        return notes;
    }

    public void setNotes(
            String notes) {

        this.notes = notes;
    }

    public String getNextReviewDate() {

        return nextReviewDate;
    }

    public void setNextReviewDate(
            String nextReviewDate) {

        this.nextReviewDate = nextReviewDate;
    }

    public String getNextFollowUpDate() {

        return nextFollowUpDate;
    }

    public void setNextFollowUpDate(
            String nextFollowUpDate) {

        this.nextFollowUpDate = nextFollowUpDate;
    }

    public String getRequirements() {

        return requirements;
    }

    public void setRequirements(
            String requirements) {

        this.requirements = requirements;
    }

    public String getTodo() {

        return todo;
    }

    public void setTodo(
            String todo) {

        this.todo = todo;
    }

    public Integer getProjectId() {

        return this.id;
    }

    public String getStatusName() {

        return statusName;
    }

    public void setStatusName(
            String statusName) {

        this.statusName = statusName;
    }

    public String getLastModified() {

        return lastModified;
    }

    public void setLastModified(
            String lastModified) {

        this.lastModified = lastModified;
    }

}
