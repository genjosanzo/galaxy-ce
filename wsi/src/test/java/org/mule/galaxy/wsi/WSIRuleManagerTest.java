package org.mule.galaxy.wsi;

import java.util.List;

import junit.framework.TestCase;

import org.mule.galaxy.wsi.impl.WSIRuleManagerImpl;

public class WSIRuleManagerTest extends TestCase {

    public void testManager() throws Exception {
        WSIRuleManager manager = new WSIRuleManagerImpl();
        
        List<WSIRule> rules = manager.getRules(WSIRuleManager.WSI_BP_1_1);
        
        assertNotNull(rules);
        assertTrue(rules.size() > 0);
        
        String desc = manager.getDescription("R2803");
        assertNotNull(desc);
        assertTrue(desc.startsWith("In a DESCRIPTION"));
    }
}
