package cz.nkp.urnnbn.solr_indexer;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpClientUtil;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.MapSolrParams;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Martin Řehánek on 15.12.17.
 */
public class SolrConnector {

    private final int CONNECTION_TIMEOUT = 10000;
    private final int SOCKET_TIMEOUT = 60000;


    private final HttpSolrClient solrClient;
    private final String collection;

    public SolrConnector(String baseurl, String collection, boolean useHttps, String login, String password) {
        this.solrClient = login == null ? buildHttpSolrClientWithoutAuth(baseurl, collection, useHttps) : buildHttpSolrClientWithAuth(baseurl, collection, useHttps, login, password);
        this.collection = collection;
    }

    public SolrConnector(String baseUrl, String collection, boolean useHttps) {
        this(baseUrl, collection, useHttps, null, null);
    }

    private HttpSolrClient buildHttpSolrClientWithAuth(String baseUrl, String collection, boolean useHttps, String login, String password) {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(login, password);
        credentialsProvider.setCredentials(AuthScope.ANY, credentials);

        ModifiableSolrParams params = new ModifiableSolrParams();
        params.set(HttpClientUtil.PROP_BASIC_AUTH_USER, login);
        params.set(HttpClientUtil.PROP_BASIC_AUTH_PASS, password);
        params.set(HttpClientUtil.PROP_CONNECTION_TIMEOUT, CONNECTION_TIMEOUT);
        params.set(HttpClientUtil.PROP_SO_TIMEOUT, SOCKET_TIMEOUT);
        //params.set(HttpClientUtil.PROP_ALLOW_COMPRESSION, true);
        //params.set(HttpClientUtil.PROP_FOLLOW_REDIRECTS, true);
        //params.set(HttpClientUtil.PROP_MAX_CONNECTIONS, 22345);
        //params.set(HttpClientUtil.PROP_MAX_CONNECTIONS_PER_HOST, 32345);
        //params.set(HttpClientUtil.PROP_USE_RETRY, false);

        //@see https://stackoverflow.com/questions/36822522/solr-nonrepeatablerequestexception-in-save-action
        HttpClientUtil.addRequestInterceptor(new PreemptiveAuthInterceptor());
        HttpClient httpClient = HttpClientUtil.createClient(params);

        return new HttpSolrClient.Builder(buildUrl(baseUrl, collection, useHttps))
                .withHttpClient(httpClient)
                /*.withConnectionTimeout(CONNECTION_TIMEOUT)
                .withSocketTimeout(SOCKET_TIMEOUT)*/
                .build();
    }

    private HttpSolrClient buildHttpSolrClientWithoutAuth(String baseUrl, String collection, boolean useHttps) {
        return new HttpSolrClient.Builder(buildUrl(baseUrl, collection, useHttps))
                .withConnectionTimeout(CONNECTION_TIMEOUT)
                .withSocketTimeout(SOCKET_TIMEOUT)
                .build();
    }

    private String buildUrl(String baseUrl, String collection, boolean useHttps) {
        StringBuilder builder = new StringBuilder();
        if (useHttps) {
            builder.append("https://");
        } else {
            builder.append("http://");
        }
        builder.append(baseUrl);
        if (!baseUrl.endsWith("/")) {
            builder.append('/');
        }
        //builder.append(collection).append('/');
        return builder.toString();
    }

    public UpdateResponse indexFromXmlFile(File xmlFile, boolean explicitCommit) throws IOException, SAXException, ParserConfigurationException, SolrServerException {
        return indexFromXmlInputStream(new FileInputStream(xmlFile), explicitCommit);
    }

    public UpdateResponse indexFromXmlString(String xmlString, boolean explicitCommit) throws SAXException, ParserConfigurationException, SolrServerException, IOException {
        return indexFromXmlInputStream(new ByteArrayInputStream(xmlString.getBytes()), explicitCommit);
    }

    private UpdateResponse indexFromXmlInputStream(InputStream in, boolean explicitCommit) throws IOException, SAXException, ParserConfigurationException, SolrServerException {
        List<SolrInputDocument> solrDoc = getSolrInputDocumentListFromXmlFile(in);
        UpdateResponse addResponse = null;
        for (SolrInputDocument doc : solrDoc) {
            //there will always only be on ADD anyway
            addResponse = solrClient.add(collection, doc);
        }
        if (explicitCommit) {
            solrClient.commit(collection);
        }
        return addResponse;
    }

    private List<SolrInputDocument> getSolrInputDocumentListFromXmlFile(InputStream in) throws ParserConfigurationException, IOException, SAXException {
        ArrayList<SolrInputDocument> solrDocList = new ArrayList<>();

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(in);

        NodeList docList = doc.getElementsByTagName("doc");

        for (int docIdx = 0; docIdx < docList.getLength(); docIdx++) {
            Node docNode = docList.item(docIdx);
            if (docNode.getNodeType() == Node.ELEMENT_NODE) {
                SolrInputDocument solrInputDoc = new SolrInputDocument();
                Element docElement = (Element) docNode;
                NodeList fieldsList = docElement.getChildNodes();
                for (int fieldIdx = 0; fieldIdx < fieldsList.getLength(); fieldIdx++) {
                    Node fieldNode = fieldsList.item(fieldIdx);
                    if (fieldNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element fieldElement = (Element) fieldNode;
                        String fieldName = fieldElement.getAttribute("name");
                        String fieldValue = fieldElement.getTextContent();
                        solrInputDoc.addField(fieldName, fieldValue);
                    }
                }
                solrDocList.add(solrInputDoc);
            }
        }
        return solrDocList;
    }

    public UpdateResponse deleteById(String id) throws IOException, SolrServerException {
        //System.out.println("deleting " + id);
        UpdateResponse deleteResponse = solrClient.deleteById(collection, id);
        //System.out.println("delete response: " + deleteResponse);
        UpdateResponse commitResponse = solrClient.commit(collection);
        //System.out.println("commit response: " + commitResponse);
        return null;
    }

    public UpdateResponse deleteAll() throws IOException, SolrServerException {
        //System.out.println("deleting all");
        UpdateResponse deleteResponse = solrClient.deleteByQuery(collection, "*");
        //System.out.println("delete response: " + deleteResponse);
        UpdateResponse commitResponse = solrClient.commit(collection);
        //System.out.println("commit response: " + commitResponse);
        return null;
    }


    public SolrDocumentList searchInAllFields(String query) throws IOException, SolrServerException {
        Map<String, String> queryParamMap = new HashMap<>();
        //queryParamMap.put("q", "title:*");
        //queryParamMap.put("q", "*");
        queryParamMap.put("q", query);
        //queryParamMap.put("fl", "id, title");
        MapSolrParams queryParams = new MapSolrParams(queryParamMap);

        QueryResponse response = solrClient.query(collection, queryParams);
        return response.getResults();
    }
}
