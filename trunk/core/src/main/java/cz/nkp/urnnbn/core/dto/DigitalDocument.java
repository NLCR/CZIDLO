/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.dto;

import org.joda.time.DateTime;

/**
 *
 * @author Martin Řehánek
 */
public class DigitalDocument implements IdentifiableWithDatestamps {

    private long id;
    private long intEntId;
    private long registrarId;
    private long archiverId;
    //
    private DateTime created;
    private DateTime modified;
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
    private Integer pictureHight;

    public DigitalDocument() {
    }

    public DigitalDocument(DigitalDocument original) {
        id = original.getId();
        intEntId = original.getIntEntId();
        registrarId = original.getRegistrarId();
        archiverId = original.getArchiverId();
        created = original.getCreated();
        modified = original.getModified();
        extent = original.getExtent();
        financedFrom = original.getFinancedFrom();
        contractNumber = original.getContractNumber();
        format = original.getFormat();
        formatVersion = original.getFormatVersion();
        resolutionHorizontal = original.getResolutionHorizontal();
        resolutionVertical = original.getResolutionVertical();
        compression = original.getCompression();
        compressionRatio = original.getCompressionRatio();
        colorModel = original.getColorModel();
        colorDepth = original.getColorDepth();
        iccProfile = original.getIccProfile();
        pictureWidth = original.getPictureWidth();
        pictureHight = original.getPictureHeight();
    }

    public long getArchiverId() {
        return archiverId;
    }

    public void setArchiverId(long archiverId) {
        this.archiverId = archiverId;
    }

    @Override
    public DateTime getCreated() {
        return created;
    }

    public void setCreated(DateTime created) {
        this.created = created;
    }

    public String getExtent() {
        return extent;
    }

    public void setExtent(String extent) {
        this.extent = extent;
    }

    public String getFinancedFrom() {
        return financedFrom;
    }

    public void setFinancedFrom(String financedFrom) {
        this.financedFrom = financedFrom;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    public long getIntEntId() {
        return intEntId;
    }

    public void setIntEntId(long intEntId) {
        this.intEntId = intEntId;
    }

    @Override
    public DateTime getModified() {
        return modified;
    }

    public void setModified(DateTime modified) {
        this.modified = modified;
    }

    public long getRegistrarId() {
        return registrarId;
    }

    public void setRegistrarId(long registrarId) {
        this.registrarId = registrarId;
    }

    public Integer getColorDepth() {
        return colorDepth;
    }

    public void setColorDepth(Integer colorDepth) {
        this.colorDepth = colorDepth;
    }

    public String getColorModel() {
        return colorModel;
    }

    public void setColorModel(String colorModel) {
        this.colorModel = colorModel;
    }

    public String getCompression() {
        return compression;
    }

    public void setCompression(String compression) {
        this.compression = compression;
    }

    public Double getCompressionRatio() {
        return compressionRatio;
    }

    public void setCompressionRatio(Double compressionRatio) {
        this.compressionRatio = compressionRatio;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getFormatVersion() {
        return formatVersion;
    }

    public void setFormatVersion(String formatVersion) {
        this.formatVersion = formatVersion;
    }

    public Integer getPictureHeight() {
        return pictureHight;
    }

    public void setPictureHeight(Integer height) {
        this.pictureHight = height;
    }

    public String getIccProfile() {
        return iccProfile;
    }

    public void setIccProfile(String iccProfile) {
        this.iccProfile = iccProfile;
    }

    public Integer getResolutionVertical() {
        return resolutionVertical;
    }

    public void setResolutionVertical(Integer resolutionVertical) {
        this.resolutionVertical = resolutionVertical;
    }

    public Integer getResolutionHorizontal() {
        return resolutionHorizontal;
    }

    public void setResolutionHorizontal(Integer resolutionHorizontal) {
        this.resolutionHorizontal = resolutionHorizontal;
    }

    public Integer getPictureWidth() {
        return pictureWidth;
    }

    public void setPictureWidth(Integer width) {
        this.pictureWidth = width;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DigitalDocument other = (DigitalDocument) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }
}
