package nz.ac.auckland.cer.project.pojo.survey;

import java.util.LinkedList;
import java.util.List;

import nz.ac.auckland.cer.project.pojo.ResearchOutput;

public class ResearchOutcome {

    private List<ResearchOutput> researchOutputs = new LinkedList<ResearchOutput>();
    private Boolean hasNoResearchOutput = false;

    public List<ResearchOutput> getResearchOutputs() {

        return researchOutputs;
    }

    public void setResearchOutputs(
            List<ResearchOutput> researchOutputs) {

        this.researchOutputs = researchOutputs;
    }

    public Boolean getHasNoResearchOutput() {

        return hasNoResearchOutput;
    }

    public void setHasNoResearchOutput(
            Boolean hasNoResearchOutput) {

        this.hasNoResearchOutput = hasNoResearchOutput;
    }

}
