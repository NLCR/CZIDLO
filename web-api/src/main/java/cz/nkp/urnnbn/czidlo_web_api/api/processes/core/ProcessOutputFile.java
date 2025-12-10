package cz.nkp.urnnbn.czidlo_web_api.api.processes.core;

import java.io.File;

public interface ProcessOutputFile {

    public String getOutMimeType();

    public String getOutFileName();

    public File getFileWithData();
}
