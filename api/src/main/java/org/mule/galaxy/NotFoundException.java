package org.mule.galaxy;

import java.util.ResourceBundle;

import org.mule.galaxy.util.BundleUtils;
import org.mule.galaxy.util.Message;

public class NotFoundException extends GalaxyException {

    private static final ResourceBundle BUNDLE = BundleUtils.getBundle(NotFoundException.class);
    
    public NotFoundException(Object key, Throwable t) {
        super(new Message("NOT_FOUND", BUNDLE, key), t);
    }

    public NotFoundException(Object key) {
        super( new Message("NOT_FOUND", BUNDLE, key) );
    }

}
