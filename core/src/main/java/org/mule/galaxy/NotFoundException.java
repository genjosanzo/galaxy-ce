package org.mule.galaxy;

import java.util.logging.Logger;

import org.mule.galaxy.util.LogUtils;
import org.mule.galaxy.util.Message;

public class NotFoundException extends RegistryException {

    private static final Logger logger = LogUtils.getL7dLogger(NotFoundException.class);
    
    public NotFoundException(Object key, Throwable t) {
        super(new Message("NOT_FOUND", logger, key), t);
    }

    public NotFoundException(Object key) {
        super( new Message("NOT_FOUND", logger, key) );
    }

}
