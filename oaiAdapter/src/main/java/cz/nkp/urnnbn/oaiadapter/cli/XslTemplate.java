package cz.nkp.urnnbn.oaiadapter.cli;

import java.io.File;

/**
 * Created by Martin Řehánek on 22.1.18.
 */
public class XslTemplate {
    private final String id;
    private final String xml;
    private final File file;


    public XslTemplate(String id, String xml, File file) {
        this.id = id;
        this.xml = xml;
        this.file = file;
    }

    public String getId() {
        return id;
    }

    public String getXml() {
        return xml;
    }

    public File getFile() {
        return file;
    }

    public String getDescription() {
        return id != null ? id : file != null ? file.getName() : "";
    }
}
