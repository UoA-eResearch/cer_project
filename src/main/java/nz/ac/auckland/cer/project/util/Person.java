package nz.ac.auckland.cer.project.util;

import nz.ac.auckland.cer.project.pojo.Adviser;
import nz.ac.auckland.cer.project.pojo.Researcher;

/*
 * Abstract away the small differences between a researcher and an adviser
 */
public class Person {

    private String department;
    private String division;
    private String email;
    private String endDate = ""; // empty string to avoid null in database which
    private String fullName;
    // fields common for adviser and researcher
    private Integer id;
    private String institution;
    private Integer institutionalRoleId;
    private String institutionalRoleName;
    private Boolean isResearcher;
    // causes problems with other apps
    private String notes;

    private String otherInstitution;
    private String phone;
    private String pictureUrl;
    // fields that only researchers have
    private String preferredName;

    // fields that only advisers have

    private String startDate;
    private Integer statusId;
    // organisational stuff
    private String statusName;

    public Person() {

    }

    public Person(Adviser tmp) {
        this.isResearcher = false;
        this.id = tmp.getId();
        this.fullName = tmp.getFullName();
        this.statusName = tmp.getStatusName();
        this.email = tmp.getEmail();
        this.phone = tmp.getPhone();
        this.institution = tmp.getInstitution();
        this.division = tmp.getDivision();
        this.department = tmp.getDepartment();
        this.pictureUrl = tmp.getPictureUrl();
        this.startDate = tmp.getStartDate();
        this.endDate = tmp.getEndDate();
    }

    public Person(Researcher tmp) {
        this.isResearcher = true;
        this.id = tmp.getId();
        this.fullName = tmp.getFullName();
        this.preferredName = tmp.getPreferredName();
        this.statusId = tmp.getStatusId();
        this.statusName = tmp.getStatusName();
        this.email = tmp.getEmail();
        this.phone = tmp.getPhone();
        this.institution = tmp.getInstitution();
        this.division = tmp.getDivision();
        this.department = tmp.getDepartment();
        this.institutionalRoleId = tmp.getInstitutionalRoleId();
        this.institutionalRoleName = tmp.getInstitutionalRoleName();
        this.pictureUrl = tmp.getPictureUrl();
        this.startDate = tmp.getStartDate();
        this.endDate = tmp.getEndDate();
    }

    public String getDepartment() {

        return department;
    }

    public String getDivision() {

        return division;
    }

    public String getEmail() {
        return email;
    }

    public String getEndDate() {

        return endDate;
    }

    public String getFullName() {

        return fullName;
    }

    public Integer getId() {

        return id;
    }

    public String getInstitution() {

        return institution;
    }

    public Integer getInstitutionalRoleId() {

        return institutionalRoleId;
    }

    public String getInstitutionalRoleName() {

        return institutionalRoleName;
    }

    public Boolean getIsResearcher() {

        return isResearcher;
    }

    public String getNotes() {

        return notes;
    }

    public String getOtherInstitution() {

        return otherInstitution;
    }

    public String getPhone() {

        return phone;
    }

    public String getPictureUrl() {

        return pictureUrl;
    }

    public String getPreferredName() {

        return preferredName;
    }

    public String getStartDate() {

        return startDate;
    }

    public Integer getStatusId() {

        return statusId;
    }

    public String getStatusName() {

        return statusName;
    }

    public Boolean isResearcher() {
        return this.isResearcher;
    }

    public void setDepartment(String department) {

        this.department = department;
    }

    public void setDivision(String division) {

        this.division = division;
    }

    public void setEmail(String email) {

        this.email = email;
    }

    public void setEndDate(String endDate) {

        this.endDate = endDate;
    }

    public void setFullName(String fullName) {

        this.fullName = fullName;
    }

    public void setId(Integer id) {

        this.id = id;
    }

    public void setInstitution(String institution) {

        this.institution = institution;
    }

    public void setInstitutionalRoleId(Integer institutionalRoleId) {

        this.institutionalRoleId = institutionalRoleId;
    }

    public void setInstitutionalRoleName(String institutionalRoleName) {

        this.institutionalRoleName = institutionalRoleName;
    }

    public void setIsResearcher(Boolean isResearcher) {

        this.isResearcher = isResearcher;
    }

    public void setNotes(String notes) {

        this.notes = notes;
    }

    public void setOtherInstitution(String otherInstitution) {

        this.otherInstitution = otherInstitution;
    }

    public void setPhone(String phone) {

        this.phone = phone;
    }

    public void setPictureUrl(String pictureUrl) {

        this.pictureUrl = pictureUrl;
    }

    public void setPreferredName(String preferredName) {

        this.preferredName = preferredName;
    }

    public void setStartDate(String startDate) {

        this.startDate = startDate;
    }

    public void setStatusId(Integer statusId) {

        this.statusId = statusId;
    }

    public void setStatusName(String string) {

        this.statusName = string;
    }

}
