package cz.nkp.urnnbn.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import cz.nkp.urnnbn.shared.ConfigurationData;
import cz.nkp.urnnbn.shared.exceptions.ServerException;

@RemoteServiceRelativePath("conf")
public interface ConfigurationService extends RemoteService {
    ConfigurationData getConfiguration() throws ServerException;
}
