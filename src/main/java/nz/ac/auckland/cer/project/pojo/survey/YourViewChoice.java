package nz.ac.auckland.cer.project.pojo.survey;

public class YourViewChoice {

	private String label = "";
	private String value;
	
	public YourViewChoice(String value) {
		this.value = value;
	}

	public String getLabel() {
		return label;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
