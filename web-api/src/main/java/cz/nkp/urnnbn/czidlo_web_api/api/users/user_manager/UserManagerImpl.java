package cz.nkp.urnnbn.czidlo_web_api.api.users.user_manager;

import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.AccessRightException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.BadArgumentException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.DuplicateRecordException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.UnknownRecordException;
import cz.nkp.urnnbn.czidlo_web_api.api.users.core.UserDetails;
import cz.nkp.urnnbn.services.*;

import java.util.List;

public class UserManagerImpl implements UserManager {

    protected DataAccessService dataAccessService() {
        return Services.instanceOf().dataAccessService();
    }

    protected DataImportService dataImportService() {
        return Services.instanceOf().dataImportService();
    }

    protected DataRemoveService dataRemoveService() {
        return Services.instanceOf().dataRemoveService();
    }

    protected DataUpdateService dataUpdateService() {
        return Services.instanceOf().dataUpdateService();
    }

    @Override
    public UserDetails createUser(Object userPerformingThisOperation, String login, String email, String password, boolean isAdmin) throws DuplicateRecordException, AccessRightException, BadArgumentException {
        return null;
    }

    @Override
    public UserDetails getUser(Object userPerformingThisOperation, long userId) throws UnknownRecordException, AccessRightException {
        return null;
    }

    @Override
    public List<UserDetails> getUsers(Object userPerformingThisOperation) throws AccessRightException {
        return List.of();
    }

    @Override
    public UserDetails updateUser(Object userPerformingThisOperation, long userId, String login, String email, boolean isAdmin) throws UnknownRecordException, DuplicateRecordException, AccessRightException, BadArgumentException {
        return null;
    }

    @Override
    public UserDetails updateUserPassword(Object userPerformingThisOperation, long userId, String newPassword) throws UnknownRecordException, AccessRightException, BadArgumentException {
        return null;
    }

    @Override
    public UserDetails addRegistrarRight(Object userPerformingThisOperation, long userId, String registrarCode) throws UnknownRecordException, AccessRightException {
        return null;
    }

    @Override
    public UserDetails removeRegistrarRight(Object userPerformingThisOperation, long userId, String registrarCode) throws UnknownRecordException, AccessRightException {
        return null;
    }

    @Override
    public List<String> getRegistrarRights(Object userPerformingThisOperation, long userId) throws UnknownRecordException, AccessRightException {
        return List.of();
    }

    @Override
    public void deleteUser(Object userPerformingThisOperation, long userId) throws UnknownRecordException, AccessRightException {

    }
}
