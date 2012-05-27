package cz.nkp.urnnbn.server.dtoTransformation;

import java.util.ArrayList;
import java.util.Collections;

import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.dto.DigDocIdentifier;
import cz.nkp.urnnbn.server.dtoTransformation.entities.DtotoIntelectualEntityTransformer;
import cz.nkp.urnnbn.services.RecordImport;
import cz.nkp.urnnbn.shared.dto.DigitalDocumentDTO;
import cz.nkp.urnnbn.shared.dto.RegistrarScopeIdDTO;
import cz.nkp.urnnbn.shared.dto.UrnNbnDTO;
import cz.nkp.urnnbn.shared.dto.ie.IntelectualEntityDTO;

public class RecordImportTransformer {

	private final IntelectualEntityDTO intEnt;
	private final DigitalDocumentDTO digDoc;
	private final UrnNbnDTO urnNbn;
	private final ArrayList<RegistrarScopeIdDTO> registrarScopeIdentifiers;
	private final RecordImport result = new RecordImport();

	public RecordImportTransformer(IntelectualEntityDTO intEnt, DigitalDocumentDTO digDoc, UrnNbnDTO urnNbn,
			ArrayList<RegistrarScopeIdDTO> registrarScopeIdentifiers) {
		this.intEnt = intEnt;
		this.digDoc = digDoc;
		this.urnNbn = urnNbn;
		this.registrarScopeIdentifiers = registrarScopeIdentifiers;
	}

	public RecordImport transform() {
		transformEntity();
		transformDigDoc();
		transformUrn();
		result.setRegistrarCode(RegistrarCode.valueOf(digDoc.getRegistrar().getCode()));
		return result;
	}

	private void transformEntity() {
		DtotoIntelectualEntityTransformer transformer = new DtotoIntelectualEntityTransformer(intEnt);
		result.setEntity(transformer.getEntity());
		result.setIntEntIds(transformer.getIdentifiers());
		result.setOriginator(transformer.getOriginator());
		result.setPublication(transformer.getPublication());
		result.setSourceDoc(transformer.getSrcDoc());
	}

	private void transformDigDoc() {
		DtosToDigitalDocumentTransformer transformer = new DtosToDigitalDocumentTransformer(digDoc, digDoc.getTechnicalMetadata());
		result.setDigitalDocument(transformer.transform());
		// TODO: dig doc identifiers
		result.setDigDocIdentifiers(Collections.<DigDocIdentifier> emptyList());
	}

	private void transformUrn() {
		if (urnNbn != null) {
			result.setUrn(new DtoToUrnNbnTransformer(urnNbn).transform());
		}
	}
}
