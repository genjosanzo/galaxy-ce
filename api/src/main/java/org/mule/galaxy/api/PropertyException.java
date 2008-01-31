package org.mule.galaxy.api;

import org.mule.galaxy.api.util.Message;

public class PropertyException extends GalaxyException {

    public PropertyException(Message msg, Throwable t) {
        super(msg, t);
    }

    public PropertyException(Message msg) {
        super(msg);
    }

    public PropertyException(Throwable cause) {
        super(cause);
    }

}
