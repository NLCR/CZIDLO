package cz.nkp.urnnbn.client.search;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import cz.nkp.urnnbn.client.AbstractDialogBox;
import cz.nkp.urnnbn.client.forms.intEntities.AnalyticalForm;
import cz.nkp.urnnbn.client.forms.intEntities.IntelectualEntityForm;
import cz.nkp.urnnbn.client.forms.intEntities.MonographForm;
import cz.nkp.urnnbn.client.forms.intEntities.MonographVolumeForm;
import cz.nkp.urnnbn.client.forms.intEntities.OtherEntityForm;
import cz.nkp.urnnbn.client.forms.intEntities.PeriodicalForm;
import cz.nkp.urnnbn.client.forms.intEntities.PeriodicalIssueForm;
import cz.nkp.urnnbn.client.forms.intEntities.PeriodicalVolumeForm;
import cz.nkp.urnnbn.client.forms.intEntities.SourceDocumentForm;
import cz.nkp.urnnbn.client.forms.intEntities.ThesisForm;
import cz.nkp.urnnbn.client.services.DataService;
import cz.nkp.urnnbn.client.services.DataServiceAsync;
import cz.nkp.urnnbn.shared.dto.ie.AnalyticalDTO;
import cz.nkp.urnnbn.shared.dto.ie.IntelectualEntityDTO;
import cz.nkp.urnnbn.shared.dto.ie.MonographDTO;
import cz.nkp.urnnbn.shared.dto.ie.MonographVolumeDTO;
import cz.nkp.urnnbn.shared.dto.ie.OtherEntityDTO;
import cz.nkp.urnnbn.shared.dto.ie.PeriodicalDTO;
import cz.nkp.urnnbn.shared.dto.ie.PeriodicalIssueDTO;
import cz.nkp.urnnbn.shared.dto.ie.PeriodicalVolumeDTO;
import cz.nkp.urnnbn.shared.dto.ie.PrimaryOriginatorDTO;
import cz.nkp.urnnbn.shared.dto.ie.ThesisDTO;

public class EditIntelectualEntityDialogBox extends AbstractDialogBox {

    private final DataServiceAsync dataService = GWT.create(DataService.class);
    private final SearchTab superPanel;
    private IntelectualEntityForm entityForm;
    private SourceDocumentForm srcDocForm;
    private String title;
    private final Label errorLabel = errorLabel(320);

    public EditIntelectualEntityDialogBox(SearchTab superPanel, IntelectualEntityDTO entity, PrimaryOriginatorDTO originator,
            SourceDocumentForm srcDocForm) {
        this.superPanel = superPanel;
        this.srcDocForm = srcDocForm;
        setAnimationEnabled(true);
        initFormsAndTitle(entity, originator);
        setText(title);
        setWidget(contentPanel());
        center();
    }

    private void initFormsAndTitle(IntelectualEntityDTO entity, PrimaryOriginatorDTO originator) {
        if (entity instanceof AnalyticalDTO) {
            title = buildTitle(constants.analytical());
            entityForm = new AnalyticalForm((AnalyticalDTO) entity, originator);
        } else if (entity instanceof MonographDTO) {
            title = buildTitle(constants.monograph());
            entityForm = new MonographForm((MonographDTO) entity, originator);
        } else if (entity instanceof MonographVolumeDTO) {
            title = buildTitle(constants.monographVolume());
            entityForm = new MonographVolumeForm((MonographVolumeDTO) entity, originator);
        } else if (entity instanceof PeriodicalDTO) {
            title = buildTitle(constants.periodical());
            entityForm = new PeriodicalForm((PeriodicalDTO) entity, originator);
        } else if (entity instanceof PeriodicalVolumeDTO) {
            title = buildTitle(constants.periodicalVolume());
            entityForm = new PeriodicalVolumeForm((PeriodicalVolumeDTO) entity, originator);
        } else if (entity instanceof PeriodicalIssueDTO) {
            title = buildTitle(constants.periodicalIssue());
            entityForm = new PeriodicalIssueForm((PeriodicalIssueDTO) entity, originator);
        } else if (entity instanceof ThesisDTO) {
            title = buildTitle(constants.thesis());
            entityForm = new ThesisForm((ThesisDTO) entity, originator);
        } else if (entity instanceof OtherEntityDTO) {
            title = buildTitle(constants.otherEntity());
            entityForm = new OtherEntityForm((OtherEntityDTO) entity, originator);
        }
    }

    private String buildTitle(String entityType) {
        return entityType + " - " + constants.recordAdjustment();
    }

    private Panel contentPanel() {
        VerticalPanel result = new VerticalPanel();
        result.add(entityForm);
        if (srcDocForm != null) {
            Label heading = new Label(constants.sourceDoc());
            heading.setStyleName(css.heading());
            result.add(heading);
            result.add(srcDocForm);
        }
        result.add(buttons());
        result.add(errorLabel);
        return result;
    }

    private Panel buttons() {
        HorizontalPanel buttons = new HorizontalPanel();
        buttons.add(saveButton());
        buttons.add(closeButtion());
        return buttons;
    }

    private Widget saveButton() {
        return new Button(constants.save(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (entityForm.isFilledCorrectly() && (srcDocForm == null || srcDocForm.isFilledCorrectly())) {
                    IntelectualEntityDTO entity = entityForm.getDto();
                    if (entity instanceof AnalyticalDTO && srcDocForm != null) {
                        ((AnalyticalDTO) entity).setSourceDocument(srcDocForm.getDto());
                    }
                    dataService.updateIntelectualEntity(entity, new AsyncCallback<Void>() {

                        @Override
                        public void onSuccess(Void result) {
                            superPanel.refresh();
                            EditIntelectualEntityDialogBox.this.hide();
                        }

                        @Override
                        public void onFailure(Throwable caught) {
                            errorLabel.setText(messages.serverError(caught.getMessage()));
                        }
                    });
                }
            }
        });
    }

    private Widget closeButtion() {
        return new Button(constants.close(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                EditIntelectualEntityDialogBox.this.hide();
            }
        });
    }

}
