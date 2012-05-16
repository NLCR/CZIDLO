/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaipmhprovider.repository.impl;

import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.oaipmhprovider.repository.DateStamp;
import cz.nkp.urnnbn.oaipmhprovider.repository.Identifier;
import cz.nkp.urnnbn.oaipmhprovider.repository.MetadataFormat;
import cz.nkp.urnnbn.oaipmhprovider.repository.OaiSet;
import cz.nkp.urnnbn.oaipmhprovider.repository.Record;
import cz.nkp.urnnbn.oaipmhprovider.repository.Repository;
import cz.nkp.urnnbn.services.Services;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;

/**
 *
 * @author Martin Řehánek
 */
public class RepositoryImpl implements Repository {

    private Services backend;

    public static Repository instanceOf(Properties properties) {
        return new RepositoryImpl(properties);
    }

    private RepositoryImpl(Properties properties) {
        //TODO: vytahnout login, heslo, db z properties
        Services.init(true);
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
            return backend.dataAccessService().urnNbnsOfChangedRecordsOfRegistrar(fromDt, untilDt);
        } else {
            Registrar registrar = registrarFromSetSpec(setSpec);
            if (registrar == null) {
                return Collections.<UrnNbn>emptySet();
            } else {
                return backend.dataAccessService().urnNbnsOfChangedRecords(registrar, fromDt, untilDt);
            }
        }
    }

    private Registrar registrarFromSetSpec(String setSpec) {
        if (setSpec == null || !setSpec.startsWith(REGISTRAR_SET_PREFIX)) {
            return null;
        } else {
            String withoutPrefix = setSpec.substring(REGISTRAR_SET_PREFIX.length());
            RegistrarCode code = RegistrarCode.valueOf(withoutPrefix);
            try {
                return backend.dataAccessService().registrarByCode(code);
            } catch (DatabaseException ex) {
                Logger.getLogger(RepositoryImpl.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }
    }

    @Override
    public Record getRecord(Identifier id, MetadataFormat format, boolean validate) {
        try {
            UrnNbn urnNbn = UrnNbn.valueOf(id.toString());
            UrnNbnWithStatus fetechedUrn = backend.dataAccessService().urnByRegistrarCodeAndDocumentCode(urnNbn.getRegistrarCode(), urnNbn.getDocumentCode());
            switch (fetechedUrn.getStatus()) {
                case ABANDONED:
                    //TODO: potentially return another implementation subclass of Record like DeletedRecord
                    return null;
                case ACTIVE:
                    return new PresentRecordBuilder(backend, fetechedUrn.getUrn(), format).build();
                case FREE:
                    return null;
                case RESERVED:
                    return null;
                default:
                    return null;
            }
        } catch (DatabaseException ex) {
            Logger.getLogger(RepositoryImpl.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public Iterable<OaiSet> getSets() {
        try {
            List<Registrar> registrars = backend.dataAccessService().registrars();
            return toOaiSets(registrars);
        } catch (DatabaseException ex) {
            Logger.getLogger(RepositoryImpl.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private List<OaiSet> toOaiSets(List<Registrar> registrars) {
        List<OaiSet> result = new ArrayList<OaiSet>(registrars.size());
        for (Registrar registrar : registrars) {
            result.add(new OaiSet(registrar));
        }
        return result;
    }
}
