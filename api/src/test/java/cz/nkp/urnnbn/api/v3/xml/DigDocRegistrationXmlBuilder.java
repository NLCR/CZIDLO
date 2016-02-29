package cz.nkp.urnnbn.api.v3.xml;

public class DigDocRegistrationXmlBuilder {

    private final String namespace;

    public DigDocRegistrationXmlBuilder(String namespace) {
        this.namespace = namespace;
    }

    public String minimal() {
        return String.format("<import xmlns=\"%s\">"//
                + "<monograph>" //
                + "<titleInfo><title>TestTitle</title></titleInfo>"//
                + "</monograph>"//
                + "<digitalDocument/>"//
                + "</import>", namespace);
    }

    public String minimal(String urnNbn) {
        return String.format("<import xmlns=\"%s\">"//
                + "<monograph>" //
                + "<titleInfo><title>TestTitle</title></titleInfo>"//
                + "</monograph>"//
                + "<digitalDocument>"//
                + "<urnNbn><value>%s</value></urnNbn>"//
                + "</digitalDocument>"//
                + "</import>", namespace, urnNbn);
    }

    public String noNamespace() {
        return "<import>"//
                + "<monograph>" //
                + "<titleInfo><title>TestTitle</title></titleInfo>"//
                + "</monograph>"//
                + "<digitalDocument/>"//
                + "</import>";
    }

}
