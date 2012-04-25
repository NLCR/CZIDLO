package cz.nkp.urnnbn.shared.dto;

import java.io.Serializable;

public class DigitalInstanceDTO implements Serializable {

	private static final long serialVersionUID = -9032263437314075051L;
	private Long id;
	private DigitalLibraryDTO library;
	private String created;
	private String modified;
	private String url;
	private String format;
	private String accessibility;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public DigitalLibraryDTO getLibrary() {
		return library;
	}

	public void setLibrary(DigitalLibraryDTO library) {
		this.library = library;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String published) {
		this.created = published;
	}

	public String getModified() {
		return modified;
	}

	public void setModified(String modified) {
		this.modified = modified;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getAccessibility() {
		return accessibility;
	}

	public void setAccessibility(String accessibility) {
		this.accessibility = accessibility;
	}

}
