package cz.nkp.urnnbn.czidlo_web_api.api.logs;

import java.io.IOException;
import java.time.LocalDate;

public interface SystemLogProvider {

    public String getLogs(Integer maxLines, LocalDate minDate, LocalDate dayAfterMaxDate) throws IOException;
}
