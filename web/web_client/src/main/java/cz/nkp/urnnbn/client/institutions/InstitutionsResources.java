package cz.nkp.urnnbn.client.institutions;

import com.google.gwt.core.client.GWT;

import cz.nkp.urnnbn.client.resources.InstitutionsPanelCss;
import cz.nkp.urnnbn.client.resources.Resources;

public class InstitutionsResources {

    static InstitutionsPanelCss loadCss() {
        Resources resources = GWT.create(Resources.class);
        InstitutionsPanelCss result = resources.InstitutionsPanelCss();
        result.ensureInjected();
        return result;
    }
}
