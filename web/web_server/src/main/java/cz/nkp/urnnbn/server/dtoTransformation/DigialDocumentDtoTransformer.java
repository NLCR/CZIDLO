package cz.nkp.urnnbn.server.dtoTransformation;

import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.core.dto.*;
import cz.nkp.urnnbn.shared.dto.*;

import java.util.ArrayList;
import java.util.List;

public class DigialDocumentDtoTransformer extends DtoTransformer {

    private final DigitalDocument doc;
    private final UrnNbn urn;
    private final Registrar registrar;
    private final Archiver archiver;
    private final ArrayList<DigitalInstanceDTO> digitalInstances;
    private final List<RegistrarScopeIdentifier> registrarScopeIds;

    public DigialDocumentDtoTransformer(DigitalDocument doc, UrnNbn urn, Registrar registrar, Archiver archiver,
                                        ArrayList<DigitalInstanceDTO> digitalInstances, List<RegistrarScopeIdentifier> registrarScopeIds) {
        this.doc = doc;
        this.urn = urn;
        this.registrar = registrar;
        this.archiver = archiver;
        this.digitalInstances = digitalInstances;
        this.registrarScopeIds = registrarScopeIds;
    }

    public DigitalDocumentDTO transform() {
        DigitalDocumentDTO result = new DigitalDocumentDTO();
        result.setIntEntId(doc.getIntEntId());
        result.setId(doc.getId());
        result.setUrn(urnNbnTransformed());
        result.setRegistrar(registrarTransformed());
        result.setArchiver(archiverTransformed());
        if (result.getArchiver().getId() == result.getRegistrar().getId()) {
            result.setArchiver(null);
        }
        result.setInstances(digitalInstances);
        result.setRegistrarScopeIdList(transformRegistrarScopeIds(registrarScopeIds));
        result.setCreated(dateTimeToStringOrNull(doc.getCreated()));
        result.setModified(dateTimeToStringOrNull(doc.getModified()));
        result.setContractNumber(doc.getContractNumber());
        result.setFinanced(doc.getFinancedFrom());
        result.setTechnicalMetadata(technicalMetadataFromDoc());
        return result;
    }

    private ArrayList<RegistrarScopeIdDTO> transformRegistrarScopeIds(List<RegistrarScopeIdentifier> original) {
        ArrayList<RegistrarScopeIdDTO> result = new ArrayList<>(original.size());
        for (RegistrarScopeIdentifier id : original) {
            result.add(new RegistrarScopeIdDtoTransformer(id).transform());
        }
        return result;
    }

    private UrnNbnDTO urnNbnTransformed() {
        if (urn == null) {
            System.err.println("no urn:nbn for digital document" + doc.getId());
            return null;
        } else {
            // TODO: proverit
            return new UrnNbnToDtoTransformer(new UrnNbnWithStatus(urn, null, null)).transform();
        }
    }

    private RegistrarDTO registrarTransformed() {
        if (registrar != null) {
            return new RegistrarDtoTransformer(registrar).transform();
        } else {
            return null;
        }
    }

    private ArchiverDTO archiverTransformed() {
        if (archiver != null) {
            return new ArchiverDtoTransformer(archiver).transform();
        } else {
            return null;
        }
    }

    private TechnicalMetadataDTO technicalMetadataFromDoc() {
        TechnicalMetadataDTO result = new TechnicalMetadataDTO();
        result.setColorDepth(doc.getColorDepth());
        result.setColorModel(doc.getColorModel());
        result.setCompression(doc.getCompression());
        result.setCompressionRatio(doc.getCompressionRatio());
        result.setExtent(doc.getExtent());
        result.setFormat(doc.getFormat());
        result.setFormatVersion(doc.getFormatVersion());
        result.setIccProfile(doc.getIccProfile());
        result.setPictureHeight(doc.getPictureHeight());
        result.setPictureWidth(doc.getPictureWidth());
        result.setResolutionHorizontal(doc.getResolutionHorizontal());
        result.setResolutionVertical(doc.getResolutionVertical());
        return result;
    }

}
