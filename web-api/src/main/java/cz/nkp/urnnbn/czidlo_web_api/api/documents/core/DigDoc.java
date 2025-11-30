package cz.nkp.urnnbn.czidlo_web_api.api.documents.core;

import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.czidlo_web_api.api.Utils;

import java.util.Date;

//@XmlRootElement(name = "digitalDocument")
//@XmlAccessorType(XmlAccessType.FIELD)
public class DigDoc {
    private Long id;
    private Date created;
    private Date modified;
    //
    public String financedFrom;
    public String contractNumber;
    //
    public String format;
    public String formatVersion;
    public String extent;
    public Integer resolutionHorizontal;
    public Integer resolutionVertical;
    public String compression;
    public Double compressionRatio;
    public String colorModel;
    public Integer colorDepth;
    public String iccProfile;
    public Integer pictureWidth;
    public Integer pictureHeight;

    public static DigDoc from(DigitalDocument dtoDigDoc) {
        DigDoc digDoc = new DigDoc();
        digDoc.id = dtoDigDoc.getId();
        digDoc.created = Utils.dateTimeToDate(dtoDigDoc.getCreated());
        digDoc.modified = Utils.dateTimeToDate(dtoDigDoc.getModified());
        digDoc.financedFrom = dtoDigDoc.getFinancedFrom();
        digDoc.contractNumber = dtoDigDoc.getContractNumber();
        digDoc.format = dtoDigDoc.getFormat();
        digDoc.formatVersion = dtoDigDoc.getFormatVersion();
        digDoc.extent = dtoDigDoc.getExtent();
        digDoc.resolutionHorizontal = dtoDigDoc.getResolutionHorizontal();
        digDoc.resolutionVertical = dtoDigDoc.getResolutionVertical();
        digDoc.compression = dtoDigDoc.getCompression();
        digDoc.compressionRatio = dtoDigDoc.getCompressionRatio();
        digDoc.colorModel = dtoDigDoc.getColorModel();
        digDoc.colorDepth = dtoDigDoc.getColorDepth();
        digDoc.iccProfile = dtoDigDoc.getIccProfile();
        digDoc.pictureWidth = dtoDigDoc.getPictureWidth();
        digDoc.pictureHeight = dtoDigDoc.getPictureHeight();
        return digDoc;
    }

    public Long getId() {
        return id;
    }

    public Date getCreated() {
        return created;
    }

    public Date getModified() {
        return modified;
    }

    public String getFinancedFrom() {
        return financedFrom;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public String getFormat() {
        return format;
    }

    public String getFormatVersion() {
        return formatVersion;
    }

    public String getExtent() {
        return extent;
    }

    public Integer getResolutionHorizontal() {
        return resolutionHorizontal;
    }

    public Integer getResolutionVertical() {
        return resolutionVertical;
    }

    public String getCompression() {
        return compression;
    }

    public Double getCompressionRatio() {
        return compressionRatio;
    }

    public String getColorModel() {
        return colorModel;
    }

    public Integer getColorDepth() {
        return colorDepth;
    }

    public String getIccProfile() {
        return iccProfile;
    }

    public Integer getPictureWidth() {
        return pictureWidth;
    }

    public Integer getPictureHeight() {
        return pictureHeight;
    }

    public DigitalDocument toDtoDigDoc() {
        DigitalDocument dtoDigDoc = new DigitalDocument();
        dtoDigDoc.setFinancedFrom(financedFrom);
        dtoDigDoc.setContractNumber(contractNumber);
        dtoDigDoc.setFormat(format);
        dtoDigDoc.setFormatVersion(formatVersion);
        dtoDigDoc.setExtent(extent);
        dtoDigDoc.setResolutionHorizontal(resolutionHorizontal);
        dtoDigDoc.setResolutionVertical(resolutionVertical);
        dtoDigDoc.setCompression(compression);
        dtoDigDoc.setCompressionRatio(compressionRatio);
        dtoDigDoc.setColorModel(colorModel);
        dtoDigDoc.setColorDepth(colorDepth);
        dtoDigDoc.setIccProfile(iccProfile);
        dtoDigDoc.setPictureWidth(pictureWidth);
        dtoDigDoc.setPictureHeight(pictureHeight);
        return dtoDigDoc;
    }

    @Override
    public String toString() {
        return "DigDoc{" +
                "id=" + id +
                ",\n created=" + created +
                ",\n modified=" + modified +
                ",\n financedFrom='" + financedFrom + '\'' +
                ",\n contractNumber='" + contractNumber + '\'' +
                ",\n format='" + format + '\'' +
                ",\n formatVersion='" + formatVersion + '\'' +
                ",\n extent='" + extent + '\'' +
                ",\n resolutionHorizontal=" + resolutionHorizontal +
                ",\n resolutionVertical=" + resolutionVertical +
                ",\n compression='" + compression + '\'' +
                ",\n compressionRatio=" + compressionRatio +
                ",\n colorModel='" + colorModel + '\'' +
                ",\n colorDepth=" + colorDepth +
                ",\n iccProfile='" + iccProfile + '\'' +
                ",\n pictureWidth=" + pictureWidth +
                ",\n pictureHeight=" + pictureHeight +
                "\n}";
    }
}



