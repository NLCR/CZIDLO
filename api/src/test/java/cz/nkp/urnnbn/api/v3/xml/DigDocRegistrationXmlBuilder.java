package cz.nkp.urnnbn.api.v3.xml;

import java.util.List;

import cz.nkp.urnnbn.api.v3.pojo.Predecessor;
import cz.nkp.urnnbn.api.v3.pojo.RsId;

public class DigDocRegistrationXmlBuilder {

    private final String namespace;

    public DigDocRegistrationXmlBuilder(String namespace) {
        this.namespace = namespace;
    }

    public String noNamespace() {
        return "<import>"//
                + "<monograph>" //
                + "<titleInfo><title>TestTitle</title></titleInfo>"//
                + "</monograph>"//
                + "<digitalDocument/>"//
                + "</import>";
    }

    public String build(String urnNbn, List<Predecessor> predecessors, List<RsId> ids) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("<import xmlns=\"%s\">", namespace));
        builder.append("<monograph><titleInfo><title>TestTitle</title></titleInfo></monograph>");
        builder.append("<digitalDocument>");
        // URN:NBN, predecessors
        if (urnNbn == null && (predecessors != null && !predecessors.isEmpty())) {// predecessors only
            builder.append("<urnNbn>");
            for (Predecessor predecessor : predecessors) {
                if (predecessor.note == null) {
                    builder.append(String.format("<predecessor value=\"%s\"/>", predecessor.urnNbn));
                } else {
                    builder.append(String.format("<predecessor value=\"%s\" note=\"%s\"/>", predecessor.urnNbn, predecessor.note));
                }
            }
            builder.append("</urnNbn>");
        } else if (urnNbn != null && (predecessors == null || predecessors.isEmpty())) {// urn only
            builder.append(String.format("<urnNbn><value>%s</value></urnNbn>", urnNbn));
        } else if (urnNbn != null && (predecessors != null && !predecessors.isEmpty())) {// both
            builder.append("<urnNbn>");
            builder.append(String.format("<value>%s</value>", urnNbn));
            for (Predecessor predecessor : predecessors) {
                if (predecessor.note == null) {
                    builder.append(String.format("<predecessor value=\"%s\"/>", predecessor.urnNbn));
                } else {
                    builder.append(String.format("<predecessor value=\"%s\" note=\"%s\"/>", predecessor.urnNbn, predecessor.note));
                }
            }
            builder.append("</urnNbn>");
        }
        // REGISTRAR-SCOPE-IDs
        if (ids != null && !ids.isEmpty()) {
            builder.append("<registrarScopeIdentifiers>");
            for (RsId id : ids) {
                builder.append(String.format("<id type=\"%s\">%s</id>", id.type, id.value));
            }
            builder.append("</registrarScopeIdentifiers>");
        }
        builder.append("</digitalDocument>");
        builder.append("</import>");
        return builder.toString();
    }

    public String minimal() {
        return build(null, null, null);
    }

    public String withUrnNbn(String urnNbn) {
        return build(urnNbn, null, null);
    }

    public String withPredecessors(List<Predecessor> predecessors) {
        return build(null, predecessors, null);
    }

    public String withPredecessors(String urnNbn, List<Predecessor> predecessors) {
        return build(urnNbn, predecessors, null);
    }

    public String withRsIds(String urnNbn, List<RsId> rsIds) {
        return build(urnNbn, null, rsIds);
    }

}
