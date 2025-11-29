package cz.nkp.urnnbn.czidlo_web_api.api.documents.core;

import cz.nkp.urnnbn.czidlo_web_api.api.archivers.core.Archiver;
import cz.nkp.urnnbn.czidlo_web_api.api.registrars.core.Registrar;

import java.util.List;

public class Record {

    //TODO: digital instance, digital library

    private Urn urnNbn;
    private Document digitalDocument;
    private Entity intelectualEntity;
    private Registrar registrar;
    private Archiver archiver;
    private List<RsId> registrarScopeIdentifiers;

    public static Record from(Urn urn, Document document, Entity entity, Registrar registrar, Archiver archiver, List<RsId> registrarScopeIdentifiers) {
        Record record = new Record();
        record.urnNbn = urn;
        record.digitalDocument = document;
        record.intelectualEntity = entity;
        record.registrar = registrar;
        record.archiver = archiver;
        record.registrarScopeIdentifiers = registrarScopeIdentifiers;
        return record;
    }

    public Urn getUrnNbn() {
        return urnNbn;
    }

    public Document getDigitalDocument() {
        return digitalDocument;
    }

    public Entity getIntelectualEntity() {
        return intelectualEntity;
    }

    public Registrar getRegistrar() {
        return registrar;
    }

    public Archiver getArchiver() {
        return archiver;
    }

    public List<RsId> getRegistrarScopeIdentifiers() {
        return registrarScopeIdentifiers;
    }
}
