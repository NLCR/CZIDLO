package cz.nkp.urnnbn.shared.dto.ie;

import java.io.Serializable;

public class PeriodicalIssueDTO extends PublishableEntityDTO implements Serializable {

    private static final long serialVersionUID = 1394915298201192582L;
    private String periodicalTitle;
    private String volumeTitle;
    private String issueTitle;
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

    public String getIssueTitle() {
        return issueTitle;
    }

    public void setIssueTitle(String issueTitle) {
        this.issueTitle = issueTitle;
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
