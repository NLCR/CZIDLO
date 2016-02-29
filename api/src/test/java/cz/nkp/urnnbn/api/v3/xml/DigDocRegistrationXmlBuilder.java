package cz.nkp.urnnbn.api.v3.xml;

import java.util.List;

import cz.nkp.urnnbn.api.v3.pojo.Predecessor;

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

    public String withPredecessors(String urnNbn, List<Predecessor> predecessors) {
        String result = String.format("<import xmlns=\"%s\">"//
                + "<monograph><titleInfo><title>TestTitle</title></titleInfo></monograph>"//
                + "<digitalDocument>", namespace);
        if (!predecessors.isEmpty() || urnNbn != null) {
            StringBuilder builder = new StringBuilder();
            builder.append("<urnNbn>");
            if (urnNbn != null) {
                builder.append("<value>").append(urnNbn).append("</value>");
            }
            for (Predecessor predecessor : predecessors) {
                builder.append("<predecessor value=\"").append(predecessor.urnNbn).append("\"");
                if (predecessor.note != null) {
                    builder.append(" note=\"").append(predecessor.note).append("\"");
                }
                builder.append("</predecessor>");
            }
            builder.append("/<urnNbn>");
            result += builder.toString();
        }
        result += "</digitalDocument></import>";
        return result;
    }

    public String withPredecessors(List<Predecessor> predecessors) {
        return withPredecessors(null, predecessors);
    }

}
