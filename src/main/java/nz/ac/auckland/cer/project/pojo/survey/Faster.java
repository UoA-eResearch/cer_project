package nz.ac.auckland.cer.project.pojo.survey;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Faster {

    private Log log = LogFactory.getLog(Faster.class.getName());
    private String factor;
    private List<String> reasons = new ArrayList<String>();
    protected String sharedMemPar = "shared memory parallelisation";
    protected String distMemPar = "distributed memory parallelisation";
    protected String algorithmOptimisation = "algorithmic improvements to my software";
    protected String buildOptimisation = "compile-time optimization of my software";
    protected String dontKnow = "I don't know";
    protected String other;

    public String toString() {

        String s = null;
        int numReasons = reasons.size();
        if (numReasons > 0 || (other != null && other.length() > 0)) {
            s = "My jobs run " + factor + " times faster than before, thanks to: ";
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

    public void setFactor(
            String factor) {

        this.factor = factor;
    }

    public void setReasons(
            List<String> reasons) {

        this.reasons = reasons;
    }

    public void setSharedMemPar(
            String sharedMemPar) {

        this.sharedMemPar = sharedMemPar;
    }

    public void setDistMemPar(
            String distMemPar) {

        this.distMemPar = distMemPar;
    }

    public void setOther(
            String other) {

        this.other = other;
    }

    public void setDontKnow(
            String dontKnow) {

        this.dontKnow = dontKnow;
    }

    public String getFactor() {

        return factor;
    }

    public List<String> getReasons() {

        return reasons;
    }

    public String getSharedMemPar() {

        return sharedMemPar;
    }

    public String getDistMemPar() {

        return distMemPar;
    }

    public String getOther() {

        return other;
    }

    public String getDontKnow() {

        return dontKnow;
    }

    public String getAlgorithmOptimisation() {

        return algorithmOptimisation;
    }

    public void setAlgorithmOptimisation(
            String algorithmOptimisation) {

        this.algorithmOptimisation = algorithmOptimisation;
    }

    public String getBuildOptimisation() {

        return buildOptimisation;
    }

    public void setBuildOptimisation(
            String buildOptimisation) {

        this.buildOptimisation = buildOptimisation;
    }

}
