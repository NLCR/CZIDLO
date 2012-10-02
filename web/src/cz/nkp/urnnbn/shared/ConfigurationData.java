package cz.nkp.urnnbn.shared;

import java.io.Serializable;

public class ConfigurationData implements Serializable {

	private static final long serialVersionUID = -7688213096753239846L;
	private boolean showAlephLinks;
	private String alephUrl;
	private String alephBase;

	public boolean showAlephLinks() {
		return showAlephLinks;
	}

	public void setShowAlephLinks(boolean showAlephLinks) {
		this.showAlephLinks = showAlephLinks;
	}

	public String getAlephUrl() {
		return alephUrl;
	}

	public void setAlephUrl(String alephUrl) {
		this.alephUrl = alephUrl;
	}

	public String getAlephBase() {
		return alephBase;
	}

	public void setAlephBase(String alephBase) {
		this.alephBase = alephBase;
	}

	@Override
	public String toString() {
		return "ConfigurationData [showAlephLinks=" + showAlephLinks + ", alephUrl=" + alephUrl + ", alephBase=" + alephBase + "]";
	}
}
