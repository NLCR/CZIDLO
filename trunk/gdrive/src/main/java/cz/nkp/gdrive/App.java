package cz.nkp.gdrive;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.CredentialStore;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class App {

	/*
    private static MyStore store = new MyStore();
    private static String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";
    private static String authToken = "";
    private static java.io.File propertiesFile = null;
    private static String serviceName = "service";
    */
	
	private static String SERVICE_ACCOUNT_EMAIL = null;
	private static String SERVICE_ACCOUNT_PKCS12_FILE_PATH = null;
	
    public static void main(String args[]) throws Exception {
    	String action = args[0];
    	java.io.File propertiesFile = new java.io.File(args[1]);
    	Properties properties = new Properties();
        properties.load(new FileInputStream(propertiesFile));
        SERVICE_ACCOUNT_EMAIL = properties.getProperty("email");
        SERVICE_ACCOUNT_PKCS12_FILE_PATH = properties.getProperty("privatekey");
    	Drive drive = getDriveService();
    	if (action.equals("upload")) {
    		System.err.println("uploading");
    		java.io.File fileToUpload = new java.io.File(args[2]);
    		File file = uploadFile(drive, "image/jpg", fileToUpload);
    		System.out.println(file.getId());
    		System.out.println(file.getWebContentLink());
    	}	
    }
	
	public static Drive getDriveService() throws Exception {
		HttpTransport httpTransport = new NetHttpTransport();
		JacksonFactory jsonFactory = new JacksonFactory();
		GoogleCredential credential = new GoogleCredential.Builder()
				.setTransport(httpTransport)
				.setJsonFactory(jsonFactory)
				.setServiceAccountId(SERVICE_ACCOUNT_EMAIL)
				.setServiceAccountScopes(DriveScopes.DRIVE)
				.setServiceAccountPrivateKeyFromP12File(
						new java.io.File(SERVICE_ACCOUNT_PKCS12_FILE_PATH))
				.build();
		Drive service = new Drive.Builder(httpTransport, jsonFactory, null)
				.setHttpRequestInitializer(credential).build();
		return service;
	}
	
    public static File uploadFile(Drive drive, String mimetype, java.io.File file) throws IOException {
        File fileMetadata = new File();
        fileMetadata.setTitle(file.getName());
        InputStreamContent mediaContent = new InputStreamContent(mimetype, new BufferedInputStream(
                new FileInputStream(file)));
        mediaContent.setLength(file.length());
        Drive.Files.Insert insert = drive.files().insert(fileMetadata, mediaContent);
        MediaHttpUploader uploader = insert.getMediaHttpUploader();
        uploader.setDirectUploadEnabled(true);
        File result = insert.execute();
        Permission newPermission = new Permission();
        newPermission.setValue("resolver.nkcr@gmail.com");
        newPermission.setName("backup");
        newPermission.setType("user");
        newPermission.setRole("writer");
        drive.permissions().insert(result.getId(), newPermission).execute();
        return result;
    }

}
