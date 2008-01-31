package org.mule.galaxy.api;

import org.mule.galaxy.api.util.Message;


public class RegistryException extends GalaxyException {

    public RegistryException(Message msg, Throwable t) {
        super(msg, t);
    }

    public RegistryException(Message msg) {
        super(msg);
    }

    public RegistryException(Throwable cause) {
        super(cause);
    }

}
