package cz.nkp.urnnbn.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import cz.nkp.urnnbn.shared.ConfigurationData;

@RemoteServiceRelativePath("conf")
public interface ConfigurationService extends RemoteService {
	ConfigurationData getConfiguration();
}
