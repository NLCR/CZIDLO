package cz.nkp.urnnbn.shared.dto.ie;

import java.io.Serializable;

public class MonographDTO extends PublishableEntityDTO implements Serializable {

	private static final long serialVersionUID = -4761020860304317926L;
	private String title;
	private String subTitle;
	private String ccnb;
	private String isbn;
	private String otherId;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public String getCcnb() {
		return ccnb;
	}

	public void setCcnb(String ccnb) {
		this.ccnb = ccnb;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getOtherId() {
		return otherId;
	}

	public void setOtherId(String otherId) {
		this.otherId = otherId;
	}

}
