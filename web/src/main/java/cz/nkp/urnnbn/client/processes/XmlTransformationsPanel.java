package cz.nkp.urnnbn.client.processes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
import cz.nkp.urnnbn.client.i18n.MessagesImpl;
import cz.nkp.urnnbn.client.resources.ProcessAdministrationCss;
import cz.nkp.urnnbn.client.resources.Resources;
import cz.nkp.urnnbn.client.services.ProcessService;
import cz.nkp.urnnbn.client.services.ProcessServiceAsync;
import cz.nkp.urnnbn.client.tabs.SingleTabContentPanel;
import cz.nkp.urnnbn.shared.dto.UserDTO;
import cz.nkp.urnnbn.shared.dto.process.XmlTransformationDTO;
import cz.nkp.urnnbn.shared.dto.process.XmlTransformationDTOType;

public class XmlTransformationsPanel extends VerticalPanel {

	private final ProcessAdministrationCss css = initCss();
	private final ProcessServiceAsync processService = GWT.create(ProcessService.class);
	private final ConstantsImpl constants = GWT.create(ConstantsImpl.class);
	private final SingleTabContentPanel superPanel;

	private List<XmlTransformationDTO> ddRegistrationTransformations = Collections.<XmlTransformationDTO> emptyList();
	private List<XmlTransformationDTO> diImportTransformations = Collections.<XmlTransformationDTO> emptyList();

	private ProcessAdministrationCss initCss() {
		Resources resources = GWT.create(Resources.class);
		ProcessAdministrationCss result = resources.ProcessAdministrationCss();
		result.ensureInjected();
		return result;
	}

	public XmlTransformationsPanel(SingleTabContentPanel superPanel) {
		super();
		this.superPanel = superPanel;
		reload();
	}

	void reload() {
		clear();
		// add(templateManagementHeader());
		add(ddRegistrationTemplateManagementPanel());
		add(diImportTemplateManagementPanel());
	}

	// private Widget templateManagementHeader() {
	// Label label = new Label("Správa šablon pro oai adapter");
	// label.addStyleName(css.processListHeading());
	// return label;
	// }

	private Widget ddRegistrationTemplateManagementPanel() {
		VerticalPanel panel = new VerticalPanel();
		Label label = new Label(constants.processOaiAdapterTransformationsDDRegistrationTitle());
		label.addStyleName(css.processListHeading());
		panel.add(label);

		panel.add(new Button(constants.processOaiAdapterTransformationUpload(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				new UploadXmlTemplateDialogBox(XmlTransformationsPanel.this, XmlTransformationDTOType.DIGITAL_DOCUMENT_REGISTRATION).show();
			}
		}));
		panel.add(ddRegistrationTemplatesPanel());
		panel.add(new Label(""));
		return panel;
	}

	private Widget ddRegistrationTemplatesPanel() {
		Panel result = new VerticalPanel();
		Label label = new Label(constants.processOaiAdapterTransformationList());
		label.addStyleName(css.planProcessHeading());
		result.add(label);
		result.add(new TransformationsListPanel(this, ddRegistrationTransformations));
		return result;
	}

	private Widget diImportTemplateManagementPanel() {
		VerticalPanel panel = new VerticalPanel();
		Label label = new Label(constants.processOaiAdapterTransformationsDIImportTitle());
		label.addStyleName(css.processListHeading());
		panel.add(label);
		panel.add(new Button(constants.processOaiAdapterTransformationUpload(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				new UploadXmlTemplateDialogBox(XmlTransformationsPanel.this, XmlTransformationDTOType.DIGITAL_INSTANCE_IMPORT).show();
			}
		}));
		panel.add(diImportTemplatesPanel());
		return panel;
	}

	private Widget diImportTemplatesPanel() {
		Panel result = new VerticalPanel();
		Label label = new Label(constants.processOaiAdapterTransformationList());
		label.addStyleName(css.planProcessHeading());
		result.add(label);
		result.add(new TransformationsListPanel(this, diImportTransformations));
		return result;
	}

	private UserDTO getActiveUser() {
		return superPanel.getActiveUser();
	}

	@Override
	public void onLoad() {
		reloadTransformations();
		// reload();
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
				// nothing
			}
		});
	}

	public List<XmlTransformationDTO> getDdRegistrationTransformations() {
		return ddRegistrationTransformations;
	}

	public List<XmlTransformationDTO> getDiImportTransformations() {
		return diImportTransformations;
	}
}
