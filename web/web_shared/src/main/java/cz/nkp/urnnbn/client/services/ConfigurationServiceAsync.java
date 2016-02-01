package cz.nkp.urnnbn.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import cz.nkp.urnnbn.shared.ConfigurationData;

public interface ConfigurationServiceAsync {

    void getConfiguration(AsyncCallback<ConfigurationData> callback);

}
