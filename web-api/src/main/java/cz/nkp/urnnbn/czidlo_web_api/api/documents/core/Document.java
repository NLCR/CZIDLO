package cz.nkp.urnnbn.czidlo_web_api.api.documents.core;

import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.czidlo_web_api.api.Utils;

import java.util.Date;

//@XmlRootElement(name = "digitalDocument")
//@XmlAccessorType(XmlAccessType.FIELD)
public class Document {
    private Date created;
    private Date modified;
    //
    private String financedFrom;
    private String contractNumber;
    //
    private String format;
    private String formatVersion;
    private String extent;
    private Integer resolutionHorizontal;
    private Integer resolutionVertical;
    private String compression;
    private Double compressionRatio;
    private String colorModel;
    private Integer colorDepth;
    private String iccProfile;
    private Integer pictureWidth;
    private Integer pictureHeight;

    public static Document from(DigitalDocument dtoDigDoc) {
        Document digDoc = new Document();
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
}



