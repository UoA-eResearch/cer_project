package nz.ac.auckland.cer.project.pojo.survey;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class BaseCategoryTest {

    BaseCategory bc;
    String templates[] = {
        "BC n=__NUMBER__",
        "BC n=__NUMBER__ f=__FACTOR__",
        "BC n=__NUMBER__. __OPTIONS__",
        "BC n=__NUMBER__ f=__FACTOR__. __OPTIONS__",
        "__OPTIONS__",
    };

    @Before
    public void setup() {

    	this.bc = new BaseCategory();
    }

    @Test
    public void testToString_noOption() {

    	bc.setTemplate(templates[0]);
    	bc.setNumber("42");
    	assertEquals("BC n=42.", bc.toString());

    	bc.setTemplate(templates[1]);
    	bc.setFactor("3");
    	assertEquals("BC n=42 f=3.", bc.toString());
    }

    @Test
    public void testToString_oneOption() {

    	bc.setTemplate(templates[2]);
    	bc.setNumber("42");
    	bc.setOptions(new String[] { "O1" });
    	assertEquals("BC n=42. O1.", bc.toString());

    	bc.setTemplate(templates[3]);
    	bc.setFactor("3");
    	assertEquals("BC n=42 f=3. O1.", bc.toString());
    }

    @Test
    public void testToString_twoOptions() {

    	bc.setTemplate(templates[2]);
    	bc.setNumber("42");
    	bc.setOptions(new String[] { "O1", "O2" });
    	assertEquals("BC n=42. O1. O2.", bc.toString());

    	bc.setTemplate(templates[3]);
    	bc.setFactor("3");
    	assertEquals("BC n=42 f=3. O1. O2.", bc.toString());
    }

    @Test
    public void testToString_otherOption() {

    	bc.setTemplate(templates[2]);
    	bc.setNumber("42");
    	bc.setOtherReason("OR");
    	assertEquals("BC n=42. Other: OR.", bc.toString());

    	bc.setTemplate(templates[3]);
    	bc.setFactor("3");
    	assertEquals("BC n=42 f=3. Other: OR.", bc.toString());
    }

    @Test
    public void testToString_twoOptionsAndOtherReason() {

    	bc.setTemplate(templates[2]);
    	bc.setNumber("42");
    	bc.setOptions(new String[] { "O1", "O2" });
    	bc.setOtherReason("OR");
    	assertEquals("BC n=42. O1. O2. Other: OR.", bc.toString());

    	bc.setTemplate(templates[3]);
    	bc.setFactor("3");
    	assertEquals("BC n=42 f=3. O1. O2. Other: OR.", bc.toString());
    }

    @Test
    public void testToString_OptionsOnly() throws Exception {
    	bc.setTemplate(templates[4]);
    	bc.setFactor("3");
    	bc.setNumber("42");
    	bc.setOptions(new String[] { "O1", "O2" });
    	assertEquals("O1. O2.", bc.toString());
    	
    	bc.setFactor(null);
    	bc.setNumber(null);
    	bc.setOtherReason("OR");
    	assertEquals("O1. O2. Other: OR.", bc.toString());
    }

    @Test
    public void testToString_BadReplacement() throws Exception {
    	bc.setTemplate(templates[0]);
    	bc.setFactor("3");
    	assertNull(bc.toString());
    	
    	bc.setTemplate(templates[1]);
    	bc.setFactor(null);
    	bc.setNumber("42");
    	assertNull(bc.toString());

    	bc.setTemplate(templates[4]);
    	bc.setFactor("3");
    	bc.setNumber("42");
    	assertNull(bc.toString());
    }

    @Test
    public void testHasOptions() throws Exception {
    	bc.setOptions(null);
    	assertFalse(bc.hasOptions());

    	bc.setOptions(new String[] {});
    	assertFalse(bc.hasOptions());
    	
    	bc.setOptions(new String[] { "O1" });
    	assertTrue(bc.hasOptions());
    	
    	bc.setOptions(new String[] { "O1", "O2" });
    	assertTrue(bc.hasOptions());
    	
    	bc.setOptions(null);
    	assertFalse(bc.hasOptions());

    	bc.setOtherReason("OR");
    	assertTrue(bc.hasOptions());
    	
    	bc.setOptions(new String[] { "O1" });
    	assertTrue(bc.hasOptions());
    }

}
