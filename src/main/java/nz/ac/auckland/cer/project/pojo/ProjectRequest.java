package nz.ac.auckland.cer.project.pojo;

public class ProjectRequest {

    private String projectTitle;
    private String projectDescription;
    private Boolean askForSuperviser;
    // superviser
    private Integer superviserId;
    private String superviserAffiliation;
    private String superviserOtherAffiliation;
    private String superviserName;
    private String superviserEmail;
    private String superviserPhone;
    // survey
    private String motivation;
    private String otherMotivation;
    private String currentEnv;
    private Limitations limitations;

    public String getMotivation() {

        return motivation;
    }

    public void setMotivation(
            String motivation) {

        this.motivation = motivation;
    }

    public Limitations getLimitations() {

        return limitations;
    }

    public void setLimitations(
            Limitations limitations) {

        this.limitations = limitations;
    }

    public String getCurrentEnv() {

        return currentEnv;
    }

    public void setCurrentEnv(
            String currentEnv) {

        this.currentEnv = currentEnv;
    }

    public String getSuperviserName() {

        return superviserName;
    }

    public void setSuperviserName(
            String superviserName) {

        this.superviserName = superviserName;
    }

    public String getSuperviserEmail() {

        return superviserEmail;
    }

    public void setSuperviserEmail(
            String superviserEmail) {

        this.superviserEmail = superviserEmail;
    }

    public String getSuperviserPhone() {

        return superviserPhone;
    }

    public void setSuperviserPhone(
            String superviserPhone) {

        this.superviserPhone = superviserPhone;
    }

    public String getProjectTitle() {

        return projectTitle;
    }

    public void setProjectTitle(
            String projectTitle) {

        this.projectTitle = projectTitle;
    }

    public String getProjectDescription() {

        return projectDescription;
    }

    public void setProjectDescription(
            String projectDescription) {

        this.projectDescription = projectDescription;
    }

    public Boolean getAskForSuperviser() {

        return askForSuperviser;
    }

    public void setAskForSuperviser(
            Boolean askForSuperviser) {

        this.askForSuperviser = askForSuperviser;
    }

    public String getOtherMotivation() {

        return otherMotivation;
    }

    public void setOtherMotivation(
            String otherMotivation) {

        this.otherMotivation = otherMotivation;
    }

    public Integer getSuperviserId() {

        return superviserId;
    }

    public void setSuperviserId(
            Integer superviserId) {

        this.superviserId = superviserId;
    }

    public String getSuperviserAffiliation() {

        return superviserAffiliation;
    }

    public void setSuperviserAffiliation(
            String superviserAffiliation) {

        this.superviserAffiliation = superviserAffiliation;
    }

    public String getSuperviserOtherAffiliation() {

        return superviserOtherAffiliation;
    }

    public void setSuperviserOtherAffiliation(
            String superviserOtherAffiliation) {

        this.superviserOtherAffiliation = superviserOtherAffiliation;
    }

}
