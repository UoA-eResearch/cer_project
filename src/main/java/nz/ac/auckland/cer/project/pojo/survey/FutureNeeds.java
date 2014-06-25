package nz.ac.auckland.cer.project.pojo.survey;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FutureNeeds {

    private Log log = LogFactory.getLog(Feedback.class.getName());
    private List<String> comments = new ArrayList<String>();
    protected String moreCpus = "more CPU cores per cluster node to run larger multi-threaded jobs";
    protected String fastInterconnect = "fast interconnect between the machines for my MPI jobs";
    protected String moreMemory = "more memory per compute node";
    protected String moreGpus = "more GPUs";
    protected String morePhis = "more Intel Xeon Phi's";
    protected String moreDisk = "more disk space";
    protected String shorterWaitTimes = "shorter wait times";    
    protected String moreScalingAdvice = "advice on how to parallelise/scale/tune my software";
    protected String moreSupport = "more/better general support";
    protected String other;

    public String toString() {

        String s = "";
        if (comments != null && comments.size() > 0) {
            int numFeedback = comments.size();
            if (numFeedback > 0) {
                for (int i = 0; i < numFeedback; i++) {
                    String tmp = comments.get(i);
                    try {
                        Field f = this.getClass().getDeclaredField(tmp);
                        s += f.get(this);
                        if (i < numFeedback - 1) {
                            s += ", ";
                        } else {
                            s += ". ";
                        }
                    } catch (Exception e) {
                        log.error("Unknown field: " + tmp);
                    }
                }
            }
        }
        if (other != null && other.trim().length() > 0) {
            s += other;
        }
        if (s.isEmpty()) {
            s += "N/A";
        }
        return s.trim();
    }

    public void setComments(
            List<String> comments) {

        this.comments = comments;
    }

    public void setOther(
            String other) {

        this.other = other;
    }

    public List<String> getComments() {

        return comments;
    }

    public String getOther() {

        return other;
    }

}
