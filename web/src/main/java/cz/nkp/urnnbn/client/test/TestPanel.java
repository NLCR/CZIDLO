package cz.nkp.urnnbn.client.test;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import cz.nkp.urnnbn.client.forms.intEntities.SourceDocumentForm;
import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
import cz.nkp.urnnbn.client.search.EditDigitalDocumentDialogBox;
import cz.nkp.urnnbn.client.search.EditIntelectualEntityDialogBox;
import cz.nkp.urnnbn.shared.dto.DigitalDocumentDTO;
import cz.nkp.urnnbn.shared.dto.UserDTO;
import cz.nkp.urnnbn.shared.dto.ie.AnalyticalDTO;
import cz.nkp.urnnbn.shared.dto.ie.MonographDTO;
import cz.nkp.urnnbn.shared.dto.ie.SourceDocumentDTO;

public class TestPanel extends ScrollPanel {
	ConstantsImpl constants = GWT.create(ConstantsImpl.class);

	public void onLoad() {
		super.onLoad();
		add(internalPanel());
	}

	private VerticalPanel internalPanel() {
		VerticalPanel result = new VerticalPanel();
		result.add(new Button("analytical-old", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				TestDialogBox dialog = new TestDialogBox();
				dialog.center();
				dialog.setPopupPosition(dialog.getPopupLeft(), 105);
				dialog.show();
			}
		}));
		result.add(new Button("digitalDocument", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				DigitalDocumentDTO doc = new DigitalDocumentDTO();
				doc.setContractNumber("123");
				doc.setFinanced("norske fondy");
				EditDigitalDocumentDialogBox dialog = new EditDigitalDocumentDialogBox(doc, null);
				dialog.center();
				dialog.setPopupPosition(dialog.getPopupLeft(), 105);
				dialog.show();
			}
		}));
		result.add(new Button("monograph", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				new EditIntelectualEntityDialogBox(null, monographDtd(), null, null).show();
			}

			private MonographDTO monographDtd() {
				MonographDTO result = new MonographDTO();
				result.setId(Long.valueOf(1));
				result.setTitle("test");
				return result;
			}
		}));

		result.add(new Button("analytical", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				EditIntelectualEntityDialogBox dialogBox = new EditIntelectualEntityDialogBox(null, analyticalDtd(), null, srcDocDtd());
				dialogBox.show();

			}

			private AnalyticalDTO analyticalDtd() {
				AnalyticalDTO result = new AnalyticalDTO();
				result.setId(Long.valueOf(1));
				result.setTitle("test");
				return result;
			}

			private SourceDocumentForm srcDocDtd() {
				return new SourceDocumentForm();
			}
		}));
		result.add(new Button("odstranit", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Window.confirm("blabla");
			}
		}));

		return result;
	}

}
