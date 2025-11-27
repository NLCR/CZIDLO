package cz.nkp.urnnbn.czidlo_web_api.api.processes.process_manager;

import cz.nkp.urnnbn.czidlo_web_api.api.processes.core.ProcessType;

import java.io.File;
import java.nio.file.Path;

public class ProcessInMemoryOutputFile {
    private final String mimeType;
    private final String extension;
    private final File file;

    public ProcessInMemoryOutputFile(long processId, ProcessType processType) {
        this.mimeType = evalMimeType(processType);
        this.extension = evalExtension(processType);
        this.file = evalFile(processId);
    }

    private String evalExtension(ProcessType processType) {
        return switch (processType) {
            case REGISTRARS_URN_NBN_CSV_EXPORT, DI_URL_AVAILABILITY_CHECK -> ".csv";
            case OAI_ADAPTER, INDEXATION -> ".txt";
            case TEST -> ".json";
        };
    }

    private String evalMimeType(ProcessType processType) {
        return switch (processType) {
            case REGISTRARS_URN_NBN_CSV_EXPORT, DI_URL_AVAILABILITY_CHECK -> "text/csv; charset=UTF-8";
            case OAI_ADAPTER, INDEXATION -> "text/plain; charset=UTF-8";
            case TEST -> "application/json; charset=UTF-8";
        };
    }

    private File evalFile(long processId) throws IllegalArgumentException {
        Path path = Path.of("data", Long.toString(processId), "output" + extension);
        return new File(path.toUri());
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getExtension() {
        return extension;
    }

    public File getFile() {
        return file;
    }
}
