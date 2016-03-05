package cz.nkp.urnnbn.api.v4;

import java.util.ArrayList;
import java.util.List;

import cz.nkp.urnnbn.api.v4.exceptions.UnknownRegistrarException;
import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.xml.apiv4.builders.RegistrarBuilder;
import cz.nkp.urnnbn.xml.apiv4.builders.RegistrarsBuilder;

public class AbstractRegistrarsResource extends V4Resource {

    public String getRegistrarsApiV4XmlRecord(boolean addDigitalLibraries, boolean addCatalogs) throws DatabaseException {
        List<RegistrarBuilder> registrarBuilders = registrarBuilderList(addDigitalLibraries, addCatalogs);
        RegistrarsBuilder builder = new RegistrarsBuilder(registrarBuilders);
        return builder.buildDocumentWithResponseHeader().toXML();
    }

    private List<RegistrarBuilder> registrarBuilderList(boolean addDigitalLibraries, boolean addCatalogs) throws DatabaseException {
        List<Registrar> registrars = dataAccessService().registrars();
        List<RegistrarBuilder> result = new ArrayList<RegistrarBuilder>(registrars.size());
        for (Registrar registrar : registrars) {
            result.add(registrarBuilder(registrar, addDigitalLibraries, addCatalogs));
        }
        return result;
    }

    protected Registrar registrarFromRegistarCode(String registrarCodeStr) {
        RegistrarCode registrarCode = Parser.parseRegistrarCode(registrarCodeStr);
        Registrar registrar = dataAccessService().registrarByCode(registrarCode);
        if (registrar == null) {
            throw new UnknownRegistrarException(registrarCode);
        } else {
            return registrar;
        }
    }

}
