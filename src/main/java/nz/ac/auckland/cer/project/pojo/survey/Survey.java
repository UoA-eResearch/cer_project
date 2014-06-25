package nz.ac.auckland.cer.project.pojo.survey;

import java.util.List;

public class Survey {

    private String projectCode;
    private List<String> improvements;
    private Faster faster;
    private Bigger bigger;
    private More more;
    private FutureNeeds futureNeeds;
    private Feedback feedback;
    private ResearchOutcome researchOutcome;

    public Faster getFaster() {

        return faster;
    }

    public void setFaster(
            Faster faster) {

        this.faster = faster;
    }

    public Bigger getBigger() {

        return bigger;
    }

    public void setBigger(
            Bigger bigger) {

        this.bigger = bigger;
    }

    public More getMore() {

        return more;
    }

    public void setMore(
            More more) {

        this.more = more;
    }

    public List<String> getImprovements() {

        return improvements;
    }

    public void setImprovements(
            List<String> improvements) {

        this.improvements = improvements;
    }

    public String getProjectCode() {

        return projectCode;
    }

    public void setProjectCode(
            String projectCode) {

        this.projectCode = projectCode;
    }

    public FutureNeeds getFutureNeeds() {

        return futureNeeds;
    }

    public void setFutureNeeds(
            FutureNeeds futureNeeds) {

        this.futureNeeds = futureNeeds;
    }

    public Feedback getFeedback() {

        return feedback;
    }

    public void setFeedback(
            Feedback feedback) {

        this.feedback = feedback;
    }

    public ResearchOutcome getResearchOutcome() {

        return researchOutcome;
    }

    public void setResearchOutcome(
            ResearchOutcome researchOutcome) {

        this.researchOutcome = researchOutcome;
    }

}
