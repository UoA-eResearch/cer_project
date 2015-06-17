package nz.ac.auckland.cer.project.pojo.survey;

import java.util.List;

public class Survey {

	private Integer addResearchOutputRow = 0;
	private String projectCode;
	private List<String> improvements;
	private PerfImpFaster perfImpFaster;
	private PerfImpBigger perfImpBigger;
	private PerfImpMore perfImpMore;
	private FutureNeeds futureNeeds;
	private Feedback feedback;
	private ResearchOutcome researchOutcome;

	public Survey() {
		this.perfImpBigger = new PerfImpBigger();
		this.perfImpFaster = new PerfImpFaster();
		this.perfImpMore = new PerfImpMore();
		this.futureNeeds = new FutureNeeds();
		this.feedback = new Feedback();
		this.researchOutcome = new ResearchOutcome();
	}
	
	public PerfImpFaster getPerfImpFaster() {
		return perfImpFaster;
	}

	public void setPerfImpFaster(PerfImpFaster perfImpFaster) {
		this.perfImpFaster = perfImpFaster;
	}

	public PerfImpBigger getPerfImpBigger() {
		return perfImpBigger;
	}

	public void setPerfImpBigger(PerfImpBigger perfImpBigger) {
		this.perfImpBigger = perfImpBigger;
	}

	public PerfImpMore getPerfImpMore() {
		return perfImpMore;
	}

	public void setPerfImpMore(PerfImpMore perfImpMore) {
		this.perfImpMore = perfImpMore;
	}

	public List<String> getImprovements() {

		return improvements;
	}

	public void setImprovements(List<String> improvements) {

		this.improvements = improvements;
	}

	public String getProjectCode() {

		return projectCode;
	}

	public void setProjectCode(String projectCode) {

		this.projectCode = projectCode;
	}

	public FutureNeeds getFutureNeeds() {

		return futureNeeds;
	}

	public void setFutureNeeds(FutureNeeds futureNeeds) {

		this.futureNeeds = futureNeeds;
	}

	public Feedback getFeedback() {

		return feedback;
	}

	public void setFeedback(Feedback feedback) {

		this.feedback = feedback;
	}

	public ResearchOutcome getResearchOutcome() {

		return researchOutcome;
	}

	public void setResearchOutcome(ResearchOutcome researchOutcome) {

		this.researchOutcome = researchOutcome;
	}

	public Integer getAddResearchOutputRow() {

		return addResearchOutputRow;
	}

	public void setAddResearchOutputRow(Integer addResearchOutputRow) {

		this.addResearchOutputRow = addResearchOutputRow;
	}

}
