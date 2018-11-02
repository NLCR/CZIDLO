package cz.nkp.urnnbn.client.processes;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
import cz.nkp.urnnbn.client.i18n.MessagesImpl;
import cz.nkp.urnnbn.client.services.ProcessService;
import cz.nkp.urnnbn.client.services.ProcessServiceAsync;
import cz.nkp.urnnbn.shared.dto.process.XmlTransformationDTO;

import java.util.List;

public class TransformationsListPanel extends ScrollPanel {

    private final ProcessServiceAsync processService = GWT.create(ProcessService.class);
    private final ConstantsImpl constants = GWT.create(ConstantsImpl.class);
    private final MessagesImpl messages = GWT.create(MessagesImpl.class);
    private final List<XmlTransformationDTO> transformations;
    private final OaiAdapterConfigPanel superPanel;

    //TODO: mozna spojit s transformationTableWidget
    public TransformationsListPanel(OaiAdapterConfigPanel superPanel, List<XmlTransformationDTO> transformations) {
        super();
        this.superPanel = superPanel;
        this.transformations = transformations;
        setWidth("900px");
        //setHeight("500px");
        add(contentPanel());
    }

    private Widget contentPanel() {
        VerticalPanel result = new VerticalPanel();
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
        return result;
    }

}
