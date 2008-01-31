package org.mule.galaxy.api;

import org.mule.galaxy.api.util.LogUtils;
import org.mule.galaxy.api.util.Message;

import java.util.logging.Logger;

public class NotFoundException extends RegistryException {

    private static final Logger logger = LogUtils.getL7dLogger(NotFoundException.class);
    
    public NotFoundException(Object key, Throwable t) {
        super(new Message("NOT_FOUND", logger, key), t);
    }

    public NotFoundException(Object key) {
        super( new Message("NOT_FOUND", logger, key) );
    }

}
