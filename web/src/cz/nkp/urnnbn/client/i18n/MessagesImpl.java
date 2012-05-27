package cz.nkp.urnnbn.client.i18n;

import com.google.gwt.i18n.client.Messages;

public interface MessagesImpl extends Messages {

	public String searchResults(String searchString, int count);

	// validation
	public String validationEmptyField();

	public String validationTooLong(int maxLength, int actualLength);

	public String validationInvalidCcnb();

	public String validationInvalidIsbn();

	public String validationInvalidIssn();

	public String validationInvalidYear();

	public String validationNotPositiveInteger();

	public String validationNotPositiveRealNumber();

	public String validationNotLimitedLengthUrl(int maxLength, int actualLength);

	public String validationInvalidEmail();

	public String validationInvalidPassword(int minLength, int maxLength);

	public String validationInvalidUrnNbnPartC();

	public String confirmDeleteDigitalLibrary(String libraryName);

	public String confirmDeleteCatalog(String catalogName);

	public String confirmDeleteArchiver(String archiverName);

	public String confirmDeleteRegistrar(String registrarName);

	public String confirmDeleteAccessRight(String userLogin, String registrarName);

	public String confirmDeleteUser(String userName);

	public String registrarCannotBeDeleted(String registrarName);

	public String archiverCannotBeDeleted(String archiverName);

	public String digitalLibraryCannotBeDeleted(String libraryName);

	public String registrarsAccessRigths(String userName);

	public String serverError(String errorMessage);
}
