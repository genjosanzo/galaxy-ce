package org.mule.galaxy.security;

import org.mule.galaxy.GalaxyException;
import org.mule.galaxy.util.Message;

/**
 * An AccessException indicates that a particular user is not authorized to access 
 * a portion of the Registry.
 */
public class AccessException extends GalaxyException {

    public AccessException(Message msg, Throwable t) {
        super(msg, t);
    }

    public AccessException(Message msg) {
        super(msg);
    }

    public AccessException(Throwable cause) {
        super(cause);
    }

}
