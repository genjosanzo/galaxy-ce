package org.mule.galaxy;

import java.util.logging.Logger;

import org.mule.galaxy.util.LogUtils;
import org.mule.galaxy.util.Message;

public class DuplicateItemException extends GalaxyException {

    private static final Logger logger = LogUtils.getL7dLogger(DuplicateItemException.class);
    
    public DuplicateItemException(Object key, Throwable t) {
        super(new Message("ITEM_EXISTS", logger, key), t);
    }

    public DuplicateItemException(Object key) {
        super( new Message("ITEM_EXISTS", logger, key) );
    }

}
