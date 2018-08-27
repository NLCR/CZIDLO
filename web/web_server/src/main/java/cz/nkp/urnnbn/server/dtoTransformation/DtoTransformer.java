package cz.nkp.urnnbn.server.dtoTransformation;

import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.core.dto.*;
import cz.nkp.urnnbn.server.dtoTransformation.entities.EntityDtoTransformer;
import cz.nkp.urnnbn.shared.dto.*;
import cz.nkp.urnnbn.shared.dto.ie.IntelectualEntityDTO;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class DtoTransformer {

    public abstract Object transform();

    public static IntelectualEntityDTO transformIntelectualEntity(IntelectualEntity entity,
                                                                  List<IntEntIdentifier> enityIds,
                                                                  Publication publication,
                                                                  Originator originator,
                                                                  SourceDocument srcDoc,
                                                                  ArrayList<DigitalDocumentDTO> docs) {
        EntityDtoTransformer entityTransformer = EntityDtoTransformer.instanceOf(entity, enityIds, publication, originator, srcDoc, docs);
        return entityTransformer.transform();
    }

    public static DigitalDocumentDTO transformDigitalDocument(DigitalDocument doc,
                                                              UrnNbn urn,
                                                              Registrar registrar,
                                                              Archiver archiver,
                                                              ArrayList<DigitalInstanceDTO> digitalInstances,
                                                              List<RegistrarScopeIdentifier> registrarScopeIds) {
        return new DigialDocumentDtoTransformer(doc, urn, registrar, archiver, digitalInstances, registrarScopeIds).transform();
    }

    public Long datetTimeToMillisOrNull(DateTime dateTime) {
        if (dateTime != null) {
            return dateTime.getMillis();
        } else {
            return null;
        }
    }

    public Long dateToMillisOrNull(Date date) {
        if (date == null) {
            return null;
        } else {
            return date.getTime();
        }
    }

    public String dateTimeToStringOrNull(DateTime dateTime) {
        if (dateTime != null) {
            DateTimeFormatter fmt = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm:ss");
            return dateTime.toString(fmt);
        } else {
            return null;
        }
    }

    public static DigitalInstanceDTO transformDigitalInstance(DigitalInstance instance, DigitalLibrary library) {
        return new DigitalInstanceDtoTransformer(instance, library).transform();
    }

    public static RegistrarDTO transformRegistrar(Registrar registrar) {
        return new RegistrarDtoTransformer(registrar).transform();
    }

    public static ArchiverDTO transformArchiver(Archiver archiver) {
        return new ArchiverDtoTransformer(archiver).transform();
    }

    public static DigitalLibraryDTO transformDigitalLibrary(DigitalLibrary original) {
        return new DigitalLibraryDtoTransformer(original).transform();
    }

    public static CatalogDTO transformCatalog(Catalog catalog) {
        return new CatalogDtoTransformer(catalog).transform();
    }

    public static UrnNbnDTO transformUrnNbn(UrnNbnWithStatus urnNbn) {
        return new UrnNbnToDtoTransformer(urnNbn).transform();
    }
}
