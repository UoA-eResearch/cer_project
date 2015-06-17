package nz.ac.auckland.cer.project.util;

import org.apache.log4j.Logger;

import nz.ac.auckland.cer.project.pojo.survey.PerfImpBigger;
import nz.ac.auckland.cer.project.pojo.survey.Feedback;
import nz.ac.auckland.cer.project.pojo.survey.FutureNeeds;
import nz.ac.auckland.cer.project.pojo.survey.PerfImpMore;
import nz.ac.auckland.cer.project.pojo.survey.PerfImpFaster;
import nz.ac.auckland.cer.project.pojo.survey.Survey;

public class SurveyUtil {

	private final Logger log = Logger.getLogger(SurveyUtil.class.getName());
	protected String template;
	
	public SurveyUtil() {
		this.template = "Performance Improvements:<br>__PI__<br><br>" +
	                    "Future Needs:<br>__FN__<br><br>" +
				        "Feedback:<br>__FB__";
	}
	

	public String getFeedbackFromSurvey(Survey s) throws Exception {

        String tmp = null;
		if (s == null) {
			log.warn("Survey is null");
			tmp = "N/A";
		} else {
			tmp = new String(template);
			StringBuilder sb = new StringBuilder("");
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
			tmp = tmp.replace("__PI__", sb.toString());

			// Future Needs
			FutureNeeds fn = s.getFutureNeeds();
			if (fn == null || fn.toString() == null || fn.toString().trim().isEmpty()) {
				tmp = tmp.replace("__FN__", "N/A");
			} else {
				tmp = tmp.replace("__FN__", fn.toString());
			}
			
			// Feedback
			Feedback fb = s.getFeedback();
			if (fb == null || fb.toString() == null || fb.toString().trim().isEmpty()) {
				tmp = tmp.replace("__FB__", "N/A");
			} else {
				tmp = tmp.replace("__FB__", fb.toString());
			}
		}
		return tmp;
	}
}
