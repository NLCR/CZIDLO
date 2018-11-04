package cz.nkp.urnnbn.client.processes.oaiAdapterConfigPanel;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import cz.nkp.urnnbn.shared.dto.process.XmlTransformationDTO;

/**
 * Created by Martin Řehánek on 28.8.18.
 */
public class TransformationButtonCell extends AbstractCell<XmlTransformationDTO> {

    private final TransformationButtonAction action;

    public TransformationButtonCell(TransformationButtonAction action) {
        super(new String[]{"click", "keydown"});
        this.action = action;
    }

    public void onBrowserEvent(Context context, Element parent, XmlTransformationDTO value, NativeEvent event, ValueUpdater<XmlTransformationDTO> valueUpdater) {
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

    public void render(Context context, XmlTransformationDTO value, SafeHtmlBuilder sb) {
        sb.append(buildHtml(value));
    }

    private SafeHtml buildHtml(XmlTransformationDTO transformation) {
        SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
        if (transformation != null) {
            if (action != null) {
                int imgSize = 14;
                safeHtmlBuilder.appendHtmlConstant("<button ")
                        .appendHtmlConstant("type=\"button\" ")
                        .appendHtmlConstant("class=\"czidloImgBtn\" ")
                        //.appendHtmlConstant("style=\"margin: 2px;padding: 2px; cursor:pointer;\" ")
                        .appendHtmlConstant(">")
                        .appendHtmlConstant("<img src=\"" + action.getBtnImgUrl() + "\" title=\"" + action.getHint() + "\" width=\"" + imgSize + "px\"" + "/>")
                        .appendHtmlConstant("</button>").toSafeHtml();
            }
        }
        return safeHtmlBuilder.toSafeHtml();
    }


    protected void onEnterKeyDown(Context context, Element parent, XmlTransformationDTO transformation, NativeEvent event, ValueUpdater<XmlTransformationDTO> valueUpdater) {
        action.getOperation().run(transformation);

    }

}
