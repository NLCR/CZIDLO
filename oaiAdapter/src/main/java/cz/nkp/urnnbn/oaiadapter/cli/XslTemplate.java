package cz.nkp.urnnbn.oaiadapter.cli;

import java.io.File;

/**
 * Created by Martin Řehánek on 22.1.18.
 */
public class XslTemplate {
    private final String id;
    private final String xml;

    public XslTemplate(String id, String xml) {
        this.id = id;
        this.xml = xml;
    }

    public String getId() {
        return id;
    }

    public String getXml() {
        return xml;
    }

    public String getDescription() {
        return id;
    }

}
