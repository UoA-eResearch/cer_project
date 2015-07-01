package nz.ac.auckland.cer.project.util;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import nz.ac.auckland.cer.common.db.project.pojo.ResearchOutput;
import nz.ac.auckland.cer.project.pojo.survey.Feedback;
import nz.ac.auckland.cer.project.pojo.survey.FutureNeeds;
import nz.ac.auckland.cer.project.pojo.survey.YourViews;
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
	YourViews yv;
	Survey s;
	SurveyUtil su;
	String piTemplate = "PI:<br>__PI__";
	String fnTemplate = "FN:<br>__FN__";
	String fbTemplate = "FB:<br>__FB__";
	String roTemplate = "RO:<br>__RO__";
	String yvTemplate = "YV:<br>__YV__";
	String gbtmTemplate = "GBTM: __GBTM__";
	String fuTemplate = "__PI__<br>__YV__<br>__FB__<br>__FN__<br>__GBTM__";
	String surveyTemplate = fuTemplate + "<br>__RO__";
	
    @Before
    public void setup() {

    	this.pif = new PerfImpFaster();
    	this.pib = new PerfImpBigger();
    	this.pim = new PerfImpMore();
    	this.fb = new Feedback();
    	this.fn = new FutureNeeds();
    	this.roc = new ResearchOutcome();
    	this.yv = new YourViews();
        this.s = new Survey();
		this.s.setPerfImpFaster(pif);
		this.s.setPerfImpBigger(pib);
		this.s.setPerfImpMore(pim);
        this.s.setFeedback(fb);
        this.s.setResearchOutcome(roc);
        this.s.setFutureNeeds(fn);
        this.s.setYourViews(yv);
        this.su = new SurveyUtil();
        this.su.piTemplate = piTemplate;
        this.su.fnTemplate = fnTemplate;
        this.su.fbTemplate = fbTemplate;
        this.su.roTemplate = roTemplate;
        this.su.yvTemplate = yvTemplate;
        this.su.gbtmTemplate = gbtmTemplate;
        this.su.fuTemplate = fuTemplate;
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
		assertEquals("RO:<br>My Book (typeId=1)", su.createRoString(s));
		ResearchOutput ro2 = new ResearchOutput();
		ro2.setTypeId(3);
		ro2.setDescription("A poster");
		ros.add(ro2);
		assertEquals("RO:<br>My Book (typeId=1)<br>A poster (typeId=3)", su.createRoString(s));
	}

	@Test
	public void testCreateGbtmString() throws Exception {
		assertEquals("GBTM: N/A", su.createGbtmString(null));
		assertEquals("GBTM: No", su.createGbtmString(s));
		s.setGetBackToMe(true);
		assertEquals("GBTM: Yes", su.createGbtmString(s));
	}

	@Test
	public void testCreateYvString() throws Exception {
		assertEquals("YV:<br>N/A", su.createYvString(null));
        yv.setRecommend("R");
        yv.setMeetNeed("MN");
        yv.setAdequateSupport("AS");
        yv.setRecommendChoice("RC");
        yv.setMeetNeedChoice("MNC");
        yv.setAdequateSupportChoice("ASC");
        assertEquals("YV:<br>R: RC<br>MN: MNC<br>AS: ASC", su.createYvString(s));
	}

	@Test
	public void testCreateFollowUpString() throws Exception {
		String piStr = "PI:<br>N/A";
		String fbStr = "FB:<br>N/A";
		String fnStr = "FN:<br>N/A";
		String yvStr = "YV:<br>N/A";
		String gbtmStr = "GBTM: N/A";
		
        yv.setRecommend("R");
        yv.setMeetNeed("MN");
        yv.setAdequateSupport("AS");
        yv.setRecommendChoice("RC");
        yv.setMeetNeedChoice("MNC");
        yv.setAdequateSupportChoice("ASC");

		assertEquals(piStr + "<br>" + yvStr + "<br>" + fbStr + "<br>" + fnStr + "<br>"
				+ gbtmStr, su.createFollowUpString(null));
		
		yvStr = "YV:<br>R: RC<br>MN: MNC<br>AS: ASC";
		gbtmStr = "GBTM: No";
		assertEquals(piStr + "<br>" + yvStr + "<br>" + fbStr + "<br>" + fnStr + "<br>"
				+ gbtmStr, su.createFollowUpString(s));
		pim.setTemplate(bcTemplates[5]);
		pim.setNumber("42");
		pim.setFactor("43");
		piStr = "PI:<br>BC n=42 f=43.";
		assertEquals(piStr + "<br>" + yvStr + "<br>" + fbStr + "<br>" + fnStr + "<br>"
				+ gbtmStr, su.createFollowUpString(s));
		
		fn.setOptions(new String[] { "O1", "O2" });
		fn.setOtherReason("OR");
		fnStr = "FN:<br>O1. O2. Other: OR.";
		assertEquals(piStr + "<br>" + yvStr + "<br>" + fbStr + "<br>" + fnStr + "<br>"
				+ gbtmStr, su.createFollowUpString(s));

		fb.setFeedback("My feedback");
		fbStr = "FB:<br>My feedback";
		assertEquals(piStr + "<br>" + yvStr + "<br>" + fbStr + "<br>" + fnStr + "<br>"
				+ gbtmStr, su.createFollowUpString(s));
	}
	
	@Test
	public void testCreateSurveyString() throws Exception {
		
		String piStr = "PI:<br>N/A";
		String fbStr = "FB:<br>N/A";
		String fnStr = "FN:<br>N/A";
		String yvStr = "YV:<br>N/A";
		String gbtmStr = "GBTM: N/A";
		String roStr = "RO:<br>N/A";

        yv.setRecommend("R");
        yv.setMeetNeed("MN");
        yv.setAdequateSupport("AS");
        yv.setRecommendChoice("RC");
        yv.setMeetNeedChoice("MNC");
        yv.setAdequateSupportChoice("ASC");

		assertEquals(
			piStr + "<br>" + yvStr + "<br>" + fbStr + "<br>" + fnStr + "<br>" +
			gbtmStr + "<br>" + roStr,
			su.createSurveyString(null));

		yvStr = "YV:<br>R: RC<br>MN: MNC<br>AS: ASC";
		s.setGetBackToMe(true);
		gbtmStr = "GBTM: Yes";
		assertEquals(
			piStr + "<br>" + yvStr + "<br>" + fbStr + "<br>" + fnStr + "<br>" +
			gbtmStr + "<br>" + roStr,
			su.createSurveyString(s));
		
		pim.setTemplate(bcTemplates[5]);
		pim.setNumber("42");
		pim.setFactor("43");
		piStr = "PI:<br>BC n=42 f=43.";
		assertEquals(
			piStr + "<br>" + yvStr + "<br>" + fbStr + "<br>" + fnStr + "<br>" +
			gbtmStr + "<br>" + roStr,
			su.createSurveyString(s));
		
		fn.setOptions(new String[] { "O1", "O2" });
		fn.setOtherReason("OR");
		fnStr = "FN:<br>O1. O2. Other: OR.";
		assertEquals(
			piStr + "<br>" + yvStr + "<br>" + fbStr + "<br>" + fnStr + "<br>" +
			gbtmStr + "<br>" + roStr,
			su.createSurveyString(s));

		fb.setFeedback("My FB");
		fbStr = "FB:<br>My FB";
		assertEquals(
			piStr + "<br>" + yvStr + "<br>" + fbStr + "<br>" + fnStr + "<br>" +
			gbtmStr + "<br>" + roStr,
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
		roStr = "RO:<br>My Book (typeId=1)<br>A poster (typeId=null)";

		assertEquals(
			piStr + "<br>" + yvStr + "<br>" + fbStr + "<br>" + fnStr + "<br>" +
			gbtmStr + "<br>" + roStr,
			su.createSurveyString(s));

	}
	
}
