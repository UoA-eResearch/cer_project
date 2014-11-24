package nz.ac.auckland.cer.common.util;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Test;

public class TemplateUtilTest extends TestCase {

    Map<String, String> params;
    String template;
    TemplateUtil tu;

    @Override
    public void setUp() {

        this.params = new HashMap<String, String>();
        this.params.put("__VAR1__", "John");
        this.params.put("__VAR2__", "Melbourne");
        this.template = "Hi __VAR1__, how is it in __VAR2__?";
        this.tu = new TemplateUtil();
    }

    @Test
    public void testSubstituteParameters_nullString() throws Exception {

        assertNull(tu.substituteParameters(null, this.params));
    }

    @Test
    public void testSubstituteParameters_nullParams() throws Exception {

        assertEquals(this.template, tu.substituteParameters(this.template, null));
    }

    @Test
    public void testSubstituteParameters_noParams() throws Exception {

        assertEquals(this.template, tu.substituteParameters(this.template, new HashMap<String, String>()));
    }

    @Test
    public void testSubstituteParameters_otherParams() throws Exception {

        this.params.remove("__VAR1__");
        this.params.remove("__VAR2__");
        this.params.put("__VAR3__", "Sydney");
        assertEquals(this.template, tu.substituteParameters(this.template, this.params));
    }

    @Test
    public void testSubstituteParameters_success() throws Exception {

        assertEquals("Hi John, how is it in Melbourne?", tu.substituteParameters(this.template, this.params));
    }

}
