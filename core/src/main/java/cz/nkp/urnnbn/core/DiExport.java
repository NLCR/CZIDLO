package cz.nkp.urnnbn.core;

import org.joda.time.DateTime;

public class DiExport {

    private String registrarCode;
    private String documentCode;
    private boolean urnActive;
    private String ieType;
    private String diUrl;
    private boolean diActive;
    private String diFormat;
    private String diAccessiblility;
    private DateTime diCreated;
    private DateTime diDeactivated;

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

    public boolean isUrnActive() {
        return urnActive;
    }

    public void setUrnActive(boolean urnActive) {
        this.urnActive = urnActive;
    }

    public String getIeType() {
        return ieType;
    }

    public void setIeType(String ieType) {
        this.ieType = ieType;
    }

    public String getDiUrl() {
        return diUrl;
    }

    public void setDiUrl(String diUrl) {
        this.diUrl = diUrl;
    }

    public boolean isDiActive() {
        return diActive;
    }

    public void setDiActive(boolean diActive) {
        this.diActive = diActive;
    }

    public String getDiFormat() {
        return diFormat;
    }

    public void setDiFormat(String diFormat) {
        this.diFormat = diFormat;
    }

    public String getDiAccessiblility() {
        return diAccessiblility;
    }

    public void setDiAccessiblility(String diAccessiblility) {
        this.diAccessiblility = diAccessiblility;
    }

    public DateTime getDiCreated() {
        return diCreated;
    }

    public void setDiCreated(DateTime diCreated) {
        this.diCreated = diCreated;
    }

    public DateTime getDiDeactivated() {
        return diDeactivated;
    }

    public void setDiDeactivated(DateTime diDeactivated) {
        this.diDeactivated = diDeactivated;
    }

}
