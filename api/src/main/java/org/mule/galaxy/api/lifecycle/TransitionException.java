package org.mule.galaxy.api.lifecycle;

import org.mule.galaxy.api.GalaxyException;
import org.mule.galaxy.api.util.BundleUtils;
import org.mule.galaxy.api.util.Message;

import java.util.ResourceBundle;

public class TransitionException extends GalaxyException
{

    private static final ResourceBundle RESOURCES = BundleUtils.getBundle(TransitionException.class);
    
    public TransitionException(Phase p) {
        super(new Message("INVALID_TRANSITION", RESOURCES, p.getName()));
    }

}
