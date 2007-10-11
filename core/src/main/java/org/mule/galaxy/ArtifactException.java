package org.mule.galaxy;

import org.mule.galaxy.util.Message;


public class ArtifactException extends GalaxyException {

    public ArtifactException(Message msg, Throwable t) {
        super(msg, t);
    }

    public ArtifactException(Message msg) {
        super(msg);
    }

    public ArtifactException(Throwable cause) {
        super(cause);
    }

}
