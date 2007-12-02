package org.mule.galaxy.wsdl;

import java.util.HashSet;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.lifecycle.Lifecycle;
import org.mule.galaxy.lifecycle.Phase;
import org.mule.galaxy.test.AbstractGalaxyTest;

public class WsdlVersioningAssessorTest extends AbstractGalaxyTest {
    
    public void testVersioning() throws Exception {
        Artifact a1 = importHelloWsdl();
        
        WsdlVersioningAssessor assessor = new WsdlVersioningAssessor();
        Lifecycle l = lifecycleManager.getDefaultLifecycle();
        Phase init = l.getInitialPhase();
        Phase next = init.getNextPhases().iterator().next();
        
        HashSet<Phase> phases = new HashSet<Phase>();
        phases.add(next);
        assessor.setBackwardCompatabilityPhases(phases);
        
        // assessor.isApproved(a1, next)
    }
    
    @Override
    protected String[] getConfigLocations() {
        return new String[] {
            "/META-INF/applicationContext-core.xml"
        };
    }
}
