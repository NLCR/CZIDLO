package cz.nkp.urnnbn.shared.exceptions;

import java.io.Serializable;

public class ServerException extends Exception implements Serializable {

	private static final long serialVersionUID = 3593913583682852630L;
	
	public ServerException(){}
	
	public ServerException(String message){
		super(message);
	}

}
