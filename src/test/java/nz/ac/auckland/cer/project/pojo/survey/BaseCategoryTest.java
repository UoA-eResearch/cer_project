package nz.ac.auckland.cer.project.pojo.survey;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class BiggerTest {

    String factor;
    List<String> reasons;
    Bigger bigger;

    @Before
    public void setup() {

        this.factor = "5";
        this.bigger = new Bigger();
        this.reasons = new ArrayList<String>();
        bigger.setReasons(reasons);
        bigger.setFactor(factor);
    }

    @Test
    public void testToString_noReason() {

        assertNull(this.bigger.toString());
    }

    @Test
    public void testToString_sharedMemPar() {

        reasons.add("moreMem");
        assertEquals("I can run larger jobs now, up to " + factor + " times larger than before, thanks to: "
                + bigger.moreMem + ".", bigger.toString());
    }

    @Test
    public void testToString_distMemPar() {

        reasons.add("distMemPar");
        assertEquals("I can run larger jobs now, up to " + factor + " times larger than before, thanks to: "
                + bigger.distMemPar + ".", bigger.toString());
    }

    @Test
    public void testToString_moreDisk() {

        reasons.add("moreDisk");
        assertEquals("I can run larger jobs now, up to " + factor + " times larger than before, thanks to: "
                + bigger.moreDisk + ".", bigger.toString());
    }

    @Test
    public void testToString_dontKnow() {

        reasons.add("dontKnow");
        assertEquals("I can run larger jobs now, up to " + factor + " times larger than before, thanks to: "
                + bigger.dontKnow + ".", bigger.toString());
    }

    @Test
    public void testToString_otherReason() {

        bigger.setOther("because of John Doe");
        assertEquals("I can run larger jobs now, up to " + factor
                + " times larger than before, thanks to: because of John Doe", bigger.toString());
    }

    @Test
    public void testToString_2Reasons() {

        reasons.add("moreMem");
        reasons.add("distMemPar");
        assertEquals("I can run larger jobs now, up to " + factor + " times larger than before, thanks to: "
                + bigger.moreMem + ", " + bigger.distMemPar + ".", bigger.toString());
    }

}
