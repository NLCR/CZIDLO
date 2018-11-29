package cz.nkp.urnnbn.client.processes.mainPanel;

import com.google.gwt.core.client.GWT;

import cz.nkp.urnnbn.client.AbstractDialogBox;
import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
import cz.nkp.urnnbn.client.services.ProcessService;
import cz.nkp.urnnbn.client.services.ProcessServiceAsync;
import cz.nkp.urnnbn.shared.dto.UserDTO;

import java.util.logging.Logger;

public abstract class AbstractScheduleProcessDialogBox extends AbstractDialogBox {
    protected static final char SEPARATOR = ':';
    protected final ProcessServiceAsync processService = GWT.create(ProcessService.class);
    protected final ConstantsImpl constants = GWT.create(ConstantsImpl.class);
    protected final UserDTO user;

    public AbstractScheduleProcessDialogBox(UserDTO user) {
        super();
        this.user = user;
    }

    public abstract void open();

    void log(Logger logger, String[] params) {
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        for (int i = 0; i < params.length; i++) {
            builder.append(params[i]);
            if (i != params.length - 1) {
                builder.append(',');
            }
        }
        builder.append(']');
        logger.info("params:" + builder.toString());
    }

}
