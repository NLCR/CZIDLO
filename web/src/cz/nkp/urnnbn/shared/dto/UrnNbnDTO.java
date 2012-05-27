package cz.nkp.urnnbn.shared.dto;

import java.io.Serializable;

public class UrnNbnDTO implements Serializable {

	private static final long serialVersionUID = -8544388940624679984L;
	private static final String PREFIX = "urn:nbn:cz:";
	private String registrarCode;
	private String documentCode;
	private Long digdocId;
	
	public UrnNbnDTO(){}

	public UrnNbnDTO(String registrarCode, String documentCode, Long digdocId) {
		this.registrarCode = registrarCode;
		this.documentCode = documentCode;
		this.digdocId = digdocId;
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

	public String toString() {
		return PREFIX + registrarCode + '-' + documentCode;
	}
}
