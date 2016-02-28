package cz.nkp.urnnbn.api.v3;

public class XmlBuilder {

    private final String namespace;

    public XmlBuilder(String namespace) {
        this.namespace = namespace;
    }

    String buildImportDiDataMinimal(long digLibId, String url) {
        return String.format("<digitalInstance xmlns=\"%s\">"//
                + "<url>%s</url>" //
                + "<digitalLibraryId>%d</digitalLibraryId>"//
                + "</digitalInstance>", namespace, url, digLibId);
    }

    String buildRegisterDigDocDataMinimal() {
        return String.format("<import xmlns=\"%s\">"//
                + "<monograph>" //
                + "<titleInfo><title>TestTitle</title></titleInfo>"//
                + "</monograph>"//
                + "<digitalDocument/>"//
                + "</import>", namespace);
    }

    String buildRegisterDigDocDataMinimal(String urnNbn) {
        return String.format("<import xmlns=\"%s\">"//
                + "<monograph>" //
                + "<titleInfo><title>TestTitle</title></titleInfo>"//
                + "</monograph>"//
                + "<digitalDocument>"//
                + "<urnNbn><value>%s</value></urnNbn>"//
                + "</digitalDocument>"//
                + "</import>", namespace, urnNbn);
    }
}
