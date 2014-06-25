package nz.ac.auckland.cer.project.pojo.survey;

import java.util.LinkedList;
import java.util.List;

import nz.ac.auckland.cer.project.pojo.ResearchOutput;

public class ResearchOutcome {

    private List<ResearchOutput> researchOutputs = new LinkedList<ResearchOutput>();
    private Integer addResearchOutputRow = 0;
    private Integer noResearchOutput = 0;

    public Integer getAddResearchOutputRow() {

        return addResearchOutputRow;
    }

    public void setAddResearchOutputRow(
            Integer addResearchOutputRow) {

        this.addResearchOutputRow = addResearchOutputRow;
    }

    public List<ResearchOutput> getResearchOutputs() {

        return researchOutputs;
    }

    public void setResearchOutputs(
            List<ResearchOutput> researchOutputs) {

        this.researchOutputs = researchOutputs;
    }

    public Integer getNoResearchOutput() {

        return noResearchOutput;
    }

    public void setNoResearchOutput(
            Integer noResearchOutput) {

        this.noResearchOutput = noResearchOutput;
    }

}
