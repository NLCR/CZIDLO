package cz.nkp.urnnbn.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LogsServiceAsync {

    void getAdminLogLastUpdatedTime(AsyncCallback<Long> callback);

    void getAdminLogs(AsyncCallback<List<String>> callback);

}
