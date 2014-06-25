package nz.ac.auckland.cer.project.pojo.survey;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class MoreTest {

    String number;
    String factor;
    More more;

    @Before
    public void setup() {

        this.number = "50";
        this.factor = "5";
        this.more = new More();
    }

    @Test
    public void testToString() {

        more.setNumber(number);
        more.setFactor(factor);
        assertEquals("I can now run up to " + number + " jobs at the same time, " + "which is " + factor
                + " times more concurrent jobs than before.", more.toString());
    }

    @Test
    public void testToString_noMore() {

        assertEquals(null, more.toString());
    }

}
