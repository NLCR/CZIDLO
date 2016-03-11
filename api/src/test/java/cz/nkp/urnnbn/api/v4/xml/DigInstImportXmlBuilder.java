package cz.nkp.urnnbn.api.v4.xml;

import cz.nkp.urnnbn.api.Utils;

public class DigInstImportXmlBuilder {

    private final String namespace;

    public DigInstImportXmlBuilder(String namespace) {
        this.namespace = namespace;
    }

    public String minimal(long digLibId, String url) {
        return String.format("<digitalInstance xmlns=\"%s\">"//
                + "<url>%s</url>" //
                + "<digitalLibraryId>%d</digitalLibraryId>"//
                + "</digitalInstance>", namespace, Utils.xmlEscape(url), digLibId);
    }

    public String noNamespace(long digLibId, String url) {
        return String.format("<digitalInstance>"//
                + "<url>%s</url>" //
                + "<digitalLibraryId>%d</digitalLibraryId>"//
                + "</digitalInstance>", Utils.xmlEscape(url), digLibId);
    }

}
