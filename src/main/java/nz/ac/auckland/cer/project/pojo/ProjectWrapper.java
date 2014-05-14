package nz.ac.auckland.cer.project.pojo;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class ProjectWrapper implements Serializable {

    private List<AdviserAction> adviserActions;
    private List<APLink> apLinks;
    private String errorMessage;
    private List<FollowUp> followUps;
    // helpers
    private String operation;
    private Project project;
    private List<ProjectFacility> projectFacilities;
    private List<ProjectKpi> projectKpis;
    private String redirect;
    private List<ResearchOutput> researchOutputs;
    private List<Review> reviews;
    private List<RPLink> rpLinks;
    private Integer secondsLeft;

    public ProjectWrapper() {
        project = new Project();
        rpLinks = new LinkedList<RPLink>();
        apLinks = new LinkedList<APLink>();
        researchOutputs = new LinkedList<ResearchOutput>();
        projectKpis = new LinkedList<ProjectKpi>();
        reviews = new LinkedList<Review>();
        followUps = new LinkedList<FollowUp>();
        adviserActions = new LinkedList<AdviserAction>();
        projectFacilities = new LinkedList<ProjectFacility>();
    }

    public List<AdviserAction> getAdviserActions() {
        return adviserActions;
    }

    public List<APLink> getApLinks() {
        return apLinks;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public List<FollowUp> getFollowUps() {
        return followUps;
    }

    public String getOperation() {
        return operation;
    }

    public Project getProject() {
        return project;
    }

    public List<ProjectFacility> getProjectFacilities() {
        return projectFacilities;
    }

    public List<ProjectKpi> getProjectKpis() {
        return projectKpis;
    }

    public String getRedirect() {
        return redirect;
    }

    public List<ResearchOutput> getResearchOutputs() {
        return researchOutputs;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public List<RPLink> getRpLinks() {
        return rpLinks;
    }

    public Integer getSecondsLeft() {
        return secondsLeft;
    }

    public void setAdviserActions(final List<AdviserAction> adviserActions) {
        this.adviserActions = adviserActions;
    }

    public void setApLinks(final List<APLink> apLinks) {
        this.apLinks = apLinks;
    }

    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setFollowUps(final List<FollowUp> followUps) {
        this.followUps = followUps;
    }

    public void setOperation(final String operation) {
        this.operation = operation;
    }

    public void setProject(final Project project) {
        this.project = project;
    }

    public void setProjectFacilities(
            final List<ProjectFacility> projectFacilities) {
        this.projectFacilities = projectFacilities;
    }

    public void setProjectKpis(final List<ProjectKpi> projectKpis) {
        this.projectKpis = projectKpis;
    }

    public void setRedirect(final String redirect) {
        this.redirect = redirect;
    }

    public void setResearchOutputs(final List<ResearchOutput> researchOutputs) {
        this.researchOutputs = researchOutputs;
    }

    public void setReviews(final List<Review> reviews) {
        this.reviews = reviews;
    }

    public void setRpLinks(final List<RPLink> rpLinks) {
        this.rpLinks = rpLinks;
    }

    public void setSecondsLeft(final Integer secondsLeft) {
        this.secondsLeft = secondsLeft;
    }

}
