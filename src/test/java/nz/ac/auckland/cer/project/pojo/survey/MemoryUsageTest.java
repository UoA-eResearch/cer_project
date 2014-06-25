package nz.ac.auckland.cer.project.pojo.survey;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class MemoryUsageTest {

    String memGb;
    MemoryUsage memUsage;

    @Before
    public void setup() {

        this.memGb = "50";
        this.memUsage = new MemoryUsage();
        memUsage.setMemGb(memGb);
    }

    @Test
    public void testToString() {

        assertEquals("Memory Usage: I'm using up to " + memGb + " GB RAM per job or MPI process.", memUsage.toString());
    }

}
