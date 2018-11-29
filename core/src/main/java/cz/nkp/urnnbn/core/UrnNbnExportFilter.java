package cz.nkp.urnnbn.core;

import org.joda.time.DateTime;

import java.util.List;

public class UrnNbnExportFilter {

    private DateTime registrationStart;
    private DateTime registrationEnd;
    private List<String> registrars;
    private List<String> entityTypes;
    private boolean withMissingCcnbOnly;
    private boolean withMissingIssnOnly;
    private boolean withMMissingIsbnOnly;
    private boolean returnActive;
    private boolean returnDeactivated;
    private DateTime deactivationStart;
    private DateTime deactivationEnd;

    public UrnNbnExportFilter() {
    }

    public UrnNbnExportFilter(DateTime registrationStart, DateTime registrationEnd, List<String> registrars, List<String> entityTypes, boolean withMissingCcnbOnly,
                              boolean withMissingIssnOnly, boolean withMMissingIsbnOnly, boolean returnActive, boolean returnDeactivated, DateTime deactivationStart, DateTime deactivationEnd) {
        this.registrationStart = registrationStart;
        this.registrationEnd = registrationEnd;
        this.registrars = registrars;
        this.entityTypes = entityTypes;
        this.withMissingCcnbOnly = withMissingCcnbOnly;
        this.withMissingIssnOnly = withMissingIssnOnly;
        this.withMMissingIsbnOnly = withMMissingIsbnOnly;
        this.returnActive = returnActive;
        this.returnDeactivated = returnDeactivated;
        this.deactivationStart = deactivationStart;
        this.deactivationEnd = deactivationEnd;
    }

    public DateTime getRegistrationStart() {
        return registrationStart;
    }

    public DateTime getRegistrationEnd() {
        return registrationEnd;
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

    public DateTime getDeactivationStart() {
        return deactivationStart;
    }

    public DateTime getDeactivationEnd() {
        return deactivationEnd;
    }

    public void setRegistrationStart(DateTime registrationStart) {
        this.registrationStart = registrationStart;
    }

    public void setRegistrationEnd(DateTime registrationEnd) {
        this.registrationEnd = registrationEnd;
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

    public void setDeactivationStart(DateTime deactivationStart) {
        this.deactivationStart = deactivationStart;
    }

    public void setDeactivationEnd(DateTime deactivationEnd) {
        this.deactivationEnd = deactivationEnd;
    }
}
