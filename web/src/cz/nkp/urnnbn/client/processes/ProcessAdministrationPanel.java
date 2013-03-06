package cz.nkp.urnnbn.client.processes;

import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import cz.nkp.urnnbn.client.resources.ProcessAdministrationCss;
import cz.nkp.urnnbn.client.resources.Resources;
import cz.nkp.urnnbn.client.services.ProcessService;
import cz.nkp.urnnbn.client.services.ProcessServiceAsync;
import cz.nkp.urnnbn.client.tabs.SingleTabContentPanel;
import cz.nkp.urnnbn.client.tabs.TabsPanel;
import cz.nkp.urnnbn.shared.dto.process.ProcessDTO;
import cz.nkp.urnnbn.shared.dto.process.ProcessDTOState;

public class ProcessAdministrationPanel extends SingleTabContentPanel {

	private final ProcessAdministrationCss css = initCss();
	private final Timer processesRefreshTimer = initProcessRefreshTimer();
	private static final int TIMER_INTERVAL = 1000;
	// private final SearchServiceAsync searchService =
	// GWT.create(SearchService.class);
	private final ProcessServiceAsync processService = GWT.create(ProcessService.class);
	private List<ProcessDTO> processes;

	// tmp
	private boolean panelActive = true;
	private Date timeSinceActivation;

	private ProcessAdministrationCss initCss() {
		Resources resources = GWT.create(Resources.class);
		ProcessAdministrationCss result = resources.ProcessAdministrationCss();
		result.ensureInjected();
		return result;
	}

	private Timer initProcessRefreshTimer() {
		return new Timer() {

			@Override
			public void run() {
				// TODO: stahnout aktualni seznam procesu (asi ne cely) a
				// prekreslit
				loadProcesses();
				// reload();
			}
		};

	}

	public ProcessAdministrationPanel(TabsPanel superPanel) {
		super(superPanel);
	}

	@Override
	public void onLoad() {
		loadProcesses();
		// reload();
		processesRefreshTimer.scheduleRepeating(TIMER_INTERVAL);
	}

