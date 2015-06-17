package nz.ac.auckland.cer.project.util;

import static org.junit.Assert.*;
import nz.ac.auckland.cer.project.pojo.survey.Feedback;
import nz.ac.auckland.cer.project.pojo.survey.FutureNeeds;
import nz.ac.auckland.cer.project.pojo.survey.PerfImpBigger;
import nz.ac.auckland.cer.project.pojo.survey.PerfImpFaster;
import nz.ac.auckland.cer.project.pojo.survey.PerfImpMore;
import nz.ac.auckland.cer.project.pojo.survey.Survey;
import nz.ac.auckland.cer.project.util.SurveyUtil;

import org.junit.Before;
import org.junit.Test;

public class SurveyUtilTest {

	String templates[] = {
	    "BC n=__NUMBER__",
	    "BC n=__NUMBER__ f=__FACTOR__",
	    "BC n=__NUMBER__. __OPTIONS__",
	    "BC n=__NUMBER__ f=__FACTOR__. __OPTIONS__",
	    "__OPTIONS__.",
	    "BC n=__NUMBER__ f=__FACTOR__",
	};

	PerfImpFaster pif;
	PerfImpBigger pib;
	PerfImpMore pim;
	Feedback fb;
	FutureNeeds fn;
	Survey s;
	SurveyUtil su;
	String template = "PI:<br>__PI__<br>FN:<br>__FN__<br>FB:<br>__FB__";
	
    @Before
    public void setup() {

    	this.pif = new PerfImpFaster();
    	this.pib = new PerfImpBigger();
    	this.pim = new PerfImpMore();
    	this.fb = new Feedback();
    	this.fn = new FutureNeeds();
        this.s = new Survey();
		this.s.setPerfImpFaster(pif);
		this.s.setPerfImpBigger(pib);
		this.s.setPerfImpMore(pim);
        this.s.setFeedback(fb);
        this.s.setFutureNeeds(fn);
        this.su = new SurveyUtil();
        this.su.template = this.template;
    }

	@Test
	public void testGetFeedbackFromSurvey_null() throws Exception {
		assertEquals(su.getFeedbackFromSurvey(null), "N/A");
	}

	@Test
	public void testGetFeedbackFromSurvey_emptySurvey() throws Exception {
		assertEquals("PI:<br>N/A<br>FN:<br>N/A<br>FB:<br>N/A",
			su.getFeedbackFromSurvey(s));
	}

	@Test
	public void testGetFeedbackFromSurvey_1BC() throws Exception {
		pif.setTemplate(templates[2]);
		pif.setNumber("42");
		pif.setOptions(new String[] {"O1"});
		pif.setOtherReason("OR");
		assertEquals("PI:<br>BC n=42. O1. Other: OR.<br>FN:<br>N/A<br>FB:<br>N/A", 
				su.getFeedbackFromSurvey(s));
	}

	@Test
	public void testGetFeedbackFromSurvey_2BC() throws Exception {
		pif.setTemplate(templates[2]);
		pif.setNumber("17");
		pif.setOptions(new String[] { "O1" });
		pif.setOtherReason("OR");
		pim.setTemplate(templates[5]);
		pim.setNumber("42");
		pim.setFactor("43");
		assertEquals(
				"PI:<br>BC n=17. O1. Other: OR. BC n=42 f=43.<br>FN:<br>N/A<br>FB:<br>N/A",
				su.getFeedbackFromSurvey(s));
	}

	@Test
	public void testGetFeedbackFromSurvey_3BC() throws Exception {
		pif.setTemplate(templates[2]);
		pif.setNumber("17");
		pif.setOptions(new String[] { "O1" });
		pif.setOtherReason("OR");
		pib.setTemplate(templates[0]);
		pib.setNumber("13");
		pim.setTemplate(templates[5]);
		pim.setNumber("42");
		pim.setFactor("43");
		assertEquals(
				"PI:<br>BC n=17. O1. Other: OR. BC n=13. BC n=42 f=43.<br>FN:<br>N/A<br>FB:<br>N/A",
				su.getFeedbackFromSurvey(s));
	}

	@Test
	public void testGetFeedbackFromSurvey_Feedback() throws Exception {
		fb.setFeedback("My feedback");
		assertEquals("PI:<br>N/A<br>FN:<br>N/A<br>FB:<br>My feedback", 
				su.getFeedbackFromSurvey(s));
	}

	@Test
	public void testGetFeedbackFromSurvey_FutureNeeds() throws Exception {
		fn.setOptions(new String[] { "O1", "O2" });
		fn.setOtherReason("OR");
		assertEquals("PI:<br>N/A<br>FN:<br>O1. O2. Other: OR.<br>FB:<br>N/A", 
				su.getFeedbackFromSurvey(s));
	}

	@Test
	public void testGetFeedbackFromSurvey_All() throws Exception {
		pif.setTemplate(templates[2]);
		pif.setNumber("17");
		pif.setOptions(new String[] { "O1" });
		pif.setOtherReason("OR");
		fb.setFeedback("My feedback");
		fn.setOptions(new String[] { "O1", "O2" });
		assertEquals("PI:<br>BC n=17. O1. Other: OR.<br>FN:<br>O1. O2.<br>FB:<br>My feedback",
				su.getFeedbackFromSurvey(s));
	}
	
}
