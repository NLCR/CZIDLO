package cz.nkp.urnnbn.xml.apiv6.unmarshallers.validation;

public class NoLeadingOrTrailingWhitespacesEnhancer implements ElementContentEnhancer {

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
