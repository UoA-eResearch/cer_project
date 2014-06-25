package nz.ac.auckland.cer.project.pojo.survey;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class FeedbackTest {

    Feedback feedback;

    @Before
    public void setup() {

        this.feedback = new Feedback();
    }

    @Test
    public void testToString_noFeedback() {

        assertEquals("N/A", this.feedback.toString());
    }

    @Test
    public void testToString_withFeedback() {

        feedback.setFeedback("All good!");
        assertEquals("All good!", feedback.toString());
    }
    
}
