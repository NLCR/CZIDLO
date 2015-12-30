package cz.nkp.urnnbn.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;

public class AbstractStatisticsWidget extends Composite {

	final StatisticsServiceAsync service = GWT.create(StatisticsService.class);

	protected List<Integer> initMonths() {
		List<Integer> result = new ArrayList<>();
		for (int i = 1; i <= 12; i++) {
			result.add(i);
		}
		return result;
	}

}
