package cz.nkp.urnnbn.client.processes;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
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
    private final XmlTransformationsPanel superPanel;

    public TransformationsListPanel(XmlTransformationsPanel superPanel, List<XmlTransformationDTO> transformations) {
        super();
        this.superPanel = superPanel;
        this.transformations = transformations;
        setWidth("900px");
        setHeight("100px");
        add(contentPanel());
    }

    private Widget contentPanel() {
        VerticalPanel result = new VerticalPanel();
        result.add(transformationsListHeader());
        for (XmlTransformationDTO transformation : transformations) {
            result.add(transformationWidget(transformation));
        }
        return result;
    }

    private Widget transformationsListHeader() {
        HorizontalPanel panel = new HorizontalPanel();
        panel.setWidth("800px");

        // title
        Widget titelLabel = headerFormated(constants.processOaiAdapterTransformationTitle());
        panel.add(titelLabel);
        panel.setCellWidth(titelLabel, "23%");

        // description
        Widget descriptionLabel = headerFormated(constants.processOaiAdapterTransformationDescription());
        panel.add(descriptionLabel);
        panel.setCellWidth(descriptionLabel, "30%");

        // created
        Widget createdLabel = headerFormated(constants.processOaiAdapterTransformationCreated());
        panel.add(createdLabel);
        panel.setCellWidth(createdLabel, "20%");

        // buttons
        Widget deleteButtonLabel = headerFormated("");
        panel.add(deleteButtonLabel);
        panel.setCellWidth(deleteButtonLabel, "27%");

        return panel;
    }

    private Widget headerFormated(String string) {
        return new HTML("<div style=\"color:grey\">" + string + "</style>");
    }

    private Widget transformationWidget(XmlTransformationDTO transformation) {
        HorizontalPanel panel = new HorizontalPanel();
        panel.setWidth("800px");
        // panel.setWidth("100%");

        // name
        Widget nameLabel = new Label(transformation.getName());
        panel.add(nameLabel);
        panel.setCellWidth(nameLabel, "23%");

        // description
        Widget descriptionLabel = new Label(transformation.getDescription());
        panel.add(descriptionLabel);
        panel.setCellWidth(descriptionLabel, "30%");

        // created
        Label createdLabel = new Label(transformation.getCreated());
        panel.add(createdLabel);
        panel.setCellWidth(createdLabel, "20%");

        // show transformation button
        Widget showTransformationButton = showTransformationButton(transformation);
        panel.add(showTransformationButton);
        panel.setCellWidth(showTransformationButton, "9%");

        // download transformation button
        Widget downloadTransformationButton = downloadTransformationButton(transformation);
        panel.add(downloadTransformationButton);
        panel.setCellWidth(downloadTransformationButton, "9%");

        // delete transformation button
        Widget deleteTransformationButton = deleteTransformationButton(transformation);
        panel.add(deleteTransformationButton);
        panel.setCellWidth(deleteTransformationButton, "9%");

        return panel;
    }

    private Widget showTransformationButton(final XmlTransformationDTO transformation) {
        return new Button(constants.show(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                String url = "/processDataServer/transformations/" + transformation.getId() + "/xslt";
                Window.open(url, "_blank", "enabled");
            }
        });
    }

    private Widget downloadTransformationButton(final XmlTransformationDTO transformation) {
        return new Button(constants.download(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                String url = "/processDataServer/transformations/" + transformation.getId() + "/xsltFile";
                Window.open(url, "_self", "enabled");
            }
        });
    }

    private Widget deleteTransformationButton(final XmlTransformationDTO transformation) {
        return new Button(constants.delete(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
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
        });
    }

}
