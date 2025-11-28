package cz.nkp.urnnbn.czidlo_web_api.api.users.user_manager;

import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.*;
import cz.nkp.urnnbn.czidlo_web_api.api.users.core.UserDetails;

import java.util.List;

public interface UserManager {

    /**
     * Creates new user.
     *
     * @param loginOfUserPerformingThisOperation login of the user performing this operation
     * @param login                              unique login name of the user
     * @param email                              unique email of the user
     * @param password                           password of the user
     * @param isAdmin                            if the user is an admin
     * @return newly created user
     * @throws DuplicateRecordException if a user with that email already exists
     * @throws AccessRightException     if userPerformingThisOperation is not admin
     * @throws BadArgumentException     if login, email or password is invalid
     */
    public UserDetails createUser(String loginOfUserPerformingThisOperation, String login, String email, String password, boolean isAdmin) throws DuplicateRecordException, AccessRightException, BadArgumentException;

    /**
     * Returns a user with given ID.
     *
     * @param loginOfUserPerformingThisOperation login of the user performing this operation
     * @param userId                             id of the user
     * @return user with given ID
     * @throws UnknownRecordException if a user with that ID does not exist
     * @throws AccessRightException   if userPerformingThisOperation is not admin or this user (user with the same userId)
     */
    public UserDetails getUser(String loginOfUserPerformingThisOperation, long userId) throws UnknownRecordException, AccessRightException;

    /**
     * Returns a user with given login.
     *
     * @param loginOfUserPerformingThisOperation login of the user performing this operation
     * @param login                              login of the user
     * @return user with given ID
     * @throws UnknownRecordException if a user with that login does not exist
     * @throws AccessRightException   if userPerformingThisOperation is not admin or this user (user with the same userId)
     */
    public UserDetails getUser(String loginOfUserPerformingThisOperation, String login) throws UnknownRecordException, AccessRightException;

    /**
     * Returns all users.
     *
     * @return list of all users
     * @throws AccessRightException if userPerformingThisOperation is not admin
     */
    public List<UserDetails> getUsers(String loginOfUserPerformingThisOperation) throws AccessRightException;

    /**
     * Updates existing user.
     *
     * @param loginOfUserPerformingThisOperation login of the user performing this operation
     * @param userId                             id of the user
     * @param login                              unique login name of the user
     * @param email                              unique email of the user
     * @param isAdmin                            if the user is an admin
     * @return updated user (without password)
     * @throws UnknownRecordException   if a user with that ID does not exist
     * @throws DuplicateRecordException if a user with that email already exists
     * @throws AccessRightException     if userPerformingThisOperation is not admin
     * @throws BadArgumentException     if login or email is invalid
     */
    public UserDetails updateUser(String loginOfUserPerformingThisOperation, long userId, String login, String email, boolean isAdmin) throws UnknownRecordException, DuplicateRecordException, AccessRightException, BadArgumentException;

    /**
     * @param loginOfUserPerformingThisOperation login of the user performing this operation
     * @param userId                             id of the user
     * @param newPassword                        new password of the user
     * @return updated user (without password)
     * @throws UnknownRecordException if a user with that ID does not exist
     * @throws AccessRightException   if userPerformingThisOperation is not admin or this user (user with the same userId)
     * @throws BadArgumentException   if password is invalid (e.g. empty or too short)
     */
    public UserDetails updateUserPassword(String loginOfUserPerformingThisOperation, long userId, String newPassword) throws UnknownRecordException, AccessRightException, BadArgumentException;

    /**
     * Adds a registrar code into the user's registrars list
     *
     * @param loginOfUserPerformingThisOperation login of the user performing this operation
     * @param userId                             id of the user
     * @param registrarCode                      registrar code added to the user
     * @return updated user with new registrar code added or null if already present
     * @throws UnknownRecordException if a user with that ID does not exist, or registrar with that code does not exist
     * @throws AccessRightException   if userPerformingThisOperation is not admin
     */
    public UserDetails addRegistrarRight(String loginOfUserPerformingThisOperation, long userId, String registrarCode) throws UnknownRecordException, AccessRightException;

    /**
     * Removes a registrar code from the user's registrars list
     *
     * @param loginOfUserPerformingThisOperation login of the user performing this operation
     * @param userId                             id of the user
     * @param registrarCode                      registrar code removed from the user
     * @return updated user without the registrar code
     * @throws UnknownRecordException if a user with that ID does not exist, or registrar with that code does not exist or isn't present
     * @throws AccessRightException   if userPerformingThisOperation is not admin
     */
    public UserDetails removeRegistrarRight(String loginOfUserPerformingThisOperation, long userId, String registrarCode) throws UnknownRecordException, AccessRightException;

    /**
     * Returns all registrar codes from the user's registrars list
     *
     * @param loginOfUserPerformingThisOperation login of the user performing this operation
     * @param userId                             id of the user
     * @return list of the user's registrar codes
     * @throws UnknownRecordException if a user with that ID does not exist
     * @throws AccessRightException   if userPerformingThisOperation is not admin
     */
    public List<String> getRegistrarRights(String loginOfUserPerformingThisOperation, long userId) throws UnknownRecordException, AccessRightException;

    /**
     * Deletes a user.
     *
     * @param loginOfUserPerformingThisOperation login of the user performing this operation
     * @param userId                             id of the user
     * @throws UnknownRecordException if a user with that ID does not exist
     * @throws AccessRightException   if userPerformingThisOperation is not admin
     */
    public void deleteUser(String loginOfUserPerformingThisOperation, long userId) throws UnknownRecordException, AccessRightException;


}
