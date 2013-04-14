package cz.nkp.urnnbn.client.tabs;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import cz.nkp.urnnbn.client.services.LogsService;
import cz.nkp.urnnbn.client.services.LogsServiceAsync;

public class TestTab extends SingleTabContentPanel {

	private final LogsServiceAsync logsService = GWT.create(LogsService.class);
	private static final int TIMER_INTERVAL = 1000;
	private final Timer refreshTabTimer = initTimer();
	private List<String> adminLogsList = new ArrayList<String>();
	private long adminLogsLastUpadated = 0;

	private Timer initTimer() {
		return new Timer() {

			@Override
			public void run() {
				loadAdminLogsIfChanged();
			}
		};
	}

	public TestTab(TabsPanel superPanel) {
		super(superPanel);
		// onLoad();
	}

	@Override
	public void onSelection() {
		refreshTabTimer.scheduleRepeating(1000);
	}

	@Override
	public void onDeselectionSelection() {
		refreshTabTimer.cancel();
	}

	public void onLoad() {
		reload();
		// Panel contentPanel = contentPanel();
		// add(new XmlTransformationsPanel(this));
		// contentPanel.add(uploader());
		// TODO: volat timerem
		refreshTabTimer.scheduleRepeating(TIMER_INTERVAL);
		// loadAdminLogsIfChanged();
	}

	private void loadAdminLogsIfChanged() {
		logsService.getAdminLogLastUpdatedTime(new AsyncCallback<Long>() {

			@Override
			public void onSuccess(Long result) {
				if (result > adminLogsLastUpadated) {
					adminLogsLastUpadated = result;
					loadAdminLogs();
				}
			}

			private void loadAdminLogs() {
				logsService.getAdminLogs(new AsyncCallback<List<String>>() {

					@Override
					public void onSuccess(List<String> result) {
						if (adminLogsList == null) {
							Window.alert("adminLogsList==null");
						} else {
							adminLogsList = result;
							reload();
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						//Window.alert(constants.serverError() + ": " + caught.getMessage());
					}
				});
			}

			@Override
			public void onFailure(Throwable caught) {
				//Window.alert(constants.serverError() + ": " + caught.getMessage());
			}
		});
	}

	private void reload() {
		clear();
		add(adminLogsPanel());
	}

	private Widget adminLogsPanel() {
		VerticalPanel panel = new VerticalPanel();
		panel.add(new Label("LOGY"));
		for (String log : adminLogsList) {
			panel.add(new Label(log));
		}
		return panel;
	}

	// private Panel contentPanel() {
	// Panel contentPanel = new VerticalPanel();
	// contentPanel.add(templateManagementHeader());
	// contentPanel.add(ddRegistrationTemplateManagementPanel());
	// contentPanel.add(diImportTemplateManagementPanel());
	// return contentPanel;
	// }

	// private Widget templateManagementHeader() {
	// return new Label("Správa šablon");
	// }
	//
	// private Widget ddRegistrationTemplateManagementPanel() {
	// // TODO: i18n
	// VerticalPanel panel = new VerticalPanel();
	// panel.add(new Label("Šablony pro registraci digitálního dokumentu"));
	// panel.add(new Button("vlozit sablonu - DD", new ClickHandler() {
	//
	// @Override
	// public void onClick(ClickEvent event) {
	// new UploadXmlTemplateDialogBox(getActiveUser(),
	// XmlTransformationDTOType.DIGITAL_DOCUMENT_REGISTRATION).show();
	// }
	// }));
	// return panel;
	// }
	//
	// private Widget diImportTemplateManagementPanel() {
	// VerticalPanel panel = new VerticalPanel();
	// panel.add(new Label("Šablony pro import digitální instance"));
	// panel.add(new Button("vlozit sablonu - DI", new ClickHandler() {
	//
	// @Override
	// public void onClick(ClickEvent event) {
	// new UploadXmlTemplateDialogBox(getActiveUser(),
	// XmlTransformationDTOType.DIGITAL_INSTANCE_IMPORT).show();
	// }
	// }));
	// return panel;
	// }

	// private MultiUploader uploader() {
	// MultiUploader uploader = new MultiUploader();
	// uploader.setMaximumFiles(1);
	//
	// uploader.addOnCancelUploadHandler(new OnCancelUploaderHandler() {
	//
	// @Override
	// public void onCancel(IUploader uploader) {
	// System.out.println("canceled " + uploader.fileUrl());
	// templateFile = null;
	// }
	// });
	//
	// uploader.addOnFinishUploadHandler(new OnFinishUploaderHandler() {
	//
	// // singleUploader.addOnFinishUploadHandler(new
	// // OnFinishUploaderHandler() {
	//
	// @Override
	// public void onFinish(IUploader uploader) {
	// if (uploader.getStatus() == Status.SUCCESS) {
	//
	// // new PreloadedImage(uploader.fileUrl(), showImage);
	//
	// // The server sends useful information to the client by
	// // default
	// UploadedInfo info = uploader.getServerInfo();
	// System.out.println("File name " + info.name);
	// System.out.println("File content-type " + info.ctype);
	// System.out.println("File size " + info.size);
	//
	// // You can send any customized message and parse it
	// System.out.println("Server message: " + info.message);
	// templateFile = info.message;
	// } else {
	// System.out.println("Uploader status: " + uploader.getStatus());
	// }
	// }
	// });
	// return uploader;
	// }

}
