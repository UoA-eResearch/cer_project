package nz.ac.auckland.cer.project.pojo.survey;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class YourViewsTest {

	YourViews view;

	@Before
	public void setup() {
    	view = new YourViews();
        view.setRecommend("R");
        view.setMeetNeed("MN");
        view.setAdequateSupport("AS");
	}
	
    @Test
    public void testToString() {

        view.setRecommendChoice("RC");
        view.setMeetNeedChoice("MNC");
        view.setAdequateSupportChoice("ASC");
        assertEquals("R: RC<br>MN: MNC<br>AS: ASC", view.toString());
    }
    
    @Test
    public void testToString_null() {
        view.setRecommendChoice(null);
        view.setMeetNeedChoice(null);
        view.setAdequateSupportChoice(null);
        assertEquals("R: null<br>MN: null<br>AS: null", view.toString());
    }

}
