/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services;

import cz.nkp.urnnbn.core.Sigla;
import cz.nkp.urnnbn.core.dto.DigRepIdentifier;
import cz.nkp.urnnbn.core.dto.DigitalRepresentation;
import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Originator;
import cz.nkp.urnnbn.core.dto.Publication;
import cz.nkp.urnnbn.core.dto.SourceDocument;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import java.util.List;

/**
 *
 * @author Martin Řehánek
 */
public class RecordImport {

    IntelectualEntity entity;
    List<IntEntIdentifier> intEntIds;
    Publication publication;
    Originator originator;
    SourceDocument sourceDoc;
    DigitalRepresentation representation;
    List<DigRepIdentifier> digRepIds;
    UrnNbn urn;
    Sigla registrarSigla;

    public List<DigRepIdentifier> getDigRepIds() {
        return digRepIds;
    }

    public void setDigRepIds(List<DigRepIdentifier> digRepIds) {
        this.digRepIds = digRepIds;
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

    public DigitalRepresentation getRepresentation() {
        return representation;
    }

    public void setRepresentation(DigitalRepresentation representation) {
        this.representation = representation;
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

    public Sigla getRegistrarSigla() {
        return registrarSigla;
    }

    public void setRegistrarSigla(Sigla registrarSigla) {
        this.registrarSigla = registrarSigla;
    }
}
