package nz.ac.auckland.cer.project.pojo.survey;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MemoryUsage {

    private Log log = LogFactory.getLog(MemoryUsage.class.getName());
    private String memGb;

    public String toString() {

        return "Memory Usage: I'm using up to " + memGb + " GB RAM per job or MPI process.";
    }

    public String getMemGb() {

        return memGb;
    }

    public void setMemGb(
            String memGb) {

        this.memGb = memGb;
    }

}
