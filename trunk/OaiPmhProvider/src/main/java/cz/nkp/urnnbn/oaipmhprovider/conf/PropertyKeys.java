/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaipmhprovider.conf;

/**
 * Properties for module OaiPmhProvider. All the keyas are prefixed by
 * "provider".
 * 
 * @author Martin Řehánek
 */
public class PropertyKeys {
    
    protected static final String REPOSITORY_NAME = "provider.repositoryName";
    protected static final String BASE_URL = "provider.baseUrl";
    protected static final String WEB_URL = "provider.webUrl";
    protected static final String EARLIEST_DATESTAMP = "provider.earliestDatestamp";
    protected static final String LIST_IDENTIFIERS_MAX_SIZE = "provider.ListIdentifiers.maxSize";
    protected static final String LIST_RECORDS_MAX_SIZE = "provider.ListRecords.maxSize";
    protected static final String LIST_REQUESTS_MINUTES_VALID = "provider.ListRequests.minutesValid";
}
