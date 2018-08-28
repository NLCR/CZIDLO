package cz.nkp.urnnbn.client.processes;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import cz.nkp.urnnbn.shared.dto.process.ProcessDTO;
import cz.nkp.urnnbn.shared.dto.process.ProcessDTOState;

/**
 * Created by Martin Řehánek on 28.8.18.
 */
public class ProcessButtonCell extends AbstractCell<ProcessDTO> {

    private final ProcessButtonAction action;

    public ProcessButtonCell(ProcessButtonAction action) {
        super(new String[]{"click", "keydown"});
        this.action = action;
    }

    public void onBrowserEvent(Context context, Element parent, ProcessDTO value, NativeEvent event, ValueUpdater<ProcessDTO> valueUpdater) {
        super.onBrowserEvent(context, parent, value, event, valueUpdater);
        if ("click".equals(event.getType())) {
            EventTarget eventTarget = event.getEventTarget();
            if (!Element.is(eventTarget)) {
                return;
            }

            if (parent.getFirstChildElement().isOrHasChild(Element.as(eventTarget))) {
                this.onEnterKeyDown(context, parent, value, event, valueUpdater);
            }
        }
    }

    public void render(Context context, ProcessDTO value, SafeHtmlBuilder sb) {
        sb.append(buildHtml(value));
    }

    private SafeHtml buildHtml(ProcessDTO process) {
        SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
        if (process != null) {
            if (action != null && matchesCurrentState(action, process)) {
                int imgSize = 14;
                safeHtmlBuilder.appendHtmlConstant("<button ")
                        .appendHtmlConstant("type=\"button\" ")
                        .appendHtmlConstant("style=\"margin: 2px;padding: 2px;\" ")
                        .appendHtmlConstant(">")
                        .appendHtmlConstant("<img src=\"" + action.getBtnImgUrl() + "\" title=\"" + action.getHint() + "\" width=\"" + imgSize + "px\"" + "/>")
                        .appendHtmlConstant("</button>").toSafeHtml();
            }
        }
        return safeHtmlBuilder.toSafeHtml();
    }

    private boolean matchesCurrentState(ProcessButtonAction action, ProcessDTO process) {
        if (action != null && action.getStates() != null && process != null && process.getState() != null) {
            for (ProcessDTOState state : action.getStates()) {
                if (state == process.getState()) {
                    return true;
                }
            }
        }
        return false;
    }


    protected void onEnterKeyDown(Context context, Element parent, ProcessDTO process, NativeEvent event, ValueUpdater<ProcessDTO> valueUpdater) {
        action.getOperation().run(process);

    }

}
