package org.mule.galaxy.web.client;

import com.google.gwt.user.client.rpc.IsSerializable;

public class RPCException extends Exception implements IsSerializable {

    public RPCException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public RPCException(String message, Throwable cause) {
        super(message, cause);
    }

}
