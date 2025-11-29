package cz.nkp.urnnbn.czidlo_web_api.api.documents.core;

import cz.nkp.urnnbn.czidlo_web_api.api.archivers.core.Archiver;
import cz.nkp.urnnbn.czidlo_web_api.api.registrars.core.Registrar;

import java.util.List;

public class Record {

    private Urn urnNbn;
    private Document digitalDocument;
    private Entity intelectualEntity;
    private Registrar registrar;
    private Archiver archiver;
    private List<RsId> registrarScopeIdentifiers;
    private List<DigInst> digitalInstances;

    public static Record from(Urn urn, Document document, Entity entity, Registrar registrar, Archiver archiver, List<RsId> registrarScopeIdentifiers, List<DigInst> digitalInstances) {
        Record record = new Record();
        record.urnNbn = urn;
        record.digitalDocument = document;
        record.intelectualEntity = entity;
        record.registrar = registrar;
        record.archiver = archiver;
        record.registrarScopeIdentifiers = registrarScopeIdentifiers;
        record.digitalInstances = digitalInstances;
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

    public List<DigInst> getDigitalInstances() {
        return digitalInstances;
    }
}
