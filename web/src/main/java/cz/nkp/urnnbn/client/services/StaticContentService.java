package cz.nkp.urnnbn.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import cz.nkp.urnnbn.shared.dto.ContentDTO;
import cz.nkp.urnnbn.shared.exceptions.ServerException;

@RemoteServiceRelativePath("staticContent")
public interface StaticContentService extends RemoteService {

	public ContentDTO getContentByNameAndLanguage(String name, String language) throws ServerException;

	public void update(ContentDTO content) throws ServerException;

}
