package nz.ac.auckland.cer.project.pojo.survey;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BaseCategory {

    private final Log log = LogFactory.getLog(BaseCategory.class.getName());
    protected String[] optionStrings = null;
    protected String template = null;
    private String[] options = null;
    private String otherReason = null;
    private String factor = null;
    private String number = null;

    public boolean hasOptions() {
    	return (options != null && options.length > 0) || 
               (otherReason != null && otherReason.trim().length() > 0);
    }
    
    public String getFactor() {

        return factor;
    }

    public String getOtherReason() {

        return otherReason;
    }

    public String[] getOptions() {

        return options;
    }

    public String[] getOptionStrings() {

        return optionStrings;
    }

    public void setFactor(String factor) {

        this.factor = factor;
    }

    public void setOtherReason(String otherReason) {

        this.otherReason = otherReason;
    }

    public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public void setOptions(String[] options) {

        this.options = options;
    }

	public String getTemplate() {

		return template;
	}

	public void setTemplate(String template) {

		this.template = template;
	}

	@Override
    public String toString() {

    	if (this.template == null) {
    		throw new RuntimeException("template must not be null");
    	}
    	
    	String statement = this.template;
    	
    	if (this.number != null) {
    		statement = statement.replace("__NUMBER__", this.number);
    	}
    	if (this.factor != null) {
    		statement = statement.replace("__FACTOR__", this.factor);
    	}
        if (this.hasOptions()) {
        	StringBuilder tmp = new StringBuilder();
            if (options != null) {
            	for (String o: options) {
            		tmp.append(o).append(". ");
            	}
            }
            if (otherReason != null && otherReason.length() > 0) {
                tmp.append("Other: ").append(otherReason);
            }
            statement = statement.replace("__OPTIONS__", tmp.toString().trim());
        }
        if (!statement.endsWith(".") && !statement.endsWith(",") && !statement.endsWith(";") &&
            !statement.endsWith("!") && !statement. endsWith("?")) {
        	statement += ".";
        }        
        if (statement.contains("__")) {
        	statement = null;
        }
        return statement;
    }
}
