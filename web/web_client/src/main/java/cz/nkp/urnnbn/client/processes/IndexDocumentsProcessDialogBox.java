package cz.nkp.urnnbn.client.processes;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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

public class IndexDocumentsProcessDialogBox extends AbstractScheduleProcessDialogBox {

    private static final String DATE_FORMAT = "d. M. yyyy H:m.s";
    protected DateTimeFormat dateFormat = DateTimeFormat.getFormat(DATE_FORMAT);

    private final UserAccountServiceAsync accountsService = GWT.create(UserAccountService.class);
    private ArrayList<RegistrarDTO> registrarsOfUser = new ArrayList<>();
    private final Label errorLabel = errorLabel(320);

    private TextInputValueField beginDate;
    private TextInputValueField endDate;

    /*private MultiSelectListBox registrarsListBox;
    private MultiSelectListBox documentTypeListBox;
    private ListBox activationFlag;*/

    public IndexDocumentsProcessDialogBox(UserDTO user) {
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
        result.add(selectDateRangePanel());
        //result.add(selectRegistrarsPanel());
        //result.add(selectTypeOfDocumentPanel());
        //result.add(selectActivationFlag());
        result.add(buttonsPanel());
        result.add(errorLabel);
        return result;
    }

    @SuppressWarnings("deprecation")
    private Panel selectDateRangePanel() {
        HorizontalPanel result = new HorizontalPanel();
        Date start = new Date(112, 8, 1); // 1.9.2012
        beginDate = new TextInputValueField(new DateTimeValidator(DATE_FORMAT), constants.timestampRegistered(), dateFormat.format(start), false);
        result.add(beginDate.getLabelWidget());
        result.add(beginDate.getContentWidget());
        endDate = new TextInputValueField(new DateTimeValidator(DATE_FORMAT), "", dateFormat.format(new Date()), false);
        result.add(new HTML(" - "));
        result.add(endDate.getContentWidget());
        return result;
    }

   /*private Panel selectRegistrarsPanel() {
        HorizontalPanel result = new HorizontalPanel();
        result.add(new Label(constants.registrar() + SEPARATOR));
        initRegistrarsList();
        result.add(registrarsListBox);
        return result;
    }*/

    /*private Panel selectTypeOfDocumentPanel() {
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
    }*/

    /*private Panel selectActivationFlag() {
        HorizontalPanel result = new HorizontalPanel();
        result.add(new Label(constants.activityFlag() + SEPARATOR));
        activationFlag = new ListBox();
        activationFlag.addItem(constants.activityAll());
        activationFlag.addItem(constants.activityActiveOnly());
        activationFlag.addItem(constants.activityDeactivatedOnly());
        result.add(activationFlag);
        return result;
    }*/

    private Panel buttonsPanel() {
        HorizontalPanel result = new HorizontalPanel();
        result.setSpacing(10);
        result.add(scheduleProcessButton());
        result.add(closeButton());
        return result;
    }

    /*private void initRegistrarsList() {
        registrarsListBox = new MultiSelectListBox();
        for (int i = 0; i < registrarsOfUser.size(); i++) {
            RegistrarDTO registrar = registrarsOfUser.get(i);
            registrarsListBox.addItem(registrar.getCode());
            registrarsListBox.setItemSelected(i, true);
        }
    }*/

    private Button scheduleProcessButton() {
        return new Button(constants.scheduleProcess(), new ClickHandler() {

            public void onClick(ClickEvent event) {
                String begin = (String) beginDate.getInsertedValue();
                String end = (String) endDate.getInsertedValue();

                /*String registrars = null;
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
                int activitySelectedIndex = activationFlag.getSelectedIndex();
                Boolean returnActive = activitySelectedIndex == 0 || activitySelectedIndex == 1;
                Boolean returnDeactivated = activitySelectedIndex == 0 || activitySelectedIndex == 2;*/

                String[] params = new String[]{begin, end};
                //String[] params = new String[]{begin, end, registrars, entityTypes, returnActive.toString(), returnDeactivated.toString()};
                processService.scheduleProcess(ProcessDTOType.INDEXATION, params, new AsyncCallback<Void>() {

                    public void onSuccess(Void result) {
                        IndexDocumentsProcessDialogBox.this.hide();
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
                IndexDocumentsProcessDialogBox.this.hide();
            }
        });
    }

}