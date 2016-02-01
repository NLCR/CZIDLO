package cz.nkp.urnnbn.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import cz.nkp.urnnbn.shared.exceptions.ServerException;
import cz.nkp.urnnbn.shared.exceptions.SessionExpirationException;

@RemoteServiceRelativePath("logs")
public interface LogsService extends RemoteService {

    long getAdminLogLastUpdatedTime() throws ServerException, SessionExpirationException;

    List<String> getAdminLogs() throws ServerException, SessionExpirationException;
}
