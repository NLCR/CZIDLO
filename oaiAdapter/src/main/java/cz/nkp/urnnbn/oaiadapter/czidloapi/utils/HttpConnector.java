package cz.nkp.urnnbn.oaiadapter.czidloapi.utils;

import javax.net.ssl.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Martin Řehánek on 1.11.17.
 */
public class HttpConnector {

    enum HttpMethod {
        GET, HEAD, POST, PUT, DELETE
    }

    private final int CONNECTION_TIMOOUT = 30 * 1000; //30 s
    private final int READ_TIMOOUT = 60 * 1000; //60 s


    public ApiResponse httpGet(URL url, Credentials credentials, boolean ignoreInvalidApiCertificate) throws IOException {
        HttpURLConnection connection = credentials == null ?
                (HttpURLConnection) url.openConnection() :
                getReadableAuthConnection(url, credentials, HttpMethod.GET, ignoreInvalidApiCertificate);
        connection.setConnectTimeout(CONNECTION_TIMOOUT);
        connection.setReadTimeout(READ_TIMOOUT);
        connection.setInstanceFollowRedirects(true);

        //follow redirect even if http -> https or https -> http, for which HttpURLConnection.setFollowRedirects(true) is not enough
        if (connection.getResponseCode() >= 300 && connection.getResponseCode() < 400) {
            String location = connection.getHeaderField("Location");
            return httpGet(new URL(location), credentials, ignoreInvalidApiCertificate);
        } else {
            InputStream stream = null;
            try {
                //somehow getting response code protects from FileNotFoundException when reading input stream when error stream is null
                connection.getResponseCode();
                stream = connection.getErrorStream();
                if (stream == null) {
                    stream = connection.getInputStream();
                }
                String body = streamToString(stream);
                return new ApiResponse(connection.getResponseCode(), body);
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }
        }
    }

    public ApiResponse httpPost(URL url, String data, Credentials credentials, boolean ignoreInvalidCertificate) throws IOException {
        HttpsURLConnection connection = getWritableAuthConnection(url, credentials, HttpMethod.POST, ignoreInvalidCertificate);
        connection.setConnectTimeout(CONNECTION_TIMOOUT);
        connection.setReadTimeout(READ_TIMOOUT);
        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
        writer.write(data);
        writer.flush();
        writer.close();
        InputStream stream = null;
        try {
            //somehow getting response code protects from FileNotFoundException when reading input stream when error stream is null
            connection.getResponseCode();
            stream = connection.getErrorStream();
            if (stream == null) {
                stream = connection.getInputStream();
            }
            String body = streamToString(stream);
            return new ApiResponse(connection.getResponseCode(), body);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    public ApiResponse httpPut(URL url, String data, Credentials credentials, boolean ignoreInvalidCertificate) throws IOException {
        HttpsURLConnection connection = getWritableAuthConnection(url, credentials, HttpMethod.PUT, ignoreInvalidCertificate);
        connection.setConnectTimeout(CONNECTION_TIMOOUT);
        connection.setReadTimeout(READ_TIMOOUT);
        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
        writer.write(data);
        writer.flush();
        writer.close();
        InputStream stream = null;
        try {
            //somehow getting response code protects from FileNotFoundException when reading input stream when error stream is null
            connection.getResponseCode();
            stream = connection.getErrorStream();
            if (stream == null) {
                stream = connection.getInputStream();
            }
            String body = streamToString(stream);
            return new ApiResponse(connection.getResponseCode(), body);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    public ApiResponse httpDelete(URL url, Credentials credentials, boolean ignoreInvalidCertificate) throws IOException {
        HttpsURLConnection connection = getWritableAuthConnection(url, credentials, HttpMethod.DELETE, ignoreInvalidCertificate);
        connection.setConnectTimeout(CONNECTION_TIMOOUT);
        connection.setReadTimeout(READ_TIMOOUT);
        InputStream stream = null;
        try {
            //somehow getting response code protects from FileNotFoundException when reading input stream when error stream is null
            connection.getResponseCode();
            stream = connection.getErrorStream();
            if (stream == null) {
                stream = connection.getInputStream();
            }
            String body = streamToString(stream);
            return new ApiResponse(connection.getResponseCode(), body);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    private String streamToString(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        byte[] byteArray = buffer.toByteArray();
        String text = new String(byteArray, StandardCharsets.UTF_8);
        return text;
    }

    public HttpsURLConnection getWritableAuthConnection(URL url, Credentials credentialsm, HttpMethod method, boolean ignoreInvalidApiCretificate) throws IOException {
        return getAuthConnection(url, credentialsm, method, true, ignoreInvalidApiCretificate);
    }

    public HttpsURLConnection getReadableAuthConnection(URL url, Credentials credentialsm, HttpMethod method, boolean ignoreInvalidApiCretificate) throws IOException {
        return getAuthConnection(url, credentialsm, method, false, ignoreInvalidApiCretificate);
    }

    private HttpsURLConnection getAuthConnection(URL url, Credentials credentialsm, HttpMethod method, boolean doOutput, boolean ignoreInvalidApiCretificate) throws IOException {
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setConnectTimeout(CONNECTION_TIMOOUT);
        connection.setReadTimeout(READ_TIMOOUT);
        if (ignoreInvalidApiCretificate) {
            connection.setSSLSocketFactory(buildIgnoreAllSslSocketFactory());
        }
        connection.setDoOutput(doOutput);
        connection.setRequestMethod(method.toString());

        connection.setDoInput(true);
        connection.setRequestProperty("Content-type", "application/xml");
        connection.setRequestProperty("Authorization", credentialsm.getBasicAccessAuthorizationHeader());
        return connection;
    }

    private SSLSocketFactory buildIgnoreAllSslSocketFactory() throws IOException {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                }
            }};
            SSLContext sc = SSLContext.getInstance("SSL");
            HostnameVerifier hv = new HostnameVerifier() {
                public boolean verify(String urlHostName, SSLSession session) {
                    return true;
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(hv);
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            return sc.getSocketFactory();
        } catch (KeyManagementException ex) {
            throw new IOException(ex);
        } catch (NoSuchAlgorithmException ex) {
            throw new IOException(ex);
        }
    }

}
