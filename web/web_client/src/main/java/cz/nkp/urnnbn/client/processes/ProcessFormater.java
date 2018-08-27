package cz.nkp.urnnbn.client.processes;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
import cz.nkp.urnnbn.shared.dto.process.ProcessDTO;

import java.util.Date;

public class ProcessFormater {

    private final ProcessDTO process;
    private final ConstantsImpl constants;

    public ProcessFormater(ProcessDTO process, ConstantsImpl constants) {
        this.process = process;
        this.constants = constants;
    }

    String getScheduled() {
        return formatDateTime(process.getScheduled());
    }

    String getStarted() {
        return formatDateTime(process.getStarted());
    }

    String getFinished() {
        return formatDateTime(process.getFinished());
    }

    private String formatDateTime(Long millis) {
        if (millis == null) {
            return null;
        } else {
            Date now = new Date();
            Date then = new Date(millis);
            if (isToday(then, now)) {
                return DateTimeFormat.getFormat("HH:mm:ss").format(then);
            } else if (isSameYear(then, now)) {
                return DateTimeFormat.getFormat("dd. M. HH:mm").format(then);
            } else { //another year
                return DateTimeFormat.getFormat("dd. M. yyyy").format(then);
            }
        }
    }

    private boolean isSameYear(Date then, Date now) {
        return then.getYear() == now.getYear();
    }

    private boolean isToday(Date then, Date now) {
        return then.getYear() == now.getYear() &&
                then.getMonth() == now.getMonth() &&
                then.getDay() == now.getDay();
    }

    Long getDurationMillis() {
        if (process.getStarted() != null && process.getFinished() != null) {
            long start = process.getStarted();
            long end = process.getFinished();
            long duration = end - start;
            return duration;
        } else {
            return null;
        }
    }

    String getDurationFormatted() {
        if (process.getStarted() != null && process.getFinished() != null) {
            long start = process.getStarted();
            long end = process.getFinished();
            long duration = end - start;
            return formatDuration(duration);
        } else {
            return null;
        }
    }

    private String formatDuration(long duration) {
        long hours = duration / 3600000;
        duration = duration % 3600000;
        long minutes = duration / 60000;
        duration = duration % 60000;
        long seconds = duration / 1000;
        duration = duration % 1000;
        long millis = duration;
        StringBuilder builder = new StringBuilder();
        if (hours != 0) {
            builder.append(hours).append("h");
        }
        if (minutes != 0) {
            builder.append(' ').append(minutes).append("m");
        }
        if (seconds != 0) {
            builder.append(' ').append(seconds).append("s");
        }
        if (millis != 0 && (hours == 0 && minutes == 0)) {
            builder.append(' ').append(millis).append("ms");
        }
        return builder.toString();
    }


    public Widget getProcessState() {
        switch (process.getState()) {
            case SCHEDULED:
                return new HTML("<div style=\"color:blue\">SCHEDULED</style>");
            case CANCELED:
                return new HTML("<div style=\"color:red\">CANCELED</style>");
            case RUNNING:
                return new HTML("<div style=\"color:green\">RUNNING</style>");
            case FINISHED:
                return new HTML("<div style=\"color:black\">FINISHED</style>");
            case FAILED:
                return new HTML("<div style=\"color:red\">FAILED</style>");
            case KILLED:
                return new HTML("<div style=\"color:red\">KILLED</style>");
            default:
                return new HTML("");
        }
    }

    public Widget getProcessType() {
        switch (process.getType()) {
            case OAI_ADAPTER:
                return new HTML(constants.OAI_ADAPTER());
            case REGISTRARS_URN_NBN_CSV_EXPORT:
                return new HTML(constants.REGISTRARS_URN_NBN_CSV_EXPORT());
            case DI_URL_AVAILABILITY_CHECK:
                return new HTML(constants.DI_URL_AVAILABILITY_CHECK());
            case INDEXATION:
                return new HTML(constants.DOCS_INDEXATION());
            default:
                return new HTML("test");
        }
    }

    public String getParams() {
        String[] params = process.getParams();
        if (params == null || params.length == 0) {
            return "[ ]";
        } else {
            StringBuilder result = new StringBuilder(params.length * 2);
            result.append('[');
            for (int i = 0; i < params.length; i++) {
                result.append(params[i]);
                if (i == params.length - 1) {
                    result.append(',');
                }
            }
            result.append(']');
            return result.toString();
        }
    }
}


