package cz.nkp.urnnbn.czidlo_web_api.api.users.user_manager;

import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.*;
import cz.nkp.urnnbn.czidlo_web_api.api.registrars.core.Registrar;
import cz.nkp.urnnbn.czidlo_web_api.api.registrars.registrar_manager.RegistrarInMemoryRepo;
import cz.nkp.urnnbn.czidlo_web_api.api.users.core.UserDetails;

import java.util.List;

public class UserManagerMockInMemory implements UserManager {

    private static final UserInMemoryRepo userRepo = UserInMemoryRepo.getInstance();
    private static final RegistrarInMemoryRepo registrarRepo = RegistrarInMemoryRepo.getInstance();

    public UserManagerMockInMemory() {
    }

    @Override
    public UserDetails createUser(String userPerformingThisOperation, String login, String email, String password, boolean isAdmin)
            throws DuplicateRecordException, BadArgumentException, AccessRightException {
        //check access rights
        if (false) { //TODO: check that userPerformingThisOperation is admin or the user with userId
            throw new AccessRightException("TODO: no access right");
        }
        //check login validity and uniqueness
        checkLogin(login);
        if (userRepo.getByLogin(login) != null) {
            throw new DuplicateRecordException("User with login \"" + login + "\" already exists");
        }
        //check email validity and uniqueness
        checkEmail(email);
        if (userRepo.getByEmail(email) != null) {
            throw new DuplicateRecordException("User with email \"" + email + "\" already exists");
        }
        //check password validity
        checkPassword(password);
        //create user
        return userRepo.create(login, email, UserInMemoryRepo.hashPassword(password), isAdmin);
    }

    @Override
    public UserDetails getUser(String userPerformingThisOperation, long userId)
            throws UnknownRecordException, AccessRightException {
        //check access rights
        if (false) { //TODO: check that userPerformingThisOperation is admin or the user with userId
            throw new AccessRightException("TODO: no access right");
        }
        //fetch user
        UserDetails user = userRepo.getById(userId);
        //check that user exists
        if (user == null) {
            throw new UnknownRecordException("Unknown user with ID: " + userId);
        }
        //return user
        return user;
    }

    @Override
    public UserDetails getUser(String userPerformingThisOperation, String login) throws UnknownRecordException, AccessRightException {
        throw new RuntimeException("UserManagerMockInMemory: Not implemented yet");
    }

    @Override
    public List<UserDetails> getUsers(String userPerformingThisOperation) throws AccessRightException {
        //check access rights
        if (false) { //TODO: check that userPerformingThisOperation is admin
            throw new AccessRightException("TODO: no access right");
        }
        //return all users
        return userRepo.getAll();
    }

    @Override
    public UserDetails updateUser(String userPerformingThisOperation, long userId, String login, String email, boolean isAdmin)
            throws UnknownRecordException, DuplicateRecordException, BadArgumentException, AccessRightException {
        //check access rights
        if (false) { //TODO: check that userPerformingThisOperation is admin
            throw new AccessRightException("TODO: no access right");
        }
        //check that user exists
        UserDetails userById = userRepo.getById(userId);
        if (userById == null) {
            throw new UnknownRecordException("Unknown user with ID: " + userId);
        }
        //check login validity and uniqueness
        checkLogin(login);
        UserDetails userByLogin = userRepo.getByLogin(login);
        if (userByLogin != null && !userById.getId().equals(userByLogin.getId())) {
            throw new DuplicateRecordException("Different user with login \"" + login + "\" already exists");
        }
        //check email validity and uniqueness
        checkEmail(email);
        UserDetails userByEmail = userRepo.getByEmail(email);
        if (userByEmail != null && !userById.getId().equals(userByEmail.getId())) {
            throw new DuplicateRecordException("Different user with email \"" + email + "\" already exists");
        }
        return userRepo.update(userId, login, email, null, isAdmin);
    }

    @Override
    public UserDetails updateUserPassword(String userPerformingThisOperation, long userId, String newPassword)
            throws UnknownRecordException, AccessRightException, BadArgumentException {
        //check access rights
        if (false) { //TODO: check that userPerformingThisOperation is admin or the user with userId
            throw new AccessRightException("TODO: no access right");
        }
        //check password validity
        checkPassword(newPassword);
        //check that user exists
        UserDetails userFromDb = userRepo.getById(userId);
        if (userFromDb == null) {
            throw new UnknownRecordException("Unknown user with ID: " + userId);
        }
        //update password
        return userRepo.update(userFromDb.getId(), userFromDb.getLogin(), userFromDb.getEmail(), UserInMemoryRepo.hashPassword(newPassword), userFromDb.isAdmin());
    }

