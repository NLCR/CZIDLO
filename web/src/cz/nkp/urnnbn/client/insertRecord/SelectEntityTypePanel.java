package cz.nkp.urnnbn.client.insertRecord;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
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

public class SelectEntityTypePanel extends VerticalPanel {

	private final ConstantsImpl constants = GWT.create(ConstantsImpl.class);
	private final DataInputPanel superPanel;
	private final ListBox listBox = entityTypeListBox();
	private CheckBox urnWillBeInserted = new CheckBox("urn:nbn bude vloženo ručně");

	private ListBox entityTypeListBox() {
		final ListBox result = new ListBox();
		result.addItem(constants.selectEntityType());
		result.addItem(constants.monograph());
		result.addItem(constants.monographVolume());
		result.addItem(constants.periodical());
		result.addItem(constants.periodicalVolume());
		result.addItem(constants.periodicalIssue());
		result.addItem(constants.analytical());
		result.addItem(constants.thesis());
		result.addItem(constants.otherEntity());
		result.setSelectedIndex(0);
		result.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				superPanel.reload(buildRecordPanel());
			}

			private RecordDataPanel buildRecordPanel() {

				Boolean withUrnTextbox = urnWillBeInserted.getValue();
				switch (listBox.getSelectedIndex()) {
				case 1:
					return new RecordDataPanel(superPanel, withUrnTextbox, new MonographForm(), constants.monograph());
				case 2:
					return new RecordDataPanel(superPanel, withUrnTextbox, new MonographVolumeForm(), constants.monographVolume());
				case 3:
					return new RecordDataPanel(superPanel, withUrnTextbox, new PeriodicalForm(), constants.periodical());
				case 4:
					return new RecordDataPanel(superPanel, withUrnTextbox, new PeriodicalVolumeForm(), constants.periodicalVolume());
				case 5:
					return new RecordDataPanel(superPanel, withUrnTextbox, new PeriodicalIssueForm(), constants.periodicalIssue());
				case 6:
					return new RecordDataPanel(superPanel, withUrnTextbox, new AnalyticalForm(), new SourceDocumentForm(), constants
							.analytical());
				case 7:
					return new RecordDataPanel(superPanel, withUrnTextbox, new ThesisForm(), constants.thesis());
				case 8:
					return new RecordDataPanel(superPanel, withUrnTextbox, new OtherEntityForm(), constants.otherEntity());
				default:
					return null;
				}
			}
		});
		return result;
	}

	public SelectEntityTypePanel(final DataInputPanel superPanel) {
		this.superPanel = superPanel;
		add(urnWillBeInserted);
		add(listBox);
	}
}
