/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.impl;

import cz.nkp.urnnbn.core.dto.UrnNbn;

/**
 * Identifiers of imported intelectual entity and digital instance records are stored here
 * to be used in possible rollback.
 * @author Martin Řehánek
 */
public class RollbackRecord {

    private Long insertedIntEntId = null;
    private Long digRepId = null;
    private UrnNbn urnAssignedByResolverOrRegistrar = null;
    private UrnNbn urnFromBookedList = null;

    public Long getDigRepId() {
        return digRepId;
    }

    public void setDigRepId(Long digRepId) {
        this.digRepId = digRepId;
    }

    public UrnNbn getUrnAssignedByResolverOrRegistrar() {
        return urnAssignedByResolverOrRegistrar;
    }

    public void setUrnAssignedByResolverOrRegistrar(UrnNbn urnAssignedByResolverOrRegistrar) {
        this.urnAssignedByResolverOrRegistrar = urnAssignedByResolverOrRegistrar;
    }

    public UrnNbn getUrnFromReservedList() {
        return urnFromBookedList;
    }

    public void setUrnFromBookedList(UrnNbn urnFromBookedList) {
        this.urnFromBookedList = urnFromBookedList;
    }

    public Long getInsertedIntEntId() {
        return insertedIntEntId;
    }

    public void setInsertedIntEntId(Long insertedIntEntId) {
        this.insertedIntEntId = insertedIntEntId;
    }
}
