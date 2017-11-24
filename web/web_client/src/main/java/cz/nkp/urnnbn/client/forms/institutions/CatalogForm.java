package cz.nkp.urnnbn.client.forms.institutions;

import cz.nkp.urnnbn.client.forms.Field;
import cz.nkp.urnnbn.client.forms.Form;
import cz.nkp.urnnbn.client.forms.FormFields;
import cz.nkp.urnnbn.client.forms.TextInputValueField;
import cz.nkp.urnnbn.shared.dto.CatalogDTO;
import cz.nkp.urnnbn.client.validation.LimitedLengthUrlValidator;
import cz.nkp.urnnbn.client.validation.LimitedLengthValidator;

public class CatalogForm extends Form {

    private final CatalogDTO catalog;

    public CatalogForm() {
        this(null);
    }

    public CatalogForm(CatalogDTO catalog) {
        if (catalog == null) {
            catalog = new CatalogDTO();
        }
        this.catalog = catalog;
        initForm();
    }

    @Override
    public FormFields buildFields() {
        FormFields result = new FormFields();
        Field name = new TextInputValueField(new LimitedLengthValidator(100), constants.title(), catalog.getName(), true);
        result.addField("name", name);
        Field description = new TextInputValueField(new LimitedLengthValidator(100), constants.description(), catalog.getDescription(), false);
        result.addField("description", description);
        Field urlPrefix = new TextInputValueField(new LimitedLengthUrlValidator(100), constants.urlPrefix(), catalog.getUrlPrefix(), true);
        result.addField("urlPrefix", urlPrefix);
        return result;
    }

    @Override
    public CatalogDTO getDto() {
        CatalogDTO result = new CatalogDTO();
        result.setId(catalog.getId());
        result.setRegistrarId(catalog.getRegistrarId());
        result.setName(getStringFieldValue("name"));
        result.setDescription(getStringFieldValue("description"));
        result.setUrlPrefix(getStringFieldValue("urlPrefix"));
        return result;
    }
}
