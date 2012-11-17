package cz.nkp.urnnbn.shared.dto;

import java.io.Serializable;

public class UrnNbnDTO implements Serializable {

	private static final long serialVersionUID = -8544388940624679984L;
	private static final String PREFIX = "urn:nbn:";
	private String countryCode;
	private String registrarCode;
	private String documentCode;
	private Long digdocId;
	private boolean active;
	private String created;
	private String lastModified;

	public UrnNbnDTO() {}

	public UrnNbnDTO(String countryCode, String registrarCode, String documentCode, Long digdocId, boolean active, String created,
			String lastModified) {
		this.countryCode = countryCode;
		this.registrarCode = registrarCode;
		this.documentCode = documentCode;
		this.digdocId = digdocId;
		this.active = active;
		this.created = created;
		this.lastModified = lastModified;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getRegistrarCode() {
		return registrarCode;
	}

	public void setRegistrarCode(String registrarCode) {
		this.registrarCode = registrarCode;
	}

	public String getDocumentCode() {
		return documentCode;
	}

	public void setDocumentCode(String documentCode) {
		this.documentCode = documentCode;
	}

	public Long getDigdocId() {
		return digdocId;
	}

	public void setDigdocId(Long digdocId) {
		this.digdocId = digdocId;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getLastModified() {
		return lastModified;
	}

	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}

	public String toString() {
		return PREFIX + countryCode + ':' + registrarCode + '-' + documentCode;
	}
}
