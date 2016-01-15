package cz.nkp.urnnbn.client.charts.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;

import cz.nkp.urnnbn.client.charts.StatisticsService;
import cz.nkp.urnnbn.client.charts.StatisticsServiceAsync;

public class TopLevelStatisticsWidget extends Composite {

	final StatisticsServiceAsync service = GWT.create(StatisticsService.class);

	protected List<Integer> initMonths() {
		List<Integer> result = new ArrayList<>();
		for (int i = 1; i <= 12; i++) {
			result.add(i);
		}
		return result;
	}

	protected Map<Integer, String> getMonthLabels() {
		// todo: i18n
		Map<Integer, String> result = new HashMap<Integer, String>();
		result.put(1, "leden");
		result.put(2, "únor");
		result.put(3, "březen");
		result.put(4, "duben");
		result.put(5, "květen");
		result.put(6, "červen");
		result.put(7, "červenec");
		result.put(8, "srpen");
		result.put(9, "září");
		result.put(10, "říjen");
		result.put(11, "listopad");
		result.put(12, "prosinec");
		// result.put(1, "january");
		// result.put(2, "fabruary");
		// result.put(3, "march");
		// result.put(4, "april");
		// result.put(5, "may");
		// result.put(6, "june");
		// result.put(7, "july");
		// result.put(8, "august");
		// result.put(9, "september");
		// result.put(10, "october");
		// result.put(11, "november");
		// result.put(12, "december");
		return result;
	}

}
