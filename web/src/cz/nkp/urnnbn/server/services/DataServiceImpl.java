package cz.nkp.urnnbn.server.services;

import cz.nkp.urnnbn.client.services.DataService;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.server.dtoTransformation.DtosToDigitalDocumentTransformer;
import cz.nkp.urnnbn.server.dtoTransformation.entities.DtotoIntelectualEntityTransformer;
import cz.nkp.urnnbn.shared.dto.DigitalDocumentDTO;
import cz.nkp.urnnbn.shared.dto.TechnicalMetadataDTO;
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
}
