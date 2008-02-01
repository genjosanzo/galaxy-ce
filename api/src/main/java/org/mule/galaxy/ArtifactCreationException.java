package org.mule.galaxy;

import org.mule.galaxy.util.Message;

public class ArtifactCreationException extends GalaxyException {

    public ArtifactCreationException(Message msg, Throwable t) {
        super(msg, t);
    }

    public ArtifactCreationException(Message msg) {
        super(msg);
    }

    public ArtifactCreationException(Throwable cause) {
        super(cause);
    }

}
