package org.mule.galaxy.index;

import org.mule.galaxy.GalaxyException;
import org.mule.galaxy.util.Message;

public class IndexException extends GalaxyException {

    public IndexException(Message msg, Throwable t) {
        super(msg, t);
    }

    public IndexException(Message msg) {
        super(msg);
    }

    public IndexException(Throwable cause) {
        super(cause);
    }

}
