package cz.nkp.urnnbn.shared;

import java.io.Serializable;

public class ConfigurationData implements Serializable {

    private static final long serialVersionUID = -7688213096753239846L;
    private boolean showAlephLinks;
    private String alephUrl;
    private String alephBase;
    private String countryCode;
    private String loginPage;
    private String gaTrackingCode;

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

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setLoginPage(String loginPage) {
        this.loginPage = loginPage;
    }

    public String getLoginPage() {
        return loginPage;
    }

    @Override
    public String toString() {
        return "ConfigurationData [showAlephLinks=" + showAlephLinks + ", alephUrl=" + alephUrl + ", alephBase=" + alephBase + ", countryCode="
                + countryCode + ", loginPage=" + loginPage + "]";
    }

    public String getGaTrackingCode() {
        return gaTrackingCode;
    }

    public void setGaTrackingCode(String gaTrackingCode) {
        this.gaTrackingCode = gaTrackingCode;
    }

}
