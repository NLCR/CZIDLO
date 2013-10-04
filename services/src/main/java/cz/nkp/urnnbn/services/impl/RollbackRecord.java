/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.impl;

import cz.nkp.urnnbn.core.dto.UrnNbn;
import java.util.Collections;
import java.util.List;

/**
 * Identifiers of imported intelectual entity and digital instance records are
 * stored here to be used in possible rollback.
 *
 * @author Martin Řehánek
 */
public class RollbackRecord {

    private Long insertedIntEntId = null;
    private Long digDocId = null;
    private UrnNbn urnAssignedByResolverOrRegistrar = null;
    private UrnNbn urnFromReservedList = null;
    private List<UrnNbn> predecessorsDeactivated = null;

    public Long getDigDocId() {
        return digDocId;
    }

    public void setDigDocId(Long digDocId) {
        this.digDocId = digDocId;
    }

    public UrnNbn getUrnAssignedByResolverOrRegistrar() {
        return urnAssignedByResolverOrRegistrar;
    }

    public void setUrnAssignedByResolverOrRegistrar(UrnNbn urnAssignedByResolverOrRegistrar) {
        this.urnAssignedByResolverOrRegistrar = urnAssignedByResolverOrRegistrar;
    }

    public UrnNbn getUrnFromReservedList() {
        return urnFromReservedList;
    }

    public void setUrnFromReservedList(UrnNbn urnFromBookedList) {
        this.urnFromReservedList = urnFromBookedList;
    }

    public Long getInsertedIntEntId() {
        return insertedIntEntId;
    }

    public void setInsertedIntEntId(Long insertedIntEntId) {
        this.insertedIntEntId = insertedIntEntId;
    }

    public List<UrnNbn> getPredecessorsDeactivated() {
        return predecessorsDeactivated == null ? Collections.<UrnNbn>emptyList() : predecessorsDeactivated;
    }

    public void setPredecessorsDeactivated(List<UrnNbn> predecessorsDeactivated) {
        this.predecessorsDeactivated = predecessorsDeactivated;
    }
}
