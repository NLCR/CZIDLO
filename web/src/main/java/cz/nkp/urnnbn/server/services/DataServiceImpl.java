package cz.nkp.urnnbn.server.services;

import java.util.ArrayList;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import cz.nkp.urnnbn.client.services.DataService;
import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.server.dtoTransformation.DtoToDigitalInstanceTransformer;
import cz.nkp.urnnbn.server.dtoTransformation.DtoToUrnNbnTransformer;
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
	public void updateDigitalDocument(DigitalDocumentDTO doc,
			TechnicalMetadataDTO technical) throws ServerException {
		// TODO: mozna jeste server-side validace validatory
		// TODO: jinak by nekdo mohl podstrcit js volani se spatnymi parametry
		// TODO: a spadlo by to bud tady (ocekavane cislo)
		// TODO: nebo v transformeru nebo az na urovni databaze (moc dlouhy
		// string)
		DigitalDocument transformed = new DtosToDigitalDocumentTransformer(doc,
				technical).transform();
		try {
			updateService.updateDigitalDocument(transformed, getUserLogin());
		} catch (Throwable e) {
			e.printStackTrace();
			throw new ServerException(e.getMessage());
		}
	}

	@Override
	public void updateIntelectualEntity(IntelectualEntityDTO entity)
			throws ServerException {
		DtotoIntelectualEntityTransformer transformer = new DtotoIntelectualEntityTransformer(
				entity);
		try {
			updateService.updateIntelectualEntity(transformer.getEntity(),
					transformer.getOriginator(), transformer.getPublication(),
					transformer.getSrcDoc(), transformer.getIdentifiers(),
					getUserLogin());
		} catch (Throwable e) {
			e.printStackTrace();
			throw new ServerException(e.getMessage());
		}
	}

	@Override
	public UrnNbnDTO saveRecord(IntelectualEntityDTO intEnt,
			DigitalDocumentDTO digDoc, UrnNbnDTO urnNbn,
			ArrayList<RegistrarScopeIdDTO> registrarScopeIdentifiers)
			throws ServerException {
		try {
			if (urnNbn != null) {// will throw exception if urn is invalid
				UrnNbn.valueOf(urnNbn.toString());
			}
			UrnNbn assigned = createService.registerDigitalDocument(
					new RecordImportTransformer(intEnt, digDoc, urnNbn, registrarScopeIdentifiers).transform(), getUserLogin());
			return DtoTransformer.transformUrnNbn(new UrnNbnWithStatus(assigned, null, null));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new ServerException(e.getMessage());
		}
	}

	@Override
	public DigitalInstanceDTO saveDigitalInstance(DigitalInstanceDTO instance,
			UrnNbnDTO urn) throws ServerException {
		try {
			DigitalInstance transformed = new DtoToDigitalInstanceTransformer(
					instance, urn).transform();
			DigitalInstance saved = createService.addDigitalInstance(
					transformed, getUserLogin());
			instance.setId(saved.getId());
			instance.setCreated(dateTimeToStringOrNull(saved.getCreated()));
			return instance;
		} catch (Throwable e) {
			e.printStackTrace();
			throw new ServerException(e.getMessage());
		}
	}

	private String dateTimeToStringOrNull(DateTime dateTime) {
		if (dateTime != null) {
			DateTimeFormatter fmt = DateTimeFormat
					.forPattern("dd.MM.yyyy HH:mm:ss");
			return dateTime.toString(fmt);
		} else {
			// System.err.println("dateTime is null");
			return null;
		}
	}

	@Override
	public void deactivateDigitalInstance(DigitalInstanceDTO instance)
			throws ServerException {
		try {
			deleteService.deactivateDigitalInstance(instance.getId(),
					getUserLogin());
		} catch (Throwable e) {
			e.printStackTrace();
			throw new ServerException(e.getMessage());
		}
	}

	@Override
	public void deactivateUrnNbn(UrnNbnDTO urnNbn) throws ServerException {
		try {
			// TODO: poresit, uz urnNbn.getDeactivationNote() je vzdy null
			// System.err.println("first: " + urnNbn.getDeactivationNote());
			UrnNbn transformed = new DtoToUrnNbnTransformer(urnNbn).transform();
			// System.err.println("second: " +
			// transformed.getDeactivationNote());
			deleteService.deactivateUrnNbn(transformed, getUserLogin(),
					urnNbn.getDeactivationNote());
		} catch (Throwable e) {
			e.printStackTrace();
			throw new ServerException(e.getMessage());
		}
	}
}
