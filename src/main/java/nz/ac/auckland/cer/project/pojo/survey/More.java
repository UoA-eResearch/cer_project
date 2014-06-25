package nz.ac.auckland.cer.project.pojo.survey;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class More {

    private Log log = LogFactory.getLog(More.class.getName());
    private String number;
    private String factor;

    public String toString() {

        String s = null;
        if (number != null && !number.isEmpty() && factor != null && !factor.isEmpty()) {
            s = "I can now run up to " + number + " jobs at the same time, " + "which is " + factor
                    + " times more concurrent jobs than before.";
        }
        return s;
    }

    public void setFactor(
            String factor) {

        this.factor = factor;
    }

    public String getFactor() {

        return this.factor;
    }

    public String getNumber() {

        return number;
    }

    public void setNumber(
            String number) {

        this.number = number;
    }

}
