package nz.ac.auckland.cer.project.pojo.survey;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Bigger {

    protected String distMemPar = "distributed memory parallelisation";
    protected String dontKnow = "I don't know";
    private String factor = "1";
    private final Log log = LogFactory.getLog(Bigger.class.getName());
    protected String moreDisk = "more disk space";
    protected String moreMem = "more memory available";
    protected String other;
    private List<String> reasons = new ArrayList<String>();

    public String getDistMemPar() {

        return distMemPar;
    }

    public String getDontKnow() {

        return dontKnow;
    }

    public String getFactor() {

        return factor;
    }

    public String getMoreMem() {

        return moreMem;
    }

    public String getOther() {

        return other;
    }

    public List<String> getReasons() {

        return reasons;
    }

    public void setDistMemPar(String distMemPar) {

        this.distMemPar = distMemPar;
    }

    public void setDontKnow(String dontKnow) {

        this.dontKnow = dontKnow;
    }

    public void setFactor(String factor) {

        this.factor = factor;
    }

    public void setMoreMem(String moreMem) {

        this.moreMem = moreMem;
    }

    public void setOther(String other) {

        this.other = other;
    }

    public void setReasons(List<String> reasons) {

        this.reasons = reasons;
    }

    @Override
    public String toString() {

        String s = null;
        int numReasons = reasons.size();
        if (numReasons > 0 || (other != null && other.length() > 0)) {
            s = "I can run larger jobs now, up to " + factor
                    + " times larger than before, thanks to: ";
            for (int i = 0; i < numReasons; i++) {
                String tmp = reasons.get(i);
                try {
                    Field f = this.getClass().getDeclaredField(tmp);
                    s += f.get(this);
                    if (i < numReasons - 1) {
                        s += ", ";
                    }
                } catch (Exception e) {
                    log.error("Unknown field: " + tmp);
                }
            }
            if (numReasons > 0 && !s.trim().endsWith(".")) {
                s = s.trim() + ". ";
            }
            if (other != null && other.length() > 0) {
                s += other;
            }
        }
        return (s == null) ? s : s.trim();
    }

}
