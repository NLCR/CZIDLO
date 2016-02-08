package cz.nkp.urnnbn.core;

import java.util.List;

import org.joda.time.DateTime;

public class UrnNbnExportFilter {

    private DateTime begin;
    private DateTime end;
    private List<String> registrars;
    private List<String> entityTypes;
    private Boolean missingCcnb;
    private Boolean missingIssn;
    private Boolean missingIsbn;
    private Boolean returnActive;
    private Boolean returnDeactivated;

    public UrnNbnExportFilter() {
    }

    public UrnNbnExportFilter(DateTime begin, DateTime end, List<String> registrars, List<String> entityTypes, Boolean missingCcnb,
            Boolean missingIssn, Boolean missingIsbn, Boolean returnActive, Boolean returnDeactivated) {
        this.begin = begin;
        this.end = end;
        this.registrars = registrars;
        this.entityTypes = entityTypes;
        this.missingCcnb = missingCcnb;
        this.missingIssn = missingIssn;
        this.missingIsbn = missingIsbn;
        this.returnActive = returnActive;
        this.returnDeactivated = returnDeactivated;
    }

    public DateTime getBegin() {
        return begin;
    }

    public DateTime getEnd() {
        return end;
    }

    public List<String> getRegistrars() {
        return registrars;
    }

    public List<String> getEntityTypes() {
        return entityTypes;
    }

    public Boolean getMissingCcnb() {
        return missingCcnb;
    }

    public Boolean getMissingIssn() {
        return missingIssn;
    }

    public Boolean getMissingIsbn() {
        return missingIsbn;
    }

    public Boolean getReturnActive() {
        return returnActive;
    }

    public Boolean getReturnDeactivated() {
        return returnDeactivated;
    }

    public void setBegin(DateTime begin) {
        this.begin = begin;
    }

    public void setEnd(DateTime end) {
        this.end = end;
    }

    public void setRegistrars(List<String> registrars) {
        if (registrars == null) {
            throw new NullPointerException("registrars");
        }
        this.registrars = registrars;
    }

    public void setEntityTypes(List<String> entityTypes) {
        if (entityTypes == null) {
            throw new NullPointerException("entityTypes");
        }
        this.entityTypes = entityTypes;
    }

    public void setMissingCcnb(Boolean missingCcnb) {
        if (missingCcnb == null) {
            throw new NullPointerException("missingCcnb");
        }
        this.missingCcnb = missingCcnb;
    }

    public void setMissingIssn(Boolean missingIssn) {
        if (missingIssn == null) {
            throw new NullPointerException("missingIssn");
        }
        this.missingIssn = missingIssn;
    }

    public void setMissingIsbn(Boolean missingIsbn) {
        if (missingIsbn == null) {
            throw new NullPointerException("missingIsbn");
        }
        this.missingIsbn = missingIsbn;
    }

    public void setReturnActive(Boolean returnActive) {
        if (returnActive == null) {
            throw new NullPointerException("returnActive");
        }
        this.returnActive = returnActive;
    }

    public void setReturnDeactivated(Boolean returnDeactivated) {
        if (returnDeactivated == null) {
            throw new NullPointerException("returnDeactivated");
        }
        this.returnDeactivated = returnDeactivated;
    }

}
