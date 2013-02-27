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
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

public class App {

    private static MyStore store = new MyStore();
    private static String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";
    private static String authToken = "";
    private static java.io.File propertiesFile = null;
    private static String serviceName = "service";

    public static void main(String args[]) throws IOException {
        propertiesFile = new java.io.File(args[0]);
        String action = args[1];
        if (action.equals("upload")) {
            java.io.File fileToUpload = new java.io.File(args[2]);
            Properties properties = new Properties();
            properties.load(new FileInputStream(propertiesFile));
            String userId = properties.getProperty("userid");
            String clientSecret = properties.getProperty("clientsecret");
            login(userId, clientSecret);
            Drive drive = getDriveService(userId, clientSecret);
            uploadFile(drive, "image/jpg", fileToUpload);
        } else if (action.equals("authorize")) {
            Properties properties = new Properties();
            properties.load(new FileInputStream(propertiesFile));
            String userId = properties.getProperty("userid");
            String clientSecret = properties.getProperty("clientsecret");
            if (args.length > 2) {
                authToken = args[2];
            }
            login(userId, clientSecret);
            Drive drive = getDriveService(userId, clientSecret);
        }
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
        return insert.execute();
    }

    public static Drive getDriveService(String clientId, String clientSecret) throws IOException {
        if (clientId == null) {
            throw new NullPointerException("clientId");
        }
        if (clientSecret == null) {
            throw new NullPointerException("clientSecret");
        }
        HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();
        Credential credential = new GoogleCredential.Builder() //
                .setTransport(httpTransport) //
                .setJsonFactory(jsonFactory) //
                .setClientSecrets(clientId, clientSecret) //
                .build();
        store.load(serviceName, credential);
        Drive drive = new Drive.Builder(httpTransport, jsonFactory, credential).setApplicationName("Google drive").build();
        System.out.println(credential.getAccessToken());
        return drive;
    }

    public static void login(String clientId, String clientSecret) throws IOException {
        HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, clientId, clientSecret, Arrays.asList(DriveScopes.DRIVE)) //
                .setAccessType("offline") //
                .setApprovalPrompt("auto") //
                .setCredentialStore(store) //
                .build();
        Credential credential = new GoogleCredential.Builder() //
                .setTransport(httpTransport) //
                .setJsonFactory(jsonFactory) //
                .setClientSecrets(clientId, clientSecret) //
                .build();
        if (!store.load(serviceName, credential)) {

            if (authToken != null) {
                try {
                    GoogleTokenResponse response = flow.newTokenRequest(authToken).setRedirectUri(REDIRECT_URI).execute();
                    credential = flow.createAndStoreCredential(response, "service");
                    store.store("service", credential);
                } catch (Exception e) {
                    String url = flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build();
                    throw new RuntimeException("Authorization token expired, get a new one at " + url, e);
                }
            } else {
                String url = flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build();
                throw new RuntimeException("Application must be authorized at " + url);
            }
        }
    }

    public static class MyStore implements CredentialStore {

        public void delete(String string, Credential crdntl) throws IOException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public boolean load(String service, Credential credential) throws IOException {
            Properties props = new Properties();
            props.load(new FileInputStream(propertiesFile));
            if (props.getProperty(service) != null) {
                credential.setAccessToken(props.getProperty(service));
                return true;
            } else {
                return false;
            }
        }

        public void store(String service, Credential credential) throws IOException {
            Properties props = new Properties();
            props.load(new FileInputStream(propertiesFile));
            props.setProperty(service, credential.getAccessToken());
            props.store(new FileOutputStream(propertiesFile), null);
        }
    }
}
