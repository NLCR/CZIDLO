/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaipmhprovider.repository.impl;

import cz.nkp.urnnbn.core.dto.Archiver;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Originator;
import cz.nkp.urnnbn.core.dto.Publication;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.RegistrarScopeIdentifier;
import cz.nkp.urnnbn.core.dto.SourceDocument;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.oaipmhprovider.conf.OaiPmhConfiguration;
import cz.nkp.urnnbn.oaipmhprovider.repository.DateStamp;
import cz.nkp.urnnbn.oaipmhprovider.repository.Identifier;
import cz.nkp.urnnbn.oaipmhprovider.repository.MetadataFormat;
import cz.nkp.urnnbn.oaipmhprovider.repository.OaiSet;
import cz.nkp.urnnbn.oaipmhprovider.repository.PresentRecord;
import cz.nkp.urnnbn.oaipmhprovider.tools.dom4j.Dom4jUtils;
import cz.nkp.urnnbn.services.DataAccessService;
import cz.nkp.urnnbn.services.Services;
import cz.nkp.urnnbn.xml.builders.ArchiverBuilder;
import cz.nkp.urnnbn.xml.builders.DigitalDocumentBuilder;
import cz.nkp.urnnbn.xml.builders.DigitalInstanceBuilder;
import cz.nkp.urnnbn.xml.builders.DigitalInstancesBuilder;
import cz.nkp.urnnbn.xml.builders.DigitalLibraryBuilder;
import cz.nkp.urnnbn.xml.builders.IntelectualEntityBuilder;
import cz.nkp.urnnbn.xml.builders.RegistrarBuilder;
import cz.nkp.urnnbn.xml.builders.RegistrarScopeIdentifierBuilder;
import cz.nkp.urnnbn.xml.builders.RegistrarScopeIdentifiersBuilder;
import cz.nkp.urnnbn.xml.commons.XOMUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;

/**
 *
 * @author Martin Řehánek
 */
public class PresentRecordImpl implements PresentRecord {

    private final DataAccessService backend;
    private final Registrar registrar;
    private final UrnNbn urnNbn;
    private final DateStamp lastUpdated;
    private final MetadataFormat format;
    private Document content = null;

    public PresentRecordImpl(Registrar registrar, UrnNbn urnNbn, DateStamp lastUpdated, MetadataFormat format, Services backend) throws DocumentException {
        this.registrar = registrar;
        this.urnNbn = urnNbn;
        this.lastUpdated = lastUpdated;
        this.format = format;
        this.backend = backend.dataAccessService();
    }

    @Override
    public Identifier getId() {
        return new Identifier(urnNbn);
    }

    @Override
    public boolean isDeleted() {
        return false;
    }

    @Override
    public DateStamp getDateStamp() {
        return lastUpdated;
    }

    @Override
    public Set<OaiSet> getOaiSets() {
        Set<OaiSet> result = new HashSet<OaiSet>(1);
        result.add(new OaiSet(registrar));
        return result;
    }

    @Override
    public MetadataFormat getMetadataFormat() {
        return format;
    }

    @Override
    public Document getMetadata() {
        if (content == null) {
            content = buildContent();
        }
        return content;
    }

    private Document buildContent() {
        try {
            DigitalDocumentBuilder digDocBuilder = digDocBuilder(urnNbn.getDigDocId());
            String metadataInResolverFormat = digDocBuilder.buildDocumentWithoutResponseHeader().toXML();
            switch (format) {
                case CZIDLO:
                    return Dom4jUtils.loadDocument(metadataInResolverFormat, false);
                case OAI_DC:
                    return transformToOaiDc(metadataInResolverFormat);
                default:
                    return null;
            }
        } catch (DocumentException ex) {
            Logger.getLogger(PresentRecordBuilder.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (DatabaseException ex) {
            Logger.getLogger(RepositoryImpl.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private DigitalDocumentBuilder digDocBuilder(Long digDocId) throws DatabaseException {
        DigitalDocument digDoc = backend.digDocByInternalId(digDocId);
        IntelectualEntityBuilder intEntBuilder = intEntBuilder(digDoc.getIntEntId());
        RegistrarScopeIdentifiersBuilder registrarScopeIdsBuilder = registrarScopeIdsBuilder(digDocId);
        DigitalInstancesBuilder digitalInstancesBuilder = digitalInstancesBuilder(digDocId);
        RegistrarBuilder registrarBuilder = new RegistrarBuilder(registrar, null, null);
        ArchiverBuilder archiverBuilder = archiverBuilder(digDoc.getArchiverId());
        return new DigitalDocumentBuilder(digDoc, urnNbn, registrarScopeIdsBuilder, digitalInstancesBuilder, registrarBuilder, archiverBuilder, intEntBuilder);
    }

    private IntelectualEntityBuilder intEntBuilder(Long entityId) throws DatabaseException {
        IntelectualEntity intEnt = backend.entityById(entityId);
        List<IntEntIdentifier> intEntIdentifiers = backend.intEntIdentifiersByIntEntId(intEnt.getId());
        Publication publication = backend.publicationByIntEntId(intEnt.getId());
        Originator originator = backend.originatorByIntEntId(intEnt.getId());
        SourceDocument srcDoc = backend.sourceDocumentByIntEntId(intEnt.getId());
        return IntelectualEntityBuilder.instanceOf(intEnt, intEntIdentifiers, publication, originator, srcDoc);
    }

    private RegistrarScopeIdentifiersBuilder registrarScopeIdsBuilder(Long digDocId) throws DatabaseException {
        List<RegistrarScopeIdentifier> identifiers = backend.registrarScopeIdentifiers(digDocId);
        List<RegistrarScopeIdentifierBuilder> idBuilderList = new ArrayList<RegistrarScopeIdentifierBuilder>(identifiers.size());
        for (RegistrarScopeIdentifier id : identifiers) {
            idBuilderList.add(new RegistrarScopeIdentifierBuilder(id));
        }
        return new RegistrarScopeIdentifiersBuilder(idBuilderList);
    }

    private DigitalInstancesBuilder digitalInstancesBuilder(Long digDocId) throws DatabaseException {
        List<DigitalInstance> digInstances = backend.digInstancesByDigDocId(digDocId);
        List<DigitalInstanceBuilder> digInstBuilders = new ArrayList<DigitalInstanceBuilder>(digInstances.size());
        for (DigitalInstance digInst : digInstances) {
            digInstBuilders.add(new DigitalInstanceBuilder(digInst, libraryBuilder(digInst.getLibraryId()), null));
        }
        return new DigitalInstancesBuilder(digInstBuilders);
    }

    private DigitalLibraryBuilder libraryBuilder(Long libraryId) throws DatabaseException {
        DigitalLibrary library = backend.libraryByInternalId(libraryId);
        return new DigitalLibraryBuilder(library, null);
    }

    private ArchiverBuilder archiverBuilder(Long archiverId) throws DatabaseException {
        Archiver archiver = backend.archiverById(archiverId);
        return new ArchiverBuilder(archiver);
    }

    private Document transformToOaiDc(String metadataInResolverFormat) throws DocumentException {
        try {
            nu.xom.Document original = XOMUtils.loadDocumentWithoutValidation(metadataInResolverFormat);
            String transformed = OaiPmhConfiguration.instanceOf().getCzidloToOaidcTransformer().transform(original).toXML();
            return Dom4jUtils.loadDocument(transformed, false);
        } catch (DocumentException ex) {
            throw ex;
        } catch (Exception ex) {
            Logger.getLogger(PresentRecordImpl.class.getName()).log(Level.SEVERE, null, ex);
            throw new DocumentException(ex);
        }
    }
}
