package cz.nkp.urnnbn.client.processes.oaiAdapterConfigPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import cz.nkp.urnnbn.client.Utils;
import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
import cz.nkp.urnnbn.client.processes.ProcessAdministrationTab;
import cz.nkp.urnnbn.client.resources.ProcessAdministrationCss;
import cz.nkp.urnnbn.client.resources.Resources;
import cz.nkp.urnnbn.client.services.ProcessService;
import cz.nkp.urnnbn.client.services.ProcessServiceAsync;
import cz.nkp.urnnbn.shared.dto.process.XmlTransformationDTO;
import cz.nkp.urnnbn.shared.exceptions.SessionExpirationException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import static cz.nkp.urnnbn.shared.dto.process.XmlTransformationDTOType.DIGITAL_DOCUMENT_REGISTRATION;
import static cz.nkp.urnnbn.shared.dto.process.XmlTransformationDTOType.DIGITAL_INSTANCE_IMPORT;

/**
 * Created by Martin Řehánek on 2.11.18.
 */
public class OaiAdapterConfigPanel extends VerticalPanel {

    private static final Logger LOGGER = Logger.getLogger(OaiAdapterConfigPanel.class.getName());
    private final ProcessAdministrationCss css = initCss();
    private final ProcessServiceAsync processService = GWT.create(ProcessService.class);
    private final ConstantsImpl constants = GWT.create(ConstantsImpl.class);
    private final ProcessAdministrationTab superPanel;

    private List<XmlTransformationDTO> ddRegistrationTransformations = Collections.emptyList();
    private List<XmlTransformationDTO> diImportTransformations = Collections.emptyList();

    private ProcessAdministrationCss initCss() {
        Resources resources = GWT.create(Resources.class);
        ProcessAdministrationCss result = resources.ProcessAdministrationCss();
        result.ensureInjected();
        return result;
    }

    public OaiAdapterConfigPanel(ProcessAdministrationTab superPanel) {
        this.superPanel = superPanel;
    }

    public void onLoad() {
        reloadTransformations();
        reload();
    }

    void reload() {
        clear();
        add(contentPanel());
    }

    private IsWidget contentPanel() {
        VerticalPanel result = new VerticalPanel();
        //TODO: i18n
        result.add(new Button("← Zpět na správu procesů", new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                superPanel.selectProcessAdminPanel();
            }
        }));
        //HEADER
        result.add(header());
        //CONFIG
        result.add(ddRegistrationPanel());
        result.add(diImportTemplateManagementPanel());
        return result;
    }


    private Widget header() {
        //TODO:i18n
        Label label = new Label("Nastavení procesu OAI Adapter");
        label.addStyleName(css.processListHeading());
        return label;
    }

    private Widget ddRegistrationPanel() {
        return new TransformationsPanel(this, css,
                DIGITAL_DOCUMENT_REGISTRATION, constants.processOaiAdapterTransformationsDDRegistrationTitle(), ddRegistrationTransformations);
    }

    private Widget diImportTemplateManagementPanel() {
        return new TransformationsPanel(this, css,
                DIGITAL_INSTANCE_IMPORT, constants.processOaiAdapterTransformationsDIImportTitle(), diImportTransformations);
    }

    void reloadTransformations() {
        processService.getXmlTransformationsOfUser(new AsyncCallback<List<XmlTransformationDTO>>() {

            @Override
            public void onSuccess(List<XmlTransformationDTO> result) {
                sortAndSaveTransformations(result);
                reload();
            }

            private void sortAndSaveTransformations(List<XmlTransformationDTO> transformations) {
                ddRegistrationTransformations = new ArrayList<XmlTransformationDTO>();
                diImportTransformations = new ArrayList<XmlTransformationDTO>();
                for (XmlTransformationDTO transformation : transformations) {
                    switch (transformation.getType()) {
                        case DIGITAL_DOCUMENT_REGISTRATION:
                            ddRegistrationTransformations.add(transformation);
                            break;
                        case DIGITAL_INSTANCE_IMPORT:
                            diImportTransformations.add(transformation);
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                if (caught instanceof SessionExpirationException) {
                    Utils.sessionExpirationRedirect();
                } else {
                    LOGGER.severe("Error loading XSLTs: " + caught.getMessage());
                }
            }
        });
    }

    public List<XmlTransformationDTO> getDdRegistrationTransformations() {
        return ddRegistrationTransformations;
    }

    public List<XmlTransformationDTO> getDiImportTransformations() {
        return diImportTransformations;
    }


    public void onSelected() {

    }

    public void onDeselected() {
    }
}