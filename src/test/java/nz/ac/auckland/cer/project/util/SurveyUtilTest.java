package nz.ac.auckland.cer.project.util;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import nz.ac.auckland.cer.common.db.project.pojo.ResearchOutput;
import nz.ac.auckland.cer.project.pojo.survey.Feedback;
import nz.ac.auckland.cer.project.pojo.survey.FutureNeeds;
import nz.ac.auckland.cer.project.pojo.survey.PerfImpBigger;
import nz.ac.auckland.cer.project.pojo.survey.PerfImpFaster;
import nz.ac.auckland.cer.project.pojo.survey.PerfImpMore;
import nz.ac.auckland.cer.project.pojo.survey.ResearchOutcome;
import nz.ac.auckland.cer.project.pojo.survey.Survey;
import nz.ac.auckland.cer.project.util.SurveyUtil;

import org.junit.Before;
import org.junit.Test;

public class SurveyUtilTest {

	String bcTemplates[] = {
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
	ResearchOutcome roc;
	Survey s;
	SurveyUtil su;
	String piTemplate = "PI:<br>__PI__";
	String fnTemplate = "FN:<br>__FN__";
	String fbTemplate = "FB:<br>__FB__";
	String roTemplate = "RO:<br>__RO__";
	String fuTemplate = "__PI__<br>__FN__<br>__FB__";
	String surveyTemplate = fuTemplate + "<br>__RO__";
	
    @Before
    public void setup() {

    	this.pif = new PerfImpFaster();
    	this.pib = new PerfImpBigger();
    	this.pim = new PerfImpMore();
    	this.fb = new Feedback();
    	this.fn = new FutureNeeds();
    	this.roc = new ResearchOutcome();
        this.s = new Survey();
		this.s.setPerfImpFaster(pif);
		this.s.setPerfImpBigger(pib);
		this.s.setPerfImpMore(pim);
        this.s.setFeedback(fb);
        this.s.setResearchOutcome(roc);
        this.s.setFutureNeeds(fn);
        this.su = new SurveyUtil();
        this.su.piTemplate = piTemplate;
        this.su.fnTemplate = fnTemplate;
        this.su.fbTemplate = fbTemplate;
        this.su.fuTemplate = fuTemplate;
        this.su.roTemplate = roTemplate;
        this.su.surveyTemplate = surveyTemplate;
    }

	@Test
	public void testCreatePiString() throws Exception {
		assertEquals("PI:<br>N/A", su.createPiString(null));
		assertEquals("PI:<br>N/A", su.createPiString(s));
		pif.setTemplate(bcTemplates[2]);
		pif.setNumber("42");
		pif.setOptions(new String[] {"O1"});
		pif.setOtherReason("OR");
		assertEquals("PI:<br>BC n=42. O1. Other: OR.", su.createPiString(s));
		
		pim.setTemplate(bcTemplates[5]);
		pim.setNumber("42");
		pim.setFactor("43");
		assertEquals("PI:<br>BC n=42. O1. Other: OR. BC n=42 f=43.", su.createPiString(s));

	}

	@Test
	public void testCreateFnString() throws Exception {
		assertEquals("FN:<br>N/A", su.createFnString(null));
		assertEquals("FN:<br>N/A", su.createFnString(s));
		fn.setOptions(new String[] { "O1", "O2" });
		fn.setOtherReason("OR");
		assertEquals("FN:<br>O1. O2. Other: OR.", su.createFnString(s));
	}

	@Test
	public void testCreateFbString() throws Exception {
		assertEquals("FB:<br>N/A", su.createFbString(null));
		assertEquals("FB:<br>N/A", su.createFbString(s));
		fb.setFeedback("My feedback");
		assertEquals("FB:<br>My feedback", su.createFbString(s));
	}

	@Test
	public void testCreateRoString() throws Exception {
		assertEquals("RO:<br>N/A", su.createRoString(null));
		assertEquals("RO:<br>N/A", su.createRoString(s));
		List<ResearchOutput> ros = new LinkedList<ResearchOutput>();
		roc.setResearchOutputs(ros);
		assertEquals("RO:<br>N/A", su.createRoString(s));
		ResearchOutput ro1 = new ResearchOutput();
		ro1.setTypeId(1);
		ro1.setDescription("My Book");
		ros.add(ro1);
		assertEquals("RO:<br>My Book (typeId=1)<br>", su.createRoString(s));
		ResearchOutput ro2 = new ResearchOutput();
		ro2.setTypeId(3);
		ro2.setDescription("A poster");
		ros.add(ro2);
		assertEquals("RO:<br>My Book (typeId=1)<br>A poster (typeId=3)<br>", su.createRoString(s));
	}

	@Test
	public void testCreateFollowUpString() throws Exception {
		assertEquals("PI:<br>N/A<br>FN:<br>N/A<br>FB:<br>N/A", su.createFollowUpString(null));
		assertEquals("PI:<br>N/A<br>FN:<br>N/A<br>FB:<br>N/A", su.createFollowUpString(s));
		pim.setTemplate(bcTemplates[5]);
		pim.setNumber("42");
		pim.setFactor("43");
		assertEquals("PI:<br>BC n=42 f=43.<br>FN:<br>N/A<br>FB:<br>N/A", 
			su.createFollowUpString(s));
		
		fn.setOptions(new String[] { "O1", "O2" });
		fn.setOtherReason("OR");
		assertEquals("PI:<br>BC n=42 f=43.<br>FN:<br>O1. O2. Other: OR.<br>FB:<br>N/A", 
				su.createFollowUpString(s));

		fb.setFeedback("My feedback");
		assertEquals("PI:<br>BC n=42 f=43.<br>FN:<br>O1. O2. Other: OR.<br>FB:<br>My feedback", 
				su.createFollowUpString(s));		
	}
	
	@Test
	public void testCreateSurveyString() throws Exception {
		assertEquals("PI:<br>N/A<br>FN:<br>N/A<br>FB:<br>N/A<br>RO:<br>N/A",
				su.createSurveyString(null));
		assertEquals("PI:<br>N/A<br>FN:<br>N/A<br>FB:<br>N/A<br>RO:<br>N/A",
				su.createSurveyString(s));
		pim.setTemplate(bcTemplates[5]);
		pim.setNumber("42");
		pim.setFactor("43");
		assertEquals("PI:<br>BC n=42 f=43.<br>FN:<br>N/A<br>FB:<br>N/A<br>RO:<br>N/A", 
			su.createSurveyString(s));
		
		fn.setOptions(new String[] { "O1", "O2" });
		fn.setOtherReason("OR");
		assertEquals("PI:<br>BC n=42 f=43.<br>FN:<br>O1. O2. Other: OR.<br>FB:<br>N/A<br>RO:<br>N/A", 
				su.createSurveyString(s));

		fb.setFeedback("My feedback");
		assertEquals("PI:<br>BC n=42 f=43.<br>FN:<br>O1. O2. Other: OR.<br>FB:<br>My feedback<br>RO:<br>N/A", 
				su.createSurveyString(s));
		
		List<ResearchOutput> ros = new LinkedList<ResearchOutput>();
		roc.setResearchOutputs(ros);
		ResearchOutput ro1 = new ResearchOutput();
		ResearchOutput ro2 = new ResearchOutput();
		ro1.setTypeId(1);
		ro1.setDescription("My Book");
		ro2.setTypeId(null);
		ro2.setDescription("A poster");
		ros.add(ro1);
		ros.add(ro2);
		assertEquals("PI:<br>BC n=42 f=43.<br>FN:<br>O1. O2. Other: OR.<br>FB:<br>My feedback<br>" +
				"RO:<br>My Book (typeId=1)<br>A poster (typeId=null)<br>",
				su.createSurveyString(s));
	}
	
}
