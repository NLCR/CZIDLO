package cz.nkp.urnnbn.shared.dto.ie;

import java.io.Serializable;

public class ThesisDTO extends PublishableEntityDTO implements Serializable {

    private static final long serialVersionUID = -2280746237974139920L;
    private String title;
    private String subTitle;
    private String ccnb;
    private String otherId;
    private String degreeAwardingInstitution;

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

    public String getOtherId() {
        return otherId;
    }

    public void setOtherId(String otherId) {
        this.otherId = otherId;
    }

    public void setDegreeAwardingInstitution(String degreeAwardingInstitution) {
        this.degreeAwardingInstitution = degreeAwardingInstitution;
    }

    public String getDegreeAwardingInstitution() {
        return degreeAwardingInstitution;
    }
}
