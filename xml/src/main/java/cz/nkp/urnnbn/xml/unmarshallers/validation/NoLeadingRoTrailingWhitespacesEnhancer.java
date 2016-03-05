package cz.nkp.urnnbn.xml.unmarshallers.validation;

public class NoLeadingRoTrailingWhitespacesEnhancer implements ElementContentEnhancer {

    @Override
    public String toEnhancedValueOrNull(String string) {
        if (string != null) {
            String result = string.trim();
            if (!result.isEmpty()) {
                return result;
            }
        }
        return null;
    }

}
