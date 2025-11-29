package cz.nkp.urnnbn.czidlo_web_api.api.documents.core;

import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.czidlo_web_api.api.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//@XmlRootElement(name = "urnNbn")
//@XmlAccessorType(XmlAccessType.FIELD)
public class Urn {

    private String registrarCode;
    private String documentCode;
    private Date reserved;
    private Date registered;
    private Date deactivated;
    private String deactivationNote;
    private Boolean active;
    //
    private String status;
    private String statusNote;
    //
    private List<Urn> predecessors;
    private List<Urn> successors;

    public static Urn from(UrnNbnWithStatus dtoUrnWithStatus) {
        Urn result = new Urn();
        if (dtoUrnWithStatus == null) {
            return null;
        }
        if (dtoUrnWithStatus.getUrn() != null) {
            result.registrarCode = dtoUrnWithStatus.getUrn().getRegistrarCode().toString();
            result.documentCode = dtoUrnWithStatus.getUrn().getDocumentCode();
            result.reserved = Utils.dateTimeToDate(dtoUrnWithStatus.getUrn().getReserved());
            result.registered = Utils.dateTimeToDate(dtoUrnWithStatus.getUrn().getRegistered());
            result.deactivated = Utils.dateTimeToDate(dtoUrnWithStatus.getUrn().getDeactivated());
            result.deactivationNote = dtoUrnWithStatus.getUrn().getDeactivationNote();
            result.active = dtoUrnWithStatus.getUrn().isActive();
            List<UrnNbnWithStatus> dtoPredecessors = dtoUrnWithStatus.getUrn().getPredecessors();
            if (dtoPredecessors != null) {
                result.predecessors = new ArrayList<>();
                for (UrnNbnWithStatus dtoPredecessor : dtoPredecessors) {
                    result.predecessors.add(from(dtoPredecessor));
                }
            }
            List<UrnNbnWithStatus> dtoSuccessors = dtoUrnWithStatus.getUrn().getSuccessors();
            if (dtoSuccessors != null) {
                result.successors = new ArrayList<>();
                for (UrnNbnWithStatus dtoSuccessor : dtoSuccessors) {
                    result.successors.add(from(dtoSuccessor));
                }
            }
        }
        result.status = dtoUrnWithStatus.getStatus().toString();
        result.statusNote = dtoUrnWithStatus.getNote();
        return result;
    }

    public String getRegistrarCode() {
        return registrarCode;
    }

    public String getDocumentCode() {
        return documentCode;
    }

    public Date getReserved() {
        return reserved;
    }

    public Date getRegistered() {
        return registered;
    }

    public Date getDeactivated() {
        return deactivated;
    }

    public String getDeactivationNote() {
        return deactivationNote;
    }

    public Boolean getActive() {
        return active;
    }

    public String getStatus() {
        return status;
    }

    public String getStatusNote() {
        return statusNote;
    }

    public List<Urn> getPredecessors() {
        return predecessors;
    }

    public List<Urn> getSuccessors() {
        return successors;
    }
}
