package nz.ac.auckland.cer.project.pojo;

public class ProjectWrapper {

    private Project project;
    private RPLink[] rpLinks;
    private APLink[] apLinks;
    private ProjectFacility[] projectFacilities;

    public ProjectWrapper() {

    }

    public ProjectWrapper(
            Project project) {

        this.project = project;
    }

    public Project getProject() {

        return project;
    }

    public void setProject(
            Project project) {

        this.project = project;
    }

    public ProjectFacility[] getProjectFacilities() {

        return projectFacilities;
    }

    public void setProjectFacilities(
            ProjectFacility[] projectFacilities) {

        this.projectFacilities = projectFacilities;
    }

    public void setRpLinks(
            RPLink[] rpLinks) {

        this.rpLinks = rpLinks;
    }

    public void setApLinks(
            APLink[] apLinks) {

        this.apLinks = apLinks;
    }

    public RPLink[] getRpLinks() {

        return rpLinks;
    }

    public APLink[] getApLink() {

        return apLinks;
    }

}
