package org.mule.galaxy.web.client;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.rpc.SerializableException;

public class RPCException extends SerializableException {

    public RPCException() {
        super();
    }

    public RPCException(String message) {
        super(message);
    }

}
