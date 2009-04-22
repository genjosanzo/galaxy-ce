package org.mule.galaxy.artifact;

import org.mule.galaxy.GalaxyException;
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
