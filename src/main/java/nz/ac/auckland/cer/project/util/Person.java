package nz.ac.auckland.cer.project.util;

import nz.ac.auckland.cer.project.pojo.Adviser;
import nz.ac.auckland.cer.project.pojo.Researcher;

/*
 * Abstract away the small differences between a researcher and an adviser
 */
public class Person {

    // fields common for adviser and researcher
    private Integer id;
    private String fullName;
    private String email;
    private String phone;
    private String institution;
    private String division;
    private String department;
    private String pictureUrl;
    private String startDate;
    private String endDate = ""; // empty string to avoid null in database which
                                 // causes problems with other apps
    private String notes;

    // fields that only researchers have
    private String preferredName;
    private Integer statusId;
    private Integer institutionalRoleId;
    private String institutionalRoleName;
   
    // fields that only advisers have
    
    // organisational stuff
    private String statusName;
    private String otherInstitution;
    private Boolean isResearcher;

    public Person () {
        
    }
    
    public Person (Researcher tmp) {
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
    
    public Person (Adviser tmp) {
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
    
    public Boolean isResearcher() {
        return this.isResearcher;
    }
    
    public Integer getId() {

        return id;
    }

    public Integer getInstitutionalRoleId() {

        return institutionalRoleId;
    }

    public void setInstitutionalRoleId(
            Integer institutionalRoleId) {

        this.institutionalRoleId = institutionalRoleId;
    }

    public void setId(
            Integer id) {

        this.id = id;
    }

    public String getFullName() {

        return fullName;
    }

    public void setFullName(
            String fullName) {

        this.fullName = fullName;
    }

    public String getPreferredName() {

        return preferredName;
    }

    public void setPreferredName(
            String preferredName) {

        this.preferredName = preferredName;
    }

    public String getEmail() {

        return email;
    }

    public void setEmail(
            String email) {

        this.email = email;
    }

    public String getPhone() {

        return phone;
    }

    public void setPhone(
            String phone) {

        this.phone = phone;
    }

    public String getInstitution() {

        return institution;
    }

    public void setInstitution(
            String institution) {

        this.institution = institution;
    }

    public String getDivision() {

        return division;
    }

    public void setDivision(
            String division) {

        this.division = division;
    }

    public String getDepartment() {

        return department;
    }

    public void setDepartment(
            String department) {

        this.department = department;
    }

    public String getPictureUrl() {

        return pictureUrl;
    }

    public void setPictureUrl(
            String pictureUrl) {

        this.pictureUrl = pictureUrl;
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

    public String getInstitutionalRoleName() {

        return institutionalRoleName;
    }

    public void setInstitutionalRoleName(
            String institutionalRoleName) {

        this.institutionalRoleName = institutionalRoleName;
    }

    public Integer getStatusId() {

        return statusId;
    }

    public void setStatusId(
            Integer statusId) {

        this.statusId = statusId;
    }

    public String getStatusName() {

        return statusName;
    }

    public void setStatusName(
            String string) {

        this.statusName = string;
    }

    public Boolean getIsResearcher() {
    
        return isResearcher;
    }

    public void setIsResearcher(
            Boolean isResearcher) {
    
        this.isResearcher = isResearcher;
    }

    public String getOtherInstitution() {
    
        return otherInstitution;
    }

    public void setOtherInstitution(
            String otherInstitution) {
    
        this.otherInstitution = otherInstitution;
    }

}
