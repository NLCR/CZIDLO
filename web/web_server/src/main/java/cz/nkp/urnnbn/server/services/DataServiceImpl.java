package cz.nkp.urnnbn.server.services;

import cz.nkp.urnnbn.client.services.DataService;
import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.dto.RegistrarScopeIdentifier;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.server.dtoTransformation.*;
import cz.nkp.urnnbn.server.dtoTransformation.entities.DtotoIntelectualEntityTransformer;
import cz.nkp.urnnbn.shared.dto.*;
import cz.nkp.urnnbn.shared.dto.ie.IntelectualEntityDTO;
import cz.nkp.urnnbn.shared.exceptions.ServerException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataServiceImpl extends AbstractService implements DataService {

    private static final long serialVersionUID = 3849934849184219566L;
    private static final Logger logger = Logger.getLogger(DataServiceImpl.class.getName());

    @Override
    public void updateDigitalDocument(DigitalDocumentDTO doc, TechnicalMetadataDTO technical) throws ServerException {
        // TODO: mozna jeste server-side validace validatory
        // TODO: jinak by nekdo mohl podstrcit js volani se spatnymi parametry
        // TODO: a spadlo by to bud tady (ocekavane cislo)
        // TODO: nebo v transformeru nebo az na urovni databaze (moc dlouhy string)
        DigitalDocument transformed = new DtosToDigitalDocumentTransformer(doc, technical).transform();
        try {
            checkNotReadOnlyMode();
            updateService.updateDigitalDocument(transformed, getUserLogin());
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }
    }

    @Override
    public void updateIntelectualEntity(IntelectualEntityDTO entity) throws ServerException {
        DtotoIntelectualEntityTransformer transformer = new DtotoIntelectualEntityTransformer(entity);
        try {
            checkNotReadOnlyMode();
            checkUserIsAdmin();
            updateService.updateIntelectualEntity(transformer.getEntity(), transformer.getOriginator(), transformer.getPublication(),
                    transformer.getSrcDoc(), transformer.getIdentifiers(), getUserLogin());
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }
    }

    @Override
    public UrnNbnDTO saveRecord(IntelectualEntityDTO intEnt, DigitalDocumentDTO digDoc, UrnNbnDTO urnNbn,
                                ArrayList<RegistrarScopeIdDTO> registrarScopeIdentifiers) throws ServerException {
        try {

            checkNotReadOnlyMode();
            UrnNbn assigned = createService.registerDigitalDocument(
                    new RecordImportTransformer(intEnt, digDoc, urnNbn, registrarScopeIdentifiers).transform(), getUserLogin());
            return DtoTransformer.transformUrnNbn(new UrnNbnWithStatus(assigned, null, null));
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }
    }

    @Override
    public RegistrarScopeIdDTO addRegistrarScopeIdentifier(RegistrarScopeIdDTO rsId) throws ServerException {
        try {
            checkNotReadOnlyMode();
            RegistrarScopeIdentifier inserted = createService.addRegistrarScopeIdentifier(new DtoToRegistrarScopeIdTransformer(rsId).transform(), getUserLogin());
            return new RegistrarScopeIdDtoTransformer(inserted).transform();
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }
    }

    @Override
    public void removeRegistrarScopeIdentifier(RegistrarScopeIdDTO rsId) throws ServerException {
        try {
            checkNotReadOnlyMode();
            RegistrarScopeIdentifier transformed = new DtoToRegistrarScopeIdTransformer(rsId).transform();
            deleteService.removeRegistrarScopeIdentifier(transformed.getDigDocId(), transformed.getType(), getUserLogin());
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }
    }

    @Override
    public RegistrarScopeIdDTO updateRegistrarScopeIdentifier(RegistrarScopeIdDTO rsId) throws ServerException {
        try {
            checkNotReadOnlyMode();
            RegistrarScopeIdentifier updated = createService.updateRegistrarScopeIdentifier(new DtoToRegistrarScopeIdTransformer(rsId).transform(), getUserLogin());
            return new RegistrarScopeIdDtoTransformer(updated).transform();
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }
    }

    @Override
    public DigitalInstanceDTO saveDigitalInstance(UrnNbnDTO urn, DigitalInstanceDTO instance) throws ServerException {
        try {
            checkNotReadOnlyMode();
            DigitalInstance transformed = new DtoToDigitalInstanceTransformer(instance, urn).transform();
            DigitalInstance saved = createService.addDigitalInstance(transformed, getUserLogin());
            instance.setId(saved.getId());
            instance.setCreated(dateTimeToStringOrNull(saved.getCreated()));
            return instance;
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }
    }

    private String dateTimeToStringOrNull(DateTime dateTime) {
        if (dateTime != null) {
            DateTimeFormatter fmt = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm:ss");
            return dateTime.toString(fmt);
        } else {
            // System.err.println("dateTime is null");
            return null;
        }
    }

    @Override
    public void deactivateDigitalInstance(DigitalInstanceDTO instance) throws ServerException {
        try {
            checkNotReadOnlyMode();
            deleteService.deactivateDigitalInstance(instance.getId(), getUserLogin());
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }
    }

    @Override
    public void deactivateUrnNbn(UrnNbnDTO urnNbn) throws ServerException {
        try {
            checkNotReadOnlyMode();
            // TODO: poresit, uz urnNbn.getDeactivationNote() je vzdy null
            // System.err.println("first: " + urnNbn.getDeactivationNote());
            UrnNbn transformed = new DtoToUrnNbnTransformer(urnNbn).transform();
            // System.err.println("second: " + transformed.getDeactivationNote());
            deleteService.deactivateUrnNbn(transformed, getUserLogin(), urnNbn.getDeactivationNote());
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }
    }

    @Override
    public void updateDigitalInstance(UrnNbnDTO urnNbn, DigitalInstanceDTO instance) throws ServerException {
        try {
            checkNotReadOnlyMode();
            DigitalInstance transformed = new DtoToDigitalInstanceTransformer(instance, urnNbn).transform();
            updateService.updateDigitalInstance(transformed, getUserLogin());
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }
    }

}
