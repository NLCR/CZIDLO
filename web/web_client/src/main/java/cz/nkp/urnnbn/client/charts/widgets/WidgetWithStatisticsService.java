package cz.nkp.urnnbn.client.charts.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;

import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
import cz.nkp.urnnbn.client.i18n.MessagesImpl;
import cz.nkp.urnnbn.client.services.GwtStatisticsService;
import cz.nkp.urnnbn.client.services.GwtStatisticsServiceAsync;

public class WidgetWithStatisticsService extends Composite {

    final GwtStatisticsServiceAsync statisticService = GWT.create(GwtStatisticsService.class);
    final protected ConstantsImpl constants = GWT.create(ConstantsImpl.class);
    final protected MessagesImpl messages = GWT.create(MessagesImpl.class);

    protected List<Integer> initMonths() {
        List<Integer> result = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            result.add(i);
        }
        return result;
    }

    protected Map<Integer, String> getMonthLabels() {
        Map<Integer, String> result = new HashMap<Integer, String>();
        result.put(1, constants.month1());
        result.put(2, constants.month2());
        result.put(3, constants.month3());
        result.put(4, constants.month4());
        result.put(5, constants.month5());
        result.put(6, constants.month6());
        result.put(7, constants.month7());
        result.put(8, constants.month8());
        result.put(9, constants.month9());
        result.put(10, constants.month10());
        result.put(11, constants.month11());
        result.put(12, constants.month12());
        return result;
    }

}
