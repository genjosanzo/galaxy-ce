package org.mule.galaxy;

import java.util.logging.Logger;

import org.mule.galaxy.util.LogUtils;
import org.mule.galaxy.util.Message;

public class ItemExistsException extends RegistryException {

    private static final Logger logger = LogUtils.getL7dLogger(ItemExistsException.class);
    
    public ItemExistsException(Object key, Throwable t) {
        super(new Message("ITEM_EXISTS", logger, key), t);
    }

    public ItemExistsException(Object key) {
        super( new Message("ITEM_EXISTS", logger, key) );
    }

}
