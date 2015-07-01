package nz.ac.auckland.cer.project.pojo.survey;

public class YourViews {

	private YourViewChoice[] choices = new YourViewChoice[] {
			new YourViewChoice("Strongly agree"),
			new YourViewChoice("Agree"),
			new YourViewChoice("Neutral"),
			new YourViewChoice("Disagree"),
			new YourViewChoice("Strongly disagree"),
			new YourViewChoice("Not applicable") };
	private String recommend = "I feel comfortable recommending these services to colleagues";
	private String meetNeed = "These services meet my needs";
	private String adequateSupport = "I receive adequate support when using these services";
	private String recommendChoice;
	private String meetNeedChoice;
	private String adequateSupportChoice;
	private String emptyLabel = "a";

	public String getRecommend() {
		return recommend;
	}

	public void setRecommend(String recommend) {
		this.recommend = recommend;
	}

	public String getMeetNeed() {
		return meetNeed;
	}

	public void setMeetNeed(String meetNeed) {
		this.meetNeed = meetNeed;
	}

	public String getAdequateSupport() {
		return adequateSupport;
	}

	public void setAdequateSupport(String adequateSupport) {
		this.adequateSupport = adequateSupport;
	}

	public String getRecommendChoice() {
		return recommendChoice;
	}

	public void setRecommendChoice(String recommendChoice) {
		this.recommendChoice = recommendChoice;
	}

	public String getMeetNeedChoice() {
		return meetNeedChoice;
	}

	public void setMeetNeedChoice(String meetNeedChoice) {
		this.meetNeedChoice = meetNeedChoice;
	}

	public String getAdequateSupportChoice() {
		return adequateSupportChoice;
	}

	public void setAdequateSupportChoice(String adequateSupportChoice) {
		this.adequateSupportChoice = adequateSupportChoice;
	}

	public String getEmptyLabel() {
		return emptyLabel;
	}

	public YourViewChoice[] getChoices() {
		return choices;
	}

	@Override
    public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(recommend).append(": ").append(recommendChoice).append("<br>")
		  .append(meetNeed).append(": ").append(meetNeedChoice).append("<br>")
		  .append(adequateSupport).append(": ").append(adequateSupportChoice);
		return sb.toString();
	}
}
