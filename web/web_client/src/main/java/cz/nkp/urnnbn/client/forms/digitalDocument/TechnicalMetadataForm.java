package cz.nkp.urnnbn.client.forms.digitalDocument;

import cz.nkp.urnnbn.client.forms.DoubleInputValueField;
import cz.nkp.urnnbn.client.forms.Field;
import cz.nkp.urnnbn.client.forms.Form;
import cz.nkp.urnnbn.client.forms.FormFields;
import cz.nkp.urnnbn.client.forms.IntegerInputValueField;
import cz.nkp.urnnbn.client.forms.TextInputValueField;
import cz.nkp.urnnbn.shared.dto.TechnicalMetadataDTO;
import cz.nkp.urnnbn.client.validation.LimitedLengthValidator;
import cz.nkp.urnnbn.client.validation.PositiveIntegerValidator;
import cz.nkp.urnnbn.client.validation.PositiveRealNumberValidator;

public class TechnicalMetadataForm extends Form {

    private final TechnicalMetadataDTO dto;

    public TechnicalMetadataForm() {
        this(null);
    }

    public TechnicalMetadataForm(TechnicalMetadataDTO dto) {
        if (dto == null) {
            dto = new TechnicalMetadataDTO();
        }
        this.dto = dto;
        initForm();
    }

    @Override
    public FormFields buildFields() {
        FormFields result = new FormFields();
        Field format = new TextInputValueField(new LimitedLengthValidator(100), constants.format(), dto.getFormat(), false);
        result.addField("format", format);

        Field formatVersion = new TextInputValueField(new LimitedLengthValidator(100), constants.formatVersion(), dto.getFormatVersion(), false);
        result.addField("formatVersion", formatVersion);

        Field extent = new TextInputValueField(new LimitedLengthValidator(100), constants.extent(), dto.getExtent(), false);
        result.addField("extent", extent);

        Field resHorizontal = new IntegerInputValueField(new PositiveIntegerValidator(), constants.horizontalResolution() + " (" + constants.inDpi()
                + ")", dto.getResolutionHorizontal(), false);
        result.addField("resHorizontal", resHorizontal);

        Field resVertical = new IntegerInputValueField(new PositiveIntegerValidator(), constants.verticalResolution() + " (" + constants.inDpi()
                + ")", dto.getResolutionVertical(), false);
        result.addField("resVertical", resVertical);

        Field compression = new TextInputValueField(new LimitedLengthValidator(100), constants.compressionAlgorithm(), dto.getCompression(), false);
        result.addField("compression", compression);

        Field compressionRatio = new DoubleInputValueField(new PositiveRealNumberValidator(), constants.compressionRatio(),
                dto.getCompressionRatio(), false);
        result.addField("compressionRatio", compressionRatio);

        Field colorModel = new TextInputValueField(new LimitedLengthValidator(100), constants.colorModel(), dto.getColorModel(), false);
        result.addField("colorModel", colorModel);

        Field colorDepth = new IntegerInputValueField(new PositiveIntegerValidator(), constants.colorDepth() + " (" + constants.inBits() + ")",
                dto.getColorDepth(), false);
        result.addField("colorDepth", colorDepth);

        Field iccProfile = new TextInputValueField(new LimitedLengthValidator(100), constants.iccProfile(), dto.getIccProfile(), false);
        result.addField("iccProfile", iccProfile);

        Field pictureWidth = new IntegerInputValueField(new PositiveIntegerValidator(), constants.picsWidth() + " (" + constants.inPixels() + ")",
                dto.getPictureWidth(), false);
        result.addField("picWidth", pictureWidth);

        Field pictureHeight = new IntegerInputValueField(new PositiveIntegerValidator(), constants.picsHeight() + " (" + constants.inPixels() + ")",
                dto.getPicturHeight(), false);
        result.addField("picHeight", pictureHeight);
        return result;
    }

    @Override
    public TechnicalMetadataDTO getDto() {
        TechnicalMetadataDTO result = new TechnicalMetadataDTO();
        result.setFormat(getStringFieldValue("format"));
        result.setFormatVersion(getStringFieldValue("formatVersion"));
        result.setExtent(getStringFieldValue("extent"));
        result.setResolutionHorizontal(getIntegerFieldValue("resHorizontal"));
        result.setResolutionVertical(getIntegerFieldValue("resVertical"));
        result.setCompression(getStringFieldValue("compression"));
        result.setCompressionRatio(getDoubleFieldValue("compressionRatio"));
        result.setColorModel(getStringFieldValue("colorModel"));
        result.setColorDepth(getIntegerFieldValue("colorDepth"));
        result.setIccProfile(getStringFieldValue("iccProfile"));
        result.setPictureWidth(getIntegerFieldValue("picWidth"));
        result.setPictureHeight(getIntegerFieldValue("picHeight"));
        return result;
    }
}
