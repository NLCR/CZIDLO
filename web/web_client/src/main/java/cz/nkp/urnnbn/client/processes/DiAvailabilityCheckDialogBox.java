package cz.nkp.urnnbn.client.processes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import cz.nkp.urnnbn.client.forms.TextInputValueField;
import cz.nkp.urnnbn.client.services.UserAccountService;
import cz.nkp.urnnbn.client.services.UserAccountServiceAsync;
import cz.nkp.urnnbn.client.validation.DateTimeValidator;
import cz.nkp.urnnbn.shared.dto.RegistrarDTO;
import cz.nkp.urnnbn.shared.dto.UserDTO;
import cz.nkp.urnnbn.shared.dto.process.ProcessDTOType;

public class DiAvailabilityCheckDialogBox extends AbstractScheduleProcessDialogBox {
    private static final String DATE_FORMAT = "d. M. yyyy H:m.s";
    protected DateTimeFormat dateFormat = DateTimeFormat.getFormat(DATE_FORMAT);

    private final UserAccountServiceAsync accountsService = GWT.create(UserAccountService.class);
    private ArrayList<RegistrarDTO> registrars = new ArrayList<RegistrarDTO>();

    // widgets
    private final Label errorLabel = errorLabel(320);
    private TextInputValueField beginDate;
    private TextInputValueField endDate;
    private MultiSelectListBox registrarsListBox;
    private MultiSelectListBox documentTypeListBox;
    private CheckBox urnStatesIncludeActive;
    private CheckBox urnStatesIncludeDeactivated;
    private CheckBox diStatesIncludeActive;
    private CheckBox diStatesIncludeDeactivated;

    public DiAvailabilityCheckDialogBox(UserDTO user) {
        super(user);
        loadRegistrars();
        reload();
        center();
    }

    private void loadRegistrars() {
        accountsService.getAllRegistrars(new AsyncCallback<ArrayList<RegistrarDTO>>() {

            @Override
            public void onSuccess(ArrayList<RegistrarDTO> result) {
                registrars = result;
                reload();
            }

            @Override
            public void onFailure(Throwable caught) {
                errorLabel.setText(messages.serverError(caught.getMessage()));
            }
        });
    }

    private void reload() {
        clear();
        setText(messages.processPlaning(constants.DI_URL_AVAILABILITY_CHECK()));
        setAnimationEnabled(true);
        setWidget(contentPanel());
    }

    private Panel contentPanel() {
        VerticalPanel result = new VerticalPanel();
        // DD
        Label ddHeader = new Label(constants.processDiAvailabilityCheckDd());
        // TODO: style properly
        ddHeader.getElement().getStyle().setFontStyle(FontStyle.ITALIC);
        ddHeader.getElement().getStyle().setFontSize(1.2, Unit.EM);
        result.add(ddHeader);
        result.add(new HTML("&nbsp;"));
        result.add(buildRegistrarSelectionPanel());
        result.add(buildIntEntTypeSelectionPanel());
        result.add(buildUrnStatePanel());

        // divider
        result.add(new HTML("<hr style=\"width:100%;\"/>"));

        // DI
        Label diHeader = new Label(constants.processDiAvailabilityCheckDi());
        // TODO: style properly
        diHeader.getElement().getStyle().setFontStyle(FontStyle.ITALIC);
        diHeader.getElement().getStyle().setFontSize(1.2, Unit.EM);
        result.add(diHeader);
        result.add(new HTML("&nbsp;"));
        result.add(selectDateRangePanel());
        result.add(buildDiStatePanel());
        result.add(buttonsPanel());
        result.add(errorLabel);
        return result;
    }

    private Panel selectDateRangePanel() {
        HorizontalPanel result = new HorizontalPanel();
        @SuppressWarnings("deprecation")
        Date start = new Date(112, 8, 1); // 1.9.2012
        constants.processDiAvailabilityCheckCreatedDeactivated();
        beginDate = new TextInputValueField(new DateTimeValidator(DATE_FORMAT), constants.processDiAvailabilityCheckCreatedDeactivated(),
                dateFormat.format(start), false);
        result.add(beginDate.getLabelWidget());
        result.add(beginDate.getContentWidget());
        endDate = new TextInputValueField(new DateTimeValidator(DATE_FORMAT), "", dateFormat.format(new Date()), false);
        result.add(new HTML(" - "));
        result.add(endDate.getContentWidget());
        return result;
    }

    private Panel buildRegistrarSelectionPanel() {
        HorizontalPanel result = new HorizontalPanel();
        result.add(new Label(constants.registrar() + SEPARATOR));
        initRegistrarsList();
        result.add(registrarsListBox);
        return result;
    }

