package cz.nkp.urnnbn.client;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;

public class RegistrarsAssignmentsWidget extends AbstractStatisticsWidget {

	private static final Logger logger = Logger.getLogger(RegistrarsAssignmentsWidget.class.getSimpleName());
	private final StatisticsServiceAsync service = GWT.create(StatisticsService.class);

	private List<Integer> years = Collections.emptyList();
	private List<Integer> months = initMonths();
	private Map<Integer, Integer> currentData;
	private boolean accumulated = false;
	private Integer currentYear = null;

}
