package nz.ac.auckland.cer.project.util;

import java.util.List;

import org.apache.log4j.Logger;

import nz.ac.auckland.cer.common.db.project.pojo.ResearchOutput;
import nz.ac.auckland.cer.project.pojo.survey.PerfImpBigger;
import nz.ac.auckland.cer.project.pojo.survey.Feedback;
import nz.ac.auckland.cer.project.pojo.survey.FutureNeeds;
import nz.ac.auckland.cer.project.pojo.survey.PerfImpMore;
import nz.ac.auckland.cer.project.pojo.survey.PerfImpFaster;
import nz.ac.auckland.cer.project.pojo.survey.ResearchOutcome;
import nz.ac.auckland.cer.project.pojo.survey.Survey;

public class SurveyUtil {

	private final Logger log = Logger.getLogger(SurveyUtil.class.getName());
	protected String piTemplate = "Performance Improvements:<br>__PI__";
	protected String fnTemplate = "Future Needs:<br>__FN__";
	protected String fbTemplate = "Feedback:<br>__FB__";
	protected String roTemplate = "Research Outcome:<br>__RO__";
	protected String fuTemplate = "__PI__<br><br>__FN__<br><br>__FB__";
	protected String surveyTemplate = fuTemplate + "<br><br>__RO__";

	// create string from performance improvements
	public String createPiString(Survey s) {
		
        StringBuilder sb = new StringBuilder();
		if (s == null) {
			sb.append("N/A");
		} else {
			PerfImpFaster faster = s.getPerfImpFaster();
			PerfImpBigger bigger = s.getPerfImpBigger();
			PerfImpMore more = s.getPerfImpMore();
			String fasterS = (faster == null) ? null : faster.toString();
			String biggerS = (bigger == null) ? null : bigger.toString();
			String moreS = (more == null) ? null : more.toString();

			if ((faster == null && bigger == null && more == null) ||
				(fasterS == null && biggerS == null && moreS == null)) {
				sb.append("N/A");
			} else {
				// Performance Improvements
				boolean addSpace = false;
				if (faster != null && fasterS != null) {
					sb.append(fasterS);
					addSpace = true;
				}
				if (bigger != null && biggerS != null) {
					if (addSpace) {
						sb.append(" ");
					}
					sb.append(biggerS);
					addSpace = true;
				}
				if (more != null && moreS != null) {
					if (addSpace) {
						sb.append(" ");
					}
					sb.append(moreS);
				}
		    }
		}
		return piTemplate.replace("__PI__", sb.toString());
	}
	
	// create string from feedback
	public String createFbString(Survey s) {

		StringBuilder sb = new StringBuilder();
		if (s == null || s.getFeedback() == null) {
			sb.append("N/A");
		} else {
			Feedback fb = s.getFeedback();
			String tmp = fb.toString();
			if (tmp == null || tmp.trim().isEmpty()) {
				sb.append("N/A");
			} else {
				sb.append(tmp.trim());
			}
			
		}
		return fbTemplate.replace("__FB__", sb.toString());
	}

	// create string from future needs
	public String createFnString(Survey s) {

		StringBuilder sb = new StringBuilder();
		if (s == null || s.getFutureNeeds() == null) {
			sb.append("N/A");
		} else {
			FutureNeeds fn = s.getFutureNeeds();
			String tmp = fn.toString();
			if (tmp == null || tmp.trim().isEmpty()) {
				sb.append("N/A");
			} else {
				sb.append(tmp.trim());
			}

		}
		return fnTemplate.replace("__FN__", sb.toString());
	}

	// create string from research outputs
	public String createRoString(Survey s) {

		StringBuilder sb = new StringBuilder();
		if (s == null || s.getResearchOutcome() == null || s.getResearchOutcome().getHasNoResearchOutput()) {
			sb.append("N/A");
		} else {
			ResearchOutcome roc = s.getResearchOutcome();
			List<ResearchOutput> l = roc.getResearchOutputs();
			if (l == null || l.isEmpty() ) {
				sb.append("N/A");
			} else {
				for (ResearchOutput ro: l) {
					sb.append(ro.getDescription()).append(" (typeId=")
					  .append(ro.getTypeId()).append(")<br>");						
				}
			}
		}
		return roTemplate.replace("__RO__", sb.toString());
	}

	public String createFollowUpString(Survey s) throws Exception {

		return fuTemplate.replace("__PI__", this.createPiString(s))
			.replace("__FN__", this.createFnString(s))
			.replace("__FB__", this.createFbString(s));
	}

	public String createSurveyString(Survey s) throws Exception {

		return surveyTemplate.replace("__PI__", this.createPiString(s))
			.replace("__FN__", this.createFnString(s))
			.replace("__FB__", this.createFbString(s))
			.replace("__RO__", this.createRoString(s));			
	}
	

}