    private void initRegistrarsList() {
        registrarsListBox = new MultiSelectListBox();
        for (int i = 0; i < registrars.size(); i++) {
            RegistrarDTO registrar = registrars.get(i);
            registrarsListBox.addItem(registrar.getCode());
            registrarsListBox.setItemSelected(i, true);
        }
    }

    private Panel buildIntEntTypeSelectionPanel() {
        HorizontalPanel result = new HorizontalPanel();
        result.add(new Label(constants.documentType() + SEPARATOR));
        documentTypeListBox = new MultiSelectListBox();
        documentTypeListBox.addItem("MONOGRAPH");
        documentTypeListBox.addItem("MONOGRAPH_VOLUME");
        documentTypeListBox.addItem("PERIODICAL");
        documentTypeListBox.addItem("PERIODICAL_VOLUME");
        documentTypeListBox.addItem("PERIODICAL_ISSUE");
        documentTypeListBox.addItem("THESIS");
        documentTypeListBox.addItem("ANALYTICAL");
        documentTypeListBox.addItem("OTHER");
        for (int i = 0; i < documentTypeListBox.getItemCount(); i++) {
            documentTypeListBox.setItemSelected(i, true);
        }
        result.add(documentTypeListBox);
        return result;
    }

    private Widget buildUrnStatePanel() {
        urnStatesIncludeActive = new CheckBox(constants.processDiAvailabilityCheckActive());
        urnStatesIncludeDeactivated = new CheckBox(constants.processDiAvailabilityCheckDeactivated());
        urnStatesIncludeActive.setValue(true);
        urnStatesIncludeDeactivated.setValue(true);
        HorizontalPanel result = new HorizontalPanel();
        result.add(new Label(constants.processDiAvailabilityCheckUrnStates() + ": "));
        result.add(urnStatesIncludeActive);
        result.add(urnStatesIncludeDeactivated);
        return result;
    }

    private Widget buildDiStatePanel() {
        diStatesIncludeActive = new CheckBox(constants.processDiAvailabilityCheckActive());
        diStatesIncludeDeactivated = new CheckBox(constants.processDiAvailabilityCheckDeactivated());
        diStatesIncludeActive.setValue(true);
        diStatesIncludeDeactivated.setValue(true);
        HorizontalPanel result = new HorizontalPanel();
        result.add(new Label(constants.processDiAvailabilityCheckDiStates() + ": "));
        result.add(diStatesIncludeActive);
        result.add(diStatesIncludeDeactivated);
        return result;
    }

    private Panel buttonsPanel() {
        HorizontalPanel result = new HorizontalPanel();
        result.setSpacing(10);
        result.add(scheduleProcessButton());
        result.add(closeButton());
        return result;
    }

    private Button scheduleProcessButton() {
        return new Button(constants.scheduleProcess(), new ClickHandler() {

            public void onClick(ClickEvent event) {
                String registrars = null;
                List<String> selectedRegistrars = registrarsListBox.getSelectedItems();
                if (selectedRegistrars.size() > 0) {
                    StringBuilder regs = new StringBuilder();
                    String sep = "";
                    for (String code : selectedRegistrars) {
                        regs.append(sep);
                        regs.append(code);
                        sep = ",";
                    }
                    registrars = regs.toString();
                }

                String entityTypes = null;
                List<String> selectedEntityTypes = documentTypeListBox.getSelectedItems();
                if (selectedEntityTypes.size() > 0) {
                    StringBuilder types = new StringBuilder();
                    String sep = "";
                    for (String code : selectedEntityTypes) {
                        types.append(sep);
                        types.append(code);
                        sep = ",";
                    }
                    entityTypes = types.toString();
                }

                String begin = (String) beginDate.getInsertedValue();
                String end = (String) endDate.getInsertedValue();

                Boolean urnIncludeActive = urnStatesIncludeActive.getValue();
                Boolean urnIncludeDeactivated = urnStatesIncludeDeactivated.getValue();
                Boolean diIncludeActive = diStatesIncludeActive.getValue();
                Boolean diIncludeDeactivated = diStatesIncludeDeactivated.getValue();

                String[] params = new String[] { registrars, entityTypes, urnIncludeActive.toString(), urnIncludeDeactivated.toString(),
                        diIncludeActive.toString(), diIncludeDeactivated.toString(), begin, end };
                processService.scheduleProcess(ProcessDTOType.DI_URL_AVAILABILITY_CHECK, params, new AsyncCallback<Void>() {

                    public void onSuccess(Void result) {
                        DiAvailabilityCheckDialogBox.this.hide();
                    }

                    public void onFailure(Throwable caught) {
                        errorLabel.setText(caught.getMessage());
                    }
                });

            }
        });
    }

    private Button closeButton() {
        return new Button(constants.close(), new ClickHandler() {

            public void onClick(ClickEvent event) {
                DiAvailabilityCheckDialogBox.this.hide();
            }
        });
    }

}
