package cz.nkp.urnnbn.api.v3;

public class XmlBuilder {

    public static String buildImportDiDataMinimal(long digLibId, String url) {
        return String.format("<digitalInstance xmlns=\"http://resolver.nkp.cz/v3/\">"//
                + "<url>%s</url>" //
                + "<digitalLibraryId>%d</digitalLibraryId>"//
                + "</digitalInstance>", url, digLibId);
    }

    public static String buildRegisterDigDocDataMinimal() {
        return "<import xmlns=\"http://resolver.nkp.cz/v3/\">"//
                + "<monograph>" //
                + "<titleInfo><title>TestTitle</title></titleInfo>"//
                + "</monograph>"//
                + "<digitalDocument/>"//
                + "</import>";
    }
}
