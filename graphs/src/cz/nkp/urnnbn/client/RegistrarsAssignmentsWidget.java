package cz.nkp.urnnbn.client;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class RegistrarsAssignmentsWidget extends AbstractStatisticsWidget {

	private static final Logger logger = Logger.getLogger(RegistrarsAssignmentsWidget.class.getSimpleName());
	private final StatisticsServiceAsync service = GWT.create(StatisticsService.class);

	private List<Integer> years = Collections.emptyList();
	private List<Integer> months = initMonths();
	private Map<Integer, Integer> currentData;
	private boolean accumulated = false;
	private Integer currentYear = null;

	public RegistrarsAssignmentsWidget() {
		// container
		VerticalPanel container = new VerticalPanel();
		container.setSpacing(5);
		container.setWidth("100%");
		RootLayoutPanel.get().add(container);

		// header
		VerticalPanel header = new VerticalPanel();
		header.setSpacing(10);
		//header.setWidth("100%");
		header.setWidth("1500px");
		container.add(header);
		
		Label label = new Label("Total");
		header.add(label);
		
		container.add(new Top3PieChart().getWidget());

		initWidget(container);
		setStyleName("RegistrarssGraph");
	}

}
