package org.mule.galaxy.example.background;

import org.mule.impl.MuleMessage;
import org.mule.umo.UMOEventContext;
import org.mule.umo.lifecycle.Callable;

public class BackgroundCheckService implements Callable {

    public Object onCall(UMOEventContext eventContext) throws Exception {
        String socialSecurityNumber = (String) eventContext.getMessage().getProperty("socialSecurityNumber");
        
        // TODO 
        
        return new MuleMessage("Security check passed.");
    }

}
