package cz.nkp.urnnbn.shared.dto;

import java.io.Serializable;

public class TechnicalMetadataDTO implements Serializable {

	private static final long serialVersionUID = -6198099230031713530L;
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

	public String getExtent() {
		return extent;
	}

	public void setExtent(String extent) {
		this.extent = extent;
	}

	public Integer getResolutionHorizontal() {
		return resolutionHorizontal;
	}

	public void setResolutionHorizontal(Integer resolutionHorizontal) {
		this.resolutionHorizontal = resolutionHorizontal;
	}

	public Integer getResolutionVertical() {
		return resolutionVertical;
	}

	public void setResolutionVertical(Integer resolutionVertical) {
		this.resolutionVertical = resolutionVertical;
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

	public String getColorModel() {
		return colorModel;
	}

	public void setColorModel(String colorModel) {
		this.colorModel = colorModel;
	}

	public Integer getColorDepth() {
		return colorDepth;
	}

	public void setColorDepth(Integer colorDepth) {
		this.colorDepth = colorDepth;
	}

	public String getIccProfile() {
		return iccProfile;
	}

	public void setIccProfile(String iccProfile) {
		this.iccProfile = iccProfile;
	}

	public Integer getPictureWidth() {
		return pictureWidth;
	}

	public void setPictureWidth(Integer pictureWidth) {
		this.pictureWidth = pictureWidth;
	}

	public Integer getPicturHeight() {
		return pictureHeight;
	}

	public void setPictureHeight(Integer pictureSize) {
		this.pictureHeight = pictureSize;
	}
}
