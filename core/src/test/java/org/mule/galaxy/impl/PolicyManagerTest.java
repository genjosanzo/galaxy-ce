package org.mule.galaxy.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.lifecycle.Lifecycle;
import org.mule.galaxy.lifecycle.Phase;
import org.mule.galaxy.policy.ArtifactPolicy;
import org.mule.galaxy.policy.PolicyInfo;
import org.mule.galaxy.test.AbstractGalaxyTest;

public class PolicyManagerTest extends AbstractGalaxyTest {
    
    public void testPM() throws Exception {
        Collection<ArtifactPolicy> policies = policyManager.getPolicies();
        assertNotNull(policies);
        assertTrue(policies.size() > 1);
        
        ArtifactPolicy p = policies.iterator().next();
        
        // try lifecycle policies
        Lifecycle lifecycle = lifecycleManager.getDefaultLifecycle();
        policyManager.setActivePolicies(lifecycle, p);
        
        Artifact artifact = importHelloWsdl();
        Workspace workspace = artifact.getWorkspace();
        
        Collection<ArtifactPolicy> active = policyManager.getActivePolicies(artifact);
        assertNotNull(active);
        assertEquals(1, active.size());
        
        active = policyManager.getActivePolicies(lifecycle);
        assertNotNull(active);
        assertEquals(1, active.size());
        
        policyManager.setActivePolicies(lifecycle);
        active = policyManager.getActivePolicies(artifact);
        assertNotNull(active);
        assertEquals(0, active.size());
        
        // Try workspace activations
        policyManager.setActivePolicies(workspace, lifecycle, p);
        
        active = policyManager.getActivePolicies(artifact);
        assertNotNull(active);
        assertEquals(1, active.size());
        
        policyManager.setActivePolicies(workspace, lifecycle);
        
        active = policyManager.getActivePolicies(artifact);
        assertNotNull(active);
        assertEquals(0, active.size());
        
        // Try artifact activations
        policyManager.setActivePolicies(artifact, lifecycle, p);
        
        active = policyManager.getActivePolicies(artifact);
        assertNotNull(active);
        assertEquals(1, active.size());
        
        Collection<PolicyInfo> policyInfos = policyManager.getActivePolicies(artifact, false);
        assertEquals(1, policyInfos.size());
        PolicyInfo policyInfo = policyInfos.iterator().next();
        assertNotNull(policyInfo.getAppliesTo());
        assertTrue(policyInfo.getAppliesTo() instanceof Lifecycle);
        
        policyManager.setActivePolicies(artifact, lifecycle);
        
        active = policyManager.getActivePolicies(artifact);
        assertNotNull(active);
        assertEquals(0, active.size());
        
        // Try phase activations
        Phase phase1 = lifecycle.getInitialPhase();
        Phase phase2 = phase1.getNextPhases().iterator().next();
        
        List<Phase> phases1 = Arrays.asList(phase1);
        
        policyManager.setActivePolicies(phases1, p);
        
        active = policyManager.getActivePolicies(artifact);
        assertNotNull(active);
        assertEquals(1, active.size());
        
        active = policyManager.getActivePolicies(phase1);
        assertNotNull(active);
        assertEquals(1, active.size());
        
        policyManager.setActivePolicies(phases1);
        active = policyManager.getActivePolicies(artifact);
        assertNotNull(active);
        assertEquals(0, active.size());
        
        // Try a phase which an artifact isn't in
        List<Phase> phases2 = Arrays.asList(phase2);
        
        policyManager.setActivePolicies(phases2, p);
        
        active = policyManager.getActivePolicies(artifact);
        assertNotNull(active);
        assertEquals(0, active.size());
        
        policyManager.setActivePolicies(phases2);
        
        // Try phase activations on workspaces
        policyManager.setActivePolicies(workspace, phases1, p);
        
        active = policyManager.getActivePolicies(artifact);
        assertNotNull(active);
        assertEquals(1, active.size());
        
        policyManager.setActivePolicies(workspace, phases1);
        active = policyManager.getActivePolicies(artifact);
        assertNotNull(active);
        assertEquals(0, active.size());
        
        // Try phase activations on artifacts
        policyManager.setActivePolicies(artifact, phases1, p);
        
        active = policyManager.getActivePolicies(artifact);
        assertNotNull(active);
        assertEquals(1, active.size());
        
        policyInfos = policyManager.getActivePolicies(artifact, false);
        assertEquals(1, policyInfos.size());
        policyInfo = policyInfos.iterator().next();
        assertNotNull(policyInfo.getAppliesTo());
        assertTrue(policyInfo.getAppliesTo() instanceof Phase);
        
        policyManager.setActivePolicies(artifact, phases1);
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
