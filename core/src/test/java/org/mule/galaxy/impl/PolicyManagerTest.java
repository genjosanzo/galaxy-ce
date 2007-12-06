package org.mule.galaxy.impl;

import java.util.Collection;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.lifecycle.Lifecycle;
import org.mule.galaxy.policy.ArtifactPolicy;
import org.mule.galaxy.test.AbstractGalaxyTest;

public class PolicyManagerTest extends AbstractGalaxyTest {
    
    public void testPM() throws Exception {
        Collection<ArtifactPolicy> policies = policyManager.getPolicies();
        assertNotNull(policies);
        assertTrue(policies.size() > 1);
        
        ArtifactPolicy p = policies.iterator().next();
        
        Lifecycle lifecycle = lifecycleManager.getDefaultLifecycle();
        policyManager.activatePolicy(p, lifecycle);
        
        Artifact artifact = importHelloWsdl();
        Workspace workspace = artifact.getWorkspace();
        
        Collection<ArtifactPolicy> active = policyManager.getActivePolicies(artifact);
        assertNotNull(active);
        assertEquals(1, active.size());
        
        active = policyManager.getActivePolicies(workspace);
        assertNotNull(active);
        assertEquals(0, active.size());
        
        policyManager.deactivatePolicy(p, lifecycle);
        active = policyManager.getActivePolicies(artifact);
        assertNotNull(active);
        assertEquals(0, active.size());
        
        // Try workspace activations
        policyManager.activatePolicy(p, workspace, lifecycle);
        
        active = policyManager.getActivePolicies(artifact);
        assertNotNull(active);
        assertEquals(1, active.size());
        
        policyManager.deactivatePolicy(p, workspace, lifecycle);
        
        active = policyManager.getActivePolicies(artifact);
        assertNotNull(active);
        assertEquals(0, active.size());
        
        // Try artifact activations
        policyManager.activatePolicy(p, artifact, lifecycle);
        
        active = policyManager.getActivePolicies(artifact);
        assertNotNull(active);
        assertEquals(1, active.size());
        
        policyManager.deactivatePolicy(p, artifact, lifecycle);
        
        active = policyManager.getActivePolicies(artifact);
        assertNotNull(active);
        assertEquals(0, active.size());
    }
    
    @Override
    protected String[] getConfigLocations() {
        return new String[] { "/META-INF/applicationContext-core.xml", 
                              "/META-INF/applicationContext-test.xml" };
    }

}
