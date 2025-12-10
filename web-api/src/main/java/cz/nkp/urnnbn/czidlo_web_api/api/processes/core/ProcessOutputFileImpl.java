package cz.nkp.urnnbn.czidlo_web_api.api.processes.core;

import java.io.File;

public class ProcessOutputFileImpl implements ProcessOutputFile {
    private final String mimeType;
    private final String outFileName;
    private final File fileOnFs;

    public ProcessOutputFileImpl(String mimeType, String outFileName, File fileOnFs) {
        this.mimeType = mimeType;
        this.outFileName = outFileName;
        this.fileOnFs = fileOnFs;
    }

    @Override
    public String getOutMimeType() {
        return mimeType;
    }

    public String getOutFileName() {
        return outFileName;
    }

    public File getFileWithData() {
        return fileOnFs;
    }
}
