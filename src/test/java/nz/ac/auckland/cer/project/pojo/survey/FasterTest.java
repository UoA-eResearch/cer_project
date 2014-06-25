package nz.ac.auckland.cer.project.pojo.survey;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class FasterTest {

    String factor;
    List<String> reasons;
    Faster faster;

    @Before
    public void setup() {

        this.factor = "5";
        this.faster = new Faster();
        this.reasons = new ArrayList<String>();
        this.faster.setReasons(reasons);
        faster.setFactor(factor);
    }

    @Test
    public void testToString_noReason() {

        assertNull(this.faster.toString());
    }

    @Test
    public void testToString_distMemPar() {

        reasons.add("distMemPar");
        assertEquals("My jobs run " + factor + " times faster than before, thanks to: " + faster.distMemPar + ".",
                faster.toString());
    }

    @Test
    public void testToString_sharedMemPar() {

        reasons.add("sharedMemPar");
        assertEquals("My jobs run " + factor + " times faster than before, thanks to: " + faster.sharedMemPar + ".",
                faster.toString());
    }

    @Test
    public void testToString_algorithmOptimisation() {

        reasons.add("algorithmOptimisation");
        assertEquals("My jobs run " + factor + " times faster than before, thanks to: " + faster.algorithmOptimisation + ".",
                faster.toString());
    }

    @Test
    public void testToString_buildOptimisation() {

        reasons.add("buildOptimisation");
        assertEquals("My jobs run " + factor + " times faster than before, thanks to: " + faster.buildOptimisation + ".",
                faster.toString());
    }

    @Test
    public void testToString_dontKnow() {

        reasons.add("dontKnow");
        assertEquals("My jobs run " + factor + " times faster than before, thanks to: " + faster.dontKnow + ".",
                faster.toString());
    }

    @Test
    public void testToString_otherReason() {

        faster.setOther("because of John Doe");
        assertEquals("My jobs run " + factor + " times faster than before, thanks to: because of John Doe",
                faster.toString());
    }

    @Test
    public void testToString_2Reasons() {

        reasons.add("distMemPar");
        reasons.add("sharedMemPar");
        assertEquals("My jobs run " + factor + " times faster than before, thanks to: " + faster.distMemPar + ", "
                + faster.sharedMemPar + ".", faster.toString());
    }

}