	private void loadProcesses() {
		// TODO
		processService.getAllProcesses(new AsyncCallback<List<ProcessDTO>>() {

			@Override
			public void onSuccess(List<ProcessDTO> result) {
				processes = result;
				reload();
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());

			}
		});
	}

	private void reload() {
		// TODO: měl by se volat v nějakém timeru
		clear();
		add(contentPanel());
	}

	private Panel contentPanel() {
		VerticalPanel result = new VerticalPanel();
		result.add(processListHeading());
		result.add(processListPanel());
		// TODO:panel se seznamem procesů
		result.add(planProcessHeading());
		// TODO:panel pro spuštění procesu
		// TODO: odstranit test label
		// result.add(testWidget());
		result.add(testButton());
		return result;
	}

	private Widget processListPanel() {
		Panel panel = new VerticalPanel();
		// TODO: nasledujici panel jen adminovi, userovi vzdy filtrovat
		// panel.add(limitListPanel());
		panel.add(processListScrollPanelHeader());
		panel.add(processListScrollPanel());
		return panel;
	}

	private Widget processListScrollPanelHeader() {
		HorizontalPanel panel = new HorizontalPanel();
		panel.setWidth("1000px");
		// id
		Widget idLabel = headerFormated("id");
		panel.add(idLabel);
		// panel.setCellWidth(idLabel, "40px");
		panel.setCellWidth(idLabel, "2%");

		// typ procesu
		Widget typeLabel = headerFormated("typ procesu");
		panel.add(typeLabel);
		panel.setCellWidth(typeLabel, "13%");

		// //parametry
		// Label paramLabel = new Label("parametry");
		// panel.add(paramLabel);
		// panel.setCellWidth(paramLabel, "5%");

		// stav procesu
		Widget stateLabel = headerFormated("stav");
		panel.add(stateLabel);
		panel.setCellWidth(stateLabel, "8%");

		// cas naplanovani
		Widget scheduledLabel = headerFormated("naplánován");
		panel.add(scheduledLabel);
		panel.setCellWidth(scheduledLabel, "10%");

		// cas spusteni
		Widget startedLabel = headerFormated("spuštěn");
		panel.add(startedLabel);
		panel.setCellWidth(startedLabel, "10%");

		// cas ukonceni
		Widget finishedLabel = headerFormated("ukončen");
		panel.add(finishedLabel);
		panel.setCellWidth(finishedLabel, "10%");

		// tlacitko na zastaveni procesu
		Widget stopProcess = new Label("");
		panel.add(stopProcess);
		panel.setCellWidth(stopProcess, "8%");

		// stazeni logu
		Widget downloadLog = new Label("");
		panel.add(downloadLog);
		panel.setCellWidth(downloadLog, "8%");

		// stazeni vystupu
		Widget downloadResults = new Label("");
		panel.add(downloadResults);
		panel.setCellWidth(downloadResults, "10%");

		return panel;
	}

	private Widget headerFormated(String string) {
		return new HTML("<div style=\"color:grey\">" + string + "</style>");
	}

	private Panel processListScrollPanel() {
		ScrollPanel root = new ScrollPanel();
		root.setWidth("1300px");
		root.setHeight("200px");
		Panel content = new VerticalPanel();
		for (ProcessDTO process : processes) {
			content.add(processWidget(process));
		}
		root.add(content);
		return root;
	}

	private Widget processWidget(ProcessDTO process) {
		HorizontalPanel panel = new HorizontalPanel();
		panel.setWidth("1000px");
		// panel.setWidth("100%");

		ProcessFormater formater = new ProcessFormater(process);

		// id
		Label idLabel = new Label(process.getId().toString());
		panel.add(idLabel);
		// panel.setCellWidth(idLabel, "40px");
		panel.setCellWidth(idLabel, "2%");

		// typ procesu
		Widget typeLabel = formater.getProcessType();
		panel.add(typeLabel);
		panel.setCellWidth(typeLabel, "13%");

		// //parametry
		// Label paramLabel = new Label(formater.getParams());
		// panel.add(paramLabel);
		// panel.setCellWidth(paramLabel, "5%");

		// stav procesu
		Widget stateLabel = formater.getProcessState();
		panel.add(stateLabel);
		panel.setCellWidth(stateLabel, "8%");

		// cas naplanovani
		Label scheduledLabel = new Label(formater.getScheduled());
		panel.add(scheduledLabel);
		panel.setCellWidth(scheduledLabel, "10%");

		// cas spusteni
		Label startedLabel = new Label(process.getStarted());
		panel.add(startedLabel);
		panel.setCellWidth(startedLabel, "10%");

		// cas ukonceni
		Label finishedLabel = new Label(process.getStarted());
		panel.add(finishedLabel);
		panel.setCellWidth(finishedLabel, "10%");

		// tlacitko na zastaveni procesu
		Widget stopProcess = stopProcessWidget(process);
		panel.add(stopProcess);
		panel.setCellWidth(stopProcess, "8%");

		// stazeni logu
		Widget downloadLog = downloadLogWidget(process);
		// new Label("stáhnout log");
		panel.add(downloadLog);
		panel.setCellWidth(downloadLog, "8%");

		// stazeni vystupu
		Widget downloadResults = downloadOutputWidget(process);
		// new Label("stáhnout výstup");
		panel.add(downloadResults);
		panel.setCellWidth(downloadResults, "10%");

		return panel;
	}

	private Widget downloadOutputWidget(final ProcessDTO process) {
		if (process.getState() == ProcessDTOState.FINISHED) {
			return new Button("stáhnout výstup", new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					String url = "http://localhost/processFileServer/FileServlet?processId=" + process.getId() + "&fileId=export.csv";
					Window.open(url, "_self", "enabled");

				}
			});
		} else {
			return new Label("");
		}
	}

	private Widget downloadLogWidget(final ProcessDTO process) {
		if (process.getState() == ProcessDTOState.FINISHED || process.getState() == ProcessDTOState.KILLED
				|| process.getState() == ProcessDTOState.FAILED) {
			return new Button("stáhnout log", new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					String url = "http://localhost/processFileServer/FileServlet?processId=" + process.getId() + "&fileId=process.log";
					Window.open(url, "_self", "enabled");

				}
			});
		} else {
			return new Label("");
		}
	}

	private Widget stopProcessWidget(final ProcessDTO process) {
		if (process.getState() == ProcessDTOState.RUNNING || process.getState() == ProcessDTOState.SCHEDULED) {
			return new Button("zrušit");
		} else {
			return new Button("smazat", new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					processService.deleteFinishedProcess(process.getId(),new AsyncCallback<Void>() {
						
						@Override
						public void onSuccess(Void result) {
							//reload();
							//nic, procesy se stejne nacitaji kazdou vterinu a volat to tu znovu snad asi nema smysl
							
						}
						
						@Override
						public void onFailure(Throwable caught) {
							// TODO Auto-generated method stub
						}
					});
					
				}
			});
		}
	}

	private Panel limitListPanel() {
		HorizontalPanel panel = new HorizontalPanel();
		panel.add(new Label("jen moje procesy"));
		return panel;
	}

	private Widget testWidget() {
		Date now = new Date();
		long diff = now.getTime() - timeSinceActivation.getTime();
		// DateTimeFormat dtf =
		// DateTimeFormat.getFormat("dd. MM. yyyy HH:mm:ss");
		// return new Label(dtf.format(now));
		return new Label("Tab je aktivní " + Long.valueOf(diff).toString() + " milisekund");
	}

	private Button testButton() {
		return new Button("zastavit", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (panelActive) {
					panelActive = false;
					processesRefreshTimer.cancel();
				} else {
					panelActive = true;
					processesRefreshTimer.scheduleRepeating(1000);
				}
			}
		});
	}

	private Label processListHeading() {
		Label label = new Label(constants.processList());
		label.addStyleName(css.processListHeading());
		return label;
	}

	private Label planProcessHeading() {
		Label label = new Label(constants.planNewProcess());
		label.addStyleName(css.planProcessHeading());
		return label;
	}

	@Override
	public void onSelection() {
		// Window.alert("aktivni");
		timeSinceActivation = new Date();
		panelActive = true;
		processesRefreshTimer.scheduleRepeating(1000);
	}

	@Override
	public void onDeselectionSelection() {
		panelActive = false;
		processesRefreshTimer.cancel();
	}

}
