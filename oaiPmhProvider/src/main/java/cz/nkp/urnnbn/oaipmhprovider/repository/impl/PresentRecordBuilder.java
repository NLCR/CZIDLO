/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaipmhprovider.repository.impl;

import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.RegistrarScopeIdentifier;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.oaipmhprovider.repository.DateStamp;
import cz.nkp.urnnbn.oaipmhprovider.repository.MetadataFormat;
import cz.nkp.urnnbn.oaipmhprovider.repository.Record;
import cz.nkp.urnnbn.services.Services;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.DocumentException;
import org.joda.time.DateTime;

/**
 *
 * @author Martin Řehánek
 */
public class PresentRecordBuilder {

    private final Services backend;
    private final UrnNbn urnNbn;
    private final MetadataFormat format;
    //
    private Registrar registrar;
    private DateStamp lastUpdated = DateStamp.MIN;

    public PresentRecordBuilder(Services backend, UrnNbn urnNbn, MetadataFormat format) {
        this.backend = backend;
        this.urnNbn = urnNbn;
        this.format = format;
    }

    Record build() {
        try {
            findRegistrarAndLastTimestamp(urnNbn.getDigDocId());
            return new PresentRecordImpl(registrar, urnNbn, lastUpdated, format, backend);
        } catch (DatabaseException ex) {
            Logger.getLogger(PresentRecordBuilder.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (DocumentException ex) {
            Logger.getLogger(PresentRecordBuilder.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private void findRegistrarAndLastTimestamp(Long digDocId) throws DatabaseException {
        DigitalDocument digDoc = backend.dataAccessService().digDocByInternalId(digDocId);
        this.registrar = backend.dataAccessService().registrarById(digDoc.getRegistrarId());
        updateLastDateStamp(digDoc.getModified());
        updateTimestampFromEntity(digDoc.getIntEntId());
        updateTimestampFromRegistrarScopeIds(digDocId);
        updateTimestampFromDigInstances(digDocId);
    }

    private void updateTimestampFromEntity(Long entityId) throws DatabaseException {
        IntelectualEntity intEnt = backend.dataAccessService().entityById(entityId);
        updateLastDateStamp(intEnt.getModified());
    }

    private void updateTimestampFromRegistrarScopeIds(Long digDocId) throws DatabaseException {
        List<RegistrarScopeIdentifier> identifiers = backend.dataAccessService().registrarScopeIdentifiers(digDocId);
        for (RegistrarScopeIdentifier id : identifiers) {
            updateLastDateStamp(id.getModified());
        }
    }

    private void updateTimestampFromDigInstances(Long digDocId) throws DatabaseException {
        List<DigitalInstance> digInstances = backend.dataAccessService().digInstancesByDigDocId(digDocId);
        for (DigitalInstance digInst : digInstances) {
            updateLastDateStamp(digInst.getDeactivated());
        }
    }

    private void updateLastDateStamp(DateTime modified) {
        if (modified != null) {
            DateStamp modifiedDs = new DateStamp(modified);
            if (modifiedDs.isAfter(lastUpdated)) {
                lastUpdated = modifiedDs;
            }
        }
    }
}
