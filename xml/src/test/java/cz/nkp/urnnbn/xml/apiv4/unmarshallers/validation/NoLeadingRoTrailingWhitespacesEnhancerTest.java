package cz.nkp.urnnbn.xml.apiv4.unmarshallers.validation;

import junit.framework.TestCase;

public class NoLeadingRoTrailingWhitespacesEnhancerTest extends TestCase {

    public NoLeadingRoTrailingWhitespacesEnhancerTest(String testName) {
        super(testName);
    }

    public void testNll() {
        assertEquals(null, new NoLeadingRoTrailingWhitespacesEnhancer().toEnhancedValueOrNull(null));
    }

    public void testEmptyString() {
        assertEquals(null, new NoLeadingRoTrailingWhitespacesEnhancer().toEnhancedValueOrNull(""));
    }

    public void testOnlyWhitespaces() {
        assertEquals(null, new NoLeadingRoTrailingWhitespacesEnhancer().toEnhancedValueOrNull("   "));
    }

    public void testLeadingSpaces() {
        assertEquals("something", new NoLeadingRoTrailingWhitespacesEnhancer().toEnhancedValueOrNull("    something"));
    }

    public void testTrailingSpaces() {
        assertEquals("something", new NoLeadingRoTrailingWhitespacesEnhancer().toEnhancedValueOrNull("something     "));
    }

    public void testLeadingTrailingSpaces() {
        assertEquals("something", new NoLeadingRoTrailingWhitespacesEnhancer().toEnhancedValueOrNull("  something     "));
    }

}
