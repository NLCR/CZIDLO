package cz.nkp.urnnbn.server.services;

import java.util.ArrayList;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import cz.nkp.urnnbn.client.services.DataService;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.server.dtoTransformation.DtoToDigitalInstanceTransformer;
import cz.nkp.urnnbn.server.dtoTransformation.DtoTransformer;
import cz.nkp.urnnbn.server.dtoTransformation.DtosToDigitalDocumentTransformer;
import cz.nkp.urnnbn.server.dtoTransformation.RecordImportTransformer;
import cz.nkp.urnnbn.server.dtoTransformation.entities.DtotoIntelectualEntityTransformer;
import cz.nkp.urnnbn.shared.dto.DigitalDocumentDTO;
import cz.nkp.urnnbn.shared.dto.DigitalInstanceDTO;
import cz.nkp.urnnbn.shared.dto.RegistrarScopeIdDTO;
import cz.nkp.urnnbn.shared.dto.TechnicalMetadataDTO;
import cz.nkp.urnnbn.shared.dto.UrnNbnDTO;
import cz.nkp.urnnbn.shared.dto.ie.IntelectualEntityDTO;
import cz.nkp.urnnbn.shared.exceptions.ServerException;

public class DataServiceImpl extends AbstractService implements DataService {

	private static final long serialVersionUID = 3849934849184219566L;

	@Override
	public void updateDigitalDocument(DigitalDocumentDTO doc, TechnicalMetadataDTO technical) throws ServerException {
		// TODO: mozna jeste server-side validace validatory
		// TODO: jinak by nekdo mohl podstrcit js volani se spatnymi parametry
		// TODO: a spadlo by to bud tady (ocekavane cislo)
		// TODO: nebo v transformeru nebo az na urovni databaze (moc dlouhy
		// string)
		DigitalDocument transformed = new DtosToDigitalDocumentTransformer(doc, technical).transform();
		try {
			updateService.updateDigitalDocument(transformed, getUserLogin());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServerException(e.getMessage());
		}
	}

	@Override
	public void updateIntelectualEntity(IntelectualEntityDTO entity) throws ServerException {
		DtotoIntelectualEntityTransformer transformer = new DtotoIntelectualEntityTransformer(entity);
		try {
			updateService.updateIntelectualEntity(transformer.getEntity(), transformer.getOriginator(), transformer.getPublication(),
					transformer.getSrcDoc(), transformer.getIdentifiers(), getUserLogin());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServerException(e.getMessage());
		}
	}

	@Override
	public UrnNbnDTO saveRecord(IntelectualEntityDTO intEnt, DigitalDocumentDTO digDoc, UrnNbnDTO urnNbn,
			ArrayList<RegistrarScopeIdDTO> registrarScopeIdentifiers) throws ServerException {
		try {
			UrnNbn assigned = createService.importNewRecord(
					new RecordImportTransformer(intEnt, digDoc, urnNbn, registrarScopeIdentifiers).transform(), getUserLogin());
			return DtoTransformer.transformUrnNbn(assigned);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServerException(e.getMessage());
		}
	}

	@Override
	public DigitalInstanceDTO saveDigitalInstance(DigitalInstanceDTO instance, UrnNbnDTO urn) throws ServerException {
		try {
			DigitalInstance transformed = new DtoToDigitalInstanceTransformer(instance, urn).transform();
			DigitalInstance saved = createService.addDigitalInstance(transformed, getUserLogin());
			instance.setId(saved.getId());
			instance.setCreated(dateTimeToStringOrNull(saved.getCreated()));
			return instance;
		} catch (Exception e) {
			e.printStackTrace();
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
	public void deleteDigitalInstance(DigitalInstanceDTO instance) throws ServerException {
		try {
			deleteService.removeDigitalInstance(instance.getId(), getUserLogin());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServerException(e.getMessage());
		}
	}
}
