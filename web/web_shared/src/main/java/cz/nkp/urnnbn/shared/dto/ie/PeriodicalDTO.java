package cz.nkp.urnnbn.shared.dto.ie;

import java.io.Serializable;

public class PeriodicalDTO extends PublishableEntityDTO implements Serializable {
    private static final long serialVersionUID = 8898201694415512017L;
    private String title;
    private String subTitle;
    private String ccnb;
    private String issn;
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
