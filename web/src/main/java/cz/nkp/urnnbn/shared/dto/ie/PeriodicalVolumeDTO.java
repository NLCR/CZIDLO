package cz.nkp.urnnbn.shared.dto.ie;

import java.io.Serializable;

public class PeriodicalVolumeDTO extends PublishableEntityDTO implements
		Serializable {
	private static final long serialVersionUID = -993270887502100469L;
	private String periodicalTitle;
	private String volumeTitle;
	private String ccnb;
	private String issn;
	private String otherId;

	public String getPeriodicalTitle() {
		return periodicalTitle;
	}

	public void setPeriodicalTitle(String periodicalTitle) {
		this.periodicalTitle = periodicalTitle;
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

	public String getIssn() {
		return issn;
	}

	public void setIssn(String issn) {
		this.issn = issn;
	}

	public String getOtherId() {
		return otherId;
	}

	public void setOtherId(String otherId) {
		this.otherId = otherId;
	}

}
