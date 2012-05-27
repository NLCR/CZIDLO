package cz.nkp.urnnbn.client.insertRecord;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

import cz.nkp.urnnbn.client.forms.intEntities.AnalyticalForm;
import cz.nkp.urnnbn.client.forms.intEntities.MonographForm;
import cz.nkp.urnnbn.client.forms.intEntities.MonographVolumeForm;
import cz.nkp.urnnbn.client.forms.intEntities.OtherEntityForm;
import cz.nkp.urnnbn.client.forms.intEntities.PeriodicalForm;
import cz.nkp.urnnbn.client.forms.intEntities.PeriodicalIssueForm;
import cz.nkp.urnnbn.client.forms.intEntities.PeriodicalVolumeForm;
import cz.nkp.urnnbn.client.forms.intEntities.SourceDocumentForm;
import cz.nkp.urnnbn.client.forms.intEntities.ThesisForm;
import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
import cz.nkp.urnnbn.shared.dto.RegistrarDTO;

public class SelectEntityParametersPanel extends VerticalPanel {

	private final ConstantsImpl constants = GWT.create(ConstantsImpl.class);
	private final DataInputPanel superPanel;
	private final ListBox entityTypeListBox = entityTypeListBox();
	private ListBox registrarsListBox;
	private final CheckBox urnWillBeInserted = new CheckBox();
	private final Timer timer = waitForRegistrarsTimer();

	public SelectEntityParametersPanel(DataInputPanel superPanel) {
		this.superPanel = superPanel;
		timer.schedule(300);
	}

	private Timer waitForRegistrarsTimer() {
		return new Timer() {

			@Override
			public void run() {
				if (superPanel.getRegistrarsManagedByUser() != null) {
					this.cancel();
					registrarsListBox = registrarsListBox();
					loadForm();
				}
			}
		};
	}

	private ListBox registrarsListBox() {
		ListBox result = new ListBox();
		ArrayList<RegistrarDTO> registrars = superPanel.getRegistrarsManagedByUser();
		for (RegistrarDTO registrar : registrars) {
			result.addItem(registrar.getCode());
		}
		return result;
	}

	private void loadForm() {
		clear();
		add(selectEntityTypePanel());
		add(selectRegistrarPanel());
		add(urnInsertedManualyPanel());
		add(continueButton());
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
		return result;
	}

	private Panel selectRegistrarPanel() {
		HorizontalPanel result = new HorizontalPanel();
		result.add(new Label(constants.registrar() + ": "));
		result.add(registrarsListBox);
		return result;
	}

	private Panel urnInsertedManualyPanel() {
		HorizontalPanel result = new HorizontalPanel();
		result.add(new Label(constants.urnNbnWillBeInserted() + ": "));
		result.add(urnWillBeInserted);
		return result;
	}

	private Panel selectEntityTypePanel() {
		HorizontalPanel result = new HorizontalPanel();
		result.add(new Label(constants.intEntityType() + ": "));
		result.add(entityTypeListBox);
		return result;
	}

	private Button continueButton() {
		return new Button(constants.moveOn(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				superPanel.reload(buildRecordPanel());
			}
		});
	}

	private RecordDataPanel buildRecordPanel() {
		Boolean withUrnTextbox = urnWillBeInserted.getValue();
		RegistrarDTO registrar = selectedRegistrar();
		switch (entityTypeListBox.getSelectedIndex()) {
		case 0:
			return new RecordDataPanel(superPanel, registrar, withUrnTextbox, new MonographForm(), constants.monograph());
		case 1:
			return new RecordDataPanel(superPanel, registrar, withUrnTextbox, new MonographVolumeForm(), constants.monographVolume());
		case 2:
			return new RecordDataPanel(superPanel, registrar, withUrnTextbox, new PeriodicalForm(), constants.periodical());
		case 3:
			return new RecordDataPanel(superPanel, registrar, withUrnTextbox, new PeriodicalVolumeForm(), constants.periodicalVolume());
		case 4:
			return new RecordDataPanel(superPanel, registrar, withUrnTextbox, new PeriodicalIssueForm(), constants.periodicalIssue());
		case 5:
			return new RecordDataPanel(superPanel, registrar, withUrnTextbox, new AnalyticalForm(), new SourceDocumentForm(),
					constants.analytical());
		case 6:
			return new RecordDataPanel(superPanel, registrar, withUrnTextbox, new ThesisForm(), constants.thesis());
		case 7:
			return new RecordDataPanel(superPanel, registrar, withUrnTextbox, new OtherEntityForm(), constants.otherEntity());
		default:
			return null;
		}
	}

	private RegistrarDTO selectedRegistrar() {
		return superPanel.getRegistrarsManagedByUser().get(registrarsListBox.getSelectedIndex());
	}
}
