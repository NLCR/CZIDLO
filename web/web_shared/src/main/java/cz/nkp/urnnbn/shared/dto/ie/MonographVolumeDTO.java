package cz.nkp.urnnbn.shared.dto.ie;

import java.io.Serializable;

public class MonographVolumeDTO extends PublishableEntityDTO implements Serializable{
	
	private static final long serialVersionUID = -2590547451858364038L;
	private String monographTitle;
	private String volumeTitle;
	private String ccnb;
	private String isbn;
	private String otherId;
	public String getMonographTitle() {
		return monographTitle;
	}
	public void setMonographTitle(String monographTitle) {
		this.monographTitle = monographTitle;
	}
	public String getVolumeTitle() {
		return volumeTitle;
	}
	public void setVolumeTitle(String volumeTitle) {
		this.volumeTitle = volumeTitle;
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
