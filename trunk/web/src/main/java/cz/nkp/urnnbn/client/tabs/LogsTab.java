package cz.nkp.urnnbn.client.tabs;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

import cz.nkp.urnnbn.client.Utils;
import cz.nkp.urnnbn.client.resources.LogsPanelCss;
import cz.nkp.urnnbn.client.resources.Resources;
import cz.nkp.urnnbn.client.services.LogsService;
import cz.nkp.urnnbn.client.services.LogsServiceAsync;
import cz.nkp.urnnbn.shared.exceptions.SessionExpirationException;

public class LogsTab extends SingleTabContentPanel {

	private static final Logger logger = Logger.getLogger(LogsTab.class.getName());
	private static final int TIMER_INTERVAL = 1000;
	private static final String ADMIN_LOG_URL = "/processDataServer/adminLog";
	private final LogsServiceAsync logsService = GWT.create(LogsService.class);
	private final LogsPanelCss css = initCss();
	private Timer refreshTabTimer = null;
	private List<String> adminLogsList = new ArrayList<String>();
	private long adminLogsLastUpdated = 0;

	private LogsPanelCss initCss() {
		Resources resources = GWT.create(Resources.class);
		LogsPanelCss result = resources.LogsPanelCss();
		result.ensureInjected();
		return result;
	}

	private void reload() {
		clear();
		add(contentPanel());
	}

	private Panel contentPanel() {
		VerticalPanel result = new VerticalPanel();
		result.add(downloadLogFileButton());
		result.add(adminLogsPanel());
		return result;
	}

	private Button downloadLogFileButton() {
		return new Button(constants.logsTabDownloadLogFile(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Window.open(ADMIN_LOG_URL, "_self", "enabled");
			}
		});
	}

	private Panel adminLogsPanel() {
		VerticalPanel panel = new VerticalPanel();
		Label label = new Label(constants.logsTabProcessListHeading());
		label.addStyleName(css.logsListHeading());
		panel.add(label);
		for (String log : adminLogsList) {
			panel.add(new Label(log));
		}
		return panel;
	}

	public LogsTab(TabsPanel superPanel) {
		super(superPanel);
	}

	@Override
	public void onLoad() {
		reload();
		startTimerIfNotRunningAlready();
	}

	@Override
	public void onSelection() {
		startTimerIfNotRunningAlready();
	}

	@Override
	public void onDeselectionSelection() {
		stopTimerIfRunning();
	}

	private void startTimerIfNotRunningAlready() {
		if (refreshTabTimer == null) {
			refreshTabTimer = initTimer();
			refreshTabTimer.scheduleRepeating(TIMER_INTERVAL);
		}
	}

	private void stopTimerIfRunning() {
		if (refreshTabTimer != null) {
			refreshTabTimer.cancel();
			refreshTabTimer = null;
		}
	}

	private Timer initTimer() {
		return new Timer() {

			@Override
			public void run() {
				loadAdminLogsIfChanged();
			}
		};
	}

	private void loadAdminLogsIfChanged() {
		logsService.getAdminLogLastUpdatedTime(new AsyncCallback<Long>() {

			@Override
			public void onSuccess(Long result) {
				// logger.info("Admin logs last updated: " + result);
				if (result > adminLogsLastUpdated) {
					adminLogsLastUpdated = result;
					loadAdminLogs();
				}

			}

			private void loadAdminLogs() {
				logsService.getAdminLogs(new AsyncCallback<List<String>>() {

					@Override
					public void onSuccess(List<String> result) {
						if (adminLogsList != null) {
							// logger.info("Admin logs loaded");
							adminLogsList = result;
							reload();
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						if (caught instanceof SessionExpirationException) {
							Utils.sessionExpirationRedirect();
						} else {
							logger.severe("Error loading admin logs: " + caught.getMessage());
						}
					}
				});
			}

			@Override
			public void onFailure(Throwable caught) {
				if (caught instanceof SessionExpirationException) {
					Utils.sessionExpirationRedirect();
				} else {
					logger.severe("Error loading admin logs last update time: " + caught.getMessage());
				}
			}
		});
	}
}
