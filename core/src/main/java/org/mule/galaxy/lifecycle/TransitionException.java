package org.mule.galaxy.lifecycle;

import java.util.ResourceBundle;

import org.springframework.util.ResourceUtils;
import org.mule.galaxy.GalaxyException;
import org.mule.galaxy.util.BundleUtils;
import org.mule.galaxy.util.LogUtils;
import org.mule.galaxy.util.Message;

public class TransitionException extends GalaxyException {

    private static final ResourceBundle RESOURCES = BundleUtils.getBundle(TransitionException.class);
    
    public TransitionException(Phase p) {
        super(new Message("INVALID_TRANSITION", RESOURCES, p.getName()));
    }

}
