package org.mule.galaxy;

import java.util.logging.Logger;

import org.mule.galaxy.util.LogUtils;
import org.mule.galaxy.util.Message;

public class DuplicateItemException extends GalaxyException {

    private static final Logger logger = LogUtils.getL7dLogger(DuplicateItemException.class);
    private final Object key;
    
    public DuplicateItemException(Object key, Throwable t) {
        super(new Message("ITEM_EXISTS", logger, key), t);
        this.key = key;
    }

    public DuplicateItemException(Object key) {
        this(key, null);
    }

    public Object getKey() {
        return key;
    }
}
