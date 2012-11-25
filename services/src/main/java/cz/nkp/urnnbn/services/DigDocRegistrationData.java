/*  
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services;

import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.core.dto.DigDocIdentifier;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Originator;
import cz.nkp.urnnbn.core.dto.Publication;
import cz.nkp.urnnbn.core.dto.SourceDocument;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Martin Řehánek
 */
public class DigDocRegistrationData {

    IntelectualEntity entity;
    List<IntEntIdentifier> intEntIds;
    Publication publication;
    Originator originator;
    SourceDocument sourceDoc;
    DigitalDocument digitalDocument;
    List<DigDocIdentifier> digDocIdentifiers;
    List<UrnNbnWithStatus> predecessors;
    UrnNbn urn;
    RegistrarCode registrarCode;

    public List<DigDocIdentifier> getDigDogIdentifiers() {
        return digDocIdentifiers;
    }

    public void setDigDocIdentifiers(List<DigDocIdentifier> digDocIdentifiers) {
        this.digDocIdentifiers = digDocIdentifiers;
    }

    public IntelectualEntity getEntity() {
        return entity;
    }

    public void setEntity(IntelectualEntity entity) {
        this.entity = entity;
    }

    public List<IntEntIdentifier> getIntEntIds() {
        return intEntIds;
    }

    public void setIntEntIds(List<IntEntIdentifier> intEntIds) {
        this.intEntIds = intEntIds;
    }

    public Originator getOriginator() {
        return originator;
    }

    public void setOriginator(Originator originator) {
        this.originator = originator;
    }

    public Publication getPublication() {
        return publication;
    }

    public void setPublication(Publication publication) {
        this.publication = publication;
    }

    public DigitalDocument getDigitalDocument() {
        return digitalDocument;
    }

    public void setDigitalDocument(DigitalDocument digitalDocument) {
        this.digitalDocument = digitalDocument;
    }

    public SourceDocument getSourceDoc() {
        return sourceDoc;
    }

    public void setSourceDoc(SourceDocument sourceDoc) {
        this.sourceDoc = sourceDoc;
    }

    public UrnNbn getUrn() {
        return urn;
    }

    public void setUrn(UrnNbn urn) {
        this.urn = urn;
    }

    public RegistrarCode getRegistrarCode() {
        return registrarCode;
    }

    public void setRegistrarCode(RegistrarCode registrarCode) {
        this.registrarCode = registrarCode;
    }

    public List<UrnNbnWithStatus> getPredecessors() {
        return predecessors == null ? Collections.<UrnNbnWithStatus>emptyList() : predecessors;
    }

    public void setPredecessors(List<UrnNbnWithStatus> predecessors) {
        this.predecessors = predecessors;
    }
}
