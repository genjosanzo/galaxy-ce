package org.mule.galaxy.web.rpc;

import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.web.client.RPCException;

public class WAccessException extends RPCException {

    public WAccessException() {
        super("You do not have permissions to perform that action!");
    }
    
}
