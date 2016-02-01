package cz.nkp.urnnbn.client.search;

import com.google.gwt.core.client.GWT;

import cz.nkp.urnnbn.client.resources.Resources;
import cz.nkp.urnnbn.client.resources.SearchPanelCss;

public class SearchPanelResources {

    static SearchPanelCss css() {
        Resources resources = GWT.create(Resources.class);
        SearchPanelCss result = resources.SearchPanelCss();
        result.ensureInjected();
        return result;
    }

}
