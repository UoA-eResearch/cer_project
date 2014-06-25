package nz.ac.auckland.cer.project.pojo.survey;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Feedback {

    private Log log = LogFactory.getLog(Feedback.class.getName());
    protected String feedback;

    public String toString() {

        String s = "";
        if (feedback != null && feedback.trim().length() > 0) {
            s += feedback;
        }
        if (s.isEmpty()) {
            s += "N/A";
        }
        return s.trim();
    }

    public String getFeedback() {

        return feedback;
    }

    public void setFeedback(
            String feedback) {

        this.feedback = feedback;
    }

}
