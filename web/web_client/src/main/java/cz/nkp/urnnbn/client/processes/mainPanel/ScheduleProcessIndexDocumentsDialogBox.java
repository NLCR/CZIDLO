package cz.nkp.urnnbn.client.processes.mainPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import cz.nkp.urnnbn.client.forms.TextInputValueField;
import cz.nkp.urnnbn.client.services.UserAccountService;
import cz.nkp.urnnbn.client.services.UserAccountServiceAsync;
import cz.nkp.urnnbn.client.validation.DateTimeValidator;
import cz.nkp.urnnbn.shared.dto.RegistrarDTO;
import cz.nkp.urnnbn.shared.dto.UserDTO;
import cz.nkp.urnnbn.shared.dto.process.ProcessDTOType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class ScheduleProcessIndexDocumentsDialogBox extends AbstractScheduleProcessDialogBox {

    private static final String DATE_FORMAT = "d. M. yyyy";
    protected DateTimeFormat dateFormat = DateTimeFormat.getFormat(DATE_FORMAT);

    private final UserAccountServiceAsync accountsService = GWT.create(UserAccountService.class);
    private ArrayList<RegistrarDTO> registrarsOfUser = new ArrayList<>();
    private final Label errorLabel = errorLabel(300);

    private CheckBox filterByRegistrationDate;
    private TextInputValueField registrationStartDate;
    private TextInputValueField registrationEndDate;

    public ScheduleProcessIndexDocumentsDialogBox(UserDTO user) {
        super(user);
        loadRegistrars();
        reload();
    }

    @Override
    public void open() {
        reload();
    }

    private void loadRegistrars() {
        AsyncCallback<ArrayList<RegistrarDTO>> callback = new AsyncCallback<ArrayList<RegistrarDTO>>() {

            public void onSuccess(ArrayList<RegistrarDTO> result) {
                registrarsOfUser = result;
                Collections.sort(registrarsOfUser, new Comparator<RegistrarDTO>() {
                    @Override
                    public int compare(RegistrarDTO o1, RegistrarDTO o2) {
                        return o1.getCode().compareTo(o2.getCode());
                    }
                });
                reload();
            }

            public void onFailure(Throwable caught) {
                Window.alert(messages.serverError(caught.getMessage()));
            }
        };
        if (user.isSuperAdmin()) {
            accountsService.getAllRegistrars(callback);
        } else {
            accountsService.getRegistrarsManagedByUser(callback);
        }
    }

    void reload() {
        clear();
        setText(messages.processPlaning(constants.DOCS_INDEXATION()));
        setAnimationEnabled(true);
        setWidget(contentPanel());
        center();
    }

    private Panel contentPanel() {
        VerticalPanel result = new VerticalPanel();
        result.add(filterByRegistrationDatePanel());
        result.add(buttonsPanel());
        result.add(errorLabel);
        return result;
    }

    private Panel filterByRegistrationDatePanel() {
        VerticalPanel result = new VerticalPanel();

        filterByRegistrationDate = new CheckBox(constants.processIndexDocumentsLimitByRegistrationDate());
        filterByRegistrationDate.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> valueChangeEvent) {
                if (valueChangeEvent.getValue()) {
                    registrationStartDate.enable();
                    registrationEndDate.enable();
                } else {
                    registrationStartDate.disable();
                    registrationEndDate.disable();
                }
            }
        });
        filterByRegistrationDate.setValue(true);
        result.add(filterByRegistrationDate);

        HorizontalPanel dataPanel = new HorizontalPanel();
        dataPanel.add(new HTML("&nbsp"));
        //start
        Date start = new Date(112, 8, 1); // 1.9.2012
        registrationStartDate = new TextInputValueField(new DateTimeValidator(DATE_FORMAT), "", dateFormat.format(start), false, 80);
        //registrationStartDate.disable();
        dataPanel.add(registrationStartDate.getContentWidget());
        //end
        registrationEndDate = new TextInputValueField(new DateTimeValidator(DATE_FORMAT), "", dateFormat.format(new Date()), false, 80);
        dataPanel.add(new HTML("&nbsp-&nbsp"));
        dataPanel.add(registrationEndDate.getContentWidget());
        //registrationEndDate.disable();
        result.add(dataPanel);

        result.add(new HTML("<br>"));
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
                String paramRegistrationStart = null;
                String paramRegistrationEnd = null;

                if (filterByRegistrationDate.getValue()) {
                    paramRegistrationStart = (String) registrationStartDate.getInsertedValue();
                    paramRegistrationEnd = (String) registrationEndDate.getInsertedValue();
                }

                boolean formOk = true;
                if (filterByRegistrationDate.getValue()) {
                    formOk &= registrationStartDate.validValueInserted();
                    formOk &= registrationEndDate.validValueInserted();
                }

                if (formOk) {
                    String[] params = new String[]{paramRegistrationStart, paramRegistrationEnd};
                    processService.scheduleProcess(ProcessDTOType.INDEXATION, params, new AsyncCallback<Void>() {

                        public void onSuccess(Void result) {
                            ScheduleProcessIndexDocumentsDialogBox.this.hide();
                        }

                        public void onFailure(Throwable caught) {
                            errorLabel.setText(caught.getMessage());
                        }
                    });
                }
            }
        });
    }

    private Button closeButton() {
        return new Button(constants.close(), new ClickHandler() {

            public void onClick(ClickEvent event) {
                ScheduleProcessIndexDocumentsDialogBox.this.hide();
            }
        });
    }

}
