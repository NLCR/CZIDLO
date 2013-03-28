package cz.nkp.urnnbn.client.tabs;

import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.IUploader.OnCancelUploaderHandler;
import gwtupload.client.IUploader.OnFinishUploaderHandler;
import gwtupload.client.IUploader.UploadedInfo;
import gwtupload.client.MultiUploader;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import cz.nkp.urnnbn.client.processes.UploadXmlTemplateDialogBox;
import cz.nkp.urnnbn.client.processes.XmlTransformationsPanel;
import cz.nkp.urnnbn.shared.dto.process.XmlTransformationDTOType;

public class TestTab extends SingleTabContentPanel {

	private String templateFile = null;

	// private FlowPanel panelImages = new FlowPanel();

	public TestTab(TabsPanel superPanel) {
		super(superPanel);
		// onLoad();
	}

	@Override
	public void onSelection() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onDeselectionSelection() {
		// TODO Auto-generated method stub
	}

	public void onLoad() {
		// Panel contentPanel = contentPanel();
		add(new XmlTransformationsPanel(this));

		// contentPanel.add(uploader());

	}

//	private Panel contentPanel() {
//		Panel contentPanel = new VerticalPanel();
//		contentPanel.add(templateManagementHeader());
//		contentPanel.add(ddRegistrationTemplateManagementPanel());
//		contentPanel.add(diImportTemplateManagementPanel());
//		return contentPanel;
//	}

//	private Widget templateManagementHeader() {
//		return new Label("Správa šablon");
//	}
//
//	private Widget ddRegistrationTemplateManagementPanel() {
//		// TODO: i18n
//		VerticalPanel panel = new VerticalPanel();
//		panel.add(new Label("Šablony pro registraci digitálního dokumentu"));
//		panel.add(new Button("vlozit sablonu - DD", new ClickHandler() {
//
//			@Override
//			public void onClick(ClickEvent event) {
//				new UploadXmlTemplateDialogBox(getActiveUser(), XmlTransformationDTOType.DIGITAL_DOCUMENT_REGISTRATION).show();
//			}
//		}));
//		return panel;
//	}
//
//	private Widget diImportTemplateManagementPanel() {
//		VerticalPanel panel = new VerticalPanel();
//		panel.add(new Label("Šablony pro import digitální instance"));
//		panel.add(new Button("vlozit sablonu - DI", new ClickHandler() {
//
//			@Override
//			public void onClick(ClickEvent event) {
//				new UploadXmlTemplateDialogBox(getActiveUser(), XmlTransformationDTOType.DIGITAL_INSTANCE_IMPORT).show();
//			}
//		}));
//		return panel;
//	}

	private MultiUploader uploader() {
		MultiUploader uploader = new MultiUploader();
		uploader.setMaximumFiles(1);

		uploader.addOnCancelUploadHandler(new OnCancelUploaderHandler() {

			@Override
			public void onCancel(IUploader uploader) {
				System.out.println("canceled " + uploader.fileUrl());
				templateFile = null;
			}
		});

		uploader.addOnFinishUploadHandler(new OnFinishUploaderHandler() {

			// singleUploader.addOnFinishUploadHandler(new
			// OnFinishUploaderHandler() {

			@Override
			public void onFinish(IUploader uploader) {
				if (uploader.getStatus() == Status.SUCCESS) {

					// new PreloadedImage(uploader.fileUrl(), showImage);

					// The server sends useful information to the client by
					// default
					UploadedInfo info = uploader.getServerInfo();
					System.out.println("File name " + info.name);
					System.out.println("File content-type " + info.ctype);
					System.out.println("File size " + info.size);

					// You can send any customized message and parse it
					System.out.println("Server message: " + info.message);
					templateFile = info.message;
				} else {
					System.out.println("Uploader status: " + uploader.getStatus());
				}
			}
		});
		return uploader;
	}

}
