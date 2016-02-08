package cz.nkp.urnnbn.client.processes;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import cz.nkp.urnnbn.shared.dto.process.ProcessDTO;

public class ProcessFormater {

    private final ProcessDTO process;

    public ProcessFormater(ProcessDTO process) {
        this.process = process;
    }

    String getScheduled() {
        return process.getScheduled();
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
        // TODO: i18n
        switch (process.getType()) {
        case OAI_ADAPTER:
            return new HTML("OAI Adapter");
        case REGISTRARS_URN_NBN_CSV_EXPORT:
            return new HTML("Export URN:NBN");
        case DI_URL_AVAILABILITY_CHECK:
            return new HTML("DI availabiility check");
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
