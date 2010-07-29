package org.mule.galaxy.security;

import java.util.ResourceBundle;

import org.mule.galaxy.GalaxyRuntimeException;
import org.mule.galaxy.util.BundleUtils;
import org.mule.galaxy.util.Message;

/**
 * An AccessException indicates that a particular user is not authorized to access 
 * a portion of the Registry.
 */
public class AccessException extends GalaxyRuntimeException {

    private static final ResourceBundle BUNDLE = BundleUtils.getBundle(AccessException.class);
    
    public AccessException() {
        super(new Message("ACCESS_RESTRICTED", BUNDLE));
    }

}
