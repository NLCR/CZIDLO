package cz.nkp.urnnbn.client.insertRecord;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
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
import cz.nkp.urnnbn.shared.UrnNbnRegistrationMode;
import cz.nkp.urnnbn.shared.dto.RegistrarDTO;

public class SelectEntityParametersPanel extends VerticalPanel {

    private static final Logger logger = Logger.getLogger(SelectEntityParametersPanel.class.getName());
    private final ConstantsImpl constants = GWT.create(ConstantsImpl.class);
    private final DataInputPanel superPanel;
    private final ListBox entityTypeListBox = entityTypeListBox();
    private ListBox registrars;
    private ListBox registrationModes;
    private final Timer timer = waitForRegistrarsTimer();

    public SelectEntityParametersPanel(DataInputPanel superPanel) {
        this.superPanel = superPanel;
        // TODO: mozna implementovat jinak - zeptat se a pak se pravidelne ptat po intervalu,
        // jestli registrarsManagedByUser nacetlo
        // v super panelu
        timer.scheduleRepeating(300);
    }

    private Timer waitForRegistrarsTimer() {
        return new Timer() {

            @Override
            public void run() {
                if (superPanel.getRegistrarsManagedByUser() != null) {
                    this.cancel();
                    registrars = registrarsListBox();
                    registrationModes = registrationModesListBox();
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
        result.addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                registrationModes = registrationModesListBox();
                loadForm();
            }
        });
        return result;
    }

    private ListBox registrationModesListBox() {
        RegistrarDTO registrar = selectedRegistrar();
        ListBox result = new ListBox();
        if (registrar.isRegModeByRegistrarAllowed()) {
            result.addItem(constants.modeByRegistrar());
        }
        if (registrar.isRegModeByResolverAllowed()) {
            result.addItem(constants.modeByResolver());
        }
        if (registrar.isRegModeByReservationAllowed()) {
            result.addItem(constants.modeByReservation());
        }
        return result;
    }

    private void loadForm() {
        clear();
        add(selectEntityTypePanel());
        add(selectRegistrarPanel());
        add(selectRegistrationModePanel());
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
        result.add(registrars);
        return result;
    }

    private Panel selectRegistrationModePanel() {
        HorizontalPanel result = new HorizontalPanel();
        result.add(new Label(constants.urnNbnRegistrationMode() + ": "));
        result.add(registrationModes);
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
        UrnNbnRegistrationMode registrationMode = selectedRegistrationMode();
        RegistrarDTO registrar = selectedRegistrar();
        switch (entityTypeListBox.getSelectedIndex()) {
        case 0:
            return new RecordDataPanel(superPanel, registrar, registrationMode, new MonographForm(), constants.monograph());
        case 1:
            return new RecordDataPanel(superPanel, registrar, registrationMode, new MonographVolumeForm(), constants.monographVolume());
        case 2:
            return new RecordDataPanel(superPanel, registrar, registrationMode, new PeriodicalForm(), constants.periodical());
        case 3:
            return new RecordDataPanel(superPanel, registrar, registrationMode, new PeriodicalVolumeForm(), constants.periodicalVolume());
        case 4:
            return new RecordDataPanel(superPanel, registrar, registrationMode, new PeriodicalIssueForm(), constants.periodicalIssue());
        case 5:
            return new RecordDataPanel(superPanel, registrar, registrationMode, new AnalyticalForm(), new SourceDocumentForm(),
                    constants.analytical());
        case 6:
            return new RecordDataPanel(superPanel, registrar, registrationMode, new ThesisForm(), constants.thesis());
        case 7:
            return new RecordDataPanel(superPanel, registrar, registrationMode, new OtherEntityForm(), constants.otherEntity());
        default:
            return null;
        }
    }

    private RegistrarDTO selectedRegistrar() {
        return superPanel.getRegistrarsManagedByUser().get(registrars.getSelectedIndex());
    }

    private UrnNbnRegistrationMode selectedRegistrationMode() {
        int index = registrationModes.getSelectedIndex();
        String text = registrationModes.getItemText(index);
        if (constants.modeByResolver().equals(text)) {
            return UrnNbnRegistrationMode.BY_RESOLVER;
        } else if (constants.modeByRegistrar().equals(text)) {
            return UrnNbnRegistrationMode.BY_REGISTRAR;
        } else if (constants.modeByReservation().equals(text)) {
            return UrnNbnRegistrationMode.BY_RESERVATION;
        } else {
            logger.severe("Unknown urn:nbn registration mode \"" + text + "\"");
            return null;
        }
    }
}
