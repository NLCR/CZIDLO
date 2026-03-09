package cz.nkp.urnnbn.services.exceptions;

public class ArchiverIsRegistrarException extends Exception {

    public ArchiverIsRegistrarException(Long archiverId, String registrarCode) {
        super("archiver with id '" + archiverId + "' is also a registrar with code '" + registrarCode + "'");
    }
}
