package org.mule.galaxy.example.background;

import org.mule.tck.FunctionalTestCase;

public class BackgroundCheckTest extends FunctionalTestCase {
    public void testStartup() throws Exception {
        
    }

    @Override
    protected String getConfigResources() {
        return "mule-config.xml";
    }
    
}
