package cz.nkp.urnnbn.client.processes.oaiAdapterConfigPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
import cz.nkp.urnnbn.client.i18n.MessagesImpl;
import cz.nkp.urnnbn.client.resources.ProcessAdministrationCss;
import cz.nkp.urnnbn.client.services.ProcessService;
import cz.nkp.urnnbn.client.services.ProcessServiceAsync;
import cz.nkp.urnnbn.shared.dto.process.XmlTransformationDTO;
import cz.nkp.urnnbn.shared.dto.process.XmlTransformationDTOType;

import java.util.List;

public class TransformationsPanel extends ScrollPanel {

    private final ProcessServiceAsync processService = GWT.create(ProcessService.class);
    private final ConstantsImpl constants = GWT.create(ConstantsImpl.class);
    private final MessagesImpl messages = GWT.create(MessagesImpl.class);
    private final OaiAdapterConfigPanel superPanel;
    private final ProcessAdministrationCss css;
    private final XmlTransformationDTOType type;
    private final String title;
    private final List<XmlTransformationDTO> transformations;

    public TransformationsPanel(OaiAdapterConfigPanel superPanel, ProcessAdministrationCss css, XmlTransformationDTOType type, String title, List<XmlTransformationDTO> transformations) {
        super();
        this.superPanel = superPanel;
        this.css = css;
        this.type = type;
        this.title = title;
        this.transformations = transformations;
        setWidth("900px");
        add(contentPanel());
    }

    private Widget contentPanel() {
        VerticalPanel result = new VerticalPanel();
        //title
        Label label = new Label(title);
        label.addStyleName(css.processConfigH2());
        result.add(label);
        //table
        result.add(new TransformationTableWidget(transformations, constants,
                        new TransformationButtonAction.Operation() {
                            @Override
                            public void run(XmlTransformationDTO transformation) {
                                String url = "/processDataServer/transformations/" + transformation.getId() + "/xslt";
                                Window.open(url, "_blank", "enabled");
                            }
                        },
                        new TransformationButtonAction.Operation() {
                            @Override
                            public void run(XmlTransformationDTO transformation) {
                                String url = "/processDataServer/transformations/" + transformation.getId() + "/xsltFile";
                                Window.open(url, "_self", "enabled");
                            }
                        },
                        new TransformationButtonAction.Operation() {
                            @Override
                            public void run(XmlTransformationDTO transformation) {
                                processService.deleteXmlTransformation(transformation, new AsyncCallback<Void>() {

                                    @Override
                                    public void onSuccess(Void result) {
                                        superPanel.reloadTransformations();
                                    }

                                    @Override
                                    public void onFailure(Throwable caught) {
                                        Window.alert(messages.serverError(caught.getMessage()));
                                    }
                                });
                            }
                        }
                )
        );
        //add new transformation button
        //TODO: image button s "+" a hint namísto Nahrát z constants.upload(), tak Přidat transformaci
        result.add(new Button(constants.upload(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                new AddTransformationDialogBox(superPanel, type).show();
            }
        }));
        result.add(new HTML("<br>"));
        return result;
    }

}
