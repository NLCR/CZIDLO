package cz.nkp.urnnbn.shared.dto;

import java.io.Serializable;

public class ContentDTO implements Serializable {

	private static final long serialVersionUID = 3837482948189700032L;

	private Long id;

	private String language;

	private String name;

	private String content;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
