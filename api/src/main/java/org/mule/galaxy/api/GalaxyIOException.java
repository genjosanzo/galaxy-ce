package org.mule.galaxy.api;

import java.io.IOException;

public class GalaxyIOException extends IOException {

    public GalaxyIOException(Throwable t) {
        super();
        initCause(t);
    }

}
