/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaipmhprovider.repository.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;

import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.impl.DatabaseConnectorFactory;
import cz.nkp.urnnbn.oaipmhprovider.repository.DateStamp;
import cz.nkp.urnnbn.oaipmhprovider.repository.Identifier;
import cz.nkp.urnnbn.oaipmhprovider.repository.MetadataFormat;
import cz.nkp.urnnbn.oaipmhprovider.repository.OaiSet;
import cz.nkp.urnnbn.oaipmhprovider.repository.Record;
import cz.nkp.urnnbn.oaipmhprovider.repository.Repository;
import cz.nkp.urnnbn.services.Services;

/**
 *
 * @author Martin Řehánek
 */
public class RepositoryImpl implements Repository {

    private Services backend;

    public static Repository instanceOf() {
        return new RepositoryImpl();
    }

    private RepositoryImpl() {
        Services.init(DatabaseConnectorFactory.getJndiPoolledConnector(), null);
        backend = Services.instanceOf();
    }

    @Override
    public Set<Record> getRecords(MetadataFormat format, DateStamp from, DateStamp until) {
        return getRecords(format, null, from, until);
    }

    @Override
    public Set<Record> getRecords(MetadataFormat format, String setSpec, DateStamp from, DateStamp until) {
        DateTime fromDt = from == null ? null : from.getDateTime();
        DateTime untilDt = until == null ? null : until.getDateTime();
        Set<UrnNbn> urnNbnSet = getUrnNbnSet(setSpec, fromDt, untilDt);
        Set<Record> result = new HashSet<Record>(urnNbnSet.size());
        for (UrnNbn urn : urnNbnSet) {
            result.add(new PresentRecordBuilder(backend, urn, format).build());
        }
        return result;
    }

    private Set<UrnNbn> getUrnNbnSet(String setSpec, DateTime fromDt, DateTime untilDt) {
        if (setSpec == null) {
            return backend.dataAccessService().urnNbnsOfChangedRecords(fromDt, untilDt);
        } else {
            Registrar registrar = registrarFromSetSpec(setSpec);
            if (registrar == null) {
                return Collections.<UrnNbn> emptySet();
            } else {
                return backend.dataAccessService().urnNbnsOfChangedRecordsOfRegistrar(registrar, fromDt, untilDt);
            }
        }
    }

    private Registrar registrarFromSetSpec(String setSpec) {
        if (setSpec == null || !setSpec.startsWith(REGISTRAR_SET_PREFIX)) {
            return null;
        } else {
            String withoutPrefix = setSpec.substring(REGISTRAR_SET_PREFIX.length());
            RegistrarCode code = RegistrarCode.valueOf(withoutPrefix);
            return backend.dataAccessService().registrarByCode(code);
        }
    }

    @Override
    public Record getRecord(Identifier id, MetadataFormat format, boolean validate) {
        UrnNbn urnNbn = UrnNbn.valueOf(id.toString());
        UrnNbnWithStatus fetechedUrn = backend.dataAccessService().urnByRegistrarCodeAndDocumentCode(urnNbn.getRegistrarCode(),
                urnNbn.getDocumentCode(), true);
        switch (fetechedUrn.getStatus()) {
        case DEACTIVATED:
            // TODO: before implementng deletedRecord will even deactivated dig docs available
            // TODO: potentially return another implementation subclass of Record like DeletedRecord
            // return null;
            return new PresentRecordBuilder(backend, fetechedUrn.getUrn(), format).build();
        case ACTIVE:
            return new PresentRecordBuilder(backend, fetechedUrn.getUrn(), format).build();
        case FREE:
            return null;
        case RESERVED:
            return null;
        default:
            return null;
        }
    }

    @Override
    public Iterable<OaiSet> getSets() {
        List<Registrar> registrars = backend.dataAccessService().registrars();
        return toOaiSets(registrars);
    }

    private List<OaiSet> toOaiSets(List<Registrar> registrars) {
        List<OaiSet> result = new ArrayList<OaiSet>(registrars.size());
        for (Registrar registrar : registrars) {
            result.add(new OaiSet(registrar));
        }
        return result;
    }
}
