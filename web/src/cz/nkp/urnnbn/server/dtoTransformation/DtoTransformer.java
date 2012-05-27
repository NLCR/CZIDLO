package cz.nkp.urnnbn.server.dtoTransformation;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import cz.nkp.urnnbn.core.dto.Archiver;
import cz.nkp.urnnbn.core.dto.Catalog;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Originator;
import cz.nkp.urnnbn.core.dto.Publication;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.SourceDocument;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.server.dtoTransformation.entities.EntityDtoTransformer;
import cz.nkp.urnnbn.shared.dto.ArchiverDTO;
import cz.nkp.urnnbn.shared.dto.CatalogDTO;
import cz.nkp.urnnbn.shared.dto.DigitalDocumentDTO;
import cz.nkp.urnnbn.shared.dto.DigitalInstanceDTO;
import cz.nkp.urnnbn.shared.dto.DigitalLibraryDTO;
import cz.nkp.urnnbn.shared.dto.RegistrarDTO;
import cz.nkp.urnnbn.shared.dto.UrnNbnDTO;
import cz.nkp.urnnbn.shared.dto.ie.IntelectualEntityDTO;

public abstract class DtoTransformer {

	public abstract Object transform();

	public static IntelectualEntityDTO transformIntelectualEntity(IntelectualEntity entity, List<IntEntIdentifier> enityIds,
			Publication publication, Originator originator, SourceDocument srcDoc, ArrayList<DigitalDocumentDTO> docs) {
		EntityDtoTransformer entityTransformer = EntityDtoTransformer.instanceOf(entity, enityIds, publication, originator, srcDoc, docs);
		return entityTransformer.transform();
	}

	public static DigitalDocumentDTO transformDigitalDocument(DigitalDocument doc, UrnNbn urn, Registrar registrar, Archiver archiver,
			ArrayList<DigitalInstanceDTO> digitalInstances) {
		return new DigialDocumentDtoTransformer(doc, urn, registrar, archiver, digitalInstances).transform();
	}

	public String dateTimeToStringOrNull(DateTime dateTime) {
		if (dateTime != null) {
			DateTimeFormatter fmt = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm:ss");
			return dateTime.toString(fmt);
		} else {
			// System.err.println("dateTime is null");
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

	public static UrnNbnDTO transformUrnNbn(UrnNbn urnNbn) {
		return new UrnNbnToDtoTransformer(urnNbn).transform();
	}
}