    @Override
    public UserDetails addRegistrarRight(String userPerformingThisOperation, long userId, String registrarCode) throws UnknownRecordException, AccessRightException {
        //check access rights
        if (false) { //TODO: check that userPerformingThisOperation is admin
            throw new AccessRightException("TODO: no access right");
        }

        //check that user exists
        UserDetails userFromDb = userRepo.getById(userId);
        if (userFromDb == null) {
            throw new UnknownRecordException("Unknown user with ID: " + userId);
        }

        //check if registrar code is a valid code
        Registrar registrar = registrarRepo.getByCode(registrarCode);

        if (registrar == null) {
            throw new UnknownRecordException("Unknown registrar: " + registrarCode);
        }

        if (userFromDb.getRegistrarRights().contains(registrarCode)) {
            return null;
        }

        userFromDb.addRegistrarRight(registrarCode);

        return userFromDb;
    }

    @Override
    public UserDetails removeRegistrarRight(String userPerformingThisOperation, long userId, String registrarCode) throws UnknownRecordException, AccessRightException {
        //check access rights
        if (false) { //TODO: check that userPerformingThisOperation is admin
            throw new AccessRightException("TODO: no access right");
        }

        //check that user exists
        UserDetails userFromDb = userRepo.getById(userId);
        if (userFromDb == null) {
            throw new UnknownRecordException("Unknown user with ID: " + userId);
        }

        //check if registrar code is a valid code
        Registrar registrar = registrarRepo.getByCode(registrarCode);

        if (registrar == null) {
            throw new UnknownRecordException("Unknown registrar: " + registrarCode);
        }

        if (!userFromDb.getRegistrarRights().contains(registrarCode)) {
            throw new UnknownRecordException("User does not contain registrar with code: " + registrarCode);
        }

        userFromDb.removeRegistrarRight(registrarCode);

        return userFromDb;
    }

    @Override
    public List<String> getRegistrarRights(String userPerformingThisOperation, long userId) throws AccessRightException, UnknownRecordException {
        //check access rights
        if (false) { //TODO: check that userPerformingThisOperation is admin
            throw new AccessRightException("TODO: no access right");
        }

        //check that user exists
        UserDetails userFromDb = userRepo.getById(userId);
        if (userFromDb == null) {
            throw new UnknownRecordException("Unknown user with ID: " + userId);
        }

        return userFromDb.getRegistrarRights();
    }

    @Override
    public void deleteUser(String userPerformingThisOperation, long userId)
            throws UnknownRecordException, AccessRightException {
        //check access rights
        if (false) { //TODO: check that userPerformingThisOperation is admin
            throw new AccessRightException("TODO: no access right");
        }
        //check that user exists
        UserDetails userFromDb = userRepo.getById(userId);
        if (userFromDb == null) {
            throw new UnknownRecordException("Unknown user with ID: " + userId);
        }
        //delete user
        userRepo.delete(userId);
    }

    private void checkLogin(String login) throws BadArgumentException {
        if (login == null || login.isEmpty()) {
            throw new BadArgumentException("Login cannot be empty");
        }
        if (login.length() < 5) {
            throw new BadArgumentException("Login must be at least 5 characters long");
        }
        if (login.length() > 15) {
            throw new BadArgumentException("Login cannot be longer than 15 characters");
        }
        if (!login.matches("^[A-Za-z0-9_-]+$")) {
            throw new BadArgumentException("Login contains invalid characters; only letters, digits and characters '_','-' are allowed");
        }
    }

    private void checkEmail(String email) throws BadArgumentException {
        if (email == null || email.isEmpty()) {
            throw new BadArgumentException("Email cannot be empty");
        }
        if (email.length() > 50) {
            throw new BadArgumentException("Email cannot be longer than 50 characters");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new BadArgumentException("Email is not valid");
        }
    }

    private void checkPassword(String password) throws BadArgumentException {
        if (password == null || password.isEmpty()) {
            throw new BadArgumentException("Password cannot be empty");
        }
        if (password.length() < 8) {
            throw new BadArgumentException("Password must be at least 8 characters long");
        }
        if (password.length() > 30) {
            throw new BadArgumentException("Password cannot be longer than 30 characters");
        }

        // povolené znaky: písmena, čísla a základní speciální znaky
        // ! @ # $ % ^ & * ( ) _ - + = [ ] { } : ; , . ? /
        if (!password.matches("^[A-Za-z0-9!@#$%^&*()_\\-+=\\[\\]{}:;,.?/]+$")) {
            throw new BadArgumentException(
                    "Password contains invalid characters; allowed are letters, digits and basic symbols !@#$%^&*()_-+=[]{}:;,.?/"
            );
        }

        // kontrola, že obsahuje malé, velké, číslo a speciální znak
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasDigit = password.matches(".*[0-9].*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_\\-+=\\[\\]{}:;,.?/].*");

        if (!(hasLower && hasUpper && hasDigit && hasSpecial)) {
            throw new BadArgumentException(
                    "Password must contain at least one lowercase letter, one uppercase letter, one digit, and one special character"
            );
        }
    }
}
