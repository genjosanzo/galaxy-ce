package org.mule.galaxy.wsdl.compat;


import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.mule.galaxy.wsdl.diff.DifferenceEvent;
import org.mule.galaxy.wsdl.diff.DifferenceListener;
import org.mule.galaxy.wsdl.diff.WsdlDiff;
import org.mule.galaxy.wsdl.diff.rule.WsdlStructureRule;

public class WsdlDiffTest extends TestCase {
    List<String> events = new ArrayList<String>();
    
    public void testMissingOperation() throws Exception {
        WsdlDiff checker = new WsdlDiff();
        
        checker.setOriginalWSDL(getClass().getResource("/wsdl/hello.wsdl").toString());
        checker.setNewWSDL(getClass().getResource("/wsdl/hello-noOperation.wsdl").toString());

        checker.check(new DifferenceListener() {
            public void onEvent(DifferenceEvent event) {
                System.out.println(event.getDescription());
                events.add(event.getType());
            }
        });
        
        assertEquals(2, events.size());
        assertTrue(events.contains(WsdlStructureRule.MISSING_OPERATION));
        assertTrue(events.contains(WsdlStructureRule.MISSING_BINDING_OPERATION));
    }
}
