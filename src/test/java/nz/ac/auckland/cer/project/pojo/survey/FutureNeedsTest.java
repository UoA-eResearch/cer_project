package nz.ac.auckland.cer.project.pojo.survey;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class FutureNeedsTest {

    List<String> comments;
    FutureNeeds fn;

    @Before
    public void setup() {

        this.fn = new FutureNeeds();
        this.comments = new ArrayList<String>();
        this.fn.setComments(comments);
    }

    @Test
    public void testToString_noFeedback() {

        assertEquals("N/A", fn.toString());
    }

    @Test
    public void testToString_fastInterconnect() {
        
        comments.add("fastInterconnect");
        assertEquals("fast interconnect between the machines for my MPI jobs.", fn.toString());        
    }
    
    @Test
    public void testToString_moreCPUs() {

        comments.add("moreCpus");
        assertEquals("more CPU cores per cluster node to run larger multi-threaded jobs.", fn.toString());
    }

    @Test
    public void testToString_moreMemory() {

        comments.add("moreMemory");
        assertEquals("more memory per compute node.", fn.toString());
    }

    @Test
    public void testToString_moreGpus() {

        comments.add("moreGpus");
        assertEquals("more GPUs.", fn.toString());
    }

    @Test
    public void testToString_morePhis() {

        comments.add("morePhis");
        assertEquals("more Intel Xeon Phi's.", fn.toString());
    }

    @Test
    public void testToString_moreDisk() {

        comments.add("moreDisk");
        assertEquals("more disk space.", fn.toString());
    }

    @Test
    public void testToString_shorterWaitTimes() {

        comments.add("shorterWaitTimes");
        assertEquals("shorter wait times.", fn.toString());
    }

    @Test
    public void testToString_moreScalingAdvice() {

        comments.add("moreScalingAdvice");
        assertEquals("advice on how to parallelise/scale/tune my software.", fn.toString());
    }

    @Test
    public void testToString_moreSupport() {

        comments.add("moreSupport");
        assertEquals("more/better general support.", fn.toString());
    }

    @Test
    public void testToString_other() {

        fn.setOther("I need this and that.");
        assertEquals("I need this and that.", fn.toString());
    }

    @Test
    public void testToString_moreDiskMoreGpus() {

        comments.add("moreDisk");
        comments.add("moreGpus");
        comments.add("moreScalingAdvice");
        fn.setOther("I need this and that.");
        assertEquals("more disk space, more GPUs, advice on how to parallelise/scale/tune my software."
                + " I need this and that.", fn.toString());
    }

}
