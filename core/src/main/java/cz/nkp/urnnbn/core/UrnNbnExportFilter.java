package cz.nkp.urnnbn.core;

import org.joda.time.DateTime;

import java.util.List;

public class UrnNbnExportFilter {

    private DateTime begin;
    private DateTime end;
    private List<String> registrars;
    private List<String> entityTypes;
    private boolean withMissingCcnbOnly;
    private boolean withMissingIssnOnly;
    private boolean withMMissingIsbnOnly;
    private boolean returnActive;
    private boolean returnDeactivated;

    public UrnNbnExportFilter() {
    }

    public UrnNbnExportFilter(DateTime begin, DateTime end, List<String> registrars, List<String> entityTypes, boolean withMissingCcnbOnly,
                              boolean withMissingIssnOnly, boolean withMMissingIsbnOnly, boolean returnActive, boolean returnDeactivated) {
        this.begin = begin;
        this.end = end;
        this.registrars = registrars;
        this.entityTypes = entityTypes;
        this.withMissingCcnbOnly = withMissingCcnbOnly;
        this.withMissingIssnOnly = withMissingIssnOnly;
        this.withMMissingIsbnOnly = withMMissingIsbnOnly;
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

    public boolean getWithMissingCcnbOnly() {
        return withMissingCcnbOnly;
    }

    public boolean getWithMissingIssnOnly() {
        return withMissingIssnOnly;
    }

    public boolean getWithMMissingIsbnOnly() {
        return withMMissingIsbnOnly;
    }

    public boolean getReturnActive() {
        return returnActive;
    }

    public boolean getReturnDeactivated() {
        return returnDeactivated;
    }

    public void setBegin(DateTime begin) {
        this.begin = begin;
    }

    public void setEnd(DateTime end) {
        this.end = end;
    }

    public void setRegistrars(List<String> registrars) {
        this.registrars = registrars;
    }

    public void setEntityTypes(List<String> entityTypes) {
        this.entityTypes = entityTypes;
    }

    public void setWithMissingCcnbOnly(boolean withMissingCcnbOnly) {
        this.withMissingCcnbOnly = withMissingCcnbOnly;
    }

    public void setWithMissingIssnOnly(boolean withMissingIssnOnly) {
        this.withMissingIssnOnly = withMissingIssnOnly;
    }

    public void setWithMissingIsbnOnly(boolean withMMissingIsbnOnly) {
        this.withMMissingIsbnOnly = withMMissingIsbnOnly;
    }

    public void setReturnActive(boolean returnActive) {
        this.returnActive = returnActive;
    }

    public void setReturnDeactivated(boolean returnDeactivated) {
        this.returnDeactivated = returnDeactivated;
    }

}
