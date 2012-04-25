package cz.nkp.urnnbn.client.insertRecord;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import cz.nkp.urnnbn.client.forms.Form;
import cz.nkp.urnnbn.client.forms.intEntities.AnalyticalForm;
import cz.nkp.urnnbn.client.forms.intEntities.MonographForm;
import cz.nkp.urnnbn.client.forms.intEntities.MonographVolumeForm;
import cz.nkp.urnnbn.client.forms.intEntities.OtherEntityForm;
import cz.nkp.urnnbn.client.forms.intEntities.PeriodicalForm;
import cz.nkp.urnnbn.client.forms.intEntities.PeriodicalIssueForm;
import cz.nkp.urnnbn.client.forms.intEntities.PeriodicalVolumeForm;
import cz.nkp.urnnbn.client.forms.intEntities.ThesisForm;
import cz.nkp.urnnbn.client.i18n.ConstantsImpl;

public class IntelectualEntityPanel extends VerticalPanel {
	public IntelectualEntityPanel() {}

	ConstantsImpl constants = GWT.create(ConstantsImpl.class);
	Form form;

	public void onLoad() {
		// setHeight("512px");
		// addEntityTypeListBox(0);
		// refreshEntityForm(0);
		// addTestButton();
		refresh(0);
	}

	private void addTestButton() {
		Button testBtn = new Button();
		testBtn.setText("test");
		add(testBtn);

		// setWidgetPosition(testBtn, 420, 440);
		// setWidgetPosition(testBtn, 420, buttonTop);

		// setWidgetPosition(testBtn, 450, buttonTop);
		testBtn.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				form.frost();

			}
		});
		testBtn.addKeyDownHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent event) {
				form.frost();
			}
		});
	}

	private void addEntityTypeListBox(int selected) {
		ListBox ieType = entityTypeListBox();
		ieType.setSelectedIndex(selected);
		add(ieType);
		// setWidgetPosition(ieType, 37, 10);
	}

	private ListBox entityTypeListBox() {
		final ListBox result = new ListBox();
		result.addItem(constants.monograph());
		result.addItem(constants.monographVolume());
		result.addItem(constants.periodical());
		result.addItem(constants.periodicalVolume());
		result.addItem(constants.periodicalIssue());
		result.addItem(constants.analytical());
		result.addItem(constants.thesis());
		result.addItem(constants.otherEntity());
		result.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				int index = result.getSelectedIndex();
				refresh(index);
			}
		});
		return result;
	}

	private void refresh(int index) {
		clear();
		addEntityTypeListBox(index);
		refreshEntityForm(index);
		addTestButton();
	}

	private void refreshEntityForm(int index) {
		// clear();
		// addEntityTypeListBox(index);
		switch (index) {
		case 0:
			replaceEntityForm(new MonographForm(null, null));
			break;
		case 1:
			replaceEntityForm(new MonographVolumeForm());
			break;
		case 2:
			replaceEntityForm(new PeriodicalForm());
			break;
		case 3:
			replaceEntityForm(new PeriodicalVolumeForm());
			break;
		case 4:
			replaceEntityForm(new PeriodicalIssueForm());
			break;
		case 5:
			replaceEntityForm(new AnalyticalForm());
			break;
		case 6:
			replaceEntityForm(new ThesisForm());
			break;
		case 7:
			replaceEntityForm(new OtherEntityForm());
			break;
		}
	}

	void replaceEntityForm(Form entityForm) {
		if (entityForm != null) {
			remove(entityForm);
			this.form = entityForm;
			add(form);
		}
	}
}
